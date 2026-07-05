package com.learning.lab11;

/**
 * Demonstrates a generic class with type parameters.
 */
public class GenericClassExample {

    public static void showGenericClass() {
        System.out.println("=== Generic Class ===");

        Box<String> stringBox = new Box<>("Hello Generics");
        Box<Integer> intBox = new Box<>(42);
        Box<Double> doubleBox = new Box<>(3.14);

        System.out.println("String box: " + stringBox.get());
        System.out.println("Integer box: " + intBox.get());
        System.out.println("Double box: " + doubleBox.get());

        Pair<String, Integer> pair = new Pair<>("Age", 30);
        System.out.println("Pair: " + pair.getKey() + " -> " + pair.getValue());
    }
}

class Box<T> {
    private T value;

    public Box(T value) {
        this.value = value;
    }

    public T get() { return value; }
    public void set(T value) { this.value = value; }
}

class Pair<K, V> {
    private K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() { return key; }
    public V getValue() { return value; }
}
