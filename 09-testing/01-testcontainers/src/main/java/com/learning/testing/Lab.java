package com.learning.testing;

public class Lab {

    public static void main(String[] args) {
        System.out.println("=== TestContainers Concepts ===\n");

        demonstrateCoreConcept();
        demonstrateDatabaseTesting();
        demonstrateModuleContainers();
        demonstrateCustomContainers();
        demonstrateLifecycle();
        demonstrateBestPractices();
    }

    private static void demonstrateCoreConcept() {
        System.out.println("--- Core Concept ---");
        System.out.println("TestContainers = Java library for throwaway Docker containers in tests");
        System.out.println("Spin up real dependencies (DB, message broker, services) during test");
        System.out.println("Containers are started before tests and torn down after");
        System.out.println();
        System.out.println("Flow: @BeforeAll -> startContainer() -> @Test -> @AfterAll -> stop()");
        System.out.println("Works with JUnit 4/5 via @Rule / @Container annotations");
    }

    private static void demonstrateDatabaseTesting() {
        System.out.println("\n--- Database Testing ---");
        System.out.println("PostgreSQLContainer:");
        System.out.println("  new PostgreSQLContainer(\"postgres:15\")");
        System.out.println("  .withDatabaseName(\"testdb\")");
        System.out.println("  .withUsername(\"test\")");
        System.out.println("  .withPassword(\"test\")");
        System.out.println();
        System.out.println("Other DB containers:");
        System.out.println("  MySQLContainer, MariaDBContainer, MSSQLServerContainer");
        System.out.println("  OracleContainer, CockroachDBContainer");
        System.out.println();
        System.out.println("JDBC URL with TestContainers:");
        System.out.println("  jdbc:tc:postgresql:15:///testdb (automatic container lifecycle)");
    }

    private static void demonstrateModuleContainers() {
        System.out.println("\n--- Module Containers ---");
        System.out.println("GenericContainer                    -> Any Docker image");
        System.out.println("KafkaContainer                      -> Kafka + ZooKeeper");
        System.out.println("RedpandaContainer (Kafka-compatible)");
        System.out.println("RabbitMQContainer                   -> RabbitMQ with AMQP");
        System.out.println("NATSContainer                       -> NATS server");
        System.out.println("MongoDBContainer                    -> MongoDB");
        System.out.println("RedisContainer                      -> Redis");
        System.out.println("MinIOContainer                      -> S3-compatible storage");
        System.out.println("LocalStackContainer                 -> AWS mock services");
        System.out.println("ToxiProxyContainer                  -> Network proxy for fault testing");
    }

    private static void demonstrateCustomContainers() {
        System.out.println("\n--- Custom Containers ---");
        System.out.println("GenericContainer<?> container = new GenericContainer<>(\"myapp:latest\")");
        System.out.println("  .withExposedPorts(8080)");
        System.out.println("  .withEnv(\"SPRING_PROFILE\", \"test\")");
        System.out.println("  .waitingFor(Wait.forHttp(\"/health\").forStatusCode(200))");
        System.out.println("  .withStartupTimeout(Duration.ofSeconds(60));");
        System.out.println();
        System.out.println("Access: String url = \"http://\" + container.getHost()");
        System.out.println("  + \":\" + container.getMappedPort(8080);");
    }

    private static void demonstrateLifecycle() {
        System.out.println("\n--- Lifecycle Management ---");
        System.out.println("@Container (JUnit 5 @Testcontainers) -> automatic start/stop");
        System.out.println("Manual: container.start() / container.stop()");
        System.out.println("Singleton containers: static @Container (shared across test class)");
        System.out.println();
        System.out.println("Reusable containers: container.withReuse(true)");
        System.out.println("  (keeps container running between test runs for speed)");
        System.out.println();
        System.out.println("Docker Compose support: DockerComposeContainer");
    }

    private static void demonstrateBestPractices() {
        System.out.println("\n--- Best Practices ---");
        System.out.println("1. Use @DynamicPropertySource to override Spring Boot config");
        System.out.println("2. Keep container images fixed (no 'latest')");
        System.out.println("3. Share containers across tests with static @Container");
        System.out.println("4. Use Ryuk (resource reaper) for container cleanup");
        System.out.println("5. Set image pull timeout for CI environments");
        System.out.println("6. Use Testcontainers Desktop for local development");
    }
}
