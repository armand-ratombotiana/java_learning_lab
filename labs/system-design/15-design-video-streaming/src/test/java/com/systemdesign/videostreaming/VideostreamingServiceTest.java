package com.systemdesign.videostreaming;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class VideostreamingServiceTest {
    private VideostreamingService service;

    @BeforeEach
    void setUp() { VideostreamingService.reset(); service = new VideostreamingService("test-svc", 1, true); }

    @Test void testProcess() {
        var r = service.process("hello");
        assertTrue(r.contains("hello"));
        assertEquals(1, VideostreamingService.getRequestCount());
    }

    @Test void testDisabled() {
        var d = new VideostreamingService("d", 1, false);
        assertThrows(IllegalStateException.class, () -> d.process("x"));
    }

    @Test void testReset() { service.process("x"); VideostreamingService.reset(); assertEquals(0, VideostreamingService.getRequestCount()); }

    @ParameterizedTest @ValueSource(strings = {"", "abc", "123", "a b c"})
    void testVarious(String in) { assertTrue(service.process(in).contains(in)); }

    @Test void testConfig() {
        var c = VideostreamingConfig.defaults("x");
        assertEquals("x", c.serviceName()); assertEquals(100, c.maxConnections());
    }

    @Test void testException() {
        var e = new VideostreamingException("E1", "err");
        assertEquals("E1", e.getErrorCode());
    }
}