package com.learning.kotlinjava;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class Lab {

    static class Person {
        private String name;
        private int age;

        Person() {}
        Person(String name, int age) { this.name = name; this.age = age; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }

        public String toString() { return name + "(" + age + ")"; }
    }

    static class DataClass {
        private final String key;
        private final int value;

        DataClass(String key, int value) { this.key = key; this.value = value; }
        public String getKey() { return key; }
        public int getValue() { return value; }
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof DataClass other)) return false;
            return Objects.equals(key, other.key) && value == other.value;
        }
        public int hashCode() { return Objects.hash(key, value); }
        public String toString() { return "DataClass(" + key + "=" + value + ")"; }
    }

    public static void main(String[] args) {
        System.out.println("=== Kotlin-Java Interop Lab ===\n");

        nullSafety();
        extensionFunctions();
        lambdaInterop();
        dataClassEquiv();
        collectionInterop();
        coroutinesVsThreads();
    }

    static void nullSafety() {
        System.out.println("--- Null Safety (Java Optional) ---");
        String nullable = null;

        var safe = Optional.ofNullable(nullable)
            .map(String::toUpperCase)
            .orElse("DEFAULT_VALUE");

        System.out.println("  Nullable value: '" + safe + "'");
        System.out.println("  Kotlin: String? vs String");
        System.out.println("  @Nullable/@NonNull annotations for interop");
    }

    static void extensionFunctions() {
        System.out.println("\n--- Extension Functions (Utility methods) ---");
        class Extensions {
            static String addExclamation(String s) { return s + "!"; }
            static <T> T elvis(T value, T fallback) { return value != null ? value : fallback; }
        }

        System.out.println("  Extension: " + Extensions.addExclamation("Hello"));
        System.out.println("  Elvis: " + Extensions.elvis(null, "default"));
        System.out.println("  Kotlin extension: fun String.addExclamation() = this + \"!\"");
    }

    static void lambdaInterop() {
        System.out.println("\n--- Lambda Interop ---");
        var names = List.of("Alice", "Bob", "Carol");

        Consumer<String> print = s -> System.out.println("  " + s);
        names.forEach(print);

        var upper = names.stream().map(String::toUpperCase).toList();
        System.out.println("  Mapped: " + upper);

        System.out.println("""
  Kotlin -> Java: (String) -> Unit becomes Function1<String, Unit>
  SAM conversion: Java interface = lambda in Kotlin
  Java -> Kotlin: Kotlin lambdas = anonymous FunctionN classes
    """);
    }

    static void dataClassEquiv() {
        System.out.println("\n--- Data Class Equivalent ---");
        var d1 = new DataClass("a", 1);
        var d2 = new DataClass("a", 1);
        var d3 = new DataClass("a", 2);

        System.out.println("  " + d1);
        System.out.println("  equals(d2): " + d1.equals(d2));
        System.out.println("  equals(d3): " + d1.equals(d3));
        System.out.println("  hashCode: " + d1.hashCode());
        System.out.println("  Kotlin: data class DataClass(val key: String, val value: Int)");
    }

    static void collectionInterop() {
        System.out.println("\n--- Collection Interop ---");
        var list = new ArrayList<>(List.of(3, 1, 4, 1, 5));
        Collections.sort(list);
        System.out.println("  Sorted: " + list);

        var map = new TreeMap<String, Integer>();
        map.put("b", 2); map.put("a", 1); map.put("c", 3);
        System.out.println("  TreeMap: " + map);

        System.out.println("""
  Kotlin uses Java collections (MutableList, MutableMap)
  No separate Kotlin collection framework
  kotlin.collections adds extension functions
  Arrays.asList -> listOf, mutableListOf -> ArrayList
    """);
    }

    static void coroutinesVsThreads() {
        System.out.println("\n--- Coroutines vs Threads ---");
        System.out.println("""
  Java Threads (kernel-level):
    new Thread(() -> work()).start()
    ~1MB stack per thread
    Expensive context switch
  Kotlin Coroutines (user-level):
    GlobalScope.launch { work() }
    ~few KB per coroutine
    Cooperative suspension (no blocking)
    Suspend functions compiled with Continuation-passing style
  Interop:
    suspend fun = Java method with Continuation parameter
    Continuation<T> has resume(T) / resumeWithException(Throwable)
    kotlinx.coroutines -> CompletableFuture bridge
    """);
    }
}
