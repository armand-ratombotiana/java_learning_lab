# Hexagonal Architecture Reference

## Package Structure
```
src/main/java/com/company/orders/
  domain/
    model/
      Order.java
      OrderStatus.java
    vo/
      OrderId.java
      CustomerId.java
    service/
      OrderDomainService.java
    port/
      inbound/
        CreateOrderUseCase.java
        GetOrderUseCase.java
      outbound/
        OrderRepository.java
  adapter/
    inbound/
      rest/
        OrderController.java
        OrderRequest.java
        OrderResponse.java
      messaging/
        OrderEventHandler.java
    outbound/
      persistence/
        JpaOrderRepository.java
        OrderEntity.java
        OrderMapper.java
      messaging/
        OrderEventPublisher.java
```

## Module Dependencies
| Module | Depends On | Description |
|--------|-----------|-------------|
| domain | - | Core business logic |
| adapter.inbound.rest | domain, spring-web | REST API |
| adapter.inbound.messaging | domain, spring-kafka | Event consumer |
| adapter.outbound.persistence | domain, spring-data-jpa | Database |
| adapter.outbound.messaging | domain, spring-kafka | Event publishing |

## Test Strategy
| Test Type | What It Tests | Infrastructure Needed |
|-----------|--------------|----------------------|
| Domain Unit Test | Business logic | None (in-memory) |
| Port Integration | Adapter correctness | Testcontainers |
| End-to-End | Full flow | All services |
| ArchUnit | Package dependencies | None |
