# Testing Module - PROJECTS.md

---

# Mini-Project 1: Unit Testing (2 hours)

## Project Overview

**Duration**: 2 hours  
**Difficulty**: Beginner  
**Concepts Used**: JUnit 5, Assertions, Test Life Cycle

Build a comprehensive unit testing project for a calculator application.

---

## Project Structure

```
27-testing/
├── pom.xml
├── src/main/java/com/learning/
│   ├── calculator/
│   │   ├── Calculator.java
│   │   └── AdvancedCalculator.java
└── src/test/java/com/learning/calculator/
    ├── CalculatorTest.java
    └── AdvancedCalculatorTest.java
```

---

## POM.xml

```xml
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.learning</groupId>
    <artifactId>unit-testing-demo</artifactId>
    <version>1.0-SNAPSHOT</version>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

---

## Implementation

```java
// calculator/Calculator.java
package com.learning.calculator;

public class Calculator {
    
    public int add(int a, int b) {
        return a + b;
    }
    
    public int subtract(int a, int b) {
        return a - b;
    }
    
    public int multiply(int a, int b) {
        return a * b;
    }
    
    public double divide(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException("Cannot divide by zero");
        }
        return (double) a / b;
    }
    
    public double squareRoot(int a) {
        if (a < 0) {
            throw new IllegalArgumentException("Cannot calculate square root of negative number");
        }
        return Math.sqrt(a);
    }
}
```

```java
// calculator/AdvancedCalculator.java
package com.learning.calculator;

public class AdvancedCalculator {
    
    public double power(double base, double exponent) {
        return Math.pow(base, exponent);
    }
    
    public double percentage(double value, double percent) {
        return (value * percent) / 100;
    }
    
    public boolean isPrime(int n) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;
        for (int i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) return false;
        }
        return true;
    }
    
    public long factorial(int n) {
        if (n < 0) throw new IllegalArgumentException("Factorial undefined for negative numbers");
        if (n == 0 || n == 1) return 1;
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }
}
```

```java
// test/CalculatorTest.java
package com.learning.calculator;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Calculator Unit Tests")
class CalculatorTest {
    
    private Calculator calculator;
    
    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }
    
    @Nested
    @DisplayName("Addition Tests")
    class AdditionTests {
        @Test
        @DisplayName("Should add two positive numbers")
        void testAddPositive() {
            assertEquals(5, calculator.add(2, 3));
        }
        
        @Test
        @DisplayName("Should add negative numbers")
        void testAddNegative() {
            assertEquals(-1, calculator.add(-5, 4));
        }
        
        @Test
        @DisplayName("Should add zero")
        void testAddZero() {
            assertEquals(10, calculator.add(10, 0));
        }
    }
    
    @Nested
    @DisplayName("Division Tests")
    class DivisionTests {
        @Test
        @DisplayName("Should divide two numbers correctly")
        void testDivide() {
            assertEquals(2.0, calculator.divide(10, 5));
        }
        
        @Test
        @DisplayName("Should throw exception when dividing by zero")
        void testDivideByZero() {
            ArithmeticException exception = assertThrows(
                ArithmeticException.class,
                () -> calculator.divide(10, 0)
            );
            assertEquals("Cannot divide by zero", exception.getMessage());
        }
    }
    
    @Nested
    @DisplayName("Square Root Tests")
    class SquareRootTests {
        @Test
        @DisplayName("Should calculate square root correctly")
        void testSquareRoot() {
            assertEquals(4.0, calculator.squareRoot(16));
        }
        
        @Test
        @DisplayName("Should throw exception for negative input")
        void testSquareRootNegative() {
            assertThrows(
                IllegalArgumentException.class,
                () -> calculator.squareRoot(-4)
            );
        }
    }
}
```

```java
// test/AdvancedCalculatorTest.java
package com.learning.calculator;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Advanced Calculator Unit Tests")
class AdvancedCalculatorTest {
    
    private AdvancedCalculator calculator;
    
    @BeforeEach
    void setUp() {
        calculator = new AdvancedCalculator();
    }
    
    @Test
    @DisplayName("Should calculate power correctly")
    void testPower() {
        assertEquals(8.0, calculator.power(2, 3));
    }
    
    @Test
    @DisplayName("Should calculate percentage correctly")
    void testPercentage() {
        assertEquals(25.0, calculator.percentage(200, 12.5));
    }
    
    @ParameterizedTest
    @CsvSource({
        "2, true",
        "3, true",
        "4, false",
        "5, true",
        "17, true"
    })
    @DisplayName("Should correctly identify prime numbers")
    void testIsPrime(int number, boolean expected) {
        assertEquals(expected, calculator.isPrime(number));
    }
    
