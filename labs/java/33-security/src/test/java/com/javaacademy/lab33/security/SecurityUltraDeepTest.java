package com.javaacademy.lab33.security;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;

class SecurityUltraDeepTest {

    @Test
    void messageDigestSHA256() throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest("test".getBytes());
        assertEquals(32, hash.length);
    }

    @Test
    void secureRandomGeneratesDifferentValues() {
        SecureRandom sr = new SecureRandom();
        byte[] b1 = new byte[16];
        byte[] b2 = new byte[16];
        sr.nextBytes(b1);
        sr.nextBytes(b2);
        assertFalse(MessageDigest.isEqual(b1, b2));
    }

    @Test
    void keyGeneratorAES() throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(256);
        SecretKey key = kg.generateKey();
        assertEquals("AES", key.getAlgorithm());
        assertEquals(32, key.getEncoded().length);
    }

    @Test
    void cipherTransformation() throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(128);
        SecretKey key = kg.generateKey();
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] plaintext = "Hello, Security!".getBytes();
        byte[] ciphertext = cipher.doFinal(plaintext);
        assertNotEquals(java.util.Arrays.toString(plaintext), java.util.Arrays.toString(ciphertext));
    }
}
