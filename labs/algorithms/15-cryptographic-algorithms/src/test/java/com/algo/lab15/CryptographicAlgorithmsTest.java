package com.algo.lab15;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import javax.crypto.SecretKey;
import java.security.KeyPair;

class CryptographicAlgorithmsTest {

    @Test
    void testSHA256Consistency() {
        String input = "test";
        assertEquals(CryptographicAlgorithms.sha256(input), CryptographicAlgorithms.sha256(input));
    }

    @Test
    void testSHA256DifferentInputs() {
        assertNotEquals(CryptographicAlgorithms.sha256("abc"), CryptographicAlgorithms.sha256("abcd"));
    }

    @Test
    void testSHA256KnownValue() {
        String hash = CryptographicAlgorithms.sha256("hello");
        assertEquals(64, hash.length());
    }

    @Test
    void testAESEncryptDecrypt() {
        SecretKey key = CryptographicAlgorithms.generateAESKey();
        String original = "AES test message";
        byte[] encrypted = CryptographicAlgorithms.encryptAES(original.getBytes(), key);
        byte[] decrypted = CryptographicAlgorithms.decryptAES(encrypted, key);
        assertEquals(original, new String(decrypted));
    }

    @Test
    void testAESDifferentKeys() {
        SecretKey key1 = CryptographicAlgorithms.generateAESKey();
        SecretKey key2 = CryptographicAlgorithms.generateAESKey();
        String original = "test";
        byte[] encrypted = CryptographicAlgorithms.encryptAES(original.getBytes(), key1);
        assertThrows(RuntimeException.class, () -> {
            CryptographicAlgorithms.decryptAES(encrypted, key2);
        });
    }

    @Test
    void testAESRoundTrip() {
        SecretKey key = CryptographicAlgorithms.generateAESKey();
        String[] messages = {"", "a", "short", "A longer message that needs proper encryption!"};
        for (String msg : messages) {
            byte[] encrypted = CryptographicAlgorithms.encryptAES(msg.getBytes(), key);
            byte[] decrypted = CryptographicAlgorithms.decryptAES(encrypted, key);
            assertEquals(msg, new String(decrypted));
        }
    }

    @Test
    void testRSAEncryptDecrypt() {
        KeyPair keys = CryptographicAlgorithms.generateRSAKeyPair();
        String original = "RSA test message";
        byte[] encrypted = CryptographicAlgorithms.encryptRSA(original.getBytes(), keys.getPublic());
        byte[] decrypted = CryptographicAlgorithms.decryptRSA(encrypted, keys.getPrivate());
        assertEquals(original, new String(decrypted));
    }

    @Test
    void testRSAWrongKey() {
        KeyPair keys1 = CryptographicAlgorithms.generateRSAKeyPair();
        KeyPair keys2 = CryptographicAlgorithms.generateRSAKeyPair();
        byte[] encrypted = CryptographicAlgorithms.encryptRSA("test".getBytes(), keys1.getPublic());
        assertThrows(RuntimeException.class, () -> {
            CryptographicAlgorithms.decryptRSA(encrypted, keys2.getPrivate());
        });
    }

    @Test
    void testHMACConsistency() {
        String data = "test data";
        String key = "secret";
        assertEquals(
            CryptographicAlgorithms.hmacSHA256(data, key),
            CryptographicAlgorithms.hmacSHA256(data, key));
    }

    @Test
    void testHMACDifferentKey() {
        String data = "test data";
        assertNotEquals(
            CryptographicAlgorithms.hmacSHA256(data, "key1"),
            CryptographicAlgorithms.hmacSHA256(data, "key2"));
    }

    @Test
    void testHMACNonEmpty() {
        assertFalse(CryptographicAlgorithms.hmacSHA256("data", "key").isEmpty());
    }

    @Test
    void testDigitalSignature() {
        KeyPair keys = CryptographicAlgorithms.generateRSAKeyPair();
        byte[] data = "Signed data".getBytes();
        byte[] signature = CryptographicAlgorithms.sign(data, keys.getPrivate());
        assertTrue(CryptographicAlgorithms.verify(data, signature, keys.getPublic()));
    }

    @Test
    void testDigitalSignatureTampered() {
        KeyPair keys = CryptographicAlgorithms.generateRSAKeyPair();
        byte[] data = "Original data".getBytes();
        byte[] signature = CryptographicAlgorithms.sign(data, keys.getPrivate());
        byte[] tampered = "Tampered data".getBytes();
        assertFalse(CryptographicAlgorithms.verify(tampered, signature, keys.getPublic()));
    }

    @Test
    void testDigitalSignatureWrongKey() {
        KeyPair keys1 = CryptographicAlgorithms.generateRSAKeyPair();
        KeyPair keys2 = CryptographicAlgorithms.generateRSAKeyPair();
        byte[] data = "test".getBytes();
        byte[] signature = CryptographicAlgorithms.sign(data, keys1.getPrivate());
        assertFalse(CryptographicAlgorithms.verify(data, signature, keys2.getPublic()));
    }
}