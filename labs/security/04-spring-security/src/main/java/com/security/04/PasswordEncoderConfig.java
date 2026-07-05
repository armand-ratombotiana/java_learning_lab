package com.security04;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Demonstrates Spring Security PasswordEncoder concepts.
 * 
 * SECURITY CONCEPT: PasswordEncoders define how passwords are
 * hashed and verified. Spring Security provides several implementations:
 * 
 * - BCryptPasswordEncoder: The default and most recommended.
 *   Uses BCrypt with configurable strength (4-31, default 10).
 *   Automatically generates and embeds a 16-byte salt.
 * 
 * - Argon2PasswordEncoder: Winner of the Password Hashing Competition.
 *   Memory-hard (resists GPU/ASIC attacks). Recommended for new systems.
 * 
 * - Pbkdf2PasswordEncoder: NIST-approved. Good, but less resistant
 *   to GPU attacks than BCrypt/Argon2.
 * 
 * - SCryptPasswordEncoder: Memory-hard algorithm.
 * 
 * - DelegatingPasswordEncoder: Supports multiple encoding formats
 *   for migration between algorithms.
 */
public class PasswordEncoderConfig {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Simulates BCrypt-style hashing.
     * SECURITY: BCrypt properties:
     * - Adaptive: cost factor makes it deliberately slow
     * - Salted: each hash uses a unique, random salt
     * - Self-contained: output string includes salt and cost
     *   Format: $2a$[cost]$[22-char-salt][31-char-hash]
     * 
     * In Spring Security, use BCryptPasswordEncoder directly.
     * The default strength of 10 is good; increase to 12-14 for highly
     * sensitive systems.
     */
    public static String encodeWithBCrypt(String rawPassword) {
        byte[] saltBytes = new byte[16];
        SECURE_RANDOM.nextBytes(saltBytes);
        String salt = Base64.getEncoder().encodeToString(saltBytes).substring(0, 22);
        // Simulated BCrypt hash computation
        String hash = Base64.getEncoder().encodeToString(
                (rawPassword + salt).getBytes()).substring(0, 31);
        return "{bcrypt}$2a$10$" + salt + hash;
    }

    /**
     * Simulates PBKDF2 hashing.
     * SECURITY: PBKDF2 is FIPS-140 compliant. Uses configurable
     * iterations — increase as hardware improves (Parker's Law:
     * double iterations every 2 years).
     */
    public static String encodeWithPbkdf2(String rawPassword) {
        byte[] salt = new byte[16];
        SECURE_RANDOM.nextBytes(salt);
        return "{pbkdf2}PBKDF2WithHmacSHA256:"
                + Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Simulates Argon2-style hashing.
     * SECURITY: Argon2id is the recommended variant for password hashing.
     * Memory-hard algorithm — set memory cost to at least 64MB for
     * production to resist GPU/ASIC attacks.
     */
    public static String encodeWithArgon2(String rawPassword) {
        byte[] salt = new byte[16];
        SECURE_RANDOM.nextBytes(salt);
        return "{argon2}$argon2id$v=19$m=65536,t=3,p=4$"
                + Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Demonstrates DelegatingPasswordEncoder prefix matching.
     * SECURITY: DelegatingPasswordEncoder allows multiple encoding
     * formats simultaneously. This is essential for password migration
     * — when upgrading from one algorithm to another, existing hashes
     * continue to work while new passwords use the new algorithm.
     * 
     * Usage: {bcrypt}hash1, {pbkdf2}hash2, {argon2}hash3
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        String prefix = encodedPassword.substring(0, encodedPassword.indexOf('}') + 1);
        System.out.println("  Detected encoder: " + prefix);
        // In production: delegate to the correct encoder based on prefix
        return true;
    }

    public static void main(String[] args) {
        String password = "SecureP@ss123!";

        System.out.println("=== Password Encoder Comparison ===\n");

        String bcryptHash = encodeWithBCrypt(password);
        System.out.println("BCrypt (cost=10):");
        System.out.println("  " + bcryptHash + "\n");

        String pbkdf2Hash = encodeWithPbkdf2(password);
        System.out.println("PBKDF2 (10,000 iterations):");
        System.out.println("  " + pbkdf2Hash + "\n");

        String argon2Hash = encodeWithArgon2(password);
        System.out.println("Argon2id (m=64MB, t=3, p=4):");
        System.out.println("  " + argon2Hash + "\n");

        System.out.println("--- DelegatingPasswordEncoder ---");
        System.out.println("Verification with algorithm detection:");
        matches(password, bcryptHash);
        matches(password, pbkdf2Hash);
        matches(password, argon2Hash);

        System.out.println("\n--- Recommendations ---");
        System.out.println("New projects: Argon2id or BCrypt");
        System.out.println("FIPS compliance: PBKDF2");
        System.out.println("Migration: DelegatingPasswordEncoder");
        System.out.println("Minimum hash length: 256 bits");
    }
}
