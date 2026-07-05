package com.security07;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Demonstrates CSRF (Cross-Site Request Forgery) token protection.
 * 
 * SECURITY CONCEPT: CSRF attacks trick authenticated users into
 * performing unwanted actions on a web application where they're
 * logged in. The attacker crafts a malicious request that leverages
 * the user's existing session cookie.
 * 
 * CSRF tokens prevent this by including a random, per-session value
 * that the attacker cannot predict or obtain.
 * 
 * Key principles:
 * - Token is bound to the user's session
 * - Token is sent with every state-changing request (POST, PUT, DELETE)
 * - Token is validated server-side before processing
 * - Token changes per session (or per request for high-security apps)
 * - SameSite cookies provide additional defense-in-depth
 */
public class CsrfTokenDemo {

    // Server-side token storage, keyed by session ID
    private static final Map<String, String> csrfTokenStore = new ConcurrentHashMap<>();
    private static final SecureRandom secureRandom = new SecureRandom();

    /**
     * Generates a CSRF token for a given session.
     * SECURITY: Token must be:
     * - Cryptographically random (SecureRandom)
     * - Sufficient length (at least 128 bits / 16 bytes)
     * - Unique per session
     * - Stored server-side
     */
    public static String generateCsrfToken(String sessionId) {
        byte[] tokenBytes = new byte[32]; // 256 bits
        secureRandom.nextBytes(tokenBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
        csrfTokenStore.put(sessionId, token);
        return token;
    }

    /**
     * Validates a CSRF token.
     * SECURITY: Use constant-time comparison to prevent timing attacks.
     * Remove token on validation for per-request tokens (highest security).
     */
    public static boolean validateCsrfToken(String sessionId, String receivedToken) {
        String storedToken = csrfTokenStore.get(sessionId);
        if (storedToken == null) {
            return false; // No token generated for this session
        }
        // Constant-time comparison prevents timing side-channel
        boolean valid = java.security.MessageDigest.isEqual(
                storedToken.getBytes(), receivedToken.getBytes());

        if (valid) {
            // SECURITY: Invalidate after use (per-request token rotation)
            csrfTokenStore.remove(sessionId);
        }

        return valid;
    }

    /**
     * Simulates a form with CSRF protection.
     */
    public static void renderProtectedForm(String sessionId) {
        String csrfToken = generateCsrfToken(sessionId);
        System.out.println("  <form method=\"POST\" action=\"/transfer\">");
        System.out.println("    <input type=\"hidden\" name=\"_csrf\" value=\""
                + csrfToken + "\" />");
        System.out.println("    <input type=\"text\" name=\"amount\" />");
        System.out.println("    <input type=\"text\" name=\"toAccount\" />");
        System.out.println("    <button type=\"submit\">Transfer</button>");
        System.out.println("  </form>");
    }

    /**
     * Simulates processing a state-changing request with CSRF validation.
     */
    public static boolean processRequest(String sessionId, String receivedCsrfToken) {
        System.out.println("Processing state-changing request...");
        if (!validateCsrfToken(sessionId, receivedCsrfToken)) {
            System.out.println("  FAILED: CSRF token invalid or missing");
            return false;
        }
        System.out.println("  SUCCESS: CSRF token validated, request processed");
        return true;
    }

    /**
     * Demonstrates a CSRF attack attempt (missing token).
     */
    public static boolean simulateCsrfAttack(String victimSessionId) {
        System.out.println("\n--- CSRF Attack Simulation ---");
        System.out.println("Attacker crafts request without CSRF token");
        // Attacker doesn't have the hidden token — sends empty or wrong token
        return processRequest(victimSessionId, "attacker-guessed-token");
    }

    public static void main(String[] args) {
        String sessionId = UUID.randomUUID().toString();

        System.out.println("=== CSRF Token Protection ===\n");

        // Normal flow: render form with token, submit with valid token
        System.out.println("1. User requests transfer form:");
        renderProtectedForm(sessionId);

        System.out.println("\n2. User submits form with CSRF token:");
        String storedToken = csrfTokenStore.get(sessionId);
        processRequest(sessionId, storedToken);

        // CSRF attack attempt
        String attackerSessionId = UUID.randomUUID().toString();
        simulateCsrfAttack(attackerSessionId);

        // Token reuse attempt
        System.out.println("\n--- Token Replay Attempt ---");
        System.out.println("Attacker tries to reuse intercepted token:");
        processRequest(sessionId, storedToken);
    }
}
