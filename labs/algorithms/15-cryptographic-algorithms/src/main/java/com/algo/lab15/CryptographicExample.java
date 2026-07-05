package com.algo.lab15;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.util.Arrays;
import java.util.Base64;

public class CryptographicExample {
    public static void main(String[] args) {
        System.out.println("=== Cryptographic Algorithms Demo ===\n");

        System.out.println("--- SHA-256 Hashing ---");
        String message = "Hello, Algorithms Academy!";
        String hash = CryptographicAlgorithms.sha256(message);
        System.out.println("Message: " + message);
        System.out.println("SHA-256: " + hash);

        System.out.println("\n--- AES Encryption ---");
        SecretKey aesKey = CryptographicAlgorithms.generateAESKey();
        String plaintext = "This is a secret message for AES encryption!";
        byte[] encrypted = CryptographicAlgorithms.encryptAES(plaintext.getBytes(), aesKey);
        byte[] decrypted = CryptographicAlgorithms.decryptAES(encrypted, aesKey);
        System.out.println("Original:  " + plaintext);
        System.out.println("Encrypted (base64): " + Base64.getEncoder().encodeToString(encrypted).substring(0, 40) + "...");
        System.out.println("Decrypted: " + new String(decrypted));
        System.out.println("Match: " + plaintext.equals(new String(decrypted)));

        System.out.println("\n--- RSA Encryption ---");
        KeyPair rsaKeys = CryptographicAlgorithms.generateRSAKeyPair();
        String rsaMessage = "RSA secret message!";
        byte[] rsaEncrypted = CryptographicAlgorithms.encryptRSA(rsaMessage.getBytes(), rsaKeys.getPublic());
        byte[] rsaDecrypted = CryptographicAlgorithms.decryptRSA(rsaEncrypted, rsaKeys.getPrivate());
        System.out.println("Original:  " + rsaMessage);
        System.out.println("Decrypted: " + new String(rsaDecrypted));

        System.out.println("\n--- HMAC ---");
        String hmacData = "Important message";
        String hmacKey = "my-secret-key";
        String hmacResult = CryptographicAlgorithms.hmacSHA256(hmacData, hmacKey);
        System.out.println("HMAC-SHA256: " + hmacResult);
        String hmacResult2 = CryptographicAlgorithms.hmacSHA256(hmacData, hmacKey);
        System.out.println("Verify (same key): " + hmacResult.equals(hmacResult2));
        String hmacResult3 = CryptographicAlgorithms.hmacSHA256(hmacData, "wrong-key");
        System.out.println("Verify (wrong key): " + hmacResult.equals(hmacResult3));

        System.out.println("\n--- Digital Signature ---");
        byte[] sigData = "Data to be signed".getBytes();
        byte[] signature = CryptographicAlgorithms.sign(sigData, rsaKeys.getPrivate());
        boolean valid = CryptographicAlgorithms.verify(sigData, signature, rsaKeys.getPublic());
        System.out.println("Signature valid: " + valid);

        byte[] tampered = "Data to be signed!".getBytes();
        boolean invalid = CryptographicAlgorithms.verify(tampered, signature, rsaKeys.getPublic());
        System.out.println("Tampered data detected: " + !invalid);
    }
}