package com.oracleebs.architecture;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class EBSSystemArchitectureTest {
    private EBSSystemArchitecture arch;

    @BeforeEach
    void setUp() {
        arch = EBSSystemArchitecture.createDefault();
    }

    @Test
    void testDefaultArchitectureHasAllTiers() {
        assertTrue(arch.hasComponent(EBSSystemArchitecture.Tier.DESKTOP, EBSSystemArchitecture.Component.JINITIATOR));
        assertTrue(arch.hasComponent(EBSSystemArchitecture.Tier.APPLICATION, EBSSystemArchitecture.Component.FORMS_SERVER));
        assertTrue(arch.hasComponent(EBSSystemArchitecture.Tier.APPLICATION, EBSSystemArchitecture.Component.OHS));
        assertTrue(arch.hasComponent(EBSSystemArchitecture.Tier.APPLICATION, EBSSystemArchitecture.Component.CONCURRENT_MANAGER));
        assertTrue(arch.hasComponent(EBSSystemArchitecture.Tier.DATABASE, EBSSystemArchitecture.Component.DATABASE_INSTANCE));
    }

    @Test
    void testMultiNodeDetection() {
        assertTrue(arch.isMultiNode());
        arch = new EBSSystemArchitecture();
        assertFalse(arch.isMultiNode());
    }

    @Test
    void testEnvironmentSettings() {
        assertEquals("/u01/inst/apps/ERSDEV/apps/apps_st/appl/admin/CONTEXT_file.xml", arch.getEnv("CONTEXT_FILE"));
        assertEquals("ERSDEV", arch.getEnv("TWO_TASK"));
    }

    @Test
    void testPortRegistry() {
        assertEquals(9001, arch.getPort("FORMS"));
        assertEquals(8000, arch.getPort("OHS"));
        assertEquals(1521, arch.getPort("DB"));
        assertEquals(-1, arch.getPort("UNKNOWN"));
    }

    @Test
    void testTotalComponents() {
        assertEquals(5, arch.totalComponents());
    }

    @Test
    void testActiveNodes() {
        assertEquals(2, arch.getActiveNodes().size());
        assertTrue(arch.getActiveNodes().contains("node1.example.com"));
    }

    @Test
    void testApplicationTierComponents() {
        var appComponents = arch.getComponents(EBSSystemArchitecture.Tier.APPLICATION);
        assertEquals(3, appComponents.size());
        assertTrue(appComponents.contains(EBSSystemArchitecture.Component.FORMS_SERVER));
        assertTrue(appComponents.contains(EBSSystemArchitecture.Component.OHS));
        assertTrue(appComponents.contains(EBSSystemArchitecture.Component.CONCURRENT_MANAGER));
    }
}
