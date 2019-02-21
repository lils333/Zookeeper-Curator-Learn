package com.lee.curator.sd;

import com.lee.curator.basic.Curator;
import org.apache.curator.framework.CuratorFramework;

import java.util.concurrent.TimeUnit;

public class ServerAttache {

    public static void main(String[] args) {

        CuratorFramework curatorFramework = Curator.create();
        curatorFramework.start();

        addServer(curatorFramework, 6);
    }

    private static void addServer(CuratorFramework curatorFramework, int id) {
        try {
            ServiceRegister serviceRegister = new ServiceRegister(curatorFramework, "/com/lee/sd");

            serviceRegister.register(new ServiceConfig("lee-" + id, "getApplication", "127.0.0.1", 8080));


            TimeUnit.SECONDS.sleep(30);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            curatorFramework.close();
        }
    }
}
