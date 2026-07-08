package com.systemdesign.chatsystem;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;

class ChatsystemConfigTest {
    @Test void testDefaults() {
        var c = ChatsystemConfig.defaults("t");
        assertEquals("t", c.serviceName()); assertEquals("us-east-1", c.getProperty("region", "?"));
    }
    @Test void testCustom() {
        var c = new ChatsystemConfig("c", 50, 1000, false, Map.of("k", "v"));
        assertEquals(50, c.maxConnections()); assertEquals("v", c.getProperty("k", "?"));
    }
    @Test void testImmutable() {
        assertThrows(UnsupportedOperationException.class, () -> ChatsystemConfig.defaults("t").properties().put("x","y"));
    }
}