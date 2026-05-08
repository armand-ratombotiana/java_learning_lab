package com.learning.gatling;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class Lab {
    record HttpAction(String method, String path, String body) {}

    record ScenarioResult(String scenario, int status, long duration) {}

    static class Scenario {
        String name;
        List<HttpAction> actions = new ArrayList<>();
        Map<String, Object> sessionData = new ConcurrentHashMap<>();

        Scenario(String name) { this.name = name; }

        Scenario exec(String method, String path) {
            actions.add(new HttpAction(method, path, ""));
            return this;
        }

        Scenario exec(String method, String path, String body) {
            actions.add(new HttpAction(method, path, body));
            return this;
        }

        Scenario pause(Duration d) {
            try { Thread.sleep(d.toMillis()); } catch (InterruptedException e) {}
            return this;
        }

        void setSession(String key, Object value) { sessionData.put(key, value); }
        Object getSession(String key) { return sessionData.get(key); }
    }

    static class InjectionProfile {
        enum Type { AT_ONCE, RAMP_USERS, CONSTANT_RATE, RAMP_RATE }
        final Type type;
        final int users;
        final Duration duration;

        InjectionProfile(Type type, int users, Duration duration) {
            this.type = type; this.users = users; this.duration = duration;
        }

        int usersAtTime(Duration elapsed) {
            return switch (type) {
                case AT_ONCE -> elapsed.isZero() ? users : 0;
                case RAMP_USERS -> {
                    double progress = Math.min(1.0, elapsed.toMillis() / (double) duration.toMillis());
                    yield (int) (users * progress);
                }
                case CONSTANT_RATE -> (int) (elapsed.toSeconds() * users);
                case RAMP_RATE -> {
                    double progress = Math.min(1.0, elapsed.toMillis() / (double) duration.toMillis());
                    yield (int) (elapsed.toSeconds() * users * progress);
                }
            };
        }

        public String toString() {
            return type + "(" + users + " users over " + duration.toSeconds() + "s)";
        }
    }

    static class Simulation {
        String name;
        Map<Scenario, InjectionProfile> scenarios = new LinkedHashMap<>();
        List<ScenarioResult> results = new CopyOnWriteArrayList<>();
        Duration maxDuration = Duration.ofSeconds(60);

        Simulation(String name) { this.name = name; }

        Simulation withProfile(Scenario s, InjectionProfile p) {
            scenarios.put(s, p); return this;
        }

        Simulation withMaxDuration(Duration d) {
            this.maxDuration = d; return this;
        }

        void run() {
            var startTime = System.currentTimeMillis();
            var endTime = startTime + maxDuration.toMillis();
            var executor = Executors.newCachedThreadPool();

            for (var entry : scenarios.entrySet()) {
                var scenario = entry.getKey();
                var profile = entry.getValue();
                System.out.println("   Running: " + scenario.name + " with " + profile);

                executor.submit(() -> {
                    long simStart = System.currentTimeMillis();
                    int injected = 0;
                    while (System.currentTimeMillis() < endTime) {
                        long elapsed = System.currentTimeMillis() - simStart;
                        int targetUsers = profile.usersAtTime(Duration.ofMillis(elapsed));
                        while (injected < targetUsers) {
                            final int userNum = ++injected;
                            executor.submit(() -> {
                                for (var action : scenario.actions) {
                                    long actionStart = System.currentTimeMillis();
                                    try { Thread.sleep(ThreadLocalRandom.current().nextInt(10, 100)); } catch (InterruptedException e) {}
                                    results.add(new ScenarioResult(scenario.name, 200,
                                        System.currentTimeMillis() - actionStart));
                                }
                            });
                        }
                        try { Thread.sleep(100); } catch (InterruptedException e) { break; }
                    }
                });
            }

            executor.shutdown();
            try { executor.awaitTermination(maxDuration.toMillis() + 5000, TimeUnit.MILLISECONDS); } catch (InterruptedException e) {}
        }

        void printReport() {
            var byScenario = results.stream().collect(Collectors.groupingBy(ScenarioResult::scenario));
            for (var e : byScenario.entrySet()) {
                var list = e.getValue();
                var stats = list.stream().mapToLong(ScenarioResult::duration).summaryStatistics();
                var byStatus = list.stream().collect(Collectors.groupingBy(ScenarioResult::status, Collectors.counting()));
                System.out.println("   " + e.getKey() + ":");
                System.out.println("     Requests: " + list.size());
                System.out.println("     Avg: " + String.format("%.0f", stats.getAverage()) + "ms");
                System.out.println("     p95: " + String.format("%.0f", percentile(list, 95)) + "ms");
                System.out.println("     p99: " + String.format("%.0f", percentile(list, 99)) + "ms");
                byStatus.forEach((s, c) -> System.out.println("     Status " + s + ": " + c));
            }
        }

        static double percentile(List<ScenarioResult> list, int pct) {
            var sorted = list.stream().mapToLong(ScenarioResult::duration).sorted().toArray();
            int idx = (int) Math.ceil(pct / 100.0 * sorted.length) - 1;
            return sorted[Math.max(0, Math.min(idx, sorted.length - 1))];
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Gatling Concepts Lab ===\n");

        Scenario browseProducts = new Scenario("Browse Products");
        browseProducts.exec("GET", "/api/products")
            .pause(Duration.ofMillis(200))
            .exec("GET", "/api/products/1")
            .pause(Duration.ofMillis(100));

        Scenario placeOrder = new Scenario("Place Order");
        placeOrder.exec("GET", "/api/products")
            .pause(Duration.ofMillis(100))
            .exec("POST", "/api/orders", "{\"productId\":1,\"qty\":1}")
            .pause(Duration.ofMillis(300));

        Scenario userLogin = new Scenario("User Login");
        userLogin.exec("POST", "/api/auth/login", "{\"user\":\"test\",\"pass\":\"pass\"}")
            .pause(Duration.ofMillis(50));

        System.out.println("1. Scenarios defined:");
        System.out.println("   - " + browseProducts.name + " (" + browseProducts.actions.size() + " requests)");
        System.out.println("   - " + placeOrder.name + " (" + placeOrder.actions.size() + " requests)");
        System.out.println("   - " + userLogin.name + " (" + userLogin.actions.size() + " requests)");

        System.out.println("\n2. Injection profiles:");
        System.out.println("   " + new InjectionProfile(InjectionProfile.Type.AT_ONCE, 10, Duration.ZERO));
        System.out.println("   " + new InjectionProfile(InjectionProfile.Type.RAMP_USERS, 50, Duration.ofSeconds(10)));
        System.out.println("   " + new InjectionProfile(InjectionProfile.Type.CONSTANT_RATE, 5, Duration.ofSeconds(30)));
        System.out.println("   " + new InjectionProfile(InjectionProfile.Type.RAMP_RATE, 20, Duration.ofSeconds(20)));

        Simulation sim = new Simulation("Order Service Load Test")
            .withProfile(browseProducts, new InjectionProfile(InjectionProfile.Type.RAMP_USERS, 20, Duration.ofSeconds(5)))
            .withProfile(placeOrder, new InjectionProfile(InjectionProfile.Type.CONSTANT_RATE, 2, Duration.ofSeconds(8)))
            .withProfile(userLogin, new InjectionProfile(InjectionProfile.Type.AT_ONCE, 5, Duration.ZERO))
            .withMaxDuration(Duration.ofSeconds(15));

        System.out.println("\n3. Running simulation: " + sim.name);
        sim.run();

        System.out.println("\n4. Results:");
        sim.printReport();

        System.out.println("\n5. Gatling concepts:");
        System.out.println("   - Scenario: business flow (user journey)");
        System.out.println("   - Feeders: CSV, JSON data injection per virtual user");
        System.out.println("   - Checks: HTTP status validation, JSON path extraction");
        System.out.println("   - Session: per-user state (feeder values, extracted data)");
        System.out.println("   - Assertions: global pass/fail on response time, success rate");

        System.out.println("\n6. HTML Reports:");
        System.out.println("   - Simulations report generated in target/gatling/");
        System.out.println("   - Active requests chart, response time distribution");
        System.out.println("   - Percentiles: 50th, 75th, 95th, 99th");

        System.out.println("\n=== Lab Complete ===");
    }
}
