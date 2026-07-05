package com.arch.clean;

public class OrderController {
    private final CreateOrderUseCase createOrderUseCase;

    public OrderController(CreateOrderUseCase createOrderUseCase) {
        this.createOrderUseCase = createOrderUseCase;
    }

    public OrderResponse createOrder(String customerId, double amount) {
        CreateOrderRequest request = new CreateOrderRequest(customerId, amount);
        return createOrderUseCase.execute(request);
    }
}
