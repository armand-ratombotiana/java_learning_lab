package com.javaacademy.lab33.security;

import javax.crypto.*;
import java.security.*;
import java.util.HexFormat;

public class AsymmetricEncryption {

    private static final String ALGORITHM = "RSA";

    public KeyPair generateKeyPair(int keySize) throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyGen.initialize(keySize, new SecureRandom());
        return keyGen.generateKeyPair();
    }

    public byte[] encrypt(byte[] plaintext, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(plaintext);
    }

    public byte[] decrypt(byte[] ciphertext, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(ciphertext);
    }

    public String encryptToBase64(String plaintext, PublicKey publicKey) throws Exception {
        return java.util.Base64.getEncoder().encodeToString(encrypt(plaintext.getBytes(), publicKey));
    }

    public String decryptFromBase64(String base64, PrivateKey privateKey) throws Exception {
        return new String(decrypt(java.util.Base64.getDecoder().decode(base64), privateKey));
    }
}
