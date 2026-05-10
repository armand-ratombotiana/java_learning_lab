# Java Collections Framework - Implementation Guide

## Module Overview

This module covers the Java Collections Framework including List, Set, Map implementations, iterators, and performance characteristics. Each implementation demonstrates from-scratch approaches and production patterns.

---

## Part 1: From-Scratch Java Implementation

### 1.1 Custom List Implementation

```java
package com.learning.collections.implementation;

import java.util.*;

public class CustomArrayList<E> implements List<E> {
    
    private static final int DEFAULT_CAPACITY = 10;
    private Object[] elements;
    private int size;
    
    public CustomArrayList() {
        elements = new Object[DEFAULT_CAPACITY];
    }
    
    public CustomArrayList(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Capacity cannot be negative");
        }
        elements = new Object[initialCapacity];
    }
    
    @Override
    public int size() {
        return size;
    }
    
    @Override
    public boolean isEmpty() {
        return size == 0;
    }
    
    @Override
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }
    
    @Override
    public Iterator<E> iterator() {
        return new CustomIterator();
    }
    
    @Override
    public Object[] toArray() {
        return Arrays.copyOf(elements, size);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            return (T[]) Arrays.copyOf(elements, size, a.getClass());
        }
        System.arraycopy(elements, 0, a, 0, size);
        if (a.length > size) {
            a[size] = null;
        }
        return a;
    }
    
    @Override
    public boolean add(E element) {
        ensureCapacity(size + 1);
        elements[size++] = element;
        return true;
    }
    
    @Override
    public void add(int index, E element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
        ensureCapacity(size + 1);
        System.arraycopy(elements, index, elements, index + 1, size - index);
        elements[index] = element;
        size++;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        return (E) elements[index];
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public E set(int index, E element) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        E old = (E) elements[index];
        elements[index] = element;
        return old;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public E remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        E removed = (E) elements[index];
        System.arraycopy(elements, index + 1, elements, index, size - index - 1);
        elements[--size] = null;
        return removed;
    }
    
    @Override
    public boolean remove(Object o) {
        int index = indexOf(o);
        if (index >= 0) {
            remove(index);
            return true;
        }
        return false;
    }
    
    @Override
    public void clear() {
        Arrays.fill(elements, 0, size, null);
        size = 0;
    }
    
    @Override
    public int indexOf(Object o) {
        if (o == null) {
            for (int i = 0; i < size; i++) {
                if (elements[i] == null) return i;
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (o.equals(elements[i])) return i;
            }
        }
        return -1;
    }
    
    @Override
    public int lastIndexOf(Object o) {
        if (o == null) {
            for (int i = size - 1; i >= 0; i--) {
                if (elements[i] == null) return i;
            }
        } else {
            for (int i = size - 1; i >= 0; i--) {
                if (o.equals(elements[i])) return i;
            }
        }
        return -1;
    }
    
    private void ensureCapacity(int minCapacity) {
        if (minCapacity > elements.length) {
            int newCapacity = elements.length + elements.length / 2;
            elements = Arrays.copyOf(elements, newCapacity);
        }
    }
    
    private class CustomIterator implements Iterator<E> {
        private int cursor;
        private boolean removable;
        
        @Override
        public boolean hasNext() {
            return cursor < size;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public E next() {
            if (cursor >= size) {
                throw new NoSuchElementException();
            }
            removable = true;
            return (E) elements[cursor++];
        }
        
        @Override
        public void remove() {
            if (!removable) {
                throw new IllegalStateException();
            }
            CustomArrayList.this.remove(--cursor);
            removable = false;
        }
    }
}
```

### 1.2 Custom Map Implementation

