package com.algorithms.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Custom: Cryptographic Algorithm Examples
 * SHA-256 hashing, HMAC, and simple RSA-style encryption demo.
 *
 * Time Complexity: O(n) for hashing
 * Space Complexity: O(1)
 */
public class CryptoExamples {

    public String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // Simple XOR "encryption" (educational, not secure)
    public String xorCipher(String input, char key) {
        char[] result = new char[input.length()];
        for (int i = 0; i < input.length(); i++) result[i] = (char) (input.charAt(i) ^ key);
        return new String(result);
    }

    // Base64 encoding/decoding
    public String encodeBase64(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes());
    }

    public String decodeBase64(String encoded) {
        return new String(Base64.getDecoder().decode(encoded));
    }

    public static void main(String[] args) {
        CryptoExamples ce = new CryptoExamples();

        String msg = "Hello, World!";
        System.out.println("SHA-256: " + ce.sha256(msg));
        System.out.println("SHA-256 (length): " + ce.sha256(msg).length() + " chars (expected: 64)");

        String encrypted = ce.xorCipher(msg, 'K');
        String decrypted = ce.xorCipher(encrypted, 'K');
        System.out.println("XOR encrypted: " + encrypted);
        System.out.println("XOR decrypted: " + decrypted + " (expected: " + msg + ")");

        String encoded = ce.encodeBase64(msg);
        System.out.println("Base64 encoded: " + encoded);
        System.out.println("Base64 decoded: " + ce.decodeBase64(encoded) + " (expected: " + msg + ")");
    }
}
