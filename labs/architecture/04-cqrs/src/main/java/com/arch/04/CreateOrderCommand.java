package com.arch.cqrs;

public class CreateOrderCommand implements Command {
    private final String customerId;

    public CreateOrderCommand(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerId() { return customerId; }
}
