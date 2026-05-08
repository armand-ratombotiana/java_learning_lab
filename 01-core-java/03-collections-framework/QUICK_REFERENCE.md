# 📌 Collections Framework - Quick Reference Sheet

Use this sheet for quick lookups while coding or studying.

---

## 🗂️ Collection Hierarchy

```
Iterable<T>
    │
    └─ Collection<T>
            │
            ├─ List<T>
            │     ├─ ArrayList     (resizable array)
            │     ├─ LinkedList    (doubly-linked)
            │     ├─ Vector        (synchronized)
            │     └─ Stack         (LIFO)
            │
            ├─ Set<T>
            │     ├─ HashSet       (hash table, O(1))
            │     ├─ LinkedHashSet (insertion order)
            │     ├─ TreeSet       (sorted, O(log n))
            │     └─ EnumSet       (enum values)
            │
            └─ Queue<T>
                  ├─ PriorityQueue  (heap-based)
                  ├─ Deque          (double-ended)
                  │     ├─ ArrayDeque
                  │     └─ LinkedList
                  └─ BlockingQueue (thread-safe)
```

---

## ⏱️ Time Complexity (Big-O)

| Operation | ArrayList | LinkedList | HashSet | TreeSet |
|-----------|-----------|------------|---------|---------|
| **Add** | O(1)* | O(1) | O(1) | O(log n) |
| **Remove** | O(n) | O(1)* | O(1) | O(log n) |
| **Get** | O(1) | O(n) | O(1) | O(log n) |
| **Search** | O(n) | O(n) | O(1) | O(log n) |
| **Sorted** | No | No | No | Yes |

*Amortized for ArrayList, O(1) at ends for LinkedList

---

## 🗺️ Map Hierarchy

```
Map<K,V>
    │
    ├─ HashMap        (O(1), allows null)
    ├─ LinkedHashMap  (insertion order)
    ├─ TreeMap        (sorted, O(log n))
    ├─ Hashtable      (synchronized, legacy)
    ├─ ConcurrentHashMap (thread-safe)
    └─ WeakHashMap    (weak keys, GC-friendly)
```

---

## 🔧 When to Use What

| Need | Use |
|------|-----|
| Fast random access | ArrayList |
| Frequent insert/delete | LinkedList |
| Unique elements | HashSet |
| Sorted unique elements | TreeSet |
| Key-value pairs | HashMap |
| Sorted keys | TreeMap |
| FIFO queue | LinkedList/ArrayDeque |
| Priority processing | PriorityQueue |

---

## 🔁 Common Operations

```java
// List operations
list.add(item);           // Append
list.add(index, item);   // Insert at position
list.remove(index);      // Remove by index
list.remove(object);     // Remove by object
list.contains(item);     // Search
list.get(index);         // Random access
list.set(index, item);   // Replace

// Set operations
set.add(item);           // Add (no duplicates)
set.remove(item);       // Remove
set.contains(item);     // Search

// Map operations
map.put(key, value);     // Add/update
map.get(key);           // Get value
map.containsKey(key);   // Check key
map.remove(key);        // Remove
map.keySet();           // All keys
map.values();           // All values
map.entrySet();         // All entries
```

---

## 🔀 Iterators

```java
// For-each (preferred)
for (String s : list) { }

// Iterator (mutable)
Iterator<String> it = list.iterator();
while (it.hasNext()) {
    String s = it.next();
    if (s.isEmpty()) it.remove();  // Safe removal
}

// ListIterator (bidirectional)
ListIterator<String> it = list.listIterator();
it.hasPrevious(); it.previous();
```

---

## 📊 Stream Operations

```java
// Filter
list.stream().filter(x -> x > 5).collect(...)

// Transform
list.stream().map(String::toUpperCase).collect(...)

// Aggregate
list.stream().count();
list.stream().reduce(0, Integer::sum);
list.stream().collect(Collectors.groupingBy(...));
```

---

## ⚠️ Common Pitfalls

1. **Modifying while iterating** - Use Iterator or copy first
2. **Using raw types** - Use `List<String>` not `List`
3. **Null keys/values** - HashMap allows, TreeMap doesn't
4. **Not overriding equals/hashCode** - For custom objects in sets/maps
5. **Choosing wrong collection** - Consider time complexity

---

## 🎯 Quick Tips

- Use `List.of()` for immutable lists (Java 9+)
- Use `Map.of()` for immutable maps (Java 9+)
- `Collections.emptyList()` vs `new ArrayList<>()`
- `Arrays.asList()` creates fixed-size list
- Always specify initial capacity for large lists to avoid resizing

---

**Remember**: "Choose the right collection for the job"