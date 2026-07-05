package com.security06;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Demonstrates AES-GCM authenticated encryption.
 * 
 * SECURITY CONCEPT: AES-GCM (Galois/Counter Mode) provides:
 * - Confidentiality: AES encryption prevents unauthorized reading
 * - Integrity: GCM authentication tag detects tampering
 * - Authenticity: verifies the data origin
 * 
 * Why AES-GCM:
 * - AEAD (Authenticated Encryption with Associated Data):
 *   encryption + integrity check in one operation
 * - Parallelizable (fast, especially with hardware acceleration)
 * - No padding oracle attacks (unlike AES-CBC)
 * 
 * Critical security parameters:
 * - Key size: 256 bits (AES-256) — never use AES-128 for sensitive data
 * - Nonce/IV: 12 bytes (96 bits) — MUST be unique per key
 * - Tag length: 128 bits (16 bytes) — never use less than 128 bits
 * - Associated Data (AAD): optionally authenticate metadata
 */
public class AESGCMEncryption {

    private static final int AES_KEY_SIZE = 256;
    private static final int GCM_NONCE_LENGTH = 12; // 96 bits
    private static final int GCM_TAG_LENGTH = 128; // bits
    private static final String AES_GCM_NO_PADDING = "AES/GCM/NoPadding";

    /**
     * Generates a new AES-256 key.
     * SECURITY: Use KeyGenerator (not random bytes) to ensure
     * the key meets algorithm-specific requirements.
     */
    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(AES_KEY_SIZE, new SecureRandom());
        return keyGen.generateKey();
    }

    /**
     * Encrypts plaintext using AES-256 GCM.
     * SECURITY: A new random nonce is generated for each encryption.
     * NEVER reuse a nonce with the same key — this destroys security.
     * 
     * Output format: Base64(nonce + ciphertext + tag)
     * The nonce is prepended so it's available for decryption.
     */
    public static String encrypt(String plaintext, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING);

        // Generate a unique nonce for every encryption operation
        byte[] nonce = new byte[GCM_NONCE_LENGTH];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(nonce);

        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, nonce);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);

        byte[] ciphertext = cipher.doFinal(plaintext.getBytes());

        // Prepend nonce for storage/transmission
        byte[] encrypted = new byte[nonce.length + ciphertext.length];
        System.arraycopy(nonce, 0, encrypted, 0, nonce.length);
        System.arraycopy(ciphertext, 0, encrypted, nonce.length, ciphertext.length);

        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * Decrypts AES-256 GCM ciphertext.
     * SECURITY: GCM authenticates the ciphertext during decryption.
     * If the ciphertext was tampered with, an AEADBadTagException is thrown.
     * Always check for this — never return partially decrypted data.
     */
    public static String decrypt(String encryptedData, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING);

        byte[] decoded = Base64.getDecoder().decode(encryptedData);

        // Extract nonce from the beginning
        byte[] nonce = new byte[GCM_NONCE_LENGTH];
        byte[] ciphertext = new byte[decoded.length - GCM_NONCE_LENGTH];
        System.arraycopy(decoded, 0, nonce, 0, nonce.length);
        System.arraycopy(decoded, nonce.length, ciphertext, 0, ciphertext.length);

        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, nonce);
        cipher.init(Cipher.DECRYPT_MODE, key, spec);

        byte[] plaintext = cipher.doFinal(ciphertext);
        return new String(plaintext);
    }

    /**
     * Restores a SecretKey from its encoded bytes.
     */
    public static SecretKey restoreKey(byte[] keyBytes) {
        return new SecretKeySpec(keyBytes, "AES");
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== AES-256 GCM Encryption ===\n");

        SecretKey key = generateKey();
        System.out.println("Key: " + Base64.getEncoder().encodeToString(key.getEncoded()));

        String plaintext = "Sensitive data: API keys, PII, financial records";
        System.out.println("Plaintext:  " + plaintext);

        String encrypted = encrypt(plaintext, key);
        System.out.println("Encrypted:  " + encrypted);

        String decrypted = decrypt(encrypted, key);
        System.out.println("Decrypted:  " + decrypted);

        // Demonstrate tamper detection
        System.out.println("\n--- Tamper Detection ---");
        byte[] tampered = Base64.getDecoder().decode(encrypted);
        tampered[tampered.length - 1] ^= 0x01; // Flip one bit
        String tamperedEncoded = Base64.getEncoder().encodeToString(tampered);

        try {
            decrypt(tamperedEncoded, key);
            System.out.println("Tampered data decrypted (SHOULD NOT HAPPEN)");
        } catch (Exception e) {
            System.out.println("Tampered data REJECTED: " + e.getMessage());
        }

        System.out.println("\n--- Security Summary ---");
        System.out.println("Algorithm: AES-256 GCM (AEAD)");
        System.out.println("Nonce:     " + (GCM_NONCE_LENGTH * 8) + "-bit (unique per encryption)");
        System.out.println("Tag:       " + GCM_TAG_LENGTH + "-bit (integrity + authenticity)");
        System.out.println("Key:       " + AES_KEY_SIZE + "-bit");
    }
}
