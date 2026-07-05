package com.learning.lab26;

import java.time.*;

/**
 * Demonstrates LocalDate, LocalTime, LocalDateTime creation and operations.
 */
public class LocalDateTimeExample {

    public static void showLocalDateTime() {
        System.out.println("=== LocalDate, LocalTime, LocalDateTime ===");

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        LocalDateTime current = LocalDateTime.now();

        System.out.println("Today: " + today);
        System.out.println("Current time: " + now);
        System.out.println("Current date-time: " + current);

        LocalDate specific = LocalDate.of(2024, Month.DECEMBER, 25);
        System.out.println("Christmas 2024: " + specific);

        LocalTime meeting = LocalTime.of(14, 30);
        System.out.println("Meeting time: " + meeting);

        LocalDateTime party = LocalDateTime.of(2024, 12, 31, 23, 59, 59);
        System.out.println("New Year moment: " + party);

        System.out.println("Today + 7 days: " + today.plusDays(7));
        System.out.println("Today - 1 month: " + today.minusMonths(1));
        System.out.println("Day of year: " + today.getDayOfYear());
        System.out.println("Is leap year? " + today.isLeapYear());
    }
}
