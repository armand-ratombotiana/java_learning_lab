# Clean Architecture Reference

## Module Structure
```
orders-service/
  src/main/java/com/company/orders/
    entity/
      Order.java
      LineItem.java
      Money.java
      OrderStatus.java
    usecase/
      CreateOrderUseCase.java
      GetOrderUseCase.java
      CancelOrderUseCase.java
      OrderRepository.java
      CreateOrderInteractor.java
      GetOrderInteractor.java
      CancelOrderInteractor.java
      CreateOrderInputData.java
      CreateOrderOutputData.java
      CreateOrderOutputBoundary.java
    adapter/
      controller/
        OrderController.java
        OrderRequestModel.java
      presenter/
        OrderPresenter.java
        OrderResponseModel.java
      gateway/
        JpaOrderRepository.java
        OrderEntity.java
        OrderMapper.java
  src/test/java/
    entity/
      OrderTest.java
    usecase/
      CreateOrderInteractorTest.java
    adapter/
      OrderControllerTest.java
```

## Layer Rules
| Layer | Can Import | Cannot Import |
|-------|-----------|---------------|
| Entity | java.* | Spring, JPA, Adapters |
| Use Case | Entity, java.* | Spring MVC, JPA, Adapters |
| Adapter | Entity, Use Case, Frameworks | - |
| Framework | All | - |

## Technology Decisions
| Concern | Technology | Layer |
|---------|-----------|-------|
| DI | Spring | Framework |
| HTTP | Spring MVC | Adapter |
| DB | JPA | Adapter |
| Mapping | MapStruct | Adapter |
| Architecture Test | ArchUnit | Test |
