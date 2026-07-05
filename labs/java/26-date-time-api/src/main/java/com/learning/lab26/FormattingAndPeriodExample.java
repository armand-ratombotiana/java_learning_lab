package com.learning.lab26;

import java.time.*;
import java.time.format.*;
import java.time.temporal.ChronoUnit;

/**
 * Demonstrates DateTimeFormatter, Period, Duration, and ChronoUnit.
 */
public class FormattingAndPeriodExample {

    public static void showFormattingAndPeriod() {
        System.out.println("=== Formatting, Period & Duration ===");

        LocalDateTime now = LocalDateTime.now();

        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        System.out.println("ISO format: " + now.format(formatter1));

        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy 'at' HH:mm");
        System.out.println("Custom format: " + now.format(formatter2));

        DateTimeFormatter formatter3 = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        System.out.println("EU format: " + now.format(formatter3));

        LocalDate start = LocalDate.of(2020, 1, 1);
        LocalDate end = LocalDate.now();
        Period period = Period.between(start, end);
        System.out.println("Since 2020-01-01: " + period.getYears() + " years, " 
            + period.getMonths() + " months, " + period.getDays() + " days");

        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.now();
        Duration duration = Duration.between(startTime, endTime);
        System.out.println("Since 09:00: " + duration.toHours() + " hours, " 
            + duration.toMinutesPart() + " minutes");

        long daysBetween = ChronoUnit.DAYS.between(start, end);
        System.out.println("Days between: " + daysBetween);
    }
}
