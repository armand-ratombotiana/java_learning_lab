package com.oracleebs.architecture;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class EditionManagerTest {
    private EditionManager mgr;

    @BeforeEach
    void setUp() {
        mgr = new EditionManager();
    }

    @Test
    void testInitialState() {
        assertEquals("RUNNING", mgr.getCurrentEdition());
        assertFalse(mgr.isOnlinePatching());
        assertEquals(1, mgr.getEditionCount());
    }

    @Test
    void testCreateEdition() {
        assertTrue(mgr.createEdition("PATCH_EDITION", "Online patching edition"));
        assertEquals(2, mgr.getEditionCount());
        var ed = mgr.getEdition("PATCH_EDITION");
        assertTrue(ed.isPresent());
        assertEquals(EditionManager.EditionState.PREPARE, ed.get().getState());
    }

    @Test
    void testDuplicateEditionNotAllowed() {
        assertTrue(mgr.createEdition("PATCH1", "First"));
        assertFalse(mgr.createEdition("PATCH1", "Duplicate"));
    }

    @Test
    void testTransitionEdition() {
        mgr.createEdition("PATCH1", "Test");
        assertEquals(EditionManager.EditionState.APPLY, mgr.transitionEdition("PATCH1", EditionManager.EditionState.APPLY));
        assertEquals(EditionManager.EditionState.FINALIZE, mgr.transitionEdition("PATCH1", EditionManager.EditionState.FINALIZE));
    }

    @Test
    void testOnlinePatchingDetection() {
        assertFalse(mgr.isOnlinePatching());
        mgr.createEdition("PATCH1", "Test");
        assertTrue(mgr.isOnlinePatching());
    }

    @Test
    void testTransitionNonexistentEdition() {
        assertThrows(IllegalArgumentException.class, () -> mgr.transitionEdition("NONEXISTENT", EditionManager.EditionState.FINALIZE));
    }

    @Test
    void testFilterByState() {
        mgr.createEdition("ED1", "First");
        mgr.transitionEdition("ED1", EditionManager.EditionState.APPLY);
        assertEquals(1, mgr.getEditionsByState(EditionManager.EditionState.APPLY).size());
    }
}