    @Test
    @DisplayName("Should calculate factorial correctly")
    void testFactorial() {
        assertEquals(120, calculator.factorial(5));
        assertEquals(1, calculator.factorial(0));
        assertEquals(1, calculator.factorial(1));
    }
    
    @Test
    @DisplayName("Should throw exception for negative factorial")
    void testFactorialNegative() {
        assertThrows(IllegalArgumentException.class, () -> calculator.factorial(-5));
    }
}
```

---

## Build Instructions

```bash
cd 27-testing
mvn test
```

---

# Mini-Project 2: Integration Testing (2 hours)

## Project Overview

**Duration**: 2 hours  
**Difficulty**: Intermediate  
**Concepts Used**: Spring Boot Test, @SpringBootTest, TestContainers

Build integration tests for a REST API with database interaction.

---

## Project Structure

```
27-testing/
├── src/main/java/com/learning/
│   ├── controller/
│   │   └── ProductController.java
│   ├── service/
│   │   └── ProductService.java
│   ├── repository/
│   │   └── ProductRepository.java
│   └── entity/
│       └── Product.java
└── src/test/java/com/learning/
    └── IntegrationTests.java
```

---

## Implementation

```java
// entity/Product.java
package com.learning.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @Column(nullable = false)
    private Double price;
    
    private Integer stock;
    
    public Product() {}
    
    public Product(String name, String description, Double price) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = 0;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}
```

```java
// repository/ProductRepository.java
package com.learning.repository;

import com.learning.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContaining(String name);
    List<Product> findByPriceLessThan(Double price);
}
```

```java
// service/ProductService.java
package com.learning.service;

import com.learning.entity.Product;
import com.learning.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {
    
    private final ProductRepository productRepository;
    
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }
    
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    public List<Product> searchProducts(String query) {
        return productRepository.findByNameContaining(query);
    }
    
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
    
    public Product updateStock(Long id, Integer newStock) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setStock(newStock);
        return productRepository.save(product);
    }
}
```

```java
// controller/ProductController.java
package com.learning.controller;

import com.learning.entity.Product;
import com.learning.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    private final ProductService productService;
    
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        return ResponseEntity.ok(productService.createProduct(product));
    }
    
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        return productService.getProductById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String q) {
        return ResponseEntity.ok(productService.searchProducts(q));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{id}/stock")
    public ResponseEntity<Product> updateStock(@PathVariable Long id, @RequestParam Integer stock) {
        return ResponseEntity.ok(productService.updateStock(id, stock));
    }
}
```

```java
// IntegrationTests.java
package com.learning;

import com.learning.entity.Product;
import com.learning.service.ProductService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class IntegrationTests {
    
    @Autowired
    private ProductService productService;
    
    private static Long createdProductId;
    
    @Test
    @Order(1)
    @DisplayName("Should create a new product")
    void testCreateProduct() {
        Product product = new Product("Laptop", "High-performance laptop", 1299.99);
        product.setStock(10);
        
        Product saved = productService.createProduct(product);
        
        assertNotNull(saved.getId());
        assertEquals("Laptop", saved.getName());
        assertEquals(1299.99, saved.getPrice());
        createdProductId = saved.getId();
    }
    
    @Test
    @Order(2)
    @DisplayName("Should retrieve product by ID")
    void testGetProductById() {
        Optional<Product> product = productService.getProductById(createdProductId);
        
        assertTrue(product.isPresent());
        assertEquals("Laptop", product.get().getName());
    }
    
    @Test
    @Order(3)
    @DisplayName("Should retrieve all products")
    void testGetAllProducts() {
        List<Product> products = productService.getAllProducts();
        
        assertFalse(products.isEmpty());
        assertTrue(products.size() >= 1);
    }
    
    @Test
    @Order(4)
    @DisplayName("Should search products by name")
    void testSearchProducts() {
        List<Product> results = productService.searchProducts("Laptop");
        
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(p -> p.getName().contains("Laptop")));
    }
    
    @Test
    @Order(5)
    @DisplayName("Should update product stock")
    void testUpdateStock() {
        Product updated = productService.updateStock(createdProductId, 25);
        
        assertEquals(25, updated.getStock());
    }
    
    @Test
    @Order(6)
    @DisplayName("Should delete product")
    void testDeleteProduct() {
        productService.deleteProduct(createdProductId);
        
        Optional<Product> product = productService.getProductById(createdProductId);
        assertTrue(product.isEmpty());
    }
}
```

---

## Build Instructions

```bash
cd 27-testing
mvn test -Dspring.profiles.active=test
```

---

# Mini-Project 3: Mocking (2 hours)

## Project Overview

**Duration**: 2 hours  
**Difficulty**: Intermediate  
**Concepts Used**: Mockito, @Mock, @InjectMocks, Spy, ArgumentMatchers

Build comprehensive mocking tests for service layer dependencies.

---

## Project Structure

```
27-testing/
├── src/main/java/com/learning/
│   ├── service/
│   │   ├── PaymentService.java
│   │   ├── OrderService.java
│   │   └── NotificationService.java
│   ├── repository/
│   │   └── OrderRepository.java
│   └── model/
│       └── Order.java
└── src/test/java/com/learning/
    ├── PaymentServiceTest.java
    └── OrderServiceTest.java
