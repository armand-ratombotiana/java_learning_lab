package com.oracleebs.setup;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;

class ConcurrentProgramSetupTest {
    private ConcurrentProgramSetup setup;

    @BeforeEach
    void setUp() {
        setup = ConcurrentProgramSetup.createDefault();
    }

    @Test
    void testSubmitRequest() {
        long id = setup.submitRequest("GLPOST", Map.of("period", "JAN-2026"));
        assertTrue(id > 0);
    }

    @Test
    void testSubmitToNonexistentProgram() {
        assertThrows(IllegalArgumentException.class, () -> setup.submitRequest("NONEXISTENT", Map.of()));
    }

    @Test
    void testSubmitToDisabledProgram() {
        assertThrows(IllegalStateException.class, () -> setup.submitRequest("MRPWORK", Map.of()));
    }

    @Test
    void testRequestPhaseAfterSubmit() {
        long id = setup.submitRequest("PURLOAD", Map.of("load_type", "INVOICE"));
        var req = setup.getRequest(id);
        assertTrue(req.isPresent());
        assertEquals(ConcurrentProgramSetup.RequestPhase.PENDING, req.get().getPhase());
    }

    @Test
    void testCancelRequest() {
        long id = setup.submitRequest("GLPOST", Map.of());
        setup.cancelRequest(id);
        var req = setup.getRequest(id);
        assertTrue(req.isPresent());
        assertEquals("CANCELLED", req.get().getStatus());
    }

    @Test
    void testRequestParameters() {
        long id = setup.submitRequest("APINV", Map.of("batch", "BATCH123", "currency", "USD"));
        var req = setup.getRequest(id);
        assertTrue(req.isPresent());
        assertEquals("BATCH123", req.get().getParameters().get("batch"));
        assertEquals("USD", req.get().getParameters().get("currency"));
    }
}
