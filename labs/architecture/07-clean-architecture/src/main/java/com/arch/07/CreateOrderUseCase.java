package com.arch.clean;

public class CreateOrderUseCase implements UseCase<CreateOrderRequest, OrderResponse> {
    private final Gateway<Order> orderGateway;

    public CreateOrderUseCase(Gateway<Order> orderGateway) {
        this.orderGateway = orderGateway;
    }

    public OrderResponse execute(CreateOrderRequest input) {
        Order order = new Order(input.getCustomerId(), input.getAmount());
        orderGateway.save(order);
        return new OrderResponse(order.getId(), order.getCustomerId(), order.getAmount());
    }
}
