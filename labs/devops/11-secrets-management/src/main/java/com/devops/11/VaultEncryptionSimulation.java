package com.devops.secrets;

import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class VaultEncryptionSimulation {
    private static final String ALGORITHM = "AES";
    private final SecretKeySpec key;
    private final Map<String, String> vault = new ConcurrentHashMap<>();

    public VaultEncryptionSimulation(String secretKey) {
        byte[] keyBytes = secretKey.getBytes();
        byte[] padded = new byte[16];
        System.arraycopy(keyBytes, 0, padded, 0, Math.min(keyBytes.length, 16));
        this.key = new SecretKeySpec(padded, ALGORITHM);
    }

    public void store(String path, String plaintext) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(plaintext.getBytes());
            vault.put(path, Base64.getEncoder().encodeToString(encrypted));
            System.out.println("Stored secret at: " + path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String read(String path) {
        try {
            String encrypted = vault.get(path);
            if (encrypted == null) return null;
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encrypted));
            return new String(decrypted);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String rotate(String path, String newValue) {
        String oldValue = read(path);
        store(path, newValue);
        return oldValue;
    }

    public static void main(String[] args) {
        VaultEncryptionSimulation vault = new VaultEncryptionSimulation("my-secret-key-12");
        vault.store("database/password", "s3cret!");
        System.out.println("Retrieved: " + vault.read("database/password"));
        vault.rotate("database/password", "new-s3cret!");
        System.out.println("After rotation: " + vault.read("database/password"));
    }
}
