package com.learning.backend14;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Demonstrates the three @Scheduled scheduling modes.
 *
 * 1. cron:    flexible expression-based scheduling (like Unix cron)
 * 2. fixedRate: invokes at a fixed interval, regardless of execution time
 * 3. fixedDelay: invokes with a fixed delay AFTER the previous execution completes
 * 4. initialDelay: optionally delays the first execution
 */
@Component
public class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * Runs every 30 seconds using a cron expression.
     * Format: second minute hour day-of-month month day-of-week
     * "0/30 * * * * *" = at seconds 0, 30 of every minute.
     */
    @Scheduled(cron = "0/30 * * * * *")
    public void reportCurrentTime() {
        log.info("Cron task — Current time: {}", LocalDateTime.now().format(FORMATTER));
        System.out.println("[" + LocalDateTime.now().format(FORMATTER) + "] Cron: 30-second heartbeat");
    }

    /**
     * Runs every 10 seconds measured from the start of each invocation.
     * If the method takes longer than 10s, the next invocation starts
     * immediately after the previous one finishes.
     */
    @Scheduled(fixedRate = 10000, initialDelay = 5000)
    public void runFixedRate() {
        log.info("FixedRate task — executed every 10 seconds");
        System.out.println("[" + LocalDateTime.now().format(FORMATTER) + "] FixedRate: 10s interval");
    }

    /**
     * Runs every 15 seconds measured from the COMPLETION of the previous run.
     * If the method takes 3 seconds, the next run starts 15 seconds after
     * those 3 seconds end (i.e., 18 seconds total between starts).
     *
     * initialDelay = 3000 means the first run happens 3 seconds after startup.
     */
    @Scheduled(fixedDelay = 15000, initialDelay = 3000)
    public void runFixedDelay() {
        log.info("FixedDelay task — executing (takes ~2 seconds)");
        System.out.println("[" + LocalDateTime.now().format(FORMATTER) + "] FixedDelay: starting (2s work)...");
        simulateWork(2);
        System.out.println("[" + LocalDateTime.now().format(FORMATTER) + "] FixedDelay: done, next in 15s");
    }

    /**
     * A scheduled task that uses a zone-aware cron expression.
     * Runs at 9:00 AM every weekday (Monday-Friday).
     * The zone attribute specifies the time zone for evaluation.
     */
    @Scheduled(cron = "0 0 9 * * MON-FRI", zone = "America/New_York")
    public void runWeekdayMorning() {
        log.info("Weekday morning task — 9 AM ET");
        System.out.println("[" + LocalDateTime.now().format(FORMATTER) + "] Weekday morning task triggered");
    }

    private void simulateWork(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
