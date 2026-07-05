package com.arch.microservices;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ApiGateway {
    private final ServiceRegistry registry;
    private final Map<String, String> routeMap = new ConcurrentHashMap<>();

    public ApiGateway(ServiceRegistry registry) {
        this.registry = registry;
    }

    public void addRoute(String path, String serviceId) {
        routeMap.put(path, serviceId);
    }

    public String forward(String path) {
        String serviceId = routeMap.get(path);
        if (serviceId == null) {
            throw new RuntimeException("No route for path: " + path);
        }
        ServiceRegistry.ServiceInstance instance = registry.discover(serviceId);
        return "Forwarding request to " + instance.getUrl() + path;
    }

    public static void main(String[] args) {
        ServiceRegistry registry = new ServiceRegistry();
        registry.register("user-service", "localhost", 8081);
        registry.register("order-service", "localhost", 8082);

        ApiGateway gateway = new ApiGateway(registry);
        gateway.addRoute("/users", "user-service");
        gateway.addRoute("/orders", "order-service");

        System.out.println(gateway.forward("/users"));
        System.out.println(gateway.forward("/orders"));
    }
}
