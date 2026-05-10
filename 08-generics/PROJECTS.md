# Java Generics Module - PROJECTS.md

---

# 🎯 Mini-Project: Type-Safe Data Structures

## Project Overview

**Duration**: 3-4 hours  
**Difficulty**: Intermediate  
**Concepts Used**: Generic Classes, Generic Methods, Type Parameters, Bounds, Wildcards

This project demonstrates Java generics through type-safe data structures.

---

## Project Structure

```
08-generics/src/main/java/com/learning/project/
├── Main.java
├── collection/
│   ├── GenericList.java
│   ├── GenericMap.java
│   └── GenericStack.java
├── utils/
│   └── ArrayUtils.java
└── ui/
    └── GenericMenu.java
```

---

## Step 1: Generic List Implementation

```java
// collection/GenericList.java
package com.learning.project.collection;

import java.util.*;

public class GenericList<E> {
    private Object[] elements;
    private int size;
    private static final int DEFAULT_CAPACITY = 10;
    
    public GenericList() {
        this.elements = new Object[DEFAULT_CAPACITY];
        this.size = 0;
    }
    
    public GenericList(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Capacity cannot be negative");
        }
        this.elements = new Object[initialCapacity];
        this.size = 0;
    }
    
    public void add(E element) {
        if (size == elements.length) {
            grow();
        }
        elements[size++] = element;
    }
    
    public void add(int index, E element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        if (size == elements.length) {
            grow();
        }
        System.arraycopy(elements, index, elements, index + 1, size - index);
        elements[index] = element;
        size++;
    }
    
    @SuppressWarnings("unchecked")
    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        return (E) elements[index];
    }
    
    @SuppressWarnings("unchecked")
    public E set(int index, E element) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        E old = (E) elements[index];
        elements[index] = element;
        return old;
    }
    
    public E remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        
        @SuppressWarnings("unchecked")
        E removed = (E) elements[index];
        
        System.arraycopy(elements, index + 1, elements, index, size - index - 1);
        elements[--size] = null;
        
        return removed;
    }
    
    public boolean remove(E element) {
        for (int i = 0; i < size; i++) {
            if (Objects.equals(elements[i], element)) {
                remove(i);
                return true;
            }
        }
        return false;
    }
    
    public int size() {
        return size;
    }
    
    public boolean isEmpty() {
        return size == 0;
    }
    
    public void clear() {
        Arrays.fill(elements, 0, size, null);
        size = 0;
    }
    
    public boolean contains(E element) {
        for (int i = 0; i < size; i++) {
            if (Objects.equals(elements[i], element)) {
                return true;
            }
        }
        return false;
    }
    
    public int indexOf(E element) {
        for (int i = 0; i < size; i++) {
            if (Objects.equals(elements[i], element)) {
                return i;
            }
        }
        return -1;
    }
    
    private void grow() {
        int newCapacity = elements.length + elements.length / 2;
        elements = Arrays.copyOf(elements, newCapacity);
    }
    
    @SuppressWarnings("unchecked")
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private int index = 0;
            
            @Override
            public boolean hasNext() {
                return index < size;
            }
            
            @Override
            public E next() {
                if (index >= size) {
                    throw new NoSuchElementException();
                }
                return (E) elements[index++];
            }
        };
    }
    
    @SuppressWarnings("unchecked")
    public void sort(Comparator<? super E> comparator) {
        Arrays.sort((E[]) elements, 0, size, comparator);
    }
    
    public Object[] toArray() {
        return Arrays.copyOf(elements, size);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            a = (T[]) Arrays.copyOf(elements, size, a.getClass());
        } else {
            System.arraycopy(elements, 0, a, 0, size);
            if (a.length > size) {
                a[size] = null;
            }
        }
        return a;
    }
    
    @Override
    public String toString() {
        if (size == 0) return "[]";
        
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            sb.append(elements[i]);
            if (i < size - 1) sb.append(", ");
        }
        return sb.append("]").toString();
    }
}
```

---

## Step 2: Generic Map Implementation

