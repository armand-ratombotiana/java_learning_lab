package com.learning.logging;

import java.io.*;
import java.lang.management.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.logging.*;
import java.util.stream.*;

public class Lab {

    private static final Logger ROOT = Logger.getLogger("com.learning.logging");

    public static void main(String[] args) {
        System.out.println("=== Logging & Monitoring Lab ===\n");

        configureLogging();
        logLevels();
        structuredLogging();
        performanceMetrics();
        logRotation();
        monitoringConcepts();
    }

    static void configureLogging() {
        System.out.println("--- Logger Configuration ---");

        ROOT.setLevel(Level.ALL);
        ROOT.setUseParentHandlers(false);

        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.FINE);
        ch.setFormatter(new SimpleFormatter() {
            @Override
            public String format(LogRecord r) {
                return String.format("[%1$tFT%1$tT.%1$tL] %2$-7s %3$s - %4$s%n",
                    r.getMillis(), r.getLevel(), r.getLoggerName(), r.getMessage());
            }
        });
        ROOT.addHandler(ch);

        System.out.println("Root logger configured with ISO-8601 format\n");
    }

    static void logLevels() {
        System.out.println("--- Log Levels Demonstration ---");

        Logger log = Logger.getLogger(Lab.class.getName());
        log.setLevel(Level.ALL);

        log.severe("Database connection failed - retrying...");
        log.warning("Memory usage at 85%, consider scaling up");
        log.info("Application started on port 8080");
        log.fine("Cache hit for key: user_42");
        log.finer("SQL query: SELECT * FROM users WHERE id=?");
        log.finest("Method enter: UserService.findById(42)");

        System.out.println("\nJava Util Logging Levels (descending):");
        String[] levels = {"SEVERE (1000)", "WARNING (900)", "INFO (800)", "CONFIG (700)", "FINE (500)", "FINER (400)", "FINEST (300)"};
        for (String lv : levels) System.out.println("  " + lv);

        System.out.println("\nSLF4J / Logback levels:");
        String[] slf = {"ERROR", "WARN", "INFO", "DEBUG", "TRACE"};
        for (String s : slf) System.out.println("  " + s);
    }

    static void structuredLogging() {
        System.out.println("\n--- Structured Logging (JSON) ---");

        record LogEvent(String level, String logger, String message, long timestamp, String thread) {}

        var events = new ConcurrentLinkedQueue<LogEvent>();

        events.add(new LogEvent("ERROR", "com.learning.db", "Connection timeout after 5000ms",
            System.currentTimeMillis(), Thread.currentThread().getName()));
        events.add(new LogEvent("INFO", "com.learning.api", "GET /api/users/42 returned 200 OK",
            System.currentTimeMillis(), Thread.currentThread().getName()));
        events.add(new LogEvent("WARN", "com.learning.cache", "Cache miss for key 'rates', fetching from DB",
            System.currentTimeMillis(), Thread.currentThread().getName()));

        System.out.println("Structured log output (JSON-like):");
        events.forEach(e -> System.out.printf(
            "  {\"timestamp\":%d,\"level\":\"%s\",\"logger\":\"%s\",\"thread\":\"%s\",\"message\":\"%s\"}%n",
            e.timestamp(), e.level(), e.logger(), e.thread(), e.message()));
    }

    static void performanceMetrics() {
        System.out.println("\n--- Performance Metrics Collection ---");

        class MetricsCollector {
            final ConcurrentHashMap<String, long[]> latencies = new ConcurrentHashMap<>();
            final ConcurrentHashMap<String, Long> counters = new ConcurrentHashMap<>();

            void record(String operation, long durationNanos) {
                latencies.computeIfAbsent(operation, k -> new long[3])[0] += durationNanos;
                latencies.get(operation)[1]++;
                counters.merge(operation, 1L, Long::sum);
            }

            void recordError(String operation) {
                latencies.computeIfAbsent(operation, k -> new long[3])[2]++;
            }

            void report() {
                System.out.printf("%-25s %10s %15s %15s %15s%n", "Operation", "Count", "Total(ms)", "Avg(ms)", "Errors");
                System.out.println("-".repeat(85));
                latencies.forEach((op, data) -> {
                    long count = data[1];
                    double avgMs = count > 0 ? (data[0] / 1_000_000.0) / count : 0;
                    System.out.printf("%-25s %10d %15.2f %15.2f %15d%n",
                        op, count, data[0] / 1_000_000.0, avgMs, data[2]);
                });
            }
        }

        var metrics = new MetricsCollector();
        var rand = ThreadLocalRandom.current();

        for (int i = 0; i < 100; i++) {
            long start = System.nanoTime();
            String op = i < 60 ? "db.query" : (i < 85 ? "api.call" : "cache.get");
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(rand.nextInt(1, 50)));
            metrics.record(op, System.nanoTime() - start);
            if (rand.nextInt(10) == 0) metrics.recordError(op);
        }

        metrics.report();
    }

    static void logRotation() {
        System.out.println("\n--- Log Rotation Strategies ---");

        record RotationStrategy(String name, String config) {}

        List<RotationStrategy> strategies = List.of(
            new RotationStrategy("Size-based", "Roll when file exceeds 10MB, keep 5 archives"),
            new RotationStrategy("Time-based", "Roll daily at midnight, keep 30 days"),
            new RotationStrategy("Size + Time", "Roll at 100MB or hourly, whichever first"),
            new RotationStrategy("Fixed window", "Keep app.log, app.log.1, app.log.2, max 3 files")
        );

        strategies.forEach(s -> System.out.printf("  %-20s %s%n", s.name(), s.config()));

        System.out.println("\nLogback configuration snippet (logback.xml):");
        String config = """
            <configuration>
              <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
                <file>logs/app.log</file>
                <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
                  <fileNamePattern>logs/app-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
                  <maxFileSize>10MB</maxFileSize>
                  <maxHistory>30</maxHistory>
                  <totalSizeCap>1GB</totalSizeCap>
                </rollingPolicy>
                <encoder>
                  <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
                </encoder>
              </appender>
            </configuration>""";
        System.out.println(config);
    }

    static void monitoringConcepts() {
        System.out.println("\n--- Monitoring Concepts ---");

        record Metric(String name, String type, String description) {}
        List<Metric> metrics = List.of(
            new Metric("jvm.memory.used", "Gauge", "Heap memory in use"),
            new Metric("http.requests.total", "Counter", "Total HTTP requests"),
            new Metric("db.query.duration", "Histogram", "Query execution time"),
            new Metric("active.sessions", "Gauge", "Current active sessions"),
            new Metric("errors.total", "Counter", "Total error count")
        );

        System.out.println("Application metrics (Prometheus format):");
        metrics.forEach(m -> System.out.printf(
            "  # HELP %s %s%n  # TYPE %s %s%n  %s 42%n",
            m.name(), m.description(), m.name(), m.type(), m.name()));

        System.out.println("\nMonitoring stack:");
        String[][] stack = {
            {"Prometheus", "Metrics collection & alerting"},
            {"Grafana", "Visualization dashboards"},
            {"ELK Stack", "Centralized logging (Elasticsearch, Logstash, Kibana)"},
            {"JMX", "Java Management Extensions for JVM metrics"},
            {"Micrometer", "Vendor-neutral metrics facade"}
        };
        for (String[] s : stack) System.out.printf("  %-15s %s%n", s[0], s[1]);

        System.out.println("\nHealth check endpoint simulation:");
        record Health(String status, int code, long uptime) {}
        Health h = new Health("UP", 200, ManagementFactory.getRuntimeMXBean().getUptime());
        System.out.printf("  {\"status\":\"%s\",\"httpCode\":%d,\"uptimeMs\":%d}%n", h.status(), h.code(), h.uptime());
    }
}
