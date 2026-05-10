# Testing Frameworks Projects - Module 9

This module covers Java testing frameworks including JUnit 5, Mockito, AssertJ, TestNG, and integration testing with Spring Boot Test.

## Mini-Project: Comprehensive Unit Testing Suite (2-4 hours)

### Overview
Create a comprehensive unit testing suite for a simple e-commerce order management system using JUnit 5, Mockito, and AssertJ.

### Project Structure
```
testing-demo/
├── pom.xml
├── src/main/java/com/learning/ecommerce/
│   ├── EcommerceApplication.java
│   ├── model/
│   │   ├── Product.java
│   │   ├── Customer.java
│   │   ├── Order.java
│   │   └── OrderItem.java
│   ├── service/
│   │   ├── ProductService.java
│   │   ├── OrderService.java
│   │   └── PricingService.java
│   └── repository/
│       ├── ProductRepository.java
│       └── OrderRepository.java
└── src/test/java/com/learning/ecommerce/
    ├── ProductServiceTest.java
    ├── OrderServiceTest.java
    └── PricingServiceTest.java
```

### Implementation

#### pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>testing-demo</artifactId>
    <version>1.0.0</version>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <properties>
        <java.version>21</java.version>
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
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        
        <!-- Test Dependencies -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-core</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-junit-jupiter</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.assertj</groupId>
            <artifactId>assertj-core</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

#### Product Model
```java
package com.learning.ecommerce.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String sku;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    private Integer stockQuantity;
    
    @Enumerated(EnumType.STRING)
    private ProductCategory category;
    
    private boolean active;
    
    public Product() {}
    
    public Product(String sku, String name, BigDecimal price, Integer stockQuantity) {
        this.sku = sku;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.active = true;
    }
    
    public enum ProductCategory {
        ELECTRONICS, CLOTHING, BOOKS, FOOD, OTHER
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
    public ProductCategory getCategory() { return category; }
    public void setCategory(ProductCategory category) { this.category = category; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
```

#### Order Model
```java
package com.learning.ecommerce.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String orderNumber;
    
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();
    
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    
    private BigDecimal totalAmount;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Order() {
        this.createdAt = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
    }
    
    public enum OrderStatus {
        PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
    }
    
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }
    
    public void calculateTotal() {
        this.totalAmount = items.stream()
            .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
```

#### Customer Model
```java
package com.learning.ecommerce.model;

import jakarta.persistence.*;

@Entity
@Table(name = "customers")
public class Customer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    private String phone;
    
    @Embedded
    private Address address;
    
    public Customer() {}
    
    public Customer(String name, String email) {
        this.name = name;
        this.email = email;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
}

@Embeddable
class Address {
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    
    public Address() {}
    
    public Address(String street, String city, String state, String zipCode, String country) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.country = country;
    }
    
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
}
```

#### OrderItem Model
```java
package com.learning.ecommerce.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
    
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    
    private Integer quantity;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal unitPrice;
    
    public OrderItem() {}
    
    public OrderItem(Product product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = product.getPrice();
    }
    
    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
}
```

#### ProductService
```java
package com.learning.ecommerce.service;

import com.learning.ecommerce.model.Product;
import com.learning.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    
    private final ProductRepository productRepository;
    
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    public Product createProduct(Product product) {
        if (product.getPrice() == null || product.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Product price must be positive");
        }
        return productRepository.save(product);
    }
    
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }
    
    public List<Product> findAllActive() {
        return productRepository.findByActiveTrue();
    }
    
    public List<Product> findByCategory(Product.ProductCategory category) {
        return productRepository.findByCategory(category);
    }
    
    public Optional<Product> findBySku(String sku) {
        return productRepository.findBySku(sku);
    }
    
    public Product updateStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        
        int newStock = product.getStockQuantity() + quantity;
        if (newStock < 0) {
            throw new IllegalArgumentException("Insufficient stock");
        }
        
        product.setStockQuantity(newStock);
        return productRepository.save(product);
    }
    
    public boolean isAvailable(Long productId, int quantity) {
        return productRepository.findById(productId)
            .map(p -> p.getStockQuantity() >= quantity && p.isActive())
            .orElse(false);
    }
}
```

