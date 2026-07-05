package com.arch.microservices;

import java.util.List;
import java.util.ArrayList;

public class ServiceDiscovery {
    private final ServiceRegistry registry;

    public ServiceDiscovery(ServiceRegistry registry) {
        this.registry = registry;
    }

    public List<String> discoverAll(String serviceId) {
        List<String> urls = new ArrayList<>();
        try {
            ServiceRegistry.ServiceInstance instance = registry.discover(serviceId);
            urls.add(instance.getUrl());
        } catch (RuntimeException e) {
            System.out.println("Discovery failed: " + e.getMessage());
        }
        return urls;
    }

    public static void main(String[] args) {
        ServiceRegistry registry = new ServiceRegistry();
        registry.register("payment-service", "192.168.1.10", 8090);

        ServiceDiscovery discovery = new ServiceDiscovery(registry);
        List<String> endpoints = discovery.discoverAll("payment-service");
        endpoints.forEach(e -> System.out.println("Discovered: " + e));
    }
}
