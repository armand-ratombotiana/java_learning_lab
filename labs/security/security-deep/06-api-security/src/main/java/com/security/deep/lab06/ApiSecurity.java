package com.security.deep.lab06;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;

public class ApiSecurity {

    public static class ApiKeyManager {
        private final Map<String, String> keyHashes = new ConcurrentHashMap<>();

        public String generateApiKey(String clientId) {
            SecureRandom sr = new SecureRandom();
            byte[] bytes = new byte[32];
            sr.nextBytes(bytes);
            String apiKey = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
            String hash = sha256(apiKey);
            keyHashes.put(clientId, hash);
            return apiKey;
        }

        public boolean validateApiKey(String clientId, String apiKey) {
            String storedHash = keyHashes.get(clientId);
            if (storedHash == null) return false;
            return storedHash.equals(sha256(apiKey));
        }

        public void revokeApiKey(String clientId) {
            keyHashes.remove(clientId);
        }

        private String sha256(String input) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                return Base64.getEncoder().encodeToString(md.digest(input.getBytes()));
            } catch (Exception e) { throw new RuntimeException(e); }
        }
    }

    public static class SlidingWindowRateLimiter {
        private final long maxRequests;
        private final long windowMs;
        private final Map<String, Deque<Long>> requestLog = new ConcurrentHashMap<>();

        public SlidingWindowRateLimiter(long maxRequests, long windowMs) {
            this.maxRequests = maxRequests;
            this.windowMs = windowMs;
        }

        public boolean allowRequest(String userId) {
            long now = System.currentTimeMillis();
            Deque<Long> timestamps = requestLog.computeIfAbsent(userId, k -> new ConcurrentLinkedDeque<>());
            synchronized (timestamps) {
                while (!timestamps.isEmpty() && timestamps.peekFirst() < now - windowMs) {
                    timestamps.pollFirst();
                }
                if (timestamps.size() >= maxRequests) return false;
                timestamps.addLast(now);
                return true;
            }
        }
    }

    public static class TokenBucketRateLimiter {
        private final Map<String, TokenBucket> buckets = new ConcurrentHashMap<>();
        private final long capacity;
        private final double refillRate; // tokens per second

        public TokenBucketRateLimiter(long capacity, double refillRate) {
            this.capacity = capacity;
            this.refillRate = refillRate;
        }

        public boolean allowRequest(String userId) {
            TokenBucket bucket = buckets.computeIfAbsent(userId, k -> new TokenBucket(capacity, refillRate));
            return bucket.tryConsume();
        }

        private static class TokenBucket {
            private double tokens;
            private long lastRefill;
            private final long capacity;
            private final double refillRate;

            TokenBucket(long capacity, double refillRate) {
                this.tokens = capacity;
                this.lastRefill = System.nanoTime();
                this.capacity = capacity;
                this.refillRate = refillRate;
            }

            synchronized boolean tryConsume() {
                long now = System.nanoTime();
                double elapsedSeconds = (now - lastRefill) / 1_000_000_000.0;
                tokens = Math.min(capacity, tokens + elapsedSeconds * refillRate);
                lastRefill = now;
                if (tokens >= 1) {
                    tokens -= 1;
                    return true;
                }
                return false;
            }
        }
    }

    public static List<String> validateInput(Map<String, Object> input, Map<String, String> schema) {
        List<String> errors = new ArrayList<>();
        for (var field : schema.entrySet()) {
            String fieldName = field.getKey();
            String expectedType = field.getValue();
            Object value = input.get(fieldName);
            if (value == null) {
                errors.add("Missing required field: " + fieldName);
                continue;
            }
            boolean valid = switch (expectedType) {
                case "string" -> value instanceof String;
                case "integer" -> value instanceof Number;
                case "boolean" -> value instanceof Boolean;
                case "email" -> value instanceof String && emailPattern.matcher((String) value).matches();
                default -> true;
            };
            if (!valid) errors.add("Invalid type for field: " + fieldName);
        }
        return errors;
    }

    private static final Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public static String sanitizeString(String input) {
        if (input == null) return null;
        return input.replaceAll("[<>\"'&]", "")
                    .replaceAll("[\r\n]", " ")
                    .trim();
    }

    public static String maskSensitiveData(String data, int visibleChars) {
        if (data == null || data.length() <= visibleChars) return data;
        return data.substring(0, visibleChars) + "*".repeat(data.length() - visibleChars);
    }
}
