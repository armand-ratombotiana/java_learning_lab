package com.learning.lab.module69;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 69: R2DBC Lab ===\n");

        System.out.println("1. R2DBC Overview:");
        System.out.println("   - Reactive Relational Database Connectivity");
        System.out.println("   - Non-blocking database driver");
        System.out.println("   - Back-pressure support");
        System.out.println("   - Works with reactive streams");

        System.out.println("\n2. R2DBC SPI:");
        System.out.println("   - ConnectionFactory");
        System.out.println("   - Connection, Statement, Row");
        System.out.println("   - Publisher<Result> for queries");
        System.out.println("   - Database drivers: PostgreSQL, MySQL, H2");

        System.out.println("\n3. Spring Data R2DBC:");
        System.out.println("   - R2dbcRepository interface");
        System.out.println("   - @Table, @Column annotations");
        System.out.println("   - Reactive CRUD operations");
        System.out.println("   - Query derivation");

        System.out.println("\n4. Transaction Management:");
        System.out.println("   - ReactiveTransactionManager");
        System.out.println("   - @Transactional (reactive)");
        System.out.println("   - Programmatic transactions");
        System.out.println("   - TransactionalOperator");

        System.out.println("\n5. Reactive Patterns:");
        System.out.println("   - Flux for multiple results");
        System.out.println("   - Mono for single result");
        System.out.println("   - Non-blocking I/O");
        System.out.println("   - Backpressure handling");

        System.out.println("\n6. Performance:");
        System.out.println("   - Connection pooling: r2dbc-pool");
        System.out.println("   - Statement caching");
        System.out.println("   - Batch operations");
        System.out.println("   - Index optimization");

        System.out.println("\n7. Testing:");
        System.out.println("   - Testcontainers for R2DBC");
        System.out.println("   - Embedded H2 R2DBC");
        System.out.println("   - Repository integration tests");

        System.out.println("\n=== R2DBC Lab Complete ===");
    }
}