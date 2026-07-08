package com.devops.fifteen;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

class TerraformManagerTest {
    private TerraformManager manager;

    @BeforeEach
    void setUp() {
        manager = new TerraformManager("test");
    }

    @Test
    @DisplayName("Should initialize correctly")
    void testInitialize() {
        assertTrue(manager.initialize());
        assertTrue(manager.isInitialized());
    }

    @Test
    @DisplayName("Should set and get config")
    void testConfig() {
        manager.setConfig("key1", "value1");
        manager.setConfig("key2", "value2");
        assertEquals("value1", manager.getConfig("key1"));
        assertEquals("value2", manager.getConfig("key2"));
    }

    @Test
    @DisplayName("Should validate after initialization")
    void testValidate() {
        manager.initialize();
        assertTrue(manager.validate());
    }

    @Test
    @DisplayName("Should fail validation before initialization")
    void testValidateBeforeInit() {
        assertFalse(manager.validate());
    }

    @Test
    @DisplayName("Should shutdown correctly")
    void testShutdown() {
        manager.initialize();
        manager.shutdown();
        assertFalse(manager.isInitialized());
    }

    @Test
    @DisplayName("Should return immutable config copy")
    void testConfigImmutability() {
        manager.setConfig("key", "value");
        var config = manager.getConfig();
        assertThrows(UnsupportedOperationException.class, () -> config.put("another", "value"));
    }

    @Nested
    @DisplayName("Config management")
    class ConfigTests {
        @Test
        @DisplayName("Should handle multiple config entries")
        void testMultipleConfigs() {
            for (int i = 0; i < 100; i++) {
                manager.setConfig("key" + i, "value" + i);
            }
            assertEquals(100, manager.getConfig().size());
        }

        @Test
        @DisplayName("Should overwrite existing config")
        void testConfigOverwrite() {
            manager.setConfig("key", "old");
            manager.setConfig("key", "new");
            assertEquals("new", manager.getConfig("key"));
        }
    }
}
