package com.systemdesign.urlshortener;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class UrlshortenerServiceTest {
    private UrlshortenerService service;

    @BeforeEach
    void setUp() { UrlshortenerService.reset(); service = new UrlshortenerService("test-svc", 1, true); }

    @Test void testProcess() {
        var r = service.process("hello");
        assertTrue(r.contains("hello"));
        assertEquals(1, UrlshortenerService.getRequestCount());
    }

    @Test void testDisabled() {
        var d = new UrlshortenerService("d", 1, false);
        assertThrows(IllegalStateException.class, () -> d.process("x"));
    }

    @Test void testReset() { service.process("x"); UrlshortenerService.reset(); assertEquals(0, UrlshortenerService.getRequestCount()); }

    @ParameterizedTest @ValueSource(strings = {"", "abc", "123", "a b c"})
    void testVarious(String in) { assertTrue(service.process(in).contains(in)); }

    @Test void testConfig() {
        var c = UrlshortenerConfig.defaults("x");
        assertEquals("x", c.serviceName()); assertEquals(100, c.maxConnections());
    }

    @Test void testException() {
        var e = new UrlshortenerException("E1", "err");
        assertEquals("E1", e.getErrorCode());
    }
}