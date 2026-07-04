# Refactoring Hash Tables

## Replace Manual Hash Table with HashMap

```java
// Before — manual implementation
class SymbolTable {
    private Entry[] entries = new Entry[100];
    // ... custom put/get/remove with linear probing
}

// After
Map<String, Symbol> symbolTable = new HashMap<>();
```

## Use computeIfAbsent for Lazy Initialization

```java
// Before
Map<String, List<Integer>> map = new HashMap<>();
if (!map.containsKey(key)) {
    map.put(key, new ArrayList<>());
}
map.get(key).add(value);

// After
map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
```

## Use merge for Cumulative Operations

```java
// Before
Map<String, Integer> counts = new HashMap<>();
counts.put(key, counts.getOrDefault(key, 0) + 1);

// After
counts.merge(key, 1, Integer::sum);
```

## Use Map.of / Map.ofEntries for Small Maps

```java
// Before
Map<String, String> map = new HashMap<>();
map.put("red", "#FF0000");
map.put("green", "#00FF00");
map.put("blue", "#0000FF");

// After
Map<String, String> map = Map.of(
    "red", "#FF0000",
    "green", "#00FF00",
    "blue", "#0000FF"
);  // immutable
```

## Prefer EnumMap for Enum Keys

```java
// Before — HashMap with enum keys
Map<DayOfWeek, String> map = new HashMap<>();

// After — EnumMap (faster, compact array-backed)
Map<DayOfWeek, String> map = new EnumMap<>(DayOfWeek.class);
```

## Use LinkedHashMap for Ordering

```java
// Before — arbitrary order
Map<String, Integer> map = new HashMap<>();

// After — insertion order
Map<String, Integer> map = new LinkedHashMap<>();

// Access order (for LRU cache)
Map<String, Integer> lru = new LinkedHashMap<>(16, 0.75f, true);
```
