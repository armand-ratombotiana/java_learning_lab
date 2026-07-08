package com.security13;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SecretsManager {

    private final Map<String, SecretEntry> secretStore = new ConcurrentHashMap<>();
    private SecretKey masterKey;
    private static final int GCM_TAG_LENGTH = 128;
    private static final int GCM_IV_LENGTH = 12;
    private static final SecureRandom RANDOM = new SecureRandom();

    public SecretsManager() throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(256);
        this.masterKey = kg.generateKey();
    }

    public SecretsManager(byte[] keyBytes) {
        this.masterKey = new SecretKeySpec(keyBytes, 0, keyBytes.length, "AES");
    }

    public String storeSecret(String path, String secret) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        byte[] iv = new byte[GCM_IV_LENGTH];
        RANDOM.nextBytes(iv);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, masterKey, spec);
        byte[] ciphertext = cipher.doFinal(secret.getBytes());
        byte[] encrypted = new byte[iv.length + ciphertext.length];
        System.arraycopy(iv, 0, encrypted, 0, iv.length);
        System.arraycopy(ciphertext, 0, encrypted, iv.length, ciphertext.length);
        SecretEntry entry = new SecretEntry(Base64.getEncoder().encodeToString(encrypted), Instant.now());
        secretStore.put(path, entry);
        return entry.version;
    }

    public Optional<String> getSecret(String path) throws Exception {
        SecretEntry entry = secretStore.get(path);
        if (entry == null) return Optional.empty();
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        byte[] encrypted = Base64.getDecoder().decode(entry.encryptedValue);
        byte[] iv = Arrays.copyOfRange(encrypted, 0, GCM_IV_LENGTH);
        byte[] ciphertext = Arrays.copyOfRange(encrypted, GCM_IV_LENGTH, encrypted.length);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, masterKey, spec);
        return Optional.of(new String(cipher.doFinal(ciphertext)));
    }

    public boolean rotateSecret(String path) throws Exception {
        Optional<String> current = getSecret(path);
        if (current.isEmpty()) return false;
        String newSecret = generateRandomSecret(32);
        storeSecret(path, newSecret);
        return true;
    }

    public static String generateRandomSecret(int length) {
        byte[] bytes = new byte[length];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    static class SecretEntry {
        final String encryptedValue;
        final String version;
        final Instant createdAt;
        SecretEntry(String encryptedValue, Instant createdAt) {
            this.encryptedValue = encryptedValue;
            this.createdAt = createdAt;
            this.version = UUID.randomUUID().toString();
        }
    }

    public static void main(String[] args) throws Exception {
        SecretsManager mgr = new SecretsManager();
        mgr.storeSecret("api/database/password", "s3cr3t!");
        System.out.println("Stored: " + mgr.getSecret("api/database/password").orElse("not found"));
    }
}
