package com.distributed.scheduling;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record Job(
    String id,
    String name,
    String cronExpression,
    Runnable task,
    Map<String, String> parameters,
    Instant createdAt,
    Instant lastFiredAt,
    int executionCount,
    JobStatus status
) {
    public enum JobStatus { SCHEDULED, RUNNING, COMPLETED, FAILED, CANCELLED }

    public static Job create(String name, String cronExpression, Runnable task) {
        return new Job(
            UUID.randomUUID().toString(),
            name,
            cronExpression,
            task,
            Map.of(),
            Instant.now(),
            null,
            0,
            JobStatus.SCHEDULED
        );
    }

    public Job withStatus(JobStatus newStatus) {
        return new Job(id, name, cronExpression, task, parameters, createdAt, lastFiredAt, executionCount, newStatus);
    }

    public Job withExecution() {
        return new Job(id, name, cronExpression, task, parameters, createdAt, Instant.now(), executionCount + 1, JobStatus.RUNNING);
    }
}
