package com.learning.postgres;

import java.util.*;
import java.util.stream.*;

public class Lab {
    record Row(Map<String, Object> cols) {
        Object get(String c) { return cols.get(c); }
        public String toString() { return cols.toString(); }
    }

    static class Table {
        String name;
        List<Row> rows = new ArrayList<>();
        List<String> columns;

        Table(String name, String... cols) { this.name = name; this.columns = Arrays.asList(cols); }

        void insert(Object... values) {
            var map = new LinkedHashMap<String, Object>();
            for (int i = 0; i < columns.size() && i < values.length; i++)
                map.put(columns.get(i), values[i]);
            rows.add(new Row(map));
        }

        List<Row> all() { return rows; }

        List<Row> where(String col, Object val) {
            return rows.stream().filter(r -> Objects.equals(r.get(col), val)).collect(Collectors.toList());
        }

        List<Row> whereLike(String col, String pattern) {
            return rows.stream().filter(r -> {
                Object v = r.get(col);
                return v != null && v.toString().toLowerCase().contains(pattern.toLowerCase());
            }).collect(Collectors.toList());
        }

        List<Row> whereBetween(String col, Number min, Number max) {
            return rows.stream().filter(r -> {
                Number v = (Number) r.get(col);
                return v != null && v.doubleValue() >= min.doubleValue() && v.doubleValue() <= max.doubleValue();
            }).collect(Collectors.toList());
        }

        List<Row> orderBy(String col, boolean asc) {
            var sorted = new ArrayList<>(rows);
            sorted.sort((a, b) -> {
                Comparable ca = (Comparable) a.get(col);
                Comparable cb = (Comparable) b.get(col);
                return asc ? ca.compareTo(cb) : cb.compareTo(ca);
            });
            return sorted;
        }
    }

    static class Database {
        Map<String, Table> tables = new LinkedHashMap<>();

        void createTable(String name, String... cols) {
            tables.put(name, new Table(name, cols));
        }

        Table get(String name) { return tables.get(name); }

        List<Row> join(String t1, String t2, String on1, String on2) {
            var result = new ArrayList<Row>();
            for (var r1 : tables.get(t1).rows) {
                for (var r2 : tables.get(t2).rows) {
                    if (Objects.equals(r1.get(on1), r2.get(on2))) {
                        var joined = new LinkedHashMap<String, Object>();
                        joined.putAll(r1.cols); joined.putAll(r2.cols);
                        result.add(new Row(joined));
                    }
                }
            }
            return result;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== PostgreSQL Concepts Lab ===\n");

        Database db = new Database();

        System.out.println("1. CREATE TABLE + INSERT:");
        db.createTable("users", "id", "name", "email", "age");
        db.get("users").insert(1, "Alice", "alice@mail.com", 30);
        db.get("users").insert(2, "Bob", "bob@mail.com", 25);
        db.get("users").insert(3, "Charlie", "charlie@mail.com", 35);
        db.get("users").insert(4, "Diana", "diana@mail.com", 28);
        System.out.println("   Inserted 4 users");

        System.out.println("\n2. SELECT * FROM users:");
        db.get("users").all().forEach(r -> System.out.println("   " + r));

        System.out.println("\n3. SELECT WHERE name = 'Bob':");
        db.get("users").where("name", "Bob").forEach(r -> System.out.println("   " + r));

        System.out.println("\n4. SELECT WHERE age BETWEEN 28 AND 32:");
        db.get("users").whereBetween("age", 28, 32).forEach(r -> System.out.println("   " + r));

        System.out.println("\n5. SELECT WHERE name LIKE '%li%':");
        db.get("users").whereLike("name", "li").forEach(r -> System.out.println("   " + r));

        System.out.println("\n6. SELECT ORDER BY age DESC:");
        db.get("users").orderBy("age", false).forEach(r -> System.out.println("   " + r));

        System.out.println("\n7. JOIN: orders + users:");
        db.createTable("orders", "id", "user_id", "product", "amount");
        db.get("orders").insert(101, 1, "Laptop", 1200);
        db.get("orders").insert(102, 1, "Mouse", 50);
        db.get("orders").insert(103, 2, "Keyboard", 100);
        db.get("orders").insert(104, 3, "Monitor", 300);

        var joined = db.join("orders", "users", "user_id", "id");
        joined.forEach(r -> System.out.println("   " + r.get("name") + " ordered " + r.get("product") + " ($" + r.get("amount") + ")"));

        System.out.println("\n8. Aggregation - GROUP BY user (via stream):");
        var orders = db.get("orders");
        var perUser = orders.rows.stream()
            .collect(Collectors.groupingBy(r -> r.get("user_id"),
                Collectors.summingDouble(r -> ((Number) r.get("amount")).doubleValue())));
        perUser.forEach((uid, total) -> {
            var user = db.get("users").where("id", uid);
            String name = user.isEmpty() ? "?" : (String) user.get(0).get("name");
            System.out.println("   " + name + " total: $" + total);
        });

        System.out.println("\n9. Transaction simulation (BEGIN/COMMIT/ROLLBACK):");
        System.out.println("   BEGIN;");
        System.out.println("   UPDATE users SET age = 31 WHERE id = 1;");
        System.out.println("   COMMIT;");
        System.out.println("   Isolation levels: READ COMMITTED, REPEATABLE READ, SERIALIZABLE");

        System.out.println("\n10. Index (simulated B-tree):");
        System.out.println("    CREATE INDEX idx_users_email ON users(email);");
        System.out.println("    Indexes accelerate WHERE/ORDER BY lookups");

        System.out.println("\n11. EXPLAIN ANALYZE simulation:");
        System.out.println("    Seq Scan on users (cost=0.00..1.04 rows=4 width=68)");
        System.out.println("    Filter: (age > 25)");

        System.out.println("\n=== Lab Complete ===");
    }
}
