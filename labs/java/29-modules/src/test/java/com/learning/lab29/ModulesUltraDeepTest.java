package com.learning.lab29;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.lang.module.*;

class ModulesUltraDeepTest {

    @Test
    void moduleDescriptorPresent() {
        ModuleDescriptor descriptor = getClass().getModule().getDescriptor();
        assertNotNull(descriptor);
    }

    @Test
    void moduleRequiresJavaBase() {
        ModuleDescriptor descriptor = getClass().getModule().getDescriptor();
        assertTrue(descriptor.requires().stream()
            .anyMatch(r -> r.name().equals("java.base")));
    }

    @Test
    void moduleIsNotAutomatic() {
        assertFalse(getClass().getModule().getDescriptor().isAutomatic());
    }

    @Test
    void moduleVersionCanBeNull() {
        ModuleDescriptor descriptor = getClass().getModule().getDescriptor();
        assertTrue(descriptor.version().isEmpty());
    }

    @Test
    void getModuleLayer() {
        ModuleLayer layer = getClass().getModule().getLayer();
        assertNotNull(layer);
    }
}