```java
package com.learning.collections.implementation;

import java.util.*;

public class CustomHashMap<K, V> implements Map<K, V> {
    
    private static final int DEFAULT_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;
    
    private Node<K, V>[] buckets;
    private int size;
    private int threshold;
    
    private static class Node<K, V> implements Map.Entry<K, V> {
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
    
    @SuppressWarnings("unchecked")
    public CustomHashMap() {
        buckets = new Node[DEFAULT_CAPACITY];
        threshold = (int) (DEFAULT_CAPACITY * LOAD_FACTOR);
    }
    
    @Override
    public int size() {
        return size;
    }
    
    @Override
    public boolean isEmpty() {
        return size == 0;
    }
    
    @Override
    public boolean containsKey(Object key) {
        return findNode(hash(key), key) != null;
    }
    
    @Override
    public boolean containsValue(Object value) {
        for (Node<K, V> bucket : buckets) {
            for (Node<K, V> node = bucket; node != null; node = node.next) {
                if (Objects.equals(node.value, value)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public V get(Object key) {
        Node<K, V> node = findNode(hash(key), key);
        return node != null ? node.value : null;
    }
    
    @Override
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
    
    @Override
    public V remove(Object key) {
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
    
    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }
    
    @Override
    public void clear() {
        Arrays.fill(buckets, null);
        size = 0;
    }
    
    @Override
    public Set<K> keySet() {
        Set<K> keys = new HashSet<>();
        for (Node<K, V> bucket : buckets) {
            for (Node<K, V> node = bucket; node != null; node = node.next) {
                keys.add(node.key);
            }
        }
        return keys;
    }
    
    @Override
    public Collection<V> values() {
        List<V> values = new ArrayList<>();
        for (Node<K, V> bucket : buckets) {
            for (Node<K, V> node = bucket; node != null; node = node.next) {
                values.add(node.value);
            }
        }
        return values;
    }
    
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> entries = new HashSet<>();
        for (Node<K, V> bucket : buckets) {
            for (Node<K, V> node = bucket; node != null; node = node.next) {
                entries.add(node);
            }
        }
        return entries;
    }
    
    private Node<K, V> findNode(int hash, Object key) {
        int index = hash & (buckets.length - 1);
        for (Node<K, V> node = buckets[index]; node != null; node = node.next) {
            if (node.hash == hash && Objects.equals(node.key, key)) {
                return node;
            }
        }
        return null;
    }
    
    private int hash(Object key) {
        return key == null ? 0 : key.hashCode();
    }
    
    @SuppressWarnings("unchecked")
    private void resize() {
        Node<K, V>[] old = buckets;
        int oldCap = old.length;
        
        Node<K, V>[] newBuckets = new Node[oldCap * 2];
        
        for (int i = 0; i < oldCap; i++) {
            Node<K, V> node = old[i];
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
}
```

### 1.3 Collection Operations Demo