```

---

## Implementation

```java
// model/Order.java
package com.learning.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private String orderId;
    private Long customerId;
    private List<OrderItem> items;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private LocalDateTime createdAt;
    
    public enum OrderStatus {
        PENDING, PAID, SHIPPED, DELIVERED, CANCELLED
    }
    
    public static class OrderItem {
        private String productId;
        private String productName;
        private int quantity;
        private BigDecimal price;
        
        public OrderItem(String productId, String productName, int quantity, BigDecimal price) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.price = price;
        }
        
        public BigDecimal getSubtotal() {
            return price.multiply(BigDecimal.valueOf(quantity));
        }
        
        public String getProductId() { return productId; }
        public String getProductName() { return productName; }
        public int getQuantity() { return quantity; }
        public BigDecimal getPrice() { return price; }
    }
    
    public Order(String orderId, Long customerId, List<OrderItem> items) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.items = items;
        this.totalAmount = items.stream().map(OrderItem::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        this.status = OrderStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
    
    public String getOrderId() { return orderId; }
    public Long getCustomerId() { return customerId; }
    public List<OrderItem> getItems() { return items; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
```

```java
// repository/OrderRepository.java
package com.learning.repository;

import com.learning.model.Order;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(String orderId);
    void update(Order order);
    void delete(String orderId);
}
```

```java
// service/PaymentService.java
package com.learning.service;

import java.math.BigDecimal;

public interface PaymentService {
    boolean processPayment(Long customerId, BigDecimal amount);
    boolean refundPayment(String transactionId);
    String getTransactionStatus(String transactionId);
}
```

```java
// service/NotificationService.java
package com.learning.service;

public interface NotificationService {
    void sendEmail(String to, String subject, String body);
    void sendSms(String phoneNumber, String message);
    void sendPushNotification(String userId, String message);
}
```

```java
// service/OrderService.java
package com.learning.service;

import com.learning.model.Order;
import com.learning.repository.OrderRepository;
import java.math.BigDecimal;
import java.util.Optional;

public class OrderService {
    
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;
    private final NotificationService notificationService;
    
    public OrderService(OrderRepository orderRepository, 
                        PaymentService paymentService,
                        NotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.paymentService = paymentService;
        this.notificationService = notificationService;
    }
    
    public Order createOrder(Order order) {
        Order saved = orderRepository.save(order);
        notificationService.sendEmail(
            "customer@example.com",
            "Order Confirmation",
            "Your order " + saved.getOrderId() + " has been placed"
        );
        return saved;
    }
    
    public boolean processPayment(String orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            return false;
        }
        
        Order order = orderOpt.get();
        boolean paymentResult = paymentService.processPayment(
            order.getCustomerId(), 
            order.getTotalAmount()
        );
        
        if (paymentResult) {
            order.setStatus(Order.OrderStatus.PAID);
            orderRepository.update(order);
            notificationService.sendEmail(
                "customer@example.com",
                "Payment Successful",
                "Payment processed for order " + orderId
            );
        }
        
        return paymentResult;
    }
    
    public boolean cancelOrder(String orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            return false;
        }
        
        Order order = orderOpt.get();
        if (order.getStatus() == Order.OrderStatus.PAID) {
            paymentService.refundPayment("TXN-" + orderId);
        }
        
        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.update(order);
        
        notificationService.sendEmail(
            "customer@example.com",
            "Order Cancelled",
            "Your order " + orderId + " has been cancelled"
        );
        
        return true;
    }
}
```

```java
// PaymentServiceTest.java
package com.learning;

import com.learning.service.PaymentService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Payment Service Mock Tests")
class PaymentServiceTest {
    
    @Mock
    private PaymentService paymentService;
    
    @Test
    @DisplayName("Should process successful payment")
    void testProcessPaymentSuccess() {
        when(paymentService.processPayment(anyLong(), any(BigDecimal.class)))
            .thenReturn(true);
        
        boolean result = paymentService.processPayment(1L, new BigDecimal("100.00"));
        
        assertTrue(result);
        verify(paymentService, times(1)).processPayment(1L, new BigDecimal("100.00"));
    }
    
    @Test
    @DisplayName("Should handle payment failure")
    void testProcessPaymentFailure() {
        when(paymentService.processPayment(anyLong(), any(BigDecimal.class)))
            .thenReturn(false);
        
        boolean result = paymentService.processPayment(1L, new BigDecimal("100.00"));
        
        assertFalse(result);
    }
    
