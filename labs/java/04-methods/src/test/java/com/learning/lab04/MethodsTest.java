package com.learning.lab04;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class MethodsTest {

    int add(int a, int b) { return a + b; }
    double add(double a, double b) { return a + b; }
    int max(int a, int b, int c) { return Math.max(a, Math.max(b, c)); }
    String join(String... words) { return String.join(" ", words); }
    int factorial(int n) { return n <= 1 ? 1 : n * factorial(n - 1); }

    @Test
    @DisplayName("Method returning sum")
    void methodAdd() {
        assertEquals(5, add(2, 3));
    }

    @Test
    @DisplayName("Method overloading with different parameter types")
    void methodOverloading() {
        assertEquals(5, add(2, 3));
        assertEquals(5.5, add(2.5, 3.0), 1e-9);
    }

    @Test
    @DisplayName("Method with varargs")
    void varargsMethod() {
        assertEquals("a b c", join("a", "b", "c"));
        assertEquals("", join());
    }

    @Test
    @DisplayName("Method with multiple parameters")
    void multipleParameters() {
        assertEquals(10, max(10, 5, 8));
        assertEquals(5, max(3, 5, 1));
        assertEquals(7, max(7, 7, 7));
    }

    @Test
    @DisplayName("Recursive method computes factorial")
    void recursiveFactorial() {
        assertEquals(120, factorial(5));
        assertEquals(1, factorial(0));
        assertEquals(1, factorial(1));
    }

    @Test
    @DisplayName("Pass by value for primitives")
    void passByValue() {
        int x = 10;
        modifyPrimitive(x);
        assertEquals(10, x);
    }

    void modifyPrimitive(int val) { val = 999; }

    @Test
    @DisplayName("Pass by value for references (reference copied)")
    void passReferenceByValue() {
        var sb = new StringBuilder("hello");
        appendWorld(sb);
        assertEquals("helloworld", sb.toString());
    }

    void appendWorld(StringBuilder sb) { sb.append("world"); }

    @Test
    @DisplayName("Method returns early")
    void earlyReturn() {
        assertEquals(0, factorial(-1));
    }

    int factorialSafe(int n) { return n < 0 ? 0 : factorial(n); }

    @Test
    @DisplayName("Static utility method")
    void staticUtility() {
        assertEquals(6, Math.max(3, 6));
    }
}
