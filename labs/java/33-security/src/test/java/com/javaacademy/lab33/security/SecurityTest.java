package com.javaacademy.lab33.security;

import org.junit.jupiter.api.*;
import javax.crypto.*;
import java.security.*;
import static org.junit.jupiter.api.Assertions.*;

class SecurityTest {

    @Test
    @DisplayName("SHA-256 hash is deterministic")
    void sha256Deterministic() throws Exception {
        HashExample hash = new HashExample();
        String h1 = hash.hashSha256("hello");
        String h2 = hash.hashSha256("hello");
        assertEquals(h1, h2);
    }

    @Test
    @DisplayName("MD5 hash produces 32-character string")
    void md5Length() throws Exception {
        HashExample hash = new HashExample();
        assertEquals(32, hash.hashMd5("test").length());
    }

    @Test
    @DisplayName("AES encryption and decryption roundtrip")
    void aesRoundtrip() throws Exception {
        SymmetricEncryption crypto = new SymmetricEncryption();
        SecretKey key = crypto.generateKey(256);
        String plaintext = "Hello, AES encryption!";
        String encrypted = crypto.encryptToHex(plaintext, key);
        String decrypted = crypto.decryptFromHex(encrypted, key);
        assertEquals(plaintext, decrypted);
    }

    @Test
    @DisplayName("AES with different keys produces different ciphertext")
    void aesDifferentKeys() throws Exception {
        SymmetricEncryption crypto = new SymmetricEncryption();
        SecretKey key1 = crypto.generateKey(256);
        SecretKey key2 = crypto.generateKey(256);
        String encrypted1 = crypto.encryptToHex("data", key1);
        String encrypted2 = crypto.encryptToHex("data", key2);
        assertNotEquals(encrypted1, encrypted2);
    }

    @Test
    @DisplayName("RSA encryption and decryption roundtrip")
    void rsaRoundtrip() throws Exception {
        AsymmetricEncryption crypto = new AsymmetricEncryption();
        KeyPair kp = crypto.generateKeyPair(2048);
        String plaintext = "RSA test message";
        String encrypted = crypto.encryptToBase64(plaintext, kp.getPublic());
        String decrypted = crypto.decryptFromBase64(encrypted, kp.getPrivate());
        assertEquals(plaintext, decrypted);
    }

    @Test
    @DisplayName("Digital signature signs and verifies correctly")
    void digitalSignature() throws Exception {
        DigitalSignatureExample dse = new DigitalSignatureExample();
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();
        String data = "Important document content";
        String sig = dse.signToHex(data, kp.getPrivate());
        assertTrue(dse.verifyFromHex(data, sig, kp.getPublic()));
    }

    @Test
    @DisplayName("Tampered data fails signature verification")
    void tamperedSignature() throws Exception {
        DigitalSignatureExample dse = new DigitalSignatureExample();
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();
        String sig = dse.signToHex("original data", kp.getPrivate());
        assertFalse(dse.verifyFromHex("tampered data", sig, kp.getPublic()));
    }

    @Test
    @DisplayName("SecureRandom generates non-zero bytes")
    void secureRandomNonZero() {
        SecureRandomExample sre = new SecureRandomExample();
        byte[] bytes = sre.generateRandomBytes(16);
        assertTrue(sre.isCryptographicallyRandom(bytes));
    }

    @Test
    @DisplayName("SecureRandom generates unique session IDs")
    void uniqueSessionIds() {
        SecureRandomExample sre = new SecureRandomExample();
        assertNotEquals(sre.generateSessionId(), sre.generateSessionId());
    }

    @Test
    @DisplayName("Hash with salt produces different hash than without")
    void hashWithSalt() throws Exception {
        HashExample hash = new HashExample();
        byte[] salt = HashExample.generateSalt(16);
        byte[] hashedWithSalt = hash.hashWithSalt("password", salt);
        String hashedWithoutSalt = hash.hashSha256("password");
        assertNotEquals(hashedWithoutSalt, HexFormat.of().formatHex(hashedWithSalt));
    }
}
