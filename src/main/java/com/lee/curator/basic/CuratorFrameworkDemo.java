package com.lee.curator.basic;

import org.apache.curator.framework.CuratorFramework;

public class CuratorFrameworkDemo {

    public static void main(String[] args) {
        create();
    }

    private static void create() {
        try (CuratorFramework client = Curator.create()) {
            client.start();
            CreatePath createPath = new CreatePath();
//            createPath.create(client);
            createPath.createTemp(client);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}