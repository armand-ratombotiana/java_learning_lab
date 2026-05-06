# Exercises: Generics

<div align="center">

![Module](https://img.shields.io/badge/Module-08-blue?style=for-the-badge)
![Exercises](https://img.shields.io/badge/Exercises-25-green?style=for-the-badge)
![Difficulty](https://img.shields.io/badge/Difficulty-Easy%20to%20Hard-orange?style=for-the-badge)

**25 comprehensive exercises for Generics module**

</div>

---

## 📚 Table of Contents

1. [Easy Exercises (1-8)](#easy-exercises-1-8)
2. [Medium Exercises (9-16)](#medium-exercises-9-16)
3. [Hard Exercises (17-21)](#hard-exercises-17-21)
4. [Interview Exercises (22-25)](#interview-exercises-22-25)

---

## 🟢 Easy Exercises (1-8)

### Exercise 1: Generic Classes
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** Generic classes, type parameters, instantiation

**Pedagogic Objective:**
Understand creating and using generic classes.

**Problem:**
Create a generic Box class that can hold any type of object.

**Complete Solution:**
```java
public class Box<T> {
    private T value;
    
    public void setValue(T value) {
        this.value = value;
    }
    
    public T getValue() {
        return value;
    }
}

public class GenericClassesExample {
    public static void main(String[] args) {
        // String box
        Box<String> stringBox = new Box<>();
        stringBox.setValue("Hello");
        System.out.println("String: " + stringBox.getValue());
        
        // Integer box
        Box<Integer> intBox = new Box<>();
        intBox.setValue(42);
        System.out.println("Integer: " + intBox.getValue());
        
        // Double box
        Box<Double> doubleBox = new Box<>();
        doubleBox.setValue(3.14);
        System.out.println("Double: " + doubleBox.getValue());
    }
}
```

**Key Concepts:**
- Type parameter T represents any type
- Generic classes provide type safety
- Eliminates casting
- Reusable for multiple types

---

### Exercise 2: Generic Methods
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** Generic methods, type parameters, method signatures

**Pedagogic Objective:**
Understand creating generic methods.

**Complete Solution:**
```java
public class GenericMethods {
    public static <T> void printArray(T[] array) {
        for (T element : array) {
            System.out.print(element + " ");
        }
        System.out.println();
    }
    
    public static <T> T getFirst(T[] array) {
        return array.length > 0 ? array[0] : null;
    }
    
    public static void main(String[] args) {
        Integer[] intArray = {1, 2, 3, 4, 5};
        String[] strArray = {"a", "b", "c"};
        
        printArray(intArray);
        printArray(strArray);
        
        System.out.println("First int: " + getFirst(intArray));
        System.out.println("First string: " + getFirst(strArray));
    }
}
```

**Key Concepts:**
- Generic methods work with any type
- Type parameter declared before return type
- Type inference from arguments
- Reusable across types

---

### Exercise 3: Bounded Type Parameters
**Difficulty:** Easy  
**Time:** 20 minutes  
**Topics:** Bounded types, extends keyword, type constraints

**Pedagogic Objective:**
Understand restricting generic types with bounds.

**Complete Solution:**
```java
public class BoundedTypeParameters {
    public static <T extends Number> double sum(T[] array) {
        double sum = 0;
        for (T element : array) {
            sum += element.doubleValue();
        }
        return sum;
    }
    
    public static void main(String[] args) {
        Integer[] intArray = {1, 2, 3, 4, 5};
        Double[] doubleArray = {1.5, 2.5, 3.5};
        
        System.out.println("Sum of integers: " + sum(intArray));
        System.out.println("Sum of doubles: " + sum(doubleArray));
    }
}
```

**Key Concepts:**
- Bounded types restrict type parameters
- extends keyword specifies upper bound
- Only types extending bound allowed
- Access to bound type methods

---

### Exercise 4: Wildcards
**Difficulty:** Easy  
**Time:** 20 minutes  
**Topics:** Wildcards, ?, unbounded wildcards

**Pedagogic Objective:**
Understand using wildcards in generics.

**Complete Solution:**
```java
import java.util.*;

public class Wildcards {
    public static void printList(List<?> list) {
        for (Object element : list) {
            System.out.print(element + " ");
        }
        System.out.println();
    }
    
    public static void main(String[] args) {
        List<String> stringList = Arrays.asList("a", "b", "c");
        List<Integer> intList = Arrays.asList(1, 2, 3);
        List<Double> doubleList = Arrays.asList(1.1, 2.2, 3.3);
        
        printList(stringList);
        printList(intList);
        printList(doubleList);
    }
}
```

**Key Concepts:**
- Wildcard ? represents unknown type
- Unbounded wildcards accept any type
- Read-only operations
- Useful for flexible method signatures

---

### Exercise 5: Upper Bounded Wildcards
**Difficulty:** Easy  
**Time:** 20 minutes  
**Topics:** Upper bounded wildcards, extends, type hierarchy

**Pedagogic Objective:**
Understand upper bounded wildcards.

**Complete Solution:**
```java
import java.util.*;

public class UpperBoundedWildcards {
    public static double sumNumbers(List<? extends Number> list) {
        double sum = 0;
        for (Number num : list) {
            sum += num.doubleValue();
        }
        return sum;
    }
    
    public static void main(String[] args) {
        List<Integer> intList = Arrays.asList(1, 2, 3);
        List<Double> doubleList = Arrays.asList(1.5, 2.5, 3.5);
        
        System.out.println("Sum of integers: " + sumNumbers(intList));
        System.out.println("Sum of doubles: " + sumNumbers(doubleList));
    }
}
```

**Key Concepts:**
- Upper bounded wildcards: ? extends Type
- Accepts type and subtypes
- Read-only operations
- Useful for reading from collections

---

### Exercise 6: Lower Bounded Wildcards
**Difficulty:** Easy  
**Time:** 20 minutes  
**Topics:** Lower bounded wildcards, super, type hierarchy

**Pedagogic Objective:**
Understand lower bounded wildcards.

**Complete Solution:**
```java
import java.util.*;

public class LowerBoundedWildcards {
    public static void addNumbers(List<? super Integer> list) {
        list.add(1);
        list.add(2);
        list.add(3);
    }
    
    public static void main(String[] args) {
        List<Number> numberList = new ArrayList<>();
        List<Integer> intList = new ArrayList<>();
        
        addNumbers(numberList);
        addNumbers(intList);
        
        System.out.println("Number list: " + numberList);
        System.out.println("Integer list: " + intList);
    }
}
```

**Key Concepts:**
- Lower bounded wildcards: ? super Type
- Accepts type and supertypes
- Write-friendly operations
- Useful for adding to collections

---

### Exercise 7: Type Erasure
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** Type erasure, runtime behavior, generics limitations

**Pedagogic Objective:**
Understand type erasure in Java generics.

**Complete Solution:**
```java
import java.util.*;

public class TypeErasure {
    public static void main(String[] args) {
        List<String> stringList = new ArrayList<>();
        List<Integer> intList = new ArrayList<>();
        
        // At runtime, both are List
        System.out.println("String list class: " + stringList.getClass());
        System.out.println("Integer list class: " + intList.getClass());
        System.out.println("Are they equal? " + 
            stringList.getClass().equals(intList.getClass()));
        
        // Cannot use instanceof with generics
        // if (stringList instanceof List<String>) {} // Compile error
        
        // Can use raw type
        if (stringList instanceof List) {
            System.out.println("stringList is a List");
        }
    }
}
```

**Key Concepts:**
- Type information erased at runtime
- Generic types become raw types
- Cannot use instanceof with generics
- Backward compatibility with raw types

---

### Exercise 8: Generic Interfaces
**Difficulty:** Easy  
**Time:** 20 minutes  
**Topics:** Generic interfaces, implementation, type parameters

**Pedagogic Objective:**
Understand implementing generic interfaces.

**Complete Solution:**
```java
public interface Container<T> {
    void add(T element);
    T get();
    boolean isEmpty();
}

public class Stack<T> implements Container<T> {
    private java.util.List<T> items = new java.util.ArrayList<>();
    
    @Override
    public void add(T element) {
        items.add(element);
    }
    
    @Override
    public T get() {
        return items.isEmpty() ? null : items.remove(items.size() - 1);
    }
    
    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }
}

public class GenericInterfacesExample {
    public static void main(String[] args) {
        Stack<String> stack = new Stack<>();
        stack.add("First");
        stack.add("Second");
        
        System.out.println(stack.get());
        System.out.println(stack.get());
    }
}
```

**Key Concepts:**
- Generic interfaces define type parameters
- Implementing classes specify type
- Type safety in implementations
- Flexible interface contracts

---

## 🟡 Medium Exercises (9-16)

### Exercise 9: Multiple Type Parameters
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Multiple type parameters, complex generics

**Complete Solution:**
```java
public class Pair<K, V> {
    private K key;
    private V value;
    
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }
    
    public K getKey() { return key; }
    public V getValue() { return value; }
    
    @Override
    public String toString() {
        return "(" + key + ", " + value + ")";
    }
}

public class MultipleTypeParameters {
    public static void main(String[] args) {
        Pair<String, Integer> pair1 = new Pair<>("Age", 25);
        Pair<String, Double> pair2 = new Pair<>("Height", 5.9);
        Pair<Integer, String> pair3 = new Pair<>(1, "One");
        
        System.out.println(pair1);
        System.out.println(pair2);
        System.out.println(pair3);
    }
}
```

---

### Exercise 10: Generic Collections
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Generic collections, type safety, collections framework

**Complete Solution:**
```java
import java.util.*;

public class GenericCollections {
    public static void main(String[] args) {
        // Type-safe list
        List<String> names = new ArrayList<>();
        names.add("Alice");
        names.add("Bob");
        
        // Type-safe map
        Map<String, Integer> ages = new HashMap<>();
        ages.put("Alice", 25);
        ages.put("Bob", 30);
        
        // Type-safe set
        Set<Integer> numbers = new HashSet<>();
        numbers.add(1);
        numbers.add(2);
        numbers.add(3);
        
        System.out.println("Names: " + names);
        System.out.println("Ages: " + ages);
        System.out.println("Numbers: " + numbers);
    }
}
```

---

### Exercise 11: Generic Comparator
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Generic comparators, sorting, type parameters

**Complete Solution:**
```java
import java.util.*;

public class Person {
    private String name;
    private int age;
    
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    public String getName() { return name; }
    public int getAge() { return age; }
    
    @Override
    public String toString() {
        return name + " (" + age + ")";
    }
}

public class GenericComparator {
    public static void main(String[] args) {
        List<Person> people = Arrays.asList(
            new Person("Alice", 25),
            new Person("Bob", 30),
            new Person("Charlie", 20)
        );
        
        // Sort by age
        people.sort(Comparator.comparingInt(Person::getAge));
        System.out.println("By age: " + people);
        
        // Sort by name
        people.sort(Comparator.comparing(Person::getName));
        System.out.println("By name: " + people);
    }
}
```

---

### Exercise 12: Generic Utility Methods
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Generic utility methods, reusable code

**Complete Solution:**
```java
import java.util.*;

public class GenericUtility {
    public static <T> List<T> reverse(List<T> list) {
        List<T> reversed = new ArrayList<>(list);
        Collections.reverse(reversed);
        return reversed;
    }
    
    public static <T> T findMax(List<T> list, Comparator<T> comparator) {
        return list.stream().max(comparator).orElse(null);
    }
    
    public static <T> int countOccurrences(List<T> list, T element) {
        return (int) list.stream().filter(e -> e.equals(element)).count();
    }
    
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        System.out.println("Reversed: " + reverse(numbers));
        System.out.println("Max: " + findMax(numbers, Integer::compareTo));
        System.out.println("Count of 3: " + countOccurrences(numbers, 3));
    }
}
```

---

### Exercise 13: Generic Constraints
**Difficulty:** Medium  
**Time:** 30 minutes  
**Topics:** Type constraints, bounded types, multiple bounds

**Complete Solution:**
```java
public class GenericConstraints {
    // Single bound
    public static <T extends Comparable<T>> T max(T a, T b) {
        return a.compareTo(b) > 0 ? a : b;
    }
    
    // Multiple bounds
    public static <T extends Number & Comparable<T>> void compare(T a, T b) {
        System.out.println("A: " + a + ", B: " + b);
        System.out.println("A > B: " + (a.doubleValue() > b.doubleValue()));
    }
    
    public static void main(String[] args) {
        System.out.println("Max of 5 and 10: " + max(5, 10));
        System.out.println("Max of 'a' and 'z': " + max("a", "z"));
        
        compare(5, 10);
        compare(3.5, 2.5);
    }
}
```

---

### Exercise 14: Generic Inheritance
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Generic inheritance, type parameters, subclassing

**Complete Solution:**
```java
public class Container<T> {
    protected T value;
    
    public void setValue(T value) {
        this.value = value;
    }
    
    public T getValue() {
        return value;
    }
}

public class NumberContainer<T extends Number> extends Container<T> {
    public double getDoubleValue() {
        return value.doubleValue();
    }
}

public class GenericInheritance {
    public static void main(String[] args) {
        NumberContainer<Integer> intContainer = new NumberContainer<>();
        intContainer.setValue(42);
        System.out.println("Value: " + intContainer.getValue());
        System.out.println("Double: " + intContainer.getDoubleValue());
        
        NumberContainer<Double> doubleContainer = new NumberContainer<>();
        doubleContainer.setValue(3.14);
        System.out.println("Value: " + doubleContainer.getValue());
        System.out.println("Double: " + doubleContainer.getDoubleValue());
    }
}
```

---

### Exercise 15: Covariance and Contravariance
**Difficulty:** Medium  
**Time:** 30 minutes  
**Topics:** Covariance, contravariance, PECS principle

**Complete Solution:**
```java
import java.util.*;

public class CovarianceContravariance {
    // Covariance: ? extends T (read-friendly)
    public static void readFromList(List<? extends Number> list) {
        for (Number num : list) {
            System.out.println(num);
        }
    }
    
    // Contravariance: ? super T (write-friendly)
    public static void writeToList(List<? super Integer> list) {
        list.add(1);
        list.add(2);
        list.add(3);
    }
    
    public static void main(String[] args) {
        List<Integer> intList = Arrays.asList(1, 2, 3);
        List<Double> doubleList = Arrays.asList(1.5, 2.5, 3.5);
        
        readFromList(intList);
        readFromList(doubleList);
        
        List<Number> numberList = new ArrayList<>();
        writeToList(numberList);
        System.out.println("Written: " + numberList);
    }
}
```

---

### Exercise 16: Generic Streams
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Generics with streams, functional programming

**Complete Solution:**
```java
import java.util.*;
import java.util.stream.*;

public class GenericStreams {
    public static <T> List<T> filter(List<T> list, java.util.function.Predicate<T> predicate) {
        return list.stream()
            .filter(predicate)
            .collect(Collectors.toList());
    }
    
    public static <T, R> List<R> map(List<T> list, java.util.function.Function<T, R> mapper) {
        return list.stream()
            .map(mapper)
            .collect(Collectors.toList());
    }
    
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        List<Integer> evens = filter(numbers, n -> n % 2 == 0);
        System.out.println("Even numbers: " + evens);
        
        List<String> strings = map(numbers, n -> "Number: " + n);
        System.out.println("Mapped: " + strings);
    }
}
```

---

## 🔴 Hard Exercises (17-21)

### Exercise 17: Generic Builder Pattern
**Difficulty:** Hard  
**Time:** 40 minutes  
**Topics:** Builder pattern, generics, fluent API

**Complete Solution:**
```java
public class GenericBuilder<T> {
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

public class GenericBuilderExample {
    public static void main(String[] args) {
        String result = new GenericBuilder<>("hello")
            .transform(String::toUpperCase)
            .transform(s -> s + " WORLD")
            .build();
        
        System.out.println(result);
        
        Integer number = new GenericBuilder<>(5)
            .transform(n -> n * 2)
            .transform(n -> n + 10)
            .build();
        
        System.out.println(number);
    }
}
```

---

### Exercise 18: Generic Recursion
**Difficulty:** Hard  
**Time:** 40 minutes  
**Topics:** Recursive generics, self-referential types

**Complete Solution:**
```java
public class Node<T extends Node<T>> {
    private String value;
    private T next;
    
    public Node(String value) {
        this.value = value;
    }
    
    public void setNext(T next) {
        this.next = next;
    }
    
    public T getNext() {
        return next;
    }
    
    public String getValue() {
        return value;
    }
}

public class LinkedList<T extends Node<T>> {
    private T head;
    
    public void add(T node) {
        if (head == null) {
            head = node;
        } else {
            T current = head;
            while (current.getNext() != null) {
                current = current.getNext();
            }
            current.setNext(node);
        }
    }
    
    public void print() {
        T current = head;
        while (current != null) {
            System.out.print(current.getValue() + " -> ");
            current = current.getNext();
        }
        System.out.println("null");
    }
}

public class GenericRecursion {
    public static void main(String[] args) {
        LinkedList<Node<Node<?>>> list = new LinkedList<>();
        list.add(new Node<>("A"));
        list.add(new Node<>("B"));
        list.add(new Node<>("C"));
        list.print();
    }
}
```

---

### Exercise 19: Generic Factory Pattern
**Difficulty:** Hard  
**Time:** 40 minutes  
**Topics:** Factory pattern, generics, reflection

**Complete Solution:**
```java
public class GenericFactory<T> {
    private Class<T> type;
    
    public GenericFactory(Class<T> type) {
        this.type = type;
    }
    
    public T create() throws Exception {
        return type.getDeclaredConstructor().newInstance();
    }
    
    public T create(Object... args) throws Exception {
        Class<?>[] paramTypes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            paramTypes[i] = args[i].getClass();
        }
        return type.getDeclaredConstructor(paramTypes).newInstance(args);
    }
}

public class GenericFactoryExample {
    static class Person {
        private String name;
        
        public Person() {
            this.name = "Unknown";
        }
        
        public Person(String name) {
            this.name = name;
        }
        
        @Override
        public String toString() {
            return "Person{" + "name='" + name + '\'' + '}';
        }
    }
    
    public static void main(String[] args) throws Exception {
        GenericFactory<Person> factory = new GenericFactory<>(Person.class);
        
        Person p1 = factory.create();
        System.out.println(p1);
        
        Person p2 = factory.create("Alice");
        System.out.println(p2);
    }
}
```

---

### Exercise 20: Generic Caching
**Difficulty:** Hard  
**Time:** 40 minutes  
**Topics:** Generics, caching, type safety

**Complete Solution:**
```java
import java.util.*;

public class GenericCache<K, V> {
    private Map<K, V> cache = new HashMap<>();
    
    public void put(K key, V value) {
        cache.put(key, value);
    }
    
    public V get(K key) {
        return cache.get(key);
    }
    
    public V getOrCompute(K key, java.util.function.Function<K, V> computer) {
        return cache.computeIfAbsent(key, computer);
    }
    
    public void clear() {
        cache.clear();
    }
    
    public int size() {
        return cache.size();
    }
}

public class GenericCachingExample {
    public static void main(String[] args) {
        GenericCache<String, Integer> cache = new GenericCache<>();
        
        cache.put("one", 1);
        cache.put("two", 2);
        
        System.out.println("one: " + cache.get("one"));
        System.out.println("three: " + cache.getOrCompute("three", k -> 3));
        System.out.println("Cache size: " + cache.size());
    }
}
```

---

### Exercise 21: Generic Validation
**Difficulty:** Hard  
**Time:** 40 minutes  
**Topics:** Generics, validation, functional programming

**Complete Solution:**
```java
import java.util.*;
import java.util.function.Predicate;

public class GenericValidator<T> {
    private List<Predicate<T>> rules = new ArrayList<>();
    
    public GenericValidator<T> addRule(Predicate<T> rule) {
        rules.add(rule);
        return this;
    }
    
    public boolean validate(T value) {
        return rules.stream().allMatch(rule -> rule.test(value));
    }
    
    public List<String> getErrors(T value) {
        List<String> errors = new ArrayList<>();
        if (!validate(value)) {
            errors.add("Validation failed");
        }
        return errors;
    }
}

public class GenericValidationExample {
    public static void main(String[] args) {
        GenericValidator<String> stringValidator = new GenericValidator<String>()
            .addRule(s -> s != null)
            .addRule(s -> !s.isEmpty())
            .addRule(s -> s.length() >= 3);
        
        System.out.println("Valid 'hello': " + stringValidator.validate("hello"));
        System.out.println("Valid 'ab': " + stringValidator.validate("ab"));
        System.out.println("Valid null: " + stringValidator.validate(null));
        
        GenericValidator<Integer> intValidator = new GenericValidator<Integer>()
            .addRule(n -> n != null)
            .addRule(n -> n > 0)
            .addRule(n -> n < 100);
        
        System.out.println("Valid 50: " + intValidator.validate(50));
        System.out.println("Valid 150: " + intValidator.validate(150));
    }
}
```

---

## 🎯 Interview Exercises (22-25)

### Exercise 22: Generic Tree Structure
**Difficulty:** Interview  
**Time:** 35 minutes

**Complete Solution:**
```java
public class TreeNode<T> {
    private T value;
    private TreeNode<T> left;
    private TreeNode<T> right;
    
    public TreeNode(T value) {
        this.value = value;
    }
    
    public void setLeft(TreeNode<T> left) { this.left = left; }
    public void setRight(TreeNode<T> right) { this.right = right; }
    
    public TreeNode<T> getLeft() { return left; }
    public TreeNode<T> getRight() { return right; }
    public T getValue() { return value; }
}

public class BinaryTree<T extends Comparable<T>> {
    private TreeNode<T> root;
    
    public void insert(T value) {
        root = insertRecursive(root, value);
    }
    
    private TreeNode<T> insertRecursive(TreeNode<T> node, T value) {
        if (node == null) {
            return new TreeNode<>(value);
        }
        
        if (value.compareTo(node.getValue()) < 0) {
            node.setLeft(insertRecursive(node.getLeft(), value));
        } else {
            node.setRight(insertRecursive(node.getRight(), value));
        }
        
        return node;
    }
    
    public void inorder(TreeNode<T> node) {
        if (node != null) {
            inorder(node.getLeft());
            System.out.print(node.getValue() + " ");
            inorder(node.getRight());
        }
    }
    
    public static void main(String[] args) {
        BinaryTree<Integer> tree = new BinaryTree<>();
        tree.insert(5);
        tree.insert(3);
        tree.insert(7);
        tree.insert(1);
        tree.insert(9);
        
        tree.inorder(tree.root);
    }
}
```

---

### Exercise 23: Generic Graph
**Difficulty:** Interview  
**Time:** 40 minutes

**Complete Solution:**
```java
import java.util.*;

public class GenericGraph<T> {
    private Map<T, List<T>> adjacencyList = new HashMap<>();
    
    public void addVertex(T vertex) {
        adjacencyList.putIfAbsent(vertex, new ArrayList<>());
    }
    
    public void addEdge(T source, T destination) {
        addVertex(source);
        addVertex(destination);
        adjacencyList.get(source).add(destination);
    }
    
    public List<T> bfs(T start) {
        List<T> result = new ArrayList<>();
        Set<T> visited = new HashSet<>();
        Queue<T> queue = new LinkedList<>();
        
        queue.add(start);
        visited.add(start);
        
        while (!queue.isEmpty()) {
            T vertex = queue.poll();
            result.add(vertex);
            
            for (T neighbor : adjacencyList.get(vertex)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        
        return result;
    }
    
    public static void main(String[] args) {
        GenericGraph<String> graph = new GenericGraph<>();
        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        graph.addEdge("B", "D");
        
        System.out.println("BFS: " + graph.bfs("A"));
    }
}
```

---

### Exercise 24: Generic Dependency Injection
**Difficulty:** Interview  
**Time:** 40 minutes

**Complete Solution:**
```java
import java.util.*;

public class GenericContainer<T> {
    private Map<Class<?>, Object> instances = new HashMap<>();
    
    public <U> void register(Class<U> type, U instance) {
        instances.put(type, instance);
    }
    
    @SuppressWarnings("unchecked")
    public <U> U resolve(Class<U> type) {
        return (U) instances.get(type);
    }
}

public class GenericDependencyInjection {
    interface Logger {
        void log(String message);
    }
    
    static class ConsoleLogger implements Logger {
        @Override
        public void log(String message) {
            System.out.println("[LOG] " + message);
        }
    }
    
    static class Service {
        private Logger logger;
        
        public Service(Logger logger) {
            this.logger = logger;
        }
        
        public void doWork() {
            logger.log("Doing work...");
        }
    }
    
    public static void main(String[] args) {
        GenericContainer<Object> container = new GenericContainer<>();
        container.register(Logger.class, new ConsoleLogger());
        
        Logger logger = container.resolve(Logger.class);
        Service service = new Service(logger);
        service.doWork();
    }
}
```

---

### Exercise 25: Generic Type Adapter
**Difficulty:** Interview  
**Time:** 40 minutes

**Complete Solution:**
```java
public interface TypeAdapter<T> {
    T fromString(String value);
    String toString(T value);
}

public class IntegerAdapter implements TypeAdapter<Integer> {
    @Override
    public Integer fromString(String value) {
        return Integer.parseInt(value);
    }
    
    @Override
    public String toString(Integer value) {
        return value.toString();
    }
}

public class GenericTypeAdapterRegistry<T> {
    private java.util.Map<Class<?>, TypeAdapter<?>> adapters = new java.util.HashMap<>();
    
    public <U> void register(Class<U> type, TypeAdapter<U> adapter) {
        adapters.put(type, adapter);
    }
    
    @SuppressWarnings("unchecked")
    public <U> TypeAdapter<U> getAdapter(Class<U> type) {
        return (TypeAdapter<U>) adapters.get(type);
    }
    
    public static void main(String[] args) {
        GenericTypeAdapterRegistry<Object> registry = new GenericTypeAdapterRegistry<>();
        registry.register(Integer.class, new IntegerAdapter());
        
        TypeAdapter<Integer> adapter = registry.getAdapter(Integer.class);
        Integer value = adapter.fromString("42");
        System.out.println("Parsed: " + value);
        System.out.println("String: " + adapter.toString(value));
    }
}
```

---

## 📊 Solutions Summary

| Exercise | Title | Difficulty | Time | Topics |
|----------|-------|-----------|------|--------|
| 1 | Generic Classes | Easy | 15 min | Classes |
| 2 | Generic Methods | Easy | 15 min | Methods |
| 3 | Bounded Types | Easy | 20 min | Bounds |
| 4 | Wildcards | Easy | 20 min | Wildcards |
| 5 | Upper Bounds | Easy | 20 min | Upper |
| 6 | Lower Bounds | Easy | 20 min | Lower |
| 7 | Type Erasure | Easy | 15 min | Erasure |
| 8 | Generic Interfaces | Easy | 20 min | Interfaces |
| 9 | Multiple Types | Medium | 25 min | Multiple |
| 10 | Collections | Medium | 25 min | Collections |
| 11 | Comparator | Medium | 25 min | Sorting |
| 12 | Utility Methods | Medium | 25 min | Utilities |
| 13 | Constraints | Medium | 30 min | Constraints |
| 14 | Inheritance | Medium | 25 min | Inheritance |
| 15 | Covariance | Medium | 30 min | Variance |
| 16 | Streams | Medium | 25 min | Streams |
| 17 | Builder | Hard | 40 min | Patterns |
| 18 | Recursion | Hard | 40 min | Recursion |
| 19 | Factory | Hard | 40 min | Patterns |
| 20 | Caching | Hard | 40 min | Caching |
| 21 | Validation | Hard | 40 min | Validation |
| 22 | Tree | Interview | 35 min | Trees |
| 23 | Graph | Interview | 40 min | Graphs |
| 24 | DI | Interview | 40 min | Patterns |
| 25 | Adapter | Interview | 40 min | Patterns |

---

<div align="center">

## Exercises: Generics

**25 Comprehensive Exercises**

**Easy (8) | Medium (8) | Hard (5) | Interview (4)**

**Total Time: 8-10 hours**

---

[Back to Module →](./README.md)

[View Pedagogic Guide →](./PEDAGOGIC_GUIDE.md)

[Take Quizzes →](./QUIZZES.md)

</div>

(ending readme)