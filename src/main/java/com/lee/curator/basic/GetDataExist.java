package com.lee.curator.basic;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.TimeUnit;

public class GetDataExist {

    public static void main(String[] args) throws Exception {

        CuratorFramework curatorFramework = Curator.create();
        curatorFramework.start();

        //getData必须要node存在，才可以添加watcher获取数据等等才做，如果不存在直接报错误
//        curatorFramework.getData().usingWatcher(new CuratorWatcher() {
//            @Override
//            public void process(WatchedEvent event) throws Exception {
//                System.out.println(event);
//            }
//        }).forPath("/com/com");

        //exist可以为不存在的znode添加一个wather，如果不存在返回值就为null，所以不会出现错误
        curatorFramework.checkExists().creatingParentContainersIfNeeded()
                .usingWatcher(new CuratorWatcher() {
                    @Override
                    public void process(WatchedEvent event) throws Exception {
                        System.out.println(event);
                    }
                }).forPath("/com/com");

        String s = curatorFramework
                .create()
                .compressed()
                .creatingParentContainersIfNeeded()
                .withMode(CreateMode.EPHEMERAL).forPath("/com/lee/test/jkl");
        System.out.println(s);

        CuratorWatcher watcher = new CuratorWatcher() {
            @Override
            public void process(WatchedEvent event) throws Exception {
                System.out.println(event);
            }
        };

        //首先把watch注册到指定的znode上，然后在把调用相应的方法，之后再把返回值封装成一个CuratorEvent，然后放到台去运行
        curatorFramework
                .checkExists()
                .creatingParentContainersIfNeeded()
                .usingWatcher(watcher).inBackground(new BackgroundCallback() {
            @Override
            public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
                //因为调用的是exist，只有一个znode的状态，没有值的信息
                System.out.println(event.getType() + " : " + event.getStat() + " : " + event.getData());
                System.out.println(event);
            }
        }).forPath("/com/lee/test/jkl");

        //获取数据的时候也可以获取这个节点状态信息
        byte[] bytes = curatorFramework
                .getData().decompressed().usingWatcher(watcher).inBackground(new BackgroundCallback() {
                    @Override
                    public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
                        System.out.println(event.getType() + " : " + new String(event.getData()) + " : " + event.getStat());
                        System.out.println(event);
                    }
                }).forPath("/com/lee/test/jkl");

        TimeUnit.SECONDS.sleep(5);

        Stat stat1 = curatorFramework.setData().compressed().forPath("/com/lee/test/jkl", "hello world".getBytes());

        TimeUnit.MINUTES.sleep(1);

        curatorFramework.close();
    }
}
