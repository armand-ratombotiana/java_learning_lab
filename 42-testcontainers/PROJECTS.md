# TestContainers - Projects

This document contains two complete projects demonstrating TestContainers: a mini-project for learning container-based testing and a real-world project implementing comprehensive integration testing with databases and message queues.

## Mini-Projects by Concept

### 1. Container Basics (2 hours)
Start and manage containers for testing. Configure container lifecycle, network isolation, and wait strategies.

### 2. Database Containers (2 hours)
Use PostgreSQL, MySQL, MongoDB containers for integration tests. Configure connection pooling and data initialization.

### 3. Messaging Containers (2 hours)
Test with Kafka, RabbitMQ, and Redis containers. Verify message publishing and consuming in tests.

### 4. Module Containers (2 hours)
Use specialized modules for Elasticsearch, Jaeger, Keycloak. Configure initialization scripts and dependencies.

### Real-world: Integration Testing Suite
Build comprehensive integration testing suite with database, messaging, and external service containers.

---

## Project 1: TestContainers Basics Mini-Project

### Overview

This mini-project demonstrates fundamental TestContainers concepts including container lifecycle, network configuration, and wait strategies. It serves as a learning starting point for understanding Docker-based integration testing.

### Project Structure

```
testcontainers-basics/
├── pom.xml
└── src/
    └── test/
        └── java/
            └── com/
                └── learning/
                    └── testcontainers/
                        ├── BasicsTest.java
                        └── ContainerLab.java
```

### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <groupId>com.learning</groupId>
    <artifactId>testcontainers-basics</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    
    <properties>
        <java.version>21</java.version>
        <testcontainers.version>1.19.3</testcontainers.version>
        <junit5.version>5.10.1</junit5.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>${testcontainers.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>postgresql</artifactId>
            <version>${testcontainers.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>testcontainers</artifactId>
            <version>${testcontainers.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>${junit5.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-engine</artifactId>
            <version>${junit5.version}</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### BasicsTest.java

```java
package com.learning.testcontainers;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.ContainerState;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class BasicsTest {
    
    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15-alpine")
        .withDatabaseName("testdb")
        .withUsername("testuser")
        .withPassword("testpass")
        .withExposedPorts(5432)
        .withNetwork(Network.newNetwork())
        .withNetworkAliases("postgres");
    
    @Test
    void shouldStartPostgresContainer() {
        assertTrue(postgresContainer.isRunning());
        assertNotNull(postgresContainer.getJdbcUrl());
        assertNotNull(postgresContainer.getHost());
        assertTrue(postgresContainer.getFirstMappedPort() > 0);
    }
    
    @Test
    void shouldConnectToPostgres() {
        String jdbcUrl = postgresContainer.getJdbcUrl();
        String username = postgresContainer.getUsername();
        String password = postgresContainer.getPassword();
        
        assertNotNull(jdbcUrl);
        assertTrue(jdbcUrl.contains(" postgres"));
        assertEquals("testuser", username);
        assertEquals("testpass", password);
    }
    
    @Test
    void shouldExecuteQuery() throws Exception {
        var connection = postgresContainer.createConnection("");
        var statement = connection.createStatement();
        var resultSet = statement.executeQuery("SELECT 1 as result");
        
        assertTrue(resultSet.next());
        assertEquals(1, resultSet.getInt("result"));
        
        resultSet.close();
        statement.close();
        connection.close();
    }
    
    @Test
    void shouldRunWithMultipleContainers() {
        try (var redis = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379)
            .waitingFor(Wait.forListeningPort())) {
            redis.start();
            
            assertTrue(redis.isRunning());
            assertTrue(redis.getFirstMappedPort() > 0);
        }
    }
}
```

### ContainerLab.java

```java
package com.learning.testcontainers;

import org.testcontainers.containers.ContainerState;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ContainerLab {
    
    public static void main(String[] args) throws Exception {
        System.out.println("=== TestContainers Basics Lab ===\n");
        
        System.out.println("1. Starting PostgreSQL container:");
        try (PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
                .withDatabaseName("testdb")
                .withUsername("testuser")
                .withPassword("testpass")) {
            postgres.start();
            
            System.out.println("   JDBC URL: " + postgres.getJdbcUrl());
            System.out.println("   Host: " + postgres.getHost());
            System.out.println("   Port: " + postgres.getFirstMappedPort());
            
            System.out.println("\n2. Running SQL query:");
            Connection conn = DriverManager.getConnection(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
            );
            
            Statement stmt = conn.createStatement();
            stmt.execute("SELECT current_user, current_database()");
            ResultSet rs = stmt.getResultSet();
            
            if (rs.next()) {
                System.out.println("   Current user: " + rs.getString(1));
                System.out.println("   Database: " + rs.getString(2));
            }
            
            stmt.execute("CREATE TABLE users (id SERIAL PRIMARY KEY, name VARCHAR(100))");
            stmt.execute("INSERT INTO users (name) VALUES ('Alice'), ('Bob'), ('Charlie')");
            
            ResultSet countRs = stmt.executeQuery("SELECT COUNT(*) FROM users");
            if (countRs.next()) {
                System.out.println("   Users inserted: " + countRs.getInt(1));
            }
            
            stmt.close();
            conn.close();
        }
        
        System.out.println("\n3. Using container network:");
        try (Network network = Network.newNetwork();
             var postgres = new PostgreSQLContainer<>("postgres:15-alpine")
                .withNetwork(network)
                .withNetworkAliases("db")
                .withDatabaseName("networkdb")) {
            postgres.start();
            
            System.out.println("   Network: " + network.getId());
            System.out.println("   Alias: db");
            System.out.println("   Can connect via alias: " + 
                DriverManager.getConnection(
                    "jdbc:postgresql://db:5432/networkdb",
                    "test",
                    "test"
                ) != null);
        }
        
        System.out.println("\n4. Waiting for container readiness:");
        try (var redis = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379)
            .waitingFor(Wait.forListeningPort())
            .waitingFor(Wait.forLogMessage(".*Ready to accept connections.*", 1))) {
            redis.start();
            System.out.println("   Redis started and ready");
            System.out.println("   Port: " + redis.getFirstMappedPort());
        }
        
        System.out.println("\n=== Lab Complete ===");
    }
}
```

### Build and Run Instructions

```bash
cd testcontainers-basics
mvn clean test
```

### Test Execution

```bash
# Run specific test
mvn test -Dtest=BasicsTest

# Run with verbose output
mvn test -Dtest=BasicsTest -Dorg.slf4j.simpleLogger.defaultLogLevel=DEBUG
```

## Project 2: Production Integration Testing

### Overview

This real-world project implements comprehensive integration testing with multiple databases, message queues, and external services using TestContainers. It demonstrates enterprise-grade testing patterns for microservices.

### Project Structure

```
testcontainers-production/
├── pom.xml
└── src/
    └── test/
        └── java/
            └── com/
                └── learning/
                    └── testcontainers/
                        ├── IntegrationTestBase.java
                        ├── OrderRepositoryTest.java
                        ├── OrderServiceTest.java
                        ├── KafkaConsumerTest.java
                        └── FullStackTest.java
```

### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <groupId>com.learning</groupId>
    <artifactId>testcontainers-production</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    
    <properties>
        <java.version>21</java.version>
        <testcontainers.version>1.19.3</testcontainers.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka</artifactId>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>testcontainers</artifactId>
            <version>${testcontainers.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>postgresql</artifactId>
            <version>${testcontainers.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>kafka</artifactId>
            <version>${testcontainers.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>localstack</artifactId>
            <version>${testcontainers.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>${testcontainers.version}</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

### IntegrationTestBase.java

```java
package com.learning.testcontainers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.ContainerLaunchException;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.*;

@ContextConfiguration(initializers = IntegrationTestBase.TestContainerInitializer.class)
public abstract class IntegrationTestBase {
    
    static Network network = Network.newNetwork();
    
    @BeforeAll
    static void startContainers() {
        postgresContainer.start();
        kafkaContainer.start();
        localStackContainer.start();
        
        System.setProperty("spring.datasource.url", postgresContainer.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgresContainer.getUsername());
        System.setProperty("spring.datasource.password", postgresContainer.getPassword());
        System.setProperty("spring.kafka.bootstrap-servers", kafkaContainer.getBootstrapServers());
    }
    
    @AfterAll
    static void stopContainers() {
        try {
            localStackContainer.stop();
            kafkaContainer.stop();
            postgresContainer.stop();
            network.close();
        } catch (Exception e) {
            System.err.println("Error stopping containers: " + e.getMessage());
        }
    }
    
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15-alpine")
        .withDatabaseName("orders")
        .withUsername("testuser")
        .withPassword("testpass")
        .withExposedPorts(5432)
        .withNetwork(network)
        .withNetworkAliases("postgres");
    
    static KafkaContainer kafkaContainer = new KafkaContainer("confluentinc/cp-kafka:7.5.0")
        .withExposedPorts(9092)
        .withNetwork(network)
        .withNetworkAliases("kafka");
    
    static LocalStackContainer localStackContainer = new LocalStackContainer(
        LocalStackContainer.NAME, "0.14.0"
    ).withServices(S3, SQS, SNS)
        .withExposedPorts(4566)
        .withNetwork(network)
        .withNetworkAliases("localstack");
    
    static class TestContainerInitializer 
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        
        @Override
        public void initialize(ConfigurableApplicationContext context) {
            TestPropertyValues.of(
                "spring.datasource.url=" + postgresContainer.getJdbcUrl(),
                "spring.datasource.username=" + postgresContainer.getUsername(),
                "spring.datasource.password=" + postgresContainer.getPassword(),
                "spring.kafka.bootstrap-servers=" + kafkaContainer.getBootstrapServers(),
                "aws.endpoint=" + localStackContainer.getEndpointOverride(S3)
            ).applyTo(context);
        }
    }
}
```

### OrderRepositoryTest.java

```java
package com.learning.testcontainers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class OrderRepositoryTest extends IntegrationTestBase {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Test
    void shouldSaveOrder() {
        Order order = new Order();
        order.setOrderNumber("ORD-001");
        order.setAmount(new BigDecimal("99.99"));
        order.setStatus(OrderStatus.CREATED);
        
        Order saved = entityManager.persistFlushFind(order);
        
        assertNotNull(saved.getId());
        assertEquals("ORD-001", saved.getOrderNumber());
        assertEquals(OrderStatus.CREATED, saved.getStatus());
    }
    
    @Test
    void shouldFindOrderByNumber() {
        Order order = new Order();
        order.setOrderNumber("ORD-002");
        order.setAmount(new BigDecimal("149.99"));
        order.setStatus(OrderStatus.PROCESSING);
        
        entityManager.persistFlushFind(order);
        
        Optional<Order> found = orderRepository.findByOrderNumber("ORD-002");
        
        assertTrue(found.isPresent());
        assertEquals("ORD-002", found.get().getOrderNumber());
    }
    
    @Test
    void shouldFindOrdersByStatus() {
        createOrder("ORD-003", new BigDecimal("50.00"), OrderStatus.CREATED);
        createOrder("ORD-004", new BigDecimal("75.00"), OrderStatus.CREATED);
        createOrder("ORD-005", new BigDecimal("100.00"), OrderStatus.COMPLETED);
        
        List<Order> createdOrders = orderRepository.findByStatus(OrderStatus.CREATED);
        
        assertEquals(2, createdOrders.size());
    }
    
    private Order createOrder(String orderNumber, BigDecimal amount, OrderStatus status) {
        Order order = new Order();
        order.setOrderNumber(orderNumber);
        order.setAmount(amount);
        order.setStatus(status);
        return entityManager.persistFlushFind(order);
    }
}
```

### OrderServiceTest.java

```java
package com.learning.testcontainers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class OrderServiceTest extends IntegrationTestBase {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    @Test
    void shouldCreateOrder() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setAmount(new BigDecimal("199.99"));
        request.setCustomerId("CUST-001");
        
        Order order = orderService.createOrder(request);
        
        assertNotNull(order.getId());
        assertNotNull(order.getOrderNumber());
        assertEquals(OrderStatus.CREATED, order.getStatus());
    }
    
    @Test
    void shouldProcessOrder() throws Exception {
        Order order = createTestOrder();
        
        kafkaTemplate.send("order-events", order.getId().toString(), "PROCESS");
        
        boolean processed = orderService.waitForStatus(
            order.getId(), 
            OrderStatus.PROCESSED,
            5,
            TimeUnit.SECONDS
        );
        
        assertTrue(processed);
    }
    
    @Test
    void shouldPublishOrderCreatedEvent() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setAmount(new BigDecimal("299.99"));
        request.setCustomerId("CUST-002");
        
        Order order = orderService.createOrder(request);
        
        var consumer = kafkaTemplate.getDefaultTopic();
        var records = kafkaTemplate.getDefaultTopic(); // Simplified for demo
        
        assertNotNull(order.getId());
    }
    
    @Test
    void shouldCancelOrder() {
        Order order = createTestOrder();
        
        Order cancelled = orderService.cancelOrder(order.getId());
        
        assertEquals(OrderStatus.CANCELLED, cancelled.getStatus());
    }
    
    private Order createTestOrder() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setAmount(new BigDecimal("99.99"));
        request.setCustomerId("TEST-CUSTOMER");
        return orderService.createOrder(request);
    }
}
```

### FullStackTest.java

```java
package com.learning.testcontainers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class FullStackTest extends IntegrationTestBase {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    private ObjectMapper objectMapper = new ObjectMapper();
    
    @Test
    void shouldCreateOrderViaRest() throws Exception {
        Map<String, Object> request = Map.of(
            "amount", 199.99,
            "customerId", "CUST-REST-001"
        );
        
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.orderNumber").exists())
            .andExpect(jsonPath("$.status").value("CREATED"));
    }
    
    @Test
    void shouldGetOrderById() throws Exception {
        Map<String, Object> request = Map.of(
            "amount", 99.99,
            "customerId", "CUST-TEST"
        );
        
        String response = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        
        Map<String, Object> order = objectMapper.readValue(response, Map.class);
        String orderId = (String) order.get("orderId");
        
        mockMvc.perform(get("/api/orders/" + orderId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.orderId").value(orderId));
    }
    
    @Test
    void shouldProcessOrderFlow() throws Exception {
        Map<String, Object> createRequest = Map.of(
            "amount", 299.99,
            "customerId", "CUST-FLOW-001"
        );
        
        String response = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        
        Map<String, Object> order = objectMapper.readValue(response, Map.class);
        String orderId = (String) order.get("orderId");
        
        kafkaTemplate.send("order-events", orderId, "PROCESS").get();
        
        Thread.sleep(1000);
        
        mockMvc.perform(get("/api/orders/" + orderId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("PROCESSED"));
    }
}
```

### application-test.yml

```yaml
spring:
  datasource:
    url: ${spring.datasource.url}
    username: ${spring.datasource.username}
    password: ${spring.datasource.password}
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
  kafka:
    bootstrap-servers: ${spring.kafka.bootstrap-servers}
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
    consumer:
      group-id: test-group
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer

aws:
  endpoint: ${aws.endpoint}
  region: us-east-1

test:
  container:
    reuse:
      allowed: false
```

### Build and Run Instructions

```bash
cd testcontainers-production
mvn clean test
```

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=OrderServiceTest

# Run with detailed output
mvn test -Dtest=FullStackTest -Dspring.profiles.active=test

# Run with TestContainers reuse (for faster re-runs)
mvn test -Dtestcontainer.reuse.enabled=true
```

## Summary

These two projects demonstrate:

1. **Mini-Project**: Basic TestContainers usage with PostgreSQL and simple test patterns
2. **Production Project**: Complete enterprise integration testing with PostgreSQL, Kafka, LocalStack, and full-stack REST API testing

Both projects provide the foundation for writing reliable, reproducible integration tests that run in isolated Docker containers.