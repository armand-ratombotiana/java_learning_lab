package com.systemdesign.distributeddb;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class DistributeddbServiceTest {
    private DistributeddbService service;

    @BeforeEach
    void setUp() { DistributeddbService.reset(); service = new DistributeddbService("test-svc", 1, true); }

    @Test void testProcess() {
        var r = service.process("hello");
        assertTrue(r.contains("hello"));
        assertEquals(1, DistributeddbService.getRequestCount());
    }

    @Test void testDisabled() {
        var d = new DistributeddbService("d", 1, false);
        assertThrows(IllegalStateException.class, () -> d.process("x"));
    }

    @Test void testReset() { service.process("x"); DistributeddbService.reset(); assertEquals(0, DistributeddbService.getRequestCount()); }

    @ParameterizedTest @ValueSource(strings = {"", "abc", "123", "a b c"})
    void testVarious(String in) { assertTrue(service.process(in).contains(in)); }

    @Test void testConfig() {
        var c = DistributeddbConfig.defaults("x");
        assertEquals("x", c.serviceName()); assertEquals(100, c.maxConnections());
    }

    @Test void testException() {
        var e = new DistributeddbException("E1", "err");
        assertEquals("E1", e.getErrorCode());
    }
}