    @Test
    @DisplayName("Should refund payment correctly")
    void testRefundPayment() {
        when(paymentService.refundPayment(anyString())).thenReturn(true);
        
        boolean result = paymentService.refundPayment("TXN-123");
        
        assertTrue(result);
        verify(paymentService).refundPayment("TXN-123");
    }
    
    @Test
    @DisplayName("Should get transaction status")
    void testGetTransactionStatus() {
        when(paymentService.getTransactionStatus("TXN-123"))
            .thenReturn("SUCCESS");
        
        String status = paymentService.getTransactionStatus("TXN-123");
        
        assertEquals("SUCCESS", status);
    }
}
```

```java
// OrderServiceTest.java
package com.learning;

import com.learning.model.Order;
import com.learning.repository.OrderRepository;
import com.learning.service.NotificationService;
import com.learning.service.PaymentService;
import com.learning.service.OrderService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Order Service Mock Tests")
class OrderServiceTest {
    
    @Mock
    private OrderRepository orderRepository;
    
    @Mock
    private PaymentService paymentService;
    
    @Mock
    private NotificationService notificationService;
    
    @InjectMocks
    private OrderService orderService;
    
    @Test
    @DisplayName("Should create order successfully")
    void testCreateOrder() {
        Order order = new Order("ORD-001", 1L, List.of(
            new Order.OrderItem("PROD-1", "Laptop", 1, new BigDecimal("1000"))
        ));
        
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        
        Order result = orderService.createOrder(order);
        
        assertNotNull(result);
        assertEquals("ORD-001", result.getOrderId());
        verify(orderRepository).save(order);
        verify(notificationService).sendEmail(anyString(), anyString(), anyString());
    }
    
    @Test
    @DisplayName("Should process payment successfully")
    void testProcessPaymentSuccess() {
        Order order = new Order("ORD-001", 1L, List.of(
            new Order.OrderItem("PROD-1", "Laptop", 1, new BigDecimal("1000"))
        ));
        
        when(orderRepository.findById("ORD-001")).thenReturn(Optional.of(order));
        when(paymentService.processPayment(anyLong(), any(BigDecimal.class))).thenReturn(true);
        
        boolean result = orderService.processPayment("ORD-001");
        
        assertTrue(result);
        assertEquals(Order.OrderStatus.PAID, order.getStatus());
        verify(orderRepository).update(order);
    }
    
    @Test
    @DisplayName("Should handle payment failure")
    void testProcessPaymentFailure() {
        Order order = new Order("ORD-001", 1L, List.of(
            new Order.OrderItem("PROD-1", "Laptop", 1, new BigDecimal("1000"))
        ));
        
        when(orderRepository.findById("ORD-001")).thenReturn(Optional.of(order));
        when(paymentService.processPayment(anyLong(), any(BigDecimal.class))).thenReturn(false);
        
        boolean result = orderService.processPayment("ORD-001");
        
        assertFalse(result);
        assertEquals(Order.OrderStatus.PENDING, order.getStatus());
    }
    
    @Test
    @DisplayName("Should cancel order and process refund")
    void testCancelOrderWithRefund() {
        Order order = new Order("ORD-001", 1L, List.of(
            new Order.OrderItem("PROD-1", "Laptop", 1, new BigDecimal("1000"))
        ));
        order.setStatus(Order.OrderStatus.PAID);
        
        when(orderRepository.findById("ORD-001")).thenReturn(Optional.of(order));
        when(paymentService.refundPayment(anyString())).thenReturn(true);
        
        boolean result = orderService.cancelOrder("ORD-001");
        
        assertTrue(result);
        assertEquals(Order.OrderStatus.CANCELLED, order.getStatus());
        verify(paymentService).refundPayment("TXN-ORD-001");
    }
    
    @Test
    @DisplayName("Should return false for non-existent order")
    void testCancelNonExistentOrder() {
        when(orderRepository.findById("ORD-999")).thenReturn(Optional.empty());
        
        boolean result = orderService.cancelOrder("ORD-999");
        
        assertFalse(result);
    }
}
```

---

## Build Instructions

```bash
cd 27-testing
mvn test -Dtest=PaymentServiceTest,OrderServiceTest
```

---

# Mini-Project 4: Test Coverage (2 hours)

## Project Overview

**Duration**: 2 hours  
**Difficulty**: Intermediate  
**Concepts Used**: JaCoCo, Code Coverage Analysis, Coverage Reports

Build comprehensive test coverage analysis with JaCoCo and identify uncovered code paths.

---

## Project Structure

```
27-testing/
├── pom.xml
├── src/main/java/com/learning/
│   ├── service/
│   │   └── BillingService.java
│   └── model/
│       └── Invoice.java
└── src/test/java/com/learning/
    └── BillingServiceTest.java
