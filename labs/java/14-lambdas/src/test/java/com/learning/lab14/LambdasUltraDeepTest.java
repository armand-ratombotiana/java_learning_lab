package com.learning.lab14;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.function.*;

class LambdasUltraDeepTest {

    @Test
    void lambdaCapturesLocalVariableThatIsEffectivelyFinal() {
        int base = 10;
        Function<Integer, Integer> addBase = x -> x + base;
        assertEquals(15, addBase.apply(5));
    }

    @Test
    void methodReferenceStaticMethod() {
        Function<String, Integer> parser = Integer::parseInt;
        assertEquals(123, parser.apply("123"));
    }

    @Test
    void methodReferenceInstanceMethodOnArbitraryObject() {
        Function<String, String> toUpper = String::toUpperCase;
        assertEquals("HELLO", toUpper.apply("hello"));
    }

    @Test
    void chainedFunctionComposition() {
        Function<Integer, Integer> multiplyBy2 = n -> n * 2;
        Function<Integer, Integer> add3 = n -> n + 3;
        Function<Integer, Integer> composed = multiplyBy2.andThen(add3);
        assertEquals(13, composed.apply(5));
    }

    @Test
    void predicateChainingWithAnd() {
        java.util.function.Predicate<String> startsWithA = s -> s.startsWith("A");
        java.util.function.Predicate<String> endsWithE = s -> s.endsWith("e");
        assertTrue(startsWithA.and(endsWithE).test("Alice"));
        assertFalse(startsWithA.and(endsWithE).test("Apple"));
    }

    @Test
    void supplierLazyEvaluation() {
        Supplier<String> lazy = () -> {
            return "Lazy Value";
        };
        assertEquals("Lazy Value", lazy.get());
    }
}
