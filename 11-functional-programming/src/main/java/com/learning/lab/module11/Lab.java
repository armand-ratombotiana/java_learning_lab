package com.learning.lab.module11;

import java.util.function.*;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 11: Functional Programming ===");
        functionalInterfacesDemo();
        pureFunctionsDemo();
        higherOrderFunctionsDemo();
        methodReferencesDemo();
        closuresDemo();
        compositionDemo();
    }

    static void functionalInterfacesDemo() {
        System.out.println("\n--- Functional Interfaces ---");
        Predicate<Integer> isEven = n -> n % 2 == 0;
        Function<Integer, Integer> square = n -> n * n;
        Consumer<String> printer = s -> System.out.println("Print: " + s);
        Supplier<Double> random = () -> Math.random();
        UnaryOperator<Integer> doubleIt = n -> n * 2;

        System.out.println("isEven(4): " + isEven.test(4));
        System.out.println("square(5): " + square.apply(5));
        printer.accept("Hello FP!");
        System.out.println("Random: " + random.get());
        System.out.println("doubleIt(10): " + doubleIt.apply(10));
    }

    static void pureFunctionsDemo() {
        System.out.println("\n--- Pure Functions ---");
        Function<Integer, Integer> pureAdd = (a, b) -> a + b;
        Function<String, String> pureConcat = (s1, s2) -> s1 + s2;

        int result = pureAdd.apply(3, 5);
        String text = pureConcat.apply("Pure ", "Functions");
        System.out.println("Pure add(3,5): " + result);
        System.out.println("Pure concat: " + text);

        int[] counter = {0};
        Runnable impure = () -> counter[0]++;
        impure.run();
        System.out.println("Impure counter: " + counter[0]);
    }

    static void higherOrderFunctionsDemo() {
        System.out.println("\n--- Higher-Order Functions ---");
        Function<Function<Integer, Integer>, Integer> applyTwice = 
            f -> f.apply(f.apply(2));
        Function<Integer, Integer> addOne = x -> x + 1;
        System.out.println("applyTwice(addOne): " + applyTwice.apply(addOne));

        Supplier<Function<Integer, Integer>> creator = () -> x -> x * x;
        Function<Integer, Integer> squareFunc = creator.get();
        System.out.println("Created square(4): " + squareFunc.apply(4));
    }

    static void methodReferencesDemo() {
        System.out.println("\n--- Method References ---");
        Function<String, String> toUpper = String::toUpperCase;
        Supplier<String> now = LocalTime::now;
        BiFunction<String, String, String> concat = String::concat;

        System.out.println("toUpper('hello'): " + toUpper.apply("hello"));
        System.out.println("concat('Hello ', 'World'): " + concat.apply("Hello ", "World"));

        List<String> names = List.of("Alice", "Bob", "Charlie");
        names.stream().forEach(System.out::println);
    }

    static void closuresDemo() {
        System.out.println("\n--- Closures ---");
        IntSupplier counter = createCounter();
        System.out.println("Counter 1: " + counter.getAsInt());
        System.out.println("Counter 2: " + counter.getAsInt());
        System.out.println("Counter 3: " + counter.getAsInt());

        int factor = 3;
        Function<Integer, Integer> multiplier = x -> x * factor;
        System.out.println("multiply(5) with factor=3: " + multiplier.apply(5));
    }

    static IntSupplier createCounter() {
        int[] count = {0};
        return () -> ++count[0];
    }

    static void compositionDemo() {
        System.out.println("\n--- Function Composition ---");
        Function<Integer, Integer> add2 = x -> x + 2;
        Function<Integer, Integer> multiply3 = x -> x * 3;

        Function<Integer, Integer> composed = add2.andThen(multiply3);
        Function<Integer, Integer> composed2 = multiply3.compose(add2);

        System.out.println("(5 + 2) * 3 = " + composed.apply(5));
        System.out.println("(" + 5 + " + 2) * 3 = " + composed2.apply(5));

        Predicate<String> startsWithA = s -> s.startsWith("A");
        Predicate<String> longerThan5 = s -> s.length() > 5;
        Predicate<String> combined = startsWithA.and(longerThan5);

        System.out.println("'Apple' starts with A and longer than 5: " + combined.test("Apple"));
        System.out.println("'Ant' starts with A and longer than 5: " + combined.test("Ant"));
    }
}

import java.time.LocalTime;
import java.util.List;
