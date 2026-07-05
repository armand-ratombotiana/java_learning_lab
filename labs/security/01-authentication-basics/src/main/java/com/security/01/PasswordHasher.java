package com.security01;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Demonstrates secure password hashing using BCrypt-compatible concepts.
 * 
 * SECURITY CONCEPT: Passwords must NEVER be stored in plaintext.
 * Use strong adaptive hashing algorithms (BCrypt, Argon2, scrypt, PBKDF2)
 * with a unique salt per password to defend against:
 * - Rainbow table attacks (precomputed hash dictionaries)
 * - Brute-force attacks (work factor / cost makes each attempt slow)
 * - Timing attacks (constant-time comparison)
 */
public class PasswordHasher {

    private static final int SALT_LENGTH = 16; // 128-bit salt
    private static final int HASH_ITERATIONS = 10000;
    private static final int HASH_LENGTH = 256; // bits for SHA-256

    /**
     * Generates a cryptographically random salt.
     * SECURITY: Use SecureRandom (not Random) for unpredictable values.
     */
    public static byte[] generateSalt() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);
        return salt;
    }

    /**
     * Hashes a password with PBKDF2 (simulates BCrypt-like behavior).
     * SECURITY: Key stretching via iteration count slows brute-force attacks.
     * In production, use BCryptPasswordEncoder from Spring Security.
     */
    public static String hashPassword(String password, byte[] salt)
            throws NoSuchAlgorithmException {
        var spec = new javax.crypto.spec.PBEKeySpec(
                password.toCharArray(), salt, HASH_ITERATIONS, HASH_LENGTH);
        var pbkdf2 = javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash = pbkdf2.generateSecret(spec).getEncoded();
        return HASH_ITERATIONS + ":" + Base64.getEncoder().encodeToString(salt)
                + ":" + Base64.getEncoder().encodeToString(hash);
    }

    /**
     * Verifies a password against a stored hash.
     * SECURITY: Uses constant-time comparison via MessageDigest.isEqual
     * to prevent timing side-channel attacks.
     */
    public static boolean verifyPassword(String password, String storedHash)
            throws NoSuchAlgorithmException {
        String[] parts = storedHash.split(":");
        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = Base64.getDecoder().decode(parts[1]);
        byte[] expectedHash = Base64.getDecoder().decode(parts[2]);

        var spec = new javax.crypto.spec.PBEKeySpec(
                password.toCharArray(), salt, iterations, HASH_LENGTH);
        var pbkdf2 = javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] actualHash = pbkdf2.generateSecret(spec).getEncoded();

        // Constant-time comparison prevents timing attacks
        return MessageDigest.isEqual(expectedHash, actualHash);
    }

    /**
     * Simulates a BCrypt-like hash format for demonstration.
     * SECURITY: BCrypt automatically embeds the salt and cost factor
     * in the output string, simplifying storage.
     */
    public static String simulateBCryptHash(String password) {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        String salt = Base64.getEncoder().encodeToString(saltBytes).substring(0, 22);
        // Format: $2a$cost$22-char-salt+31-char-hash
        String hash = Base64.getEncoder().encodeToString(
                (password + salt).getBytes()).substring(0, 31);
        return "$2a$12$" + salt + hash;
    }

    public static void main(String[] args) throws Exception {
        String password = "SecureP@ss123!";

        // Demonstrate hashing
        byte[] salt = generateSalt();
        String hashed = hashPassword(password, salt);
        System.out.println("Original password: " + password);
        System.out.println("Stored hash: " + hashed);

        // Demonstrate verification (correct password)
        boolean match = verifyPassword(password, hashed);
        System.out.println("Password match (correct): " + match);

        // Demonstrate verification (wrong password)
        boolean noMatch = verifyPassword("WrongPassword", hashed);
        System.out.println("Password match (wrong): " + noMatch);

        // Simulated BCrypt format
        System.out.println("\nBCrypt-style hash: " + simulateBCryptHash(password));
    }
}
