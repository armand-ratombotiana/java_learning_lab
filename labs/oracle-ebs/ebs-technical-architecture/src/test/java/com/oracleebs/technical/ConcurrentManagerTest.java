package com.oracleebs.technical;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;

class ConcurrentManagerTest {
    private ConcurrentManager mgr;

    @BeforeEach
    void setUp() {
        mgr = ConcurrentManager.createDefault();
    }

    @Test
    void testManagerInitialState() {
        assertEquals(ConcurrentManager.ManagerStatus.ACTIVE, mgr.getStatus());
        assertEquals("STANDARD_MANAGER", mgr.getManagerName());
        assertEquals(60, mgr.getSleepSeconds());
    }

    @Test
    void testSubmitRequest() {
        ConcurrentManager mgr2 = new ConcurrentManager("TEST_MGR", 30);
        assertTrue(mgr2.submit(new ConcurrentManager.RequestWrapper("TEST", "P", "NORMAL", Map.of())));
    }

    @Test
    void testProcessRequest() {
        mgr.processRequest("GLPOST");
        assertEquals(1, mgr.getProcessedCount());
        assertEquals(1, mgr.getRunning().size());
        assertEquals(1, mgr.getCompleted().size());
    }

    @Test
    void testInactiveManagerRejectsRequests() {
        mgr.setStatus(ConcurrentManager.ManagerStatus.INACTIVE);
        assertFalse(mgr.submit(new ConcurrentManager.RequestWrapper("TEST", "P", "NORMAL", Map.of())));
    }

    @Test
    void testLogFileGenerated() {
        mgr.processRequest("APINV");
        var completed = mgr.getCompleted();
        assertTrue(completed.get(completed.size()-1).getLogFileName().contains("APINV"));
    }
}
