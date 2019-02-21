package com.lee.curator.share;

import com.lee.curator.basic.Curator;
import com.lee.curator.embed.Server;
import com.lee.curator.embed.StandaloneZookeeperServer;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.shared.SharedCount;
import org.apache.curator.framework.recipes.shared.VersionedValue;

import java.util.concurrent.TimeUnit;

public class SharedCountMonitor {

    public static void main(String[] args) throws Exception {
        Server server = new StandaloneZookeeperServer();
        server.start();

        TimeUnit.SECONDS.sleep(2);

        CuratorFramework curatorFramework = Curator.create();
        curatorFramework.start();

        SharedCount sharedCount = new SharedCount(curatorFramework, "/com/lee/shared", 6);
        try {
            sharedCount.start();

            for (int i = 0; i < 1000; i++) {
                TimeUnit.SECONDS.sleep(2);
                VersionedValue<Integer> versionedValue = sharedCount.getVersionedValue();
                System.out.println(versionedValue.getVersion() + " : " + versionedValue.getValue());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sharedCount.close();
            curatorFramework.close();
            server.stop();
        }
    }
}
