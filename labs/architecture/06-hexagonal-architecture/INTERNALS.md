# Hexagonal Architecture Internals

## Package Structure
```
com.company.orders/
  domain/
    model/Order.java, OrderItem.java, OrderStatus.java
    vo/OrderId.java, CustomerId.java, Money.java
    service/OrderDomainService.java
    port/
      inbound/CreateOrderUseCase.java
      inbound/GetOrderUseCase.java
      outbound/OrderRepository.java
      outbound/ProductRepository.java
    event/OrderCreatedEvent.java
  adapter/
    inbound/
      rest/OrderController.java
      messaging/OrderMessageHandler.java
      job/OrderBatchJob.java
    outbound/
      persistence/
        jpa/JpaOrderRepository.java
        entity/OrderEntity.java
        mapper/OrderMapper.java
      messaging/
        kafka/OrderEventPublisher.java
      client/
        rest/ProductServiceClient.java
```

## Port Implementation
```java
// Inbound ports are interfaces in domain layer
// They define how external world interacts with the core
package com.company.orders.domain.port.inbound;

public interface CancelOrderUseCase {
    void execute(CancelOrderCommand command);
}

// Outbound ports are interfaces in domain layer
// They define how the core interacts with external world
package com.company.orders.domain.port.outbound;

public interface OrderRepository {
    Optional<Order> findById(OrderId id);
    Order save(Order order);
    void delete(OrderId id);
}
```

## Dependency Injection
```java
@Configuration
public class HexagonalConfig {

    @Bean
    public CreateOrderUseCase createOrderUseCase(
            OrderRepository orderRepository,
            ProductRepository productRepository) {
        // Domain service depends on port interfaces
        return new CreateOrderService(orderRepository, productRepository);
    }

    @Bean
    public OrderRepository orderRepository(
            SpringDataJpaRepository jpa,
            OrderMapper mapper) {
        // Adapter implements port interface
        return new JpaOrderRepository(jpa, mapper);
    }
}
```

## Architectural Testing with ArchUnit
```java
@Test
void domainShouldNotDependOnAdapters() {
    JavaClasses classes = new ClassFileImporter()
        .importPackages("com.company.orders");

    ArchRule rule = classes().that()
        .resideInAPackage("..domain..")
        .should().onlyDependOnClassesThat()
        .resideInAnyPackage("..domain..", "java..", "org.springframework..");

    rule.check(classes);
}
```