```java
// collection/GenericMap.java
package com.learning.project.collection;

import java.util.*;

public class GenericMap<K, V> {
    private static final int DEFAULT_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;
    
    private Node<K, V>[] buckets;
    private int size;
    private int threshold;
    
    private static class Node<K, V> {
        final int hash;
        final K key;
        V value;
        Node<K, V> next;
        
        Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
    
    @SuppressWarnings("unchecked")
    public GenericMap() {
        this.buckets = new Node[DEFAULT_CAPACITY];
        this.size = 0;
        this.threshold = (int) (DEFAULT_CAPACITY * LOAD_FACTOR);
    }
    
    public V put(K key, V value) {
        int hash = hash(key);
        int index = hash & (buckets.length - 1);
        
        Node<K, V> node = buckets[index];
        
        while (node != null) {
            if (node.hash == hash && Objects.equals(node.key, key)) {
                V old = node.value;
                node.value = value;
                return old;
            }
            node = node.next;
        }
        
        Node<K, V> newNode = new Node<>(hash, key, value, buckets[index]);
        buckets[index] = newNode;
        size++;
        
        if (size >= threshold) {
            resize();
        }
        
        return null;
    }
    
    public V get(K key) {
        int hash = hash(key);
        int index = hash & (buckets.length - 1);
        
        Node<K, V> node = buckets[index];
        
        while (node != null) {
            if (node.hash == hash && Objects.equals(node.key, key)) {
                return node.value;
            }
            node = node.next;
        }
        
        return null;
    }
    
    public V remove(K key) {
        int hash = hash(key);
        int index = hash & (buckets.length - 1);
        
        Node<K, V> node = buckets[index];
        Node<K, V> prev = null;
        
        while (node != null) {
            if (node.hash == hash && Objects.equals(node.key, key)) {
                if (prev == null) {
                    buckets[index] = node.next;
                } else {
                    prev.next = node.next;
                }
                size--;
                return node.value;
            }
            prev = node;
            node = node.next;
        }
        
        return null;
    }
    
    public boolean containsKey(K key) {
        int hash = hash(key);
        int index = hash & (buckets.length - 1);
        
        Node<K, V> node = buckets[index];
        
        while (node != null) {
            if (node.hash == hash && Objects.equals(node.key, key)) {
                return true;
            }
            node = node.next;
        }
        
        return false;
    }
    
    public boolean containsValue(V value) {
        for (Node<K, V> bucket : buckets) {
            Node<K, V> node = bucket;
            while (node != null) {
                if (Objects.equals(node.value, value)) {
                    return true;
                }
                node = node.next;
            }
        }
        return false;
    }
    
    public int size() {
        return size;
    }
    
    public boolean isEmpty() {
        return size == 0;
    }
    
    public void clear() {
        Arrays.fill(buckets, null);
        size = 0;
    }
    
    private void resize() {
        Node<K, V>[] oldBuckets = buckets;
        int oldCapacity = oldBuckets.length;
        
        @SuppressWarnings("unchecked")
        Node<K, V>[] newBuckets = new Node[oldCapacity * 2];
        
        for (int i = 0; i < oldCapacity; i++) {
            Node<K, V> node = oldBuckets[i];
            
            while (node != null) {
                Node<K, V> next = node.next;
                int newIndex = node.hash & (newBuckets.length - 1);
                node.next = newBuckets[newIndex];
                newBuckets[newIndex] = node;
                node = next;
            }
        }
        
        buckets = newBuckets;
        threshold = (int) (newBuckets.length * LOAD_FACTOR);
    }
    
    private int hash(Object key) {
        if (key == null) return 0;
        return key.hashCode();
    }
    
    public Set<K> keySet() {
        Set<K> keys = new HashSet<>();
        for (Node<K, V> bucket : buckets) {
            Node<K, V> node = bucket;
            while (node != null) {
                keys.add(node.key);
                node = node.next;
            }
        }
        return keys;
    }
    
    public Collection<V> values() {
        List<V> values = new ArrayList<>();
        for (Node<K, V> bucket : buckets) {
            Node<K, V> node = bucket;
            while (node != null) {
                values.add(node.value);
                node = node.next;
            }
        }
        return values;
    }
    
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> entries = new HashSet<>();
        for (Node<K, V> bucket : buckets) {
            Node<K, V> node = bucket;
            while (node != null) {
                entries.add(new MapEntry<>(node.key, node.value));
                node = node.next;
            }
        }
        return entries;
    }
    
    private static class MapEntry<K, V> implements Map.Entry<K, V> {
        private final K key;
        private V value;
        
        MapEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }
        
        @Override
        public K getKey() { return key; }
        
        @Override
        public V getValue() { return value; }
        
        @Override
        public V setValue(V value) {
            V old = this.value;
            this.value = value;
            return old;
        }
    }
}
```

---

## Step 3: Generic Stack

```java
// collection/GenericStack.java
package com.learning.project.collection;

import java.util.*;

public class GenericStack<E> {
    private final GenericList<E> list;
    
    public GenericStack() {
        this.list = new GenericList<>();
    }
    
    public void push(E element) {
        list.add(element);
    }
    
    public E pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return list.remove(list.size() - 1);
    }
    
    public E peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return list.get(list.size() - 1);
    }
    
    public boolean isEmpty() {
        return list.isEmpty();
    }
    
    public int size() {
        return list.size();
    }
    
    public void clear() {
        list.clear();
    }
    
    public int search(Object o) {
        for (int i = list.size() - 1; i >= 0; i--) {
            if (Objects.equals(list.get(i), o)) {
                return list.size() - i;
            }
        }
        return -1;
    }
    
    @Override
    public String toString() {
        return list.toString();
    }
}
```

