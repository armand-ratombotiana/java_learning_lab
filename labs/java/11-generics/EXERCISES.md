# Generics — Practice Exercises

## Exercise 1: Generic Pair

Create a generic `Pair<K, V>` class with:
- Constructor taking K and V
- Getters for both values
- A `swap()` method that returns a new Pair with key and value swapped
- A static factory method `of(K key, V value)`

```java
// Expected usage:
Pair<String, Integer> p1 = Pair.of("age", 30);
Pair<Integer, String> p2 = p1.swap();
```

## Exercise 2: Generic Stack

Implement a generic stack `Stack<T>`:
- `push(T item)`
- `T pop()` (throws `EmptyStackException` if empty)
- `T peek()`
- `boolean isEmpty()`
- Use an internal `Object[]` array (generics limitation)

## Exercise 3: Type-Safe Heterogeneous Container

Create a `TypeSafeMap` that can store and retrieve values of any type, keyed by `Class<T>`:

```java
TypeSafeMap map = new TypeSafeMap();
map.put(String.class, "hello");
map.put(Integer.class, 42);
String s = map.get(String.class);  // No cast needed
```

## Exercise 4: Generic Binary Search

Implement a generic binary search method:

```java
public static <T extends Comparable<T>> int binarySearch(T[] array, T key)
```

## Exercise 5: Wildcard Methods

Implement a `copy` method and a `merge` method:

```java
public static <T> void copy(List<? extends T> source, List<? super T> dest)
public static <T> List<T> merge(List<? extends T> a, List<? extends T> b)
```

## Exercise 6: Generic Builder

Create a generic builder pattern that works for any class with a no-arg constructor:

```java
Order order = GenericBuilder.of(Order::new)
    .with(Order::setId, 1L)
    .with(Order::setCustomer, "Alice")
    .build();
```

## Exercise 7: Recursive Type Bound

Create an `Entity<T extends Entity<T>>` hierarchy:

```java
class User extends Entity<User> {
    public User withName(String name) { ...; return this; }
}
// Allows fluent API: new User().withId(1).withName("Alice")
```

## Solutions

### Exercise 1 Solution

```java
public record Pair<K, V>(K key, V value) {
    public Pair<K, V> swap() {
        return new Pair<>(value, key);
    }
    
    public static <K, V> Pair<K, V> of(K key, V value) {
        return new Pair<>(key, value);
    }
}
```

### Exercise 2 Solution

```java
public class Stack<T> {
    private Object[] elements;
    private int size = 0;

    public Stack(int capacity) {
        elements = new Object[capacity];
    }

    public void push(T item) {
        elements[size++] = item;
    }

    @SuppressWarnings("unchecked")
    public T pop() {
        if (isEmpty()) throw new EmptyStackException();
        T item = (T) elements[--size];
        elements[size] = null;
        return item;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) throw new EmptyStackException();
        return (T) elements[size - 1];
    }

    public boolean isEmpty() { return size == 0; }
}
```

### Exercise 3 Solution

```java
public class TypeSafeMap {
    private final Map<Class<?>, Object> map = new HashMap<>();

    public <T> void put(Class<T> type, T value) {
        map.put(type, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> type) {
        return (T) map.get(type);
    }
}
```
