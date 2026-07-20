package com.learning.lab15;

import org.junit.jupiter.api.*;
import java.util.*;
import java.util.function.Function;
import static org.junit.jupiter.api.Assertions.*;

class FunctionalProgrammingTest {

    @Test
    @DisplayName("PureFunctionExample.sumPure computes sum")
    void sumPureComputesSum() {
        assertEquals(15, PureFunctionExample.sumPure(List.of(1, 2, 3, 4, 5)));
        assertEquals(0, PureFunctionExample.sumPure(List.of()));
    }

    @Test
    @DisplayName("PureFunctionExample.applyToEach transforms elements")
    void applyToEachTransforms() {
        List<Integer> doubled = PureFunctionExample.applyToEach(List.of(1, 2, 3), n -> n * 2);
        assertEquals(List.of(2, 4, 6), doubled);
    }

    @Test
    @DisplayName("PureFunctionExample.applyToEach returns unmodifiable list")
    void applyToEachReturnsUnmodifiable() {
        var result = PureFunctionExample.applyToEach(List.of(1), n -> n);
        assertThrows(UnsupportedOperationException.class, () -> result.add(2));
    }

    @Test
    @DisplayName("PureFunctionExample.formatGreeting formats correctly")
    void formatGreetingFormats() {
        assertEquals("Hello, Alice! Welcome to functional programming.",
            PureFunctionExample.formatGreeting("Alice"));
    }

    @Test
    @DisplayName("OptionalChainingExample.getName returns short name")
    void getNameReturnsShortName() {
        assertEquals("Al", OptionalChainingExample.getName(false));
        assertNull(OptionalChainingExample.getName(true));
    }

    @Test
    @DisplayName("FunctionComposition andThen composes left to right")
    void andThenComposes() {
        Function<Integer, Integer> multiplyBy2 = n -> n * 2;
        Function<Integer, Integer> add3 = n -> n + 3;
        Function<Integer, String> toString = Object::toString;
        assertEquals("13", multiplyBy2.andThen(add3).andThen(toString).apply(5));
    }

    @Test
    @DisplayName("FunctionComposition compose chains right to left")
    void composeChainsRightToLeft() {
        Function<Integer, Integer> multiplyBy2 = n -> n * 2;
        Function<Integer, Integer> add3 = n -> n + 3;
        assertEquals(16, multiplyBy2.compose(add3).apply(5));
    }

    @Test
    @DisplayName("Predicate composition with and")
    void predicateAnd() {
        java.util.function.Predicate<String> startsWithA = s -> s.startsWith("A");
        java.util.function.Predicate<String> endsWithE = s -> s.endsWith("e");
        assertTrue(startsWithA.and(endsWithE).test("Alice"));
        assertFalse(startsWithA.and(endsWithE).test("Anna"));
    }

    @Test
    @DisplayName("Stream pipeline with collect produces correct result")
    void streamPipeline() {
        var words = List.of("apple", "banana", "avocado", "cherry", "apricot");
        var result = words.stream()
            .filter(s -> s.startsWith("a"))
            .map(String::toUpperCase)
            .sorted()
            .toList();
        assertEquals(List.of("APPLE", "APRICOT", "AVOCADO"), result);
    }

    @Test
    @DisplayName("Transaction summary groups by user")
    void transactionSummary() {
        var transactions = List.of(
            new Transaction("Alice", 100),
            new Transaction("Bob", 200),
            new Transaction("Alice", 50)
        );
        var summary = transactions.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                Transaction::user,
                java.util.stream.Collectors.summingInt(Transaction::amount)
            ));
        assertEquals(150, summary.get("Alice"));
        assertEquals(200, summary.get("Bob"));
    }
}
