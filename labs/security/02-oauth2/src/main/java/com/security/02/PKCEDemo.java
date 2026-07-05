package com.security02;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Demonstrates PKCE (Proof Key for Code Exchange) for OAuth2 public clients.
 * 
 * SECURITY CONCEPT: PKCE (RFC 7636) protects public clients
 * (SPAs, mobile apps) from authorization code interception attacks.
 * 
 * Before PKCE, public clients using the Authorization Code flow
 * were vulnerable to code interception because they couldn't
 * securely store a client_secret.
 * 
 * How PKCE protects:
 * - Client generates a random code_verifier (high entropy)
 * - Sends code_challenge (hash of verifier) in auth request
 * - When exchanging code, sends the raw code_verifier
 * - Server hashes verifier and matches against stored challenge
 * - An attacker who intercepts the code cannot exchange it
 *   without the verifier
 */
public class PKCEDemo {

    private final SecureRandom secureRandom;
    private String codeVerifier;

    public PKCEDemo() {
        this.secureRandom = new SecureRandom();
    }

    /**
     * Generate a code_verifier.
     * SECURITY: Must be cryptographically random with sufficient entropy.
     * Minimum 43 chars, maximum 128 chars (RFC 7636).
     * Uses unreserved characters: [A-Z] / [a-z] / [0-9] / "-" / "." / "_" / "~"
     */
    public String generateCodeVerifier() {
        byte[] verifierBytes = new byte[32]; // 256 bits of entropy
        secureRandom.nextBytes(verifierBytes);
        this.codeVerifier = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(verifierBytes);
        return this.codeVerifier;
    }

    /**
     * Generate a code_challenge using S256 method.
     * SECURITY: SHA-256 is a one-way hash — the verifier cannot
     * be derived from the challenge. This binds the auth code
     * to the specific client session.
     * 
     * Two methods available:
     * - S256 (recommended): SHA-256 hash of verifier
     * - plain: verifier sent as-is (less secure, only for legacy)
     */
    public String generateCodeChallengeS256(String verifier)
            throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] challenge = md.digest(
                verifier.getBytes(StandardCharsets.US_ASCII));
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(challenge);
    }

    /**
     * Verify the code_verifier against the stored challenge.
     * The authorization server performs this check during
     * the token exchange.
     */
    public boolean verifyCodeChallenge(String verifier, String storedChallenge)
            throws NoSuchAlgorithmException {
        String computedChallenge = generateCodeChallengeS256(verifier);
        return MessageDigest.isEqual(
                computedChallenge.getBytes(StandardCharsets.US_ASCII),
                storedChallenge.getBytes(StandardCharsets.US_ASCII));
    }

    /**
     * Demonstrates the full PKCE flow.
     */
    public void demonstratePKCE() throws NoSuchAlgorithmException {
        System.out.println("=== PKCE (Proof Key for Code Exchange) Flow ===\n");

        // Step 1: Generate code_verifier
        String verifier = generateCodeVerifier();
        System.out.println("1. Client generates code_verifier:");
        System.out.println("   " + verifier);
        System.out.println("   Length: " + verifier.length() + " chars\n");

        // Step 2: Generate code_challenge (S256)
        String challenge = generateCodeChallengeS256(verifier);
        System.out.println("2. Client computes code_challenge (SHA-256):");
        System.out.println("   " + challenge + "\n");

        // Step 3: Send auth request with code_challenge
        System.out.println("3. Authorization request includes:");
        System.out.println("   code_challenge=" + challenge);
        System.out.println("   code_challenge_method=S256\n");

        // Step 4: After receiving auth code, exchange it with verifier
        System.out.println("4. Token exchange sends code_verifier");
        boolean valid = verifyCodeChallenge(verifier, challenge);
        System.out.println("   Server verifies: " + valid + " (match: " + valid + ")\n");

        // Demonstrate attack resistance
        System.out.println("--- Attack Scenario: Intercepted Auth Code ---");
        String attackerVerifier = generateCodeVerifier();
        System.out.println("Attacker's verifier:    " + attackerVerifier);
        System.out.println("Original challenge:     " + challenge);
        boolean attackResult = verifyCodeChallenge(attackerVerifier, challenge);
        System.out.println("Server verification:    " + attackResult);
        System.out.println("Attack prevented:      " + !attackResult);
    }

    public static void main(String[] args) throws Exception {
        new PKCEDemo().demonstratePKCE();
    }
}
