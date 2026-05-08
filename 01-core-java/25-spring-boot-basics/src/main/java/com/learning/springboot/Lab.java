package com.learning.springboot;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public class Lab {

    public static void main(String[] args) {
        System.out.println("=== Spring Boot Basics Lab (Conceptual) ===\n");

        dependencyInjection();
        autoConfiguration();
        embeddedServer();
        startersAndProperties();
        actuatorEndpoints();
    }

    static void dependencyInjection() {
        System.out.println("--- Dependency Injection (Manual) ---");

        @FunctionalInterface
        interface UserRepository {
            Optional<String> findById(long id);
        }

        class JdbcUserRepository implements UserRepository {
            public Optional<String> findById(long id) {
                return id == 1 ? Optional.of("Alice") : Optional.empty();
            }
        }

        class UserService {
            private final UserRepository repo;

            UserService(UserRepository repo) { this.repo = repo; }

            String getUserName(long id) {
                return repo.findById(id).orElse("Unknown");
            }
        }

        var container = new HashMap<Class<?>, Object>();
        container.put(UserRepository.class, new JdbcUserRepository());
        container.put(UserService.class, new UserService((UserRepository) container.get(UserRepository.class)));

        var service = (UserService) container.get(UserService.class);
        System.out.println("  User 1: " + service.getUserName(1));
        System.out.println("  User 2: " + service.getUserName(2));

        System.out.println("\n  Spring DI annotations (conceptual):");
        String[][] ann = {
            {"@Component", "Generic Spring bean"},
            {"@Service", "Service layer stereotype"},
            {"@Repository", "Data access stereotype"},
            {"@Controller", "Web controller stereotype"},
            {"@Autowired", "Injection point marker"},
            {"@Qualifier", "Specific bean qualifier"}
        };
        for (String[] a : ann) System.out.printf("  %-15s %s%n", a[0], a[1]);
    }

    static void autoConfiguration() {
        System.out.println("\n--- Auto-Configuration Concept ---");

        class ConditionEvaluator {
            boolean classExists(String className) {
                try {
                    Class.forName(className);
                    return true;
                } catch (ClassNotFoundException e) {
                    return false;
                }
            }
        }

        class AutoConfigure {
            final Map<String, Supplier<Object>> beans = new LinkedHashMap<>();
            final ConditionEvaluator conditions = new ConditionEvaluator();

            void conditionalBean(String name, Supplier<Object> supplier, String conditionClass) {
                if (conditions.classExists(conditionClass)) {
                    beans.put(name, supplier);
                    System.out.printf("  @ConditionalOnClass(%s) -> Bean '%s' created%n",
                        conditionClass.substring(conditionClass.lastIndexOf('.') + 1), name);
                } else {
                    System.out.printf("  @ConditionalOnClass(%s) -> Bean '%s' skipped%n",
                        conditionClass.substring(conditionClass.lastIndexOf('.') + 1), name);
                }
            }

            Map<String, Object> getBeans() {
                var result = new LinkedHashMap<String, Object>();
                beans.forEach((k, v) -> result.put(k, v.get()));
                return result;
            }
        }

        var auto = new AutoConfigure();
        auto.conditionalBean("dataSource", () -> "H2 DataSource", "org.h2.Driver");
        auto.conditionalBean("mongoClient", () -> "MongoClient", "com.mongodb.client.MongoClient");
        auto.conditionalBean("redisTemplate", () -> "RedisTemplate", "org.springframework.data.redis.core.RedisTemplate");

        System.out.println("\n  Created beans:");
        auto.getBeans().forEach((name, bean) -> System.out.printf("    %s = %s%n", name, bean));

        System.out.println("\n  @SpringBootApplication combines:");
        String[] combos = {
            "@Configuration - marks class as config source",
            "@EnableAutoConfiguration - auto-configures beans",
            "@ComponentScan - scans package for components"
        };
        for (String c : combos) System.out.println("  " + c);
    }

    static void embeddedServer() {
        System.out.println("\n--- Embedded Server Concept ---");

        record ServerConfig(int port, String contextPath, boolean enableCompression) {}

        record ServerStatus(String server, int port, String state, long uptimeMs) {}

        class EmbeddedServerSimulator {
            ServerConfig config;
            volatile boolean running = false;
            long startTime;

            void start(ServerConfig cfg) {
                config = cfg;
                running = true;
                startTime = System.currentTimeMillis();
                System.out.printf("  Starting embedded server on port %d (context: %s)%n",
                    cfg.port(), cfg.contextPath());
                System.out.println("  Tomcat / Jetty / Undertow initialized");
                System.out.printf("  Server started in %dms%n",
                    ThreadLocalRandom.current().nextLong(1000, 3000));
            }

            ServerStatus status() {
                return new ServerStatus("Apache Tomcat", config.port(),
                    running ? "RUNNING" : "STOPPED", System.currentTimeMillis() - startTime);
            }

            void stop() {
                running = false;
                System.out.println("  Graceful shutdown complete");
            }
        }

        var server = new EmbeddedServerSimulator();
        server.start(new ServerConfig(8080, "/app", true));
        System.out.println("\n  " + server.status());
        server.stop();
    }

    static void startersAndProperties() {
        System.out.println("\n--- Starters & Configuration ---");

        record Starter(String name, String description) {}
        List<Starter> starters = List.of(
            new Starter("spring-boot-starter-web", "RESTful web apps (Tomcat + MVC)"),
            new Starter("spring-boot-starter-data-jpa", "JPA + Hibernate + H2"),
            new Starter("spring-boot-starter-security", "Authentication & authorization"),
            new Starter("spring-boot-starter-actuator", "Production monitoring endpoints"),
            new Starter("spring-boot-starter-test", "JUnit + Mockito + Testcontainers"),
            new Starter("spring-boot-starter-validation", "Bean Validation (Hibernate Validator)")
        );

        System.out.printf("%-40s %s%n", "Starter", "Description");
        System.out.println("-".repeat(80));
        starters.forEach(s -> System.out.printf("%-40s %s%n", s.name(), s.description()));

        System.out.println("\n  application.yml (hierarchical config):");
        String yaml = """
            server:
              port: 8080
              servlet:
                context-path: /api

            spring:
              datasource:
                url: jdbc:h2:mem:testdb
                username: sa
              jpa:
                hibernate:
                  ddl-auto: create-drop
                show-sql: true

            logging:
              level:
                root: INFO
                com.learning: DEBUG
            """;
        System.out.println(yaml);

        System.out.println("  Property binding (conceptual):");
        String[][] props = {
            {"server.port", "int", "HTTP port"},
            {"spring.datasource.url", "String", "JDBC URL"},
            {"logging.level.root", "Level", "Root log level"},
            {"management.endpoints.web.exposure.include", "String[]", "Actuator endpoints"}
        };
        System.out.printf("  %-40s %-10s %s%n", "Property", "Type", "Description");
        System.out.println("  " + "-".repeat(70));
        for (String[] p : props) System.out.printf("  %-40s %-10s %s%n", p[0], p[1], p[2]);
    }

    static void actuatorEndpoints() {
        System.out.println("\n--- Actuator Endpoints ---");

        record Endpoint(String path, String description, boolean sensitive) {}

        List<Endpoint> endpoints = List.of(
            new Endpoint("/actuator/health", "Application health", false),
            new Endpoint("/actuator/info", "Application info", false),
            new Endpoint("/actuator/metrics", "JVM and system metrics", false),
            new Endpoint("/actuator/env", "Environment properties", true),
            new Endpoint("/actuator/beans", "All Spring beans", true),
            new Endpoint("/actuator/threaddump", "Thread dump", true),
            new Endpoint("/actuator/heapdump", "Heap dump (download)", true),
            new Endpoint("/actuator/loggers", "View/change log levels", true)
        );

        System.out.printf("%-35s %-40s %s%n", "Endpoint", "Description", "Exposed by default");
        System.out.println("-".repeat(85));
        endpoints.forEach(e -> System.out.printf("%-35s %-40s %s%n",
            e.path(), e.description(), e.sensitive() ? "No" : "Yes"));

        record HealthStatus(String status, Map<String, Object> details) {}
        var health = new HealthStatus("UP", Map.of(
            "diskSpace", Map.of("status", "UP", "total", 499_963_170_816L, "free", 200_123_456_789L),
            "db", Map.of("status", "UP", "database", "H2", "validationQuery", "SELECT 1"),
            "ping", Map.of("status", "UP")
        ));
        System.out.println("\n  Health check response:");
        System.out.printf("  {\"status\":\"%s\", \"components\":%s}%n",
            health.status(), health.details());
    }
}
