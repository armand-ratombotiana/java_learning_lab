package com.learning.lab.module06;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 06: Exception Handling ===");
        tryCatchDemo();
        multiCatchDemo();
        tryWithResourcesDemo();
        throwThrowsDemo();
        customExceptionDemo();
        exceptionHierarchyDemo();
    }

    static void tryCatchDemo() {
        System.out.println("\n--- Try-Catch ---");
        try {
            int result = 10 / 0;
        } catch (ArithmeticException e) {
            System.out.println("Caught: " + e.getMessage());
        }
        System.out.println("Continuing after exception");

        try {
            int[] arr = {1, 2, 3};
            System.out.println(arr[5]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Array error: " + e.getMessage());
        }
    }

    static void multiCatchDemo() {
        System.out.println("\n--- Multi-Catch ---");
        try {
            String s = null;
            System.out.println(s.length());
        } catch (NullPointerException | ArithmeticException e) {
            System.out.println("Caught: " + e.getClass().getSimpleName());
        }
    }

    static void tryWithResourcesDemo() {
        System.out.println("\n--- Try-with-Resources ---");
        try (AutoCloseable resource = () -> System.out.println("Closing")) {
            System.out.println("Using resource");
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    static void throwThrowsDemo() {
        System.out.println("\n--- Throw/Throws ---");
        try {
            validateAge(15);
        } catch (InvalidAgeException e) {
            System.out.println("Validation failed: " + e.getMessage());
        }
    }

    static void validateAge(int age) throws InvalidAgeException {
        if (age < 18) {
            throw new InvalidAgeException("Age must be 18 or older");
        }
        System.out.println("Valid age");
    }

    static void customExceptionDemo() {
        System.out.println("\n--- Custom Exception ---");
        try {
            throw new CustomException("Custom error occurred");
        } catch (CustomException e) {
            System.out.println("Caught custom: " + e.getMessage());
            System.out.println("Error code: " + e.getErrorCode());
        }
    }

    static void exceptionHierarchyDemo() {
        System.out.println("\n--- Exception Hierarchy ---");
        try {
            throw new Exception("Base exception");
        } catch (RuntimeException e) {
            System.out.println("Cannot catch RuntimeException here");
        } catch (Exception e) {
            System.out.println("Caught: " + e.getMessage());
        }

        try {
            int[] arr = null;
            arr[0] = 1;
        } catch (NullPointerException e) {
            System.out.println("Unchecked exception caught");
        }
    }
}

class InvalidAgeException extends Exception {
    public InvalidAgeException(String message) {
        super(message);
    }
}

class CustomException extends Exception {
    private final int errorCode = 1001;
    public CustomException(String message) {
        super(message);
    }
    public int getErrorCode() { return errorCode; }
}