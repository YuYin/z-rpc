package com.z.rpc.client.hystrix;

import com.google.auto.service.AutoService;
import com.netflix.hystrix.HystrixCommand;
import com.z.rpc.client.hystrix.config.SetterFactory;
import com.z.rpc.client.BaseConsumerFilter;
import com.z.rpc.common.RPCSystemConfig;
import com.z.rpc.common.filter.Filter;
import com.z.rpc.common.filter.FilterInvoker;
import com.z.rpc.common.protocol.RequestPacket;

@AutoService(Filter.class)
public class HystrixFilter extends BaseConsumerFilter {
    @Override
    public Object invoke(FilterInvoker<?> invoker, RequestPacket request) throws Throwable {
        if (!Boolean.valueOf(request.getAttachments().get(RPCSystemConfig.ENABLE_HYSTRIX_KEY))) {
            return invoker.invoke(request);
        }
        String methodName = request.getMethodName();
        String interfaceName = request.getInterfaceName();
        //获取相关熔断配置
        HystrixCommand.Setter setter = SetterFactory.create(interfaceName, methodName);
        ClientCommand command = new ClientCommand(setter, invoker, request, request.getAttachments().get(RPCSystemConfig.FALL_BACK_CLASS_KEY),Thread.currentThread());
        Object result = command.execute();
        return result;
    }

    @Override
    public int order() {
        return 2;
    }
}
