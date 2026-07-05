package com.arch.cqrs;

public class GetOrderQuery implements Query {
    private final String orderId;

    public GetOrderQuery(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() { return orderId; }
}
