package com.learning.backend23.event;

import java.time.Instant;
import java.util.List;

public record OrderCreatedEvent(
    String orderId,
    String customerId,
    List<OrderItem> items,
    Instant createdAt
) {
    public record OrderItem(String productId, String productName, int quantity, double price) {}
}
