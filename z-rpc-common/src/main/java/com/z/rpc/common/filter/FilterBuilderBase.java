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
package com.z.rpc.common.filter;



import com.z.rpc.common.Constants;
import com.z.rpc.common.protocol.RequestPacket;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class FilterBuilderBase implements FilterBuilder {
     private static final Logger logger = LoggerFactory.getLogger(FilterBuilderBase.class);

    private List<Filter> filters = new ArrayList<>();
   public  FilterBuilderBase(){

   }
    public FilterBuilderBase(String group) {
        //初始化SPI加载filter
        ServiceLoader<Filter> serviceLoader = ServiceLoader.load(Filter.class, Thread.currentThread().getContextClassLoader());
        Iterator iterator = serviceLoader.iterator();
        Filter filter = null;
        while (iterator.hasNext()) {
            filter = (Filter) iterator.next();
           checkGroup(filter);
           if(filter.group().equals(group)){
              logger.info("加载:{}端filter:{}",group,filter.getClass().getCanonicalName());
               filters.add(filter);
           }
        }
         Collections.sort(filters,new FilterComparator());
    }
    private void checkGroup(Filter filter){
         if(!StringUtils.isNotEmpty(filter.group())){
             throw new RuntimeException("filter must define a group");
         }
         if(!filter.group().equals(Constants.CONSUMER)&&!filter.group().equals(Constants.PROVIDER)){
            throw new IllegalStateException("filter group must  define with consumer/provider");
         }
    }


    public <T> FilterInvoker<T> buildChain(FilterInvoker<T> invoker) {
        FilterInvoker last = invoker;

        if (filters.size() > 0) {
            for (int i = filters.size() - 1; i >= 0; i--) {
                Filter filter = filters.get(i);
                FilterInvoker<T> next = last;
                //此处装饰者,通过构建新的对象来包装前一个对象,举个例子，假如前一个对象是个小圈，如何包装这个小圈，那就是构造一个大圈来将小圈包围，调用的时候先调用最新的对象的invoke方法，再层层往里调用
                //构造新的ModuleInvoker，同时传入上一个invoker
                //假设只有EchoChainFiler,ClassLoaderChainFilter这两个filter
                //循环第一次:首先构造EchoChainFiler的ModuleInvoker,新的ModuleInvoker中传入EchoChainFiler,同时传入上一个ModuleInvoker，来自RpcHandlder invoke传入
                //循环第二次:构造ClassLoaderChainFilter的ModuleInvoker,在当前新的ModuleInvoker中传入ClassLoaderChainFilter，同时传入上一个ModuleInvoker来自第一次循环构建
                //循环结束
                last = new FilterInvoker<T>() {
                    @Override
                    public Object invoke(RequestPacket request) throws Throwable {
                        return filter.invoke(next, request);
                    }

                    @Override
                    public Class<T> getInterface() {
                        return invoker.getInterface();
                    }

                    @Override
                    public String toString() {
                        return invoker.toString();
                    }

                    @Override
                    public void destroy() {
                        invoker.destroy();
                    }
                };
            }
        }
        //last对象中是一个链表结构
        return last;
    }
}

