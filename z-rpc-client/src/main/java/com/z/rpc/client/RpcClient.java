package com.z.rpc.client;

import com.z.rpc.client.connection.Connection;
import com.z.rpc.client.connection.ConnectionManager;
import com.z.rpc.common.exception.RpcException;
import com.z.rpc.common.serialize.RpcSerializerProtocol;
import com.z.rpc.registry.DataChangeCallBack;
import com.z.rpc.registry.Registry;
import com.z.rpc.registry.ServerNode;
import com.z.rpc.registry.zookeeper.ZKClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * RPC Handler Init
 */
public class RpcClient extends AbstractRpcClient {
    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);


    public RpcClient(Registry registry, RpcSerializerProtocol rpcSerializerProtocol) {
        super(registry, rpcSerializerProtocol);
    }
    public RpcClient(Registry registry) {
        super(registry, RpcSerializerProtocol.HESSIANSERIALIZE);
    }

    @Override
    public boolean doStart() throws Exception {
        this.serverNodeList = registry.subscribe(new DataChangeCallBack() {
            @Override
            public void call(List<ServerNode> serverNodeList) {
                 refreshServerNode(serverNodeList);
            }
        });
        if (serverNodeList.size() == 0) {
            throw new RpcException("未找到服务地址,请检查服务端连接");
        }
        this.connectionMap = ConnectionManager.initConnection(serverNodeList, nioEventLoopGroup,
                new RpcClientInitializer(rpcSerializerProtocol),rpcSerializerProtocol);
        return true;
    }
    public void refreshServerNode(List<ServerNode> serverNodeList){
       this.serverNodeList=serverNodeList;
        List<String> latestServerAddressList=serverNodeList.stream().map(node->node.getAddress()).collect(Collectors.toList());
        
        Set<String> connectionSet=connectionMap.keySet();
        //需要删除的连接
        List<String> removeList=new ArrayList<>();
        for(String connection :connectionSet){
            if(latestServerAddressList.contains(connection)){
                continue;
            } else {
                removeList.add(connection);
            }
        }
        //需要新增的连接
         List<String> addList=new ArrayList<>();
        for(String serverAddress:latestServerAddressList ){
             if(connectionSet.contains(serverAddress)){
                 continue;
             }else {
                 addList.add(serverAddress);
             }
        }
        for(String removeAddress:removeList){
            Connection connection=connectionMap.get(removeAddress);
            connection.getChannel().close();
            connectionMap.remove(removeAddress);
            logger.info("检测到服务端断开,删除连接:{}",removeAddress);
        }
        for(String addAddress:addList){
            logger.info("检测到有新的服务(集群服务)上线,新增连接:{}",addAddress);
            connectionMap.put(addAddress,ConnectionManager.addConnection(new ServerNode(addAddress),nioEventLoopGroup,
                new RpcClientInitializer(rpcSerializerProtocol),rpcSerializerProtocol)) ;
        }
    }

    @Override
    protected boolean doStop() throws Exception {
        if (nioEventLoopGroup != null) {
            nioEventLoopGroup.shutdownGracefully();
        }
        ZKClient.getInstance().close();
        return true;
    }


}
