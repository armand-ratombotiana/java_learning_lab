package com.architecture.deep.lab04;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CleanArchLab {
    public static void main(String[] args) {
        var repository = new OrderRepositoryImpl();
        var presenter = new OrderPresenter();
        var useCase = new CreateOrderUseCase(repository, presenter);
        var controller = new OrderController(useCase);

        controller.createOrder("Alice", List.of("item-1", "item-2"), 2999);
        controller.createOrder("Bob", List.of("item-3"), 1500);

        System.out.println(presenter.getViewModel());
    }
}

record Order(String id, String customer, List<String> items, long totalCents, String status) {}

interface OrderRepository {
    void save(Order order);
    Optional<Order> findById(String id);
}

interface CreateOrderInputBoundary {
    void execute(CreateOrderRequest request);
}

interface CreateOrderOutputBoundary {
    void present(OrderResponse response);
}

record CreateOrderRequest(String customer, List<String> items, long totalCents) {}
record OrderResponse(String orderId, String customer, String status, String message) {}

class CreateOrderUseCase implements CreateOrderInputBoundary {
    private final OrderRepository repository;
    private final CreateOrderOutputBoundary presenter;

    CreateOrderUseCase(OrderRepository repository, CreateOrderOutputBoundary presenter) {
        this.repository = repository;
        this.presenter = presenter;
    }

    public void execute(CreateOrderRequest request) {
        var order = new Order(
            UUID.randomUUID().toString().substring(0, 8),
            request.customer(), request.items(), request.totalCents(), "CREATED"
        );
        repository.save(order);
        presenter.present(new OrderResponse(order.id(), order.customer(), order.status(),
            "Order created successfully for " + order.customer()));
    }
}

class OrderController {
    private final CreateOrderInputBoundary useCase;

    OrderController(CreateOrderInputBoundary useCase) { this.useCase = useCase; }

    void createOrder(String customer, List<String> items, long totalCents) {
        useCase.execute(new CreateOrderRequest(customer, items, totalCents));
    }
}

class OrderPresenter implements CreateOrderOutputBoundary {
    private final List<String> viewModel = new ArrayList<>();

    public void present(OrderResponse response) {
        viewModel.add("[" + response.status() + "] " + response.message());
    }

    String getViewModel() { return String.join("\n", viewModel); }
}

class OrderRepositoryImpl implements OrderRepository {
    private final Map<String, Order> store = new ConcurrentHashMap<>();

    public void save(Order order) { store.put(order.id(), order); }
    public Optional<Order> findById(String id) { return Optional.ofNullable(store.get(id)); }
}
