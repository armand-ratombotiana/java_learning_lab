package com.devops.servicemesh;

import java.util.Random;

public class RetryPattern {
    private final int maxRetries;
    private final long baseDelayMs;
    private final Random random = new Random();

    public RetryPattern(int maxRetries, long baseDelayMs) {
        this.maxRetries = maxRetries;
        this.baseDelayMs = baseDelayMs;
    }

    public boolean execute(Runnable operation) {
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                operation.run();
                System.out.println("Succeeded on attempt " + attempt);
                return true;
            } catch (Exception e) {
                System.out.println("Attempt " + attempt + " failed: " + e.getMessage());
                if (attempt < maxRetries) {
                    long delay = baseDelayMs * (long) Math.pow(2, attempt - 1);
                    System.out.println("  Backoff: " + delay + "ms");
                    try { Thread.sleep(delay); } catch (InterruptedException ie) { }
                }
            }
        }
        System.out.println("All " + maxRetries + " retries exhausted");
        return false;
    }

    public static void main(String[] args) {
        RetryPattern retry = new RetryPattern(3, 100);
        retry.execute(() -> {
            if (new Random().nextInt(3) < 2) {
                throw new RuntimeException("Service unavailable");
            }
        });
    }
}
