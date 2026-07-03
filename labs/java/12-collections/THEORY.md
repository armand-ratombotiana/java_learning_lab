# Collections — Theoretical Foundation

## Core Interfaces

### Collection Hierarchy

```
Iterable
 └── Collection
      ├── List (ordered, allows duplicates)
      │    ├── ArrayList (resizable array)
      │    └── LinkedList (doubly-linked list)
      ├── Set (no duplicates)
      │    ├── HashSet (hash table)
      │    ├── LinkedHashSet (hash + linked list)
      │    └── TreeSet (red-black tree)
      ├── Queue (FIFO processing)
      │    ├── LinkedList (also a Queue)
      │    ├── PriorityQueue (heap-based)
      │    └── ArrayDeque (resizable array)
      └── Deque (double-ended queue)
           └── ArrayDeque, LinkedList

 Map (not extending Collection)
  ├── HashMap (hash table)
  ├── LinkedHashMap (hash + linked list)
  ├── TreeMap (red-black tree)
  └── EnumMap (for enum keys)
```

## List Interface

Ordered collection (sequence). May contain duplicates. Access by index.

```java
List<String> list = new ArrayList<>();
list.add("a");     // [a]
list.add(0, "b");  // [b, a]
list.get(0);       // b
list.set(0, "c");  // [c, a]
list.remove(0);    // [a]
```

### List Implementations

| Feature | ArrayList | LinkedList |
|---------|-----------|------------|
| Internal | Resizable array | Doubly-linked nodes |
| get(index) | O(1) | O(n) |
| add(index) | O(n) amortized | O(1) at ends, O(n) middle |
| remove(index) | O(n) | O(n) |
| Memory | Less (contiguous) | More (node overhead) |
| Iteration | Fast | Slower (pointer chasing) |

## Set Interface

No duplicates. `equals()` determines equality.

```java
Set<String> set = new HashSet<>();
set.add("a");
set.add("a");  // No effect — already present
set.contains("a");  // true
```

### Set Implementations

| Feature | HashSet | LinkedHashSet | TreeSet |
|---------|---------|---------------|---------|
| Ordering | None | Insertion order | Sorted (Comparable/Comparator) |
| Performance | O(1) | O(1) | O(log n) |
| Null | One null | One null | No null (NullPointerException) |
| Comparison | hashCode/equals | hashCode/equals | compareTo/compare |

## Map Interface

Key-value mappings. No duplicate keys.

```java
Map<String, Integer> map = new HashMap<>();
map.put("a", 1);
map.get("a");           // 1
map.getOrDefault("b", 0);  // 0
map.containsKey("a");   // true
map.forEach((k, v) -> System.out.println(k + "=" + v));
```

### Map Implementations

| Feature | HashMap | LinkedHashMap | TreeMap |
|---------|---------|---------------|---------|
| Ordering | None | Insertion/access order | Sorted by keys |
| Performance | O(1) | O(1) | O(log n) |
| Null keys | One | One | No |
| Use case | General | LRU cache, ordered iteration | Sorted operations |

## Queue Interface

For holding elements prior to processing. Typically FIFO.

```java
Queue<String> queue = new LinkedList<>();
queue.offer("a");  // Enqueue
queue.offer("b");
queue.poll();       // "a" (dequeue)
queue.peek();       // "b" (don't remove)
```

## Deque Interface

Double-ended queue. Supports insertion/removal at both ends.

```java
Deque<String> deque = new ArrayDeque<>();
deque.addFirst("a");
deque.addLast("b");
deque.removeFirst();  // "a"
```

## Collections Utility Class

```java
Collections.sort(list);
Collections.binarySearch(list, "key");
Collections.reverse(list);
Collections.shuffle(list);
Collections.unmodifiableList(list);
Collections.synchronizedList(list);
Collections.min(collection);
Collections.max(collection);
Collections.frequency(collection, element);
Collections.disjoint(collection1, collection2);
```

## Unmodifiable Collections

```java
List<String> immutable = List.of("a", "b", "c");
Set<String> immutableSet = Set.of("a", "b");
Map<String, Integer> immutableMap = Map.of("a", 1, "b", 2);

// Or from existing:
List<String> unmod = Collections.unmodifiableList(modifiableList);
```

These throw `UnsupportedOperationException` on modification attempts.
