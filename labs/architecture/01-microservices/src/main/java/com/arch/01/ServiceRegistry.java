package com.arch.microservices;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceRegistry {
    private final Map<String, ServiceInstance> services = new ConcurrentHashMap<>();

    public void register(String serviceId, String host, int port) {
        services.put(serviceId, new ServiceInstance(serviceId, host, port));
        System.out.println("Registered: " + serviceId + " at " + host + ":" + port);
    }

    public ServiceInstance discover(String serviceId) {
        ServiceInstance instance = services.get(serviceId);
        if (instance == null) {
            throw new RuntimeException("Service not found: " + serviceId);
        }
        return instance;
    }

    public void deregister(String serviceId) {
        services.remove(serviceId);
    }

    public static class ServiceInstance {
        private final String serviceId;
        private final String host;
        private final int port;

        public ServiceInstance(String serviceId, String host, int port) {
            this.serviceId = serviceId;
            this.host = host;
            this.port = port;
        }

        public String getUrl() {
            return "http://" + host + ":" + port;
        }
    }
}
