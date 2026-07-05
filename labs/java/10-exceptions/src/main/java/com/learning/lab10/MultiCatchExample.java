package com.learning.lab10;

import java.io.*;
import java.sql.*;

/**
 * Demonstrates multi-catch, exception chaining, and best practices.
 */
public class MultiCatchExample {

    public static void showMultiCatch() {
        System.out.println("=== Multi-Catch & Chaining ===");

        try {
            riskyOperation("invalid");
        } catch (IOException | SQLException e) {
            System.out.println("Multi-catch caught: " + e.getClass().getSimpleName());
            System.out.println("  Message: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Generic catch: " + e.getMessage());
        }

        try {
            methodWithChaining();
        } catch (Exception e) {
            System.out.println("Root cause: " + e.getCause().getMessage());
        }
    }

    static void riskyOperation(String input) throws IOException, SQLException {
        if (input.equals("invalid")) {
            throw new IOException("Invalid input provided");
        }
    }

    static void methodWithChaining() throws Exception {
        try {
            throw new IllegalArgumentException("Original error");
        } catch (IllegalArgumentException e) {
            throw new Exception("Wrapper exception", e);
        }
    }
}
