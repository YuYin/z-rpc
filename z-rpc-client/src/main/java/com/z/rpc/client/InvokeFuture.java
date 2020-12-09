package com.z.rpc.client;

import com.z.rpc.common.RPCSystemConfig;
import com.z.rpc.common.exception.RpcException;
import com.z.rpc.common.protocol.RequestPacket;
import com.z.rpc.common.protocol.ResponsePacket;
import com.z.rpc.common.thread.RPCThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.ReentrantLock;

/**
 * InvokeFuture for async RPC call
 */
public class InvokeFuture implements Future<Object> {
    private static final Logger logger = LoggerFactory.getLogger(InvokeFuture.class);

    private static ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) RPCThreadPool.getExecutor(RPCSystemConfig.SYSTEM_PROPERTY_THREADPOOL_THREAD_NUMS,
            RPCSystemConfig.SYSTEM_PROPERTY_THREADPOOL_QUEUE_NUMS, "client");


    private Sync sync;
    private RequestPacket request;
    private ResponsePacket response;
    private long startTime;
    private long responseTimeThreshold = 5000;

    private CountDownLatch countDownLatch;


    private List<AsyncRPCCallback> pendingCallbacks = new ArrayList<>();
    private ReentrantLock lock = new ReentrantLock();

    public InvokeFuture(RequestPacket request) {
        this.sync = new Sync();
        this.request = request;
        this.startTime = System.currentTimeMillis();
        this.countDownLatch = new CountDownLatch(1);
    }

    @Override
    public boolean isDone() {
        return sync.isDone();
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        // sync.acquire(-1);
        countDownLatch.await();
        if (this.response != null) {
            if (response.isSuccess()) {
                return this.response.getPayload();
            } else {
                throw new RpcException(response.getMessage());
            }
        } else {
            logger.error("Response is null");
            return null;
        }
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        // boolean success = sync.tryAcquireNanos(-1, unit.toNanos(timeout));
        boolean success = countDownLatch.await(timeout, unit);
        if (success) {
            if (this.response != null) {
                if (response.isSuccess()) {
                    return this.response.getPayload();
                } else {
                    throw new RpcException(response.getMessage());
                }
            } else {
                logger.error("Response is null");
                return null;
            }
        } else {
            throw new RpcException("Timeout exception. Request id: " + this.request.getRequestId()
                    + ". Request class name: " + this.request.getRequestId()
                    + ". Request method: " + this.request.getMethodName());
        }
    }

    @Override
    public boolean isCancelled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException();
    }

    public void done(ResponsePacket response) {
        this.response = response;
        // sync.release(1);
        countDownLatch.countDown();
        invokeCallbacks();
        // Threshold
        long responseTime = System.currentTimeMillis() - startTime;
        if (responseTime > this.responseTimeThreshold) {
            logger.warn("Service response time is too slow. Request id = " + response.getRequestId() + ". Response Time = " + responseTime + "ms");
        }
    }

    private void invokeCallbacks() {
        lock.lock();
        try {
            for (final AsyncRPCCallback callback : pendingCallbacks) {
                runCallback(callback);
            }
        } finally {
            lock.unlock();
        }
    }

    public InvokeFuture addCallback(AsyncRPCCallback callback) {
        lock.lock();
        try {
            if (isDone()) {
                runCallback(callback);
            } else {
                this.pendingCallbacks.add(callback);
            }
        } finally {
            lock.unlock();
        }
        return this;
    }

    private void runCallback(final AsyncRPCCallback callback) {
        final ResponsePacket res = this.response;
        threadPoolExecutor.submit(() -> {
            if (res.isSuccess()) {
                callback.success(res.getPayload());
            } else {
                callback.fail(new RpcException("Response error", new Throwable(res.getMessage())));
            }
        });
    }


    static class Sync extends AbstractQueuedSynchronizer {

        private static final long serialVersionUID = 1L;

        //future status
        private final int done = 1;
        private final int pending = 0;

        @Override
        protected boolean tryAcquire(int arg) {
            return getState() == done;
        }

        @Override
        protected boolean tryRelease(int arg) {
            if (getState() == pending) {
                if (compareAndSetState(pending, done)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }

        public boolean isDone() {
            getState();
            return getState() == done;
        }
    }
}
