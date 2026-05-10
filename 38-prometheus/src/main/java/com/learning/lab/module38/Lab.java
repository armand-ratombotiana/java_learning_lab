package com.learning.lab.module38;

import io.micrometer.core.instrument.*;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class Lab {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Module 38: Prometheus Lab ===\n");

        PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

        System.out.println("1. Metrics Configuration:");
        System.out.println("   - Registry: PrometheusMeterRegistry");
        System.out.println("   - Scrape Endpoint: /prometheus");
        System.out.println("   - Default Metrics: JVM, CPU, Memory");

        System.out.println("\n2. Counters:");
        Counter requestsCounter = Counter.builder("http_requests_total")
                .tag("method", "GET")
                .tag("endpoint", "/api/users")
                .description("Total HTTP requests")
                .register(registry);
        requestsCounter.increment(10);
        requestsCounter.increment(5);
        System.out.println("   http_requests_total: " + requestsCounter.count());

        System.out.println("\n3. Gauges:");
        Gauge.builder("active_connections", () -> 42)
                .tag("service", "api")
                .description("Active connections")
                .register(registry);
        System.out.println("   active_connections: 42");

        System.out.println("\n4. Histograms:");
        Histogram requestLatency = Histogram.builder("request_duration_seconds")
                .tag("service", "api")
                .description("Request latency")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(registry);
        requestLatency.record(0.15);
        requestLatency.record(0.35);
        requestLatency.record(0.55);
        System.out.println("   request_duration_seconds: recorded samples");

        System.out.println("\n5. Timers:");
        Timer timer = Timer.builder("db_query_duration")
                .tag("database", "users")
                .description("Database query time")
                .publishPercentiles(0.5, 0.9, 0.99)
                .register(registry);
        timer.record(100, TimeUnit.MILLISECONDS);
        timer.record(250, TimeUnit.MILLISECONDS);
        System.out.println("   db_query_duration: recorded samples");

        System.out.println("\n6. Summary:");
        Summary summary = Summary.builder("response_size_bytes")
                .tag("endpoint", "/api/data")
                .description("Response size")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(registry);
        summary.record(1024);
        summary.record(2048);
        System.out.println("   response_size_bytes: recorded samples");

        System.out.println("\n7. Custom Tags:");
        Counter taggedCounter = Counter.builder("app_events")
                .tag("app", "myapp")
                .tag("environment", "production")
                .register(registry);
        taggedCounter.increment();
        System.out.println("   Tagged counter created with app=myapp, environment=production");

        System.out.println("\n8. Prometheus Output:");
        System.out.println(registry.scrape());

        System.out.println("\n=== Prometheus Lab Complete ===");
    }
}