```java
package com.learning.collections.implementation;

import java.util.*;

public class CollectionsOperations {
    
    public void demonstrateListOperations() {
        List<String> arrayList = new ArrayList<>();
        arrayList.add("Apple");
        arrayList.add("Banana");
        arrayList.add("Cherry");
        
        List<String> linkedList = new LinkedList<>();
        linkedList.addAll(arrayList);
        
        // List operations
        System.out.println("First element: " + arrayList.get(0));
        System.out.println("Contains 'Banana': " + arrayList.contains("Banana"));
        System.out.println("Index of 'Cherry': " + arrayList.indexOf("Cherry"));
        
        // Sublist
        List<String> subList = arrayList.subList(1, 3);
        System.out.println("Sublist: " + subList);
        
        // Sort
        Collections.sort(arrayList);
        System.out.println("Sorted: " + arrayList);
        
        // Shuffle
        Collections.shuffle(arrayList);
        System.out.println("Shuffled: " + arrayList);
    }
    
    public void demonstrateSetOperations() {
        Set<Integer> hashSet = new HashSet<>();
        hashSet.add(1);
        hashSet.add(2);
        hashSet.add(3);
        
        Set<Integer> linkedHashSet = new LinkedHashSet<>();
        linkedHashSet.add(1);
        linkedHashSet.add(2);
        linkedHashSet.add(3);
        
        Set<Integer> treeSet = new TreeSet<>();
        treeSet.add(3);
        treeSet.add(1);
        treeSet.add(2);
        
        // Set operations
        System.out.println("HashSet: " + hashSet);
        System.out.println("LinkedHashSet: " + linkedHashSet);
        System.out.println("TreeSet (sorted): " + treeSet);
        
        // Set operations
        Set<Integer> set1 = new HashSet<>(Set.of(1, 2, 3));
        Set<Integer> set2 = new HashSet<>(Set.of(3, 4, 5));
        
        Set<Integer> union = new HashSet<>(set1);
        union.addAll(set2);
        System.out.println("Union: " + union);
        
        Set<Integer> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        System.out.println("Intersection: " + intersection);
        
        Set<Integer> difference = new HashSet<>(set1);
        difference.removeAll(set2);
        System.out.println("Difference: " + difference);
    }
    
    public void demonstrateMapOperations() {
        Map<String, Integer> hashMap = new HashMap<>();
        hashMap.put("Apple", 1);
        hashMap.put("Banana", 2);
        hashMap.put("Cherry", 3);
        
        Map<String, Integer> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("First", 1);
        linkedHashMap.put("Second", 2);
        
        Map<String, Integer> treeMap = new TreeMap<>();
        treeMap.put("Zebra", 26);
        treeMap.put("Apple", 1);
        treeMap.put("Banana", 2);
        
        // Map operations
        System.out.println("Get 'Banana': " + hashMap.get("Banana"));
        System.out.println("Contains 'Apple': " + hashMap.containsKey("Apple"));
        System.out.println("Contains value 2: " + hashMap.containsValue(2));
        
        // Iteration
        for (Map.Entry<String, Integer> entry : hashMap.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
        
        // Compute operations
        hashMap.compute("Apple", (k, v) -> v + 10);
        hashMap.merge("Cherry", 100, (old, newVal) -> old + newVal);
        System.out.println("After compute: " + hashMap);
    }
    
    public void demonstrateQueueOperations() {
        Queue<Integer> priorityQueue = new PriorityQueue<>();
        priorityQueue.add(5);
        priorityQueue.add(2);
        priorityQueue.add(8);
        
        // PriorityQueue maintains natural order
        System.out.println("PriorityQueue: " + priorityQueue);
        System.out.println("Poll: " + priorityQueue.poll());
        System.out.println("After poll: " + priorityQueue);
        
        // Deque operations
        Deque<String> deque = new ArrayDeque<>();
        deque.addFirst("First");
        deque.addLast("Last");
        System.out.println("Deque: " + deque);
        System.out.println("Poll first: " + deque.pollFirst());
    }
    
    public void demonstrateSortingWithComparators() {
        List<Person> people = new ArrayList<>();
        people.add(new Person("John", 30));
        people.add(new Person("Alice", 25));
        people.add(new Person("Bob", 35));
        
        // Sort by name
        people.sort(Comparator.comparing(Person::getName));
        System.out.println("By name: " + people);
        
        // Sort by age descending
        people.sort(Comparator.comparingInt(Person::getAge).reversed());
        System.out.println("By age desc: " + people);
        
        // Complex sorting
        people.sort(Comparator.comparingInt(Person::getAge)
                .thenComparing(Person::getName));
    }
    
    static class Person {
        private String name;
        private int age;
        
        Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
        
        String getName() { return name; }
        int getAge() { return age; }
        
        @Override
        public String toString() {
            return name + "(" + age + ")";
        }
    }
}
```

---

## Part 2: Production Variant with Spring Boot

### 2.1 Spring Data JPA Configuration

