package com.databases.cassandra;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class CassandraDataModelerTest {
    private CassandraDataModeler model;
    @BeforeEach void setUp() { model = CassandraDataModeler.createUserModel(); }

    @Test void shouldCreateKeyspace() {
        var cql = model.generateCreateKeyspace("testks", "SimpleStrategy", 3);
        assertTrue(cql.contains("CREATE KEYSPACE"));
        assertTrue(cql.contains("replication_factor': 3"));
    }

    @Test void shouldCreateTable() {
        var cql = model.generateCreateTable("users");
        assertTrue(cql.contains("CREATE TABLE"));
        assertTrue(cql.contains("user_id"));
        assertTrue(cql.contains("PRIMARY KEY"));
    }

    @Test void shouldHaveTables() {
        assertTrue(model.getTables().contains("users"));
        assertTrue(model.getTables().contains("user_events"));
    }

    @Test void shouldGenerateCompositePrimaryKey() {
        var cql = model.generateCreateTable("user_events");
        assertTrue(cql.contains("user_id"));
        assertTrue(cql.contains("event_time"));
    }
}