#### OrderService
```java
package com.learning.ecommerce.service;

import com.learning.ecommerce.model.*;
import com.learning.ecommerce.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final PricingService pricingService;
    
    public OrderService(OrderRepository orderRepository, 
                        ProductService productService,
                        PricingService pricingService) {
        this.orderRepository = orderRepository;
        this.productService = productService;
        this.pricingService = pricingService;
    }
    
    @Transactional
    public Order createOrder(Customer customer, List<OrderItemRequest> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }
        
        Order order = new Order();
        order.setOrderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setCustomer(customer);
        
        BigDecimal total = BigDecimal.ZERO;
        
        for (OrderItemRequest itemRequest : items) {
            Product product = productService.findById(itemRequest.productId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + itemRequest.productId()));
            
            if (!product.isActive()) {
                throw new IllegalArgumentException("Product is not active: " + product.getSku());
            }
            
            if (!productService.isAvailable(product.getId(), itemRequest.quantity())) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getSku());
            }
            
            BigDecimal itemPrice = pricingService.calculateItemPrice(product, itemRequest.quantity());
            OrderItem item = new OrderItem(product, itemRequest.quantity());
            item.setUnitPrice(itemPrice);
            order.addItem(item);
            
            total = total.add(item.getSubtotal());
        }
        
        order.setTotalAmount(total);
        return orderRepository.save(order);
    }
    
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }
    
    public Optional<Order> findByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }
    
    public List<Order> findByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }
    
    @Transactional
    public Order updateStatus(Long orderId, Order.OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        
        validateStatusTransition(order.getStatus(), newStatus);
        
        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        
        return orderRepository.save(order);
    }
    
    private void validateStatusTransition(Order.OrderStatus current, Order.OrderStatus next) {
        boolean valid = switch (current) {
            case PENDING -> next == Order.OrderStatus.CONFIRMED || next == Order.OrderStatus.CANCELLED;
            case CONFIRMED -> next == Order.OrderStatus.SHIPPED || next == Order.OrderStatus.CANCELLED;
            case SHIPPED -> next == Order.OrderStatus.DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };
        
        if (!valid) {
            throw new IllegalArgumentException("Invalid status transition from " + current + " to " + next);
        }
    }
    
    public record OrderItemRequest(Long productId, int quantity) {}
}
```

#### PricingService
```java
package com.learning.ecommerce.service;

import com.learning.ecommerce.model.Product;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PricingService {
    
    private static final BigDecimal BULK_DISCOUNT_THRESHOLD = new BigDecimal("100.00");
    private static final BigDecimal BULK_DISCOUNT_RATE = new BigDecimal("0.10");
    private static final BigDecimal VIP_DISCOUNT_RATE = new BigDecimal("0.15");
    
    public BigDecimal calculateItemPrice(Product product, int quantity) {
        BigDecimal basePrice = product.getPrice();
        
        BigDecimal discount = calculateDiscount(basePrice, quantity);
        
        return basePrice.subtract(discount).setScale(2, RoundingMode.HALF_UP);
    }
    
    public BigDecimal calculateDiscount(BigDecimal price, int quantity) {
        BigDecimal total = price.multiply(BigDecimal.valueOf(quantity));
        
        if (total.compareTo(BULK_DISCOUNT_THRESHOLD) >= 0) {
            return total.multiply(BULK_DISCOUNT_RATE);
        }
        
        return BigDecimal.ZERO;
    }
    
    public BigDecimal applyVipDiscount(BigDecimal amount) {
        return amount.multiply(BigDecimal.ONE.subtract(VIP_DISCOUNT_RATE))
            .setScale(2, RoundingMode.HALF_UP);
    }
    
    public BigDecimal calculateTax(BigDecimal amount, BigDecimal taxRate) {
        return amount.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
    }
    
    public BigDecimal calculateTotalWithTax(BigDecimal subtotal, BigDecimal taxRate) {
        BigDecimal tax = calculateTax(subtotal, taxRate);
        return subtotal.add(tax);
    }
}
```

