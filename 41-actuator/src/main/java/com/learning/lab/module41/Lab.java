package com.learning.lab.module41;

import org.springframework.boot.actuate.health.*;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.boot.actuate.endpoint.Endpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class Lab {

    public static void main(String[] args) {
        System.out.println("=== Module 41: Actuator Lab ===\n");

        SpringApplication.run(Lab.class, args);

        System.out.println("\n1. Health Endpoint:");
        healthEndpointDemo();

        System.out.println("\n2. Info Endpoint:");
        infoEndpointDemo();

        System.out.println("\n3. Metrics Endpoint:");
        metricsEndpointDemo();

        System.out.println("\n4. Custom Endpoints:");
        customEndpointsDemo();

        System.out.println("\n=== Actuator Lab Complete ===");
    }

    static void healthEndpointDemo() {
        System.out.println("   Health Endpoint: /actuator/health");
        System.out.println("   Health Details: /actuator/health/{component}");
        System.out.println("   Health Indicators:");
        System.out.println("   - DiskSpaceHealthIndicator: Checks disk space");
        System.out.println("   - DataSourceHealthIndicator: Checks DB connectivity");
        System.out.println("   - RedisHealthIndicator: Checks Redis connection");
        System.out.println("   - MailHealthIndicator: Checks mail server");
        System.out.println("   - CustomHealthIndicator: User-defined health checks");
        System.out.println("   Response: {\"status\":\"UP\",\"components\":{...}}");
    }

    static void infoEndpointDemo() {
        System.out.println("   Info Endpoint: /actuator/info");
        System.out.println("   Git Info: /actuator/info/git");
        System.out.println("   Build Info: /actuator/info/build");
        System.out.println("   Java Info: /actuator/info/java");
        System.out.println("   Environment Info: /actuator/info/env");
        System.out.println("   Custom Info: /actuator/info/custom");
    }

    static void metricsEndpointDemo() {
        System.out.println("   Metrics Endpoint: /actuator/metrics");
        System.out.println("   Specific Metric: /actuator/metrics/{metricName}");
        System.out.println("   Available Metrics:");
        System.out.println("   - jvm.memory.used");
        System.out.println("   - jvm.gc.pause");
        System.out.println("   - process.cpu.usage");
        System.out.println("   - http.server.requests");
        System.out.println("   - logback.events");
        System.out.println("   Tags: /actuator/metrics/jvm.memory.used?tag=area:heap");
    }

    static void customEndpointsDemo() {
        System.out.println("   Built-in Endpoints:");
        System.out.println("   - /actuator/health: Health check");
        System.out.println("   - /actuator/info: Application info");
        System.out.println("   - /actuator/metrics: Metrics");
        System.out.println("   - /actuator/env: Environment properties");
        System.out.println("   - /actuator/beans: Bean information");
        System.out.println("   - /actuator/conditions: Auto-configuration");
        System.out.println("   - /actuator/loggers: Logger configuration");
        System.out.println("   - /actuator/threaddump: Thread dump");
        System.out.println("   - /actuator/heapdump: Heap dump");
        System.out.println("   - /actuator/scheduledtasks: Scheduled tasks");
    }

    @Bean
    public HealthIndicator customHealth() {
        return () -> Health.up().withDetail("custom", "OK").build();
    }
}