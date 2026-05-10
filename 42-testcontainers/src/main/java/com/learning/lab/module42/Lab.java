package com.learning.lab.module42;

import org.testcontainers.containers.*;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.ImagePullPolicy;
import org.testcontainers.junit.jupiter.*;
import org.testcontainers.utility.DockerImageName;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 42: Testcontainers Lab ===\n");

        System.out.println("1. Container Testing:");
        System.out.println("   - Start: tc.start()");
        System.out.println("   - Stop: tc.stop()");
        System.out.println("   - Logs: tc.getLogs()");

        System.out.println("\n2. PostgreSQL Container:");
        postgresqlContainerDemo();

        System.out.println("\n3. MySQL Container:");
        mysqlContainerDemo();

        System.out.println("\n4. Redis Container:");
        redisContainerDemo();

        System.out.println("\n5. Kafka Container:");
        kafkaContainerDemo();

        System.out.println("\n6. Custom Container:");
        customContainerDemo();

        System.out.println("\n7. Compose Container:");
        composeContainerDemo();

        System.out.println("\n=== Testcontainers Lab Complete ===");
    }

    static void postgresqlContainerDemo() {
        System.out.println("   PostgreSQL Configuration:");
        System.out.println("   - Image: postgres:15-alpine");
        System.out.println("   - Database: testdb");
        System.out.println("   - Username: testuser");
        System.out.println("   - Password: testpass");
        System.out.println("   - Port: 5432");

        GenericContainer<?> postgres = new GenericContainer<>("postgres:15-alpine")
                .withEnv("POSTGRES_DB", "testdb")
                .withEnv("POSTGRES_USER", "testuser")
                .withEnv("POSTGRES_PASSWORD", "testpass")
                .withExposedPorts(5432);

        postgres.start();
        String jdbcUrl = postgres.getJdbcUrl();
        System.out.println("   JDBC URL: " + jdbcUrl);
        postgres.stop();
    }

    static void mysqlContainerDemo() {
        System.out.println("   MySQL Configuration:");
        System.out.println("   - Image: mysql:8");
        System.out.println("   - Database: testdb");
        System.out.println("   - Username: root");
        System.out.println("   - Password: rootpass");
        System.out.println("   - Port: 3306");
    }

    static void redisContainerDemo() {
        System.out.println("   Redis Configuration:");
        System.out.println("   - Image: redis:7-alpine");
        System.out.println("   - Exposed Port: 6379");
        System.out.println("   - Command: redis-server --appendonly yes");
    }

    static void kafkaContainerDemo() {
        System.out.println("   Kafka Configuration:");
        System.out.println("   - Image: confluentinc/cp-kafka:7.4.0");
        System.out.println("   - ZooKeeper: confluentinc/cp-zookeeper:7.4.0");
        System.out.println("   - Ports: 9092 (Kafka), 2181 (ZooKeeper)");
        System.out.println("   - Broker ID: 1");
    }

    static void customContainerDemo() {
        System.out.println("   Custom Container:");
        System.out.println("   - GenericContainer with Docker image");
        System.out.println("   - withCommand() - Custom startup command");
        System.out.println("   - withEnv() - Environment variables");
        System.out.println("   - withFileSystemBind() - Mount files");
        System.out.println("   - waitingFor() - Wait strategies");
    }

    static void composeContainerDemo() {
        System.out.println("   Docker Compose:");
        System.out.println("   - DockerComposeContainer");
        System.out.println("   - withComposeFile() - docker-compose.yml");
        System.out.println("   - withServices() - Service selection");
        System.out.println("   - waitingFor() - Service wait strategies");
    }

    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @Container
    static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8")
            .withDatabaseName("testdb")
            .withUsername("root")
            .withPassword("rootpass");

    @Container
    static RedisContainer redisContainer = new RedisContainer("redis:7-alpine");
}