```

---

## Implementation

```xml
<!-- pom.xml - Add JaCoCo configuration -->
<build>
    <plugins>
        <plugin>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <version>0.8.11</version>
            <executions>
                <execution>
                    <id>prepare-agent</id>
                    <goals>
                        <goal>prepare-agent</goal>
                    </goals>
                </execution>
                <execution>
                    <id>report</id>
                    <phase>test</phase>
                    <goals>
                        <goal>report</goal>
                    </goals>
                </execution>
                <execution>
                    <id>check</id>
                    <goals>
                        <goal>check</goal>
                    </goals>
                    <configuration>
                        <rules>
                            <rule>
                                <element>CLASS</element>
                                <limits>
                                    <limit>
                                        <counter>LINE</counter>
                                        <value>COVEREDRATIO</value>
                                        <minimum>0.80</minimum>
                                    </limit>
                                </limits>
                            </rule>
                        </rules>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

```java
// model/Invoice.java
package com.learning.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class Invoice {
    private String invoiceId;
    private String customerId;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private List<InvoiceItem> items;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal total;
    private InvoiceStatus status;
    
    public enum InvoiceStatus {
        DRAFT, ISSUED, PAID, OVERDUE, CANCELLED
    }
    
    public static class InvoiceItem {
        private String description;
        private int quantity;
        private BigDecimal unitPrice;
        
        public InvoiceItem(String description, int quantity, BigDecimal unitPrice) {
            this.description = description;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }
        
        public BigDecimal getSubtotal() {
            return unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
        
        public String getDescription() { return description; }
        public int getQuantity() { return quantity; }
        public BigDecimal getUnitPrice() { return unitPrice; }
    }
    
    public Invoice(String invoiceId, String customerId) {
        this.invoiceId = invoiceId;
        this.customerId = customerId;
        this.issueDate = LocalDate.now();
        this.dueDate = LocalDate.now().plusDays(30);
        this.status = InvoiceStatus.DRAFT;
    }
    
    public void addItem(InvoiceItem item) {
        items.add(item);
        calculateTotal();
    }
    
    private void calculateTotal() {
        this.subtotal = items.stream()
            .map(InvoiceItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.tax = subtotal.multiply(new BigDecimal("0.10"));
        this.total = subtotal.add(tax);
    }
    
    public void markAsPaid() {
        this.status = InvoiceStatus.PAID;
    }
    
    public void markAsOverdue() {
        if (LocalDate.now().isAfter(dueDate) && status == InvoiceStatus.ISSUED) {
            this.status = InvoiceStatus.OVERDUE;
        }
    }
    
    public boolean isOverdue() {
        return LocalDate.now().isAfter(dueDate) && 
               (status == InvoiceStatus.ISSUED || status == InvoiceStatus.OVERDUE);
    }
    
    public BigDecimal calculateLateFee(double dailyRate) {
        if (!isOverdue()) return BigDecimal.ZERO;
        long daysOverdue = LocalDate.now().toEpochDay() - dueDate.toEpochDay();
        return total.multiply(BigDecimal.valueOf(daysOverdue * dailyRate / 100));
    }
    
    public String getInvoiceId() { return invoiceId; }
    public String getCustomerId() { return customerId; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getDueDate() { return dueDate; }
    public List<InvoiceItem> getItems() { return items; }
    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getTax() { return tax; }
    public BigDecimal getTotal() { return total; }
    public InvoiceStatus getStatus() { return status; }
    public void setStatus(InvoiceStatus status) { this.status = status; }
}
```

