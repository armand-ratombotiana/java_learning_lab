package com.learning.lab26;

import java.time.*;

/**
 * Demonstrates ZonedDateTime, ZoneId, and time zone conversions.
 */
public class ZonedDateTimeExample {

    public static void showZonedDateTime() {
        System.out.println("=== ZonedDateTime ===");

        ZonedDateTime nowHere = ZonedDateTime.now();
        System.out.println("Current zone: " + nowHere);

        ZoneId tokyo = ZoneId.of("Asia/Tokyo");
        ZonedDateTime tokyoTime = ZonedDateTime.now(tokyo);
        System.out.println("Tokyo time: " + tokyoTime);

        ZoneId london = ZoneId.of("Europe/London");
        ZonedDateTime londonTime = nowHere.withZoneSameInstant(london);
        System.out.println("London time (same instant): " + londonTime);

        ZoneId ny = ZoneId.of("America/New_York");
        ZonedDateTime nyTime = nowHere.withZoneSameInstant(ny);
        System.out.println("New York time: " + nyTime);

        System.out.println("Available zones: " + ZoneId.getAvailableZoneIds().size() + " total");

        OffsetDateTime utcNow = OffsetDateTime.now(ZoneOffset.UTC);
        System.out.println("UTC time: " + utcNow);

        OffsetDateTime offset5 = OffsetDateTime.now(ZoneOffset.ofHours(5));
        System.out.println("UTC+5: " + offset5);
    }
}
