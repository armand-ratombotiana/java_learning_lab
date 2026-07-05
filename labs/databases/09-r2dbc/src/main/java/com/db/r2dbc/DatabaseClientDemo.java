package com.db.r2dbc;

import org.springframework.r2dbc.core.DatabaseClient;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Demonstrates Spring's DatabaseClient for R2DBC.
 *
 * DatabaseClient is a higher-level abstraction over raw R2DBC,
 * providing a fluent API for executing queries and mapping results.
 */
public class DatabaseClientDemo {

    public static void main(String[] args) {
        ConnectionFactory factory = ConnectionFactories.get("r2dbc:h2:mem:///clientdb");
        DatabaseClient client = DatabaseClient.create(factory);

        System.out.println("=== DatabaseClient with R2DBC ===\n");

        // Create table
        client.sql("""
                CREATE TABLE IF NOT EXISTS employees (
                    id    BIGINT AUTO_INCREMENT PRIMARY KEY,
                    name  VARCHAR(100),
                    role  VARCHAR(50),
                    salary DECIMAL(10,2)
                )
                """)
            .fetch()
            .rowsUpdated()
            .block();

        // Insert using named parameters
        Flux<Integer> insertResults = client.sql(
                "INSERT INTO employees (name, role, salary) VALUES (:name, :role, :salary)")
            .bind("name", "Alice").bind("role", "Engineer").bind("salary", 85000.00)
            .fetch().rowsUpdated()
            .then(client.sql(
                    "INSERT INTO employees (name, role, salary) VALUES (:name, :role, :salary)")
                .bind("name", "Bob").bind("role", "Manager").bind("salary", 95000.00)
                .fetch().rowsUpdated())
            .then(client.sql(
                    "INSERT INTO employees (name, role, salary) VALUES (:name, :role, :salary)")
                .bind("name", "Carol").bind("role", "Engineer").bind("salary", 82000.00)
                .fetch().rowsUpdated())
            .flux();

        insertResults.blockLast();
        System.out.println("  Inserted 3 employees");

        // Query and map to a custom type
        System.out.println("\n  Employees (engineers only):");
        client.sql("SELECT id, name, role, salary FROM employees WHERE role = :role ORDER BY salary DESC")
            .bind("role", "Engineer")
            .fetch()
            .all()
            .map(row -> String.format("  %d | %-6s | %s | $%.0f",
                row.get("id"), row.get("name"), row.get("role"), row.get("salary")))
            .doOnNext(System.out::println)
            .blockLast();

        // Aggregation query
        System.out.println("\n  Salary stats:");
        client.sql("SELECT COUNT(*) AS cnt, AVG(salary) AS avg_sal, MAX(salary) AS max_sal FROM employees")
            .fetch()
            .one()
            .map(row -> String.format("  Count=%d | Avg=$%.0f | Max=$%.0f",
                row.get("cnt"), row.get("avg_sal"), row.get("max_sal")))
            .doOnNext(System.out::println)
            .block();
    }
}
