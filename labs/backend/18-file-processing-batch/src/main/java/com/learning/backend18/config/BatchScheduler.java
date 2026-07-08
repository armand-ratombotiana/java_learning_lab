package com.learning.backend18.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BatchScheduler {

    private static final Logger log = LoggerFactory.getLogger(BatchScheduler.class);
    private final JobLauncher jobLauncher;
    private final Job importProductJob;

    public BatchScheduler(JobLauncher jobLauncher, Job importProductJob) {
        this.jobLauncher = jobLauncher;
        this.importProductJob = importProductJob;
    }

    @Scheduled(cron = "0 0 2 * * ?")
    public void runDailyImport() {
        try {
            JobParameters params = new JobParametersBuilder()
                .addLong("runTime", System.currentTimeMillis())
                .toJobParameters();
            var execution = jobLauncher.run(importProductJob, params);
            log.info("Batch job completed: status={}, exitCode={}",
                execution.getStatus(), execution.getExitStatus().getExitCode());
        } catch (Exception e) {
            log.error("Batch job failed", e);
        }
    }
}
