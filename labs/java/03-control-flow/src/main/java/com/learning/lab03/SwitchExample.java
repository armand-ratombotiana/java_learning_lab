package com.learning.lab03;

/**
 * Demonstrates traditional switch, switch expressions (Java 14+), and arrow syntax.
 */
public class SwitchExample {

    public static void showSwitch() {
        System.out.println("=== Switch Statements & Expressions ===");

        String day = "WEDNESDAY";

        String result = switch (day) {
            case "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY" -> "Weekday";
            case "SATURDAY", "SUNDAY" -> "Weekend";
            default -> "Invalid day";
        };
        System.out.println(day + " is a " + result);

        int num = 2;
        String word = switch (num) {
            case 1 -> "One";
            case 2 -> "Two";
            case 3 -> "Three";
            default -> "Other";
        };
        System.out.println(num + " -> " + word);

        int score = 85;
        String grade = switch (score / 10) {
            case 10, 9 -> "A";
            case 8 -> "B";
            case 7 -> "C";
            case 6 -> "D";
            default -> "F";
        };
        System.out.println("Score " + score + " -> Grade " + grade);
    }
}
