# Generics — Refactoring Guide

## Refactoring 1: Raw Types to Parameterized Types

**Before:**
```java
public class OrderProcessor {
    private List orders = new ArrayList();

    public void addOrder(Object order) {
        orders.add(order);
    }

    public Object getOrder(int index) {
        return orders.get(index);
    }
}
```

**After:**
```java
public class OrderProcessor {
    private List<Order> orders = new ArrayList<>();

    public void addOrder(Order order) {
        orders.add(order);
    }

    public Order getOrder(int index) {
        return orders.get(index);
    }
}
```

**Benefits**: Type safety, eliminated casts, self-documenting.

## Refactoring 2: Duplicate Classes to Generic

**Before:**
```java
public class StringCache {
    private final Map<String, String> cache = new HashMap<>();
    public String get(String key) { return cache.get(key); }
    public void put(String key, String value) { cache.put(key, value); }
}

public class IntegerCache {
    private final Map<String, Integer> cache = new HashMap<>();
    public Integer get(String key) { return cache.get(key); }
    public void put(String key, Integer value) { cache.put(key, value); }
}
```

**After:**
```java
public class Cache<T> {
    private final Map<String, T> cache = new HashMap<>();
    public T get(String key) { return cache.get(key); }
    public void put(String key, T value) { cache.put(key, value); }
}
```

## Refactoring 3: Unsafe Casts to Class.cast()

**Before:**
```java
public class Deserializer {
    @SuppressWarnings("unchecked")
    public <T> T deserialize(String data) {
        Object result = // ... deserialize logic
        return (T) result;  // Unsafe — no check
    }
}
```

**After:**
```java
public class Deserializer {
    public <T> T deserialize(String data, Class<T> type) {
        Object result = // ... deserialize logic
        return type.cast(result);  // Safe — ClassCastException at cast point
    }
}
```

## Refactoring 4: Wildcard to Exact Type

**Before:**
```java
public <T> void copy(List<? extends T> src, List<? extends T> dest) {
    for (T item : src) {
        dest.add(item);  // Error! Cannot add to ? extends T
    }
}
```

**After:**
```java
public <T> void copy(List<? extends T> src, List<? super T> dest) {
    for (T item : src) {
        dest.add(item);  // OK — consumer uses ? super T
    }
}
```

## Refactoring 5: Remove Redundant Type Parameters (Diamond)

**Before:**
```java
Map<String, List<Order>> orderMap = new HashMap<String, List<Order>>();
```

**After:**
```java
Map<String, List<Order>> orderMap = new HashMap<>();
```

## Refactoring 6: Extract Generic Interface

**Before:**
```java
public interface StringRepository {
    String findById(String id);
    void save(String value);
}

public interface OrderRepository {
    Order findById(String id);
    void save(Order value);
}
```

**After:**
```java
public interface Repository<T> {
    T findById(String id);
    void save(T value);
}

public class OrderRepository implements Repository<Order> { ... }
public class StringRepository implements Repository<String> { ... }
```

## Refactoring Checklist

- [ ] Replace raw types with parameterized types
- [ ] Eliminate unsafe casts with `Class.cast()`
- [ ] Replace duplicate classes with generics
- [ ] Fix wildcard direction (PECS)
- [ ] Add `@SuppressWarnings("unchecked")` only after verifying safety
- [ ] Remove redundant type parameters with diamond operator
- [ ] Extract common interfaces as generic interfaces
- [ ] Use bounded type parameters where appropriate
