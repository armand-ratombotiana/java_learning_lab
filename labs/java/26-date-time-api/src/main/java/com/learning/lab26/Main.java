package com.learning.lab26;

/**
 * Main entry point for Lab 26: Date & Time API.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   Lab 26: Date & Time API");
        System.out.println("========================================\n");

        LocalDateTimeExample.showLocalDateTime();
        System.out.println();
        ZonedDateTimeExample.showZonedDateTime();
        System.out.println();
        FormattingAndPeriodExample.showFormattingAndPeriod();

        System.out.println("\n========================================");
        System.out.println("   Lab 26 Complete");
        System.out.println("========================================");
    }
}
