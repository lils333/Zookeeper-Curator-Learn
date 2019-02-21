package com.lee.curator.barrier;

import com.lee.curator.basic.Curator;
import com.lee.curator.embed.Server;
import com.lee.curator.embed.StandaloneZookeeperServer;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;

import java.util.concurrent.TimeUnit;

public class DistributedBarrierDemo {

    public static void main(String[] args) throws Exception {
        Server server = new StandaloneZookeeperServer();
        server.start();

        TimeUnit.SECONDS.sleep(2);

        CuratorFramework curatorFramework = Curator.create();
        curatorFramework.start();

        DistributedBarrier barrier = new DistributedBarrier(curatorFramework, "/com/lee/barrier");

        for (int i = 0; i < 5; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Waiter " + Thread.currentThread().getName() + " ");
                    try {
                        barrier.waitOnBarrier();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println("Enter " + Thread.currentThread().getName() + " ");
                }
            }).start();
        }

        barrier.removeBarrier();

        TimeUnit.SECONDS.sleep(5);

        curatorFramework.close();
        server.stop();
    }
}