#### ProductServiceTest
```java
package com.learning.ecommerce;

import com.learning.ecommerce.model.Product;
import com.learning.ecommerce.repository.ProductRepository;
import com.learning.ecommerce.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    
    @Mock
    private ProductRepository productRepository;
    
    @InjectMocks
    private ProductService productService;
    
    private Product testProduct;
    
    @BeforeEach
    void setUp() {
        testProduct = new Product("SKU-001", "Test Product", new BigDecimal("29.99"), 100);
        testProduct.setId(1L);
        testProduct.setCategory(Product.ProductCategory.ELECTRONICS);
    }
    
    @Test
    @DisplayName("Should create product successfully")
    void shouldCreateProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        
        Product result = productService.createProduct(testProduct);
        
        assertThat(result).isNotNull();
        assertThat(result.getSku()).isEqualTo("SKU-001");
        verify(productRepository).save(testProduct);
    }
    
    @Test
    @DisplayName("Should throw exception when price is null")
    void shouldThrowExceptionWhenPriceIsNull() {
        Product product = new Product("SKU-002", "Test", null, 10);
        
        assertThatThrownBy(() -> productService.createProduct(product))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("price must be positive");
    }
    
    @Test
    @DisplayName("Should throw exception when price is zero")
    void shouldThrowExceptionWhenPriceIsZero() {
        Product product = new Product("SKU-002", "Test", BigDecimal.ZERO, 10);
        
        assertThatThrownBy(() -> productService.createProduct(product))
            .isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    @DisplayName("Should find product by id")
    void shouldFindProductById() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        
        Optional<Product> result = productService.findById(1L);
        
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Test Product");
    }
    
    @Test
    @DisplayName("Should return empty when product not found")
    void shouldReturnEmptyWhenProductNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());
        
        Optional<Product> result = productService.findById(999L);
        
        assertThat(result).isEmpty();
    }
    
    @Test
    @DisplayName("Should find all active products")
    void shouldFindAllActiveProducts() {
        when(productRepository.findByActiveTrue()).thenReturn(List.of(testProduct));
        
        List<Product> result = productService.findAllActive();
        
        assertThat(result).hasSize(1);
        assertThat(result).extracting(Product::getSku).contains("SKU-001");
    }
    
    @Test
    @DisplayName("Should update stock successfully")
    void shouldUpdateStockSuccessfully() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        
        Product result = productService.updateStock(1L, 10);
        
        assertThat(result.getStockQuantity()).isEqualTo(110);
        verify(productRepository).save(testProduct);
    }
    
    @Test
    @DisplayName("Should throw exception when updating stock results in negative")
    void shouldThrowExceptionWhenStockBecomesNegative() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        
        assertThatThrownBy(() -> productService.updateStock(1L, -200))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Insufficient stock");
    }
    
    @Test
    @DisplayName("Should check product availability")
    void shouldCheckProductAvailability() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        
        boolean result = productService.isAvailable(1L, 50);
        
        assertThat(result).isTrue();
    }
    
    @Test
    @DisplayName("Should return false when stock insufficient")
    void shouldReturnFalseWhenStockInsufficient() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        
        boolean result = productService.isAvailable(1L, 200);
        
        assertThat(result).isFalse();
    }
}
```

#### OrderServiceTest
```java
package com.learning.ecommerce;

import com.learning.ecommerce.model.*;
import com.learning.ecommerce.repository.OrderRepository;
import com.learning.ecommerce.service.OrderService;
import com.learning.ecommerce.service.PricingService;
import com.learning.ecommerce.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    
    @Mock
    private OrderRepository orderRepository;
    
    @Mock
    private ProductService productService;
    
    @Mock
    private PricingService pricingService;
    
    @InjectMocks
    private OrderService orderService;
    
    private Customer testCustomer;
    private Product testProduct;
    private Order testOrder;
    
    @BeforeEach
    void setUp() {
        testCustomer = new Customer("John Doe", "john@example.com");
        testCustomer.setId(1L);
        
        testProduct = new Product("SKU-001", "Test Product", new BigDecimal("29.99"), 100);
        testProduct.setId(1L);
        testProduct.setActive(true);
        
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setOrderNumber("ORD-TEST123");
        testOrder.setCustomer(testCustomer);
        testOrder.setStatus(Order.OrderStatus.PENDING);
        testOrder.setTotalAmount(new BigDecimal("29.99"));
    }
    
    @Test
    @DisplayName("Should create order successfully")
    void shouldCreateOrderSuccessfully() {
        when(productService.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productService.isAvailable(1L, 2)).thenReturn(true);
        when(pricingService.calculateItemPrice(any(Product.class), anyInt()))
            .thenReturn(new BigDecimal("29.99"));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        
        OrderService.OrderItemRequest itemRequest = new OrderService.OrderItemRequest(1L, 2);
        Order result = orderService.createOrder(testCustomer, List.of(itemRequest));
        
        assertThat(result).isNotNull();
        assertThat(result.getOrderNumber()).startsWith("ORD-");
        verify(orderRepository).save(any(Order.class));
    }
    
    @Test
    @DisplayName("Should throw exception when order has no items")
    void shouldThrowExceptionWhenOrderHasNoItems() {
        assertThatThrownBy(() -> orderService.createOrder(testCustomer, List.of()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("at least one item");
    }
    
    @Test
    @DisplayName("Should throw exception when product not found")
    void shouldThrowExceptionWhenProductNotFound() {
        when(productService.findById(999L)).thenReturn(Optional.empty());
        
        OrderService.OrderItemRequest itemRequest = new OrderService.OrderItemRequest(999L, 1);
        
        assertThatThrownBy(() -> orderService.createOrder(testCustomer, List.of(itemRequest)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Product not found");
    }
    
    @Test
    @DisplayName("Should throw exception when product is inactive")
    void shouldThrowExceptionWhenProductIsInactive() {
        testProduct.setActive(false);
        when(productService.findById(1L)).thenReturn(Optional.of(testProduct));
        
        OrderService.OrderItemRequest itemRequest = new OrderService.OrderItemRequest(1L, 1);
        
        assertThatThrownBy(() -> orderService.createOrder(testCustomer, List.of(itemRequest)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("not active");
    }
    
    @Test
    @DisplayName("Should update order status from PENDING to CONFIRMED")
    void shouldUpdateOrderStatusFromPendingToConfirmed() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        
        Order result = orderService.updateStatus(1L, Order.OrderStatus.CONFIRMED);
        
        assertThat(result.getStatus()).isEqualTo(Order.OrderStatus.CONFIRMED);
        verify(orderRepository).save(testOrder);
    }
    
    @Test
    @DisplayName("Should throw exception for invalid status transition")
    void shouldThrowExceptionForInvalidStatusTransition() {
        testOrder.setStatus(Order.OrderStatus.DELIVERED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        
        assertThatThrownBy(() -> orderService.updateStatus(1L, Order.OrderStatus.PENDING))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid status transition");
    }
    
    @Test
    @DisplayName("Should throw exception when order not found")
    void shouldThrowExceptionWhenOrderNotFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());
        
        assertThatThrownBy(() -> orderService.updateStatus(999L, Order.OrderStatus.CONFIRMED))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Order not found");
    }
}
```

