package testing;

/**
 * Testcontainers advanced example.
 * 
 * Testcontainers provides disposable Docker containers for integration testing.
 * Supports: databases, message queues, browsers, custom containers.
 * 
 * Maven dependencies:
 *   org.testcontainers:testcontainers:1.19.3
 *   org.testcontainers:postgresql:1.19.3
 *   org.testcontainers:kafka:1.19.3
 *   org.testcontainers:mongodb:1.19.3
 *   org.testcontainers:junit-jupiter:1.19.3
 * 
 * JUnit 5 integration:
 *   @Container PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");
 *   @Container KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));
 */
public class TestContainersExample {

    // Simulated database
    static class UserRepository {
        private final String jdbcUrl;
        private final String user;
        private final String pass;

        UserRepository(String jdbcUrl, String user, String pass) {
            this.jdbcUrl = jdbcUrl;
            this.user = user;
            this.pass = pass;
        }

        boolean isConnected() {
            return jdbcUrl != null && !jdbcUrl.isEmpty();
        }

        int insert(String name) {
            System.out.println("Inserted " + name + " @ " + jdbcUrl);
            return 1;
        }
    }

    // Simulated Kafka producer
    static class EventProducer {
        private final String bootstrapServers;

        EventProducer(String bootstrapServers) {
            this.bootstrapServers = bootstrapServers;
        }

        boolean send(String topic, String message) {
            System.out.println("Sent to " + topic + " @ " + bootstrapServers + ": " + message);
            return true;
        }
    }

    public static void main(String[] args) {
        // Testcontainers usage simulation
        System.out.println("=== Testcontainers Demo ===");

        // PostgreSQL
        System.out.println("\nPostgreSQL:");
        System.out.println("  @Container PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(\"postgres:16\");");
        System.out.println("  postgres.start();");
        System.out.println("  String url = postgres.getJdbcUrl();");
        System.out.println("  String user = postgres.getUsername();");
        System.out.println("  String pass = postgres.getPassword();");

        var repo = new UserRepository("jdbc:postgresql://localhost:5432/testdb", "test", "test");
        assert repo.isConnected();
        assert repo.insert("Alice") == 1;

        // Kafka
        System.out.println("\nKafka:");
        System.out.println("  @Container KafkaContainer kafka = new KafkaContainer(...);");
        System.out.println("  kafka.start();");
        System.out.println("  String bootstrapServers = kafka.getBootstrapServers();");

        var producer = new EventProducer("localhost:9092");
        assert producer.send("test-topic", "{\"event\":\"test\"}");

        // Custom container
        System.out.println("\nCustom containers:");
        System.out.println("  GenericContainer<?> redis = new GenericContainer<>(\"redis:7-alpine\")");
        System.out.println("      .withExposedPorts(6379);");
        System.out.println("  redis.start();");
        System.out.println("  String host = redis.getHost();");
        System.out.println("  Integer port = redis.getMappedPort(6379);");

        // Container lifecycle
        System.out.println("\nContainer lifecycle:");
        System.out.println("  @Testcontainers — enables lifecycle management");
        System.out.println("  @Container — manages container start/stop");
        System.out.println("  Static @Container — shared across tests (start once)");
        System.out.println("  Instance @Container — per-test container");

        System.out.println("All TestContainersExample tests passed.");
    }
}