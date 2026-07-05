package com.learning.lab14;

import java.util.*;
import java.util.function.*;

/**
 * Demonstrates lambda expression syntax and functional interfaces.
 */
public class LambdaSyntaxExample {

    public static void showLambdaSyntax() {
        System.out.println("=== Lambda Syntax ===");

        Comparator<String> byLength = (a, b) -> Integer.compare(a.length(), b.length());
        List<String> words = Arrays.asList("java", "lambda", "functional", "code");
        words.sort(byLength);
        System.out.println("Sorted by length: " + words);

        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        numbers.forEach(n -> System.out.print(n + " "));
        System.out.println();

        BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;
        System.out.println("Lambda add(5, 3): " + add.apply(5, 3));

        Supplier<Double> random = () -> Math.random();
        System.out.println("Random from supplier: " + random.get());
    }
}