#### PricingServiceTest
```java
package com.learning.ecommerce;

import com.learning.ecommerce.model.Product;
import com.learning.ecommerce.service.PricingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PricingServiceTest {
    
    @InjectMocks
    private PricingService pricingService;
    
    private Product testProduct;
    
    @BeforeEach
    void setUp() {
        testProduct = new Product("SKU-001", "Test Product", new BigDecimal("10.00"), 100);
    }
    
    @Test
    @DisplayName("Should calculate regular price without discount")
    void shouldCalculateRegularPriceWithoutDiscount() {
        BigDecimal result = pricingService.calculateItemPrice(testProduct, 1);
        
        assertThat(result).isEqualByComparingTo(new BigDecimal("10.00"));
    }
    
    @Test
    @DisplayName("Should apply bulk discount for orders over 100")
    void shouldApplyBulkDiscountForOrdersOver100() {
        testProduct.setPrice(new BigDecimal("10.00"));
        
        BigDecimal result = pricingService.calculateItemPrice(testProduct, 15);
        
        assertThat(result).isLessThan(new BigDecimal("150.00"));
    }
    
    @Test
    @DisplayName("Should apply VIP discount")
    void shouldApplyVipDiscount() {
        BigDecimal result = pricingService.applyVipDiscount(new BigDecimal("100.00"));
        
        assertThat(result).isEqualByComparingTo(new BigDecimal("85.00"));
    }
    
    @Test
    @DisplayName("Should calculate tax correctly")
    void shouldCalculateTaxCorrectly() {
        BigDecimal taxRate = new BigDecimal("0.10");
        
        BigDecimal result = pricingService.calculateTax(new BigDecimal("100.00"), taxRate);
        
        assertThat(result).isEqualByComparingTo(new BigDecimal("10.00"));
    }
    
    @Test
    @DisplayName("Should calculate total with tax")
    void shouldCalculateTotalWithTax() {
        BigDecimal taxRate = new BigDecimal("0.08");
        
        BigDecimal result = pricingService.calculateTotalWithTax(new BigDecimal("100.00"), taxRate);
        
        assertThat(result).isEqualByComparingTo(new BigDecimal("108.00"));
    }
    
    @Test
    @DisplayName("Should calculate discount correctly")
    void shouldCalculateDiscountCorrectly() {
        BigDecimal result = pricingService.calculateDiscount(new BigDecimal("10.00"), 15);
        
        assertThat(result).isEqualByComparingTo(new BigDecimal("15.00"));
    }
}
```

### Build and Run Instructions
```bash
# Build the project
cd testing-demo
mvn clean compile

# Run all tests
mvn test

# Run tests with coverage report
mvn test jacoco:report

# Run only unit tests
mvn test -Dtest=ProductServiceTest

# Run tests with verbose output
mvn test -Dsurefire.useFile=false

# Run tests and skip integration tests
mvn test -DskipIntegrationTests=true

# View test results
# Open: target/site/jacoco/index.html for coverage report
# Open: target/surefire-reports/ for test results
```

---

## Real-World Project: Full Stack Testing Framework (8+ hours)

