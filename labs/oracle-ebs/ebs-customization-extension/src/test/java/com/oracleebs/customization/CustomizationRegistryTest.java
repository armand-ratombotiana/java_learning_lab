package com.oracleebs.customization;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class CustomizationRegistryTest {
    private CustomizationRegistry reg;

    @BeforeEach
    void setUp() {
        reg = CustomizationRegistry.createDefault();
    }

    @Test
    void testDefaultEntriesCount() {
        assertEquals(5, reg.getTotalCount());
    }

    @Test
    void testGetEntry() {
        var entry = reg.getEntry("C001");
        assertTrue(entry.isPresent());
        assertEquals("Set MO Profile", entry.get().getName());
    }

    @Test
    void testGetByType() {
        var extensions = reg.getByType(CustomizationRegistry.CustomizationType.EXTENSION);
        assertEquals(1, extensions.size());
    }

    @Test
    void testGetByModule() {
        var fndEntries = reg.getByModule("FND");
        assertEquals(1, fndEntries.size());
    }

    @Test
    void testActiveEntries() {
        assertEquals(4, reg.getActiveEntries().size());
    }

    @Test
    void testRegisterNewEntry() {
        reg.register(new CustomizationRegistry.CustomizationEntry("N001", "New Config",
            CustomizationRegistry.CustomizationType.CONFIGURATION, "GL", "Test", true, "1.0"));
        assertEquals(6, reg.getTotalCount());
    }
}
