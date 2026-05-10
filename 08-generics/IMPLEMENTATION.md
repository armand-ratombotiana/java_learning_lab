# Java Generics - Implementation Guide

## Module Overview

This module covers Java generics including generic classes, methods, type bounds, wildcards, and type erasure.

---

## Part 1: From-Scratch Java Implementation

### 1.1 Generic Classes

```java
package com.learning.generics.implementation;

// Generic class with single type parameter
public class Box<T> {
    private T content;
    
    public Box() {}
    
    public Box(T content) {
        this.content = content;
    }
    
    public T getContent() {
        return content;
    }
    
    public void setContent(T content) {
        this.content = content;
    }
    
    @Override
    public String toString() {
        return "Box{" + content + "}";
    }
}

// Generic class with multiple type parameters
public class Pair<K, V> {
    private K key;
    private V value;
    
    public Pair() {}
    
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }
    
    public K getKey() { return key; }
    public void setKey(K key) { this.key = key; }
    public V getValue() { return value; }
    public void setValue(V value) { this.value = value; }
    
    public static <K, V> Pair<K, V> of(K key, V value) {
        return new Pair<>(key, value);
    }
    
    @Override
    public String toString() {
        return "Pair{" + key + "=" + value + "}";
    }
}

// Generic class with bounded type parameter
public class NumberBox<T extends Number> {
    private T value;
    
    public NumberBox(T value) {
        this.value = value;
    }
    
    public double doubleValue() {
        return value.doubleValue();
    }
    
    public int intValue() {
        return value.intValue();
    }
    
    public Class<? extends Number> getType() {
        return value.getClass();
    }
}

// Generic class with multiple bounds
public class Calculator<T extends Number & Comparable<T>> {
    public T add(T a, T b) {
        // Note: This is simplified; actual implementation would need reflection
        return a;
    }
    
    public T max(T a, T b) {
        return a.compareTo(b) > 0 ? a : b;
    }
    
    public T min(T a, T b) {
        return a.compareTo(b) < 0 ? a : b;
    }
}
```

### 1.2 Generic Methods

```java
package com.learning.generics.implementation;

import java.util.*;

public class GenericMethods {
    
    // Generic method with single type parameter
    public static <T> void printArray(T[] array) {
        for (T element : array) {
            System.out.print(element + " ");
        }
        System.out.println();
    }
    
    // Generic method with return type
    public static <T> T getFirst(List<T> list) {
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }
    
    // Generic method with bounded type
    public static <T extends Comparable<T>> T findMax(T[] array) {
        if (array == null || array.length == 0) {
            return null;
        }
        
        T max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i].compareTo(max) > 0) {
                max = array[i];
            }
        }
        return max;
    }
    
    // Generic method with multiple bounds
    public static <T extends Number & Iterable<T>> void process(T item) {
        for (T i : item) {
            System.out.println(i);
        }
    }
    
    // Generic method with wildcard
    public static void printList(List<?> list) {
        for (Object obj : list) {
            System.out.println(obj);
        }
    }
    
    // Generic method with upper bound wildcard
    public static double sumOfList(List<? extends Number> list) {
        double sum = 0;
        for (Number n : list) {
            sum += n.doubleValue();
        }
        return sum;
    }
    
    // Generic method with lower bound wildcard
    public static void addNumbers(List<? super Integer> list) {
        list.add(10);
        list.add(20);
    }
    
    // Generic method with type token
    public static <T> T createInstance(Class<T> clazz) 
            throws InstantiationException, IllegalAccessException {
        return clazz.newInstance();
    }
    
    // Generic varargs
    public static <T> void printAll(T... elements) {
        for (T element : elements) {
            System.out.println(element);
        }
    }
    
    // Generic method for swapping
    public static <T> void swap(T[] array, int i, int j) {
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
```

### 1.3 Generic Interfaces

