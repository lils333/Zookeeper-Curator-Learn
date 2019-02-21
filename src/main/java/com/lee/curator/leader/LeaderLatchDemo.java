package com.lee.curator.leader;

import com.lee.curator.basic.Curator;
import com.lee.curator.embed.Server;
import com.lee.curator.embed.StandaloneZookeeperServer;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.utils.CloseableUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LeaderLatchDemo {

    public static void main(String[] args) throws Exception {

        Server server = new StandaloneZookeeperServer();
        server.start();

        TimeUnit.SECONDS.sleep(2);

        List<CuratorFramework> clients = new ArrayList<>();
        List<LeaderLatch> latches = new ArrayList<>();

        try {
            for (int i = 0; i < 10; i++) {
                CuratorFramework curatorFramework = Curator.create();
                clients.add(curatorFramework);
                curatorFramework.start();

                LeaderLatch latch = new LeaderLatch(curatorFramework, "/com/lee/leader", "client-" + i);
                latches.add(latch);
                latch.start();
            }

            TimeUnit.SECONDS.sleep(2);

            LeaderLatch cureentLeader = null;

            for (int i = 0; i < 10; i++) {
                if (latches.get(i).hasLeadership()) {
                    cureentLeader = latches.get(i);
                    System.out.println("current leader : " + latches.get(i).getId());
                }
            }

            TimeUnit.SECONDS.sleep(2);

            cureentLeader.close();

            TimeUnit.SECONDS.sleep(3);

            for (int i = 0; i < 10; i++) {
                if (latches.get(i).hasLeadership()) {
                    System.out.println("current leader : " + latches.get(i).getId());
                }
            }

            TimeUnit.SECONDS.sleep(5);
        } finally {
            for (int i = 0; i < 10; i++) {
                try {
                    CloseableUtils.closeQuietly(latches.get(i));
                } catch (Exception e) {
                }
            }

            for (int i = 0; i < 10; i++) {
                CloseableUtils.closeQuietly(clients.get(i));
            }
        }

        server.stop();
    }
}
