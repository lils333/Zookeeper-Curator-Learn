package com.lee.curator.basic;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.api.UnhandledErrorListener;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;

import java.util.concurrent.TimeUnit;

public class Delete {

    public static void main(String[] args) {

        String path = "/com/lee/delete/jkl";

        CuratorFramework curatorFramework = Curator.create();
        curatorFramework.start();

        CuratorWatcher watcher = new CuratorWatcher() {
            @Override
            public void process(WatchedEvent event) throws Exception {
                System.out.println(event);
            }
        };

        try {
            //如果使用checkExists来创建节点的话，默认只能够创建父节点，创建不了孩子节点，所以还是必须通过create来创建孩子节点
            //exist 或者 getData都会接受到节点的删除，节点数据的修改，exist还可以节点到节点的创建
            curatorFramework.checkExists()
                    .creatingParentContainersIfNeeded()
                    .usingWatcher(watcher)
                    .inBackground(new BackgroundCallback() {
                        @Override
                        public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
                            System.out.println(event.getType());
                        }
                    })
                    .withUnhandledErrorListener(new UnhandledErrorListener() {
                        @Override
                        public void unhandledError(String message, Throwable e) {
                            System.out.println(message);
                        }
                    }).forPath(path);

            String s = curatorFramework.create()
                    .creatingParentContainersIfNeeded()
                    //.withProtection() //会创建一个GUID在节点前面
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(path);
            System.out.println(s);

            curatorFramework.getChildren().usingWatcher(new CuratorWatcher() {
                @Override
                public void process(WatchedEvent event) throws Exception {
                    System.out.println(event);
                }
            }).inBackground(new BackgroundCallback() {
                @Override
                public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
                    System.out.println(event.getType() + " : " + event.getChildren());
                }
            }).forPath(ZKPaths.getPathAndNode(path).getPath());

            curatorFramework.getData().usingWatcher(watcher).inBackground(new BackgroundCallback() {
                @Override
                public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
                    System.out.println(event.getType());
                }
            }).forPath(path);

            String jkl1 = curatorFramework.create()
                    .creatingParentContainersIfNeeded()
                    //.withProtection()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(ZKPaths.makePath(ZKPaths.getPathAndNode(path).getPath(), "jkl1"));

            TimeUnit.SECONDS.sleep(3);

            curatorFramework.delete()
                    .guaranteed()
                    .deletingChildrenIfNeeded()
                    .inBackground(new BackgroundCallback() {
                        @Override
                        public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
                            System.out.println(event.getType());
                        }
                    }).forPath(s);

            TimeUnit.SECONDS.sleep(3);

            TimeUnit.SECONDS.sleep(10);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭的时候，如果还有临时节点。那么还是会触发所有的临时节点的删除通知，所有在临时节点上面注册了watch的都会收到通知
            curatorFramework.close();
        }

    }
}
