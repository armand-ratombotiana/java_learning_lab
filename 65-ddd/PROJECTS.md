# DDD Projects - Module 65

This module covers Domain-Driven Design, Bounded contexts, and Aggregates.

## Mini-Project: Order Management DDD (2-4 hours)

### Overview
Build an order management system using DDD patterns with entities, value objects, and repositories.

### Project Structure
```
ddd-orders/
├── src/main/java/com/learning/ddd/
│   ├── domain/
│   │   ├── order/
│   │   │   ├── Order.java
│   │   │   ├── OrderItem.java
│   │   │   ├── OrderStatus.java
│   │   │   └── OrderRepository.java
│   │   ├── valueobjects/
│   │   │   ├── Money.java
│   │   │   ├── Address.java
│   │   │   └── CustomerId.java
│   │   └── services/
│   │       └── PricingService.java
│   ├── application/
│   │   └── OrderService.java
│   └── infrastructure/
│       └── JpaOrderRepository.java
├── pom.xml
└── run.sh
```

### Implementation
```java
// pom.xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.learning</groupId>
    <artifactId>ddd-orders</artifactId>
    <version>1.0.0</version>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
</project>

// Money.java (Value Object)
package com.learning.ddd.valueobjects;

import java.math.BigDecimal;
import java.util.Currency;

public final class Money {
    private final BigDecimal amount;
    private final Currency currency;
    
    private Money(BigDecimal amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;
    }
    
    public static Money of(BigDecimal amount, Currency currency) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        return new Money(amount, currency);
    }
    
    public static Money usd(BigDecimal amount) {
        return of(amount, Currency.getInstance("USD"));
    }
    
    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot add different currencies");
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }
    
    public Money multiply(int quantity) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(quantity)), this.currency);
    }
    
    public BigDecimal getAmount() { return amount; }
    public Currency getCurrency() { return currency; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return amount.equals(money.amount) && currency.equals(money.currency);
    }
    
    @Override
    public int hashCode() {
        return amount.hashCode() + currency.hashCode();
    }
}

// Address.java
package com.learning.ddd.valueobjects;

public final class Address {
    private final String street;
    private final String city;
    private final String state;
    private final String zipCode;
    private final String country;
    
    private Address(String street, String city, String state, 
                   String zipCode, String country) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.country = country;
    }
    
    public static Address of(String street, String city, String state, 
                            String zipCode, String country) {
        return new Address(street, city, state, zipCode, country);
    }
    
    public String getStreet() { return street; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getZipCode() { return zipCode; }
    public String getCountry() { return country; }
}

// Order.java (Aggregate Root)
package com.learning.ddd.domain.order;

import com.learning.ddd.valueobjects.Money;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Order {
    private final OrderId id;
    private final CustomerId customerId;
    private List<OrderItem> items;
    private OrderStatus status;
    private Money total;
    private Instant createdAt;
    private Instant updatedAt;
    
    private Order(OrderId id, CustomerId customerId) {
        this.id = id;
        this.customerId = customerId;
        this.items = new ArrayList<>();
        this.status = OrderStatus.DRAFT;
        this.total = Money.usd(java.math.BigDecimal.ZERO);
        this.createdAt = Instant.now();
    }
    
    public static Order create(CustomerId customerId) {
        return new Order(OrderId.generate(), customerId);
    }
    
    public void addItem(ProductId productId, int quantity, Money unitPrice) {
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException("Cannot add items to non-draft order");
        }
        
        OrderItem item = new OrderItem(productId, quantity, unitPrice);
        items.add(item);
        recalculateTotal();
    }
    
    public void submit() {
        if (items.isEmpty()) {
            throw new IllegalStateException("Cannot submit empty order");
        }
        
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException("Order is not in DRAFT status");
        }
        
        status = OrderStatus.SUBMITTED;
        this.updatedAt = Instant.now();
    }
    
    public void confirm() {
        if (status != OrderStatus.SUBMITTED) {
            throw new IllegalStateException("Cannot confirm order not in SUBMITTED status");
        }
        
        status = OrderStatus.CONFIRMED;
        this.updatedAt = Instant.now();
    }
    
    public void cancel() {
        if (status == OrderStatus.SHIPPED || status == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot cancel shipped or delivered order");
        }
        
        status = OrderStatus.CANCELLED;
        this.updatedAt = Instant.now();
    }
    
    private void recalculateTotal() {
        total = items.stream()
            .map(item -> item.getUnitPrice().multiply(item.getQuantity()))
            .reduce(Money.usd(java.math.BigDecimal.ZERO), Money::add);
    }
    
    public OrderId getId() { return id; }
    public CustomerId getCustomerId() { return customerId; }
    public List<OrderItem> getItems() { return items; }
    public OrderStatus getStatus() { return status; }
    public Money getTotal() { return total; }
    public Instant getCreatedAt() { return createdAt; }
}

// OrderId.java
package com.learning.ddd.domain.order;

import java.util.UUID;

public final class OrderId {
    private final UUID value;
    
    private OrderId(UUID value) {
        this.value = value;
    }
    
    public static OrderId generate() {
        return new OrderId(UUID.randomUUID());
    }
    
    public static OrderId from(String uuid) {
        return new OrderId(UUID.fromString(uuid));
    }
    
    public UUID getValue() { return value; }
    
    @Override
    public String toString() { return value.toString(); }
}
```

