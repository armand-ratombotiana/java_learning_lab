package com.learning.cdc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CDCSolutionTest {

    private CDCSolution solution;

    @BeforeEach
    void setUp() {
        solution = new CDCSolution();
    }

    @Test
    void testCreateConfig() {
        CDCSolution.CDCConfig config = solution.createConfig("test-connector");
        assertEquals("test-connector", config.getConnectorClass());
    }

    @Test
    void testMySQLConfig() {
        CDCSolution.CDCConfig config = solution.mysqlConfig();
        assertEquals("io.debezium.connector.mysql.MySqlConnector", config.getConnectorClass());
    }

    @Test
    void testPostgresConfig() {
        CDCSolution.CDCConfig config = solution.postgresConfig();
        assertEquals("io.debezium.connector.postgresql.PostgresConnector", config.getConnectorClass());
    }

    @Test
    void testCreateInsertEvent() {
        CDCSolution.CDCEvent event = solution.createInsertEvent("users", Map.of("id", 1, "name", "John"));
        assertEquals("insert", event.getOperation());
        assertEquals("users", event.getTable());
        assertNotNull(event.getAfter());
    }

    @Test
    void testCreateUpdateEvent() {
        CDCSolution.CDCEvent event = solution.createUpdateEvent("users", 
            Map.of("id", 1, "name", "John"), 
            Map.of("id", 1, "name", "Jane"));
        assertEquals("update", event.getOperation());
        assertEquals("John", event.getBefore().get("name"));
        assertEquals("Jane", event.getAfter().get("name"));
    }

    @Test
    void testCreateDeleteEvent() {
        CDCSolution.CDCEvent event = solution.createDeleteEvent("users", Map.of("id", 1, "name", "John"));
        assertEquals("delete", event.getOperation());
        assertNotNull(event.getBefore());
    }

    @Test
    void testCDCProcessor() {
        boolean[] processed = {false};
        CDCSolution.CDCProcessor processor = solution.createProcessor()
            .onInsert(event -> processed[0] = true);
        
        CDCSolution.CDCEvent event = solution.createInsertEvent("users", Map.of("id", 1));
        processor.process(event);
        assertTrue(processed[0]);
    }

    @Test
    void testCreateConnector() {
        CDCSolution.CDCConfig config = solution.mysqlConfig();
        CDCSolution.DebeziumConnector connector = solution.createConnector(config);
        assertEquals("running", connector.getStatus());
    }

    @Test
    void testSnapshotModes() {
        assertEquals("initial", solution.initial().getMode());
        assertEquals("schema_only", solution.schemaOnly().getMode());
        assertEquals("always", solution.always().getMode());
        assertEquals("never", solution.never().getMode());
    }
}