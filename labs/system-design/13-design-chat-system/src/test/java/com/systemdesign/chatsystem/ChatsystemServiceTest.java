package com.systemdesign.chatsystem;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class ChatsystemServiceTest {
    private ChatsystemService service;

    @BeforeEach
    void setUp() { ChatsystemService.reset(); service = new ChatsystemService("test-svc", 1, true); }

    @Test void testProcess() {
        var r = service.process("hello");
        assertTrue(r.contains("hello"));
        assertEquals(1, ChatsystemService.getRequestCount());
    }

    @Test void testDisabled() {
        var d = new ChatsystemService("d", 1, false);
        assertThrows(IllegalStateException.class, () -> d.process("x"));
    }

    @Test void testReset() { service.process("x"); ChatsystemService.reset(); assertEquals(0, ChatsystemService.getRequestCount()); }

    @ParameterizedTest @ValueSource(strings = {"", "abc", "123", "a b c"})
    void testVarious(String in) { assertTrue(service.process(in).contains(in)); }

    @Test void testConfig() {
        var c = ChatsystemConfig.defaults("x");
        assertEquals("x", c.serviceName()); assertEquals(100, c.maxConnections());
    }

    @Test void testException() {
        var e = new ChatsystemException("E1", "err");
        assertEquals("E1", e.getErrorCode());
    }
}