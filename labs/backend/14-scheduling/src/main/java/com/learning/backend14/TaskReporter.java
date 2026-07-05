package com.learning.backend14;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Demonstrates combining @Scheduled with @Async for non-blocking execution.
 *
 * @Async allows the scheduled method to run in a separate thread pool,
 * so the scheduler thread is not blocked by long-running tasks.
 */
@Component
public class TaskReporter {

    private static final Logger log = LoggerFactory.getLogger(TaskReporter.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final AtomicInteger taskCounter = new AtomicInteger(0);

    /**
     * Runs every 20 seconds with a cron expression.
     * Interacts with other services and logs a summary report.
     */
    @Scheduled(cron = "0/20 * * * * *")
    public void generateReport() {
        int taskNumber = taskCounter.incrementAndGet();
        log.info("Generating scheduled report #{}", taskNumber);
        System.out.println("[" + LocalDateTime.now().format(FORMATTER)
            + "] Report #" + taskNumber + " generated"
            + " | Memory: " + (Runtime.getRuntime().totalMemory() / 1024 / 1024) + "MB"
            + " | Processors: " + Runtime.getRuntime().availableProcessors());
    }

    /**
     * Async scheduled task — runs every minute on a separate thread.
     * The @Async annotation requires @EnableAsync on a configuration class.
     */
    @Async
    @Scheduled(fixedRate = 60000)
    public void asyncCleanupTask() {
        log.info("Async cleanup task running on thread: {}", Thread.currentThread().getName());
        System.out.println("[" + LocalDateTime.now().format(FORMATTER)
            + "] Async cleanup complete"
            + " | Thread: " + Thread.currentThread().getName());
    }
}
