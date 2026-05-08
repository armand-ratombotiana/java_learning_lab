package com.learning.databases;

public class Lab {

    public static void main(String[] args) {
        System.out.println("=== PostgreSQL Concepts ===\n");

        demonstrateSQLFeatures();
        demonstrateRelationships();
        demonstrateIndexing();
        demonstrateTransactions();
        demonstrateAdvancedFeatures();
        demonstratePerformance();
    }

    private static void demonstrateSQLFeatures() {
        System.out.println("--- PostgreSQL SQL Features ---");
        System.out.println("CREATE TABLE users (");
        System.out.println("  id    SERIAL PRIMARY KEY,");
        System.out.println("  email VARCHAR(255) UNIQUE NOT NULL,");
        System.out.println("  name  VARCHAR(255) NOT NULL,");
        System.out.println("  created_at TIMESTAMPTZ DEFAULT NOW()");
        System.out.println(");");
        System.out.println();
        System.out.println("JSON/JSONB: document storage within relational DB");
        System.out.println("  SELECT data->>'name' FROM documents WHERE data @> '{\"type\":\"report\"}'");
        System.out.println("Full-text search: tsvector/tsquery with ranking");
        System.out.println("Array types, Range types, UUID, HSTORE, ENUM");
    }

    private static void demonstrateRelationships() {
        System.out.println("\n--- Relationships & Constraints ---");
        System.out.println("1:1     -> User has one Profile (FK + UNIQUE)");
        System.out.println("1:N     -> Customer has many Orders (FK on orders table)");
        System.out.println("M:N     -> Students <-> Courses via enrollments join table");
        System.out.println();
        System.out.println("Constraints: NOT NULL, CHECK, UNIQUE, PRIMARY KEY, FOREIGN KEY");
        System.out.println("ON DELETE CASCADE / SET NULL / RESTRICT");
        System.out.println("Exclusion constraints (for overlapping time ranges)");
    }

    private static void demonstrateIndexing() {
        System.out.println("\n--- Index Types ---");
        System.out.println("B-tree   -> Default, good for =, <, >, BETWEEN, LIKE (prefix)");
        System.out.println("Hash     -> Equality only (=)");
        System.out.println("GiST     -> Full-text search, geometry, range overlap");
        System.out.println("GIN      -> JSONB, array contains, tsvector");
        System.out.println("BRIN     -> Large tables with natural ordering (timestamp)");
        System.out.println("SP-GiST  -> Spatial/network data structures");
        System.out.println();
        System.out.println("Partial indexes: CREATE INDEX ON orders(status) WHERE status = 'PENDING'");
        System.out.println("Covering indexes: INCLUDE (col1, col2) for index-only scans");
    }

    private static void demonstrateTransactions() {
        System.out.println("\n--- Transactions & MVCC ---");
        System.out.println("BEGIN; UPDATE accounts SET balance = balance - 100 WHERE id = 1;");
        System.out.println("UPDATE accounts SET balance = balance + 100 WHERE id = 2;");
        System.out.println("COMMIT;  -- or ROLLBACK;");
        System.out.println();
        System.out.println("Isolation levels (from ANSI SQL):");
        System.out.println("  READ UNCOMMITTED -> mapped to READ COMMITTED in PG");
        System.out.println("  READ COMMITTED   -> default, sees committed changes only");
        System.out.println("  REPEATABLE READ  -> snapshot at first query");
        System.out.println("  SERIALIZABLE     -> true serial execution");
        System.out.println();
        System.out.println("MVCC: readers never block writers, writers never block readers");
    }

    private static void demonstrateAdvancedFeatures() {
        System.out.println("\n--- Advanced Features ---");
        System.out.println("Views       -> Virtual tables (CREATE VIEW)");
        System.out.println("Materialized Views -> Cached snapshot (REFRESH MATERIALIZED VIEW)");
        System.out.println("Window Functions -> ROW_NUMBER(), RANK(), LAG(), LEAD()");
        System.out.println("CTE (WITH)  -> Common Table Expressions for recursive queries");
        System.out.println("Partitioning-> Range, List, Hash partitioning for large tables");
        System.out.println("Extensions  -> PostGIS, pg_stat_statements, uuid-ossp");
    }

    private static void demonstratePerformance() {
        System.out.println("\n--- Performance & Monitoring ---");
        System.out.println("EXPLAIN ANALYZE -> Query execution plan with timings");
        System.out.println("VACUUM     -> Reclaim dead tuples (autovacuum runs by default)");
        System.out.println("ANALYZE    -> Update table statistics for query planner");
        System.out.println("pg_stat_statements -> Track query performance metrics");
        System.out.println("Connection pooling -> PgBouncer, Pgpool-II");
        System.out.println("Replication: Streaming (sync/async), Logical replication");
    }
}
