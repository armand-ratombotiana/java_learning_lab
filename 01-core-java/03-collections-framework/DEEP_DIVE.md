# 🔍 Collections Framework - Deep Dive

## Table of Contents
1. [Collections Framework Architecture](#collections-framework-architecture)
2. [List Implementations Deep Dive](#list-implementations-deep-dive)
3. [Set Implementations](#set-implementations)
4. [Map Implementations](#map-implementations)
5. [Queue Implementations](#queue-implementations)
6. [Performance & Thread-Safety](#performance--thread-safety)

---

## Collections Framework Architecture

### The Hierarchy

```
                    Iterable<E>
                        ▲
                        │
                   Collection<E>
                    ▲    ▲    ▲
                    │    │    │
                 List  Set  Queue
                 ▲     ▲     ▲
                 │     │     │
            ArrayList HashSet PriorityQueue
            LinkedList TreeSet
            CopyOnWriteArrayList
```

### Core Interfaces

```java
// ITERABLE: Can be iterated
public interface Iterable<E> {
    Iterator<E> iterator();
}

// COLLECTION: Basic operations
public interface Collection<E> extends Iterable<E> {
    boolean add(E e);
    boolean remove(Object o);
    boolean contains(Object o);
    int size();
    boolean isEmpty();
    void clear();
}

// LIST: Ordered, allows duplicates
public interface List<E> extends Collection<E> {
    E get(int index);
    E set(int index, E element);
    void add(int index, E element);
    E remove(int index);
    int indexOf(Object o);
}

// SET: Unique elements, no order guarantee
public interface Set<E> extends Collection<E> {
    // Same as Collection, but enforces uniqueness
}

// QUEUE: FIFO operations
public interface Queue<E> extends Collection<E> {
    boolean offer(E e);      // Add to tail
    E poll();                // Remove from head
    E peek();                // View head
}

// MAP: Key-value pairs
public interface Map<K, V> {
    V put(K key, V value);
    V get(Object key);
    V remove(Object key);
    boolean containsKey(Object key);
    Set<K> keySet();
    Collection<V> values();
}
```

### Iterator Pattern

```java
// Manual iteration
List<String> list = new ArrayList<>(Arrays.asList("A", "B", "C"));

Iterator<String> iterator = list.iterator();
while (iterator.hasNext()) {
    String element = iterator.next();
    System.out.println(element);
}

// Enhanced for-loop (uses Iterator internally)
for (String element : list) {
    System.out.println(element);
}

// Stream API (modern approach)
list.stream().forEach(System.out::println);
```

**Why Iterator?**
- Decouples collection from iteration logic
- Allows safe removal during iteration
- Supports different traversal strategies
- Enables lazy evaluation

---

## List Implementations Deep Dive

### ArrayList: Dynamic Array

**Internal Structure**:
```
┌─────────────────────────────────────────┐
│ ArrayList<String>                       │
├─────────────────────────────────────────┤
│ elementData: Object[] = [A, B, C, null] │
│ size: 3                                 │
│ capacity: 4                             │
└─────────────────────────────────────────┘
```

**How It Works**:
```java
ArrayList<String> list = new ArrayList<>();  // capacity = 10

list.add("A");  // size = 1
list.add("B");  // size = 2
list.add("C");  // size = 3
// ... add 7 more items
list.add("K");  // size = 10, capacity = 10

list.add("L");  // size = 11, capacity = 15 (resized!)
                // Old array copied to new larger array
```

**Resizing Algorithm**:
```
New Capacity = Old Capacity * 1.5 (rounded down)

Example:
Initial: 10
After 10 adds: 10 → 15 (10 * 1.5)
After 15 adds: 15 → 22 (15 * 1.5 = 22.5)
After 22 adds: 22 → 33 (22 * 1.5 = 33)
```

**Performance Characteristics**:
```
Operation      | Time Complexity | Notes
───────────────┼─────────────────┼──────────────────────
get(index)     | O(1)            | Direct array access
add(element)   | O(1)*           | * Amortized (resize rare)
add(index, e)  | O(n)            | Shift elements right
remove(index)  | O(n)            | Shift elements left
contains(o)    | O(n)            | Linear search
```

**Memory Layout**:
```
Stack:                  Heap:
┌──────────┐           ┌─────────────────────────┐
│ list ────┼──────────→│ ArrayList object        │
└──────────┘           ├─────────────────────────┤
                       │ elementData ────┐       │
                       │ size: 3         │       │
                       │ capacity: 10    │       │
                       └────────┬────────┘       │
                                │                │
                                ▼                │
                       ┌─────────────────────┐   │
                       │ Object[] array      │◄──┘
                       ├─────────────────────┤
                       │ [0]: "A"            │
                       │ [1]: "B"            │
                       │ [2]: "C"            │
                       │ [3-9]: null         │
                       └─────────────────────┘
```

### LinkedList: Node-Based

**Internal Structure**:
```
┌──────────────────────────────────────────┐
│ LinkedList<String>                       │
├──────────────────────────────────────────┤
│ first ──┐                                │
│ last ───┼──┐                             │
│ size: 3 │  │                             │
└────────┬┼──┼─────────────────────────────┘
         │   │
         ▼   ▼
    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
    │ Node        │    │ Node        │    │ Node        │
    ├─────────────┤    ├─────────────┤    ├─────────────┤
    │ prev: null  │    │ prev: ◄─────┼────┤ prev: ◄─────┼
    │ value: "A"  │    │ value: "B"  │    │ value: "C"  │
    │ next: ──────┼───→│ next: ──────┼───→│ next: null  │
    └─────────────┘    └─────────────┘    └─────────────┘
```

**Node Class**:
```java
private static class Node<E> {
    E item;
    Node<E> next;
    Node<E> prev;
    
    Node(Node<E> prev, E element, Node<E> next) {
        this.item = element;
        this.next = next;
        this.prev = prev;
    }
}
```

**Performance Characteristics**:
```
Operation      | Time Complexity | Notes
───────────────┼─────────────────┼──────────────────────
get(index)     | O(n)            | Must traverse from head/tail
add(element)   | O(1)            | Add to tail
add(index, e)  | O(n)            | Find position, then O(1) insert
remove(index)  | O(n)            | Find position, then O(1) remove
contains(o)    | O(n)            | Linear search
```

**When to Use LinkedList**:
- ✅ Frequent insertions/deletions at head/tail
- ✅ Implementing Stack or Queue
- ✅ Unknown size, frequent modifications
- ❌ Random access (use ArrayList instead)
- ❌ Memory-constrained (overhead per node)

### CopyOnWriteArrayList: Thread-Safe

**How It Works**:
```java
// On write: Create new array, copy all elements, add new element
public synchronized boolean add(E e) {
    Object[] elements = getArray();
    int len = elements.length;
    Object[] newElements = Arrays.copyOf(elements, len + 1);
    newElements[len] = e;
    setArray(newElements);
    return true;
}

// On read: No synchronization needed
public E get(int index) {
    return get(getArray(), index);  // No lock!
}
```

**Performance**:
```
Operation      | Time Complexity | Notes
───────────────┼─────────────────┼──────────────────────
get(index)     | O(1)            | No synchronization
add(element)   | O(n)            | Copy entire array
remove(index)  | O(n)            | Copy entire array
contains(o)    | O(n)            | Linear search
```

**When to Use**:
- ✅ Many reads, few writes
- ✅ Iteration-heavy workloads
- ✅ Safe iteration (no ConcurrentModificationException)
- ❌ Write-heavy workloads (expensive)
- ❌ Large collections (memory overhead)

---

## Set Implementations

### HashSet: Unordered, Fast

**Internal Structure**:
```
HashSet uses HashMap internally:

┌──────────────────────────────────┐
│ HashSet<String>                  │
├──────────────────────────────────┤
│ map: HashMap<E, Object>          │
│      (stores elements as keys)   │
└──────────────────────────────────┘
```

**Hash Table Internals**:
```
┌─────────────────────────────────────────┐
│ HashMap (backing store)                 │
├─────────────────────────────────────────┤
│ table: Node<K,V>[] (size = 16)          │
│ size: 3                                 │
│ loadFactor: 0.75                        │
│ threshold: 12 (16 * 0.75)               │
└─────────────────────────────────────────┘

Bucket Array:
┌────┬────┬────┬────┬────┬────┬────┬────┐
│ 0  │ 1  │ 2  │ 3  │ 4  │ 5  │ 6  │ 7  │
├────┼────┼────┼────┼────┼────┼────┼────┤
│null│ ●  │ ●  │null│ ●  │null│null│null│
└────┴─┬──┴─┬──┴────┴─┬──┴────┴────┴────┘
       │    │        │
       ▼    ▼        ▼
    "Apple" "Banana" "Cherry"
```

**Hash Function**:
```java
// Simplified
int hash(Object key) {
    int h = key.hashCode();
    return h ^ (h >>> 16);  // Mix high and low bits
}

int index = hash(key) & (table.length - 1);  // Modulo operation
```

**Collision Handling** (Chaining):
```
If two keys hash to same bucket:

┌────┐
│ 2  │
├────┤
│ ●  │
└─┬──┘
  │
  ▼
┌──────────────┐    ┌──────────────┐
│ Node         │    │ Node         │
├──────────────┤    ├──────────────┤
│ key: "Apple" │    │ key: "Apricot"
│ value: ...   │    │ value: ...   │
│ next: ───────┼───→│ next: null   │
└──────────────┘    └──────────────┘
```

**Performance**:
```
Operation      | Time Complexity | Notes
───────────────┼─────────────────┼──────────────────────
add(element)   | O(1)            | Average case
remove(element)| O(1)            | Average case
contains(o)    | O(1)            | Average case
               | O(n)            | Worst case (all collisions)
```

### TreeSet: Sorted, Navigable

**Internal Structure** (Red-Black Tree):
```
TreeSet uses TreeMap internally:

        ┌─────────┐
        │    B    │ (Black)
        └────┬────┘
            / \
           /   \
          /     \
    ┌────▼──┐  ┌─▼────┐
    │   A   │  │   D   │ (Red)
    └───────┘  └─┬────┘
                / \
               /   \
          ┌───▼──┐ ┌─▼───┐
          │   C  │ │  E  │ (Black)
          └──────┘ └─────┘
```

**Properties**:
- Every node is either red or black
- Root is always black
- Red nodes have black children
- All paths have same number of black nodes
- Maintains O(log n) operations

**Performance**:
```
Operation      | Time Complexity | Notes
───────────────┼─────────────────┼──────────────────────
add(element)   | O(log n)        | Tree rebalancing
remove(element)| O(log n)        | Tree rebalancing
contains(o)    | O(log n)        | Binary search
first/last     | O(log n)        | Navigate tree
```

**Navigable Operations**:
```java
TreeSet<Integer> set = new TreeSet<>(Arrays.asList(1, 3, 5, 7, 9));

set.first();           // 1
set.last();            // 9
set.lower(5);          // 3 (greatest < 5)
set.floor(5);          // 5 (greatest <= 5)
set.ceiling(5);        // 5 (least >= 5)
set.higher(5);         // 7 (least > 5)
set.headSet(5);        // [1, 3] (all < 5)
set.tailSet(5);        // [5, 7, 9] (all >= 5)
set.subSet(3, 7);      // [3, 5] (3 <= x < 7)
```

---

## Map Implementations

### HashMap: Unordered, Fast

**Structure**: Same as HashSet (uses hash table)

**Key Operations**:
```java
HashMap<String, Integer> map = new HashMap<>();

map.put("Alice", 30);
map.put("Bob", 25);

map.get("Alice");           // 30
map.getOrDefault("Charlie", 0);  // 0
map.putIfAbsent("Charlie", 35);  // Add if not present
map.replace("Alice", 31);   // Update existing
map.remove("Bob");          // Remove
```

**Null Handling**:
```java
HashMap<String, Integer> map = new HashMap<>();

map.put(null, 0);           // Allowed! One null key
map.put("key", null);       // Allowed! Null values
map.get(null);              // 0

// But be careful:
map.put(null, 1);           // Overwrites previous null key
```

**Performance**:
```
Operation      | Time Complexity | Notes
───────────────┼─────────────────┼──────────────────────
put(k, v)      | O(1)            | Average case
get(key)       | O(1)            | Average case
remove(key)    | O(1)            | Average case
containsKey(k) | O(1)            | Average case
               | O(n)            | Worst case (collisions)
```

### TreeMap: Sorted, Navigable

**Structure**: Red-Black Tree (same as TreeSet)

**Key Operations**:
```java
TreeMap<String, Integer> map = new TreeMap<>();

map.put("Alice", 30);
map.put("Bob", 25);
map.put("Charlie", 35);

map.firstKey();             // "Alice"
map.lastKey();              // "Charlie"
map.lowerKey("Bob");        // "Alice"
map.higherKey("Bob");       // "Charlie"
map.headMap("Bob");         // {Alice=30}
map.tailMap("Bob");         // {Bob=25, Charlie=35}
map.subMap("Alice", "Charlie");  // {Alice=30, Bob=25}
```

**Performance**:
```
Operation      | Time Complexity | Notes
───────────────┼─────────────────┼──────────────────────
put(k, v)      | O(log n)        | Tree rebalancing
get(key)       | O(log n)        | Binary search
remove(key)    | O(log n)        | Tree rebalancing
containsKey(k) | O(log n)        | Binary search
```

### ConcurrentHashMap: Thread-Safe

**Segmentation Strategy**:
```
Instead of one lock for entire map, use multiple locks:

┌─────────────────────────────────────────┐
│ ConcurrentHashMap                       │
├─────────────────────────────────────────┤
│ segments: Segment[] (default 16)        │
└─────────────────────────────────────────┘

Each segment has its own lock:

Segment 0 ──┐
Segment 1 ──┼─ Lock 0
Segment 2 ──┘

Segment 3 ──┐
Segment 4 ──┼─ Lock 1
Segment 5 ──┘

...

This allows concurrent writes to different segments!
```

**Performance**:
```
Operation      | Time Complexity | Notes
───────────────┼─────────────────┼──────────────────────
put(k, v)      | O(1)            | Segment-level lock
get(key)       | O(1)            | No lock (volatile read)
remove(key)    | O(1)            | Segment-level lock
containsKey(k) | O(1)            | No lock
```

**Atomic Operations**:
```java
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

// Atomic: Check and put
map.putIfAbsent("key", 1);

// Atomic: Compute if absent
map.computeIfAbsent("key", k -> k.length());

// Atomic: Compute and replace
map.compute("key", (k, v) -> v == null ? 1 : v + 1);

// Atomic: Replace if present
map.replace("key", 1, 2);
```

---

## Queue Implementations

### PriorityQueue: Heap-Based

**Internal Structure** (Min-Heap):
```
        ┌─────┐
        │  1  │ (smallest)
        └──┬──┘
          / \
         /   \
    ┌───▼─┐ ┌─▼───┐
    │  2  │ │  3  │
    └──┬──┘ └─────┘
      / \
  ┌──▼─┐ ┌─────┐
  │ 4  │ │  5  │
  └────┘ └─────┘

Array representation: [1, 2, 3, 4, 5]
```

**Heap Properties**:
```
For index i:
- Parent: (i - 1) / 2
- Left child: 2*i + 1
- Right child: 2*i + 2

Parent always <= children (min-heap)
```

**Operations**:
```java
PriorityQueue<Integer> pq = new PriorityQueue<>();

pq.offer(5);    // Add: O(log n)
pq.offer(3);
pq.offer(7);
pq.offer(1);

pq.peek();      // View min: O(1) → 1
pq.poll();      // Remove min: O(log n) → 1
pq.poll();      // → 3
pq.poll();      // → 5
pq.poll();      // → 7
```

**Custom Comparator**:
```java
// Max-heap (reverse order)
PriorityQueue<Integer> maxHeap = 
    new PriorityQueue<>((a, b) -> b - a);

maxHeap.offer(5);
maxHeap.offer(3);
maxHeap.offer(7);

maxHeap.poll();  // 7
maxHeap.poll();  // 5
maxHeap.poll();  // 3
```

### BlockingQueue: Producer-Consumer

**Blocking Operations**:
```java
BlockingQueue<String> queue = new LinkedBlockingQueue<>(10);

// Put: Blocks if queue is full
queue.put("item");

// Take: Blocks if queue is empty
String item = queue.take();

// Timed operations
queue.offer("item", 1, TimeUnit.SECONDS);  // Wait up to 1 second
String item = queue.poll(1, TimeUnit.SECONDS);
```

**Producer-Consumer Pattern**:
```java
BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(5);

// Producer thread
new Thread(() -> {
    for (int i = 0; i < 10; i++) {
        try {
            queue.put(i);  // Blocks if queue full
            System.out.println("Produced: " + i);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}).start();

// Consumer thread
new Thread(() -> {
    try {
        while (true) {
            Integer item = queue.take();  // Blocks if queue empty
            System.out.println("Consumed: " + item);
        }
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
}).start();
```

---

## Performance & Thread-Safety

### Performance Comparison

```
                 | Add | Remove | Get | Space | Ordered
─────────────────┼─────┼────────┼─────┼───────┼─────────
ArrayList        | O(1)| O(n)   | O(1)| O(n)  | Index
LinkedList       | O(1)| O(1)*  | O(n)| O(n)  | Index
HashSet          | O(1)| O(1)   | O(1)| O(n)  | No
TreeSet          |O(ln)| O(ln)  |O(ln)| O(n)  | Sorted
HashMap          | O(1)| O(1)   | O(1)| O(n)  | No
TreeMap          |O(ln)| O(ln)  |O(ln)| O(n)  | Sorted
ConcurrentHashMap| O(1)| O(1)   | O(1)| O(n)  | No

* Head/tail only
```

### Thread-Safety Strategies

```
┌──────────────────────────────────────────────────────┐
│ Thread-Safety Options                                │
├──────────────────────────────────────────────────────┤
│                                                      │
│ 1. Synchronized Collections (Slow)                  │
│    Collections.synchronizedList(list)               │
│    Collections.synchronizedSet(set)                 │
│    Collections.synchronizedMap(map)                 │
│                                                      │
│ 2. Concurrent Collections (Fast)                    │
│    CopyOnWriteArrayList (read-heavy)                │
│    ConcurrentHashMap (write-heavy)                  │
│    ConcurrentSkipListMap (sorted)                   │
│                                                      │
│ 3. Manual Synchronization (Flexible)                │
│    synchronized (lock) { ... }                      │
│    ReentrantLock                                    │
│                                                      │
└──────────────────────────────────────────────────────┘
```

### Choosing the Right Collection

```
Need ordered access?
├─ Yes → Use List
│   ├─ Frequent random access? → ArrayList
│   ├─ Frequent insertions/deletions? → LinkedList
│   └─ Thread-safe reads? → CopyOnWriteArrayList
│
└─ No → Use Set or Map
    ├─ Need uniqueness?
    │   ├─ Yes → Use Set
    │   │   ├─ Need sorting? → TreeSet
    │   │   └─ Need speed? → HashSet
    │   │
    │   └─ No → Use Map
    │       ├─ Need key-value pairs?
    │       │   ├─ Yes → Use Map
    │       │   │   ├─ Need sorting? → TreeMap
    │       │   │   ├─ Need thread-safety? → ConcurrentHashMap
    │       │   │   └─ Need speed? → HashMap
    │       │   │
    │       │   └─ No → Use Queue
    │       │       ├─ Need priority? → PriorityQueue
    │       │       └─ Need blocking? → BlockingQueue
```

---

## Key Takeaways

### Memory Considerations
- ArrayList: Contiguous memory, cache-friendly
- LinkedList: Scattered memory, pointer overhead
- HashSet/HashMap: Hash table overhead
- TreeSet/TreeMap: Tree node overhead

### Performance Tradeoffs
- Fast access (O(1)): ArrayList, HashMap, HashSet
- Fast insertion/deletion: LinkedList, TreeSet, TreeMap
- Sorted access: TreeSet, TreeMap
- Thread-safe: ConcurrentHashMap, CopyOnWriteArrayList

### Design Principles
- Use interfaces (List, Set, Map, Queue)
- Choose implementation based on use case
- Consider thread-safety requirements
- Measure performance with real data
- Avoid premature optimization

---

**Next**: Study QUIZZES.md to test your understanding!