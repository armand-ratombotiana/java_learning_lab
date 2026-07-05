package com.learning.lab15;

import java.util.function.*;

/**
 * Demonstrates function composition using andThen and compose.
 */
public class FunctionCompositionExample {

    public static void showFunctionComposition() {
        System.out.println("=== Function Composition ===");

        Function<Integer, Integer> multiplyBy2 = n -> n * 2;
        Function<Integer, Integer> add3 = n -> n + 3;
        Function<Integer, String> toString = Object::toString;

        Function<Integer, String> composed = multiplyBy2.andThen(add3).andThen(toString);
        System.out.println("((5 * 2) + 3) = " + composed.apply(5));

        Function<Integer, Integer> addThenMultiply = multiplyBy2.compose(add3);
        System.out.println("(5 + 3) * 2 = " + addThenMultiply.apply(5));

        Predicate<String> startsWithA = s -> s.startsWith("A");
        Predicate<String> endsWithE = s -> s.endsWith("e");
        Predicate<String> combined = startsWithA.and(endsWithE);

        System.out.println("'Alice' matches: " + combined.test("Alice"));
        System.out.println("'Apple' matches: " + combined.test("Apple"));
    }
}
