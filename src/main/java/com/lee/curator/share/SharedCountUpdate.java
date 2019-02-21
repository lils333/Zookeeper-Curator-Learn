package com.lee.curator.share;

import com.lee.curator.basic.Curator;
import org.apache.curator.framework.CuratorFramework;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

public class SharedCountUpdate {

    public static void main(String[] args) throws Exception {
        CuratorFramework curatorFramework = Curator.create();
        curatorFramework.start();

        for (int i = 0; i < 100; i++) {
            byte[] bytes = new byte[4];
            ByteBuffer.wrap(bytes).putInt(i);
            curatorFramework.setData().forPath("/com/lee/shared", bytes);
            TimeUnit.MILLISECONDS.sleep(200);
        }
        curatorFramework.close();
    }
}
