package com.security.deep.lab01;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.security.spec.*;
import java.util.Arrays;

public class CryptographyDeep {

    public static byte[] aesEncrypt(byte[] plaintext, byte[] key, byte[] iv, String mode) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/" + mode + "/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        if (mode.equals("ECB")) {
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv));
        }
        return cipher.doFinal(plaintext);
    }

    public static byte[] aesDecrypt(byte[] ciphertext, byte[] key, byte[] iv, String mode) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/" + mode + "/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        if (mode.equals("ECB")) {
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv));
        }
        return cipher.doFinal(ciphertext);
    }

    public static byte[] aesGcmEncrypt(byte[] plaintext, byte[] key, byte[] nonce, byte[] aad) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(128, nonce);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);
        if (aad != null) cipher.updateAAD(aad);
        return cipher.doFinal(plaintext);
    }

    public static byte[] aesGcmDecrypt(byte[] ciphertext, byte[] key, byte[] nonce, byte[] aad) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(128, nonce);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);
        if (aad != null) cipher.updateAAD(aad);
        return cipher.doFinal(ciphertext);
    }

    public static KeyPair rsaGenerateKeyPair(int keySize) throws Exception {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(keySize, new SecureRandom());
        return gen.generateKeyPair();
    }

    public static byte[] rsaEncrypt(byte[] plaintext, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(plaintext);
    }

    public static byte[] rsaDecrypt(byte[] ciphertext, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(ciphertext);
    }

    public static byte[] rsaSign(byte[] data, PrivateKey privateKey) throws Exception {
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(privateKey);
        sig.update(data);
        return sig.sign();
    }

    public static boolean rsaVerify(byte[] data, byte[] signature, PublicKey publicKey) throws Exception {
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(publicKey);
        sig.update(data);
        return sig.verify(signature);
    }

    public static long[] diffieHellman(long p, long g, long a, long b) {
        long alicePub = modPow(g, a, p);
        long bobPub = modPow(g, b, p);
        long aliceShared = modPow(bobPub, a, p);
        long bobShared = modPow(alicePub, b, p);
        return new long[]{alicePub, bobPub, aliceShared, bobShared};
    }

    public static byte[] ecdsaSign(byte[] data, PrivateKey privateKey) throws Exception {
        Signature sig = Signature.getInstance("SHA256withECDSA");
        sig.initSign(privateKey);
        sig.update(data);
        return sig.sign();
    }

    public static boolean ecdsaVerify(byte[] data, byte[] signature, PublicKey publicKey) throws Exception {
        Signature sig = Signature.getInstance("SHA256withECDSA");
        sig.initVerify(publicKey);
        sig.update(data);
        return sig.verify(signature);
    }

    public static KeyPair ecGenerateKeyPair() throws Exception {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("EC");
        gen.initialize(new ECGenParameterSpec("secp256r1"), new SecureRandom());
        return gen.generateKeyPair();
    }

    private static long modPow(long base, long exp, long mod) {
        long result = 1;
        base = base % mod;
        while (exp > 0) {
            if ((exp & 1) == 1) result = (result * base) % mod;
            base = (base * base) % mod;
            exp >>= 1;
        }
        return result;
    }
}
