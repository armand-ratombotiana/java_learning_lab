package com.oracleebs.manufacturing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;

class WipSimulatorTest {
    private WipSimulator sim;

    @BeforeEach
    void setUp() {
        sim = WipSimulator.createDefault();
    }

    @Test
    void testCreateJob() {
        var job = sim.createJob("JOB002", "FIN002", 50, "ROUTING_B", LocalDate.now());
        assertNotNull(job);
        assertEquals(WipSimulator.WipJobStatus.RELEASED, job.getStatus());
    }

    @Test
    void testCompleteOperation() {
        var job = sim.getJob("JOB001").get();
        sim.completeOperation("JOB001", 100);
        assertEquals(100, job.getCompletedQuantity());
        assertEquals(WipSimulator.WipJobStatus.COMPLETED, job.getStatus());
    }

    @Test
    void testScrapJob() {
        var job = sim.createJob("JOB003", "FIN003", 10, "ROUTING_C", LocalDate.now());
        sim.scrapJob("JOB003", 10);
        assertEquals(WipSimulator.WipJobStatus.SCRAPPED, job.getStatus());
    }

    @Test
    void testJobOperations() {
        var job = sim.getJob("JOB001").get();
        assertEquals(2, job.getOperations().size());
        assertEquals("ASSEMBLY", job.getOperations().get(0).getDepartment());
    }

    @Test
    void testTotalHoursCalculation() {
        var op = sim.getJob("JOB001").get().getOperations().get(0);
        assertEquals(1.0 + 0.5 * 100, op.totalHours(100), 0.01);
    }

    @Test
    void testGetJobsByStatus() {
        assertEquals(1, sim.getJobsByStatus(WipSimulator.WipJobStatus.RELEASED).size());
    }
}
