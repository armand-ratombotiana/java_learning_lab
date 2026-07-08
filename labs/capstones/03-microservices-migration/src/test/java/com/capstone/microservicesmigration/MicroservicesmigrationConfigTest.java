package com.capstone.microservicesmigration;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;

class MicroservicesmigrationConfigTest {
    @Test void testDefaults() { var c = MicroservicesmigrationConfig.defaults("t"); assertEquals("t",c.name()); }
    @Test void testCustom() { var c = new MicroservicesmigrationConfig("c",8,5000,false,Map.of("k","v")); assertEquals(8,c.maxWorkers()); }
    @Test void testImmutable() { assertThrows(UnsupportedOperationException.class, () -> MicroservicesmigrationConfig.defaults("t").settings().put("x","y")); }
}