```java
// service/BillingService.java
package com.learning.service;

import com.learning.model.Invoice;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class BillingService {
    
    public Invoice createInvoice(String customerId, List<Invoice.InvoiceItem> items) {
        if (customerId == null || customerId.isEmpty()) {
            throw new IllegalArgumentException("Customer ID is required");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Invoice must have at least one item");
        }
        
        Invoice invoice = new Invoice("INV-" + System.currentTimeMillis(), customerId);
        for (Invoice.InvoiceItem item : items) {
            invoice.addItem(item);
        }
        
        return invoice;
    }
    
    public Invoice issueInvoice(Invoice invoice) {
        if (invoice.getStatus() != Invoice.InvoiceStatus.DRAFT) {
            throw new IllegalStateException("Only draft invoices can be issued");
        }
        invoice.setStatus(Invoice.InvoiceStatus.ISSUED);
        return invoice;
    }
    
    public Invoice payInvoice(Invoice invoice, BigDecimal paymentAmount) {
        if (paymentAmount == null || paymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }
        
        if (paymentAmount.compareTo(invoice.getTotal()) < 0) {
            throw new IllegalArgumentException("Payment amount is less than invoice total");
        }
        
        invoice.markAsPaid();
        return invoice;
    }
    
    public List<Invoice> filterOverdueInvoices(List<Invoice> invoices) {
        if (invoices == null) {
            return List.of();
        }
        
        return invoices.stream()
            .filter(Invoice::isOverdue)
            .collect(Collectors.toList());
    }
    
    public BigDecimal calculateTotalRevenue(List<Invoice> invoices) {
        if (invoices == null || invoices.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        return invoices.stream()
            .filter(inv -> inv.getStatus() == Invoice.InvoiceStatus.PAID)
            .map(Invoice::getTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public List<Invoice> applyDiscount(List<Invoice> invoices, double discountPercent) {
        if (discountPercent < 0 || discountPercent > 100) {
            throw new IllegalArgumentException("Discount must be between 0 and 100");
        }
        
        return invoices.stream()
            .map(inv -> {
                if (inv.getStatus() == Invoice.InvoiceStatus.DRAFT) {
                    BigDecimal discount = inv.getTotal()
                        .multiply(BigDecimal.valueOf(discountPercent / 100));
                    inv.setStatus(Invoice.InvoiceStatus.ISSUED);
                }
                return inv;
            })
            .collect(Collectors.toList());
    }
}
```

```java
// BillingServiceTest.java
package com.learning;

import com.learning.model.Invoice;
import com.learning.service.BillingService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Billing Service Tests")
class BillingServiceTest {
    
    @InjectMocks
    private BillingService billingService;
    
    @Test
    @DisplayName("Should create invoice with items")
    void testCreateInvoice() {
        List<Invoice.InvoiceItem> items = List.of(
            new Invoice.InvoiceItem("Product A", 2, new BigDecimal("50.00")),
            new Invoice.InvoiceItem("Product B", 1, new BigDecimal("30.00"))
        );
        
        Invoice invoice = billingService.createInvoice("CUST-001", items);
        
        assertNotNull(invoice);
        assertEquals("CUST-001", invoice.getCustomerId());
        assertEquals(new BigDecimal("130.00"), invoice.getSubtotal());
        assertEquals(new BigDecimal("13.00"), invoice.getTax());
        assertEquals(new BigDecimal("143.00"), invoice.getTotal());
    }
    
    @Test
    @DisplayName("Should throw exception for empty customer ID")
    void testCreateInvoiceEmptyCustomerId() {
        List<Invoice.InvoiceItem> items = List.of(
            new Invoice.InvoiceItem("Product A", 1, new BigDecimal("50.00"))
        );
        
        assertThrows(IllegalArgumentException.class, 
            () -> billingService.createInvoice("", items));
    }
    
    @Test
    @DisplayName("Should throw exception for empty items")
    void testCreateInvoiceEmptyItems() {
        assertThrows(IllegalArgumentException.class, 
            () -> billingService.createInvoice("CUST-001", List.of()));
    }
    
    @Test
    @DisplayName("Should issue invoice")
    void testIssueInvoice() {
        Invoice invoice = new Invoice("INV-001", "CUST-001");
        invoice.addItem(new Invoice.InvoiceItem("Product A", 1, new BigDecimal("100.00")));
        
        Invoice issued = billingService.issueInvoice(invoice);
        
        assertEquals(Invoice.InvoiceStatus.ISSUED, issued.getStatus());
    }
    
    @Test
    @DisplayName("Should throw exception when issuing non-draft invoice")
    void testIssueNonDraftInvoice() {
        Invoice invoice = new Invoice("INV-001", "CUST-001");
        invoice.setStatus(Invoice.InvoiceStatus.ISSUED);
        
        assertThrows(IllegalStateException.class, 
            () -> billingService.issueInvoice(invoice));
    }
    
    @Test
    @DisplayName("Should pay invoice successfully")
    void testPayInvoice() {
        Invoice invoice = new Invoice("INV-001", "CUST-001");
        invoice.addItem(new Invoice.InvoiceItem("Product A", 1, new BigDecimal("100.00")));
        invoice.setStatus(Invoice.InvoiceStatus.ISSUED);
        
        Invoice paid = billingService.payInvoice(invoice, new BigDecimal("110.00"));
        
        assertEquals(Invoice.InvoiceStatus.PAID, paid.getStatus());
    }
    
    @Test
    @DisplayName("Should calculate total revenue")
    void testCalculateTotalRevenue() {
        List<Invoice> invoices = List.of(
            createInvoice("CUST-001", new BigDecimal("100.00"), Invoice.InvoiceStatus.PAID),
            createInvoice("CUST-002", new BigDecimal("200.00"), Invoice.InvoiceStatus.PAID),
            createInvoice("CUST-003", new BigDecimal("150.00"), Invoice.InvoiceStatus.ISSUED)
        );
        
        BigDecimal revenue = billingService.calculateTotalRevenue(invoices);
        
        assertEquals(new BigDecimal("300.00"), revenue);
    }
    
    private Invoice createInvoice(String customerId, BigDecimal amount, Invoice.InvoiceStatus status) {
        Invoice invoice = new Invoice("INV-" + System.currentTimeMillis(), customerId);
        invoice.addItem(new Invoice.InvoiceItem("Product", 1, amount));
        invoice.setStatus(status);
        return invoice;
    }
}
```

