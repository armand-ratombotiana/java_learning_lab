package testing;

/**
 * Testcontainers example demonstrating integration tests with real services.
 * 
 * Testcontainers allows you to spin up Docker containers for databases,
 * message brokers, etc. in your tests.
 * 
 * This self-contained demo simulates the pattern. In real projects:
 * 1. Add dependency: org.testcontainers:testcontainers:1.19+
 * 2. Add database driver (e.g., org.postgresql:postgresql)
 * 3. Use @Container annotation or try-with-resources
 * 
 * Dependencies (Maven):
 *   org.testcontainers:testcontainers:1.19.3
 *   org.testcontainers:postgresql:1.19.3
 *   org.postgresql:postgresql:42.7.1
 */
public class TestContainerExample {

    // Simulated database operations
    static class UserDao {
        private final String jdbcUrl;
        private final String username;
        private final String password;

        UserDao(String jdbcUrl, String username, String password) {
            this.jdbcUrl = jdbcUrl;
            this.username = username;
            this.password = password;
        }

        boolean canConnect() {
            // In real code, try DriverManager.getConnection()
            return jdbcUrl != null && !jdbcUrl.isEmpty();
        }

        int createUser(String name) {
            // In real code: INSERT INTO users (name) VALUES (?)
            System.out.println("Creating user: " + name + " on " + jdbcUrl);
            return 1; // returns generated ID
        }
    }

    public static void main(String[] args) {
        // Simulate what Testcontainers does:
        // PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");
        // postgres.start();
        // String jdbcUrl = postgres.getJdbcUrl();
        // String username = postgres.getUsername();
        // String password = postgres.getPassword();

        String jdbcUrl = "jdbc:postgresql://localhost:5432/test";
        String username = "test";
        String password = "test";

        UserDao dao = new UserDao(jdbcUrl, username, password);
        assert dao.canConnect() : "Should be able to connect (simulated)";

        int id = dao.createUser("TestUser");
        assert id > 0 : "User should be created with positive ID";

        // Simulated container lifecycle
        // postgres.stop();

        System.out.println("All TestContainerExample pattern tests passed.");
        System.out.println("Usage: @Container PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(\"postgres:16\");");
        System.out.println("        then use postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword()");
    }
}