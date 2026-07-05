package com.security01;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simulates HTTP Basic Authentication.
 * 
 * SECURITY CONCEPT: Basic Authentication sends credentials
 * as Base64-encoded "username:password" in the Authorization header.
 * Base64 is NOT encryption — it is encoding. Must ALWAYS use HTTPS
 * (TLS) to protect credentials in transit. Without TLS, credentials
 * are visible to anyone capturing network traffic.
 * 
 * NEVER use Basic Auth without TLS. Prefer token-based auth (JWT)
 * or OAuth2 for modern applications.
 */
public class BasicAuthSimulator {

    // In-memory user store (in production, use a database)
    private static final Map<String, String> userStore = new ConcurrentHashMap<>();

    static {
        // Passwords would be hashed in production
        userStore.put("admin", "hashed:$2a$12$exampleadminhash");
        userStore.put("user", "hashed:$2a$12$exampleuserhash");
    }

    /**
     * Parses the Authorization header and extracts credentials.
     * Expected format: "Basic base64(username:password)"
     */
    public static String[] parseBasicAuth(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Basic ")) {
            throw new IllegalArgumentException("Missing or invalid Authorization header");
        }
        // Decode Base64 — this is encoding, NOT encryption!
        String base64Credentials = authorizationHeader.substring("Basic ".length()).trim();
        String decoded = new String(Base64.getDecoder().decode(base64Credentials),
                StandardCharsets.UTF_8);
        String[] credentials = decoded.split(":", 2);
        if (credentials.length != 2) {
            throw new IllegalArgumentException("Invalid credential format");
        }
        return credentials; // [0] = username, [1] = password
    }

    /**
     * Simulates authentication against stored credentials.
     * In production, verify against hashed password using BCrypt.
     */
    public static boolean authenticate(String username, String password) {
        String stored = userStore.get(username);
        if (stored == null) {
            return false; // User not found — do not reveal which field is wrong
        }
        // In production: use BCryptPasswordEncoder.matches(password, storedHash)
        return "password123".equals(password);
    }

    /**
     * Builds the WWW-Authenticate header for 401 responses.
     * Tells the client which auth scheme is expected.
     */
    public static String buildAuthenticateHeader(String realm) {
        return "Basic realm=\"" + realm + "\", charset=\"UTF-8\"";
    }

    public static void main(String[] args) {
        // Simulate a client sending credentials
        String username = "admin";
        String password = "password123";
        String encoded = Base64.getEncoder().encodeToString(
                (username + ":" + password).getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + encoded;

        System.out.println("=== HTTP Basic Auth Simulation ===");
        System.out.println("Authorization: " + authHeader);

        // Server-side parsing
        try {
            String[] creds = parseBasicAuth(authHeader);
            System.out.println("Decoded username: " + creds[0]);
            System.out.println("Decoded password: " + creds[1]);

            if (authenticate(creds[0], creds[1])) {
                System.out.println("Authentication: SUCCESS");
            } else {
                System.out.println("Authentication: FAILED");
                System.out.println("Response Header: " + buildAuthenticateHeader("Secure App"));
            }
        } catch (Exception e) {
            System.out.println("Auth error: " + e.getMessage());
        }

        // Security warning demonstration
        System.out.println("\n*** SECURITY WARNING ***");
        System.out.println("Base64 is NOT encryption:");
        System.out.println("Encoded: " + encoded);
        System.out.println("Anyone can decode: " + new String(
                Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8));
        System.out.println("Always use HTTPS with Basic Auth!");
    }
}
