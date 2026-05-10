package com.learning.lab.module08;

import java.util.*;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 08: Generics ===");
        genericClassDemo();
        genericMethodsDemo();
        boundedTypeDemo();
        wildcardDemo();
        genericInterfacesDemo();
    }

    static void genericClassDemo() {
        System.out.println("\n--- Generic Class ---");
        Box<String> stringBox = new Box<>("Hello");
        System.out.println("String box: " + stringBox.getContent());

        Box<Integer> intBox = new Box<>(42);
        System.out.println("Integer box: " + intBox.getContent());

        Pair<String, Integer> pair = new Pair<>("Age", 25);
        System.out.println("Pair: " + pair.getKey() + " = " + pair.getValue());
    }

    static void genericMethodsDemo() {
        System.out.println("\n--- Generic Methods ---");
        String[] names = {"Alice", "Bob", "Charlie"};
        Integer[] numbers = {1, 2, 3, 4, 5};

        System.out.println("Max: " + findMax(numbers));
        System.out.println("First: " + getFirst(names));

        List<String> list = Arrays.asList("a", "b", "c");
        System.out.println("To array: " + Arrays.toString(toArray(list)));
    }

    static void boundedTypeDemo() {
        System.out.println("\n--- Bounded Type Parameters ---");
        NumberBox<Integer> intBox = new NumberBox<>(100);
        System.out.println("Double value: " + intBox.getDoubleValue());

        NumberBox<Double> doubleBox = new NumberBox<>(3.14);
        System.out.println("Sum with 10: " + doubleBox.addTo(10));
    }

    static void wildcardDemo() {
        System.out.println("\n--- Wildcards ---");
        List<Integer> integers = List.of(1, 2, 3);
        List<Double> doubles = List.of(1.1, 2.2);

        printList(integers);
        printList(doubles);

        List<Number> numbers = new ArrayList<>();
        copyElements(integers, numbers);
        System.out.println("Copied: " + numbers);
    }

    static void genericInterfacesDemo() {
        System.out.println("\n--- Generic Interfaces ---");
        Container<String> stringContainer = new StringContainer("Value");
        System.out.println("String container: " + stringContainer.get());

        Container<Integer> intContainer = new IntegerContainer(42);
        System.out.println("Integer container: " + intContainer.get());
    }

    static <T extends Comparable<T>> T findMax(T[] array) {
        T max = array[0];
        for (T item : array) {
            if (item.compareTo(max) > 0) max = item;
        }
        return max;
    }

    static <T> T getFirst(T[] array) { return array[0]; }

    static <T> T[] toArray(List<T> list) {
        @SuppressWarnings("unchecked")
        T[] array = (T[]) list.toArray();
        return array;
    }

    static void printList(List<?> list) {
        System.out.println("List: " + list);
    }

    static <T extends Number> void copyElements(List<T> source, List<? super T> dest) {
        dest.addAll(source);
    }
}

class Box<T> {
    private T content;
    public Box(T content) { this.content = content; }
    public T getContent() { return content; }
    public void setContent(T content) { this.content = content; }
}

class Pair<K, V> {
    private K key;
    private V value;
    public Pair(K key, V value) { this.key = key; this.value = value; }
    public K getKey() { return key; }
    public V getValue() { return value; }
}

class NumberBox<T extends Number> {
    private T number;
    public NumberBox(T number) { this.number = number; }
    public double getDoubleValue() { return number.doubleValue(); }
    public double addTo(double val) { return number.doubleValue() + val; }
}

interface Container<T> {
    T get();
    void set(T value);
}

class StringContainer implements Container<String> {
    private String value;
    public StringContainer(String value) { this.value = value; }
    public String get() { return value; }
    public void set(String value) { this.value = value; }
}

class IntegerContainer implements Container<Integer> {
    private Integer value;
    public IntegerContainer(Integer value) { this.value = value; }
    public Integer get() { return value; }
    public void set(Integer value) { this.value = value; }
}