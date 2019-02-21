package com.lee.curator.sd;

import com.google.common.collect.Maps;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.apache.curator.x.discovery.strategies.RoundRobinStrategy;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

public class ServiceLookup implements Closeable {

    private ServiceDiscovery<ServiceConfig> discovery;
    private Map<String, ServiceProvider<ServiceConfig>> providers = Maps.newConcurrentMap();

    public ServiceLookup(CuratorFramework client, String registerPath) throws Exception {
        discovery = ServiceDiscoveryBuilder.builder(ServiceConfig.class)
                .serializer(new JsonInstanceSerializer<>(ServiceConfig.class))
                .basePath(registerPath)
                .client(client)
                .build();
        discovery.start();
    }

    /**
     * service lookup
     *
     * @param serviceName need find service
     * @return if no service is find, then return null
     * @throws Exception any exception
     */
    public ServiceConfig lookup(String serviceName) throws Exception {
        ServiceProvider<ServiceConfig> provider = providers.get(serviceName);
        if (provider == null) {
            synchronized (this) {
                if ((provider = providers.get(serviceName)) == null) {
                    provider = discovery.serviceProviderBuilder()
                            .serviceName(serviceName)
                            .providerStrategy(new RoundRobinStrategy<>())
                            .build();
                    provider.start();
                    providers.put(serviceName, provider);
                }
            }
        }
        ServiceInstance<ServiceConfig> instance = provider.getInstance();
        if (instance != null) {
            return instance.getPayload();
        } else {
            return null;
        }
    }

    @Override
    public void close() {
        //discovery contains all providers in list ,and will be closed when discovery closed
        if (discovery != null) {
            CloseableUtils.closeQuietly(discovery);
        }
    }
}
