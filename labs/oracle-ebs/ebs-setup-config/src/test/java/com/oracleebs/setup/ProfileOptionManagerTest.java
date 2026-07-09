package com.oracleebs.setup;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class ProfileOptionManagerTest {
    private ProfileOptionManager mgr;

    @BeforeEach
    void setUp() {
        mgr = ProfileOptionManager.createDefault();
    }

    @Test
    void testDefaultProfilesExist() {
        assertTrue(mgr.resolveProfile("MO_DEFAULT_ORG_ID", ProfileOptionManager.ProfileLevel.SITE, "SITE").isPresent());
        assertTrue(mgr.resolveProfile("ICX_SESSION_TIMEOUT", ProfileOptionManager.ProfileLevel.APPLICATION, "ERP").isPresent());
    }

    @Test
    void testProfileResolution() {
        var val = mgr.resolveProfile("MO_DEFAULT_ORG_ID", ProfileOptionManager.ProfileLevel.SITE, "SITE");
        assertTrue(val.isPresent());
        assertEquals("101", val.get());
    }

    @Test
    void testEffectiveValue() {
        var val = mgr.resolveEffectiveValue("MO_DEFAULT_ORG_ID", "user1", "resp1", "app1");
        assertEquals("101", val);
    }

    @Test
    void testClearProfile() {
        mgr.clearProfile("FND_COLOR_SCHEME");
        assertTrue(mgr.resolveProfile("FND_COLOR_SCHEME", ProfileOptionManager.ProfileLevel.USER, "OPERATIONS").isEmpty());
    }

    @Test
    void testGetAllProfiles() {
        assertEquals(5, mgr.getAllProfiles().size());
    }

    @Test
    void testNonexistentProfile() {
        assertTrue(mgr.resolveProfile("NONEXISTENT", ProfileOptionManager.ProfileLevel.SITE, "SITE").isEmpty());
    }
}
