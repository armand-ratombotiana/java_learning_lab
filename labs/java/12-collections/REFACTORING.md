# Collections — Refactoring Guide

## Refactoring 1: Replace Loop with Collection Methods

**Before:**
```java
List<String> result = new ArrayList<>();
for (String s : list) {
    if (!s.isEmpty()) {
        result.add(s);
    }
}
```

**After:**
```java
List<String> result = new ArrayList<>(list);
result.removeIf(String::isEmpty);
```

## Refactoring 2: Replace Array with ArrayList

**Before:**
```java
String[] names = new String[10];
names[0] = "Alice";
int count = 1;
// Manual resizing needed
```

**After:**
```java
List<String> names = new ArrayList<>();
names.add("Alice");
```

## Refactoring 3: Replace Raw HashMap with Typed Map

**Before:**
```java
Map cache = new HashMap();
cache.put("key", "value");
String val = (String) cache.get("key");
```

**After:**
```java
Map<String, String> cache = new HashMap<>();
cache.put("key", "value");
String val = cache.get("key");
```

## Refactoring 4: Extract Map Iteration

**Before:**
```java
for (Map.Entry<String, Integer> entry : map.entrySet()) {
    process(entry.getKey(), entry.getValue());
}
```

**After:**
```java
map.forEach(this::process);
```

## Refactoring 5: Replace Legacy Classes

**Before:**
```java
Vector<String> v = new Vector<>();
Enumeration<String> e = v.elements();
while (e.hasMoreElements()) {
    String s = e.nextElement();
}
```

**After:**
```java
List<String> list = new ArrayList<>();
for (String s : list) { ... }
```

## Refactoring 6: Add Generics to Legacy Collections

**Before:**
```java
public class StringList {
    private List items = new ArrayList();
    public void add(String s) { items.add(s); }
    public String get(int i) { return (String) items.get(i); }
}
```

**After:**
```java
public class StringList {
    private List<String> items = new ArrayList<>();
    public void add(String s) { items.add(s); }
    public String get(int i) { return items.get(i); }
}
```

## Refactoring 7: Use EnumMap for Enum Keys

**Before:**
```java
Map<Status, List<Order>> ordersByStatus = new HashMap<>();
for (Status s : Status.values()) {
    ordersByStatus.put(s, new ArrayList<>());
}
```

**After:**
```java
Map<Status, List<Order>> ordersByStatus = new EnumMap<>(Status.class);
for (Status s : Status.values()) {
    ordersByStatus.put(s, new ArrayList<>());
}
// More efficient: EnumMap uses array internally
```

## Refactoring Checklist

- [ ] Replace loops with `removeIf()`, `replaceAll()`, `forEach()`
- [ ] Replace `Vector`/`Hashtable`/`Stack` with modern equivalents
- [ ] Add generic type parameters
- [ ] Replace anonymous Comparator classes with lambda/comparator factories
- [ ] Use `computeIfAbsent()` instead of verbose `containsKey()` + `put()`
- [ ] Use `Map.merge()` for atomic upsert operations
- [ ] Use `List.of()`/`Set.of()` for known constants
- [ ] Use `stream.toList()` (Java 16+) instead of `collect(Collectors.toList())`
- [ ] Use `SequencedCollection` methods (`getFirst()`, `getLast()`) where appropriate