---

## Step 4: Generic Array Utilities

```java
// utils/ArrayUtils.java
package com.learning.project.utils;

import java.util.*;

public class ArrayUtils {
    
    public static <T> void swap(T[] array, int i, int j) {
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
    
    public static <T extends Comparable<T>> void sort(T[] array) {
        Arrays.sort(array);
    }
    
    public static <T> void sort(T[] array, Comparator<? super T> comparator) {
        Arrays.sort(array, comparator);
    }
    
    public static <T> int binarySearch(T[] array, T key) {
        return Arrays.binarySearch(array, key);
    }
    
    public static <T> int binarySearch(T[] array, T key, Comparator<? super T> c) {
        return Arrays.binarySearch(array, key, c);
    }
    
    public static <T> T[] concatenate(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
    
    public static <T> T[] reverse(T[] array) {
        int left = 0, right = array.length - 1;
        while (left < right) {
            swap(array, left++, right--);
        }
        return array;
    }
    
    public static <T> List<T> asList(T... elements) {
        return Arrays.asList(elements);
    }
    
    public static <T> boolean contains(T[] array, T element) {
        for (T item : array) {
            if (Objects.equals(item, element)) {
                return true;
            }
        }
        return false;
    }
    
    public static <T> T[] removeDuplicates(T[] array) {
        Set<T> set = new LinkedHashSet<>(Arrays.asList(array));
        return set.toArray(array);
    }
    
    public static <T> T findMax(T[] array, Comparator<? super T> comparator) {
        if (array.length == 0) return null;
        
        T max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (comparator.compare(array[i], max) > 0) {
                max = array[i];
            }
        }
        return max;
    }
    
    public static <T> T findMin(T[] array, Comparator<? super T> comparator) {
        if (array.length == 0) return null;
        
        T min = array[0];
        for (int i = 1; i < array.length; i++) {
            if (comparator.compare(array[i], min) < 0) {
                min = array[i];
            }
        }
        return min;
    }
    
    public static <T> void shuffle(T[] array) {
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            swap(array, i, j);
        }
    }
    
    public static <T> void fill(T[] array, T value) {
        Arrays.fill(array, value);
    }
    
    public static <T> T[] copyOf(T[] original, int newLength) {
        return Arrays.copyOf(original, newLength);
    }
    
    public static <T> T[] copyOfRange(T[] original, int from, int to) {
        return Arrays.copyOfRange(original, from, to);
    }
}
```

---

## Step 5: Generic Menu

