package com.javaacademy.lab33.security;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.util.HexFormat;

public class SymmetricEncryption {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";

    public SecretKey generateKey(int keySize) throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(keySize, new SecureRandom());
        return keyGen.generateKey();
    }

    public SecretKey generateKeyFromPassword(String password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), ALGORITHM);
    }

    public byte[] encrypt(byte[] plaintext, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] iv = cipher.getIV();
        byte[] ciphertext = cipher.doFinal(plaintext);
        byte[] combined = new byte[iv.length + ciphertext.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(ciphertext, 0, combined, iv.length, ciphertext.length);
        return combined;
    }

    public byte[] decrypt(byte[] ciphertextWithIv, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        byte[] iv = new byte[12];
        byte[] ciphertext = new byte[ciphertextWithIv.length - 12];
        System.arraycopy(ciphertextWithIv, 0, iv, 0, 12);
        System.arraycopy(ciphertextWithIv, 12, ciphertext, 0, ciphertext.length);
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, spec);
        return cipher.doFinal(ciphertext);
    }

    public String encryptToHex(String plaintext, SecretKey key) throws Exception {
        return HexFormat.of().formatHex(encrypt(plaintext.getBytes(), key));
    }

    public String decryptFromHex(String hex, SecretKey key) throws Exception {
        return new String(decrypt(HexFormat.of().parseHex(hex), key));
    }
}
