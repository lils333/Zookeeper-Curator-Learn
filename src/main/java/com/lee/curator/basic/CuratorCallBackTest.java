package com.lee.curator.basic;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;

import java.util.concurrent.TimeUnit;

public class CuratorCallBackTest {

    public static void main(String[] args) {

        CuratorFramework curatorFramework = Curator.create();
        curatorFramework.start();

        //所有的callback方法都不会执行，也就是说，在连接断开以后，所有的异步执行的方法都会失败，但是失败的前提是建立在重试
        //已经完成了的情况，每一个异步的操作都会被封装成一个操作对象，然后把这个对象放到后台线程去执行，只有连接是成功的才会被
        //执行，连接不成功，会采用重试机制，一直到从事机制不满足以后，当前异步操作的对象就会丢弃掉，在取下一个异步操作来执行
        //也就是说，callback在连接断开的时候是不会被回调的。
        try {
            for (int i = 0; i < 180; i++) {
                TimeUnit.SECONDS.sleep(2);
                curatorFramework.getData().inBackground(new BackgroundCallback() {
                    @Override
                    public void processResult(CuratorFramework curator, CuratorEvent event) throws Exception {
                        System.out.println(event);
                    }
                }).forPath("/com/lee/fuck");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            curatorFramework.close();
        }
    }
}