```java
package com.learning.collections.config;

import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(basePackages = "com.learning.collections.repository")
@EnableTransactionManagement
public class CollectionsConfig {
    
    @Bean
    public DataSource dataSource() {
        // HikariCP configuration
        com.zaxxer.hikari.HikariConfig config = new com.zaxxer.hikari.HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/collections_db");
        config.setUsername("postgres");
        config.setPassword("password");
        config.setMaximumPoolSize(10);
        return new com.zaxxer.hikari.HikariDataSource(config);
    }
}
```

### 2.2 Service Layer with Collections

```java
package com.learning.collections.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.learning.collections.model.Product;
import com.learning.collections.repository.ProductRepository;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {
    
    private final ProductRepository repository;
    
    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }
    
    @Transactional(readOnly = true)
    public List<Product> findAllProducts() {
        return repository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Map<String, List<Product>> groupByCategory() {
        return repository.findAll().stream()
                .collect(Collectors.groupingBy(Product::getCategory));
    }
    
    @Transactional(readOnly = true)
    public Map<String, Long> countByCategory() {
        return repository.findAll().stream()
                .collect(Collectors.groupingBy(
                        Product::getCategory,
                        Collectors.counting()
                ));
    }
    
    @Transactional(readOnly = true)
    public Optional<Product> findMostExpensive() {
        return repository.findAll().stream()
                .max(Comparator.comparingDouble(Product::getPrice));
    }
    
    @Transactional
    public List<Product> findByPriceRange(double min, double max) {
        return repository.findAll().stream()
                .filter(p -> p.getPrice() >= min && p.getPrice() <= max)
                .sorted(Comparator.comparingDouble(Product::getPrice))
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public Set<String> getAllCategories() {
        return repository.findAll().stream()
                .map(Product::getCategory)
                .collect(Collectors.toSet());
    }
}
```

---

## Part 3: Step-by-Step Code Explanation

### 3.1 Custom ArrayList

**Step 1: Array Storage**
- Use Object[] array to store elements
- Maintain size variable tracking actual elements

**Step 2: Dynamic Growth**
- grow() doubles capacity when full
- ensureCapacity() checks and expands before adding

**Step 3: Index Operations**
- get/set: O(1) direct array access
- add/remove: O(n) requires shifting elements

**Step 4: Iterator Implementation**
- Maintain cursor position
- Support remove() during iteration

### 3.2 Custom HashMap

**Step 1: Buckets and Hashing**
- Array of linked list nodes
- hash() distributes keys across buckets

**Step 2: Collision Handling**
- Chaining: nodes form linked list
- Same index stores multiple entries

**Step 3: Resize**
- Double capacity when load factor exceeded
- Rehash all entries to new buckets

### 3.3 Collection Operations

**Step 1: List Operations**
- ArrayList: fast random access, slow insertions
- LinkedList: fast insertions/deletions, slow random access

**Step 2: Set Operations**
- HashSet: O(1) add/remove/contains
- TreeSet: O(log n) with sorted order

**Step 3: Map Operations**
- HashMap: key-value pairs, O(1) operations
- TreeMap: sorted keys, O(log n)

---

## Part 4: Key Concepts Demonstrated

| Concept | Implementation | Complexity |
|---------|---------------|------------|
| **ArrayList** | Dynamic array with resizing | O(1) get, O(n) add/remove |
| **LinkedList** | Doubly linked list | O(n) get, O(1) add/remove |
| **HashMap** | Hash table with chaining | O(1) average |
| **TreeMap** | Red-black tree | O(log n) |
| **HashSet** | HashMap-backed set | O(1) average |
| **TreeSet** | TreeMap-backed set | O(log n) |
| **PriorityQueue** | Binary heap | O(log n) insert/remove |
| **Comparators** | Multiple sorting strategies | Flexible |

---

## Key Takeaways

1. Choose collection based on operations needed
2. Understand time complexity trade-offs
3. Use generics for type safety
4. Prefer interfaces over implementations in APIs
5. Use streams for functional operations