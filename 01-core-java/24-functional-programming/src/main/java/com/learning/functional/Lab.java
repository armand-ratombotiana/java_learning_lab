package com.learning.functional;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class Lab {

    public static void main(String[] args) {
        System.out.println("=== Functional Programming Lab ===\n");

        lambdaExpressions();
        functionalInterfaces();
        streamsApi();
        optionalMonad();
        methodReferences();
        composingFunctions();
    }

    static void lambdaExpressions() {
        System.out.println("--- Lambda Expressions ---");

        List<String> names = Arrays.asList("Charlie", "Alice", "Bob", "Diana");

        names.sort((a, b) -> a.compareTo(b));
        System.out.println("  Sorted with lambda: " + names);

        names.sort(String::compareToIgnoreCase);
        System.out.println("  Sorted (MR):       " + names);

        Runnable greeting = () -> System.out.println("  Hello from lambda!");
        greeting.run();

        IntBinaryOperator max = (a, b) -> a > b ? a : b;
        System.out.println("  Max of 42, 17: " + max.applyAsInt(42, 17));

        System.out.println("\n  Lambda syntax forms:");
        String[][] forms = {
            {"(x) -> x * 2", "Single param, expression body"},
            {"(x, y) -> x + y", "Multiple params, expression"},
            {"() -> 42", "No params"},
            {"x -> { return x * 2; }", "Block body with return"},
            {"(String s) -> s.length()", "Explicit type"}
        };
        for (String[] f : forms) System.out.printf("  %-30s %s%n", f[0], f[1]);
    }

    static void functionalInterfaces() {
        System.out.println("\n--- Key Functional Interfaces ---");

        // Predicate
        Predicate<String> isEmpty = s -> s == null || s.isEmpty();
        System.out.println("  Predicate: isEmpty.test(\"hello\") = " + isEmpty.test("hello"));

        // Function
        Function<String, Integer> parser = Integer::parseInt;
        System.out.println("  Function: parser.apply(\"42\") = " + parser.apply("42"));

        // Consumer
        var sb = new StringBuilder();
        Consumer<String> appender = s -> sb.append(s).append(",");
        appender.accept("a");
        appender.accept("b");
        appender.accept("c");
        System.out.println("  Consumer: sb = " + sb);

        // Supplier
        Supplier<Double> random = Math::random;
        System.out.println("  Supplier: random.get() = " + random.get());

        // BiFunction
        BiFunction<Integer, Integer, String> adder = (a, b) -> a + " + " + b + " = " + (a + b);
        System.out.println("  BiFunction: " + adder.apply(3, 5));

        // UnaryOperator
        UnaryOperator<String> shout = s -> s.toUpperCase() + "!";
        System.out.println("  UnaryOperator: " + shout.apply("hello"));

        System.out.println("\n  java.util.function package summary:");
        String[][] funcs = {
            {"Predicate<T>", "test(T) -> boolean"},
            {"Function<T,R>", "apply(T) -> R"},
            {"Consumer<T>", "accept(T) -> void"},
            {"Supplier<T>", "get() -> T"},
            {"BiFunction<T,U,R>", "apply(T,U) -> R"},
            {"UnaryOperator<T>", "apply(T) -> T (Function<T,T>)"},
            {"BinaryOperator<T>", "apply(T,T) -> T (BiFunction<T,T,T>)"}
        };
        for (String[] f : funcs) System.out.printf("  %-22s %s%n", f[0], f[1]);
    }

    static void streamsApi() {
        System.out.println("\n--- Streams API ---");

        record Product(String name, String category, double price) {}

        List<Product> products = List.of(
            new Product("Laptop", "Electronics", 1200.00),
            new Product("Phone", "Electronics", 800.00),
            new Product("Shirt", "Clothing", 40.00),
            new Product("Jeans", "Clothing", 80.00),
            new Product("Tablet", "Electronics", 350.00),
            new Product("Socks", "Clothing", 12.00)
        );

        System.out.println("  Products > $100 (sorted by price):");
        products.stream()
            .filter(p -> p.price() > 100)
            .sorted(Comparator.comparingDouble(Product::price))
            .forEach(p -> System.out.printf("    %s: $%.2f%n", p.name(), p.price()));

        double avg = products.stream()
            .filter(p -> p.category().equals("Electronics"))
            .collect(Collectors.averagingDouble(Product::price));
        System.out.printf("  Avg Electronics price: $%.2f%n", avg);

        Map<String, List<Product>> byCategory = products.stream()
            .collect(Collectors.groupingBy(Product::category));
        System.out.println("  Products by category:");
        byCategory.forEach((cat, prods) -> System.out.printf("    %s: %s%n", cat,
            prods.stream().map(Product::name).collect(Collectors.joining(", "))));

        String allNames = products.stream()
            .map(Product::name)
            .collect(Collectors.joining(", "));
        System.out.println("  All products: " + allNames);

        long cheapCount = products.stream()
            .filter(p -> p.price() < 50)
            .count();
        System.out.println("  Products under $50: " + cheapCount);

        double total = products.stream()
            .mapToDouble(Product::price)
            .sum();
        System.out.printf("  Total inventory value: $%.2f%n", total);

        System.out.println("\n  Stream operations lifecycle:");
        String[] ops = {
            "Source: collection, array, generator function, IO",
            "Intermediate: filter, map, sorted, distinct, limit, skip",
            "Terminal: collect, forEach, count, reduce, findFirst, anyMatch"
        };
        for (String o : ops) System.out.println("  " + o);
    }

    static void optionalMonad() {
        System.out.println("\n--- Optional<T> ---");

        Optional<String> full = Optional.of("Hello");
        Optional<String> empty = Optional.empty();

        System.out.println("  of(\"Hello\").map(String::length): " + full.map(String::length).orElse(0));
        System.out.println("  empty.map(String::length):     " + empty.map(String::length).orElse(-1));

        String result = full
            .filter(s -> s.length() > 3)
            .map(String::toUpperCase)
            .orElseGet(() -> "default");
        System.out.println("  Chained Optional: " + result);

        Optional.ofNullable(null)
            .ifPresentOrElse(
                v -> System.out.println("  Value: " + v),
                () -> System.out.println("  null was handled safely")
            );

        System.out.println("\n  Optional best practices:");
        String[][] tips = {
            {"DO", "Use Optional for return types"},
            {"DO", "Use orElse/orElseGet for defaults"},
            {"DON'T", "Use Optional for fields or params"},
            {"DON'T", "Call get() without isPresent() check"}
        };
        for (String[] t : tips) System.out.printf("  %-6s %s%n", t[0], t[1]);
    }

    static void methodReferences() {
        System.out.println("\n--- Method References ---");

        List<Integer> nums = List.of(3, 1, 4, 1, 5, 9, 2, 6);

        System.out.print("  Static method ref (Integer::toHexString): ");
        nums.stream().map(Integer::toHexString).forEach(s -> System.out.print(s + " "));
        System.out.println();

        System.out.print("  Instance method ref (String::toUpperCase): ");
        List.of("a", "b", "c").stream().map(String::toUpperCase).forEach(s -> System.out.print(s + " "));
        System.out.println();

        System.out.print("  Constructor ref (ArrayList::new): ");
        Supplier<List<Integer>> listSupplier = ArrayList::new;
        List<Integer> newList = listSupplier.get();
        newList.addAll(nums);
        System.out.println(newList);

        System.out.println("\n  Method reference types:");
        String[][] mrs = {
            {"Static", "Integer::parseInt", "Class::staticMethod"},
            {"Instance (specific)", "str::length", "object::instanceMethod"},
            {"Instance (arbitrary)", "String::length", "Class::instanceMethod"},
            {"Constructor", "ArrayList::new", "Class::new"}
        };
        for (String[] mr : mrs) System.out.printf("  %-24s %-20s %s%n", mr[0], mr[1], mr[2]);
    }

    static void composingFunctions() {
        System.out.println("\n--- Function Composition ---");

        Function<Integer, Integer> times2 = x -> x * 2;
        Function<Integer, Integer> plus3 = x -> x + 3;
        Function<Integer, Integer> square = x -> x * x;

        // compose: apply argument -> times2 -> plus3
        Function<Integer, Integer> composed = times2.andThen(plus3);
        System.out.println("  andThen: (x*2).andThen(x+3) => 5 -> " + composed.apply(5));

        // compose: apply argument -> plus3 -> times2 (reverse of andThen)
        Function<Integer, Integer> composed2 = times2.compose(plus3);
        System.out.println("  compose: (x*2).compose(x+3) => 5 -> " + composed2.apply(5));

        // Predicate chaining
        Predicate<String> nonNull = Objects::nonNull;
        Predicate<String> notEmpty = s -> !s.isEmpty();
        Predicate<String> isLong = s -> s.length() > 5;
        Predicate<String> valid = nonNull.and(notEmpty).and(isLong);
        System.out.println("  Predicate chain: valid.test(\"hello\") = " + valid.test("hello"));
        System.out.println("  Predicate chain: valid.test(\"hello world\") = " + valid.test("hello world"));

        // Chaining collectors
        List<String> words = List.of("functional", "programming", "java", "streams", "lambda");
        Map<Integer, Long> lengthCounts = words.stream()
            .collect(Collectors.groupingBy(String::length, Collectors.counting()));
        System.out.println("  Word length frequencies: " + lengthCounts);

        // Parallel streams
        long sum = LongStream.rangeClosed(1, 10_000_000)
            .parallel()
            .sum();
        System.out.println("  Parallel sum 1..10M: " + sum);
    }
}
