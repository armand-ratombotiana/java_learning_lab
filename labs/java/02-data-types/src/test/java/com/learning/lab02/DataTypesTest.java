package com.learning.lab02;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class DataTypesTest {

    @Test
    @DisplayName("Primitive data types have correct sizes")
    void primitiveTypes() {
        byte b = 127;
        short s = 32767;
        int i = 2147483647;
        long l = 9223372036854775807L;
        float f = 3.14f;
        double d = 3.14159265358979;
        char c = 'A';
        boolean bool = true;

        assertEquals(127, b);
        assertEquals(32767, s);
        assertEquals(2147483647, i);
        assertEquals(9223372036854775807L, l);
        assertEquals(3.14f, f, 1e-6);
        assertEquals(3.14159265358979, d, 1e-9);
        assertEquals('A', c);
        assertTrue(bool);
    }

    @Test
    @DisplayName("Autoboxing converts primitives to wrapper objects")
    void autoboxing() {
        Integer boxed = 42;
        int unboxed = boxed;
        assertEquals(42, boxed.intValue());
        assertEquals(42, unboxed);
    }

    @Test
    @DisplayName("Wrapper class parsing")
    void wrapperParsing() {
        assertEquals(123, Integer.parseInt("123"));
        assertEquals(3.14, Double.parseDouble("3.14"), 1e-9);
        assertTrue(Boolean.parseBoolean("true"));
    }

    @Test
    @DisplayName("String is a reference type")
    void stringReferenceType() {
        String s1 = "hello";
        String s2 = "hello";
        assertSame(s1, s2);
    }

    @Test
    @DisplayName("Reference types can be null")
    void referenceNullability() {
        String nullString = null;
        assertNull(nullString);
    }

    @Test
    @DisplayName("Type conversion and casting")
    void typeConversion() {
        int i = 100;
        long l = i;
        assertEquals(100L, l);
        double d = i;
        assertEquals(100.0, d, 1e-9);
        int back = (int) d;
        assertEquals(100, back);
    }

    @Test
    @DisplayName("Integer overflow wraps around")
    void integerOverflow() {
        int max = Integer.MAX_VALUE;
        assertEquals(Integer.MIN_VALUE, max + 1);
    }

    @Test
    @DisplayName("Floating point precision")
    void floatingPointPrecision() {
        double a = 0.1;
        double b = 0.2;
        double sum = a + b;
        assertTrue(Math.abs(sum - 0.3) < 1e-10);
    }
}
