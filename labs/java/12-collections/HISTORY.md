# Collections — Evolution Across Java Versions

## Java 1.0–1.1 (1996–1997): Legacy Classes

- `Vector` — synchronized dynamic array
- `Hashtable` — synchronized hash table
- `Stack` — extends Vector (inheritance abuse)
- `Enumeration` — iteration interface (two methods: `hasMoreElements()`, `nextElement()`)

No unified framework. Each class had its own API.

## Java 1.2 (1998): Collections Framework Introduced

Massive addition to the standard library:
- **Core interfaces**: `Collection`, `List`, `Set`, `Map`, `Iterator`
- **Implementations**: `ArrayList`, `LinkedList`, `HashSet`, `TreeSet`, `HashMap`, `TreeMap`, `Vector` retrofitted to implement `List`
- **Utility class**: `Collections` with algorithms (sort, binarySearch, reverse, shuffle)
- **Wrappers**: `synchronizedList()`, `unmodifiableList()`

## Java 1.4 (2002): LinkedHashSet, LinkedHashMap

- `LinkedHashSet` and `LinkedHashMap` — maintain insertion order with predictable iteration
- `IdentityHashMap` — uses reference equality (`==`) instead of `.equals()`

## Java 5 (2004): Generics Integration

Collections were retrofitted with generics:
```java
List<String> list = new ArrayList<>();  // Type-safe
```
- `Queue` interface added
- `PriorityQueue` — heap-based priority queue
- `ConcurrentHashMap`, `CopyOnWriteArrayList`, `ConcurrentLinkedQueue` in `java.util.concurrent`

## Java 6 (2006): Deque, NavigableSet, NavigableMap

- `Deque` (double-ended queue) and `ArrayDeque` — faster than `Stack` and `LinkedList` for stack/queue
- `NavigableSet` / `NavigableMap` — `TreeSet`/`TreeMap` enhanced with navigation methods

## Java 7 (2011): Fork/Join, TransferQueue

- `TransferQueue` and `LinkedTransferQueue` — producer/consumer handoff

## Java 8 (2014): Stream Integration

- `Collection.stream()` and `Collection.parallelStream()`
- `Map.forEach()`, `Map.replaceAll()`, `Map.computeIfAbsent()`
- `Map.merge()` — atomic upsert
- `Map.getOrDefault()` — safe default retrieval
- `Comparator` functional interface with factory methods

## Java 9 (2017): Immutable Collections Factory

```java
List.of("a", "b", "c");       // Immutable list
Set.of("a", "b", "c");        // Immutable set
Map.of("k1", "v1", "k2", "v2"); // Immutable map
Map.ofEntries(Map.entry("k", "v")); // For many entries
```

These are structurally immutable (not wrappers) and reject nulls.

## Java 10 (2018): Collectors.toUnmodifiableList()

```java
List<String> immutable = stream.collect(Collectors.toUnmodifiableList());
```

## Java 16 (2021): Stream.toList()

```java
List<String> immutable = stream.toList();  // Direct, no collector needed
```

## Java 21 (2023): SequencedCollection

```java
interface SequencedCollection<E> extends Collection<E> {
    E getFirst();
    E getLast();
    void addFirst(E);
    void addLast(E);
    E removeFirst();
    E removeLast();
}

interface SequencedSet<E> extends SequencedCollection<E>, Set<E> {}
interface SequencedMap<K,V> extends Map<K,V> {}
```

`LinkedHashSet`, `TreeSet`, `ArrayList`, `ArrayDeque` implement `SequencedCollection`. Provides uniform API for ordered collections.
