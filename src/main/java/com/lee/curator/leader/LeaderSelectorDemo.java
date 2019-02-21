package com.lee.curator.leader;

import com.lee.curator.basic.Curator;
import com.lee.curator.embed.Server;
import com.lee.curator.embed.StandaloneZookeeperServer;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.utils.CloseableUtils;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LeaderSelectorDemo extends LeaderSelectorListenerAdapter implements Closeable {

    private LeaderSelector leaderSelector;

    public LeaderSelectorDemo(CuratorFramework curatorFramework, String path, String id) {
        leaderSelector = new LeaderSelector(curatorFramework, path, this);
        leaderSelector.autoRequeue(); //再一次参与选举
        leaderSelector.setId(id);
    }

    public static void main(String[] args) throws Exception {

        Server server = new StandaloneZookeeperServer();
        server.start();

        TimeUnit.SECONDS.sleep(2);

        List<CuratorFramework> clients = new ArrayList<>();
        List<LeaderSelectorDemo> latches = new ArrayList<>();

        try {
            for (int i = 0; i < 10; i++) {
                CuratorFramework curatorFramework = Curator.create();
                clients.add(curatorFramework);
                curatorFramework.start();

                LeaderSelectorDemo latch = new LeaderSelectorDemo(curatorFramework, "/com/lee/leader", "client : " + i);
                latches.add(latch);
                latch.start();
            }

            TimeUnit.SECONDS.sleep(30);
        } finally {
            for (int i = 0; i < 10; i++) {
                try {
                    CloseableUtils.closeQuietly(latches.get(i));
                } catch (Exception e) {
                }
            }

            TimeUnit.SECONDS.sleep(10);

            for (int i = 0; i < 10; i++) {
                CloseableUtils.closeQuietly(clients.get(i));
            }
        }
        server.stop();
    }

    @Override
    public void takeLeadership(CuratorFramework client) throws Exception {
        //只要不退出，那么就不会释放领导权
        //需要响应中断信号，中断表示当前connection被susupend或者lost了
        System.out.println("leader : " + leaderSelector.getId());
        TimeUnit.SECONDS.sleep(2);
    }

    @Override
    public void close() throws IOException {
        leaderSelector.close();
    }

    public void start() {
        leaderSelector.start();
    }
}
