package com.db.sqlfundamentals;

import java.sql.*;

/**
 * Demonstrates SQL GROUP BY, aggregate functions, HAVING, and window functions.
 */
public class GroupByDemo {

    public static void main(String[] args) throws Exception {
        String url = "jdbc:h2:mem:groupdemo;DB_CLOSE_DELAY=-1";

        try (Connection conn = DriverManager.getConnection(url)) {
            setup(conn);

            System.out.println("=== GROUP BY + COUNT + AVG ===");
            groupByCountAvg(conn);

            System.out.println("\n=== GROUP BY with HAVING ===");
            groupByHaving(conn);

            System.out.println("\n=== Window Function: ROW_NUMBER() ===");
            windowFunction(conn);
        }
    }

    static void setup(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("CREATE TABLE sales (id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "product VARCHAR(50), category VARCHAR(50), amount DECIMAL(10,2), sale_date DATE)");
            st.execute("INSERT INTO sales VALUES " +
                "(1,'Mouse','Peripherals',29.99,'2024-01-10')," +
                "(2,'Keyboard','Peripherals',89.99,'2024-01-11')," +
                "(3,'Monitor','Displays',199.99,'2024-01-12')," +
                "(4,'Mouse','Peripherals',29.99,'2024-02-05')," +
                "(5,'Monitor','Displays',199.99,'2024-02-06')," +
                "(6,'Webcam','Peripherals',59.99,'2024-02-07')");
        }
    }

    static void groupByCountAvg(Connection conn) throws SQLException {
        String sql = """
            SELECT category, COUNT(*) AS cnt, AVG(amount) AS avg_amount
            FROM sales
            GROUP BY category
            ORDER BY cnt DESC
            """;
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                System.out.printf("  %-12s count=%-2d avg=$%.2f%n",
                    rs.getString("category"), rs.getInt("cnt"), rs.getDouble("avg_amount"));
            }
        }
    }

    static void groupByHaving(Connection conn) throws SQLException {
        String sql = """
            SELECT product, SUM(amount) AS total
            FROM sales
            GROUP BY product
            HAVING SUM(amount) > 100
            ORDER BY total DESC
            """;
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                System.out.printf("  %-10s total=$%.2f%n",
                    rs.getString("product"), rs.getDouble("total"));
            }
        }
    }

    static void windowFunction(Connection conn) throws SQLException {
        // H2 supports ROW_NUMBER() since version 2.x
        String sql = """
            SELECT product, category, amount,
                   ROW_NUMBER() OVER (PARTITION BY category ORDER BY amount DESC) AS rn
            FROM sales
            ORDER BY category, rn
            """;
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                System.out.printf("  %-10s %-12s $%.2f rank=%d%n",
                    rs.getString("product"), rs.getString("category"),
                    rs.getDouble("amount"), rs.getInt("rn"));
            }
        }
    }
}