---

## Build Instructions

```bash
cd 27-testing
mvn clean verify
mvn jacoco:report
# View report at target/site/jacoco/index.html
```

---

# Real-World Project: Test Automation Framework (10+ hours)

## Project Overview

**Duration**: 10+ hours  
**Difficulty**: Advanced  
**Concepts Used**: Page Object Model, TestNG, Selenium, REST Assured, Extent Reports

Build a comprehensive test automation framework for a web application.

---

## Project Structure

```
27-testing/
├── src/test/java/com/learning/
│   ├── framework/
│   │   ├── base/
│   │   │   ├── BaseTest.java
│   │   │   └── BasePage.java
│   │   ├── pages/
│   │   │   ├── LoginPage.java
│   │   │   └── DashboardPage.java
│   │   ├── utils/
│   │   │   ├── DriverManager.java
│   │   │   └── ConfigManager.java
│   │   └── reports/
│   │       └── ExtentReportManager.java
│   ├── api/
│   │   └── ApiTest.java
│   └── tests/
│       ├── SmokeTests.java
│       └── RegressionTests.java
├── src/test/resources/
│   ├── config.properties
│   └── testdata.json
└── pom.xml
```

---

## Implementation

```java
// framework/base/BaseTest.java
package com.learning.framework.base;

import com.learning.framework.reports.ExtentReportManager;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;
import java.io.IOException;

public class BaseTest {
    
    protected WebDriver driver;
    protected ExtentReportManager reportManager;
    
    @BeforeSuite
    public void beforeSuite() {
        ExtentReportManager.initReports();
    }
    
    @BeforeClass
    @Parameters({"browser", "headless"})
    public void setUp(@Optional("chrome") String browser, 
                      @Optional("false") boolean headless) {
        driver = DriverManager.getDriver(browser, headless);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, java.util.concurrent.TimeUnit.SECONDS);
    }
    
    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
    
    @AfterSuite
    public void afterSuite() {
        ExtentReportManager.flushReports();
    }
    
    protected void navigateTo(String url) {
        driver.get(url);
    }
}
```

```java
// framework/pages/LoginPage.java
package com.learning.framework.pages;

import com.learning.framework.base.BasePage;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.ui.*;

public class LoginPage extends BasePage {
    
    @FindBy(how = How.ID, using = "username")
    private WebElement usernameInput;
    
    @FindBy(how = How.ID, using = "password")
    private WebElement passwordInput;
    
    @FindBy(how = How.ID, using = "loginButton")
    private WebElement loginButton;
    
    @FindBy(how = How.CSS, using = ".error-message")
    private WebElement errorMessage;
    
    public LoginPage(WebDriver driver) {
        super(driver);
    }
    
    public DashboardPage login(String username, String password) {
        waitForElementVisible(usernameInput);
        usernameInput.clear();
        usernameInput.sendKeys(username);
        
        passwordInput.clear();
        passwordInput.sendKeys(password);
        
        loginButton.click();
        
        return new DashboardPage(driver);
    }
    
    public boolean isErrorDisplayed() {
        try {
            waitForElementVisible(errorMessage, 5);
            return errorMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    public String getErrorMessageText() {
        return errorMessage.getText();
    }
}
```

```java
// framework/pages/DashboardPage.java
package com.learning.framework.pages;

import com.learning.framework.base.BasePage;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import java.util.List;

public class DashboardPage extends BasePage {
    
    @FindBy(how = How.CSS, using = ".user-profile")
    private WebElement userProfile;
    
    @FindBy(how = How.CSS, using = ".nav-item")
    private List<WebElement> navItems;
    
    @FindBy(how = How.ID, using = "searchInput")
    private WebElement searchInput;
    
    @FindBy(how = How.CSS, using = ".data-table tbody tr")
    private List<WebElement> tableRows;
    
    public DashboardPage(WebDriver driver) {
        super(driver);
    }
    
    public boolean isUserLoggedIn() {
        return userProfile.isDisplayed();
    }
    
    public void search(String query) {
        searchInput.clear();
        searchInput.sendKeys(query);
        searchInput.sendKeys(Keys.ENTER);
    }
    
    public int getTableRowCount() {
        return tableRows.size();
    }
    
    public void clickNavigationItem(String itemName) {
        for (WebElement item : navItems) {
            if (item.getText().contains(itemName)) {
                item.click();
                break;
            }
        }
    }
}
```

