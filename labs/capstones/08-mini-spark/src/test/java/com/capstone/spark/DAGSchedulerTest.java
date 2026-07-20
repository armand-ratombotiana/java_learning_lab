package com.capstone.spark;

import org.junit.jupiter.api.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DAGSchedulerTest {
    private SparkContext sc;
    private DAGScheduler scheduler;

    @BeforeEach
    void setUp() {
        sc = new SparkContext("test", "local");
        scheduler = new DAGScheduler(sc);
    }

    @Test void testRunJob() {
        var rdd = sc.parallelize(List.of(1, 2, 3, 4, 5));
        var result = scheduler.runJob(rdd, "test-job");
        assertEquals(5, result.size());
    }

    @Test void testGetJob() {
        var rdd = sc.parallelize(List.of(1, 2, 3));
        scheduler.runJob(rdd, "job-1");
        var jobs = scheduler.getAllJobs();
        assertFalse(jobs.isEmpty());
        assertEquals(DAGScheduler.JobStatus.COMPLETED, jobs.get(0).status());
    }

    @Test void testCompletedJobs() {
        var rdd = sc.parallelize(List.of(1, 2));
        scheduler.runJob(rdd, "j1");
        assertEquals(1, scheduler.getCompletedJobs().size());
    }

    @Test void testRunJobWithCallback() {
        var rdd = sc.parallelize(List.of(1));
        var result = scheduler.runJobWithCallback(rdd, "test", () -> {});
        assertNotNull(result);
    }

    @Test void testShutdown() {
        scheduler.shutdown();
        scheduler.clear();
        assertTrue(scheduler.getAllJobs().isEmpty());
    }
}
