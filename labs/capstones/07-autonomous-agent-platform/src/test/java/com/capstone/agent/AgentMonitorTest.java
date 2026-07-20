package com.capstone.agent;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class AgentMonitorTest {
    private AgentMonitor monitor;

    @BeforeEach
    void setUp() { monitor = new AgentMonitor(); }

    @Test void testRecordStep() {
        monitor.recordStep("agent-1", true, 150, "search");
        var metrics = monitor.getMetrics("agent-1");
        assertTrue(metrics.isPresent());
        assertEquals(1, metrics.get().totalSteps());
        assertEquals(1, metrics.get().successfulSteps());
        assertTrue(metrics.get().avgLatencyMs() > 0);
    }

    @Test void testMultipleSteps() {
        monitor.recordStep("agent-1", true, 100, "search");
        monitor.recordStep("agent-1", false, 200, "calculate");
        var metrics = monitor.getMetrics("agent-1").orElseThrow();
        assertEquals(2, metrics.totalSteps());
        assertEquals(1, metrics.failedSteps());
    }

    @Test void testRecordEvent() {
        monitor.recordEvent("agent-1", "ERROR", "Something went wrong");
        var events = monitor.getEvents("agent-1");
        assertEquals(1, events.size());
        assertEquals("ERROR", events.get(0).eventType());
    }

    @Test void testStrugglingAgents() {
        for (int i = 0; i < 10; i++) monitor.recordStep("bad-agent", i < 5, 100, "tool");
        var struggling = monitor.getStrugglingAgents(5);
        assertFalse struggling.isEmpty();
    }

    @Test void testTopPerforming() {
        monitor.recordStep("fast-agent", true, 10, "search");
        monitor.recordStep("slow-agent", true, 500, "search");
        var top = monitor.getTopPerformingAgents(1);
        assertEquals("fast-agent", top.get(0).agentId());
    }

    @Test void testAllMetrics() {
        monitor.recordStep("a1", true, 100, "t1");
        monitor.recordStep("a2", true, 200, "t2");
        assertEquals(2, monitor.getAllMetrics().size());
    }
}