### Overview
Build a comprehensive testing framework for a Spring Boot microservice architecture including unit tests, integration tests, contract tests, and end-to-end tests with testcontainers.

### Architecture
- **Application Under Test**: E-commerce microservices (order, product, customer)
- **Testing Layers**: Unit, Integration, Contract, E2E
- **Test Infrastructure**: Testcontainers for databases and message brokers
- **CI Integration**: Test reports and coverage analysis

### Project Structure
```
testing-framework/
├── pom.xml
├── src/test/java/com/learning/
│   ├── TestingFrameworkApplication.java
│   ├── unit/
│   │   ├── service/
│   │   ├── controller/
│   │   └── repository/
│   ├── integration/
│   │   ├── DatabaseIntegrationTest.java
│   │   ├── KafkaIntegrationTest.java
│   │   └── RestAssuredIntegrationTest.java
│   ├── contract/
│   │   └── consumer/
│   ├── e2e/
│   │   └── FullFlowTest.java
│   └── testutil/
│       ├── TestDataFactory.java
│       └── IntegrationTestBase.java
├── src/test/resources/
│   ├── application-test.yml
│   └── contracts/
└── docker-compose.yml
```

### Implementation

#### Main pom.xml with test dependencies
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>testing-framework</artifactId>
    <version>1.0.0</version>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <properties>
        <java.version>21</java.version>
        <testcontainers.version>1.19.0</testcontainers.version>
        <rest-assured.version>5.3.0</rest-assured.version>
        <spring-cloud-contract.version>4.0.4</spring-cloud-contract.version>
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
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka</artifactId>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        
        <!-- Testing Dependencies -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka-test</artifactId>
            <scope>test</scope>
        </dependency>
        
        <!-- Testcontainers -->
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
            <artifactId>kafka</artifactId>
            <version>${testcontainers.version}</version>
            <scope>test</scope>
        </dependency>
        
        <!-- REST Assured -->
        <dependency>
            <groupId>io.rest-assured</groupId>
            <artifactId>rest-assured</artifactId>
            <version>${rest-assured.version}</version>
            <scope>test</scope>
        </dependency>
        
        <!-- AssertJ -->
        <dependency>
            <groupId>org.assertj</groupId>
            <artifactId>assertj-core</artifactId>
            <version>3.24.2</version>
            <scope>test</scope>
        </dependency>
        
        <!-- JSON assertion -->
        <dependency>
            <groupId>com.jayway.jsonpath</groupId>
            <artifactId>json-path-assert</artifactId>
            <version>2.8.0</version>
            <scope>test</scope>
        </dependency>
        
        <!-- H2 for unit tests -->
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <configuration>
                    <includes>
                        <include>**/*Test.java</include>
                        <include>**/*Tests.java</include>
                    </includes>
                    <excludes>
                        <exclude>**/*IntegrationTest.java</exclude>
                        <exclude>**/*E2ETest.java</exclude>
                    </excludes>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-failsafe-plugin</artifactId>
                <executions>
                    <execution>
                        <goals>
                            <goal>integration-test</goal>
                            <goal>verify</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <includes>
                        <include>**/*IntegrationTest.java</include>
                        <include>**/*E2ETest.java</include>
                    </includes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

#### Integration Test Base with Testcontainers
```java
package com.learning.testutil;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class IntegrationTestBase {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");
    
    @Container
    static KafkaContainer kafka = new KafkaContainer("confluentinc/cp-kafka:7.5.0");
    
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }
}
```

#### Test Data Factory
```java
package com.learning.testutil;

import com.learning.ecommerce.model.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public class TestDataFactory {
    
    public static Product createProduct(String sku, BigDecimal price, int stock) {
        Product product = new Product(sku, "Test Product " + sku, price, stock);
        product.setCategory(Product.ProductCategory.ELECTRONICS);
        product.setActive(true);
        return product;
    }
    
    public static Customer createCustomer(String email) {
        Customer customer = new Customer("Test Customer", email);
        customer.setPhone("+1234567890");
        Address address = new Address("123 Test St", "Test City", "TS", "12345", "US");
        customer.setAddress(address);
        return customer;
    }
    
    public static Order createOrder(Customer customer, List<Product> products) {
        Order order = new Order();
        order.setOrderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setCustomer(customer);
        
        BigDecimal total = BigDecimal.ZERO;
        for (Product product : products) {
            OrderItem item = new OrderItem(product, 1);
            order.addItem(item);
            total = total.add(product.getPrice());
        }
        
        order.setTotalAmount(total);
        return order;
    }
    
    public static List<Product> createProducts(int count) {
        return IntStream.range(0, count)
            .mapToObj(i -> createProduct("SKU-" + i, 
                BigDecimal.valueOf(10.0 * (i + 1)), 
                100))
            .toList();
    }
    
    public static Order createSampleOrder() {
        Customer customer = createCustomer("test@example.com");
        Product product = createProduct("SKU-001", new BigDecimal("29.99"), 50);
        return createOrder(customer, List.of(product));
    }
}
```

