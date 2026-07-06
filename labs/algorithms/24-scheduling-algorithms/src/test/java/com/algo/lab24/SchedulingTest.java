package com.algo.lab24;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class SchedulingTest {

    @Test
    void testFCFS() {
        List<Process> processes = List.of(
            new Process(1, 0, 5),
            new Process(2, 1, 3),
            new Process(3, 2, 8)
        );
        List<ScheduleResult> results = FCFSScheduler.schedule(processes);
        assertEquals(3, results.size());
        assertTrue(results.get(0).finishTime() <= results.get(1).startTime());
    }

    @Test
    void testFCFSEmpty() {
        assertTrue(FCFSScheduler.schedule(List.of()).isEmpty());
    }

    @Test
    void testSJF() {
        List<Process> processes = List.of(
            new Process(1, 0, 5),
            new Process(2, 1, 3),
            new Process(3, 2, 2)
        );
        List<ScheduleResult> results = SJFScheduler.schedule(processes);
        assertFalse(results.isEmpty());
    }

    @Test
    void testSJFSingleProcess() {
        List<Process> processes = List.of(new Process(1, 0, 5));
        List<ScheduleResult> results = SJFScheduler.schedule(processes);
        assertEquals(1, results.size());
        assertEquals(5, results.get(0).finishTime());
    }

    @Test
    void testRoundRobin() {
        List<Process> processes = List.of(
            new Process(1, 0, 5),
            new Process(2, 1, 3),
            new Process(3, 2, 8)
        );
        List<ScheduleResult> results = RoundRobinScheduler.schedule(processes, 2);
        assertFalse(results.isEmpty());
    }

    @Test
    void testRoundRobinEmpty() {
        assertTrue(RoundRobinScheduler.schedule(List.of(), 2).isEmpty());
    }

    @Test
    void testPriorityScheduling() {
        List<Process> processes = List.of(
            new Process(1, 0, 5, 1),
            new Process(2, 1, 3, 2),
            new Process(3, 2, 8, 1)
        );
        List<ScheduleResult> results = PriorityScheduler.schedule(processes);
        assertFalse(results.isEmpty());
    }

    @Test
    void testPriorityEmpty() {
        assertTrue(PriorityScheduler.schedule(List.of()).isEmpty());
    }

    @Test
    void testEDF() {
        List<Process> processes = List.of(
            new Process(1, 0, 5, 0, 10),
            new Process(2, 1, 3, 0, 5),
            new Process(3, 2, 8, 0, 15)
        );
        List<ScheduleResult> results = EDFScheduler.schedule(processes);
        assertFalse(results.isEmpty());
    }

    @Test
    void testEDFEmpty() {
        assertTrue(EDFScheduler.schedule(List.of()).isEmpty());
    }

    @Test
    void testProcessRecord() {
        Process p = new Process(1, 0, 5);
        assertEquals(1, p.id());
        assertEquals(0, p.arrivalTime());
        assertEquals(5, p.burstTime());
    }

    @Test
    void testScheduleResult() {
        ScheduleResult r = new ScheduleResult(1, 0, 5, 2);
        assertEquals(1, r.processId());
        assertEquals(0, r.startTime());
        assertEquals(5, r.finishTime());
        assertEquals(7, r.turnaroundTime());
    }
}
