# Common Mistakes with Hash Tables

## Mutable Keys

```java
// WRONG — using mutable object as key
Map<List<Integer>, String> map = new HashMap<>();
List<Integer> key = new ArrayList<>(List.of(1, 2, 3));
map.put(key, "value");
key.add(4);  // hash changes! map.get(key) returns null now

// CORRECT — use immutable keys (String, Integer, record)
Map<List<Integer>, String> map = new HashMap<>();
map.put(List.of(1, 2, 3), "value");  // List.of is immutable
```

## Broken hashCode/equals Contract

```java
// WRONG — equals overridden but not hashCode
class Key {
    String id;
    @Override public boolean equals(Object o) { ... }
    // hashCode not overridden → default Object.hashCode()
}

// CORRECT — both or neither
@Override public int hashCode() { return Objects.hash(id); }
```

## hashCode Returns Constant

```java
// WRONG — all objects hash to same bucket → O(n)
@Override public int hashCode() { return 42; }

// CORRECT — use all relevant fields
@Override public int hashCode() { return Objects.hash(field1, field2); }
```

## Forgetting capacity is power of 2

```java
// WRONG — HashMap requires power-of-2 capacity
new HashMap<>(100);  // actually rounds up to 128
// For custom implementation:
int capacity = 100;
int actual = 1;
while (actual < capacity) actual <<= 1;  // → 128
```

## Not Handling null Keys

```java
// HashMap handles null key (hash = 0, special bucket)
// ConcurrentHashMap does NOT allow null keys or values
```

## Assuming Insertion Order

```java
Map<String, Integer> map = new HashMap<>();
map.put("a", 1);
map.put("b", 2);
map.put("c", 3);
// Order NOT guaranteed! Use LinkedHashMap for insertion order.
```

## Ignoring Load Factor

```java
// WRONG — using default capacity for many entries
Map<Integer, String> map = new HashMap<>(16);
for (int i = 0; i < 100000; i++) {
    map.put(i, "value");  // many resizes!
}

// CORRECT — estimate needed capacity
Map<Integer, String> map = new HashMap<>(100000 / 0.75 + 1);
```
