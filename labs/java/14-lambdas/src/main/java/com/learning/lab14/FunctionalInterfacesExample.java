package com.learning.lab14;

import java.util.*;
import java.util.function.*;

/**
 * Demonstrates Predicate, Function, Consumer, Supplier functional interfaces with lambdas.
 */
public class FunctionalInterfacesExample {

    public static void showFunctionalInterfaces() {
        System.out.println("=== Predicate, Function, Consumer, Supplier ===");

        Predicate<String> isEmpty = s -> s.isEmpty();
        System.out.println("isEmpty.test(\"\"): " + isEmpty.test(""));
        System.out.println("isEmpty.test(\"Java\"): " + isEmpty.test("Java"));

        Predicate<Integer> isEven = n -> n % 2 == 0;
        List<Integer> nums = Arrays.asList(1, 2, 3, 4, 5, 6);
        List<Integer> evens = nums.stream().filter(isEven).toList();
        System.out.println("Even numbers: " + evens);

        Function<String, Integer> lengthFunc = String::length;
        System.out.println("lengthFunc.apply(\"Hello\"): " + lengthFunc.apply("Hello"));

        Function<Integer, String> repeat = n -> "*".repeat(n);
        System.out.println("repeat.apply(5): " + repeat.apply(5));

        Consumer<String> printer = msg -> System.out.println("Consumed: " + msg);
        printer.accept("Hello Consumer!");

        List<String> collected = new ArrayList<>();
        Consumer<String> collector = collected::add;
        collector.accept("item1");
        collector.accept("item2");
        System.out.println("Collected: " + collected);

        Supplier<Double> piSupplier = () -> Math.PI;
        System.out.println("PI from supplier: " + piSupplier.get());

        Supplier<String> greeting = () -> "Hello, World!";
        System.out.println("Greeting: " + greeting.get());
    }
}
