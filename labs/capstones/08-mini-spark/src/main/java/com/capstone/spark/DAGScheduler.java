package com.capstone.spark;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class DAGScheduler {
    private final SparkContext sc;
    private final AtomicLong jobIdGen = new AtomicLong(0);
    private final Map<Long, Job> jobs = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newWorkStealingPool();

    public record Job(long jobId, String jobName, List<Stage> stages, JobStatus status, Instant submittedAt, Instant completedAt) {}
    public record Stage(int stageId, String stageType, List<String> dependencies, StageStatus status) {}
    public enum JobStatus { SUBMITTED, RUNNING, COMPLETED, FAILED }
    public enum StageStatus { PENDING, RUNNING, COMPLETED, FAILED }

    public DAGScheduler(SparkContext sc) { this.sc = sc; }

    public <T> List<T> runJob(RDD<T> rdd, String jobName) {
        long jobId = jobIdGen.incrementAndGet();
        List<Stage> stages = buildStages(rdd);
        Job job = new Job(jobId, jobName, stages, JobStatus.SUBMITTED, Instant.now(), null);
        jobs.put(jobId, job);
        job = new Job(jobId, jobName, stages, JobStatus.RUNNING, job.submittedAt(), null);
        jobs.put(jobId, job);
        List<T> result = new CopyOnWriteArrayList<>();
        CompletableFuture<?>[] futures = stages.stream()
            .map(stage -> CompletableFuture.runAsync(() -> {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }, executor))
            .toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(futures).join();
        job = new Job(jobId, jobName, stages, JobStatus.COMPLETED, job.submittedAt(), Instant.now());
        jobs.put(jobId, job);
        return rdd.collect();
    }

    public <T> List<T> runJobWithCallback(RDD<T> rdd, String jobName, Runnable callback) {
        List<T> result = runJob(rdd, jobName);
        if (callback != null) callback.run();
        return result;
    }

    public Job getJob(long jobId) { return jobs.get(jobId); }

    public List<Job> getCompletedJobs() {
        return jobs.values().stream().filter(j -> j.status() == JobStatus.COMPLETED).toList();
    }

    public List<Job> getAllJobs() { return List.copyOf(jobs.values()); }

    public int pendingJobCount() {
        return (int) jobs.values().stream().filter(j -> j.status() == JobStatus.RUNNING).count();
    }

    private <T> List<Stage> buildStages(RDD<T> rdd) {
        List<Stage> stages = new ArrayList<>();
        List<String> deps = rdd.getDependencies();
        for (int i = 0; i < Math.max(deps.size(), 1); i++) {
            String depType = i < deps.size() ? deps.get(i) : "source";
            stages.add(new Stage(i, depType, List.of(), StageStatus.PENDING));
        }
        return stages;
    }

    public void shutdown() { executor.shutdown(); }

    public void clear() { jobs.clear(); }
}
