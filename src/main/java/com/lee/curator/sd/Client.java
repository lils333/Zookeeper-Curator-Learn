package com.lee.curator.sd;

import com.lee.curator.basic.Curator;
import org.apache.curator.framework.CuratorFramework;

public class Client {

    public static void main(String[] args) throws Exception {

        CuratorFramework curatorFramework = Curator.create();
        curatorFramework.start();


        ServiceLookup lookup = new ServiceLookup(curatorFramework, "/com/lee/sd");
        try {
            for (int i = 0; i < 100; i++) {
                ServiceConfig service = lookup.lookup("getApplication");
                System.out.println(service);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lookup.close();
            curatorFramework.close();
        }
    }
}
