package com.capstone.realtimeanalytics;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;

class RealtimeanalyticsConfigTest {
    @Test void testDefaults() { var c = RealtimeanalyticsConfig.defaults("t"); assertEquals("t",c.name()); }
    @Test void testCustom() { var c = new RealtimeanalyticsConfig("c",8,5000,false,Map.of("k","v")); assertEquals(8,c.maxWorkers()); }
    @Test void testImmutable() { assertThrows(UnsupportedOperationException.class, () -> RealtimeanalyticsConfig.defaults("t").settings().put("x","y")); }
}