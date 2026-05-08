package com.learning.springadvanced;

import java.lang.annotation.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;
import java.util.function.*;
import java.util.stream.*;

public class Lab {

    public static void main(String[] args) {
        System.out.println("=== Spring Boot Advanced Lab (Conceptual) ===\n");

        conditionalConfiguration();
        eventsAndListeners();
        cachingStrategies();
        asyncProcessing();
        scheduling();
        profilesAndExternalConfig();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface ConditionalOnProperty { String value(); String havingValue() default "true"; }

    static void conditionalConfiguration() {
        System.out.println("--- Conditional Configuration ---");

        var props = new Properties();
        props.setProperty("app.cache.enabled", "true");
        props.setProperty("app.metrics.enabled", "false");

        record Config(String name, String prop, boolean enabled) {}
        List.of(
            new Config("CacheConfig", "app.cache.enabled", true),
            new Config("MetricsConfig", "app.metrics.enabled", false)
        ).forEach(c -> System.out.printf("  @ConditionalOnProperty(%s) -> %s: %s%n",
            c.prop(), c.name(), c.enabled() == Boolean.parseBoolean(props.getProperty(c.prop())) ? "Loaded" : "Skipped"));

        System.out.println("\n  @Conditional variants:");
        for (var c : List.of("OnClass", "OnMissingClass", "OnBean", "OnMissingBean", "OnProperty", "OnExpression"))
            System.out.println("  @Conditional" + c);
    }

    static void eventsAndListeners() {
        System.out.println("\n--- Event-Driven Design ---");

        record AppEvent(String type, String data, long timestamp) {}
        @FunctionalInterface
        interface EventListener { void onEvent(AppEvent event); }

        class EventPublisher {
            final Map<String, List<EventListener>> listeners = new ConcurrentHashMap<>();
            void register(String type, EventListener l) {
                listeners.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>()).add(l);
            }
            void publish(AppEvent e) {
                System.out.println("  Published: " + e.type());
                var h = listeners.get(e.type());
                if (h != null) h.forEach(l -> l.onEvent(e));
            }
        }

        var ep = new EventPublisher();
        ep.register("AppStarted", e -> System.out.println("    Loading config..."));
        ep.register("AppReady", e -> System.out.println("    Ready on port 8080"));
        ep.publish(new AppEvent("AppStarted", "boot", System.currentTimeMillis()));

        System.out.println("\n  Lifecycle events order:");
        for (var e : List.of("ApplicationStarting", "EnvironmentPrepared", "ContextRefreshed", "ApplicationReady", "ApplicationFailed"))
            System.out.println("  " + e);
    }

    static void cachingStrategies() {
        System.out.println("\n--- Caching Strategies ---");
        class CacheManager {
            final ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<>();
            final ConcurrentHashMap<String, Long> expiry = new ConcurrentHashMap<>();

            @SuppressWarnings("unchecked")
            <T> T getOrCompute(String key, Supplier<T> loader, long ttlMs) {
                if (cache.containsKey(key) && expiry.get(key) > System.currentTimeMillis()) {
                    System.out.println("  HIT: " + key);
                    return (T) cache.get(key);
                }
                System.out.println("  MISS: " + key);
                T val = loader.get();
                cache.put(key, val);
                expiry.put(key, System.currentTimeMillis() + ttlMs);
                return val;
            }
        }

        var cache = new CacheManager();
        var calls = new AtomicInteger(0);
        for (int i = 0; i < 4; i++)
            cache.getOrCompute("user:42", () -> "result-" + calls.incrementAndGet(), 5000);
        System.out.println("  DB calls: " + calls.get() + " (expected 1)");

        System.out.println("\n  @Cacheable annotations:");
        for (var a : List.of("@Cacheable", "@CacheEvict", "@CachePut", "@Caching", "@CacheConfig"))
            System.out.println("  " + a);
    }

    static void asyncProcessing() {
        System.out.println("\n--- Async Processing ---");
        var executor = Executors.newFixedThreadPool(4);

        var f1 = CompletableFuture.supplyAsync(() -> {
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(50));
            return "Task A";
        }, executor);
        var f2 = CompletableFuture.supplyAsync(() -> {
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(80));
            return "Task B";
        }, executor);

        f1.thenCombine(f2, (a, b) -> a + " + " + b)
            .thenAccept(r -> System.out.println("  Combined: " + r))
            .join();
        executor.shutdown();

        System.out.println("\n  @Async configuration:");
        System.out.println("""
            @EnableAsync
            @Bean
            public Executor taskExecutor() {
                var pool = new ThreadPoolTaskExecutor();
                pool.setCorePoolSize(4);
                pool.setMaxPoolSize(10);
                pool.setQueueCapacity(100);
                return pool;
            }""");
    }

    static void scheduling() {
        System.out.println("\n--- Scheduling ---");
        var scheduler = Executors.newScheduledThreadPool(2);
        var count = new AtomicInteger(0);

        scheduler.scheduleAtFixedRate(() -> {
            int n = count.incrementAndGet();
            System.out.println("  [tick] #" + n);
        }, 0, 100, TimeUnit.MILLISECONDS);

        scheduler.schedule(() -> {
            scheduler.shutdown();
            System.out.println("  Scheduler stopped");
        }, 300, TimeUnit.MILLISECONDS);

        try { scheduler.awaitTermination(500, TimeUnit.MILLISECONDS); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        System.out.println("\n  @Scheduled variants:");
        for (var s : List.of("@Scheduled(fixedRate=5000)", "@Scheduled(fixedDelay=5000)",
                "@Scheduled(cron='0 0 * * * *')", "@Scheduled(initialDelay=10000, fixedRate=5000)"))
            System.out.println("  " + s);
    }

    static void profilesAndExternalConfig() {
        System.out.println("\n--- Profiles & External Configuration ---");

        var dev = new Properties(); dev.setProperty("server.port", "8080");
        dev.setProperty("spring.datasource.url", "jdbc:h2:mem:devdb");
        dev.setProperty("logging.level.root", "DEBUG");

        var prod = new Properties(); prod.setProperty("server.port", "8080");
        prod.setProperty("spring.datasource.url", "jdbc:postgresql://prod-db:5432/app");
        prod.setProperty("logging.level.root", "WARN");

        var profiles = Map.of("dev", dev, "prod", prod);
        System.out.println("  Active: dev");
        profiles.get("dev").forEach((k, v) ->
            System.out.println("    " + k + " = " + (k.toString().contains("url") ? "***" : v)));

        System.out.println("\n  Property source order (highest first):");
        for (var s : List.of("@TestPropertySource", "Command line args", "OS env vars",
                "application-{profile}.properties", "application.properties"))
            System.out.println("  " + s);

        System.out.println("\n  Multi-document YAML profiles:");
        System.out.println("""
            ---
            spring.config.activate.on-profile: dev
            logging.level.root: DEBUG
            ---
            spring.config.activate.on-profile: prod
            logging.level.root: WARN""");
    }
}
