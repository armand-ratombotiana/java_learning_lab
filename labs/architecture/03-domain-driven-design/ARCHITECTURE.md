# DDD Architecture Reference

## Layered Architecture
```
+------------------------------------------+
|             Presentation Layer            |
|  Controllers, DTOs, View Models          |
+------------------------------------------+
|           Application Layer               |
|  Application Services, Commands, DTOs    |
+------------------------------------------+
|             Domain Layer                  |
|  Entities, Value Objects, Aggregates     |
|  Domain Events, Domain Services          |
|  Repository Interfaces                   |
+------------------------------------------+
|          Infrastructure Layer             |
|  Persistence, Messaging, External APIs   |
+------------------------------------------+
```

## Package Structure
```
com.company.order/
  application/
    service/OrderApplicationService.java
    command/PlaceOrderCommand.java
    dto/OrderResponse.java
  domain/
    model/Order.java
    model/LineItem.java
    model/OrderStatus.java
    vo/OrderId.java
    vo/Money.java
    event/OrderSubmitted.java
    service/DiscountCalculationService.java
    repository/OrderRepository.java
  infrastructure/
    persistence/JpaOrderRepository.java
    messaging/KafkaEventPublisher.java
    config/DataSourceConfig.java
```

## Bounded Context Dependencies
| Context | Depends On | Relationship |
|---------|-----------|--------------|
| Order Management | - | Core domain |
| Inventory | Order | Customer-Supplier |
| Shipping | Order | Conformist |
| Billing | Order, Inventory | Partnership |
| Notification | All | Open Host |
