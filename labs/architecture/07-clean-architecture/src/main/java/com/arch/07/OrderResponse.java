package com.arch.clean;

public class OrderResponse {
    private final String orderId;
    private final String customerId;
    private final double amount;

    public OrderResponse(String orderId, String customerId, double amount) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.amount = amount;
    }

    public String getOrderId() { return orderId; }
    public String getCustomerId() { return customerId; }
    public double getAmount() { return amount; }
}
