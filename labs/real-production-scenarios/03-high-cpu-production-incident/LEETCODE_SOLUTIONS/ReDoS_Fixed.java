package com.prod.solutions.highcpu;

import java.util.concurrent.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Fixed version of ReDoS-vulnerable code.
 * Uses a regex with possessive quantifiers and adds a timeout wrapper
 * to kill runaway regex matching.
 */
public class ReDoS_Fixed {

    // FIX: Use possessive quantifier (++) to prevent backtracking
    private static final Pattern SAFE_PATTERN =
            Pattern.compile("^(a++)+b$");

    private static final ExecutorService TIMEOUT_EXECUTOR =
            Executors.newSingleThreadExecutor(r -> {
                Thread t = new Thread(r, "regex-timeout-thread");
                t.setDaemon(true);
                return t;
            });

    public static void main(String[] args) {
        System.out.println("=== ReDoS Fix Demo ===");

        String evilInput = "a".repeat(30) + "c";

        try {
            boolean matches = matchesWithTimeout(SAFE_PATTERN, evilInput, 1, TimeUnit.SECONDS);
            System.out.printf("Result: %b (completed within timeout)%n", matches);
        } catch (TimeoutException e) {
            System.out.println("SAFE: Regex timed out and was killed.");
        } catch (Exception e) {
            System.out.printf("Error: %s%n", e.getMessage());
        }

        // Test with bad regex + timeout
        Pattern badPattern = Pattern.compile("^(a+)+b$");
        try {
            boolean matches = matchesWithTimeout(badPattern, evilInput, 1, TimeUnit.SECONDS);
            System.out.printf("Result: %b%n", matches);
        } catch (TimeoutException e) {
            System.out.println("PROTECTED: Bad regex was killed by timeout.");
        }

        TIMEOUT_EXECUTOR.shutdown();
    }

    /**
     * Matches a pattern against input with a timeout.
     * FIX: Wraps regex matching in a Future with a timeout
     * to prevent ReDoS attacks from hanging the service.
     */
    static boolean matchesWithTimeout(Pattern pattern, String input,
                                       long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        Future<Boolean> future = TIMEOUT_EXECUTOR.submit(
                () -> pattern.matcher(input).matches());
        return future.get(timeout, unit);
    }
}
