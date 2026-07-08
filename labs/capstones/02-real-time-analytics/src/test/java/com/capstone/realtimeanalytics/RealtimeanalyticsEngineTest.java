package com.capstone.realtimeanalytics;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class RealtimeanalyticsEngineTest {
    private RealtimeanalyticsEngine engine;

    @BeforeEach
    void setUp() { RealtimeanalyticsEngine.reset(); engine = new RealtimeanalyticsEngine("test-eng", "1.0", true); }

    @Test void testExecute() {
        var r = engine.execute("TRANSFORM", "data");
        assertTrue(r.contains("DATA")); assertEquals(1, RealtimeanalyticsEngine.getOpCount());
    }

    @Test void testInactive() {
        var i = new RealtimeanalyticsEngine("i", "1", false);
        assertThrows(IllegalStateException.class, () -> i.execute("OP", "x"));
        assertEquals(1, RealtimeanalyticsEngine.getErrCount());
    }

    @Test void testNullOp() { assertThrows(IllegalArgumentException.class, () -> engine.execute(null, "x")); }
    @Test void testReset() { engine.execute("OP","x"); RealtimeanalyticsEngine.reset(); assertEquals(0,RealtimeanalyticsEngine.getOpCount()); }

    @ParameterizedTest @ValueSource(strings = {"read","write","delete","search"})
    void testOps(String op) { assertTrue(engine.execute(op, "x").contains(op.toUpperCase())); }

    @Test void testConfig() {
        var c = RealtimeanalyticsConfig.defaults("test");
        assertEquals("test", c.name()); assertEquals(4, c.maxWorkers());
    }

    @Test void testConfigInvalid() {
        assertThrows(IllegalArgumentException.class, () -> new RealtimeanalyticsConfig("",1,1,true,null));
        assertThrows(IllegalArgumentException.class, () -> new RealtimeanalyticsConfig("n",0,1,true,null));
    }
}