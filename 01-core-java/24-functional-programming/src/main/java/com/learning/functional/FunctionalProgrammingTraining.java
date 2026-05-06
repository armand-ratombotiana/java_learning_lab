package com.learning.functional;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class FunctionalProgrammingTraining {

    public static void main(String[] args) {
        System.out.println("=== Functional Programming Training ===");

        demonstrateLambdaExpressions();
        demonstrateFunctionalInterfaces();
        demonstrateStreamOperations();
        demonstrateHigherOrderFunctions();
    }

    private static void demonstrateLambdaExpressions() {
        System.out.println("\n--- Lambda Expressions ---");

        Runnable noArg = () -> System.out.println("Runnable with no args");
        noArg.run();

        Function<String, Integer> strLength = s -> s.length();
        System.out.println("Length of 'Hello': " + strLength.apply("Hello"));

        BiFunction<Integer, Integer, Integer> sum = (a, b) -> a + b;
        System.out.println("Sum of 5 + 3: " + sum.apply(5, 3));

        Consumer<String> printer = s -> System.out.println("Printing: " + s);
        printer.accept("Message");

        Supplier<Double> random = () -> Math.random();
        System.out.println("Random: " + random.get());

        Predicate<Integer> isEven = n -> n % 2 == 0;
        System.out.println("Is 4 even? " + isEven.test(4));

        System.out.println("\nLambda Syntax Variations:");
        String[] variations = {
            "(a, b) -> a + b          // multiple params",
            "a -> a * 2               // single param, no parens",
            "() -> 42                 // no params",
            "(int a) -> { return a; } // block body"
        };
        for (String v : variations) System.out.println("  " + v);
    }

    private static void demonstrateFunctionalInterfaces() {
        System.out.println("\n--- Built-in Functional Interfaces ---");

        System.out.println("java.util.function package:");
        Map<String, String> interfaces = new LinkedHashMap<>();
        interfaces.put("Function<T,R>", "T -> R transformation");
        interfaces.put("Predicate<T>", "T -> boolean test");
        interfaces.put("Consumer<T>", "T -> void accept");
        interfaces.put("Supplier<T>", "() -> T get");
        interfaces.put("UnaryOperator<T>", "T -> T unary");
        interfaces.put("BinaryOperator<T>", "(T,T) -> T binary");
        interfaces.forEach((k, v) -> System.out.printf("  %s: %s%n", k, v));

        System.out.println("\nMethod References:");
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
        
        names.forEach(System.out::println);
        
        Function<String, String> upper = String::toUpperCase;
        System.out.println("\nUpper 'hello': " + upper.apply("hello"));

        Supplier<List<String>> listSupplier = ArrayList::new;
        System.out.println("New list: " + listSupplier.get());
    }

    private static void demonstrateStreamOperations() {
        System.out.println("\n--- Stream Pipeline ---");

        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        List<Integer> result = numbers.stream()
            .filter(n -> n % 2 == 0)
            .map(n -> n * 2)
            .collect(Collectors.toList());

        System.out.println("Original: " + numbers);
        System.out.println("Filter even, double: " + result);

        System.out.println("\nIntermediate Operations:");
        String[] intermediates = {
            "filter(Predicate) - filter elements",
            "map(Function) - transform elements",
            "flatMap(Function) - flatten streams",
            "distinct() - remove duplicates",
            "sorted() - sort elements",
            "limit(n) - take first n",
            "skip(n) - skip first n"
        };
        for (String i : intermediates) System.out.println("  - " + i);

        System.out.println("\nTerminal Operations:");
        String[] terminals = {
            "collect() - gather to collection",
            "reduce() - combine to single value",
            "forEach() - iterate each element",
            "count() - count elements",
            "anyMatch/Predicate - boolean test",
            "findFirst/Any - optional result"
        };
        for (String t : terminals) System.out.println("  - " + t);

        System.out.println("\nStream Examples:");
        int sum = numbers.stream().mapToInt(Integer::intValue).sum();
        System.out.println("Sum: " + sum);

        Optional<Integer> max = numbers.stream().max(Integer::compare);
        System.out.println("Max: " + max.orElse(0));

        Double avg = numbers.stream().mapToInt(Integer::intValue).average().orElse(0);
        System.out.println("Average: " + avg);

        Map<Boolean, List<Integer>> partition = numbers.stream()
            .collect(Collectors.partitioningBy(n -> n > 5));
        System.out.println("Partitioned >5: " + partition);
    }

    private static void demonstrateHigherOrderFunctions() {
        System.out.println("\n--- Higher Order Functions ---");

        Function<Integer, Function<Integer, Integer>> curriedAdd = a -> b -> a + b;
        Function<Integer, Integer> add5 = curriedAdd.apply(5);
        System.out.println("add5(3) = " + add5.apply(3));

        System.out.println("\nFunction Composition:");
        Function<Integer, Integer> doubleIt = x -> x * 2;
        Function<Integer, Integer> squareIt = x -> x * x;
        
        Function<Integer, Integer> doubleThenSquare = squareIt.compose(doubleIt);
        System.out.println("doubleThenSquare(3) = " + doubleThenSquare.apply(3));

        Function<Integer, Integer> squareThenDouble = squareIt.andThen(doubleIt);
        System.out.println("squareThenDouble(3) = " + squareThenDouble.apply(3));

        System.out.println("\nClosure Example:");
        IntUnaryOperator multiplyBy3 = createMultiplier(3);
        IntUnaryOperator multiplyBy10 = createMultiplier(10);
        
        System.out.println("multiplyBy3(5) = " + multiplyBy3.applyAsInt(5));
        System.out.println("multiplyBy10(5) = " + multiplyBy10.applyAsInt(5));

        System.out.println("\nFunctional Programming Principles:");
        String[] principles = {
            "1. Functions as first-class citizens",
            "2. Immutability - avoid state mutation",
            "3. Pure functions - no side effects",
            "4. Declarative - what, not how",
            "5. Lazy evaluation - defer computation"
        };
        for (String p : principles) System.out.println("  " + p);
    }

    private static IntUnaryOperator createMultiplier(int factor) {
        return n -> n * factor;
    }
}