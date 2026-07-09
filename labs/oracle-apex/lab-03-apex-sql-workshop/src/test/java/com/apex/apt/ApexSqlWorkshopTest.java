package com.apex.apt;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

class ApexSqlWorkshopTest {
    private ApexSqlWorkshop ws;
    @BeforeEach void setUp() { ws = ApexSqlWorkshop.createSample(); }

    @Test void shouldHaveTables() { assertEquals(3, ws.getAllTables().size()); }
    @Test void shouldFindTable() { assertNotNull(ws.getTable("EMPLOYEES")); }
    @Test void shouldExecuteQuery() {
        var result = ws.executeQuery("SELECT * FROM EMPLOYEES");
        assertFalse(result.columns().isEmpty());
    }
    @Test void shouldTrackHistory() {
        ws.executeQuery("SELECT 1 FROM DUAL");
        assertEquals(1, ws.getQueryHistory().size());
    }
    @Test void shouldParseCsv() {
        var csv = "name,age\nAlice,30\nBob,25";
        var rows = ws.parseCsv(csv, ",");
        assertEquals(2, rows.size());
    }
}