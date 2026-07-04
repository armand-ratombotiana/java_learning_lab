# Clean Architecture Internals

## Package Structure
```
com.company.orders/
  entity/
    Order.java
    LineItem.java
    OrderStatus.java
  usecase/
    inbound/
      CreateOrderUseCase.java
      GetOrderUseCase.java
    outbound/
      OrderRepository.java
    interactor/
      CreateOrderInteractor.java
      GetOrderInteractor.java
    dto/
      CreateOrderInputData.java
      CreateOrderOutputData.java
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
```

## Use Case Boundaries
```java
// Input boundary
public interface CreateOrderUseCase {
    void execute(CreateOrderInputData input, CreateOrderOutputBoundary output);
}

// Output boundary
public interface CreateOrderOutputBoundary {
    void present(CreateOrderOutputData output);
    void presentError(String message);
}
```

## Presenter Pattern
```java
@Component
@RequestScope
public class OrderPresenter implements CreateOrderOutputBoundary {

    private OrderResponseModel response;
    private String error;

    @Override
    public void present(CreateOrderOutputData output) {
        this.response = OrderResponseModel.builder()
            .orderId(output.getOrderId())
            .status(output.getStatus())
            .total(output.getTotal())
            .build();
        this.error = null;
    }

    @Override
    public void presentError(String message) {
        this.error = message;
        this.response = null;
    }

    public OrderResponseModel getResponse() {
        if (error != null) {
            throw new OrderCreationException(error);
        }
        return response;
    }
}
```

## Dependency Injection
```java
@Configuration
public class CleanArchitectureConfig {

    @Bean
    public CreateOrderUseCase createOrderUseCase(
            OrderRepository orderRepository) {
        return new CreateOrderInteractor(orderRepository);
    }

    @Bean
    public OrderRepository orderRepository(
            SpringDataJpaOrderRepository jpaRepo,
            OrderMapper mapper) {
        return new JpaOrderRepository(jpaRepo, mapper);
    }
}
```
