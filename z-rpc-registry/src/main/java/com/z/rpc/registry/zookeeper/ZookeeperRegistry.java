package com.z.rpc.registry.zookeeper;

import com.z.rpc.common.serialize.kryo.Kryos;
import com.z.rpc.registry.Constant;
import com.z.rpc.registry.DataChangeCallBack;
import com.z.rpc.registry.Registry;
import com.z.rpc.registry.ServerNode;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * zk注册中心
 */
public class ZookeeperRegistry implements Registry {

    private static final Logger logger = LoggerFactory.getLogger(ZookeeperRegistry.class);

    private String zookeeperAddress;

    private ZooKeeper zooKeeper;


    public ZookeeperRegistry(String zookeeperAddress) {
        this.zookeeperAddress = zookeeperAddress;
    }

    public String register(ServerNode serverNode) {
        if (serverNode != null) {
            if (zooKeeper == null) {
                zooKeeper = ZKClient.getInstance().connectZK(zookeeperAddress);
            }
            addRootNode(zooKeeper); // Add root node if not exist
          return  createNode(zooKeeper, serverNode);
        }
        return "";
    }

    @Override
    public void unregister(String  path) throws KeeperException, InterruptedException {
       if(StringUtils.isNotEmpty(path)){
           removeNode(zooKeeper,path);
       }
    }

    @Override
    public List<ServerNode> subscribe(DataChangeCallBack changeCallBack) {
        if (zooKeeper == null) {
            zooKeeper = ZKClient.getInstance().connectZK(zookeeperAddress);
        }
        List<ServerNode> dataList = new ArrayList<>();
        try {
            List<String> nodeList = zooKeeper.getChildren(Constant.ZK_REGISTRY_PATH, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getType() == Event.EventType.NodeChildrenChanged) {
                        //服务端下线时zk通知客户端更新连接
                     List<ServerNode> serverNodes=  subscribe(changeCallBack);
                     changeCallBack.call(serverNodes);
                    }
                }
            });
            for (String node : nodeList) {
                byte[] bytes = zooKeeper.getData(Constant.ZK_REGISTRY_PATH + "/" + node, false, null);
                dataList.add(Kryos.deserialize(bytes, ServerNode.class));
            }
            logger.debug("node data: {}", dataList);
            logger.debug("Service subscribe triggered updating connected server node.");

            return dataList;
        } catch (KeeperException | InterruptedException e) {
            logger.error("获取zk子节点数据失败", e);
        }
        return dataList;
    }

    private void addRootNode(ZooKeeper zk) {
        try {
            Stat s = zk.exists(Constant.ZK_REGISTRY_PATH, false);
            if (s == null) {
                zk.create(Constant.ZK_REGISTRY_PATH, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (KeeperException e) {
            logger.error(e.toString());
        } catch (InterruptedException e) {
            logger.error(e.toString());
        }
    }

    private String createNode(ZooKeeper zk, ServerNode serverNode) {
        try {
            byte[] bytes = Kryos.serialize(serverNode);
            String path = zk.create(Constant.ZK_DATA_PATH, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            logger.debug("create zookeeper node ({} => address:{},weight:{})", path, serverNode.getAddress(), serverNode.getWeight());
            return path;
        } catch (KeeperException e) {
            logger.error("", e);
        } catch (InterruptedException ex) {
            logger.error("", ex);
        }
        return null;
    }

    private void removeNode(ZooKeeper zk,String path) throws KeeperException, InterruptedException {
         zk.delete(path,-1);
    }
}