```java
// ui/GenericMenu.java
package com.learning.project.ui;

import com.learning.project.collection.*;
import com.learning.project.utils.*;
import java.util.*;

public class GenericMenu {
    private Scanner scanner;
    private boolean running;
    
    public GenericMenu() {
        this.scanner = new Scanner(System.in);
        this.running = true;
    }
    
    public void start() {
        System.out.println("\n🔧 TYPE-SAFE DATA STRUCTURES");
        System.out.println("===========================");
        
        while (running) {
            displayMenu();
            handleChoice(getChoice());
        }
    }
    
    private void displayMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1. Generic List Demo");
        System.out.println("2. Generic Map Demo");
        System.out.println("3. Generic Stack Demo");
        System.out.println("4. Generic Array Utils");
        System.out.println("5. Bounded Type Demo");
        System.out.println("6. Wildcard Demo");
        System.out.println("7. Exit");
        System.out.print("\nChoice: ");
    }
    
    private int getChoice() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    private void handleChoice(int choice) {
        switch (choice) {
            case 1 -> genericListDemo();
            case 2 -> genericMapDemo();
            case 3 -> genericStackDemo();
            case 4 -> genericArrayDemo();
            case 5 -> boundedTypeDemo();
            case 6 -> wildcardDemo();
            case 7 -> { System.out.println("Goodbye!"); running = false; }
            default -> System.out.println("Invalid choice!");
        }
    }
    
    private void genericListDemo() {
        System.out.println("\n=== Generic List Demo ===");
        
        GenericList<String> list = new GenericList<>();
        list.add("Apple");
        list.add("Banana");
        list.add("Cherry");
        
        System.out.println("List: " + list);
        System.out.println("Size: " + list.size());
        System.out.println("Get(1): " + list.get(1));
        
        list.add(1, "Blueberry");
        System.out.println("After insert: " + list);
        
        list.remove("Banana");
        System.out.println("After remove: " + list);
        
        System.out.println("Contains 'Apple': " + list.contains("Apple"));
        System.out.println("Index of 'Cherry': " + list.indexOf("Cherry"));
    }
    
    private void genericMapDemo() {
        System.out.println("\n=== Generic Map Demo ===");
        
        GenericMap<String, Integer> map = new GenericMap<>();
        map.put("One", 1);
        map.put("Two", 2);
        map.put("Three", 3);
        
        System.out.println("Map size: " + map.size());
        System.out.println("Get 'Two': " + map.get("Two"));
        System.out.println("Contains 'One': " + map.containsKey("One"));
        
        map.put("Two", 22);
        System.out.println("After update: " + map.get("Two"));
        
        map.remove("One");
        System.out.println("After remove: " + map.size());
        
        System.out.println("Keys: " + map.keySet());
        System.out.println("Values: " + map.values());
    }
    
    private void genericStackDemo() {
        System.out.println("\n=== Generic Stack Demo ===");
        
        GenericStack<Integer> stack = new GenericStack<>();
        stack.push(10);
        stack.push(20);
        stack.push(30);
        
        System.out.println("Stack: " + stack);
        System.out.println("Peek: " + stack.peek());
        System.out.println("Pop: " + stack.pop());
        System.out.println("Stack after pop: " + stack);
        
        System.out.println("Search 10: " + stack.search(10));
    }
    
    private void genericArrayDemo() {
        System.out.println("\n=== Generic Array Utils Demo ===");
        
        Integer[] nums = {5, 2, 8, 1, 9, 3};
        System.out.println("Original: " + Arrays.toString(nums));
        
        ArrayUtils.sort(nums);
        System.out.println("Sorted: " + Arrays.toString(nums));
        
        ArrayUtils.shuffle(nums);
        System.out.println("Shuffled: " + Arrays.toString(nums));
        
        Integer max = ArrayUtils.findMax(nums, Integer::compareTo);
        Integer min = ArrayUtils.findMin(nums, Integer::compareTo);
        System.out.println("Max: " + max + ", Min: " + min);
        
        String[] strings = {"Zebra", "Apple", "Mango"};
        ArrayUtils.sort(strings, Comparator.naturalOrder());
        System.out.println("Sorted strings: " + Arrays.toString(strings));
    }
    
    private void boundedTypeDemo() {
        System.out.println("\n=== Bounded Type Demo ===");
        
        NumberList<Integer> intList = new NumberList<>();
        intList.add(10);
        intList.add(20);
        intList.add(30);
        
        System.out.println("Integer list sum: " + intList.sum());
        
        NumberList<Double> doubleList = new NumberList<>();
        doubleList.add(1.5);
        doubleList.add(2.5);
        
        System.out.println("Double list sum: " + doubleList.sum());
        
        System.out.println("Max of integers: " + intList.max());
    }
    
    private void wildcardDemo() {
        System.out.println("\n=== Wildcard Demo ===");
        
        List<Integer> ints = Arrays.asList(1, 2, 3);
        List<Double> doubles = Arrays.asList(1.0, 2.0, 3.0);
        List<String> strings = Arrays.asList("a", "b", "c");
        
        printList(ints);
        printList(doubles);
        // printList(strings); // Would fail - not Number
        
        printNumbers(ints);
        printNumbers(doubles);
        // printNumbers(strings); // Would fail
    }
    
    public static void printList(List<?> list) {
        System.out.println("List contents: " + list);
    }
    
    public static void printNumbers(List<? extends Number> list) {
        System.out.println("Numbers: " + list);
        double sum = 0;
        for (Number n : list) {
            sum += n.doubleValue();
        }
        System.out.println("Sum: " + sum);
    }
    
    public static void main(String[] args) {
        new GenericMenu().start();
    }
}

// Bounded type example
class NumberList<T extends Number> {
    private GenericList<T> list = new GenericList<>();
    
    public void add(T element) {
        list.add(element);
    }
    
    public double sum() {
        double sum = 0;
        for (int i = 0; i < list.size(); i++) {
            sum += list.get(i).doubleValue();
        }
        return sum;
    }
    
    public T max() {
        if (list.size() == 0) return null;
        
        T max = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i).doubleValue() > max.doubleValue()) {
                max = list.get(i);
            }
        }
        return max;
    }
}
```

---

## Running the Mini-Project

```bash
cd 08-generics
javac -d out -sourcepath src src/com/learning/project/**/*.java src/com/learning/project/*.java
java -cp out com.learning.project.ui.GenericMenu
```

---

## Generics Concepts Demonstrated

| Concept | Implementation |
|---------|----------------|
| **Generic Classes** | GenericList, GenericMap, GenericStack |
| **Generic Methods** | ArrayUtils methods |
| **Type Parameters** | E, K, V for element, key, value |
| **Bounded Types** | T extends Number |
| **Wildcards** | <?>, <? extends Number> |
| **Type Erasure** | Runtime type handling |

---

# 🚀 Real-World Project: Type-Safe Data Structures Library

## Project Overview

