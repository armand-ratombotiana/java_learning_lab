package com.learning.java.generics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Elite Generics Training - Comprehensive implementation covering all generic concepts
 * from basic to advanced interview-level problems.
 * 
 * This class demonstrates:
 * - Generic classes with single and multiple type parameters
 * - Bounded type parameters
 * - Generic methods
 * - Wildcards (upper and lower bounded)
 * - Type erasure implications
 * - Real-world generic patterns
 */
public class EliteGenericsTraining {

    // ============================================================================
    // SECTION 1: BASIC GENERIC CLASSES
    // ============================================================================

    /**
     * Generic Box class - holds any type of value
     * Demonstrates: Single type parameter
     */
    public static class Box<T> {
        private T value;

        public void set(T value) {
            this.value = value;
        }

        public T get() {
            return value;
        }

        public boolean isEmpty() {
            return value == null;
        }
    }

    /**
     * Generic Pair class - holds two related values
     * Demonstrates: Multiple type parameters
     */
    public static class Pair<K, V> {
        private final K key;
        private final V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "(" + key + ", " + value + ")";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Pair<?, ?> pair = (Pair<?, ?>) o;
            return Objects.equals(key, pair.key) && Objects.equals(value, pair.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, value);
        }
    }

    /**
     * Generic Triple class - holds three related values
     * Demonstrates: Three type parameters
     */
    public static class Triple<A, B, C> {
        private final A first;
        private final B second;
        private final C third;

        public Triple(A first, B second, C third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }

        public A getFirst() { return first; }
        public B getSecond() { return second; }
        public C getThird() { return third; }

        @Override
        public String toString() {
            return "(" + first + ", " + second + ", " + third + ")";
        }
    }

    // ============================================================================
    // SECTION 2: BOUNDED TYPE PARAMETERS
    // ============================================================================

    /**
     * NumberBox - only accepts Number types
     * Demonstrates: Upper bounded type parameter
     */
    public static class NumberBox<T extends Number> {
        private T value;

        public void setValue(T value) {
            this.value = value;
        }

        public T getValue() {
            return value;
        }

        public double asDouble() {
            return value.doubleValue();
        }

        public int asInt() {
            return value.intValue();
        }
    }

    /**
     * ComparableBox - only accepts Comparable types
     * Demonstrates: Bounded by Comparable interface
     */
    public static class ComparableBox<T extends Comparable<T>> {
        private T value;

        public void setValue(T value) {
            this.value = value;
        }

        public T getValue() {
            return value;
        }

        public int compareTo(ComparableBox<T> other) {
            return this.value.compareTo(other.value);
        }
    }

    /**
     * Advanced bounded type with multiple bounds
     * Demonstrates: Multiple bounds (Number AND Comparable)
     */
    public static class AdvancedNumberBox<T extends Number & Comparable<T>> {
        private T value;

        public AdvancedNumberBox(T value) {
            this.value = value;
        }

        public T getValue() {
            return value;
        }

        public boolean isGreaterThan(AdvancedNumberBox<T> other) {
            return this.value.compareTo(other.value) > 0;
        }

        public double sum(AdvancedNumberBox<T>... others) {
            double sum = this.value.doubleValue();
            for (AdvancedNumberBox<T> other : others) {
                sum += other.value.doubleValue();
            }
            return sum;
        }
    }

    // ============================================================================
    // SECTION 3: GENERIC METHODS
    // ============================================================================

    /**
     * Utility class with generic methods
     * Demonstrates: Generic method declarations
     */
    public static class GenericUtils {

        /**
         * Print array of any type
         */
        public static <T> void printArray(T[] array) {
            for (T element : array) {
                System.out.print(element + " ");
            }
            System.out.println();
        }

        /**
         * Get first element of array
         */
        public static <T> T getFirst(T[] array) {
            return array != null && array.length > 0 ? array[0] : null;
        }

        /**
         * Get last element of array
         */
        public static <T> T getLast(T[] array) {
            return array != null && array.length > 0 ? array[array.length - 1] : null;
        }

        /**
         * Swap two elements in array
         */
        public static <T> void swap(T[] array, int i, int j) {
            if (array != null && i >= 0 && j >= 0 && i < array.length && j < array.length) {
                T temp = array[i];
                array[i] = array[j];
                array[j] = temp;
            }
        }

        /**
         * Reverse an array
         */
        public static <T> void reverse(T[] array) {
            if (array == null) return;
            int left = 0, right = array.length - 1;
            while (left < right) {
                swap(array, left++, right--);
            }
        }

        /**
         * Find maximum element (requires Comparable)
         */
        public static <T extends Comparable<T>> T findMax(T[] array) {
            if (array == null || array.length == 0) return null;
            T max = array[0];
            for (int i = 1; i < array.length; i++) {
                if (array[i].compareTo(max) > 0) {
                    max = array[i];
                }
            }
            return max;
        }

        /**
         * Find minimum element (requires Comparable)
         */
        public static <T extends Comparable<T>> T findMin(T[] array) {
            if (array == null || array.length == 0) return null;
            T min = array[0];
            for (int i = 1; i < array.length; i++) {
                if (array[i].compareTo(min) < 0) {
                    min = array[i];
                }
            }
            return min;
        }

        /**
         * Sum all numbers in array
         */
        public static <T extends Number> double sumNumbers(T[] array) {
            double sum = 0;
            for (T element : array) {
                sum += element.doubleValue();
            }
            return sum;
        }

        /**
         * Count occurrences of element in array
         */
        public static <T> int countOccurrences(T[] array, T element) {
            int count = 0;
            for (T item : array) {
                if (Objects.equals(item, element)) {
                    count++;
                }
            }
            return count;
        }

        /**
         * Check if array contains element
         */
        public static <T> boolean contains(T[] array, T element) {
            for (T item : array) {
                if (Objects.equals(item, element)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Convert array to List
         */
        public static <T> List<T> toList(T[] array) {
            List<T> list = new ArrayList<>();
            for (T item : array) {
                list.add(item);
            }
            return list;
        }

        /**
         * Create a Pair
         */
        public static <K, V> Pair<K, V> createPair(K key, V value) {
            return new Pair<>(key, value);
        }
    }

    // ============================================================================
    // SECTION 4: WILDCARDS AND VARIANCE
    // ============================================================================

    /**
     * Wildcard utilities demonstrating PECS principle
     * Producer Extends, Consumer Super
     */
    public static class WildcardUtils {

        /**
         * Unbounded wildcard - works with any List
         */
        public static void printList(List<?> list) {
            for (Object element : list) {
                System.out.print(element + " ");
            }
            System.out.println();
        }

        /**
         * Upper bounded wildcard - read from producer
         * Can read as Number, cannot write
         */
        public static double sumOfNumbers(List<? extends Number> list) {
            double sum = 0;
            for (Number num : list) {
                sum += num.doubleValue();
            }
            return sum;
        }

        /**
         * Lower bounded wildcard - write to consumer
         * Can write Integers, can only read as Object
         */
        public static void addNumbers(List<? super Integer> list) {
            list.add(1);
            list.add(2);
            list.add(3);
        }

        /**
         * Copy from source to destination using PECS
         */
        public static <T> void copy(List<? extends T> source, List<? super T> dest) {
            for (T element : source) {
                dest.add(element);
            }
        }

        /**
         * Get size of any list
         */
        public static int getSize(List<?> list) {
            return list.size();
        }

        /**
         * Find maximum in comparable list
         */
        public static <T extends Comparable<? super T>> T max(List<T> list) {
            T max = list.get(0);
            for (T element : list) {
                if (element.compareTo(max) > 0) {
                    max = element;
                }
            }
            return max;
        }
    }

    // ============================================================================
    // SECTION 5: GENERIC INTERFACES AND IMPLEMENTATIONS
    // ============================================================================

    /**
     * Generic container interface
     */
    public interface Container<T> {
        void add(T item);
        T get(int index);
        int size();
        boolean isEmpty();
        void clear();
    }

    /**
     * Generic stack implementation
     */
    public static class Stack<T> implements Container<T> {
        private final List<T> items = new ArrayList<>();

        @Override
        public void add(T item) {
            items.add(item);
        }

        @Override
        public T get(int index) {
            return items.get(index);
        }

        @Override
        public int size() {
            return items.size();
        }

        @Override
        public boolean isEmpty() {
            return items.isEmpty();
        }

        @Override
        public void clear() {
            items.clear();
        }

        public T push(T item) {
            items.add(item);
            return item;
        }

        public T pop() {
            if (isEmpty()) return null;
            return items.remove(items.size() - 1);
        }

        public T peek() {
            if (isEmpty()) return null;
            return items.get(items.size() - 1);
        }
    }

    /**
     * Generic queue implementation
     */
    public static class Queue<T> implements Container<T> {
        private final List<T> items = new ArrayList<>();

        @Override
        public void add(T item) {
            items.add(item);
        }

        @Override
        public T get(int index) {
            return items.get(index);
        }

        @Override
        public int size() {
            return items.size();
        }

        @Override
        public boolean isEmpty() {
            return items.isEmpty();
        }

        @Override
        public void clear() {
            items.clear();
        }

        public T enqueue(T item) {
            items.add(item);
            return item;
        }

        public T dequeue() {
            if (isEmpty()) return null;
            return items.remove(0);
        }

        public T peek() {
            if (isEmpty()) return null;
            return items.get(0);
        }
    }

    // ============================================================================
    // SECTION 6: ADVANCED GENERIC PATTERNS
    // ============================================================================

    /**
     * Generic cache with compute-if-absent
     */
    public static class Cache<K, V> {
        private final Map<K, V> cache = new HashMap<>();

        public void put(K key, V value) {
            cache.put(key, value);
        }

        public V get(K key) {
            return cache.get(key);
        }

        public V getOrCompute(K key, java.util.function.Function<K, V> computer) {
            return cache.computeIfAbsent(key, computer);
        }

        public boolean contains(K key) {
            return cache.containsKey(key);
        }

        public void remove(K key) {
            cache.remove(key);
        }

        public void clear() {
            cache.clear();
        }

        public int size() {
            return cache.size();
        }
    }

    /**
     * Generic builder pattern with self-referential type
     */
    public static class GenericBuilder<T> {
        private T object;

        public GenericBuilder(T object) {
            this.object = object;
        }

        public <R> GenericBuilder<R> transform(java.util.function.Function<T, R> transformer) {
            return new GenericBuilder<>(transformer.apply(object));
        }

        public T build() {
            return object;
        }
    }

    /**
     * Generic validator with functional predicates
     */
    public static class Validator<T> {
        private final List<java.util.function.Predicate<T>> rules = new ArrayList<>();

        public Validator<T> addRule(java.util.function.Predicate<T> rule) {
            rules.add(rule);
            return this;
        }

        public boolean validate(T value) {
            return rules.stream().allMatch(rule -> rule.test(value));
        }

        public List<String> getErrors(T value, List<String> errorMessages) {
            List<String> errors = new ArrayList<>();
            for (int i = 0; i < rules.size(); i++) {
                if (!rules.get(i).test(value)) {
                    errors.add(i < errorMessages.size() ? errorMessages.get(i) : "Rule " + i + " failed");
                }
            }
            return errors;
        }
    }

    // ============================================================================
    // SECTION 7: MAIN METHOD - DEMONSTRATION
    // ============================================================================

    public static void main(String[] args) {
        System.out.println("=== Elite Generics Training ===\n");

        // 1. Basic Generic Classes
        System.out.println("1. Basic Generic Classes:");
        Box<String> stringBox = new Box<>();
        stringBox.set("Hello Generics!");
        System.out.println("   String Box: " + stringBox.get());

        Box<Integer> intBox = new Box<>();
        intBox.set(42);
        System.out.println("   Integer Box: " + intBox.get());

        // 2. Multiple Type Parameters
        System.out.println("\n2. Multiple Type Parameters:");
        Pair<String, Integer> pair = new Pair<>("age", 30);
        System.out.println("   Pair: " + pair);

        Triple<String, Integer, Double> triple = new Triple<>("John", 25, 5.9);
        System.out.println("   Triple: " + triple);

        // 3. Bounded Type Parameters
        System.out.println("\n3. Bounded Type Parameters:");
        NumberBox<Integer> numberBox = new NumberBox<>();
        numberBox.setValue(100);
        System.out.println("   NumberBox value: " + numberBox.getValue());
        System.out.println("   As double: " + numberBox.asDouble());

        // 4. Generic Methods
        System.out.println("\n4. Generic Methods:");
        Integer[] numbers = {1, 2, 3, 4, 5};
        System.out.print("   Array: ");
        GenericUtils.printArray(numbers);
        System.out.println("   Max: " + GenericUtils.findMax(numbers));
        System.out.println("   Min: " + GenericUtils.findMin(numbers));
        System.out.println("   Sum: " + GenericUtils.sumNumbers(numbers));

        String[] strings = {"apple", "banana", "cherry"};
        System.out.println("   Max string: " + GenericUtils.findMax(strings));

        // 5. Wildcards
        System.out.println("\n5. Wildcards (PECS):");
        List<Integer> intList = List.of(1, 2, 3, 4, 5);
        System.out.println("   Sum of integers: " + WildcardUtils.sumOfNumbers(intList));

        List<Double> doubleList = List.of(1.5, 2.5, 3.5);
        System.out.println("   Sum of doubles: " + WildcardUtils.sumOfNumbers(doubleList));

        List<Number> numberList = new ArrayList<>();
        WildcardUtils.addNumbers(numberList);
        System.out.println("   Added numbers: " + numberList);

        // 6. Generic Stack
        System.out.println("\n6. Generic Stack:");
        Stack<String> stack = new Stack<>();
        stack.push("First");
        stack.push("Second");
        stack.push("Third");
        System.out.println("   Pop: " + stack.pop());
        System.out.println("   Peek: " + stack.peek());
        System.out.println("   Size: " + stack.size());

        // 7. Generic Cache
        System.out.println("\n7. Generic Cache:");
        Cache<String, Integer> cache = new Cache<>();
        cache.put("one", 1);
        cache.put("two", 2);
        System.out.println("   Get 'one': " + cache.get("one"));
        System.out.println("   Get or compute: " + cache.getOrCompute("three", k -> 3));

        // 8. Generic Builder
        System.out.println("\n8. Generic Builder Pattern:");
        String result = new GenericBuilder<>("hello")
                .transform(String::toUpperCase)
                .transform(s -> s + " WORLD!")
                .build();
        System.out.println("   Transformed: " + result);

        // 9. Generic Validator
        System.out.println("\n9. Generic Validator:");
        Validator<Integer> intValidator = new Validator<Integer>()
                .addRule(n -> n != null)
                .addRule(n -> n > 0)
                .addRule(n -> n < 100);

        System.out.println("   Validate 50: " + intValidator.validate(50));
        System.out.println("   Validate 150: " + intValidator.validate(150));
        System.out.println("   Validate -5: " + intValidator.validate(-5));

        System.out.println("\n=== All Demonstrations Complete ===");
    }
}
