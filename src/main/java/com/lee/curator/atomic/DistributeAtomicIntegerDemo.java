package com.lee.curator.atomic;

import com.lee.curator.basic.Curator;
import com.lee.curator.embed.Server;
import com.lee.curator.embed.StandaloneZookeeperServer;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.utils.CloseableUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class DistributeAtomicIntegerDemo {

    public static void main(String[] args) throws Exception {

        Server server = new StandaloneZookeeperServer();
        server.start();

        TimeUnit.SECONDS.sleep(2);

        List<CuratorFramework> clients = new ArrayList<>();

        CountDownLatch latch = new CountDownLatch(4);

        for (int i = 0; i < 4; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    CuratorFramework curatorFramework = Curator.create();
                    curatorFramework.start();
                    clients.add(curatorFramework);
                    DistributedAtomicInteger atomicInteger
                            = new DistributedAtomicInteger(curatorFramework, "/com/lee/auto", new RetryNTimes(5, 1));

                    for (int j = 0; j < 10; j++) {
                        try {
                            AtomicValue<Integer> increment = atomicInteger.increment();
                            System.out.println(increment.postValue());
                            TimeUnit.MILLISECONDS.sleep(500);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    latch.countDown();
                }
            }).start();
        }

        latch.await();

        for (int i = 0; i < 4; i++) {
            CloseableUtils.closeQuietly(clients.get(i));
        }
        server.stop();
    }
}