#### Database Integration Test
```java
package com.learning.integration;

import com.learning.ecommerce.model.Product;
import com.learning.ecommerce.repository.ProductRepository;
import com.learning.testutil.IntegrationTestBase;
import com.learning.testutil.TestDataFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class DatabaseIntegrationTest extends IntegrationTestBase {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Test
    @DisplayName("Should save and retrieve product")
    void shouldSaveAndRetrieveProduct() {
        Product product = TestDataFactory.createProduct("SKU-TEST", new BigDecimal("19.99"), 50);
        
        Product saved = productRepository.save(product);
        
        assertThat(saved.getId()).isNotNull();
        
        Optional<Product> retrieved = productRepository.findById(saved.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getSku()).isEqualTo("SKU-TEST");
    }
    
    @Test
    @DisplayName("Should find products by category")
    void shouldFindProductsByCategory() {
        Product product1 = TestDataFactory.createProduct("SKU-001", new BigDecimal("10.00"), 10);
        product1.setCategory(Product.ProductCategory.ELECTRONICS);
        
        Product product2 = TestDataFactory.createProduct("SKU-002", new BigDecimal("20.00"), 20);
        product2.setCategory(Product.ProductCategory.BOOKS);
        
        productRepository.save(product1);
        productRepository.save(product2);
        
        List<Product> electronics = productRepository.findByCategory(Product.ProductCategory.ELECTRONICS);
        
        assertThat(electronics).hasSize(1);
        assertThat(electronics.get(0).getSku()).isEqualTo("SKU-001");
    }
    
    @Test
    @DisplayName("Should find all active products")
    void shouldFindAllActiveProducts() {
        Product activeProduct = TestDataFactory.createProduct("SKU-ACTIVE", new BigDecimal("15.00"), 30);
        activeProduct.setActive(true);
        
        Product inactiveProduct = TestDataFactory.createProduct("SKU-INACTIVE", new BigDecimal("25.00"), 40);
        inactiveProduct.setActive(false);
        
        productRepository.save(activeProduct);
        productRepository.save(inactiveProduct);
        
        List<Product> activeProducts = productRepository.findByActiveTrue();
        
        assertThat(activeProducts).hasSize(1);
        assertThat(activeProducts.get(0).getSku()).isEqualTo("SKU-ACTIVE");
    }
    
    @Test
    @DisplayName("Should update product stock")
    void shouldUpdateProductStock() {
        Product product = TestDataFactory.createProduct("SKU-UPDATE", new BigDecimal("50.00"), 100);
        Product saved = productRepository.save(product);
        
        saved.setStockQuantity(50);
        productRepository.save(saved);
        
        Optional<Product> updated = productRepository.findById(saved.getId());
        
        assertThat(updated).isPresent();
        assertThat(updated.get().getStockQuantity()).isEqualTo(50);
    }
    
    @Test
    @DisplayName("Should delete product")
    void shouldDeleteProduct() {
        Product product = TestDataFactory.createProduct("SKU-DELETE", new BigDecimal("5.00"), 10);
        Product saved = productRepository.save(product);
        
        productRepository.delete(saved);
        
        Optional<Product> deleted = productRepository.findById(saved.getId());
        assertThat(deleted).isEmpty();
    }
}
```

