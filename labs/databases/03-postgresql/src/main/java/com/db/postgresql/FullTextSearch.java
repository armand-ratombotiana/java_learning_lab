package com.db.postgresql;

import java.sql.*;

/**
 * Demonstrates PostgreSQL full-text search (tsvector/tsquery).
 *
 * Full-text search provides linguistic search capabilities
 * with stemming, ranking, and index support (GIN).
 */
public class FullTextSearch {

    static final String URL = "jdbc:postgresql://localhost:5432/academy";
    static final String USER = "academy_user";
    static final String PASSWORD = "academy_pass";

    public static void main(String[] args) throws Exception {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            setup(conn);

            System.out.println("=== Insert documents for FTS ===");
            insertDocument(conn, 1, "The quick brown fox jumps over the lazy dog");
            insertDocument(conn, 2, "A quick brown rabbit jumps over the fence");
            insertDocument(conn, 3, "Slow and steady wins the race");
            insertDocument(conn, 4, "PostgreSQL full text search is powerful");

            System.out.println("\n=== Basic tsquery match ===");
            ftsQuery(conn, "quick & brown");

            System.out.println("\n=== Phrase search ===");
            ftsPhrase(conn, "quick brown");

            System.out.println("\n=== Ranked search results ===");
            ftsRanked(conn, "jumps");

            System.out.println("\n=== Highlighted snippets ===");
            ftsHeadline(conn, "quick");

        } catch (SQLException e) {
            System.out.println("PostgreSQL required: " + e.getMessage());
        }
    }

    static void setup(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("CREATE SCHEMA IF NOT EXISTS academy");
            st.execute("SET search_path TO academy");
            st.execute("""
                CREATE TABLE IF NOT EXISTS documents (
                    id      BIGINT PRIMARY KEY,
                    title   VARCHAR(255),
                    body    TEXT NOT NULL,
                    tsv     TSVECTOR
                )
                """);
            st.execute("CREATE INDEX IF NOT EXISTS idx_fts ON documents USING GIN (tsv)");

            // Trigger to auto-update tsvector
            st.execute("""
                CREATE OR REPLACE FUNCTION documents_tsv_trigger() RETURNS trigger AS $$
                BEGIN
                    NEW.tsv := to_tsvector('english', NEW.body);
                    RETURN NEW;
                END;
                $$ LANGUAGE plpgsql
                """);
            st.execute("""
                DROP TRIGGER IF EXISTS trg_tsv ON documents
                """);
            st.execute("""
                CREATE TRIGGER trg_tsv
                BEFORE INSERT OR UPDATE ON documents
                FOR EACH ROW EXECUTE FUNCTION documents_tsv_trigger()
                """);
        }
    }

    static void insertDocument(Connection conn, long id, String body) throws SQLException {
        String sql = "INSERT INTO documents (id, body) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.setString(2, body);
            ps.executeUpdate();
            System.out.printf("  Document %d inserted%n", id);
        }
    }

    static void ftsQuery(Connection conn, String query) throws SQLException {
        String sql = """
            SELECT id, body FROM documents
            WHERE tsv @@ to_tsquery('english', ?)
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, query);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    System.out.printf("  [%d] %s%n", rs.getLong("id"), rs.getString("body"));
                }
            }
        }
    }

    static void ftsPhrase(Connection conn, String phrase) throws SQLException {
        String sql = """
            SELECT id, body FROM documents
            WHERE tsv @@ phraseto_tsquery('english', ?)
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phrase);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    System.out.printf("  [%d] %s%n", rs.getLong("id"), rs.getString("body"));
                }
            }
        }
    }

    static void ftsRanked(Connection conn, String query) throws SQLException {
        String sql = """
            SELECT id, body,
                   ts_rank(tsv, to_tsquery('english', ?)) AS rank
            FROM documents
            WHERE tsv @@ to_tsquery('english', ?)
            ORDER BY rank DESC
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, query);
            ps.setString(2, query);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    System.out.printf("  [%d] rank=%.4f %s%n",
                        rs.getLong("id"), rs.getDouble("rank"), rs.getString("body"));
                }
            }
        }
    }

    static void ftsHeadline(Connection conn, String query) throws SQLException {
        String sql = """
            SELECT id,
                   ts_headline('english', body, to_tsquery('english', ?),
                               'StartSel=<b>, StopSel=</b>, MaxWords=10, MinWords=3') AS headline
            FROM documents
            WHERE tsv @@ to_tsquery('english', ?)
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, query);
            ps.setString(2, query);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    System.out.printf("  [%d] %s%n", rs.getLong("id"), rs.getString("headline"));
                }
            }
        }
    }
}
