/**
 * Copyright (C) 2018 Newland Group Holding Limited
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.z.rpc.samples.server.filter;


import com.google.auto.service.AutoService;
import com.z.rpc.common.filter.Filter;
import com.z.rpc.common.filter.FilterInvoker;
import com.z.rpc.common.protocol.RequestPacket;
import com.z.rpc.server.BaseProviderFilter;

@AutoService(Filter.class)
public class ClassLoaderFilter extends BaseProviderFilter {
    @Override
    public Object invoke(FilterInvoker<?> invoker, RequestPacket request) throws Throwable {
        ClassLoader ocl = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(invoker.getInterface().getClassLoader());
        Object result = null;
        try {
            System.out.println("ClassLoaderFilter##TRACE MESSAGE-ID:" + request.getRequestId());
            result = invoker.invoke(request);
            return result;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw throwable;
        } finally {
            Thread.currentThread().setContextClassLoader(ocl);
        }
    }

    @Override
    public int order() {
        return 1;
    }
}