```java
package com.learning.generics.implementation;

// Generic interface
public interface Repository<T, ID> {
    T save(T entity);
    void delete(ID id);
    T findById(ID id);
    List<T> findAll();
}

// Generic interface implementation
public class InMemoryRepository<T, ID> implements Repository<T, ID> {
    private final Map<ID, T> storage = new HashMap<>();
    private ID nextId = null;
    
    @Override
    public T save(T entity) {
        // Simplified - just use incrementing ID
        return entity;
    }
    
    @Override
    public void delete(ID id) {
        storage.remove(id);
    }
    
    @Override
    public T findById(ID id) {
        return storage.get(id);
    }
    
    @Override
    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }
}

// Generic functional interface
@FunctionalInterface
public interface Transformer<T, R> {
    R transform(T input);
    
    default <V> Transformer<T, V> andThen(Transformer<R, V> after) {
        return t -> after.transform(transform(t));
    }
}

// Generic predicate
@FunctionalInterface
public interface Predicate<T> {
    boolean test(T value);
    
    default Predicate<T> and(Predicate<T> other) {
        return t -> test(t) && other.test(t);
    }
    
    default Predicate<T> or(Predicate<T> other) {
        return t -> test(t) || other.test(t);
    }
    
    default Predicate<T> negate() {
        return t -> !test(t);
    }
    
    static <T> Predicate<T> alwaysTrue() {
        return t -> true;
    }
}
```

### 1.4 Wildcards and Bounds

```java
package com.learning.generics.implementation;

import java.util.*;

public class WildcardsAndBounds {
    
    // Unbounded wildcard - accepts any type
    public static void printAnything(List<?> list) {
        list.forEach(System.out::println);
    }
    
    // Upper bound wildcard - accepts type and its subtypes
    public static void processNumbers(List<? extends Number> numbers) {
        for (Number n : numbers) {
            System.out.println(n.doubleValue());
        }
    }
    
    // Lower bound wildcard - accepts type and its supertypes
    public static void addIntegers(List<? super Integer> list) {
        list.add(1);
        list.add(2);
    }
    
    // Producer extends, consumer super (PECS)
    public static <T> void copy(List<? extends T> source, 
            List<? super T> destination) {
        for (T item : source) {
            destination.add(item);
        }
    }
    
    // Bounded wildcard with specific interface
    public static void processComparable(List<? extends Comparable<?>> list) {
        list.sort(Comparator.naturalOrder());
    }
    
    // Wildcard capture
    public static void swap(List<?> list, int i, int j) {
        // This needs helper method due to wildcard capture
        swapHelper(list, i, j);
    }
    
    private static <T> void swapHelper(List<T> list, int i, int j) {
        T temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }
    
    // Method with type parameter for wildcard capture
    public static <T> List<T> capture(List<?> list) {
        // This demonstrates capture conversion
        return (List<T>) list;
    }
}
```

### 1.5 Type Erasure and Bridge Methods

```java
package com.learning.generics.implementation;

import java.util.*;

public class TypeErasureDemo {
    
    // Generic class - erased to Object
    public static class ErasureBox<T> {
        private T content;
        
        public T getContent() {
            return content;
        }
        
        public void setContent(T content) {
            this.content = content;
        }
    }
    
    // After erasure becomes:
    public static class ErasureBoxRaw {
        private Object content;
        
        public Object getContent() {
            return content;
        }
        
        public void setContent(Object content) {
            this.content = content;
        }
    }
    
    // Generic method before erasure
    public static <T> T identity(T value) {
        return value;
    }
    
    // After erasure - type parameter removed
    public static Object identity(Object value) {
        return value;
    }
    
    // Bridge method demonstration
    public static class Node<T> {
        private T data;
        private Node<T> next;
        
        public Node(T data) {
            this.data = data;
        }
        
        public void setData(T data) {
            this.data = data;
        }
    }
    
    // Subclass with concrete type - gets bridge method
    public static class StringNode extends Node<String> {
        public StringNode(String data) {
            super(data);
        }
        
        @Override
        public void setData(String data) {
            super.setData(data);
        }
        
        // Bridge method added by compiler:
        // public void setData(Object data) {
        //     setData((String) data);
        // }
    }
    
    // Avoiding unchecked warnings
    @SuppressWarnings("unchecked")
    public static <T> List<T> createList() {
        return (List<T>) new ArrayList<String>();
    }
    
    // Using instanceof with generics
    public static void checkType(Object obj) {
        if (obj instanceof List<?>) {
            List<?> list = (List<?>) obj;
            System.out.println("List with " + list.size() + " elements");
        }
    }
}
```

