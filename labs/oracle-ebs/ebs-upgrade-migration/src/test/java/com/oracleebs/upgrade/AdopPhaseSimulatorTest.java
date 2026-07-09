package com.oracleebs.upgrade;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class AdopPhaseSimulatorTest {
    private AdopPhaseSimulator sim;

    @BeforeEach
    void setUp() {
        sim = AdopPhaseSimulator.createDefault();
    }

    @Test
    void testStartCycle() {
        var cycle = sim.getCycle("CYCLE_001");
        assertTrue(cycle.isPresent());
        assertEquals("Patch 35042176", cycle.get().getPatchName());
    }

    @Test
    void testInitialPhaseIsPrepare() {
        assertEquals(AdopPhaseSimulator.AdopPhase.PREPARE, sim.getCurrentPhase("CYCLE_001"));
    }

    @Test
    void testAdvancePhase() {
        assertTrue(sim.advancePhase("CYCLE_001"));
        assertEquals(AdopPhaseSimulator.AdopPhase.APPLY, sim.getCurrentPhase("CYCLE_001"));
    }

    @Test
    void testFullCycle() {
        for (int i = 0; i < 5; i++) {
            sim.advancePhase("CYCLE_001");
        }
        assertTrue(sim.isCycleComplete("CYCLE_001"));
    }

    @Test
    void testFailPhase() {
        sim.failPhase("CYCLE_001", "Edition creation failed");
        var cycle = sim.getCycle("CYCLE_001").get();
        assertEquals(AdopPhaseSimulator.AdopStatus.FAILED, cycle.getPhaseStatus(AdopPhaseSimulator.AdopPhase.PREPARE));
    }

    @Test
    void testNonexistentCycle() {
        assertNull(sim.getCurrentPhase("NONEXISTENT"));
    }

    @Test
    void testEditionCreated() {
        var cycle = sim.getCycle("CYCLE_001").get();
        assertTrue(cycle.getEditionName().startsWith("EDITION_"));
    }
}
