package com.learning.lab26;

import java.time.*;
import java.time.temporal.ChronoUnit;

/**
 * Demonstrates Instant, Duration, and machine-level timestamps.
 */
public class InstantTimestampExample {

    public static void showInstant() {
        System.out.println("=== Instant & Duration ===");

        Instant now = Instant.now();
        System.out.println("Current instant: " + now);

        Instant epoch = Instant.EPOCH;
        System.out.println("Epoch: " + epoch);

        long epochMillis = System.currentTimeMillis();
        Instant fromMillis = Instant.ofEpochMilli(epochMillis);
        System.out.println("From millis: " + fromMillis);

        Instant start = Instant.now();
        for (int i = 0; i < 1_000_000; i++) { Math.sqrt(i); }
        Instant end = Instant.now();

        Duration elapsed = Duration.between(start, end);
        System.out.println("Elapsed: " + elapsed.toMillis() + "ms");

        System.out.println("Seconds since epoch: " + now.getEpochSecond());
        System.out.println("Nano adjustment: " + now.getNano());

        Instant tomorrow = now.plus(1, ChronoUnit.DAYS);
        System.out.println("Tomorrow: " + tomorrow);
    }
}
