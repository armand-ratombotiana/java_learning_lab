package com.learning.lab09;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Ultra-deep tests for Abstraction and Interfaces lab.
 * Explores default method resolution, interface super calls, bridge methods.
 */
class UltraDeepTest {

    @Test
    void interfaceSuperCall() {
        interface Logger {
            default String tag() { return "default"; }
        }
        class CustomLogger implements Logger {
            @Override
            public String tag() {
                return "custom:" + Logger.super.tag();
            }
        }
        assertEquals("custom:default", new CustomLogger().tag());
    }

    @Test
    void staticMethodInInterface() {
        interface MathUtils {
            static int twice(int x) { return x * 2; }
        }
        assertEquals(10, MathUtils.twice(5));
    }

    @Test
    void functionalInterfaceLambdaConversion() {
        @FunctionalInterface
        interface Transformer {
            String transform(String input);
        }
        Transformer upper = s -> s.toUpperCase();
        assertEquals("HELLO", upper.transform("hello"));
    }
}
