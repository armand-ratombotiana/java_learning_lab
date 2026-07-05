package com.db.jdbcjpa;

import jakarta.persistence.*;
import java.util.List;

/**
 * Demonstrates JPA entity mappings with annotations.
 *
 * Shows: @Entity, @Table, @Id, @GeneratedValue, @Column,
 *        @OneToMany, @ManyToOne, @JoinColumn, @Enumerated.
 *
 * These are compile-ready entities for use with Hibernate or EclipseLink.
 */
public class JpaEntityMapping {

    // ==========================================
    // Entity: Customer
    // ==========================================
    @Entity
    @Table(name = "customers")
    static class Customer {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "full_name", nullable = false, length = 150)
        private String fullName;

        @Column(nullable = false, unique = true)
        private String email;

        @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        private List<Order> orders;

        public Customer() {}

        public Customer(String fullName, String email) {
            this.fullName = fullName;
            this.email = email;
        }

        public Long getId() { return id; }
        public String getFullName() { return fullName; }
        public String getEmail() { return email; }
        public List<Order> getOrders() { return orders; }

        @Override
        public String toString() {
            return "Customer{id=%d, name='%s', email='%s'}".formatted(id, fullName, email);
        }
    }

    // ==========================================
    // Entity: Order
    // ==========================================
    public enum OrderStatus { PENDING, PAID, SHIPPED, CANCELLED }

    @Entity
    @Table(name = "orders")
    static class Order {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "customer_id", nullable = false)
        private Customer customer;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private OrderStatus status = OrderStatus.PENDING;

        @Column(nullable = false)
        private Double total;

        public Order() {}

        public Order(Customer customer, Double total) {
            this.customer = customer;
            this.total = total;
        }

        public Long getId() { return id; }
        public Customer getCustomer() { return customer; }
        public OrderStatus getStatus() { return status; }
        public Double getTotal() { return total; }

        @Override
        public String toString() {
            return "Order{id=%d, customerId=%d, status=%s, total=%.2f}"
                .formatted(id, customer.getId(), status, total);
        }
    }

    // ==========================================
    // Demo: show entity metadata
    // ==========================================
    public static void main(String[] args) {
        System.out.println("=== JPA Entity Mappings ===\n");

        Customer customer = new Customer("Alice Smith", "alice@example.com");
        Order order = new Order(customer, 199.99);

        System.out.println("Entity classes defined:");
        System.out.println("  @Entity Customer → table 'customers'");
        System.out.println("  @Entity Order    → table 'orders'");
        System.out.println();
        System.out.println("Relationships:");
        System.out.println("  @OneToMany Customer.orders (mappedBy='customer')");
        System.out.println("  @ManyToOne  Order.customer (@JoinColumn='customer_id')");
        System.out.println("  CascadeType.ALL — persisting Customer also persists Orders");
        System.out.println("  FetchType.LAZY — orders loaded on demand");
        System.out.println();
        System.out.println("Enum mapping:");
        System.out.println("  @Enumerated(EnumType.STRING) → stores enum name in DB");
        System.out.println();
        System.out.println("Sample objects:");
        System.out.println("  " + customer);
        System.out.println("  " + order);
    }
}