**Duration**: 15-20 hours  
**Difficulty**: Advanced  
**Concepts Used**: Generic Classes, Generic Methods, Type Bounds, Wildcards, Constraints, Multiple Type Parameters

This project implements a comprehensive type-safe data structures library with advanced generics features.

---

## Project Structure

```
08-generics/src/main/java/com/learning/project/
├── Main.java
├── collection/
│   ├── GenericList.java
│   ├── GenericMap.java
│   ├── GenericStack.java
│   ├── GenericQueue.java
│   └── GenericTree.java
├── utils/
│   ├── ArrayUtils.java
│   ├── CollectionUtils.java
│   └── ComparatorUtils.java
├── functional/
│   ├── GenericFunction.java
│   ├── GenericPredicate.java
│   └── GenericTransformer.java
├── pair/
│   ├── Pair.java
│   └── Triple.java
├── cache/
│   └── GenericCache.java
└── ui/
    └── LibraryMenu.java
```

---

## Step 1: Generic Pair and Triple

```java
// pair/Pair.java
package com.learning.project.pair;

import java.util.*;

public class Pair<K, V> {
    private final K key;
    private final V value;
    
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }
    
    public static <K, V> Pair<K, V> of(K key, V value) {
        return new Pair<>(key, value);
    }
    
    public K getKey() { return key; }
    public V getValue() { return value; }
    
    public <K2, V2> Pair<K2, V2> transform(java.util.function.Function<K, K2> keyMapper,
                                          java.util.function.Function<V, V2> valueMapper) {
        return Pair.of(keyMapper.apply(key), valueMapper.apply(value));
    }
    
    public <V2> Pair<K, V2> mapValue(java.util.function.Function<V, V2> mapper) {
        return Pair.of(key, mapper.apply(value));
    }
    
    public <K2> Pair<K2, V> mapKey(java.util.function.Function<K, K2> mapper) {
        return Pair.of(mapper.apply(key), value);
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
    
    @Override
    public String toString() {
        return String.format("(%s, %s)", key, value);
    }
}

// pair/Triple.java
package com.learning.project.pair;

import java.util.*;

public class Triple<A, B, C> {
    private final A first;
    private final B second;
    private final C third;
    
    public Triple(A first, B second, C third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }
    
    public static <A, B, C> Triple<A, B, C> of(A first, B second, C third) {
        return new Triple<>(first, second, third);
    }
    
    public A getFirst() { return first; }
    public B getSecond() { return second; }
    public C getThird() { return third; }
    
    public <A2, B2, C2> Triple<A2, B2, C2> transform(
            java.util.function.Function<A, A2> f1,
            java.util.function.Function<B, B2> f2,
            java.util.function.Function<C, C2> f3) {
        return Triple.of(f1.apply(first), f2.apply(second), f3.apply(third));
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Triple<?, ?, ?> triple = (Triple<?, ?, ?>) o;
        return Objects.equals(first, triple.first) && 
               Objects.equals(second, triple.second) && 
               Objects.equals(third, triple.third);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(first, second, third);
    }
    
    @Override
    public String toString() {
        return String.format("(%s, %s, %s)", first, second, third);
    }
}
```

---

## Step 2: Generic Queue Implementation

```java
// collection/GenericQueue.java
package com.learning.project.collection;

import java.util.*;

public class GenericQueue<E> {
    private final GenericList<E> list;
    private final int maxSize;
    
    public GenericQueue() {
        this.list = new GenericList<>();
        this.maxSize = Integer.MAX_VALUE;
    }
    
    public GenericQueue(int maxSize) {
        this.list = new GenericList<>();
        this.maxSize = maxSize;
    }
    
    public boolean offer(E element) {
        if (list.size() >= maxSize) {
            return false;
        }
        list.add(element);
        return true;
    }
    
    public E poll() {
        if (list.isEmpty()) {
            return null;
        }
        return list.remove(0);
    }
    
    public E peek() {
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }
    
    public E element() {
        if (list.isEmpty()) {
            throw new NoSuchElementException();
        }
        return list.get(0);
    }
    
    public int size() {
        return list.size();
    }
    
    public boolean isEmpty() {
        return list.isEmpty();
    }
    
    public boolean isFull() {
        return list.size() >= maxSize;
    }
    
    public void clear() {
        list.clear();
    }
    
    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }
    
    @Override
    public String toString() {
        return list.toString();
    }
}
```

---

## Step 3: Generic Cache

