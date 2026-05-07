package com.learning.ddd;

public class DDDLab {

    public static void main(String[] args) {
        System.out.println("=== Domain-Driven Design Lab ===\n");

        System.out.println("1. Building Blocks:");
        System.out.println("   - Entities: Objects with identity (User, Order)");
        System.out.println("   - Value Objects: Immutable objects (Money, Address)");
        System.out.println("   - Aggregates: Cluster of related objects (Order + OrderLines)");
        System.out.println("   - Repositories: Collection-like access to aggregates");
        System.out.println("   - Domain Services: Business logic not belonging to entities");
        System.out.println("   - Factories: Complex object creation");

        System.out.println("\n2. Example:");
        Order order = new Order("ORD-001");
        order.addLine("PROD-1", 2, 25.0);
        order.addLine("PROD-2", 1, 50.0);
        order.submit();
        System.out.println("   Order " + order.id() + " total: $" + order.totalAmount());
        System.out.println("   Status: " + order.status());

        System.out.println("\n=== Domain-Driven Design Lab Complete ===");
    }

    record OrderId(String value) {}
    record ProductId(String value) {}

    static class Order {
        private final OrderId id;
        private OrderStatus status;
        private double totalAmount;

        Order(String id) {
            this.id = new OrderId(id);
            this.status = OrderStatus.CREATED;
            this.totalAmount = 0;
        }

        void addLine(String product, int quantity, double price) {
            if (status != OrderStatus.CREATED) throw new IllegalStateException("Cannot modify " + status);
            totalAmount += quantity * price;
        }

        void submit() {
            if (status != OrderStatus.CREATED) throw new IllegalStateException("Cannot submit " + status);
            status = OrderStatus.SUBMITTED;
        }

        OrderId id() { return id; }
        OrderStatus status() { return status; }
        double totalAmount() { return totalAmount; }
    }

    enum OrderStatus { CREATED, SUBMITTED, PAID, SHIPPED, DELIVERED }
}