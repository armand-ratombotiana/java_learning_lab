package com.learning.lab01;

/**
 * Demonstrates if-else, switch, for, while, and do-while control flow.
 */
public class ControlFlowExample {

    public static void showControlFlow() {
        System.out.println("=== Control Flow ===");

        int score = 85;
        if (score >= 90) {
            System.out.println("Grade: A");
        } else if (score >= 80) {
            System.out.println("Grade: B");
        } else {
            System.out.println("Grade: C or lower");
        }

        String day = "MONDAY";
        switch (day) {
            case "MONDAY" -> System.out.println("Start of work week");
            case "FRIDAY" -> System.out.println("Almost weekend");
            default -> System.out.println("Midweek");
        }

        System.out.print("For loop (1 to 5): ");
        for (int i = 1; i <= 5; i++) {
            System.out.print(i + " ");
        }
        System.out.println();

        System.out.print("While loop: ");
        int count = 0;
        while (count < 3) {
            System.out.print(count + " ");
            count++;
        }
        System.out.println();

        System.out.print("Do-while loop: ");
        int n = 0;
        do {
            System.out.print(n + " ");
            n++;
        } while (n < 3);
        System.out.println();
    }
}
