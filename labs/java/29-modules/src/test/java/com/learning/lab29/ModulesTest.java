package com.learning.lab29;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class ModulesTest {

    @Test
    @DisplayName("Module layer is present")
    void moduleLayerPresent() {
        Module module = getClass().getModule();
        assertNotNull(module);
        assertTrue(module.isNamed());
    }

    @Test
    @DisplayName("Module name matches module-info")
    void moduleNameMatches() {
        Module module = getClass().getModule();
        assertEquals("com.learning.lab29", module.getName());
    }

    @Test
    @DisplayName("Module exports packages")
    void moduleExports() {
        Module module = getClass().getModule();
        assertTrue(module.isExported("com.learning.lab29"));
    }

    @Test
    @DisplayName("ServiceLoaderExample creates demo instance")
    void serviceLoaderDemo() {
        var example = new ServiceLoaderExample();
        assertNotNull(example);
    }

    @Test
    @DisplayName("ModuleDemoExample demonstrates module API")
    void moduleDemo() {
        var demo = new ModuleDemoExample();
        assertNotNull(demo);
    }

    @Test
    @DisplayName("ModuleInfoExample shows module metadata")
    void moduleInfo() {
        var info = new ModuleInfoExample();
        assertNotNull(info);
    }
}