```java
// cache/GenericCache.java
package com.learning.project.cache;

import java.util.*;
import java.util.concurrent.*;
import java.time.*;

public class GenericCache<K, V> {
    private final Map<K, CacheEntry<V>> cache;
    private final long ttlMillis;
    private final int maxSize;
    private final ExecutorService cleanupExecutor;
    
    private static class CacheEntry<V> {
        final V value;
        final long createdAt;
        
        CacheEntry(V value) {
            this.value = value;
            this.createdAt = System.currentTimeMillis();
        }
        
        boolean isExpired(long ttl) {
            return System.currentTimeMillis() - createdAt > ttl;
        }
    }
    
    public GenericCache(long ttlSeconds, int maxSize) {
        this.cache = new ConcurrentHashMap<>();
        this.ttlMillis = ttlSeconds * 1000;
        this.maxSize = maxSize;
        this.cleanupExecutor = Executors.newSingleThreadExecutor();
        startCleanup();
    }
    
    private void startCleanup() {
        cleanupExecutor.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(ttlMillis);
                    cleanup();
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
    }
    
    private void cleanup() {
        cache.entrySet().removeIf(e -> e.getValue().isExpired(ttlMillis));
        
        if (cache.size() > maxSize) {
            List<K> keys = new ArrayList<>(cache.keySet());
            keys.sort((k1, k2) -> {
                long t1 = cache.get(k1).createdAt;
                long t2 = cache.get(k2).createdAt;
                return Long.compare(t1, t2);
            });
            
            int toRemove = cache.size() - maxSize;
            for (int i = 0; i < toRemove; i++) {
                cache.remove(keys.get(i));
            }
        }
    }
    
    public void put(K key, V value) {
        if (cache.size() >= maxSize) {
            cleanup();
        }
        cache.put(key, new CacheEntry<>(value));
    }
    
    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry == null) return null;
        
        if (entry.isExpired(ttlMillis)) {
            cache.remove(key);
            return null;
        }
        
        return entry.value;
    }
    
    public V getOrDefault(K key, V defaultValue) {
        V value = get(key);
        return value != null ? value : defaultValue;
    }
    
    public V computeIfAbsent(K key, java.util.function.Supplier<V> supplier) {
        V value = get(key);
        if (value == null) {
            value = supplier.get();
            put(key, value);
        }
        return value;
    }
    
    public boolean containsKey(K key) {
        return get(key) != null;
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
    
    public Set<K> keySet() {
        cleanup();
        return new HashSet<>(cache.keySet());
    }
    
    public Collection<V> values() {
        cleanup();
        List<V> values = new ArrayList<>();
        for (CacheEntry<V> entry : cache.values()) {
            if (!entry.isExpired(ttlMillis)) {
                values.add(entry.value);
            }
        }
        return values;
    }
    
    public void shutdown() {
        cleanupExecutor.shutdown();
    }
}
```

---

## Step 4: Functional Interfaces

```java
// functional/GenericFunction.java
package com.learning.project.functional;

import java.util.function.Function;

@FunctionalInterface
public interface GenericFunction<T, R> {
    R apply(T input);
    
    default <V> GenericFunction<T, V> andThen(Function<? super R, ? extends V> after) {
        return t -> after.apply(apply(t));
    }
    
    default <V> GenericFunction<V, R> compose(Function<? super V, ? extends T> before) {
        return v -> apply(before.apply(v));
    }
    
    static <T> GenericFunction<T, T> identity() {
        return t -> t;
    }
}

// functional/GenericPredicate.java
package com.learning.project.functional;

import java.util.function.Predicate;

@FunctionalInterface
public interface GenericPredicate<T> {
    boolean test(T value);
    
    default GenericPredicate<T> and(GenericPredicate<? super T> other) {
        return t -> test(t) && other.test(t);
    }
    
    default GenericPredicate<T> or(GenericPredicate<? super T> other) {
        return t -> test(t) || other.test(t);
    }
    
    default GenericPredicate<T> negate() {
        return t -> !test(t);
    }
    
    static <T> GenericPredicate<T> isEqual(Object target) {
        return t -> Objects.equals(t, target);
    }
    
    static <T> GenericPredicate<T> alwaysTrue() {
        return t -> true;
    }
    
    static <T> GenericPredicate<T> alwaysFalse() {
        return t -> false;
    }
}

// functional/GenericTransformer.java
package com.learning.project.functional;

import java.util.*;
import java.util.stream.*;

public class GenericTransformer<T, R> {
    private final List<Function<T, R>> transformations;
    
    public GenericTransformer() {
        this.transformations = new ArrayList<>();
    }
    
    public GenericTransformer<T, R> addTransformation(Function<T, R> function) {
        transformations.add(function);
        return this;
    }
    
    public R transform(T input) {
        R result = null;
        for (Function<T, R> transformation : transformations) {
            result = transformation.apply(input);
        }
        return result;
    }
    
    public List<R> transformAll(Collection<T> inputs) {
        return inputs.stream()
            .map(this::transform)
            .collect(Collectors.toList());
    }
    
    public static <T, R> List<R> transformAll(
            Collection<T> inputs, 
            Function<T, R> function) {
        
        return inputs.stream()
            .map(function)
            .collect(Collectors.toList());
    }
}
```

