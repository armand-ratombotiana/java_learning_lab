package com.prod.solutions.highcpu;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Demonstrates a ReDoS (Regular Expression Denial of Service) attack
 * through catastrophic backtracking. This mimics the real-world incident
 * where a poorly-written regex caused 800% CPU usage.
 *
 * BUG: The regex (a+)+b exhibits catastrophic backtracking when given
 * input that almost matches but doesn't. The nested quantifiers create
 * exponential matching time.
 */
public class ReDoSExample {

    // BUG: Nested quantifiers cause catastrophic backtracking
    // For input like "aaaaaaaaaaaaaac", the engine tries 2^n paths
    private static final Pattern VULNERABLE_PATTERN =
            Pattern.compile("^(a+)+b$");

    // Safe version using possessive quantifier
    private static final Pattern SAFE_PATTERN =
            Pattern.compile("^(a++)+b$");

    public static void main(String[] args) {
        System.out.println("=== ReDoS Catastrophic Backtracking Demo ===");

        // Innocuous input — matches quickly
        String goodInput = "aaaaaaaab";
        testPattern("Good input", goodInput, false);

        // Evil input — almost matches but doesn't
        // "a" repeated 25 times then "c" — causes 2^25 backtracking paths
        String evilInput = "a".repeat(25) + "c";
        testPattern("Evil input (25 a's + 'c')", evilInput, false);

        System.out.println("\n--- Comparing with safe pattern ---");
        testPatternSafe("Same evil input with safe regex", evilInput);
    }

    static void testPattern(String label, String input, boolean safe) {
        Pattern p = safe ? SAFE_PATTERN : VULNERABLE_PATTERN;
        long start = System.nanoTime();
        try {
            Matcher m = p.matcher(input);
            boolean matches = m.matches();
            long elapsed = (System.nanoTime() - start) / 1_000_000;
            System.out.printf("%s: %b (took %d ms)%n", label, matches, elapsed);
            if (elapsed > 100) {
                System.out.println("  WARNING: ReDoS vulnerability detected!");
            }
        } catch (StackOverflowError e) {
            long elapsed = (System.nanoTime() - start) / 1_000_000;
            System.out.printf("%s: STACK OVERFLOW (took %d ms)%n", label, elapsed);
        } catch (OutOfMemoryError e) {
            long elapsed = (System.nanoTime() - start) / 1_000_000;
            System.out.printf("%s: OUT OF MEMORY (took %d ms)%n", label, elapsed);
        }
    }

    static void testPatternSafe(String label, String input) {
        long start = System.nanoTime();
        Matcher m = SAFE_PATTERN.matcher(input);
        boolean matches = m.matches();
        long elapsed = (System.nanoTime() - start) / 1_000_000;
        System.out.printf("%s: %b (took %d ms)%n", label, matches, elapsed);
    }
}