#### REST API Integration Test with RestAssured
```java
package com.learning.integration;

import com.learning.ecommerce.model.Product;
import com.learning.ecommerce.repository.ProductRepository;
import com.learning.testutil.IntegrationTestBase;
import com.learning.testutil.TestDataFactory;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.math.BigDecimal;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;

class RestAssuredIntegrationTest extends IntegrationTestBase {
    
    @LocalServerPort
    private int port;
    
    @Autowired
    private ProductRepository productRepository;
    
    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        productRepository.deleteAll();
    }
    
    @Test
    @DisplayName("Should create product via REST API")
    void shouldCreateProductViaRestApi() {
        String requestBody = """
            {
                "sku": "SKU-API-001",
                "name": "API Test Product",
                "price": 29.99,
                "stockQuantity": 100,
                "category": "ELECTRONICS"
            }
            """;
        
        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/api/products")
        .then()
            .statusCode(201)
            .body("sku", equalTo("SKU-API-001"))
            .body("name", equalTo("API Test Product"))
            .body("price", equalTo(29.99f));
    }
    
    @Test
    @DisplayName("Should get all products")
    void shouldGetAllProducts() {
        Product product1 = TestDataFactory.createProduct("SKU-001", new BigDecimal("10.00"), 10);
        Product product2 = TestDataFactory.createProduct("SKU-002", new BigDecimal("20.00"), 20);
        productRepository.save(product1);
        productRepository.save(product2);
        
        given()
        .when()
            .get("/api/products")
        .then()
            .statusCode(200)
            .body("size()", equalTo(2));
    }
    
    @Test
    @DisplayName("Should get product by ID")
    void shouldGetProductById() {
        Product product = TestDataFactory.createProduct("SKU-GET", new BigDecimal("15.00"), 50);
        Product saved = productRepository.save(product);
        
        given()
        .when()
            .get("/api/products/" + saved.getId())
        .then()
            .statusCode(200)
            .body("sku", equalTo("SKU-GET"));
    }
    
    @Test
    @DisplayName("Should return 404 for non-existent product")
    void shouldReturn404ForNonExistentProduct() {
        given()
        .when()
            .get("/api/products/99999")
        .then()
            .statusCode(404);
    }
    
    @Test
    @DisplayName("Should update product")
    void shouldUpdateProduct() {
        Product product = TestDataFactory.createProduct("SKU-UPDATE", new BigDecimal("10.00"), 10);
        Product saved = productRepository.save(product);
        
        String updateBody = """
            {
                "name": "Updated Name",
                "price": 15.99,
                "stockQuantity": 20
            }
            """;
        
        given()
            .contentType(ContentType.JSON)
            .body(updateBody)
        .when()
            .put("/api/products/" + saved.getId())
        .then()
            .statusCode(200)
            .body("name", equalTo("Updated Name"))
            .body("price", equalTo(15.99f));
    }
    
    @Test
    @DisplayName("Should delete product")
    void shouldDeleteProduct() {
        Product product = TestDataFactory.createProduct("SKU-DELETE", new BigDecimal("5.00"), 10);
        Product saved = productRepository.save(product);
        
        given()
        .when()
            .delete("/api/products/" + saved.getId())
        .then()
            .statusCode(204);
        
        assertThat(productRepository.findById(saved.getId())).isEmpty();
    }
    
    @Test
    @DisplayName("Should validate request body")
    void shouldValidateRequestBody() {
        String invalidBody = """
            {
                "sku": "",
                "name": "",
                "price": -10
            }
            """;
        
        given()
            .contentType(ContentType.JSON)
            .body(invalidBody)
        .when()
            .post("/api/products")
        .then()
            .statusCode(400);
    }
}
```

#### Kafka Integration Test
```java
package com.learning.integration;

import com.learning.testutil.IntegrationTestBase;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.EmbeddedKafkaBroker;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;

class KafkaIntegrationTest extends IntegrationTestBase {
    
    @Test
    @DisplayName("Should send and receive message via Kafka")
    void shouldSendAndReceiveMessageViaKafka() throws Exception {
        String topic = "test-topic";
        String message = "Hello Kafka Testcontainers";
        
        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, 
            kafka.getBootstrapServers());
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, 
            StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, 
            StringSerializer.class);
        
        DefaultKafkaProducerFactory<String, String> producerFactory = 
            new DefaultKafkaProducerFactory<>(producerProps);
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(producerFactory);
        
        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, 
            kafka.getBootstrapServers());
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, 
            StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, 
            StringDeserializer.class);
        
        DefaultKafkaConsumerFactory<String, String> consumerFactory = 
            new DefaultKafkaConsumerFactory<>(consumerProps);
        
        BlockingQueue<ConsumerRecord<String, String>> records = new LinkedBlockingQueue<>();
        ContainerProperties containerProperties = new ContainerProperties(topic);
        containerProperties.setMessageListener((MessageListener<String, String>) record -> {
            records.offer(record);
        });
        
        KafkaMessageListenerContainer<String, String> container = 
            new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        container.start();
        
        kafkaTemplate.send(topic, message);
        
        ConsumerRecord<String, String> received = records.poll(10, TimeUnit.SECONDS);
        
        assertThat(received).isNotNull();
        assertThat(received.value()).isEqualTo(message);
        
        container.stop();
        producerFactory.destroy();
    }
    
    @Test
    @DisplayName("Should handle multiple messages")
    void shouldHandleMultipleMessages() throws Exception {
        String topic = "multi-topic";
        int messageCount = 5;
        
        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, 
            kafka.getBootstrapServers());
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, 
            StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, 
            StringSerializer.class);
        
        DefaultKafkaProducerFactory<String, String> producerFactory = 
            new DefaultKafkaProducerFactory<>(producerProps);
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(producerFactory);
        
        for (int i = 0; i < messageCount; i++) {
            kafkaTemplate.send(topic, "Message-" + i);
        }
        
        Thread.sleep(1000);
        
        producerFactory.destroy();
    }
}
```

