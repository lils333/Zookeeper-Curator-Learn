package com.lee.curator.basic;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.StandardConnectionStateErrorPolicy;
import org.apache.curator.retry.BoundedExponentialBackoffRetry;
import org.apache.curator.retry.RetryOneTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Curator {

    private static final Logger LOGGER = LoggerFactory.getLogger(Curator.class);

    //同一个node的所有操作都是有顺序的，不管是异步还是同步，都会按照顺序进行
    public static CuratorFramework create() {
        return CuratorFrameworkFactory.builder()
//                .connectString("135.242.204.161:2181")
                .connectString("127.0.0.1:2181")
                .connectionTimeoutMs(5000)
                .sessionTimeoutMs(15000)
                .maxCloseWaitMs(3000)
                .retryPolicy(new BoundedExponentialBackoffRetry(100, 3000, 15))
                .compressionProvider(new Lz4CompressProvider())
                .connectionStateErrorPolicy(new StandardConnectionStateErrorPolicy()) //SUSPEND and LOST
                .zk34CompatibilityMode(true)
                .threadFactory(
                        new BasicThreadFactory.Builder()
                                .namingPattern("curator-%d")
                                .uncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                                    @Override
                                    public void uncaughtException(Thread t, Throwable e) {
                                        LOGGER.warn("xxx");
                                    }
                                }).build()
                ).build();
    }
}
