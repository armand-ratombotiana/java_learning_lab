package com.arch.strangleradvanced;

import java.util.*;
import java.util.concurrent.*;

public class AsyncMigrationCoordinator {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
    private final Map<String, MigrationJob> activeJobs = new ConcurrentHashMap<>();
    private final Map<String, List<Runnable>> onComplete = new ConcurrentHashMap<>();

    public String startMigration(String jobName, long batchSize, Runnable migrationLogic) {
        String jobId = UUID.randomUUID().toString();
        MigrationJob job = new MigrationJob(jobId, jobName, batchSize);
        activeJobs.put(jobId, job);
        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(() -> {
            try {
                migrationLogic.run();
                job.incrementProgress();
                if (job.getProgress() >= 100) {
                    job.complete();
                    List<Runnable> callbacks = onComplete.get(jobId);
                    if (callbacks != null) callbacks.forEach(Runnable::run);
                    future.cancel(false);
                }
            } catch (Exception e) {
                job.fail(e.getMessage());
                future.cancel(false);
            }
        }, 0, 1, TimeUnit.SECONDS);
        return jobId;
    }

    public MigrationJob getJob(String jobId) { return activeJobs.get(jobId); }
    public void onJobComplete(String jobId, Runnable callback) {
        onComplete.computeIfAbsent(jobId, k -> new CopyOnWriteArrayList<>()).add(callback);
    }
    public void shutdown() { scheduler.shutdown(); }
}

class MigrationJob {
    private final String jobId;
    private final String jobName;
    private final long batchSize;
    private volatile int progress;
    private volatile MigrationJobStatus status;
    private volatile String errorMessage;

    public MigrationJob(String jobId, String jobName, long batchSize) {
        this.jobId = jobId; this.jobName = jobName; this.batchSize = batchSize;
        this.status = MigrationJobStatus.RUNNING;
    }

    public synchronized void incrementProgress() { if (progress < 100) progress += 10; }
    public synchronized void complete() { status = MigrationJobStatus.COMPLETED; progress = 100; }
    public synchronized void fail(String error) { status = MigrationJobStatus.FAILED; errorMessage = error; }
    public int getProgress() { return progress; }
    public MigrationJobStatus getStatus() { return status; }
    public String getJobId() { return jobId; }
    public String getJobName() { return jobName; }
}

enum MigrationJobStatus { RUNNING, COMPLETED, FAILED }
