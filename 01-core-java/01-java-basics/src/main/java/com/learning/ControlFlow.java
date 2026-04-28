package com.learning;

/**
 * Demonstrates Java control flow statements including if-else, switch,
 * loops (for, while, do-while), and jump statements (break, continue, return).
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class ControlFlow {
    
    /**
     * Demonstrates all control flow statements in Java.
     */
    public static void demonstrateControlFlow() {
        demonstrateIfElse();
        demonstrateSwitch();
        demonstrateForLoop();
        demonstrateWhileLoop();
        demonstrateDoWhileLoop();
        demonstrateJumpStatements();
    }
    
    /**
     * Demonstrates if-else statements.
     */
    private static void demonstrateIfElse() {
        System.out.println("If-Else Statements:");
        
        int score = 85;
        
        // Simple if
        if (score >= 90) {
            System.out.println("Grade: A");
        } else if (score >= 80) {
            System.out.println("Grade: B");
        } else if (score >= 70) {
            System.out.println("Grade: C");
        } else if (score >= 60) {
            System.out.println("Grade: D");
        } else {
            System.out.println("Grade: F");
        }
        
        // Nested if
        int age = 25;
        boolean hasLicense = true;
        
        if (age >= 18) {
            if (hasLicense) {
                System.out.println("Can drive");
            } else {
                System.out.println("Cannot drive - no license");
            }
        } else {
            System.out.println("Cannot drive - too young");
        }
    }
    
    /**
     * Demonstrates switch statements (traditional and enhanced).
     */
    private static void demonstrateSwitch() {
        System.out.println("\nSwitch Statements:");
        
        // Traditional switch
        int day = 3;
        String dayName;
        
        switch (day) {
            case 1:
                dayName = "Monday";
                break;
            case 2:
                dayName = "Tuesday";
                break;
            case 3:
                dayName = "Wednesday";
                break;
            case 4:
                dayName = "Thursday";
                break;
            case 5:
                dayName = "Friday";
                break;
            case 6:
                dayName = "Saturday";
                break;
            case 7:
                dayName = "Sunday";
                break;
            default:
                dayName = "Invalid day";
        }
        System.out.println("Traditional switch - Day " + day + ": " + dayName);
        
        // Enhanced switch (Java 14+)
        String month = "March";
        int days = switch (month) {
            case "January", "March", "May", "July", "August", "October", "December" -> 31;
            case "April", "June", "September", "November" -> 30;
            case "February" -> 28;
            default -> 0;
        };
        System.out.println("Enhanced switch - " + month + " has " + days + " days");
    }
    
    /**
     * Demonstrates for loops (traditional, enhanced, and nested).
     */
    private static void demonstrateForLoop() {
        System.out.println("\nFor Loops:");
        
        // Traditional for loop
        System.out.print("Traditional for: ");
        for (int i = 1; i <= 5; i++) {
            System.out.print(i + " ");
        }
        System.out.println();
        
        // Enhanced for loop (for-each)
        int[] numbers = {10, 20, 30, 40, 50};
        System.out.print("Enhanced for: ");
        for (int num : numbers) {
            System.out.print(num + " ");
        }
        System.out.println();
        
        // Nested for loop
        System.out.println("Nested for (multiplication table):");
        for (int i = 1; i <= 3; i++) {
            for (int j = 1; j <= 3; j++) {
                System.out.print(i * j + "\t");
            }
            System.out.println();
        }
    }
    
    /**
     * Demonstrates while loops.
     */
    private static void demonstrateWhileLoop() {
        System.out.println("\nWhile Loop:");
        
        int count = 1;
        System.out.print("While loop: ");
        while (count <= 5) {
            System.out.print(count + " ");
            count++;
        }
        System.out.println();
    }
    
    /**
     * Demonstrates do-while loops.
     */
    private static void demonstrateDoWhileLoop() {
        System.out.println("\nDo-While Loop:");
        
        int count = 1;
        System.out.print("Do-while loop: ");
        do {
            System.out.print(count + " ");
            count++;
        } while (count <= 5);
        System.out.println();
        
        // Do-while executes at least once
        int x = 10;
        System.out.print("Do-while (condition false): ");
        do {
            System.out.print(x + " ");
            x++;
        } while (x < 10); // Condition is false, but executes once
        System.out.println();
    }
    
    /**
     * Demonstrates jump statements (break, continue, return).
     */
    private static void demonstrateJumpStatements() {
        System.out.println("\nJump Statements:");
        
        // Break statement
        System.out.print("Break statement: ");
        for (int i = 1; i <= 10; i++) {
            if (i == 6) {
                break; // Exit loop when i is 6
            }
            System.out.print(i + " ");
        }
        System.out.println();
        
        // Continue statement
        System.out.print("Continue statement: ");
        for (int i = 1; i <= 10; i++) {
            if (i % 2 == 0) {
                continue; // Skip even numbers
            }
            System.out.print(i + " ");
        }
        System.out.println();
        
        // Labeled break
        System.out.println("Labeled break:");
        outer: for (int i = 1; i <= 3; i++) {
            for (int j = 1; j <= 3; j++) {
                if (i == 2 && j == 2) {
                    break outer; // Break out of outer loop
                }
                System.out.println("i=" + i + ", j=" + j);
            }
        }
    }
    
    /**
     * Checks if a number is positive, negative, or zero.
     * 
     * @param number the number to check
     * @return "positive", "negative", or "zero"
     */
    public static String checkNumber(int number) {
        if (number > 0) {
            return "positive";
        } else if (number < 0) {
            return "negative";
        } else {
            return "zero";
        }
    }
    
    /**
     * Gets the day name for a given day number.
     * 
     * @param day the day number (1-7)
     * @return the day name
     */
    public static String getDayName(int day) {
        return switch (day) {
            case 1 -> "Monday";
            case 2 -> "Tuesday";
            case 3 -> "Wednesday";
            case 4 -> "Thursday";
            case 5 -> "Friday";
            case 6 -> "Saturday";
            case 7 -> "Sunday";
            default -> "Invalid day";
        };
    }
    
    /**
     * Calculates the sum of numbers from 1 to n.
     * 
     * @param n the upper limit
     * @return the sum
     */
    public static int sumUpTo(int n) {
        int sum = 0;
        for (int i = 1; i <= n; i++) {
            sum += i;
        }
        return sum;
    }
    
    /**
     * Calculates factorial using recursion.
     * 
     * @param n the number
     * @return the factorial
     */
    public static long factorial(int n) {
        if (n <= 1) {
            return 1;
        }
        return n * factorial(n - 1);
    }
}