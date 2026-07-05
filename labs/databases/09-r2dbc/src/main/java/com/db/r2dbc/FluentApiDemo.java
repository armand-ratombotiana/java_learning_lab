package com.db.r2dbc;

import io.r2dbc.spi.*;
import reactor.core.publisher.Flux;

/**
 * Demonstrates R2DBC Fluent Statement API for dynamic query building.
 *
 * The Fluent API allows programmatic construction of queries
 * with bind parameter chaining and batch support.
 */
public class FluentApiDemo {

    public static void main(String[] args) {
        ConnectionFactory factory = ConnectionFactories.get("r2dbc:h2:mem:///fluentdb");

        Flux.usingWhen(
            factory.create(),
            conn -> {
                System.out.println("=== R2DBC Fluent Statement API ===\n");

                return conn.createStatement("""
                        CREATE TABLE IF NOT EXISTS logs (
                            id      BIGINT AUTO_INCREMENT PRIMARY KEY,
                            level   VARCHAR(10),
                            message VARCHAR(500),
                            source  VARCHAR(100)
                        )
                        """)
                    .execute()
                    .flatMap(Result::getRowsUpdated)
                    .thenMany(
                        // Batch insert using Fluent API
                        conn.createStatement("INSERT INTO logs (level, message, source) VALUES ($1, $2, $3)")
                            .bind("$1", "INFO").bind("$2", "Application started").bind("$3", "main")
                            .add()
                            .bind("$1", "WARN").bind("$2", "High memory usage detected")
                            .bind("$3", "monitor")
                            .add()
                            .bind("$1", "ERROR").bind("$2", "Connection pool exhausted")
                            .bind("$3", "db-pool")
                            .add()
                            .bind("$1", "INFO").bind("$2", "User login successful")
                            .bind("$3", "auth")
                            .execute()
                            .flatMap(Result::getRowsUpdated)
                            .reduce(0, Integer::sum)
                            .doOnNext(n -> System.out.println("  Batch inserted " + n + " log entries"))
                    )
                    .thenMany(
                        // Parameterized SELECT with filtering
                        conn.createStatement(
                                "SELECT id, level, message, source FROM logs " +
                                "WHERE level IN ($1, $2) ORDER BY id DESC LIMIT $3")
                            .bind("$1", "ERROR").bind("$2", "WARN").bind("$3", 5)
                            .execute()
                            .flatMap(result -> result.map((row, meta) -> {
                                long id = row.get("id", Long.class);
                                String level = row.get("level", String.class);
                                String message = row.get("message", String.class);
                                String source = row.get("source", String.class);

                                String icon = switch (level) {
                                    case "ERROR" -> "[!]";
                                    case "WARN"  -> "[*]";
                                    default      -> "[i]";
                                };
                                return String.format("  %s %-5s | %-30s | %s", icon, level, message, source);
                            }))
                    )
                    .doOnNext(System.out::println);
            },
            Connection::close
        ).blockLast();
    }
}
