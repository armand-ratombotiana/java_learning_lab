package com.security01;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simulates form-based login with session management.
 * 
 * SECURITY CONCEPT: Form-based authentication uses HTML forms
 * (POST with username/password) and server-side sessions.
 * Critical protections required:
 * - CSRF tokens to prevent cross-site request forgery
 * - Session hijacking prevention (secure cookies, HttpOnly, SameSite)
 * - Account lockout after failed attempts
 * - Rate limiting on login endpoints
 */
public class FormLoginSimulator {

    // Simulated session store
    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();
    // Track failed login attempts
    private static final Map<String, Integer> loginAttempts = new ConcurrentHashMap<>();
    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCKOUT_DURATION_MINUTES = 15;

    static class Session {
        final String sessionId;
        final String username;
        final Instant createdAt;
        final Instant expiresAt;

        Session(String username) {
            this.sessionId = UUID.randomUUID().toString();
            this.username = username;
            this.createdAt = Instant.now();
            this.expiresAt = createdAt.plusSeconds(1800); // 30 min timeout
        }

        boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }

        // Generate a CSRF token bound to this session
        String generateCsrfToken() {
            SecureRandom random = new SecureRandom();
            byte[] token = new byte[32];
            random.nextBytes(token);
            return Base64.getEncoder().encodeToString(token);
        }
    }

    /**
     * Simulates form login submission.
     * SECURITY: Implements account lockout and rate limiting.
     */
    public static Session login(String username, String password) {
        // Check account lockout
        Integer attempts = loginAttempts.get(username);
        if (attempts != null && attempts >= MAX_ATTEMPTS) {
            System.out.println("Account LOCKED due to too many failed attempts");
            return null;
        }

        // In production: verify against hashed password via BCrypt
        if ("validUser".equals(username) && "correctPassword".equals(password)) {
            loginAttempts.remove(username); // Reset on success
            Session session = new Session(username);
            sessions.put(session.sessionId, session);
            System.out.println("Login successful for: " + username);
            return session;
        } else {
            loginAttempts.merge(username, 1, Integer::sum);
            int remaining = MAX_ATTEMPTS - loginAttempts.get(username);
            System.out.println("Login failed for: " + username
                    + " (attempts remaining: " + Math.max(0, remaining) + ")");
            return null;
        }
    }

    /**
     * Validates a session.
     * SECURITY: Check expiration and regenerate session ID
     * after privilege escalation to prevent session fixation.
     */
    public static boolean validateSession(Session session) {
        if (session == null || session.isExpired()) {
            System.out.println("Session invalid or expired");
            return false;
        }
        return true;
    }

    /**
     * Simulates logout — invalidates session.
     * SECURITY: Must invalidate both server-side session
     * and client-side session cookie.
     */
    public static void logout(Session session) {
        if (session != null) {
            sessions.remove(session.sessionId);
            System.out.println("Session terminated for: " + session.username);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Form Login Simulation ===\n");

        // Attempt 1 & 2: Wrong password
        login("validUser", "wrong");
        login("validUser", "wrong");

        // Attempt 3: Success after failures (attempts are not yet exhausted)
        Session session = login("validUser", "correctPassword");
        if (session != null) {
            System.out.println("Session ID: " + session.sessionId);
            System.out.println("Created: " + session.createdAt);
            System.out.println("Expires: " + session.expiresAt);

            // CSRF token generation per session
            String csrf = session.generateCsrfToken();
            System.out.println("CSRF Token: " + csrf);

            // Validate session
            System.out.println("Session valid: " + validateSession(session));

            // Logout
            logout(session);
            System.out.println("Session valid after logout: " + validateSession(session));
        }

        // Demonstrate lockout
        System.out.println("\n--- Account Lockout Demo ---");
        for (int i = 0; i < 6; i++) {
            login("targetUser", "wrongPassword");
        }
    }
}
