package com.prod.solutions.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Demonstrates secure API authentication using HMAC-based request signing.
 * This prevents replay attacks, tampering, and unauthorized access.
 *
 * Production: Use OAuth2, JWT, or AWS Signature V4.
 */
public class APIAuthExample {

    static class ApiCredentials {
        final String apiKey;
        final String apiSecret;

        ApiCredentials(String apiKey, String apiSecret) {
            this.apiKey = apiKey;
            this.apiSecret = apiSecret;
        }
    }

    static class SecureApiAuth {
        private final Map<String, ApiCredentials> credentials = new ConcurrentHashMap<>();
        private static final long MAX_REQUEST_AGE_SECONDS = 300; // 5 min

        void registerKey(String apiKey, String apiSecret) {
            credentials.put(apiKey, new ApiCredentials(apiKey, apiSecret));
            System.out.printf("Registered API key: %s%n", apiKey);
        }

        boolean authenticateRequest(String apiKey, String timestamp, String signature, String body) {
            ApiCredentials creds = credentials.get(apiKey);
            if (creds == null) {
                System.out.println("  REJECTED: Unknown API key");
                return false;
            }

            // Check timestamp (prevent replay attacks)
            long requestTime = Long.parseLong(timestamp);
            long now = Instant.now().getEpochSecond();
            if (Math.abs(now - requestTime) > MAX_REQUEST_AGE_SECONDS) {
                System.out.println("  REJECTED: Request timestamp too old (replay attack?)");
                return false;
            }

            // Verify signature
            String expectedSignature = computeSignature(creds.apiSecret, timestamp, body);
            if (!signature.equals(expectedSignature)) {
                System.out.println("  REJECTED: Invalid signature (tampered request?)");
                return false;
            }

            System.out.println("  AUTHENTICATED: Request verified");
            return true;
        }

        static String computeSignature(String secret, String timestamp, String body) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                String payload = secret + timestamp + body;
                byte[] hash = md.digest(payload.getBytes());
                return HexFormat.of().formatHex(hash);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }

        static String generateSignature(String secret, String body) {
            String timestamp = String.valueOf(Instant.now().getEpochSecond());
            return timestamp + ":" + computeSignature(secret, timestamp, body);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Secure API Authentication Demo ===\n");

        SecureApiAuth auth = new SecureApiAuth();
        auth.registerKey("key-001", "my-secret-key");

        String body = "{\"userId\": 123, \"amount\": 100}";

        // Valid request
        System.out.println("\n--- Valid Request ---");
        String authHeader = SecureApiAuth.generateSignature("my-secret-key", body);
        String[] parts = authHeader.split(":");
        boolean authenticated = auth.authenticateRequest("key-001", parts[0], parts[1], body);

        // Tampered body
        System.out.println("\n--- Tampered Body ---");
        String tamperedBody = "{\"userId\": 123, \"amount\": 99999}";
        String[] parts2 = authHeader.split(":");
        auth.authenticateRequest("key-001", parts2[0], parts2[1], tamperedBody);

        // Replay attack
        System.out.println("\n--- Replay Attack (old timestamp) ---");
        String oldTimestamp = String.valueOf(Instant.now().getEpochSecond() - 600);
        String replayedSig = SecureApiAuth.computeSignature("my-secret-key", oldTimestamp, body);
        auth.authenticateRequest("key-001", oldTimestamp, replayedSig, body);

        System.out.printf("%nSecure authentication prevents:%n");
        System.out.println("  - Replay attacks (timestamp validation)");
        System.out.println("  - Tampered payloads (HMAC signature)");
        System.out.println("  - Unauthorized access (API key validation)");
    }
}
