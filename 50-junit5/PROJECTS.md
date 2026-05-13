# JUnit 5 - Projects

This document contains two complete projects demonstrating JUnit 5: a mini-project for learning advanced testing features and a real-world project implementing production-grade unit tests.

## Mini-Projects by Concept

### 1. Test Lifecycle (2 hours)
Configure JUnit 5 lifecycle with @BeforeEach, @AfterEach, @BeforeAll, @AfterAll. Use test instance lifecycle.

### 2. Parametrized Tests (2 hours)
Create parametrized tests with @ParameterizedTest. Use @CsvSource, @EnumSource, and custom sources.

### 3. Test Interfaces (2 hours)
Implement test interfaces with default methods. Use nested classes for test organization.

### 4. Extensions & Customizers (2 hours)
Build custom JUnit 5 extensions for setup/teardown. Implement ParameterResolver and TestWatcher.

### Real-world: Unit Testing Framework
Build production-grade unit testing framework with parametrized tests, extensions, and reporting.

---

## Project 1: JUnit 5 Basics Mini-Project

### Overview

This mini-project demonstrates fundamental JUnit 5 concepts including parametrized tests, nested tests, and test interfaces. It serves as a learning starting point for modern Java testing.

### Project Structure

```
junit5-basics/
├── pom.xml
└── src/
    └── test/
        └── java/
            └── com/
                └── learning/
                    └── junit5/
```

### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
    http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>junit5-basics</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <java.version>21</java.version>
        <junit.version>5.10.1</junit.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>${junit.version}</version>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-params</artifactId>
            <version>${junit.version}</version>
        </dependency>
    </dependencies>
</project>
```

### ParametrizedTests.java

```java
package com.learning.junit5;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.EmptySource;
import static org.junit.jupiter.api.Assertions.*;

public class ParametrizedTests {
    
    @ParameterizedTest
    @ValueSource(strings = {"racecar", "radar", "level"})
    void shouldRecognizePalindromes(String word) {
        assertEquals(true, isPalindrome(word));
    }
    
    @ParameterizedTest
    @CsvSource({"1, 2, 3", "10, 20, 30", "5, 10, 15"})
    void shouldCalculateSum(int a, int b, int expected) {
        assertEquals(expected, a + b);
    }
    
    @ParameterizedTest
    @EmptySource
    void shouldHandleEmptyStrings(String input) {
        assertTrue(input.isEmpty());
    }
    
    private boolean isPalindrome(String s) {
        return s.equals(new StringBuilder(s).reverse().toString());
    }
}
```

### NestedTests.java

```java
package com.learning.junit5;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class NestedTests {
    
    private Calculator calculator;
    
    @BeforeEach
    void setup() {
        calculator = new Calculator();
    }
    
    @Nested
    @DisplayName("Addition Tests")
    class AdditionTests {
        
        @Test
        void shouldAddTwoNumbers() {
            assertEquals(5, calculator.add(2, 3));
        }
        
        @Test
        void shouldAddNegativeNumbers() {
            assertEquals(-1, calculator.add(-5, 4));
        }
    }
    
    @Nested
    @DisplayName("Multiplication Tests")
    class MultiplicationTests {
        
        @Test
        void shouldMultiplyTwoNumbers() {
            assertEquals(6, calculator.multiply(2, 3));
        }
        
        @Test
        void shouldMultiplyByZero() {
            assertEquals(0, calculator.multiply(5, 0));
        }
    }
    
    static class Calculator {
        int add(int a, int b) { return a + b; }
        int multiply(int a, int b) { return a * b; }
    }
}
```

### Build and Run

```bash
cd junit5-basics
mvn test
```

## Project 2: Production Quality Tests

### Project Structure

```
junit5-production/
├── pom.xml
└── src/
    └── test/
        └── java/
            └── com/
                └── learning/
                    └── junit5/
```

### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
    http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>junit5-production</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <java.version>21</java.version>
        <junit.version>5.10.1</junit.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>${junit.version}</version>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.2.2</version>
            </plugin>
        </plugins>
    </build>
</project>
```

### OrderServiceTest.java

```java
package com.learning.junit5;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class OrderServiceTest {
    
    private OrderService service;
    
    @BeforeEach
    void setup() {
        service = new OrderService();
    }
    
    @Nested
    @DisplayName("Order Creation")
    class OrderCreation {
        
        @Test
        @DisplayName("Should create order with valid data")
        void shouldCreateOrder() {
            Order order = service.createOrder("PROD-001", 2, "user@test.com");
            assertNotNull(order.getId());
            assertEquals("PENDING", order.getStatus());
        }
        
        @ParameterizedTest
        @CsvFileSource(resources = "/test-data/invalid-orders.csv")
        void shouldRejectInvalidOrders(String productId, int qty) {
            assertThrows(IllegalArgumentException.class, 
                () -> service.createOrder(productId, qty, "user@test.com"));
        }
    }
    
    @Nested
    @DisplayName("Order Processing")
    class OrderProcessing {
        
        @Test
        @Tag("slow")
        void shouldProcessOrderInPipeline() {
            Order order = service.createOrder("PROD-001", 1, "user@test.com");
            Order processed = service.processOrder(order.getId());
            assertEquals("PROCESSING", processed.getStatus());
        }
    }
    
    @Test
    @Disabled("Requires database")
    void shouldPersistOrder() {
        // Integration test placeholder
    }
    
    @AfterEach
    void cleanup() {
        service.clear();
    }
}
```

### OrderService.java

```java
package com.learning.junit5;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class OrderService {
    
    private final Map<String, Order> orders = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    public Order createOrder(String productId, int quantity, String userEmail) {
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("Product ID is required");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        
        Order order = new Order(
            String.valueOf(idGenerator.getAndIncrement()),
            productId,
            quantity,
            userEmail,
            "PENDING"
        );
        orders.put(order.getId(), order);
        return order;
    }
    
    public Order processOrder(String orderId) {
        Order order = orders.get(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found");
        }
        order.setStatus("PROCESSING");
        return order;
    }
    
    public void clear() {
        orders.clear();
    }
}

class Order {
    private final String id;
    private final String productId;
    private final int quantity;
    private final String userEmail;
    private String status;
    
    Order(String id, String productId, int quantity, String userEmail, String status) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.userEmail = userEmail;
        this.status = status;
    }
    
    public String getId() { return id; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
```

### test-data/invalid-orders.csv

```csv
productId,quantity
,1
PROD-001,-5
PROD-001,0
```

### Build and Run

```bash
cd junit5-production
mvn test

# Run specific test
mvn test -Dtest=OrderServiceTest

# Run tests with tag
mvn test -Dgroups=slow

# Generate HTML report
mvn surefire-report:report
```

## Summary

These projects demonstrate:

1. **Mini-Project**: Parametrized tests with multiple sources, nested test classes, and test lifecycle
2. **Production Project**: Complete test organization with tags, disabled tests, and data-driven testing

JUnit 5 provides powerful modern testing features for Java applications.