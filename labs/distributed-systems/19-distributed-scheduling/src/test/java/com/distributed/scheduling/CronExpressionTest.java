package com.distributed.scheduling;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class CronExpressionTest {

    @Test
    void testEveryMinute() {
        CronExpression cron = new CronExpression("* * * * *");
        LocalDateTime now = LocalDateTime.of(2026, 7, 8, 10, 0);
        assertTrue(cron.matches(now));
        LocalDateTime next = cron.nextFireTime(now);
        assertEquals(now.plusMinutes(1), next);
    }

    @Test
    void testEveryHour() {
        CronExpression cron = new CronExpression("0 * * * *");
        LocalDateTime time = LocalDateTime.of(2026, 7, 8, 10, 0);
        assertTrue(cron.matches(time));
        assertFalse(cron.matches(time.withMinute(30)));
    }

    @Test
    void testSpecificTime() {
        CronExpression cron = new CronExpression("30 14 * * *");
        LocalDateTime time = LocalDateTime.of(2026, 7, 8, 14, 30);
        assertTrue(cron.matches(time));
        assertFalse(cron.matches(time.withHour(15)));
    }

    @Test
    void testDayOfWeek() {
        CronExpression cron = new CronExpression("0 9 * * 1");
        LocalDateTime monday = LocalDateTime.of(2026, 7, 13, 9, 0);
        LocalDateTime tuesday = LocalDateTime.of(2026, 7, 14, 9, 0);
        assertTrue(cron.matches(monday));
        assertFalse(cron.matches(tuesday));
    }

    @Test
    void testStep() {
        CronExpression cron = new CronExpression("*/15 * * * *");
        LocalDateTime t1 = LocalDateTime.of(2026, 7, 8, 10, 0);
        LocalDateTime t2 = LocalDateTime.of(2026, 7, 8, 10, 15);
        LocalDateTime t3 = LocalDateTime.of(2026, 7, 8, 10, 10);
        assertTrue(cron.matches(t1));
        assertTrue(cron.matches(t2));
        assertFalse(cron.matches(t3));
    }
}
