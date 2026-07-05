package com.arch.hexagonal;

public class Order {
    private final String id;
    private final String customerId;
    private final double amount;

    public Order(String id, String customerId, double amount) {
        this.id = id;
        this.customerId = customerId;
        this.amount = amount;
    }

    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public double getAmount() { return amount; }
}
