package com.learning.logging;

import java.util.*;
import java.util.concurrent.*;
import java.time.*;

public class LoggingMonitoringTraining {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(LoggingMonitoringTraining.class);

    public static void main(String[] args) {
        System.out.println("=== Logging and Monitoring Training ===");

        demonstrateLoggingLevels();
        demonstrateStructuredLogging();
        demonstrateMetrics();
        demonstrateTracing();
    }

    private static void demonstrateLoggingLevels() {
        System.out.println("\n--- SLF4J Logging Levels ---");

        logger.trace("Detailed trace message");
        logger.debug("Debug message for development");
        logger.info("Info message - application status");
        logger.warn("Warning message - potential issue");
        logger.error("Error message - failure occurred");

        System.out.println("\nLog Level Hierarchy (most to least verbose):");
        String[] levels = {"TRACE", "DEBUG", "INFO", "WARN", "ERROR"};
        for (int i = 0; i < levels.length; i++) {
            System.out.printf("  %d. %s%n", i + 1, levels[i]);
        }
    }

    private static void demonstrateStructuredLogging() {
        System.out.println("\n--- Structured Logging ---");

        String userId = "user-123";
        String action = "login";
        long duration = 150;

        logger.info("action={} userId={} duration={}ms", action, userId, duration);

        Map<String, Object> event = new LinkedHashMap<>();
        event.put("timestamp", Instant.now().toString());
        event.put("level", "INFO");
        event.put("event", "user_action");
        event.put("userId", userId);
        event.put("action", action);
        event.put("duration_ms", duration);

        System.out.println("Structured Log Entry:");
        System.out.println(event);
    }

    private static void demonstrateMetrics() {
        System.out.println("\n--- Application Metrics ---");

        Map<String, Object> metrics = new LinkedHashMap<>();

        metrics.put("jvm.memory.used", "256 MB");
        metrics.put("jvm.cpu.usage", "45%");
        metrics.put("http.requests.total", 1250);
        metrics.put("http.requests.errors", 5);
        metrics.put("db.connections.active", 10);
        metrics.put("app.uptime.seconds", 3600);

        System.out.println("Key Metrics:");
        metrics.forEach((key, value) -> System.out.printf("  %s = %s%n", key, value));

        System.out.println("\nMetric Types:");
        String[] types = {
            "Counter - cumulative values (request count)",
            "Gauge - current value (memory usage)",
            "Histogram - distribution (response time)",
            "Timer - duration metrics"
        };
        for (String type : types) {
            System.out.println("  - " + type);
        }
    }

    private static void demonstrateTracing() {
        System.out.println("\n--- Distributed Tracing ---");

        String traceId = UUID.randomUUID().toString().substring(0, 8);
        String spanId = UUID.randomUUID().toString().substring(0, 8);

        System.out.println("Trace Context:");
        System.out.println("  traceId: " + traceId);
        System.out.println("  spanId: " + spanId);

        List<Map<String, String>> spans = Arrays.asList(
            Map.of("name", "http-request", "duration", "120ms"),
            Map.of("name", "db-query", "duration", "45ms"),
            Map.of("name", "external-api", "duration", "80ms")
        );

        System.out.println("\nDistributed Trace Spans:");
        for (Map<String, String> span : spans) {
            System.out.printf("  [%s] %s - %s%n", traceId, span.get("name"), span.get("duration"));
        }

        System.out.println("\nObservability Best Practices:");
        String[] practices = {
            "Log structured data, not just strings",
            "Use correlation IDs across services",
            "Export metrics to Prometheus/DataDog",
            "Implement distributed tracing (Zipkin/Jaeger)",
            "Set up alerting on anomaly detection"
        };
        for (String practice : practices) {
            System.out.println("  - " + practice);
        }
    }
}