package com.databases.dbtesting;

import java.util.*;
import java.util.function.Supplier;

public class DataFixtureGenerator {
    private final Random random = new Random(42);
    private final Map<String, Supplier<?>> generators = new LinkedHashMap<>();

    public DataFixtureGenerator withField(String name, Supplier<?> gen) {
        generators.put(name, gen);
        return this;
    }

    public Map<String, Object> generate() {
        Map<String, Object> row = new LinkedHashMap<>();
        for (var e : generators.entrySet()) row.put(e.getKey(), e.getValue().get());
        return row;
    }

    public List<Map<String, Object>> generateBatch(int count) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (int i = 0; i < count; i++) rows.add(generate());
        return rows;
    }

    public static DataFixtureGenerator userFixture() {
        var gen = new DataFixtureGenerator();
        var idCounter = new Object() { int i = 1; };
        gen.withField("id", () -> idCounter.i++);
        gen.withField("name", () -> "user_" + random.nextInt(100000));
        gen.withField("email", () -> "user" + random.nextInt(100000) + "@example.com");
        gen.withField("age", () -> random.nextInt(60) + 18);
        gen.withField("active", () -> random.nextBoolean());
        gen.withField("created_at", () -> System.currentTimeMillis() - random.nextLong(1000000000L));
        return gen;
    }

    public static DataFixtureGenerator orderFixture() {
        var gen = new DataFixtureGenerator();
        var idCounter = new Object() { int i = 1; };
        gen.withField("order_id", () -> idCounter.i++);
        gen.withField("user_id", () -> random.nextInt(1000) + 1);
        gen.withField("total", () -> Math.round(random.nextDouble() * 1000 * 100.0) / 100.0);
        gen.withField("status", () -> {
            String[] statuses = {"PENDING", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED"};
            return statuses[random.nextInt(statuses.length)];
        });
        gen.withField("items", () -> random.nextInt(10) + 1);
        return gen;
    }

    public String toInsertSQL(String table, Map<String, Object> row) {
        var cols = new StringBuilder();
        var vals = new StringBuilder();
        for (var e : row.entrySet()) {
            if (cols.length() > 0) { cols.append(", "); vals.append(", "); }
            cols.append(e.getKey());
            Object v = e.getValue();
            if (v instanceof String) vals.append("'").append(v).append("'");
            else if (v instanceof Boolean) vals.append(v);
            else vals.append(v);
        }
        return "INSERT INTO " + table + " (" + cols + ") VALUES (" + vals + ");";
    }

    public List<String> toInsertSQLBatch(String table, List<Map<String, Object>> rows) {
        return rows.stream().map(r -> toInsertSQL(table, r)).toList();
    }
}
