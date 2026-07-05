package com.arch.hexagonal;

import java.util.List;

public class OrderService {
    private final OrderRepositoryPort repository;

    public OrderService(OrderRepositoryPort repository) {
        this.repository = repository;
    }

    public void createOrder(Order order) {
        repository.save(order);
    }

    public Order getOrder(String id) {
        return repository.findById(id);
    }

    public List<Order> getAllOrders() {
        return repository.findAll();
    }
}
