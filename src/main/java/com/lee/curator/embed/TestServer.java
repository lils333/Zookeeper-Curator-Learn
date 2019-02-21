package com.lee.curator.embed;

import com.lee.curator.basic.Curator;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class TestServer {

    public static void main(String[] args) throws InterruptedException, IOException, QuorumPeerConfig.ConfigException {
        Server server = new StandaloneZookeeperServer();
        server.start();

        CuratorFramework curatorFramework = Curator.create();
        curatorFramework.start();

        try {
            Stat stat = curatorFramework.checkExists().creatingParentContainersIfNeeded().forPath("/com/lee/test");
            if (stat != null) {
                System.out.println("czxid : " + stat.getCzxid());
                System.out.println("mzxid : " + stat.getMzxid());
                System.out.println("pzxid : " + stat.getPzxid());
                System.out.println("version : " + stat.getVersion());
                System.out.println("aversion : " + stat.getAversion());
                System.out.println("cversion : " + stat.getCversion());
                System.out.println("ctime : " + stat.getCtime());
                System.out.println("mtime : " + stat.getMtime());
                System.out.println("ephemeralowner : " + stat.getEphemeralOwner());
                System.out.println("datalength : " + stat.getDataLength());
                System.out.println("NumChildren : " + stat.getNumChildren());
            } else {
                curatorFramework.create().creatingParentContainersIfNeeded()
                        .withMode(CreateMode.PERSISTENT).forPath("/com/lee/test");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            curatorFramework.close();
        }

        TimeUnit.SECONDS.sleep(60);

        server.stop();
    }
}
