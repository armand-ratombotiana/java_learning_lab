# Collections — Interview Questions

## Beginner

### Q1: What is the difference between Collection and Collections?

`Collection` is the root interface of the collections hierarchy (List, Set, Queue extend it). `Collections` is a utility class with static methods (sort, binarySearch, unmodifiableList, etc.).

### Q2: Explain the difference between ArrayList and LinkedList.

ArrayList uses a dynamic array — O(1) random access, O(n) insertion/deletion at arbitrary positions. LinkedList uses doubly-linked nodes — O(n) random access, O(1) insertion/deletion at ends. Use ArrayList for most cases; LinkedList when frequently adding/removing from both ends.

### Q3: How does HashSet work internally?

HashSet is backed by a HashMap. Each element is stored as a HashMap key with a dummy value. Adding an element calls `map.put(e, PRESENT)`. contains() calls `map.containsKey()`. The hash table provides O(1) average-time operations.

## Intermediate

### Q4: What is the difference between HashMap and ConcurrentHashMap?

HashMap is not thread-safe. ConcurrentHashMap allows concurrent reads and striped writes (bucket-level locking). ConcurrentHashMap never throws ConcurrentModificationException during iteration (weakly consistent). It's the preferred choice for concurrent access.

### Q5: Explain Map.computeIfAbsent() with an example.

```java
// Without computeIfAbsent:
List<String> list = map.get(key);
if (list == null) {
    list = new ArrayList<>();
    map.put(key, list);
}
list.add(value);

// With computeIfAbsent:
map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
```

It atomically computes and inserts a value only if the key is absent.

### Q6: What is the contract between hashCode() and equals()?

If `a.equals(b)` is true, then `a.hashCode() == b.hashCode()` must be true. The converse is not required (hash collisions are allowed). Objects used as HashMap keys or added to HashSet must obey this contract. Violating it causes collections to behave incorrectly (e.g., contains() returns false for present elements).

## Advanced

### Q7: How does HashMap handle hash collisions in Java 8+?

When a bucket's linked list exceeds TREEIFY_THRESHOLD (8), the list converts to a red-black tree. This provides O(log n) worst-case performance instead of O(n). Degrades back to list when count drops below UNTREEIFY_THRESHOLD (6). Treeification only occurs if the table has at least MIN_TREEIFY_CAPACITY (64) — otherwise the table just resizes.

### Q8: What is the SequencedCollection interface (Java 21+)?

A new interface that provides uniform API for collections with defined encounter order:

```java
interface SequencedCollection<E> extends Collection<E> {
    E getFirst();
    E getLast();
    void addFirst(E);
    void addLast(E);
    E removeFirst();
    E removeLast();
}
```

Implemented by ArrayList, LinkedList, ArrayDeque, LinkedHashSet, TreeSet. Also SequencedMap for LinkedHashMap, TreeMap.

### Q9: How would you implement an LRU cache using standard Java collections?

```java
public class LRUCache<K,V> extends LinkedHashMap<K,V> {
    private final int maxSize;
    public LRUCache(int maxSize) {
        super(16, 0.75f, true);  // access-order = true
        this.maxSize = maxSize;
    }
    @Override
    protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
        return size() > maxSize;
    }
}
```

### Q10: Compare Arrays.asList(), List.of(), and Collections.unmodifiableList().

- `Arrays.asList()`: Returns fixed-size list backed by array. Not structurally immutable (can use set()), but cannot add/remove.
- `List.of()` (Java 9+): Fully immutable, rejects nulls, structurally compact.
- `Collections.unmodifiableList()`: Wrapper view of existing list. Backing list can still change. Not a true immutable.
