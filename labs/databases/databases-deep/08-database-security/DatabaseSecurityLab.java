package com.databases.deep.lab08;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.security.SecureRandom;
import java.sql.*;
import java.util.*;

/**
 * DatabaseSecurityLab — demonstrates column-level encryption (AES-GCM),
 * SQL injection prevention, and RLS simulation.
 */
public class DatabaseSecurityLab {

    static SecretKey generateKey() throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(256);
        return kg.generateKey();
    }

    static byte[] encrypt(String plaintext, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        byte[] iv = new byte[12];
        SecureRandom.getInstanceStrong().nextBytes(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(128, iv));
        byte[] encrypted = cipher.doFinal(plaintext.getBytes());
        byte[] combined = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);
        return combined;
    }

    static String decrypt(byte[] combined, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        byte[] iv = Arrays.copyOfRange(combined, 0, 12);
        byte[] encrypted = Arrays.copyOfRange(combined, 12, combined.length);
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(128, iv));
        return new String(cipher.doFinal(encrypted));
    }

    public static void main(String[] args) throws Exception {
        // --- Column-level encryption ---
        System.out.println("=== Column-level Encryption ===");
        SecretKey key = generateKey();
        String ssn = "123-45-6789";
        byte[] encrypted = encrypt(ssn, key);
        String decrypted = decrypt(encrypted, key);
        System.out.println("Original:  " + ssn);
        System.out.println("Encrypted: " + Base64.getEncoder().encodeToString(encrypted).substring(0, 20) + "...");
        System.out.println("Decrypted: " + decrypted);

        // --- SQL injection demonstration ---
        System.out.println("\n=== SQL Injection Prevention ===");
        String userInput = "'; DROP TABLE users; --";
        // Insecure
        String insecureSQL = "SELECT * FROM users WHERE name = '" + userInput + "'";
        System.out.println("Insecure SQL: " + insecureSQL);
        // Secure: use PreparedStatement (simulated)
        String secureSQL = "SELECT * FROM users WHERE name = ?";
        System.out.println("Secure SQL:   " + secureSQL + " (parameterized)");

        // --- RLS simulation ---
        System.out.println("\n=== Row-Level Security Simulation ===");
        Map<String, String> tenantData = new HashMap<>();
        tenantData.put("alice", "Order-1");
        tenantData.put("alice", "Order-2");
        tenantData.put("bob", "Order-3");
        String currentUser = "alice";
        System.out.println("User '" + currentUser + "' sees: orders belonging to " + currentUser + " only");
        System.out.println("SELECT * FROM orders WHERE customer = current_user");
    }
}