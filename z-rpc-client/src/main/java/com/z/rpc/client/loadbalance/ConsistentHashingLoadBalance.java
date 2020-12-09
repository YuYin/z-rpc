/*
 *
 * Copyright (c) 2005-2017 All Rights Reserved.
 */
/*
 * 修订记录：
 * Administrator 2018/5/30 16:57 创建
*/
/*
 * @author Administrator
*/
package com.z.rpc.client.loadbalance;

/**
 * 带虚拟节点的一致性Hash算法
 */
//public class ConsistentHashingLoadBalance  extends AbstractLoadBalance
//{
//
//    /**
//     * 虚拟节点，key表示虚拟节点的hash值，value表示虚拟节点的名称
//     */
//    private static SortedMap<Integer, String> virtualNodes =
//            new TreeMap<Integer, String>();
//
//    /**
//     * 虚拟节点的数目，这里写死，为了演示需要，一个真实结点对应5个虚拟节点
//     */
//    private static final int VIRTUAL_NODES = 5;
//
//    public void doRoute(List<String> servers){
//
//        // 添加虚拟节点，遍历LinkedList使用foreach循环效率会比较高
//        for (String str : servers)
//        {
//            for (int i = 0; i < VIRTUAL_NODES; i++)
//            {
//                String virtualNodeName = str + "&&VN" + String.valueOf(i);
//                int hash = getHash(virtualNodeName);
//                System.out.println("虚拟节点[" + virtualNodeName + "]被添加, hash值为" + hash);
//                virtualNodes.put(hash, virtualNodeName);
//            }
//        }
//
//    }
//
//    /**
//     * 使用FNV1_32_HASH算法计算服务器的Hash值,这里不使用重写hashCode的方法，最终效果没区别
//     */
//    private static int getHash(String str)
//    {
//        final int p = 16777619;
//        int hash = (int)2166136261L;
//        for (int i = 0; i < str.length(); i++)
//            hash = (hash ^ str.charAt(i)) * p;
//        hash += hash << 13;
//        hash ^= hash >> 7;
//        hash += hash << 3;
//        hash ^= hash >> 17;
//        hash += hash << 5;
//
//        // 如果算出来的值为负数则取其绝对值
//        if (hash < 0)
//            hash = Math.abs(hash);
//        return hash;
//    }
//
//    /**
//     * 得到应当路由到的结点
//     */
//    private static String getServer(String node)
//    {
//        // 得到带路由的结点的Hash值
//        int hash = getHash(node);
//        // 得到大于该Hash值的所有Map
//        SortedMap<Integer, String> subMap =
//                virtualNodes.tailMap(hash);
//        // 第一个Key就是顺时针过去离node最近的那个结点
//        Integer i = subMap.firstKey();
//        // 返回对应的虚拟节点名称，这里字符串稍微截取一下
//        String virtualNode = subMap.get(i);
//        return virtualNode.substring(0, virtualNode.indexOf("&&"));
//    }
//    @Override
//    public String route(List<ServerNode> serverList) {
//        return null;
//    }
//}
