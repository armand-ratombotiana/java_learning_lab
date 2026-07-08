package com.capstone.ecommerce;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;

class EcommerceConfigTest {
    @Test void testDefaults() { var c = EcommerceConfig.defaults("t"); assertEquals("t",c.name()); }
    @Test void testCustom() { var c = new EcommerceConfig("c",8,5000,false,Map.of("k","v")); assertEquals(8,c.maxWorkers()); }
    @Test void testImmutable() { assertThrows(UnsupportedOperationException.class, () -> EcommerceConfig.defaults("t").settings().put("x","y")); }
}