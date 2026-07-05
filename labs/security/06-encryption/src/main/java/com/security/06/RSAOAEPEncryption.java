package com.security06;

import javax.crypto.Cipher;
import java.security.*;
import java.util.Base64;

/**
 * Demonstrates RSA-OAEP asymmetric encryption.
 * 
 * SECURITY CONCEPT: RSA is an asymmetric cryptosystem where:
 * - Public key: shared freely, used for encryption
 * - Private key: kept secret, used for decryption
 * 
 * OAEP (Optimal Asymmetric Encryption Padding) is REQUIRED for RSA
 * encryption in modern systems. It provides:
 * - Semantic security: same plaintext encrypts differently each time
 * - Chosen-ciphertext attack (CCA) resistance
 * - Integrity verification through the OAEP padding
 * 
 * Use cases:
 * - Encrypting symmetric keys for key exchange
 * - Encrypting small payloads (< 256 bytes for 2048-bit RSA)
 * - Digital signatures (using different padding: PSS)
 * 
 * NEVER use textbook RSA (without padding) or PKCS#1 v1.5 (deprecated).
 */
public class RSAOAEPEncryption {

    private static final int RSA_KEY_SIZE = 2048; // 2048-bit minimum
    private static final String RSA_OAEP = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    /**
     * Generates an RSA key pair.
     * SECURITY: Minimum 2048-bit keys. 4096-bit recommended for
     * high-security environments. 1024-bit is BROKEN and must never be used.
     */
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(RSA_KEY_SIZE, new SecureRandom());
        return generator.generateKeyPair();
    }

    /**
     * Encrypts data with RSA-OAEP using the public key.
     * SECURITY: OAEP padding includes randomization — same plaintext
     * produces different ciphertext each time (semantic security).
     */
    public static String encrypt(String plaintext, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_OAEP);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] ciphertext = cipher.doFinal(plaintext.getBytes());
        return Base64.getEncoder().encodeToString(ciphertext);
    }

    /**
     * Decrypts data with RSA-OAEP using the private key.
     * SECURITY: The private key must be protected — use a
     * Hardware Security Module (HSM) or secure keystore.
     */
    public static String decrypt(String ciphertext, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_OAEP);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
        return new String(decrypted);
    }

    /**
     * Exports a public key to Base64 for sharing.
     */
    public static String exportPublicKey(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== RSA-OAEP Encryption ===\n");

        KeyPair keyPair = generateKeyPair();
        System.out.println("Public Key (Base64):");
        System.out.println(exportPublicKey(keyPair.getPublic()));
        System.out.println("Key size: " + RSA_KEY_SIZE + " bits\n");

        String plaintext = "Symmetric key material for AES session";
        System.out.println("Plaintext:  " + plaintext);

        String encrypted = encrypt(plaintext, keyPair.getPublic());
        System.out.println("Encrypted:  " + encrypted);

        String decrypted = decrypt(encrypted, keyPair.getPrivate());
        System.out.println("Decrypted:  " + decrypted + "\n");

        // Demonstrate that RSA has size limits
        System.out.println("--- RSA Payload Size ---");
        int maxPayloadBytes = RSA_KEY_SIZE / 8 - 66; // OAEP overhead
        System.out.println("Max payload: ~" + maxPayloadBytes + " bytes "
                + "(for " + RSA_KEY_SIZE + "-bit key with SHA-256 OAEP)");

        // Demonstrate semantic security (different ciphertexts each time)
        System.out.println("\n--- Semantic Security ---");
        String samePlaintext = "Hello";
        String enc1 = encrypt(samePlaintext, keyPair.getPublic());
        String enc2 = encrypt(samePlaintext, keyPair.getPublic());
        System.out.println("Encryption 1: " + enc1);
        System.out.println("Encryption 2: " + enc2);
        System.out.println("Different each time: " + !enc1.equals(enc2));
    }
}