---

## Real-World Project: E-Commerce Platform with DDD (8+ hours)

### Overview
Build a comprehensive e-commerce platform using DDD with multiple bounded contexts, domain events, and CQRS.

### Project Structure
```
ecommerce-ddd/
├── domain/
│   ├── order/
│   │   ├── aggregate/
│   │   ├── valueobjects/
│   │   ├── events/
│   │   └── repository/
│   ├── product/
│   │   ├── aggregate/
│   │   ├── valueobjects/
│   │   └── repository/
│   ├── inventory/
│   └── customer/
├── application/
│   ├── commands/
│   └── queries/
├── infrastructure/
│   ├── persistence/
│   └── messaging/
├── pom.xml
└── docker-compose.yml
```

### Implementation
```java
// Product.java (Aggregate)
package com.learning.ddd.domain.product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class Product {
    private ProductId id;
    private ProductName name;
    private ProductDescription description;
    private Money price;
    private StockQuantity stockQuantity;
    private ProductStatus status;
    private List<ProductCategory> categories;
    private ProductDimensions dimensions;
    
    private Product(ProductId id, ProductName name) {
        this.id = id;
        this.name = name;
        this.status = ProductStatus.ACTIVE;
    }
    
    public static Product create(String name, String description, 
                                  BigDecimal price, int stock) {
        Product product = new Product(ProductId.generate(), ProductName.of(name));
        product.description = ProductDescription.of(description);
        product.price = Money.usd(price);
        product.stockQuantity = StockQuantity.of(stock);
        return product;
    }
    
    public void adjustPrice(BigDecimal newPrice) {
        this.price = Money.usd(newPrice);
    }
    
    public void updateStock(int quantity) {
        this.stockQuantity = StockQuantity.of(quantity);
    }
    
    public boolean isAvailable() {
        return status == ProductStatus.ACTIVE && stockQuantity.getValue() > 0;
    }
    
    public boolean reserveStock(int quantity) {
        if (!isAvailable() || stockQuantity.getValue() < quantity) {
            return false;
        }
        
        stockQuantity = StockQuantity.of(stockQuantity.getValue() - quantity);
        return true;
    }
}

// Customer.java (Aggregate)
package com.learning.ddd.domain.customer;

import java.time.Instant;
import java.util.List;

public class Customer {
    private CustomerId id;
    private Email email;
    private CustomerName name;
    private Address shippingAddress;
    private Address billingAddress;
    private CustomerTier tier;
    private Instant createdAt;
    private Instant updatedAt;
    
    public void updateShippingAddress(Address address) {
        this.shippingAddress = address;
        this.updatedAt = Instant.now();
    }
    
    public void upgradeTier() {
        if (tier != CustomerTier.PLATINUM) {
            tier = CustomerTier.next(tier);
        }
    }
}

// Domain Events
package com.learning.ddd.domain.order.events;

import java.time.Instant;

public class OrderPlacedEvent {
    private final String orderId;
    private final String customerId;
    private final double totalAmount;
    private final Instant timestamp;
    
    public OrderPlacedEvent(String orderId, String customerId, 
                          double totalAmount, Instant timestamp) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.totalAmount = totalAmount;
        this.timestamp = timestamp;
    }
}

package com.learning.ddd.domain.product.events;

public class StockAdjustedEvent {
    private final String productId;
    private final int newQuantity;
    private final String reason;
    
    public StockAdjustedEvent(String productId, int newQuantity, String reason) {
        this.productId = productId;
        this.newQuantity = newQuantity;
        this.reason = reason;
    }
}

// Repository Interfaces
package com.learning.ddd.domain.order;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(OrderId id);
    List<Order> findByCustomerId(CustomerId customerId);
    List<Order> findByStatus(OrderStatus status);
}

// Application Service
package com.learning.ddd.application;

import org.springframework.stereotype.Service;

@Service
public class OrderApplicationService {
    
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final DomainEventPublisher eventPublisher;
    
    public OrderApplicationService(OrderRepository orderRepository,
                                   ProductRepository productRepository,
                                   DomainEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.eventPublisher = eventPublisher;
    }
    
    public String placeOrder(PlaceOrderCommand command) {
        Order order = Order.create(command.getCustomerId());
        
        for (OrderItemCommand item : command.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(item.getProductId()));
            
            product.reserveStock(item.getQuantity());
            productRepository.save(product);
            
            order.addItem(item.getProductId(), item.getQuantity(), product.getPrice());
        }
        
        order.submit();
        orderRepository.save(order);
        
        eventPublisher.publish(new OrderPlacedEvent(
            order.getId().toString(),
            command.getCustomerId().toString(),
            order.getTotal().getAmount().doubleValue(),
            Instant.now()
        ));
        
        return order.getId().toString();
    }
}
```

### Build and Run
```bash
mvn clean package -DskipTests
java -jar target/ecommerce-ddd-1.0.0.jar

# Run tests
mvn test
```

### Learning Outcomes
- Implement DDD aggregates and entities
- Create value objects
- Handle domain events
- Build repositories
- Design bounded contexts
- Apply domain services