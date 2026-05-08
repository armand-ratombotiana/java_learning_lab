package com.learning.actuator;

import java.lang.management.*;
import java.time.*;
import java.util.*;
import java.util.stream.*;

public class Lab {
    record HealthIndicator(String name, boolean healthy, String detail) {}

    record Metric(String name, double value, String unit) {}

    record Endpoint(String path, String description, boolean enabled) {}

    static class ActuatorEndpoints {
        List<Endpoint> endpoints = new ArrayList<>();

        ActuatorEndpoints() {
            endpoints.add(new Endpoint("/actuator/health", "Application health status", true));
            endpoints.add(new Endpoint("/actuator/info", "Application info", true));
            endpoints.add(new Endpoint("/actuator/metrics", "Application metrics", true));
            endpoints.add(new Endpoint("/actuator/env", "Environment properties", true));
            endpoints.add(new Endpoint("/actuator/beans", "Spring beans", true));
            endpoints.add(new Endpoint("/actuator/mappings", "Request mappings", true));
            endpoints.add(new Endpoint("/actuator/threaddump", "Thread dump", true));
            endpoints.add(new Endpoint("/actuator/heapdump", "Heap dump (download)", false));
            endpoints.add(new Endpoint("/actuator/loggers", "Logger configuration", true));
            endpoints.add(new Endpoint("/actuator/httptrace", "HTTP request/response traces", true));
        }

        List<Endpoint> listAll() { return endpoints; }

        List<Endpoint> listEnabled() {
            return endpoints.stream().filter(Endpoint::enabled).collect(Collectors.toList());
        }
    }

    static class HealthAggregator {
        List<HealthIndicator> indicators = new ArrayList<>();

        HealthAggregator() {
            indicators.add(new HealthIndicator("db", true, "Database is reachable"));
            indicators.add(new HealthIndicator("diskSpace", true, "Free space: 45.2 GB"));
            indicators.add(new HealthIndicator("ping", true, "OK"));
            indicators.add(new HealthIndicator("redis", true, "Redis cache connected"));
            indicators.add(new HealthIndicator("rabbitmq", false, "RabbitMQ connection refused"));
        }

        String getOverallStatus() {
            boolean allHealthy = indicators.stream().allMatch(HealthIndicator::healthy);
            return allHealthy ? "UP" : "DEGRADED";
        }

        List<HealthIndicator> getIndicators() { return indicators; }

        List<HealthIndicator> getUnhealthy() {
            return indicators.stream().filter(i -> !i.healthy).collect(Collectors.toList());
        }
    }

    interface MetricCollector {
        List<Metric> collect();
    }

    static class JvmMetrics implements MetricCollector {
        public List<Metric> collect() {
            var rt = Runtime.getRuntime();
            var mem = ManagementFactory.getMemoryMXBean();
            var os = ManagementFactory.getOperatingSystemMXBean();
            var threads = ManagementFactory.getThreadMXBean();

            return List.of(
                new Metric("jvm.memory.used", (mem.getHeapMemoryUsage().getUsed() + mem.getNonHeapMemoryUsage().getUsed()) / 1048576.0, "MB"),
                new Metric("jvm.memory.max", (mem.getHeapMemoryUsage().getMax() + mem.getNonHeapMemoryUsage().getMax()) / 1048576.0, "MB"),
                new Metric("jvm.threads.live", threads.getThreadCount(), "threads"),
                new Metric("jvm.threads.daemon", threads.getDaemonThreadCount(), "threads"),
                new Metric("system.cpu.count", os.getAvailableProcessors(), "cores"),
                new Metric("process.uptime", ManagementFactory.getRuntimeMXBean().getUptime() / 1000.0, "seconds")
            );
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Spring Boot Actuator Concepts Lab ===\n");

        System.out.println("1. Available Actuator Endpoints:");
        var actuator = new ActuatorEndpoints();
        actuator.listEnabled().forEach(e ->
            System.out.println("   " + e.path() + " - " + e.description()));

        System.out.println("\n2. Health Endpoint:");
        var health = new HealthAggregator();
        System.out.println("   Overall status: " + health.getOverallStatus());
        health.getIndicators().forEach(i ->
            System.out.println("   " + i.name() + ": " + (i.healthy() ? "UP" : "DOWN") + " (" + i.detail() + ")"));

        if (!health.getUnhealthy().isEmpty()) {
            System.out.println("   Unhealthy components detected!");
            health.getUnhealthy().forEach(i ->
                System.out.println("   - " + i.name() + ": " + i.detail()));
        }

        System.out.println("\n3. Metrics (JVM via ManagementFactory):");
        var metrics = new JvmMetrics();
        metrics.collect().forEach(m ->
            System.out.println("   " + m.name() + ": " + String.format("%.1f", m.value()) + " " + m.unit()));

        System.out.println("\n4. /actuator/info:");
        System.out.println("   app.name: java-learning-lab");
        System.out.println("   app.version: 1.0.0");
        System.out.println("   java.version: " + System.getProperty("java.version"));
        System.out.println("   os.name: " + System.getProperty("os.name"));

        System.out.println("\n5. /actuator/env (environment properties):");
        System.out.println("   server.port: 8080");
        System.out.println("   spring.datasource.url: jdbc:postgresql://localhost:5432/db");
        System.out.println("   spring.profiles.active: prod");
        System.out.println("   user.dir: " + System.getProperty("user.dir"));

        System.out.println("\n6. /actuator/loggers:");
        System.out.println("   ROOT: INFO");
        System.out.println("   com.learning: DEBUG");
        System.out.println("   org.springframework.web: WARN");
        System.out.println("   POST /actuator/loggers/com.learning - change log level at runtime");

        System.out.println("\n7. /actuator/httptrace:");
        System.out.println("   Shows last 100 HTTP request/response exchanges");
        System.out.println("   Includes: method, path, status, timeTaken, headers");

        System.out.println("\n8. Custom Health Indicator pattern:");
        System.out.println("   Implement HealthIndicator interface");
        System.out.println("   Override health() method returning Health object");
        System.out.println("   Return Health.up() or Health.down() with details");

        System.out.println("\n9. Security:");
        System.out.println("   management.endpoints.web.exposure.include=health,metrics,info");
        System.out.println("   Sensitive endpoints like env, beans should be restricted");
        System.out.println("   Spring Security can secure actuator paths");

        System.out.println("\n=== Lab Complete ===");
    }
}
