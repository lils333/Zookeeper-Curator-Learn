package com.lee.curator.embed;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ThreadFactory;

public class StandaloneZookeeperServer extends ZooKeeperServerMain implements Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(StandaloneZookeeperServer.class);

    private ThreadFactory threadFactory = new BasicThreadFactory.Builder()
            .namingPattern("Zookeeper-server-%d")
            .daemon(false)
            .priority(Thread.NORM_PRIORITY)
            .uncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    LOGGER.warn("Thread " + t.getName() + " exit, caused by " + e.getMessage(), e);
                }
            }).build();

    public void start() {
        Thread thread = threadFactory.newThread(new Runnable() {
            @Override
            public void run() {
                try {
                    runFromConfig(createConfig());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (QuorumPeerConfig.ConfigException e) {
                    e.printStackTrace();
                }
                LOGGER.info("Zookeeper exit");
            }
        });
        thread.start();
    }

    public void stop() {
        super.shutdown();
    }

    private ServerConfig createConfig() throws IOException, QuorumPeerConfig.ConfigException {
        QuorumPeerConfig quorumConfig = new QuorumPeerConfig();
        quorumConfig.parseProperties(getProperty());
        ServerConfig config = new ServerConfig();
        config.readFrom(quorumConfig);
        return config;
    }

    private Properties getProperty() {
        Properties props = new Properties();
        props.setProperty("tickTime", "2000");
        props.setProperty("dataDir", new File("C:\\var\\opt\\oss", "zookeeper").getAbsolutePath());
        props.setProperty("clientPort", "2181");
        props.setProperty("initLimit", "10");
        props.setProperty("syncLimit", "5");
        return props;
    }
}
