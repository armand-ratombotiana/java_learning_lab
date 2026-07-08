package com.databases.dbtesting;

import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class DataFixtureGeneratorTest {
    @Test void shouldGenerateUserFixture() {
        var gen = DataFixtureGenerator.userFixture();
        var row = gen.generate();
        assertNotNull(row.get("id"));
        assertNotNull(row.get("name"));
        assertNotNull(row.get("email"));
        assertNotNull(row.get("age"));
        assertNotNull(row.get("active"));
    }

    @Test void shouldGenerateBatch() {
        var rows = DataFixtureGenerator.userFixture().generateBatch(100);
        assertEquals(100, rows.size());
    }

    @Test void shouldGenerateOrderFixture() {
        var gen = DataFixtureGenerator.orderFixture();
        var rows = gen.generateBatch(50);
        assertEquals(50, rows.size());
        assertTrue(rows.stream().allMatch(r -> r.containsKey("order_id")));
    }

    @Test void shouldGenerateInsertSQL() {
        var gen = DataFixtureGenerator.userFixture();
        var row = gen.generate();
        String sql = gen.toInsertSQL("users", row);
        assertTrue(sql.startsWith("INSERT INTO users"));
    }

    @Test void shouldGenerateBatchSQL() {
        var rows = DataFixtureGenerator.orderFixture().generateBatch(10);
        var sqls = new DataFixtureGenerator().toInsertSQLBatch("orders", rows);
        assertEquals(10, sqls.size());
    }
}
