package com.capstone.microservicesmigration;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class MicroservicesmigrationEngineTest {
    private MicroservicesmigrationEngine engine;

    @BeforeEach
    void setUp() { MicroservicesmigrationEngine.reset(); engine = new MicroservicesmigrationEngine("test-eng", "1.0", true); }

    @Test void testExecute() {
        var r = engine.execute("TRANSFORM", "data");
        assertTrue(r.contains("DATA")); assertEquals(1, MicroservicesmigrationEngine.getOpCount());
    }

    @Test void testInactive() {
        var i = new MicroservicesmigrationEngine("i", "1", false);
        assertThrows(IllegalStateException.class, () -> i.execute("OP", "x"));
        assertEquals(1, MicroservicesmigrationEngine.getErrCount());
    }

    @Test void testNullOp() { assertThrows(IllegalArgumentException.class, () -> engine.execute(null, "x")); }
    @Test void testReset() { engine.execute("OP","x"); MicroservicesmigrationEngine.reset(); assertEquals(0,MicroservicesmigrationEngine.getOpCount()); }

    @ParameterizedTest @ValueSource(strings = {"read","write","delete","search"})
    void testOps(String op) { assertTrue(engine.execute(op, "x").contains(op.toUpperCase())); }

    @Test void testConfig() {
        var c = MicroservicesmigrationConfig.defaults("test");
        assertEquals("test", c.name()); assertEquals(4, c.maxWorkers());
    }

    @Test void testConfigInvalid() {
        assertThrows(IllegalArgumentException.class, () -> new MicroservicesmigrationConfig("",1,1,true,null));
        assertThrows(IllegalArgumentException.class, () -> new MicroservicesmigrationConfig("n",0,1,true,null));
    }
}