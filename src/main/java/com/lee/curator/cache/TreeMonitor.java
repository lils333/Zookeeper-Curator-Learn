package com.lee.curator.cache;

import com.lee.curator.basic.Curator;
import com.lee.curator.embed.Server;
import com.lee.curator.embed.StandaloneZookeeperServer;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.zookeeper.CreateMode;

import java.util.concurrent.TimeUnit;

public class TreeMonitor {

    public static void main(String[] args) throws Exception {
        Server server = new StandaloneZookeeperServer();
        server.start();

        TimeUnit.SECONDS.sleep(2);

        CuratorFramework curatorFramework = Curator.create();
        curatorFramework.start();

        TreeCache treeCache = new TreeCache(curatorFramework, "/com/lee");
        treeCache.start();

        Thread.sleep(1000 * 2);

//        curatorFramework.create().creatingParentsIfNeeded().forPath("/com/lee/tree/node/1");
//        curatorFramework.create().creatingParentsIfNeeded().forPath("/com/lee/tree/node/2");
//        curatorFramework.create().creatingParentsIfNeeded().forPath("/com/lee/tree/node/3");
//        curatorFramework.create().creatingParentsIfNeeded().forPath("/com/lee/tree/node/4");

        curatorFramework.setData().forPath("/com/lee/tree/node", "hello world".getBytes());

        ChildData currentData = treeCache.getCurrentData("/com/lee/tree/node");
        System.out.println(new String(currentData.getData()));


        curatorFramework.setData().forPath("/com/lee/tree/node", "hello world1".getBytes());

        currentData = treeCache.getCurrentData("/com/lee/tree/node");
        System.out.println(new String(currentData.getData()));


        curatorFramework.setData().forPath("/com/lee/tree/node/1", "hello world".getBytes());

        currentData = treeCache.getCurrentData("/com/lee/tree/node");
        System.out.println(new String(currentData.getData()));


        currentData = treeCache.getCurrentData("/com/lee/tree/node/1");
        System.out.println(new String(currentData.getData()));


        treeCache.close();
        curatorFramework.close();
        server.stop();
    }
}
