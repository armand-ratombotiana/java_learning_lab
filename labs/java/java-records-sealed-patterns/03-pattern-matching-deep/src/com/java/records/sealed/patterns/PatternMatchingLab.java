package com.java.records.sealed.patterns;

import java.util.*;
import java.util.stream.*;

/**
 * Lab 03: Pattern Matching Deep Dive — type patterns, record patterns,
 * nested patterns, guarded patterns, null handling.
 */
public class PatternMatchingLab {

    // --- Sealed tree for Boolean expressions ---
    sealed interface BoolExpr permits BoolVal, NotExpr, AndExpr, OrExpr {}
    record BoolVal(boolean value) implements BoolExpr {}
    record NotExpr(BoolExpr expr) implements BoolExpr {}
    record AndExpr(BoolExpr left, BoolExpr right) implements BoolExpr {}
    record OrExpr(BoolExpr left, BoolExpr right) implements BoolExpr {}

    // --- Evaluate using exhaustive pattern matching ---
    public boolean evaluate(BoolExpr expr) {
        return switch (expr) {
            case BoolVal(boolean v) -> v;
            case NotExpr(BoolExpr e) -> !evaluate(e);
            case AndExpr(BoolExpr l, BoolExpr r) -> evaluate(l) && evaluate(r);
            case OrExpr(BoolExpr l, BoolExpr r) -> evaluate(l) || evaluate(r);
        };
    }

    // --- Guarded patterns with when ---
    public String classifyNumber(Object obj) {
        return switch (obj) {
            case Integer i when i < 0 -> "negative";
            case Integer i when i == 0 -> "zero";
            case Integer i -> "positive";
            case String s when s.matches("-?\\d+") -> "numeric string";
            case String s -> "text: " + s;
            case null -> "null object";
            default -> "unknown";
        };
    }

    // --- Nested record pattern ---
    record Pair<T, U>(T first, U second) {}
    record Person(String name, int age) {}

    public String extractNested(Object obj) {
        return switch (obj) {
            case Pair(Person(String name, int age), Person(String name2, int age2)) ->
                    name + "(" + age + ") and " + name2 + "(" + age2 + ")";
            case Pair(Person(String name, int age), var second) when second == null ->
                    name + "(" + age + ") and null";
            case Pair(var first, var second) ->
                    "Pair of " + first + " and " + second;
            case null -> "null";
            default -> obj.toString();
        };
    }

    // --- Flatten with pattern matching ---
    sealed interface Nested permits IntVal, NestedList {}
    record IntVal(int value) implements Nested {}
    record NestedList(List<Nested> items) implements Nested {}

    public List<Integer> flatten(Nested nested) {
        return switch (nested) {
            case IntVal(int v) -> List.of(v);
            case NestedList(var items) ->
                    items.stream().map(this::flatten).flatMap(List::stream).toList();
        };
    }

    // --- Demo ---
    public static void main(String[] args) {
        var lab = new PatternMatchingLab();

        // Evaluate boolean expression: (true AND (NOT false)) OR false
        var expr = new OrExpr(
                new AndExpr(new BoolVal(true), new NotExpr(new BoolVal(false))),
                new BoolVal(false));
        System.out.println("Evaluate: " + lab.evaluate(expr));

        // Classify numbers
        System.out.println(lab.classifyNumber(-5));
        System.out.println(lab.classifyNumber("123"));
        System.out.println(lab.classifyNumber(null));

        // Flatten nested structure
        var nested = new NestedList(List.of(
                new IntVal(1),
                new IntVal(2),
                new NestedList(List.of(new IntVal(3), new IntVal(4))),
                new IntVal(5)));
        System.out.println("Flattened: " + lab.flatten(nested));

        // Nested record patterns
        var pair = new Pair<>(new Person("Alice", 30), new Person("Bob", 25));
        System.out.println(lab.extractNested(pair));
    }
}
