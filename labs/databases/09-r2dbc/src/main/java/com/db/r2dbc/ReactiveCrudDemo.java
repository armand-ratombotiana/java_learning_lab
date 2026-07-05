package com.db.r2dbc;

import io.r2dbc.spi.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Demonstrates R2DBC reactive CRUD operations.
 *
 * R2DBC (Reactive Relational Database Connectivity) enables
 * non-blocking database access using Project Reactor.
 *
 * Requires: H2 or PostgreSQL with R2DBC driver on classpath.
 */
public class ReactiveCrudDemo {

    static final String URL = "r2dbc:h2:mem:///reactivedb";

    public static void main(String[] args) {
        ConnectionFactory factory = ConnectionFactories.get(URL);

        Mono.from(factory.create())
            .flatMapMany(conn -> {
                System.out.println("=== Reactive CRUD with R2DBC ===\n");

                return conn.createStatement("""
                        CREATE TABLE IF NOT EXISTS products (
                            id    BIGINT AUTO_INCREMENT PRIMARY KEY,
                            name  VARCHAR(100),
                            price DECIMAL(10,2)
                        )
                        """)
                    .execute()
                    .flatMap(Result::getRowsUpdated)
                    .thenMany(
                        conn.createStatement("INSERT INTO products (name, price) VALUES ($1, $2)")
                            .bind("$1", "Reactive Mouse").bind("$2", 29.99)
                            .add()
                            .bind("$1", "Reactive Keyboard").bind("$2", 89.99)
                            .add()
                            .bind("$1", "Reactive Monitor").bind("$2", 199.99)
                            .execute()
                            .flatMap(Result::getRowsUpdated)
                    )
                    .thenMany(
                        conn.createStatement("SELECT id, name, price FROM products ORDER BY price DESC")
                            .execute()
                            .flatMap(result ->
                                result.map((row, meta) -> {
                                    long id = row.get("id", Long.class);
                                    String name = row.get("name", String.class);
                                    double price = row.get("price", Double.class);
                                    return String.format("  %d | %-18s | $%.2f", id, name, price);
                                })
                            )
                    )
                    .doOnNext(System.out::println)
                    .then(
                        conn.createStatement("UPDATE products SET price = $1 WHERE name = $2")
                            .bind("$1", 24.99).bind("$2", "Reactive Mouse")
                            .execute()
                            .flatMap(Result::getRowsUpdated)
                            .doOnNext(n -> System.out.println("\n  Updated " + n + " row(s)"))
                    )
                    .then(
                        conn.createStatement("DELETE FROM products WHERE price < $1")
                            .bind("$1", 50.00)
                            .execute()
                            .flatMap(Result::getRowsUpdated)
                            .doOnNext(n -> System.out.println("  Deleted " + n + " row(s)"))
                    )
                    .thenMany(
                        conn.createStatement("SELECT COUNT(*) AS cnt FROM products")
                            .execute()
                            .flatMap(r -> r.map((row, meta) ->
                                "  Remaining products: " + row.get("cnt", Long.class)))
                    )
                    .doOnNext(System.out::println)
                    .then(Mono.from(conn.close()));
            })
            .blockLast();
    }
}
