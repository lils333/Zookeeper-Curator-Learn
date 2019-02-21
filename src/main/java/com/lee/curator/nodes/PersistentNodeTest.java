package com.lee.curator.nodes;

import com.lee.curator.basic.Curator;
import com.lee.curator.embed.Server;
import com.lee.curator.embed.StandaloneZookeeperServer;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.nodes.PersistentNode;
import org.apache.zookeeper.CreateMode;

import java.util.concurrent.TimeUnit;

public class PersistentNodeTest {

    public static void main(String[] args) throws Exception {
        Server server = new StandaloneZookeeperServer();
        server.start();

        TimeUnit.SECONDS.sleep(2);

        CuratorFramework curatorFramework = Curator.create();
        curatorFramework.start();

        PersistentNode persistentNode = new PersistentNode(
                curatorFramework, CreateMode.EPHEMERAL, false, "/com/lee/persistent", "hello world".getBytes());
        persistentNode.start();

        try {

            for (int i = 0; i < 50; i++) {
                TimeUnit.SECONDS.sleep(2);
                System.out.println(new String(persistentNode.getData()));
            }

            TimeUnit.SECONDS.sleep(30);

        } finally {
            persistentNode.close();
            curatorFramework.close();
            server.start();
        }
    }

}
