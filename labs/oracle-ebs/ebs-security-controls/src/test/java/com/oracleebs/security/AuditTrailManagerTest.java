package com.oracleebs.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class AuditTrailManagerTest {
    private AuditTrailManager mgr;

    @BeforeEach
    void setUp() {
        mgr = AuditTrailManager.createDefault();
    }

    @Test
    void testAuditRecording() {
        var record = mgr.record("GL_BALANCES", "BAL001", AuditTrailManager.AuditAction.UPDATE, "USER1", "OLD=100", "NEW=200", "10.0.0.1");
        assertNotNull(record);
        assertEquals("GL_BALANCES", record.getTableName());
    }

    @Test
    void testAuditForUnmonitoredTable() {
        var record = mgr.record("UNMONITORED_TABLE", "KEY1", AuditTrailManager.AuditAction.DELETE, "USER1", "OLD", "NEW", "10.0.0.1");
        assertNull(record);
    }

    @Test
    void testQueryByUser() {
        var records = mgr.queryByUser("SYSADMIN");
        assertEquals(1, records.size());
    }

    @Test
    void testQueryByTable() {
        var records = mgr.queryByTable("AP_INVOICES");
        assertEquals(1, records.size());
    }

    @Test
    void testQueryByAction() {
        var records = mgr.queryByAction(AuditTrailManager.AuditAction.INSERT);
        assertEquals(1, records.size());
    }

    @Test
    void testTotalRecords() {
        assertEquals(2, mgr.getTotalRecords());
    }

    @Test
    void testAuditTimestamps() {
        var record = mgr.record("FND_USER", "U001", AuditTrailManager.AuditAction.LOGIN, "USER2", null, null, "10.0.0.2");
        assertNotNull(record.getTimestamp());
    }
}
