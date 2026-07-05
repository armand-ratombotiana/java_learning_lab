package com.learning.lab01;

import java.util.Scanner;

/**
 * Demonstrates basic input/output using Scanner and System.out.
 */
public class BasicIOExample {

    public static void showBasicIO() {
        System.out.println("=== Basic Input/Output ===");

        System.out.println("Printing with println (adds newline)");
        System.out.print("Printing with print (no newline) ");
        System.out.printf("Formatted: %s is %d years old%n", "Alice", 30);

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter your name: ");
            String name = scanner.nextLine();
            System.out.println("Hello, " + name + "!");
        }
    }
}
