package com.capstone.mlplatform;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;

class MlplatformConfigTest {
    @Test void testDefaults() { var c = MlplatformConfig.defaults("t"); assertEquals("t",c.name()); }
    @Test void testCustom() { var c = new MlplatformConfig("c",8,5000,false,Map.of("k","v")); assertEquals(8,c.maxWorkers()); }
    @Test void testImmutable() { assertThrows(UnsupportedOperationException.class, () -> MlplatformConfig.defaults("t").settings().put("x","y")); }
}