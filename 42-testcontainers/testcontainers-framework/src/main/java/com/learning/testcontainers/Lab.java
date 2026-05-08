package com.learning.testcontainers;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;

public class Lab {
    record ContainerImage(String image, String tag, int exposedPort) {}

    static class GenericContainer {
        final ContainerImage image;
        boolean running;
        String jdbcUrl;
        String host;
        int mappedPort;
        Duration startupTimeout;
        Map<String, String> envVars = new LinkedHashMap<>();

        GenericContainer(ContainerImage image) {
            this.image = image;
        }

        GenericContainer withEnv(String key, String value) {
            envVars.put(key, value);
            return this;
        }

        GenericContainer withStartupTimeout(Duration timeout) {
            this.startupTimeout = timeout;
            return this;
        }

        void start() {
            running = true;
            host = "localhost";
            mappedPort = 10000 + new Random().nextInt(50000);
            jdbcUrl = "jdbc:" + image.image().split(":")[0] + "://" + host + ":" + mappedPort + "/testdb";
            System.out.println("   Started " + image.image() + ":" + image.tag() + " on port " + mappedPort);
        }

        void stop() {
            running = false;
            System.out.println("   Stopped " + image.image() + ":" + image.tag());
        }

        boolean isRunning() { return running; }
        String getJdbcUrl() { return jdbcUrl; }
        int getMappedPort() { return mappedPort; }
    }

    static class Network {
        String name;
        Set<String> containers = new HashSet<>();

        Network(String name) { this.name = name; }

        void connect(String containerAlias) { containers.add(containerAlias); }

        boolean isConnected(String alias) { return containers.contains(alias); }
    }

    static class WaitStrategy {
        enum Strategy { LOG, HTTP, HEALTH, PORT }
        final Strategy strategy;
        final String pattern;

        WaitStrategy(Strategy strategy, String pattern) {
            this.strategy = strategy;
            this.pattern = pattern;
        }

        boolean waitForContainer(GenericContainer container) {
            System.out.println("   Waiting for " + strategy + " \"" + pattern + "\" on " + container.image.image());
            return true;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== TestContainers Concepts Lab ===\n");

        System.out.println("1. PostgreSQL Container:");
        var postgres = new GenericContainer(new ContainerImage("postgres", "15-alpine", 5432));
        postgres.withEnv("POSTGRES_DB", "testdb")
                .withEnv("POSTGRES_USER", "test")
                .withEnv("POSTGRES_PASSWORD", "test")
                .withStartupTimeout(Duration.ofSeconds(60));
        postgres.start();
        System.out.println("   JDBC URL: " + postgres.getJdbcUrl());
        System.out.println("   Running: " + postgres.isRunning());

        System.out.println("\n2. Redis Container:");
        var redis = new GenericContainer(new ContainerImage("redis", "7-alpine", 6379));
        redis.start();
        System.out.println("   Redis on port: " + redis.getMappedPort());
        System.out.println("   Connection: redis://" + redis.host + ":" + redis.getMappedPort());

        System.out.println("\n3. Multi-Container Network:");
        var network = new Network("test-network");
        network.connect("postgres");
        network.connect("redis");
        network.connect("app");
        System.out.println("   Network '" + network.name + "' has " + network.containers.size() + " connected containers");
        System.out.println("   postgres connected: " + network.isConnected("postgres"));
        System.out.println("   app connected: " + network.isConnected("app"));

        System.out.println("\n4. Wait Strategies:");
        var logStrategy = new WaitStrategy(WaitStrategy.Strategy.LOG,
            "database system is ready to accept connections");
        var httpStrategy = new WaitStrategy(WaitStrategy.Strategy.HTTP, "200 OK");
        var portStrategy = new WaitStrategy(WaitStrategy.Strategy.PORT, "5432/tcp");

        logStrategy.waitForContainer(postgres);
        httpStrategy.waitForContainer(postgres);
        portStrategy.waitForContainer(postgres);

        System.out.println("\n5. Container Cleanup:");
        System.out.println("   @Container annotation: lifecycle managed by JUnit");
        System.out.println("   Ryuk container auto-cleans after tests");
        System.out.println("   containers.stop() for manual cleanup");

        System.out.println("\n6. TestContainers JDBC Support:");
        System.out.println("   jdbc:tc:postgresql:15:///testdb - auto-start postgres");
        System.out.println("   jdbc:tc:mysql:8:///testdb - auto-start MySQL");
        System.out.println("   Automatic container lifecycle with JDBC URL scheme");

        System.out.println("\n7. Module-specific Containers:");
        System.out.println("   - PostgreSQLContainer: withDatabaseName(), withUsername()");
        System.out.println("   - MySQLContainer, MSSQLServerContainer, OracleContainer");
        System.out.println("   - KafkaContainer, ToxiproxyContainer (network chaos)");
        System.out.println("   - LocalStackContainer (AWS mocking)");

        System.out.println("\n8. Database Migration Testing Flow:");
        System.out.println("   1. Start postgres container");
        System.out.println("   2. Run Flyway/Liquibase migrations on container");
        System.out.println("   3. Insert test data");
        System.out.println("   4. Run application tests against real DB");
        System.out.println("   5. Container auto-destroyed after tests");

        postgres.stop();
        redis.stop();

        System.out.println("\n=== Lab Complete ===");
    }
}
