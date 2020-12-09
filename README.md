# Z-RPC框架

基于netty通信的高性能rpc框架

## 1. 特性

### 1.0 版本特性

- 支持zookeeper,redis作为注册中心
- 支持客户端负载均衡,随机,轮询,哈希,加权轮询等算法
- 支持服务端集群,支持客户端失败重试
- 支持服务端客户端filter机制,filter支持基于SPI扩展
- 支持kryo, hessian,原生jdk等协议序列化
- 支持同步调用,Future调用,异步回调模式
- 支持注解@RpcReference，@RpcService注入服务和暴露服务
- 支持直接main方法启动,不依赖spring启动,也可以集成spring启动
- 支持配置自定义线程池线程数
- 支持apache commons-pool2对象池化技术
- 支持netflix hystrix熔断降级

### 1.1 更新
- 支持直连调用√(已支持)

### 1.2 更新
- 支持多协议√(已支持,支持tcp和http调用)

### 1.3 更新
- 优化client server端服务启动流程

### 1.4 更新

 去除HTTP/TCP调用模式,使用自定义RPC协议通信

 **基础Packet字段**

| **字段名**   | **字段类型**        | **字节数(byte)** | **字段功能**                                       | **备注**                                |
| ------------ | ------------------- | ---------------- | -------------------------------------------------- | --------------------------------------- |
| magicNumber  | int                 | 2                | 魔数，类似于`Java`的字节码文件的魔数是`0xcafebase` |                                         |
| version      | int                 | 2                | 版本号                                             | 预留字段，默认为1                       |
| requestId | java.lang.String    | 4                | 请求流水号                                         | 每个请求的唯一标识                      |
| messageType  | MessageType         | 1                | 消息类型                                           | 自定义的枚举类型                        |
| attachments  | Map<String, String> | 视实际情况而定   | 附件                                               | `K-V`形式，类似于`HTTP`协议中的`Header` |



**请求扩展Packet字段**



| 字段名                   | 字段类型           | 字节数(byte)   | **字段功能**           | **备注**                         |
| ------------------------ | ------------------ | -------------- | ---------------------- | -------------------------------- |
| interfaceName            | java.lang.String   | 视实际情况而定 | 接口全类名             |                                  |
| methodName               | java.lang.String   | 视实际情况而定 | 方法名                 |                                  |
| methodArgumentSignatures | java.lang.String[] | 视实际情况而定 | 方法参数签名字符串数组 | 存放方法参数类型全类名字符串数组 |
| methodArguments          | java.lang.Object[] | 视实际情况而定 | 方法参数数组           |                                  |

**响应Packet扩展字段**：

| 字段名  | 字段类型          | 字节数(byte)   | 字段功能 | **备注**                                   |
| ------- | ----------------- | -------------- | -------- | ------------------------------------------ |
| code    | java.lang.String  | 视实际情况而定 | 响应码   |                                            |
| message | java.lang.String  | 视实际情况而定 | 响应消息 | 如果出现异常，`message`就是对应的异常信息  |
| payload | java.lang.Object  | 视实际情况而定 | 消息载荷 | 业务处理返回的消息载荷，定义为`Object`类型 |
| success | java.lang.Boolean | 1              | 是否成功 | 请求是否成功                               |

**组件的交互流程：**

![img](https://throwable-blog-1256189093.cos.ap-guangzhou.myqcloud.com/202001/n-s-b-c-p-1.png)


### 1.5 规划
- 优化代码
- 支持服务治理
- 支持服务监控

``````
优化负载均衡算法
``````


## 2. 使用

详见zds-rpc-samples

## 3. 性能

做了简单测试,本机直接启动服务端和客户端,#doPersonTest方法#1000次用时823ms


## 4. 参考:
- https://netty.io/
- https://github.com/luxiaoxun/NettyRpc.git 
- https://github.com/xuxueli/xxl-rpc.git 
- http://www.throwable.club/2020/01/12/netty-custom-rpc-framework-protocol/
