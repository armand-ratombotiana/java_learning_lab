package com.arch.hexagonal;

import java.util.List;

public interface OrderRepositoryPort {
    void save(Order order);
    Order findById(String id);
    List<Order> findAll();
}
