package com.learning.backend14;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.Executor;

/**
 * Configuration for scheduling infrastructure.
 *
 * Customizes the task scheduler thread pool and async executor.
 * Without this, Spring uses a single-threaded default scheduler.
 */
@Configuration
@EnableAsync
public class SchedulingConfig {

    private static final Logger log = LoggerFactory.getLogger(SchedulingConfig.class);

    /**
     * Custom thread pool for scheduled tasks.
     * poolSize: number of concurrent scheduled tasks supported.
     * threadNamePrefix: identifies threads in logs/debugging.
     * waitForTasksToCompleteOnShutdown: ensures graceful shutdown.
     */
    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        log.info("Configuring ThreadPoolTaskScheduler with pool size=5");
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);
        scheduler.setThreadNamePrefix("sched-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(30);
        return scheduler;
    }

    /**
     * Executor for @Async methods.
     * This allows async scheduled tasks to run without blocking the scheduler.
     */
    @Bean("taskExecutor")
    public Executor taskExecutor() {
        log.info("Configuring async task executor with pool size=3");
        ThreadPoolTaskScheduler executor = new ThreadPoolTaskScheduler();
        executor.setPoolSize(3);
        executor.setThreadNamePrefix("async-");
        return executor;
    }
}
