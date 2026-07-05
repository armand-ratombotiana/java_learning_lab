package com.db.queryoptimization;

import java.sql.*;

/**
 * Demonstrates index strategies and their impact on query performance.
 *
 * Index types covered:
 * - Single-column B-tree index
 * - Composite (multi-column) index
 * - Covering index (included columns)
 * - Partial index (WHERE clause)
 * - Index scan vs. full table scan
 */
public class IndexStrategyDemo {

    public static void main(String[] args) throws Exception {
        String url = "jdbc:h2:mem:indexes;DB_CLOSE_DELAY=-1";

        try (Connection conn = DriverManager.getConnection(url)) {
            setup(conn);

            System.out.println("=== Index Strategy Analysis ===\n");

            // Query without index
            System.out.println("1. Full table scan (no index):");
            explainQuery(conn, "SELECT * FROM articles WHERE author_id = 50");

            // Add single-column index
            conn.createStatement().execute("CREATE INDEX idx_author ON articles(author_id)");
            System.out.println("\n2. Single-column B-tree index on author_id:");
            explainQuery(conn, "SELECT * FROM articles WHERE author_id = 50");

            // Composite index for multi-field queries
            conn.createStatement().execute(
                "CREATE INDEX idx_author_status ON articles(author_id, status)");
            System.out.println("\n3. Composite index (author_id, status):");
            explainQuery(conn,
                "SELECT * FROM articles WHERE author_id = 50 AND status = 'PUBLISHED'");

            // Covering index — all columns needed are in the index
            conn.createStatement().execute(
                "CREATE INDEX idx_author_title ON articles(author_id, title)");
            System.out.println("\n4. Covering index (author_id, title) — index-only scan:");
            explainQuery(conn,
                "SELECT title FROM articles WHERE author_id = 50");

            // Partial index
            conn.createStatement().execute(
                "CREATE INDEX idx_active_articles ON articles(author_id) WHERE status = 'PUBLISHED'");
            System.out.println("\n5. Partial index (author_id WHERE status = 'PUBLISHED'):");
            explainQuery(conn,
                "SELECT * FROM articles WHERE author_id = 50 AND status = 'PUBLISHED'");

            System.out.println("\n=== Summary ===");
            System.out.println("""
                Index Selection Guidelines:
                - High-cardinality columns → good for B-tree indexes
                - Composite indexes → order columns by selectivity (most selective first)
                - Covering indexes → avoid table heap lookups (index-only scans)
                - Partial indexes → save space when filtering on a constant
                - Avoid: over-indexing (write performance suffers)
                - Avoid: indexes on low-cardinality columns alone (e.g., boolean)
                """);
        }
    }

    static void setup(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("""
                CREATE TABLE articles (
                    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
                    author_id  BIGINT NOT NULL,
                    title      VARCHAR(255) NOT NULL,
                    status     VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
                    created_at TIMESTAMP
                )
                """);
            for (int i = 0; i < 5000; i++) {
                st.execute("INSERT INTO articles (author_id, title, status, created_at) VALUES (" +
                    (i % 100) + ", 'Article " + i + "', " +
                    (i % 3 == 0 ? "'PUBLISHED'" : "'DRAFT'") + ", NOW())");
            }
        }
    }

    static void explainQuery(Connection conn, String sql) throws SQLException {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("EXPLAIN ANALYZE " + sql)) {
            while (rs.next()) {
                System.out.println("  " + rs.getString(1));
            }
        }
    }
}
