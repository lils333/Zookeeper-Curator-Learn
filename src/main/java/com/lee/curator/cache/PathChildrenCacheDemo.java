package com.lee.curator.cache;

import com.lee.curator.basic.Curator;
import com.lee.curator.embed.Server;
import com.lee.curator.embed.StandaloneZookeeperServer;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.zookeeper.CreateMode;

import java.util.concurrent.TimeUnit;

public class PathChildrenCacheDemo {

    public static void main(String[] args) throws Exception {
        Server server = new StandaloneZookeeperServer();
        server.start();

        TimeUnit.SECONDS.sleep(2);

        CuratorFramework curatorFramework = Curator.create();
        curatorFramework.start();

        PathChildrenCache pathChildrenCache = new PathChildrenCache(curatorFramework, "/com/lee/pathc", true);
        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                System.out.println("eventtype : " + event.getType());
                System.out.println("eventtype : " + event.getData());
            }
        });

        pathChildrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);


        TimeUnit.SECONDS.sleep(3);

        curatorFramework.create()
                .creatingParentContainersIfNeeded()
                .withMode(CreateMode.EPHEMERAL).forPath("/com/lee/pathc/1");
        TimeUnit.SECONDS.sleep(3);
        curatorFramework.create()
                .creatingParentContainersIfNeeded()
                .withMode(CreateMode.EPHEMERAL).forPath("/com/lee/pathc/2");
        TimeUnit.SECONDS.sleep(3);
        curatorFramework.create()
                .creatingParentContainersIfNeeded()
                .withMode(CreateMode.EPHEMERAL).forPath("/com/lee/pathc/3");
        TimeUnit.SECONDS.sleep(3);
        curatorFramework.create()
                .creatingParentContainersIfNeeded()
                .withMode(CreateMode.EPHEMERAL).forPath("/com/lee/pathc/4");

        TimeUnit.SECONDS.sleep(3);


        curatorFramework.delete().guaranteed().deletingChildrenIfNeeded().forPath("/com/lee/pathc/1");
        TimeUnit.SECONDS.sleep(3);
        curatorFramework.delete().guaranteed().deletingChildrenIfNeeded().forPath("/com/lee/pathc/2");
        TimeUnit.SECONDS.sleep(3);
        curatorFramework.delete().guaranteed().deletingChildrenIfNeeded().forPath("/com/lee/pathc/3");
        TimeUnit.SECONDS.sleep(3);
        curatorFramework.delete().guaranteed().deletingChildrenIfNeeded().forPath("/com/lee/pathc/4");

        TimeUnit.SECONDS.sleep(5);


        pathChildrenCache.close();
        curatorFramework.close();
        server.stop();
    }
}
