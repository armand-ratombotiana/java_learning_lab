package com.databases.cockroachdb;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GeoPartitionManagerTest {
    private GeoPartitionManager gpm;
    @BeforeEach void setUp() {
        gpm = new GeoPartitionManager();
        gpm.addRegion("us-east", "us-east-1", 3);
        gpm.addRegion("eu-west", "eu-west-1", 3);
    }

    @Test void shouldAddRegions() {
        assertEquals(2, gpm.getRegions().size());
    }

    @Test void shouldAssignTable() {
        gpm.assignTable("orders", "us-east");
        assertEquals(1, gpm.getRegion("us-east").tables().size());
    }

    @Test void shouldGenerateSQL() {
        gpm.assignTable("orders", "us-east");
        var sqls = gpm.generateFullGeoSetup("myapp");
        assertFalse(sqls.isEmpty());
        assertTrue(sqls.stream().anyMatch(s -> s.contains("orders")));
    }

    @Test void shouldThrowForUnknownRegion() {
        assertThrows(IllegalArgumentException.class, () -> gpm.assignTable("t", "unknown"));
    }
}