#### End-to-End Test
```java
package com.learning.e2e;

import com.learning.ecommerce.model.Customer;
import com.learning.ecommerce.model.Order;
import com.learning.ecommerce.model.Product;
import com.learning.ecommerce.repository.CustomerRepository;
import com.learning.ecommerce.repository.OrderRepository;
import com.learning.ecommerce.repository.ProductRepository;
import com.learning.testutil.IntegrationTestBase;
import com.learning.testutil.TestDataFactory;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.*;

class FullFlowTest extends IntegrationTestBase {
    
    @LocalServerPort
    private int port;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        orderRepository.deleteAll();
        customerRepository.deleteAll();
        productRepository.deleteAll();
    }
    
    @Test
    @DisplayName("Should complete full order flow")
    void shouldCompleteFullOrderFlow() {
        Product product = TestDataFactory.createProduct("SKU-E2E-001", new BigDecimal("99.99"), 50);
        product = productRepository.save(product);
        
        Customer customer = TestDataFactory.createCustomer("e2e@example.com");
        customer = customerRepository.save(customer);
        
        String orderRequest = """
            {
                "customerId": """ + customer.getId() + """,
                "items": [
                    {
                        "productId": """ + product.getId() + """,
                        "quantity": 2
                    }
                ]
            }
            """;
        
        String orderId = given()
            .contentType(ContentType.JSON)
            .body(orderRequest)
        .when()
            .post("/api/orders")
        .then()
            .statusCode(200)
            .extract()
            .path("orderId");
        
        assertThat(orderId).isNotNull();
        
        given()
        .when()
            .get("/api/orders/" + orderId)
        .then()
            .statusCode(200)
            .body("status", equalTo("PENDING"));
        
        given()
        .when()
            .put("/api/orders/" + orderId + "/confirm")
        .then()
            .statusCode(200)
            .body("status", equalTo("CONFIRMED"));
        
        given()
        .when()
            .get("/api/orders/" + orderId)
        .then()
            .statusCode(200)
            .body("status", equalTo("CONFIRMED"));
    }
    
    @Test
    @DisplayName("Should handle order cancellation")
    void shouldHandleOrderCancellation() {
        Product product = TestDataFactory.createProduct("SKU-E2E-002", new BigDecimal("49.99"), 30);
        product = productRepository.save(product);
        
        Customer customer = TestDataFactory.createCustomer("cancel@example.com");
        customer = customerRepository.save(customer);
        
        String orderRequest = """
            {
                "customerId": """ + customer.getId() + """,
                "items": [
                    {
                        "productId": """ + product.getId() + """,
                        "quantity": 1
                    }
                ]
            }
            """;
        
        String orderId = given()
            .contentType(ContentType.JSON)
            .body(orderRequest)
        .when()
            .post("/api/orders")
        .then()
            .statusCode(200)
            .extract()
            .path("orderId");
        
        given()
        .when()
            .put("/api/orders/" + orderId + "/cancel")
        .then()
            .statusCode(200);
        
        given()
        .when()
            .get("/api/orders/" + orderId)
        .then()
            .statusCode(200)
            .body("status", equalTo("CANCELLED"));
    }
}
```

#### application-test.yml
```yaml
spring:
  profiles:
    active: test
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
  kafka:
    consumer:
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer

server:
  port: 0
```

### Build and Run Instructions
```bash
# Build the project
cd testing-framework
mvn clean compile

# Run unit tests only
mvn test

# Run integration tests
mvn verify

# Run all tests including E2E
mvn verify -P integration

# Run specific test class
mvn test -Dtest=ProductServiceTest

# Run tests with coverage
mvn test jacoco:report
mvn verify jacoco:report

# Run with debugging
mvn test -Dtest=ProductServiceTest -Dsurefire.useFile=false

# View test reports
# Unit tests: target/surefire-reports/index.html
# Integration tests: target/failsafe-reports/index.html
# Coverage: target/site/jacoco/index.html
```

### Learning Outcomes
- Write effective unit tests with JUnit 5 and Mockito
- Create integration tests with Testcontainers
- Implement REST API tests with RestAssured
- Test Kafka message-driven systems
- Build end-to-end test scenarios
- Configure test execution for different layers
- Generate and analyze test coverage reports