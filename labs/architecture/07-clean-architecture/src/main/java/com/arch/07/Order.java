package com.arch.clean;

import java.util.UUID;

public class Order {
    private final String id;
    private final String customerId;
    private final double amount;

    public Order(String customerId, double amount) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.customerId = customerId;
        this.amount = amount;
    }

    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public double getAmount() { return amount; }
}
