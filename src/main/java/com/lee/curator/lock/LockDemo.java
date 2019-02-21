package com.lee.curator.lock;

import com.lee.curator.basic.Curator;
import com.lee.curator.embed.Server;
import com.lee.curator.embed.StandaloneZookeeperServer;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.Locker;

import java.util.concurrent.TimeUnit;

public class LockDemo {

    public static void main(String[] args) throws Exception {

        Server server = new StandaloneZookeeperServer();
        server.start();

        TimeUnit.SECONDS.sleep(2);

        CuratorFramework curatorFramework = Curator.create();
        curatorFramework.start();

        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    InterProcessMutex mutex = new InterProcessMutex(curatorFramework, "/com/lee/lock");
                    try (Locker locker = new Locker(mutex)) {
                        TimeUnit.SECONDS.sleep(2);
                        System.out.println("acquired by " + Thread.currentThread().getName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        TimeUnit.SECONDS.sleep(30);
        curatorFramework.close();
        server.stop();
    }
}
