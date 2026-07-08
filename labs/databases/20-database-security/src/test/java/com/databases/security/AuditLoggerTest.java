package com.databases.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuditLoggerTest {
    private AuditLogger logger;
    @BeforeEach void setUp() { logger = new AuditLogger(1000); }

    @Test void shouldLogEvent() {
        var event = logger.log("user1", "READ", "orders/123", "SUCCESS", "test", 10);
        assertNotNull(event);
        assertEquals("user1", event.userId());
    }

    @Test void shouldQueryByUser() {
        logger.log("user1", "READ", "orders/1", "SUCCESS", "", 1);
        logger.log("user2", "WRITE", "orders/2", "SUCCESS", "", 2);
        assertEquals(1, logger.queryByUser("user2").size());
    }

    @Test void shouldBoundQueueSize() {
        var small = new AuditLogger(5);
        for (int i = 0; i < 10; i++) small.log("u", "READ", "r", "OK", "", 1);
        assertTrue(small.getEventCount() <= 5);
    }

    @Test void shouldGenerateReport() {
        logger.logAccess("u1", "r1", true, 1);
        logger.logAccess("u2", "r2", false, 2);
        var report = logger.generateReport();
        assertTrue(report.contains("ACCESS_GRANTED"));
        assertTrue(report.contains("ACCESS_DENIED"));
    }
}
