package com.db.relational;

import java.util.Arrays;
import java.util.List;

/**
 * Demonstrates relational database schema design concepts:
 * entities, attributes, relationships (1:1, 1:M, M:M), and constraints.
 *
 * This file contains SQL DDL strings and Java records modeling a simple
 * e-commerce domain: Customer → Order → OrderItem → Product.
 */
public class SchemaDesign {

    // --- SQL DDL ---

    static String createCustomersTable() {
        return """
            CREATE TABLE customers (
                id          BIGSERIAL    PRIMARY KEY,
                email       VARCHAR(255) NOT NULL UNIQUE,
                full_name   VARCHAR(150) NOT NULL,
                created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
            );
            """;
    }

    static String createProductsTable() {
        return """
            CREATE TABLE products (
                id          BIGSERIAL    PRIMARY KEY,
                sku         VARCHAR(50)  NOT NULL UNIQUE,
                name        VARCHAR(255) NOT NULL,
                price       NUMERIC(10,2) NOT NULL CHECK (price >= 0),
                stock_qty   INTEGER      NOT NULL DEFAULT 0 CHECK (stock_qty >= 0)
            );
            """;
    }

    static String createOrdersTable() {
        return """
            CREATE TABLE orders (
                id           BIGSERIAL    PRIMARY KEY,
                customer_id  BIGINT       NOT NULL REFERENCES customers(id),
                order_date   TIMESTAMP    NOT NULL DEFAULT NOW(),
                status       VARCHAR(20)  NOT NULL DEFAULT 'PENDING'
                                      CHECK (status IN ('PENDING','PAID','SHIPPED','CANCELLED')),
                total        NUMERIC(12,2) NOT NULL CHECK (total >= 0)
            );
            CREATE INDEX idx_orders_customer ON orders(customer_id);
            """;
    }

    static String createOrderItemsTable() {
        return """
            CREATE TABLE order_items (
                id          BIGSERIAL    PRIMARY KEY,
                order_id    BIGINT       NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
                product_id  BIGINT       NOT NULL REFERENCES products(id),
                quantity    INTEGER      NOT NULL CHECK (quantity > 0),
                unit_price  NUMERIC(10,2) NOT NULL
            );
            CREATE INDEX idx_order_items_order ON order_items(order_id);
            """;
    }

    // --- Java Records (entity representation) ---

    record Customer(Long id, String email, String fullName) {}
    record Product(Long id, String sku, String name, double price, int stockQty) {}
    record Order(Long id, Long customerId, String status, double total) {}
    record OrderItem(Long id, Long orderId, Long productId, int quantity, double unitPrice) {}

    // --- Relationship explanation ---

    static List<String> relationships() {
        return Arrays.asList(
            "Customer 1──M Order      (one customer has many orders)",
            "Order    1──M OrderItem  (one order has many line items)",
            "Product  1──M OrderItem  (one product appears in many order items)",
            "OrderItem M──1 Product   (many items reference the same product)"
        );
    }

    public static void main(String[] args) {
        System.out.println("=== Schema Design: E-Commerce Domain ===\n");
        System.out.println(createCustomersTable());
        System.out.println(createProductsTable());
        System.out.println(createOrdersTable());
        System.out.println(createOrderItemsTable());

        System.out.println("--- Relationships ---");
        relationships().forEach(System.out::println);

        System.out.println("\n--- Sample Entities ---");
        Customer c = new Customer(1L, "alice@example.com", "Alice Smith");
        Product p = new Product(10L, "SKU-001", "Wireless Mouse", 29.99, 150);
        Order o = new Order(100L, 1L, "PAID", 59.98);
        OrderItem oi = new OrderItem(1000L, 100L, 10L, 2, 29.99);

        System.out.println(c);
        System.out.println(p);
        System.out.println(o);
        System.out.println(oi);
    }
}