```java
// tests/SmokeTests.java
package com.learning.tests;

import com.learning.framework.base.BaseTest;
import com.learning.framework.pages.LoginPage;
import com.learning.framework.pages.DashboardPage;
import org.testng.annotations.*;
import static org.testng.Assert.*;

public class SmokeTests extends BaseTest {
    
    private LoginPage loginPage;
    private DashboardPage dashboardPage;
    
    @BeforeMethod
    public void beforeMethod() {
        navigateTo("https://example-app.com");
        loginPage = new LoginPage(driver);
    }
    
    @Test(priority = 1)
    public void testSuccessfulLogin() {
        dashboardPage = loginPage.login("validuser", "validpass");
        
        assertTrue(dashboardPage.isUserLoggedIn(), 
            "User should be logged in after providing valid credentials");
    }
    
    @Test(priority = 2)
    public void testFailedLogin() {
        loginPage.login("invaliduser", "invalidpass");
        
        assertTrue(loginPage.isErrorDisplayed(), 
            "Error message should be displayed for invalid credentials");
        assertEquals(loginPage.getErrorMessageText(), 
            "Invalid username or password");
    }
    
    @Test(priority = 3)
    public void testEmptyCredentialsLogin() {
        loginPage.login("", "");
        
        assertTrue(loginPage.isErrorDisplayed(), 
            "Error message should be displayed for empty credentials");
    }
    
    @Test(priority = 4)
    public void testDashboardSearch() {
        dashboardPage = loginPage.login("validuser", "validpass");
        dashboardPage.search("test query");
        
        assertTrue(dashboardPage.getTableRowCount() > 0, 
            "Search should return results");
    }
}
```

```java
// api/ApiTest.java
package com.learning.api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.*;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class ApiTest {
    
    @BeforeClass
    public void setUp() {
        RestAssured.baseURI = "https://api.example.com";
        RestAssured.basePath = "/v1";
    }
    
    @Test
    public void testGetUsers() {
        given()
            .header("Authorization", "Bearer test-token")
        .when()
            .get("/users")
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .body("[0].id", notNullValue())
            .body("[0].name", notNullValue());
    }
    
    @Test
    public void testCreateUser() {
        String requestBody = """
            {
                "name": "John Doe",
                "email": "john@example.com",
                "role": "user"
            }
            """;
        
        given()
            .header("Authorization", "Bearer test-token")
            .header("Content-Type", "application/json")
            .body(requestBody)
        .when()
            .post("/users")
        .then()
            .statusCode(201)
            .body("name", equalTo("John Doe"))
            .body("email", equalTo("john@example.com"));
    }
    
    @Test
    public void testUpdateUser() {
        String requestBody = """
            {
                "name": "John Updated"
            }
            """;
        
        given()
            .header("Authorization", "Bearer test-token")
            .header("Content-Type", "application/json")
            .body(requestBody)
        .when()
            .put("/users/1")
        .then()
            .statusCode(200)
            .body("name", equalTo("John Updated"));
    }
    
    @Test
    public void testDeleteUser() {
        given()
            .header("Authorization", "Bearer test-token")
        .when()
            .delete("/users/1")
        .then()
            .statusCode(204);
    }
    
    @Test
    public void testGetUserNotFound() {
        given()
            .header("Authorization", "Bearer test-token")
        .when()
            .get("/users/99999")
        .then()
            .statusCode(404);
    }
}
```

---

## Build Instructions

```bash
cd 27-testing
mvn test -DsuiteXmlFile=src/test/resources/testng-smoke.xml
mvn test -DsuiteXmlFile=src/test/resources/testng-regression.xml
```

---

# Additional Testing Best Practices

## Test Organization

```java
// Use descriptive test names
@Test
@DisplayName("Should return 404 when product does not exist")
void shouldReturn404WhenProductDoesNotExist() { }

// Use test categories
@Test(groups = {"smoke", "regression"})
void testCriticalFeature() { }

// Use data-driven tests
@Test(dataProvider = "invalid-inputs")
void shouldRejectInvalidInput(String input, String expectedError) { }
```

## Code Coverage Goals

| Coverage Type | Minimum Target | Recommended |
|---------------|---------------|-------------|
| Line Coverage | 70% | 80% |
| Branch Coverage | 60% | 70% |
| Method Coverage | 80% | 90% |
| Class Coverage | 80% | 90% |

## Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=CalculatorTest

# Run tests with coverage
mvn clean verify

# Run tests in parallel
mvn test -Dparallel=methods -DthreadCount=4
```