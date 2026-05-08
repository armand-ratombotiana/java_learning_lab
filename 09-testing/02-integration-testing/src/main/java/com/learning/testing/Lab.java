package com.learning.testing;

public class Lab {

    public static void main(String[] args) {
        System.out.println("=== Integration Testing Concepts ===\n");

        demonstrateTestingPyramid();
        demonstrateIntegrationTestLayers();
        demonstrateTestSlices();
        demonstrateTestRestTemplate();
        demonstrateDatabaseTesting();
        demonstrateBestPractices();
    }

    private static void demonstrateTestingPyramid() {
        System.out.println("--- Testing Pyramid ---");
        System.out.println("  /\\         Unit Tests (fast, isolated, many)");
        System.out.println(" /  \\        Integration Tests (medium, Spring context)");
        System.out.println("/____\\       E2E Tests (slow, full system)");
        System.out.println();
        System.out.println("Integration tests validate component interactions:");
        System.out.println("  - Database queries (Repository layer)");
        System.out.println("  - HTTP endpoints (Controller layer)");
        System.out.println("  - Message routing (Messaging layer)");
        System.out.println("  - External API clients (Client layer)");
    }

    private static void demonstrateIntegrationTestLayers() {
        System.out.println("\n--- @SpringBootTest Layers ---");
        System.out.println("1. Full context: @SpringBootTest(webEnvironment = RANDOM_PORT)");
        System.out.println("   Starts embedded server, loads all beans");
        System.out.println();
        System.out.println("2. Web layer: @WebMvcTest(Controller.class)");
        System.out.println("   Loads only web layer (controllers, converters, filters)");
        System.out.println();
        System.out.println("3. Data layer: @DataJpaTest");
        System.out.println("   Loads JPA repositories, embedded DB, EntityManager");
        System.out.println();
        System.out.println("4. JSON layer: @JsonTest");
        System.out.println("   Tests JSON serialization/deserialization");
    }

    private static void demonstrateTestSlices() {
        System.out.println("\n--- Test Slices ---");
        System.out.println("@RestClientTest     -> REST client mocking");
        System.out.println("@JdbcTest          -> JDBC template testing");
        System.out.println("@DataMongoTest     -> MongoDB repository testing");
        System.out.println("@DataRedisTest     -> Redis template testing");
        System.out.println("@DataLdapTest      -> LDAP repository testing");
        System.out.println("@Neo4jTest         -> Neo4j repository testing");
        System.out.println();
        System.out.println("Use @AutoConfigureMockMvc with web slice for mock HTTP calls");
        System.out.println("Use @AutoConfigureTestDatabase with data slice for embedded DB");
    }

    private static void demonstrateTestRestTemplate() {
        System.out.println("\n--- TestRestTemplate / WebTestClient ---");
        System.out.println("@SpringBootTest(webEnvironment = RANDOM_PORT)");
        System.out.println("  TestRestTemplate template;        // REST template injected");
        System.out.println("  ResponseEntity<User> response =");
        System.out.println("    template.postForEntity(\"/api/users\", user, User.class);");
        System.out.println();
        System.out.println("WebTestClient for reactive apps (WebFlux):");
        System.out.println("  client.post().uri(\"/api/users\").body(user).exchange()");
        System.out.println("    .expectStatus().isCreated()");
        System.out.println("    .expectBody().jsonPath(\"$.name\").isEqualTo(\"John\");");
    }

    private static void demonstrateDatabaseTesting() {
        System.out.println("\n--- Database Testing ---");
        System.out.println("H2 in-memory DB (default for @DataJpaTest)");
        System.out.println("TestContainers: real PostgreSQL/MySQL in Docker");
        System.out.println();
        System.out.println("Fixture setup: @Sql(\"/test-data.sql\")");
        System.out.println("  or @BeforeEach + repository.save(...)");
        System.out.println();
        System.out.println("Rollback: @Transactional (rollback after each test)");
        System.out.println("  or @DirtiesContext (reload context)");
    }

    private static void demonstrateBestPractices() {
        System.out.println("\n--- Best Practices ---");
        System.out.println("1. Focus on behavior, not implementation");
        System.out.println("2. Use @TestConstructor for constructor injection");
        System.out.println("3. Avoid Spring context caching issues (unique contexts)");
        System.out.println("4. Use WireMock for external HTTP service simulation");
        System.out.println("5. Run integration tests separately: mvn verify -Pintegration");
        System.out.println("6. Keep tests independent (no shared mutable state)");
        System.out.println("7. Use assertAll() for grouped assertions");
    }
}
