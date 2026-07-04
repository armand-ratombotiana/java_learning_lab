# Step-by-Step Microservices Implementation

## Step 1: Create Service Skeleton
```bash
# Using Spring Initializr
curl https://start.spring.io/starter.zip \
  -d type=maven-project \
  -d language=java \
  -d bootVersion=3.2.0 \
  -d baseDir=order-service \
  -d dependencies=web,data-jpa,postgresql,cloud-eureka,cloud-feign,actuator \
  -o order-service.zip
```

## Step 2: Configure Service Discovery
```yaml
# application.yml
spring:
  application:
    name: order-service
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
```

## Step 3: Define Domain Models
```java
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String customerId;
    private String productId;
    private Integer quantity;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private BigDecimal totalAmount;
}
```

## Step 4: Create Repository
```java
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerId(String customerId);
    List<Order> findByStatus(OrderStatus status);
}
```

## Step 5: Implement Feign Client
```java
@FeignClient(name = "inventory-service")
public interface InventoryClient {
    @GetMapping("/api/inventory/{productId}/availability")
    InventoryStatus checkAvailability(
        @PathVariable String productId,
        @RequestParam Integer quantity);
}
```

## Step 6: Add Circuit Breaker
```java
@CircuitBreaker(name = "inventory", fallbackMethod = "fallback")
@Retry(name = "inventory")
public InventoryStatus checkWithResilience(String productId, int qty) {
    return inventoryClient.checkAvailability(productId, qty);
}
```

## Step 7: Containerize
```dockerfile
FROM eclipse-temurin:17-jre
COPY target/order-service.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## Step 8: Deploy with Docker Compose
```yaml
version: '3.8'
services:
  order-service:
    build: ./order-service
    ports:
      - "8081:8081"
    depends_on:
      - postgres
      - eureka-server
  eureka-server:
    image: springcloud/eureka-server
    ports:
      - "8761:8761"
```
