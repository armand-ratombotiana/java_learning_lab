package com.learning.lab26;

import org.junit.jupiter.api.*;
import java.time.*;
import java.time.format.*;
import static org.junit.jupiter.api.Assertions.*;

class DateTimeTest {

    @Test
    @DisplayName("LocalDate creation and access")
    void localDateCreation() {
        LocalDate date = LocalDate.of(2024, 1, 15);
        assertEquals(2024, date.getYear());
        assertEquals(1, date.getMonthValue());
        assertEquals(15, date.getDayOfMonth());
    }

    @Test
    @DisplayName("LocalTime creation and access")
    void localTimeCreation() {
        LocalTime time = LocalTime.of(14, 30, 0);
        assertEquals(14, time.getHour());
        assertEquals(30, time.getMinute());
    }

    @Test
    @DisplayName("LocalDateTime creation and access")
    void localDateTimeCreation() {
        LocalDateTime dt = LocalDateTime.of(2024, 6, 15, 10, 30);
        assertEquals(2024, dt.getYear());
        assertEquals(6, dt.getMonthValue());
        assertEquals(15, dt.getDayOfMonth());
        assertEquals(10, dt.getHour());
    }

    @Test
    @DisplayName("Instant represents a moment on timeline")
    void instantCreation() {
        Instant now = Instant.now();
        assertNotNull(now);
        assertTrue(now.toEpochMilli() > 0);
    }

    @Test
    @DisplayName("Instant plus/minus operations")
    void instantPlusMinus() {
        Instant now = Instant.now();
        Instant later = now.plus(Duration.ofHours(1));
        assertTrue(later.isAfter(now));
        Instant earlier = now.minus(Duration.ofMinutes(30));
        assertTrue(earlier.isBefore(now));
    }

    @Test
    @DisplayName("Duration between instants")
    void durationBetween() {
        Instant start = Instant.parse("2024-01-01T00:00:00Z");
        Instant end = Instant.parse("2024-01-01T01:30:00Z");
        Duration d = Duration.between(start, end);
        assertEquals(90, d.toMinutes());
    }

    @Test
    @DisplayName("Period between dates")
    void periodBetween() {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 12, 31);
        Period p = Period.between(start, end);
        assertEquals(11, p.getMonths());
        assertEquals(30, p.getDays());
    }

    @Test
    @DisplayName("DateTimeFormatter formats date")
    void dateTimeFormatterFormat() {
        LocalDate date = LocalDate.of(2024, 12, 25);
        String formatted = date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        assertEquals("2024/12/25", formatted);
    }

    @Test
    @DisplayName("DateTimeFormatter parses date")
    void dateTimeFormatterParse() {
        String text = "2024-03-15";
        LocalDate parsed = LocalDate.parse(text, DateTimeFormatter.ISO_LOCAL_DATE);
        assertEquals(LocalDate.of(2024, 3, 15), parsed);
    }

    @Test
    @DisplayName("ZonedDateTime with timezone")
    void zonedDateTime() {
        ZonedDateTime zdt = ZonedDateTime.of(
            LocalDateTime.of(2024, 6, 15, 12, 0),
            ZoneId.of("America/New_York"));
        assertEquals(ZoneId.of("America/New_York"), zdt.getZone());
        assertEquals(12, zdt.getHour());
    }

    @Test
    @DisplayName("ZonedDateTime conversion between timezones")
    void zonedDateTimeConversion() {
        ZonedDateTime ny = ZonedDateTime.of(
            LocalDateTime.of(2024, 6, 15, 12, 0),
            ZoneId.of("America/New_York"));
        ZonedDateTime london = ny.withZoneSameInstant(ZoneId.of("Europe/London"));
        assertEquals(17, london.getHour());
    }
}
