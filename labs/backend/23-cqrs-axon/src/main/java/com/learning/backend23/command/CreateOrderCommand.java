package com.learning.backend23.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;
import java.util.List;

public record CreateOrderCommand(
    @TargetAggregateIdentifier String orderId,
    String customerId,
    List<OrderItem> items
) {
    public record OrderItem(String productId, String productName, int quantity, double price) {}
}
