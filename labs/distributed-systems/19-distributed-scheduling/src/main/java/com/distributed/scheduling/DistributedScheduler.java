package com.distributed.scheduling;

import java.util.List;
import java.util.Optional;

public interface DistributedScheduler {
    String schedule(Job job, String cronExpression);
    boolean unschedule(String jobId);
    Optional<Job> getJob(String jobId);
    List<Job> getScheduledJobs();
    void start();
    void stop();
}
