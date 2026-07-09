package com.apex.apt;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

class ApexAdvancedWorksheetTest {
    private ApexAdvancedWorksheet ws;
    @BeforeEach void setUp() { ws = ApexAdvancedWorksheet.createSample(); }

    @Test void shouldCreateCollection() {
        ws.createCollection("TEST");
        assertEquals(0, ws.getCollectionCount("TEST"));
    }

    @Test void shouldAddToCollection() {
        ws.addToCollection("MY_CART", "A", "B", "C", null, null);
        assertEquals(1, ws.getCollectionCount("MY_CART"));
    }

    @Test void shouldCacheValues() {
        ws.cachePut("key1", "value1", 5000);
        assertEquals("value1", ws.cacheGet("key1"));
    }

    @Test void shouldSendMail() {
        var result = ws.sendMail("user@test.com", "Test", "Body");
        assertTrue(result.contains("Queued"));
    }

    @Test void shouldRegisterPlugin() {
        var p = ws.getPlugin("Signature Pad");
        assertNotNull(p);
        assertEquals("ITEM", p.type());
    }
}