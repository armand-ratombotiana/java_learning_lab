# Generics — Step-by-Step Tutorial

## Step 1: Create a Simple Generic Class

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

    public void setKey(K key) { this.key = key; }
    public void setValue(V value) { this.value = value; }
}
```

**Try it:** `Pair<String, Integer> pair = new Pair<>("age", 30);`

## Step 2: Add a Generic Method

```java
public class Pair<K, V> {
    // Existing code...

    public static <K, V> Pair<K, V> of(K key, V value) {
        return new Pair<>(key, value);
    }
}
```

Usage: `Pair<String, Integer> pair = Pair.of("age", 30);`

## Step 3: Add Bounded Type Parameter

Make a method that only works for `Number` values:

```java
public <T extends Number> double sumValue(Pair<?, T> pair) {
    return pair.getValue().doubleValue();
}
```

## Step 4: Add Wildcard Methods

```java
public boolean keysMatch(Pair<?, ?> other) {
    return this.key.equals(other.getKey());
}

public void copyTo(Pair<? super K, ? super V> other) {
    other.setKey(this.key);
    other.setValue(this.value);
}
```

## Step 5: Create a Generic Interface

```java
public interface Transformer<T, R> {
    R transform(T input);

    default <U> Transformer<T, U> andThen(Transformer<R, U> next) {
        return t -> next.transform(this.transform(t));
    }
}
```

Implement it:

```java
Transformer<String, Integer> length = String::length;
Transformer<Integer, String> toString = Object::toString;
Transformer<String, String> combined = length.andThen(toString);
// "hello" → 5 → "5"
```

## Step 6: Use Type Tokens for Runtime Type Access

```java
public class Repository<T> {
    private final Class<T> entityClass;
    private final Map<Object, T> store = new HashMap<>();

    public Repository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public T create(Object id) {
        try {
            T instance = entityClass.getDeclaredConstructor().newInstance();
            store.put(id, instance);
            return instance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public T find(Object id) { return store.get(id); }

    public Class<T> getEntityClass() { return entityClass; }
}

// Usage:
Repository<Order> repo = new Repository<>(Order.class);
Order order = repo.create(1L);
```

## Step 7: Test Generic Invariants

```java
// This compiles — generics are invariant:
List<Number> numbers = new ArrayList<>();
// numbers.addAll(List.of(1, 2, 3));  // Error! List<Integer> ≠ List<? extends Number>

// This works — wildcard gives covariance:
numbers.addAll(List.<Number>of(1, 2, 3));
numbers.addAll(Arrays.asList(1.5, 2.5, 3.5));  // List<Double> → Collection<? extends Number>
```

## Step 8: Avoid Raw Types

```java
// BAD — raw type:
List list = new ArrayList();
list.add("hello");
list.add(42);

// GOOD — parameterized:
List<String> list = new ArrayList<>();
list.add("hello");
// list.add(42);  // Compile error
```