---

## Part 2: Production Variant with Spring Boot

### 2.1 Generic Service

```java
package com.learning.generics.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

public abstract class GenericService<Entity, ID, Repo extends JpaRepository<Entity, ID>> {
    
    protected abstract Repo getRepository();
    
    @Transactional(readOnly = true)
    public List<Entity> findAll() {
        return getRepository().findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<Entity> findById(ID id) {
        return getRepository().findById(id);
    }
    
    @Transactional
    public Entity save(Entity entity) {
        return getRepository().save(entity);
    }
    
    @Transactional
    public void delete(Entity entity) {
        getRepository().delete(entity);
    }
    
    @Transactional
    public void deleteById(ID id) {
        getRepository().deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public long count() {
        return getRepository().count();
    }
}
```

### 2.2 Generic Controller

```java
package com.learning.generics.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

public abstract class GenericController<Entity, ID, Service> {
    
    protected abstract Service getService();
    
    @GetMapping
    public ResponseEntity<List<Entity>> getAll() {
        return ResponseEntity.ok(getService().findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Entity> getById(@PathVariable ID id) {
        return getService().findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Entity> create(@RequestBody Entity entity) {
        Entity saved = getService().save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Entity> update(
            @PathVariable ID id,
            @RequestBody Entity entity) {
        // Set ID from path
        return ResponseEntity.ok(getService().save(entity));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable ID id) {
        getService().deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
```

---

## Part 3: Step-by-Step Code Explanation

### 3.1 Generic Classes

**Step 1: Type Parameter**
- <T> represents placeholder type
- Can be replaced with any reference type

**Step 2: Type Bounds**
- extends limits to specific type or interface
- T extends Number ensures numeric operations

**Step 3: Multiple Type Parameters**
- <K, V> for key-value pairs
- Each can have own bounds

### 3.2 Generic Methods

**Step 1: Method-Level Type Parameters**
- Declared before return type
- Scoped to method only

**Step 2: Type Inference**
- Compiler infers type from arguments
- diamond <> syntax for constructors

### 3.3 Wildcards

**Step 1: Unbounded** - <?> accepts anything

**Step 2: Upper Bound** - <? extends Number> accepts Number and subtypes

**Step 3: Lower Bound** - <? super Integer> accepts Integer and supertypes

### 3.4 Type Erasure

**Step 1: Erasure**
- Type parameters removed at runtime
- Replaced with Object or bounds

**Step 2: Bridge Methods**
- Compiler adds for type compatibility

---

## Part 4: Key Concepts Demonstrated

| Concept | Implementation | Notes |
|---------|---------------|-------|
| **Generic Classes** | Box<T>, Pair<K,V> | Type parameters |
| **Generic Methods** | GenericMethods | Type inference |
| **Type Bounds** | T extends Number | Restricts types |
| **Wildcards** | ?, ? extends, ? super | Flexible typing |
| **Erasure** | Runtime type removal | Legacy compatibility |
| **PECS** | Producer extends, consumer super | Best practices |

---

## Key Takeaways

1. Generics provide compile-time type safety
2. Use bounded types when operations require specific capabilities
3. Prefer wildcards for flexibility in method parameters
4. Remember type erasure for backward compatibility
5. Apply PECS principle for collection parameters