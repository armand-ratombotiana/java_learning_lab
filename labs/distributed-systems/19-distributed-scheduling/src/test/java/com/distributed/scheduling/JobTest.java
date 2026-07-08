package com.distributed.scheduling;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JobTest {

    @Test
    void testJobCreation() {
        Job job = Job.create("test-job", "*/5 * * * *", () -> {});
        assertNotNull(job.id());
        assertEquals("test-job", job.name());
        assertEquals(Job.JobStatus.SCHEDULED, job.status());
    }

    @Test
    void testJobStatusTransition() {
        Job job = Job.create("status-test", "* * * * *", () -> {});
        Job running = job.withExecution();
        assertEquals(Job.JobStatus.RUNNING, running.status());
        assertEquals(1, running.executionCount());
        Job completed = running.withStatus(Job.JobStatus.COMPLETED);
        assertEquals(Job.JobStatus.COMPLETED, completed.status());
    }
}
