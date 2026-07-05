package com.db.queryoptimization;

import java.sql.*;
import java.util.*;

/**
 * Demonstrates the N+1 query problem in JDBC/JPA contexts.
 *
 * The N+1 problem occurs when:
 * 1 query fetches N parent entities
 * N additional queries fetch children for each parent
 *
 * Total queries: 1 (parent) + N (children) = N+1
 *
 * This is most common with ORM lazy-loading (JPA/Hibernate).
 */
public class NPlusOneDemo {

    public static void main(String[] args) throws Exception {
        String url = "jdbc:h2:mem:nplus1;DB_CLOSE_DELAY=-1";

        try (Connection conn = DriverManager.getConnection(url)) {
            setup(conn);

            System.out.println("=== N+1 Query Problem ===\n");

            // N+1 approach
            System.out.println("BAD: N+1 queries (one SELECT per category)");
            long start1 = System.nanoTime();
            nPlusOneApproach(conn);
            long time1 = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start1);
            System.out.println("  Time: " + time1 + "ms");

            System.out.println();
            System.out.println("GOOD: Single JOIN query");
            long start2 = System.nanoTime();
            joinApproach(conn);
            long time2 = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start2);
            System.out.println("  Time: " + time2 + "ms");

            System.out.println("\n=== Analysis ===");
            System.out.println("  N+1: 1 (categories) + N (products per category) queries");
            System.out.println("  JOIN: 1 query with JOIN, no additional round-trips");
            System.out.println();
            System.out.println("Solutions:");
            System.out.println("  - JOIN FETCH (JPA)");
            System.out.println("  - @EntityGraph (JPA)");
            System.out.println("  - Batch fetching (@BatchSize)");
            System.out.println("  - DTO projections with JOIN");
        }
    }

    static void setup(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("CREATE TABLE categories (id BIGINT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100))");
            st.execute("CREATE TABLE products (id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "category_id BIGINT, name VARCHAR(100), price DECIMAL(10,2))");

            st.execute("INSERT INTO categories VALUES (1, 'Electronics'),(2, 'Books'),(3, 'Clothing')");
            for (int i = 0; i < 100; i++) {
                st.execute("INSERT INTO products (category_id, name, price) VALUES (" +
                    (i % 3 + 1) + ", 'Product " + i + "', " + (Math.random() * 200 + 10) + ")");
            }
        }
    }

    /**
     * BAD: Fetches categories, then for each category fetches products separately.
     */
    static void nPlusOneApproach(Connection conn) throws SQLException {
        // Query 1: fetch all categories
        String catSql = "SELECT id, name FROM categories";
        Map<Long, String> categories = new LinkedHashMap<>();

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(catSql)) {
            while (rs.next()) {
                categories.put(rs.getLong("id"), rs.getString("name"));
            }
        }

        System.out.println("  Query 1: SELECT * FROM categories → " + categories.size() + " rows");

        int queryCount = 1;
        String prodSql = "SELECT id, name, price FROM products WHERE category_id = ?";

        for (var entry : categories.entrySet()) {
            queryCount++;
            try (PreparedStatement ps = conn.prepareStatement(prodSql)) {
                ps.setLong(1, entry.getKey());
                try (ResultSet rs = ps.executeQuery()) {
                    int count = 0;
                    while (rs.next()) count++;
                    System.out.printf("  Query %d: products for '%s' → %d rows%n",
                        queryCount, entry.getValue(), count);
                }
            }
        }
        System.out.println("  Total queries: " + queryCount + " (1 + " + categories.size() + ")");
    }

    /**
     * GOOD: Single JOIN query fetches everything in one round-trip.
     */
    static void joinApproach(Connection conn) throws SQLException {
        String sql = """
            SELECT c.name AS category, p.name AS product, p.price
            FROM categories c
            JOIN products p ON c.id = p.category_id
            ORDER BY c.name, p.name
            """;

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            int total = 0;
            while (rs.next()) {
                total++;
            }
            System.out.println("  Single JOIN query returned " + total + " rows");
            System.out.println("  Total queries: 1");
        }
    }
}
