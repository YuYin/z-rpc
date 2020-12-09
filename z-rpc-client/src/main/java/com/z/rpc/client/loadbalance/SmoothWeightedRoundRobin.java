/*
 * www.zdsoft.cn Inc.
 * Copyright (c) 2005-2017 All Rights Reserved.
 */
package com.z.rpc.client.loadbalance;

import com.z.rpc.registry.ServerNode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author <a href=mailto:someharder@gmail.com>yinyu</a> 2020/7/27
 *         https://www.jianshu.com/p/159fb7805147 加权轮询
 */
public class SmoothWeightedRoundRobin extends AbstractLoadBalance {
    
    private ReentrantLock lock = new ReentrantLock();

    public String doRoute(List<ServerNode> serverList) {
        try {
            lock.lock();
            return this.selectInner(serverList);
        } finally {
            lock.unlock();
        }
    }

    private String selectInner(List<ServerNode> serverList) {
        List<Node> nodeList = new ArrayList<>();
        for (ServerNode serverNode : serverList) {
            Node node = new Node();
            node.setServeAddress(serverNode.getAddress());
            node.setCurrentWeight(serverNode.getWeight());
            node.setWeight(serverNode.getWeight());
            nodeList.add(node);
        }
        int totalWeight = 0;
        Node maxNode = null;
        int maxWeight = 0;

        for (int i = 0; i < nodeList.size(); i++) {
            Node n = nodeList.get(i);
            totalWeight += n.getWeight();
            // 每个节点的当前权重要加上原始的权重
            n.setCurrentWeight(n.getCurrentWeight() + n.getWeight());
            // 保存当前权重最大的节点
            if (maxNode == null || maxWeight < n.getCurrentWeight()) {
                maxNode = n;
                maxWeight = n.getCurrentWeight();
            }
        }
        // 被选中的节点权重减掉总权重
        maxNode.setCurrentWeight(maxNode.getCurrentWeight() - totalWeight);
        return maxNode.getServeAddress();
    }

    @Override
   public String route(List<ServerNode> serverList) {
        return doRoute(serverList);
    }

    static class Node {
        private int weight;  // 初始权重 （保持不变）
        private String serveAddress; // 服务名
        private int currentWeight; // 当前权重

        public int getCurrentWeight() {
            return currentWeight;
        }

        public int getWeight() {
            return weight;
        }

        public void setCurrentWeight(int currentWeight) {
            this.currentWeight = currentWeight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        public String getServeAddress() {
            return serveAddress;
        }

        public void setServeAddress(String serveAddress) {
            this.serveAddress = serveAddress;
        }
    }
}
