package com.learning.lab10;

/**
 * Demonstrates checked vs unchecked exceptions and creating custom exceptions.
 */
public class CustomExceptionExample {

    public static void showCustomExceptions() {
        System.out.println("=== Checked vs Unchecked & Custom Exceptions ===");

        try {
            withdraw(50.0);
            withdraw(200.0);
        } catch (InsufficientFundsException e) {
            System.out.println("Caught custom checked exception: " + e.getMessage());
            System.out.println("  Shortfall: $" + e.getShortfall());
        }

        try {
            validateAge(15);
        } catch (IllegalArgumentException e) {
            System.out.println("Caught unchecked exception: " + e.getMessage());
        }

        System.out.println("Program continues after exception handling");
    }

    static void withdraw(double amount) throws InsufficientFundsException {
        double balance = 100.0;
        if (amount > balance) {
            throw new InsufficientFundsException(amount - balance, 
                "Insufficient funds: requested $" + amount + ", balance $" + balance);
        }
        System.out.println("Withdrew $" + amount);
    }

    static void validateAge(int age) {
        if (age < 18) {
            throw new IllegalArgumentException("Must be 18 or older, got: " + age);
        }
        System.out.println("Age validated: " + age);
    }
}

class InsufficientFundsException extends Exception {
    private final double shortfall;

    public InsufficientFundsException(double shortfall, String message) {
        super(message);
        this.shortfall = shortfall;
    }

    public double getShortfall() {
        return shortfall;
    }
}
