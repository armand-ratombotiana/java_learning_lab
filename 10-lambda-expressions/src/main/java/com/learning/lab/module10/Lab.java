package com.learning.lab.module10;

import java.util.*;
import java.util.function.*;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 10: Lambda Expressions ===");
        lambdaBasicsDemo();
        functionalInterfacesDemo();
        methodReferencesDemo();
        closureDemo();
        streamsWithLambdaDemo();
    }

    static void lambdaBasicsDemo() {
        System.out.println("\n--- Lambda Basics ---");
        Greeting greeting = (name) -> "Hello, " + name + "!";
        System.out.println(greeting.sayHello("World"));

        Calculator add = (a, b) -> a + b;
        Calculator multiply = (a, b) -> a * b;
        System.out.println("Add: " + add.calculate(5, 3));
        System.out.println("Multiply: " + multiply.calculate(4, 6));

        Runnable task = () -> System.out.println("Running task");
        task.run();
    }

    static void functionalInterfacesDemo() {
        System.out.println("\n--- Functional Interfaces ---");
        Predicate<Integer> isEven = n -> n % 2 == 0;
        System.out.println("Is 4 even? " + isEven.test(4));
        System.out.println("Is 7 even? " + isEven.test(7));

        Function<String, Integer> length = s -> s.length();
        System.out.println("Length of 'Java': " + length.apply("Java"));

        Consumer<String> printer = s -> System.out.println("Print: " + s);
        printer.accept("Hello");

        Supplier<Double> random = () -> Math.random();
        System.out.println("Random: " + random.get());

        UnaryOperator<Integer> square = n -> n * n;
        System.out.println("Square of 5: " + square.apply(5));

        BinaryOperator<Integer> max = (a, b) -> a > b ? a : b;
        System.out.println("Max of 3,5: " + max.apply(3, 5));
    }

    static void methodReferencesDemo() {
        System.out.println("\n--- Method References ---");
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie");

        names.forEach(System.out::println);

        List<String> upper = names.stream().map(String::toUpperCase).toList();
        System.out.println("Uppercase: " + upper);

        Comparator<String> cmp = String::compareToIgnoreCase;
        Collections.sort(names, cmp);
        System.out.println("Sorted (ignore case): " + names);
    }

    static void closureDemo() {
        System.out.println("\n--- Closure ---");
        int factor = 2;
        IntUnaryOperator multiplier = n -> n * factor;
        System.out.println("Multiply 5 by factor(2): " + multiplier.applyAsInt(5));

        int[] counter = {0};
        Runnable increment = () -> counter[0]++;
        increment.run();
        increment.run();
        System.out.println("Counter: " + counter[0]);
    }

    static void streamsWithLambdaDemo() {
        System.out.println("\n--- Streams with Lambda ---");
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        List<Integer> result = numbers.stream()
            .filter(n -> n % 2 == 0)
            .map(n -> n * 2)
            .toList();
        System.out.println("Even, doubled: " + result);

        Optional<Integer> sum = numbers.stream().reduce(Integer::sum);
        System.out.println("Sum: " + sum.orElse(0));

        numbers.parallelStream()
            .filter(n -> n % 2 == 0)
            .forEach(n -> System.out.print(n + " "));
        System.out.println();
    }
}

@FunctionalInterface
interface Greeting {
    String sayHello(String name);
}

@FunctionalInterface
interface Calculator {
    int calculate(int a, int b);
}