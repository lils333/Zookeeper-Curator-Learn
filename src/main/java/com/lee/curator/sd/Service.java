package com.lee.curator.sd;

import com.lee.curator.basic.Curator;
import com.lee.curator.embed.Server;
import com.lee.curator.embed.StandaloneZookeeperServer;
import org.apache.curator.framework.CuratorFramework;

import java.util.concurrent.TimeUnit;

public class Service {

    public static void main(String[] args) throws Exception {
        Server server = new StandaloneZookeeperServer();
        server.start();

        TimeUnit.SECONDS.sleep(2);

        CuratorFramework curatorFramework = Curator.create();
        curatorFramework.start();

        ServiceRegister serviceRegister = new ServiceRegister(curatorFramework, "/com/lee/sd");
        try {

            for (int i = 0; i < 10; i++) {
                serviceRegister.register(new ServiceConfig("lee-" + i, "getApplication", "127.0.0.1", 8080));
            }

            TimeUnit.MINUTES.sleep(5);
        } finally {
            serviceRegister.close();
            curatorFramework.close();
            server.stop();
        }
    }
}
