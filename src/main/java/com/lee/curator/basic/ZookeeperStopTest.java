package com.lee.curator.basic;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;

import java.util.concurrent.TimeUnit;

public class ZookeeperStopTest {

    public static void main(String[] args) {

        CuratorFramework curatorFramework = Curator.create();
        curatorFramework.start();

        //zookeeper服务器端重新启动，数据会恢复

        //当zookeeper服务停止了以后，如果在sessionTime时间段内重新服务器启动了，并且连接重新连接以后，那么当前sessionID
        //会复用，当前session上面的所有temp node，或则上面的watcher都会被复用，只用当客户端超过了指定sessionTimeout
        //以后还没有和服务器端建立连接，客户端到服务器的状态就是LOST，
        //那么，当服务端再次启动以后，session会重新连接上，但是由于客户端已经LOST了，所以建立的连接都是新的，
        //由于服务端启动以后，所有的数据，状态都会恢复到停止服务之前，所以之前session还存在，但是由于客户端已经关闭了，所以客户端
        //会建立新的session，以前session的数据和watch也会根据客户端是否还在使用这个node来决定是否会被删除掉

        //如果是用于客户端，由于网络延迟等原因，没有能够在指定的sessionTimeout(t)以内发送心跳报给服务器端，服务器端会删除掉当前session的所有数据
        //然后服务器在收到数据以后，就会认为session expired，实际上是心跳报会在一个合理的时间（t/3）以内发送，如果2t/3
        //时间以内还没有接受到服务器端的回复，那么客户端还会在一次重connteciontString中选择下一个进行连接
        try {
            curatorFramework.create().creatingParentContainersIfNeeded()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath("/com/lee/zookeeper-test");

            for (int i = 0; i < 180; i++) {
                TimeUnit.SECONDS.sleep(3);
                System.out.println(StringUtils.repeat("@", 100));
                System.out.println(curatorFramework.getZookeeperClient().getZooKeeper().getSessionId());
                curatorFramework.checkExists().inBackground(new BackgroundCallback() {
                    @Override
                    public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
                        if (event.getResultCode() == KeeperException.Code.OK.intValue()) {
                            System.out.println(event.getStat().getEphemeralOwner());
                        }
                    }
                }).forPath("/com/lee/zookeeper-test");
                System.out.println(StringUtils.repeat("#", 100));
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            curatorFramework.close();
        }
    }
}
