package com.db.postgresql;

import java.sql.*;
import java.util.Properties;

/**
 * Demonstrates PostgreSQL connection setup and basic operations.
 *
 * Key concepts: connection URL, SSL config, schema search path,
 * database metadata inspection.  Requires a running PostgreSQL instance.
 */
public class PostgresConnection {

    // Connection parameters (customize for your environment)
    static final String URL = "jdbc:postgresql://localhost:5432/academy";
    static final String USER = "academy_user";
    static final String PASSWORD = "academy_pass";

    /**
     * Creates a connection with recommended PostgreSQL settings.
     * - fetchSize: stream large result sets
     * - schema: set search path
     * - sslmode: verify-full in production
     */
    static Connection connect() throws SQLException {
        Properties props = new Properties();
        props.setProperty("user", USER);
        props.setProperty("password", PASSWORD);
        props.setProperty("sslmode", "disable");          // use "verify-full" in prod
        props.setProperty("applicationName", "AcademyApp");
        props.setProperty("fetchSize", "100");            // cursor-based fetching
        props.setProperty("binaryTransfer", "true");      // performance optimization

        Connection conn = DriverManager.getConnection(URL, props);
        try (Statement st = conn.createStatement()) {
            st.execute("SET search_path TO academy, public");
        }
        return conn;
    }

    static void showMetadata(Connection conn) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        System.out.println("DB Product: " + meta.getDatabaseProductVersion());
        System.out.println("Driver:     " + meta.getDriverVersion());
        System.out.println("JDBC URL:   " + meta.getURL());
        System.out.println("Username:   " + meta.getUserName());

        try (ResultSet rs = meta.getTables(null, "academy", "%", new String[]{"TABLE"})) {
            System.out.println("\nTables in 'academy' schema:");
            while (rs.next()) {
                System.out.println("  - " + rs.getString("TABLE_NAME"));
            }
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== PostgreSQL Connection Demo ===\n");
        try (Connection conn = connect()) {
            showMetadata(conn);

            System.out.println("\n=== Querying pg_stat_activity ===");
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(
                     "SELECT pid, state, query FROM pg_stat_activity WHERE datname = current_database()")) {
                while (rs.next()) {
                    System.out.printf("  pid=%d state=%s query=%.60s%n",
                        rs.getInt("pid"), rs.getString("state"), rs.getString("query"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Cannot connect to PostgreSQL: " + e.getMessage());
            System.out.println("Start PostgreSQL and create user/database:");
            System.out.println("  CREATE USER academy_user WITH PASSWORD 'academy_pass';");
            System.out.println("  CREATE DATABASE academy OWNER academy_user;");
        }
    }
}