---

## Step 5: Collection Utilities

```java
// utils/CollectionUtils.java
package com.learning.project.utils;

import java.util.*;
import java.util.stream.*;

public class CollectionUtils {
    
    public static <T> List<T> filter(Collection<T> collection, java.util.function.Predicate<T> predicate) {
        return collection.stream()
            .filter(predicate)
            .collect(Collectors.toList());
    }
    
    public static <T, R> List<R> map(Collection<T> collection, Function<T, R> mapper) {
        return collection.stream()
            .map(mapper)
            .collect(Collectors.toList());
    }
    
    public static <T> List<T> flatMap(Collection<T> collection, Function<T, Collection<R>> mapper) {
        return collection.stream()
            .flatMap(t -> mapper.apply(t).stream())
            .collect(Collectors.toList());
    }
    
    public static <T> Optional<T> findFirst(Collection<T> collection, java.util.function.Predicate<T> predicate) {
        return collection.stream().filter(predicate).findFirst();
    }
    
    public static <T> boolean anyMatch(Collection<T> collection, java.util.function.Predicate<T> predicate) {
        return collection.stream().anyMatch(predicate);
    }
    
    public static <T> boolean allMatch(Collection<T> collection, java.util.function.Predicate<T> predicate) {
        return collection.stream().allMatch(predicate);
    }
    
    public static <T> List<T> union(Collection<T> a, Collection<T> b) {
        Set<T> set = new HashSet<>(a);
        set.addAll(b);
        return new ArrayList<>(set);
    }
    
    public static <T> List<T> intersection(Collection<T> a, Collection<T> b) {
        Set<T> setA = new HashSet<>(a);
        setA.retainAll(b);
        return new ArrayList<>(setA);
    }
    
    public static <T> List<T> difference(Collection<T> a, Collection<T> b) {
        Set<T> result = new HashSet<>(a);
        result.removeAll(b);
        return new ArrayList<>(result);
    }
    
    public static <T> List<T> reverse(List<T> list) {
        List<T> result = new ArrayList<>(list);
        Collections.reverse(result);
        return result;
    }
    
    public static <T> List<T> shuffle(List<T> list, Random random) {
        List<T> result = new ArrayList<>(list);
        Collections.shuffle(result, random);
        return result;
    }
    
    public static <T extends Comparable<T>> List<T> sort(List<T> list) {
        List<T> result = new ArrayList<>(list);
        Collections.sort(result);
        return result;
    }
    
    public static <T> List<T> sort(List<T> list, Comparator<? super T> comparator) {
        List<T> result = new ArrayList<>(list);
        result.sort(comparator);
        return result;
    }
    
    public static <T> List<List<T>> partition(List<T> list, int size) {
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            partitions.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return partitions;
    }
    
    public static <T> Map<T, Long> frequency(Collection<T> collection) {
        return collection.stream()
            .collect(Collectors.groupingBy(t -> t, Collectors.counting()));
    }
    
    public static <T> T randomElement(Collection<T> collection, Random random) {
        if (collection.isEmpty()) return null;
        int index = random.nextInt(collection.size());
        return new ArrayList<>(collection).get(index);
    }
}
```

---

## Step 6: Library Menu Interface

