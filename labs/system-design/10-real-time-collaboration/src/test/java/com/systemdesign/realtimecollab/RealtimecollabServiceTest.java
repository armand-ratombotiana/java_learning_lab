package com.systemdesign.realtimecollab;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class RealtimecollabServiceTest {
    private RealtimecollabService service;

    @BeforeEach
    void setUp() { RealtimecollabService.reset(); service = new RealtimecollabService("test-svc", 1, true); }

    @Test void testProcess() {
        var r = service.process("hello");
        assertTrue(r.contains("hello"));
        assertEquals(1, RealtimecollabService.getRequestCount());
    }

    @Test void testDisabled() {
        var d = new RealtimecollabService("d", 1, false);
        assertThrows(IllegalStateException.class, () -> d.process("x"));
    }

    @Test void testReset() { service.process("x"); RealtimecollabService.reset(); assertEquals(0, RealtimecollabService.getRequestCount()); }

    @ParameterizedTest @ValueSource(strings = {"", "abc", "123", "a b c"})
    void testVarious(String in) { assertTrue(service.process(in).contains(in)); }

    @Test void testConfig() {
        var c = RealtimecollabConfig.defaults("x");
        assertEquals("x", c.serviceName()); assertEquals(100, c.maxConnections());
    }

    @Test void testException() {
        var e = new RealtimecollabException("E1", "err");
        assertEquals("E1", e.getErrorCode());
    }
}