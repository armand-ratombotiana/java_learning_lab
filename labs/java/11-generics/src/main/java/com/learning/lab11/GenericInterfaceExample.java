package com.learning.lab11;

/**
 * Demonstrates generic interfaces and their implementations.
 */
public class GenericInterfaceExample {

    public static void showGenericInterface() {
        System.out.println("=== Generic Interface ===");

        Transformer<String, Integer> lengthTransformer = s -> s.length();
        Transformer<Integer, String> stringTransformer = Object::toString;

        System.out.println("Length of 'Hello': " + lengthTransformer.transform("Hello"));
        System.out.println("String of 42: " + stringTransformer.transform(42));

        Pair2<Integer, String> pair = new Pair2<>(1, "one");
        System.out.println("Pair: key=" + pair.getKey() + ", value=" + pair.getValue());
    }
}

interface Transformer<T, R> {
    R transform(T input);
}

class Pair2<K, V> {
    private K key;
    private V value;

    public Pair2(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() { return key; }
    public V getValue() { return value; }
}
