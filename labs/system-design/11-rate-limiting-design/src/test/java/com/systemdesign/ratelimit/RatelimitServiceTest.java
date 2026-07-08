package com.systemdesign.ratelimit;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class RatelimitServiceTest {
    private RatelimitService service;

    @BeforeEach
    void setUp() { RatelimitService.reset(); service = new RatelimitService("test-svc", 1, true); }

    @Test void testProcess() {
        var r = service.process("hello");
        assertTrue(r.contains("hello"));
        assertEquals(1, RatelimitService.getRequestCount());
    }

    @Test void testDisabled() {
        var d = new RatelimitService("d", 1, false);
        assertThrows(IllegalStateException.class, () -> d.process("x"));
    }

    @Test void testReset() { service.process("x"); RatelimitService.reset(); assertEquals(0, RatelimitService.getRequestCount()); }

    @ParameterizedTest @ValueSource(strings = {"", "abc", "123", "a b c"})
    void testVarious(String in) { assertTrue(service.process(in).contains(in)); }

    @Test void testConfig() {
        var c = RatelimitConfig.defaults("x");
        assertEquals("x", c.serviceName()); assertEquals(100, c.maxConnections());
    }

    @Test void testException() {
        var e = new RatelimitException("E1", "err");
        assertEquals("E1", e.getErrorCode());
    }
}