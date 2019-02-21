package com.lee.curator.cache;

import com.lee.curator.basic.Curator;
import com.lee.curator.embed.Server;
import com.lee.curator.embed.StandaloneZookeeperServer;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;

import java.util.concurrent.TimeUnit;

public class NodeCacheDemo {

    public static void main(String[] args) throws Exception {
        Server server = new StandaloneZookeeperServer();
        server.start();

        TimeUnit.SECONDS.sleep(2);

        CuratorFramework curatorFramework = Curator.create();
        curatorFramework.start();

        NodeCache nodeCache = new NodeCache(curatorFramework, "/com/lee/node/cache");
        nodeCache.start(true);//true -- 首先会获取节点值缓存到本地


        for (int i = 0; i < 100; i++) {
            ChildData currentData = nodeCache.getCurrentData();
            if (currentData != null) {
                System.out.println(new String(currentData.getData()));
            }
            TimeUnit.SECONDS.sleep(2);
        }

        nodeCache.close();

        curatorFramework.close();
        server.stop();
    }
}