```java
// ui/LibraryMenu.java
package com.learning.project.ui;

import com.learning.project.collection.*;
import com.learning.project.pair.*;
import com.learning.project.cache.*;
import com.learning.project.utils.*;
import java.util.*;

public class LibraryMenu {
    private Scanner scanner;
    private boolean running;
    
    public LibraryMenu() {
        this.scanner = new Scanner(System.in);
        this.running = true;
    }
    
    public void start() {
        System.out.println("\n📚 TYPE-SAFE DATA STRUCTURES LIBRARY");
        System.out.println("=====================================");
        
        while (running) {
            displayMenu();
            handleChoice(getChoice());
        }
    }
    
    private void displayMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1. Generic List");
        System.out.println("2. Generic Map");
        System.out.println("3. Generic Queue");
        System.out.println("4. Generic Stack");
        System.out.println("5. Pair and Triple");
        System.out.println("6. Generic Cache");
        System.out.println("7. Collection Utils");
        System.out.println("8. Type Bounds Demo");
        System.out.println("9. Exit");
        System.out.print("\nChoice: ");
    }
    
    private int getChoice() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    private void handleChoice(int choice) {
        switch (choice) {
            case 1 -> demoList();
            case 2 -> demoMap();
            case 3 -> demoQueue();
            case 4 -> demoStack();
            case 5 -> demoPair();
            case 6 -> demoCache();
            case 7 -> demoUtils();
            case 8 -> demoBounds();
            case 9 -> { System.out.println("Goodbye!"); running = false; }
            default -> System.out.println("Invalid choice!");
        }
    }
    
    private void demoList() {
        System.out.println("\n=== Generic List Demo ===");
        GenericList<String> list = new GenericList<>();
        list.add("A"); list.add("B"); list.add("C");
        System.out.println("List: " + list);
        list.add(1, "X");
        System.out.println("After insert: " + list);
        System.out.println("Get 2: " + list.get(2));
    }
    
    private void demoMap() {
        System.out.println("\n=== Generic Map Demo ===");
        GenericMap<String, Integer> map = new GenericMap<>();
        map.put("A", 1); map.put("B", 2); map.put("C", 3);
        System.out.println("Map: " + map.size() + " entries");
        System.out.println("Get B: " + map.get("B"));
    }
    
    private void demoQueue() {
        System.out.println("\n=== Generic Queue Demo ===");
        GenericQueue<String> queue = new GenericQueue<>(5);
        queue.offer("One"); queue.offer("Two"); queue.offer("Three");
        System.out.println("Queue: " + queue);
        System.out.println("Poll: " + queue.poll());
        System.out.println("After poll: " + queue);
    }
    
    private void demoStack() {
        System.out.println("\n=== Generic Stack Demo ===");
        GenericStack<Integer> stack = new GenericStack<>();
        stack.push(1); stack.push(2); stack.push(3);
        System.out.println("Stack: " + stack);
        System.out.println("Pop: " + stack.pop());
        System.out.println("After pop: " + stack);
    }
    
    private void demoPair() {
        System.out.println("\n=== Pair and Triple Demo ===");
        Pair<String, Integer> pair = Pair.of("Age", 25);
        System.out.println("Pair: " + pair);
        System.out.println("Key: " + pair.getKey() + ", Value: " + pair.getValue());
        
        Triple<String, Integer, String> triple = Triple.of("Name", 30, "City");
        System.out.println("Triple: " + triple);
    }
    
    private void demoCache() {
        System.out.println("\n=== Generic Cache Demo ===");
        GenericCache<String, String> cache = new GenericCache<>(60, 100);
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        
        System.out.println("Get key1: " + cache.get("key1"));
        System.out.println("Get missing: " + cache.getOrDefault("missing", "default"));
        System.out.println("Size: " + cache.size());
    }
    
    private void demoUtils() {
        System.out.println("\n=== Collection Utils Demo ===");
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        List<Integer> filtered = CollectionUtils.filter(numbers, n -> n > 2);
        System.out.println("Filtered > 2: " + filtered);
        
        List<Integer> doubled = CollectionUtils.map(numbers, n -> n * 2);
        System.out.println("Doubled: " + doubled);
        
        var freq = CollectionUtils.frequency(Arrays.asList("a", "b", "a", "c", "a"));
        System.out.println("Frequency: " + freq);
    }
    
    private void demoBounds() {
        System.out.println("\n=== Type Bounds Demo ===");
        
        NumberList<Integer> intList = new NumberList<>();
        intList.add(10); intList.add(20); intList.add(30);
        System.out.println("Sum: " + intList.sum());
        
        List<Integer> ints = Arrays.asList(1, 2, 3);
        List<Double> doubles = Arrays.asList(1.0, 2.0, 3.0);
        
        printSum(ints);
        printSum(doubles);
    }
    
    public static void printSum(List<? extends Number> numbers) {
        double sum = 0;
        for (Number n : numbers) {
            sum += n.doubleValue();
        }
        System.out.println("Sum: " + sum);
    }
    
    public static void main(String[] args) {
        new LibraryMenu().start();
    }
}
```

---

## Running the Real-World Project

```bash
cd 08-generics
javac -d out -sourcepath src src/com/learning/project/**/*.java src/com/learning/project/*.java
java -cp out com.learning.project.ui.LibraryMenu
```

---

## Advanced Generics Concepts

| Concept | Implementation |
|---------|----------------|
| **Multiple Type Parameters** | GenericMap<K, V>, Pair<K, V> |
| **Bounded Wildcards** | <? extends Number> |
| **Unbounded Wildcards** | <?> |
| **Generic Methods** | CollectionUtils methods |
| **Type Constraints** | T extends Number |
| **Generic Cache** | TTL-based cache |
| **Functional Interfaces** | GenericFunction, GenericPredicate |

---

## Extensions

1. Add Binary Search Tree implementation
2. Implement Graph data structure
3. Add generic algorithms (sort, search)
4. Implement thread-safe variants
5. Add serialization support

---

## Summary

This module covered comprehensive generics concepts:
- Type-safe data structures
- Generic methods and classes
- Type bounds and wildcards
- Functional interfaces with generics
- Advanced type-safe utilities

After completing this module, you have a complete understanding of Java generics for building type-safe applications.