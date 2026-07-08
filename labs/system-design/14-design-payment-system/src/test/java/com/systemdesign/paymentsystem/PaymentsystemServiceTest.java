package com.systemdesign.paymentsystem;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class PaymentsystemServiceTest {
    private PaymentsystemService service;

    @BeforeEach
    void setUp() { PaymentsystemService.reset(); service = new PaymentsystemService("test-svc", 1, true); }

    @Test void testProcess() {
        var r = service.process("hello");
        assertTrue(r.contains("hello"));
        assertEquals(1, PaymentsystemService.getRequestCount());
    }

    @Test void testDisabled() {
        var d = new PaymentsystemService("d", 1, false);
        assertThrows(IllegalStateException.class, () -> d.process("x"));
    }

    @Test void testReset() { service.process("x"); PaymentsystemService.reset(); assertEquals(0, PaymentsystemService.getRequestCount()); }

    @ParameterizedTest @ValueSource(strings = {"", "abc", "123", "a b c"})
    void testVarious(String in) { assertTrue(service.process(in).contains(in)); }

    @Test void testConfig() {
        var c = PaymentsystemConfig.defaults("x");
        assertEquals("x", c.serviceName()); assertEquals(100, c.maxConnections());
    }

    @Test void testException() {
        var e = new PaymentsystemException("E1", "err");
        assertEquals("E1", e.getErrorCode());
    }
}