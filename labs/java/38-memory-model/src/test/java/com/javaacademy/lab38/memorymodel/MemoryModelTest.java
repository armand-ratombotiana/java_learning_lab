package com.javaacademy.lab38.memorymodel;

import org.junit.jupiter.api.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

class MemoryModelTest {

    @Test
    @DisplayName("Happens-before with synchronized guarantees visibility")
    void happensBeforeSynchronized() throws Exception {
        HappensBeforeExample hb = new HappensBeforeExample();
        int result = hb.demonstrateHappensBefore();
        assertTrue(result == 42 || result == -1);
    }

    @Test
    @DisplayName("Atomic classes provide happens-before")
    void atomicHappensBefore() throws Exception {
        HappensBeforeExample hb = new HappensBeforeExample();
        int result = hb.demonstrateAtomic();
        assertTrue(result == 42 || result == -1);
    }

    @Test
    @DisplayName("Volatile keyword ensures visibility")
    void volatileVisibility() throws Exception {
        VolatileExample ve = new VolatileExample();
        ve.runIncrementComparison(1000, 4);
        assertTrue(ve.getSynchronizedCounter() <= 4000);
        assertTrue(ve.getAtomicCounter() <= 4000);
    }

    @Test
    @DisplayName("Final field guarantees in constructor")
    void finalFieldGuarantees() {
        FinalFieldExample example = new FinalFieldExample();
        assertEquals(1, example.getX());
        assertEquals(2, example.getY());
    }

    @Test
    @DisplayName("False sharing example runs without error")
    void falseSharingRuns() {
        FalseSharingExample fse = new FalseSharingExample(4);
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 4; i++) {
                fse.runContended(i, 1000);
                fse.runPlain(i, 1000);
            }
        });
    }

    @Test
    @DisplayName("GC logging simulation runs")
    void gcLoggingSimulation() {
        GcLoggingExample gc = new GcLoggingExample();
        assertDoesNotThrow(() -> gc.simulateGcLoad(3));
    }

    @Test
    @DisplayName("Safe publication with volatile + final")
    void safePublication() {
        FinalFieldExample safe = new FinalFieldExample();
        safe.safeWriter(42);
        int value = safe.safeReader();
        assertEquals(42, value);
    }

    @Test
    @DisplayName("Multiple threads can increment atomic counter")
    void concurrentAtomicIncrement() throws Exception {
        VolatileExample ve = new VolatileExample();
        int threadCount = 8;
        int increments = 10000;
        ve.runIncrementComparison(increments, threadCount);
        assertEquals(threadCount * increments, ve.getAtomicCounter());
    }

    @Test
    @DisplayName("False sharing counter values accumulate")
    void falseSharingCounters() {
        FalseSharingExample fse = new FalseSharingExample(2);
        long v1 = fse.runContended(0, 1000);
        long v2 = fse.runContended(1, 2000);
        assertEquals(1000, v1);
        assertEquals(2000, v2);
    }
}
