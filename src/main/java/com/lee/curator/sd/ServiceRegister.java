package com.lee.curator.sd;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.io.Closeable;

public class ServiceRegister implements Closeable {

    private ServiceDiscovery<ServiceConfig> discovery;

    public ServiceRegister(CuratorFramework client, String registerPath) throws Exception {
        this.discovery = ServiceDiscoveryBuilder.builder(ServiceConfig.class)
                .serializer(new JsonInstanceSerializer<>(ServiceConfig.class))
                .basePath(registerPath)
                .client(client)
                .build();
        discovery.start();
    }

    /**
     * @param config ServiceInstance represent a service instance
     * @throws Exception if any error
     */
    public void register(ServiceConfig config) throws Exception {
        discovery.registerService(
                ServiceInstance.<ServiceConfig>builder()
                        .id(config.getId())
                        .name(config.getName())
                        .address(config.getAddress())
                        .port(config.getPort())
                        .registrationTimeUTC(System.currentTimeMillis())
                        .payload(config)
                        .build()
        );
    }

    /**
     * unregister a exists service instance, if unrigister a not exists service instance nothing happen
     *
     * @param config unregister a service from nameing service
     * @throws Exception if any exception
     */
    public void unRegister(ServiceConfig config) throws Exception {
        discovery.unregisterService(
                ServiceInstance.<ServiceConfig>builder()
                        .id(config.getId())
                        .name(config.getName())
                        .build()
        );
    }

    /**
     * @param config dynamic modify service instance with same instance id
     * @throws Exception if any exception
     */
    public void update(ServiceConfig config) throws Exception {
        discovery.updateService(
                ServiceInstance.<ServiceConfig>builder()
                        .id(config.getId())
                        .name(config.getName())
                        .address(config.getAddress())
                        .port(config.getPort())
                        .registrationTimeUTC(System.currentTimeMillis())
                        .payload(config)
                        .build()
        );
    }

    @Override
    public void close() {
        if (discovery != null) {
            CloseableUtils.closeQuietly(discovery);
        }
    }
}
