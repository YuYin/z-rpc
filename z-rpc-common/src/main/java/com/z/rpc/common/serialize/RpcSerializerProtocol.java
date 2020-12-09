/**
 * Copyright (C) 2016 Newland Group Holding Limited
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
package com.z.rpc.common.serialize;

import com.z.rpc.common.serialize.hessian.HessianSerializer;
import com.z.rpc.common.serialize.jdk.JdkSerializer;
import com.z.rpc.common.serialize.kryo.KryoSerializer;

public enum RpcSerializerProtocol {

   KRYOSERIALIZE(new KryoSerializer()),
    JDKSERIALIZE(new JdkSerializer()),
    HESSIANSERIALIZE(new HessianSerializer());

    public RPCSerializer rpcSerializer;

    RpcSerializerProtocol(RPCSerializer rpcSerializer){
        this.rpcSerializer=rpcSerializer;
    }
}
