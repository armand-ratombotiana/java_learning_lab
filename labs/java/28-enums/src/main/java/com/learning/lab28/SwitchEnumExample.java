package com.learning.lab28;

/**
 * Demonstrates switch on enum types with arrow syntax and exhaustive handling.
 */
public class SwitchEnumExample {

    public static void showSwitchOnEnum() {
        System.out.println("=== Switch on Enum ===");

        Status current = Status.PENDING;

        String message = switch (current) {
            case PENDING -> "Order is pending payment";
            case PROCESSING -> "Order is being prepared";
            case SHIPPED -> "Order is on the way";
            case DELIVERED -> "Order delivered successfully";
            case CANCELLED -> "Order was cancelled";
        };
        System.out.println("Status: " + current + " -> " + message);

        for (Status status : Status.values()) {
            System.out.println("  " + status + " (" + status.getLabel() + ")");
        }
    }
}

enum Status {
    PENDING("Awaiting Payment"),
    PROCESSING("Preparing Order"),
    SHIPPED("In Transit"),
    DELIVERED("Completed"),
    CANCELLED("Voided");

    private final String label;

    Status(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
