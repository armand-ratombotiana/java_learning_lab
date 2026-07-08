package com.security13;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class SecretsManagerTest {
    private SecretsManager manager;

    @BeforeEach
    void setUp() throws Exception {
        manager = new SecretsManager();
    }

    @Test
    void testStoreAndRetrieveSecret() throws Exception {
        manager.storeSecret("test/path", "mySecretValue");
        var result = manager.getSecret("test/path");
        assertTrue(result.isPresent());
        assertEquals("mySecretValue", result.get());
    }

    @Test
    void testGetNonExistentSecret() throws Exception {
        var result = manager.getSecret("nonexistent");
        assertFalse(result.isPresent());
    }

    @Test
    void testRotateSecret() throws Exception {
        manager.storeSecret("test/path", "original");
        manager.rotateSecret("test/path");
        var result = manager.getSecret("test/path");
        assertTrue(result.isPresent());
        assertNotEquals("original", result.get());
    }

    @Test
    void testRotateNonExistentSecret() throws Exception {
        assertFalse(manager.rotateSecret("nonexistent"));
    }

    @Test
    void testGenerateRandomSecret() {
        String secret = SecretsManager.generateRandomSecret(32);
        assertNotNull(secret);
        assertEquals(43, secret.length());
    }
}
