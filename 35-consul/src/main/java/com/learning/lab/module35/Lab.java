package com.learning.lab.module35;

import com.ecwid.consul.v1.*;
import com.ecwid.consul.v1.kv.model.GetValue;
import com.ecwid.consul.v1.agent.model.Member;
import com.ecwid.consul.v1.health.model.HealthService;

import java.util.*;

public class Lab {
    private static final String CONSUL_HOST = "localhost";
    private static final int CONSUL_PORT = 8500;

    public static void main(String[] args) {
        System.out.println("=== Module 35: Consul Service Discovery ===");

        try {
            kvStoreDemo();
            serviceRegistrationDemo();
            serviceDiscoveryDemo();
            healthCheckDemo();
            configurationDemo();
        } catch (Exception e) {
            System.out.println("Note: Requires running Consul agent: consul agent -dev");
            e.printStackTrace();
        }
    }

    static void kvStoreDemo() throws Exception {
        System.out.println("\n--- Consul KV Store ---");
        ConsulClient client = new ConsulClient(CONSUL_HOST, CONSUL_PORT);

        client.setKVValue("app/config/database", "jdbc:postgresql://localhost:5432/db");
        client.setKVValue("app/config/redis", "localhost:6379");
        client.setKVValue("app/config/api-key", "secret-key");

        System.out.println("Keys set successfully");

        List<GetValue> values = client.getKVValues("app/config").getValue();
        for (GetValue v : values) {
            System.out.println("Key: " + v.getKey() + ", Value: " + v.getValue());
        }

        client.deleteKVValue("app/config/api-key");
        System.out.println("Key deleted");
    }

    static void serviceRegistrationDemo() throws Exception {
        System.out.println("\n--- Service Registration ---");
        ConsulClient client = new ConsulClient(CONSUL_HOST, CONSUL_PORT);

        Map<String, Object> service = new HashMap<>();
        service.put("ID", "my-service-1");
        service.put("Name", "my-service");
        service.put("Port", 8080);
        service.put("Address", "localhost");
        service.put("Check", Map.of(
                "HTTP", "http://localhost:8080/health",
                "Interval", "10s",
                "Timeout", "5s"
        ));

        client.agentServiceRegister(service);
        System.out.println("Service registered: my-service");

        client.agentServiceDeregister("my-service-1");
        System.out.println("Service deregistered");
    }

    static void serviceDiscoveryDemo() throws Exception {
        System.out.println("\n--- Service Discovery ---");
        ConsulClient client = new ConsulClient(CONSUL_HOST, CONSUL_PORT);

        List<HealthService> services = client.getHealthServices("my-service", true, null).getValue();
        System.out.println("Found " + services.size() + " healthy instances");

        for (HealthService svc : services) {
            System.out.println("Service: " + svc.getService().getService());
            System.out.println("Host: " + svc.getService().getAddress());
            System.out.println("Port: " + svc.getService().getPort());
        }
    }

    static void healthCheckDemo() throws Exception {
        System.out.println("\n--- Health Checks ---");
        ConsulClient client = new ConsulClient(CONSUL_HOST, CONSUL_PORT);

        System.out.println("Health check types:");
        System.out.println("1. HTTP - GET request to endpoint");
        System.out.println("2. TCP - TCP connection check");
        System.out.println("3. Script - Execute script");
        System.out.println("4. TTL - Service updates TTL");
        System.out.println("5. Docker - Check Docker container");

        List<Member> members = client.getAgentMembers().getValue();
        System.out.println("Agent members: " + members.size());
    }

    static void configurationDemo() throws Exception {
        System.out.println("\n--- Distributed Configuration ---");
        ConsulClient client = new ConsulClient(CONSUL_HOST, CONSUL_PORT);

        String configKey = "app/config/production/database";
        client.setKVValue(configKey, "postgres://prod-db:5432/app");

        GetValue value = client.getKVValue(configKey).getValue();
        System.out.println("Config value: " + (value != null ? value.getValue() : "not found"));

        String watchKey = "app/config/";
        System.out.println("Watching key prefix for changes: " + watchKey);
        System.out.println("Use Consul watches or long polling for updates");
    }
}