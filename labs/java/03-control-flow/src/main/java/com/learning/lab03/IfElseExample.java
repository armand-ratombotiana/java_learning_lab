package com.learning.lab03;

/**
 * Demonstrates if-else, else-if ladder, and ternary operator.
 */
public class IfElseExample {

    public static void showIfElse() {
        System.out.println("=== if-else Statements ===");

        int temperature = 30;

        if (temperature > 35) {
            System.out.println("Very hot!");
        } else if (temperature > 25) {
            System.out.println("Warm");
        } else if (temperature > 15) {
            System.out.println("Pleasant");
        } else {
            System.out.println("Cold");
        }

        int number = 7;
        String evenOdd = (number % 2 == 0) ? "even" : "odd";
        System.out.println(number + " is " + evenOdd + " (using ternary)");

        boolean isLoggedIn = true;
        if (isLoggedIn) {
            System.out.println("User is logged in");
        }
    }
}
