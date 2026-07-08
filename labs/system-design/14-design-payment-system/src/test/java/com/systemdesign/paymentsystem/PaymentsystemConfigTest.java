package com.systemdesign.paymentsystem;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;

class PaymentsystemConfigTest {
    @Test void testDefaults() {
        var c = PaymentsystemConfig.defaults("t");
        assertEquals("t", c.serviceName()); assertEquals("us-east-1", c.getProperty("region", "?"));
    }
    @Test void testCustom() {
        var c = new PaymentsystemConfig("c", 50, 1000, false, Map.of("k", "v"));
        assertEquals(50, c.maxConnections()); assertEquals("v", c.getProperty("k", "?"));
    }
    @Test void testImmutable() {
        assertThrows(UnsupportedOperationException.class, () -> PaymentsystemConfig.defaults("t").properties().put("x","y"));
    }
}