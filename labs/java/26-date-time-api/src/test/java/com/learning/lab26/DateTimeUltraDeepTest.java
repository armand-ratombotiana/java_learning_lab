package com.learning.lab26;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.*;
import java.time.temporal.*;

class DateTimeUltraDeepTest {

    @Test
    void instantToEpochMilli() {
        Instant instant = Instant.ofEpochMilli(1000);
        assertEquals(1000, instant.toEpochMilli());
    }

    @Test
    void localDateMinusDays() {
        LocalDate date = LocalDate.of(2024, 3, 1);
        assertEquals(LocalDate.of(2024, 2, 29), date.minusDays(1));
    }

    @Test
    void localDateLeapYear() {
        assertTrue(Year.isLeap(2024));
        assertFalse(Year.isLeap(2023));
    }

    @Test
    void dayOfWeekEnum() {
        LocalDate date = LocalDate.of(2024, 7, 20);
        assertEquals(DayOfWeek.SATURDAY, date.getDayOfWeek());
    }

    @Test
    void monthEnum() {
        LocalDate date = LocalDate.of(2024, 12, 25);
        assertEquals(Month.DECEMBER, date.getMonth());
    }

    @Test
    void localDateWithAdjuster() {
        LocalDate date = LocalDate.of(2024, 6, 1);
        LocalDate nextMonday = date.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        assertEquals(DayOfWeek.MONDAY, nextMonday.getDayOfWeek());
    }

    @Test
    void durationOfHoursMinutes() {
        Duration d = Duration.ofHours(2).plusMinutes(30);
        assertEquals(150, d.toMinutes());
    }

    @Test
    void localDateTimeTruncatedTo() {
        LocalDateTime dt = LocalDateTime.of(2024, 6, 15, 10, 30, 45, 123_000_000);
        LocalDateTime truncated = dt.truncatedTo(ChronoUnit.MINUTES);
        assertEquals(10, truncated.getHour());
        assertEquals(30, truncated.getMinute());
        assertEquals(0, truncated.getSecond());
    }

    @Test
    void zoneOffsetHours() {
        ZoneOffset offset = ZoneOffset.ofHours(-5);
        assertEquals(-5, offset.getTotalSeconds() / 3600);
    }
}
