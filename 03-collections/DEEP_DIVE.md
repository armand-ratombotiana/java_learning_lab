# Java Collections - Deep Dive

## Table of Contents

1. [Collection Hierarchy](#collection-hierarchy)
2. [List Implementations](#list-implementations)
3. [Set Implementations](#set-implementations)
4. [Map Implementations](#map-implementations)
5. [Queue Implementations](#queue-implementations)
6. [Performance Comparison](#performance-comparison)
7. [Choosing the Right Collection](#choosing-the-right-collection)

---

## Collection Hierarchy

```
Iterable
├── Collection
│   ├── List (ordered, indexed)
│   │   ├── ArrayList
│   │   ├── LinkedList
│   │   └── Vector
│   ├── Set (unique elements)
│   │   ├── HashSet
│   │   ├── LinkedHashSet
│   │   └── TreeSet
│   └── Queue (FIFO)
│       ├── PriorityQueue
│       ├── ArrayDeque
│       └── Deque
└── Map (key-value)
    ├── HashMap
    ├── LinkedHashMap
    ├── TreeMap
    └── Hashtable
```

---

## List Implementations

### ArrayList

```java
List<String> list = new ArrayList<>();

// Adding elements
list.add("Apple");
list.add(0, "Banana");  // Insert at index
list.addAll(Arrays.asList("Cherry", "Date"));

// Accessing
String first = list.get(0);
int size = list.size();
boolean hasApple = list.contains("Apple");

// Removing
list.remove("Banana");  // By object
list.remove(0);  // By index
list.removeIf(s -> s.startsWith("A"));  // Lambda

// Searching
int index = list.indexOf("Apple");
int lastIndex = list.lastIndexOf("Apple");

// Sublist
List<String> sub = list.subList(1, 3);

// Iteration
for (String s : list) { }
list.forEach(System.out::println);
Iterator<String> it = list.iterator();

// Streams (Module 4)
list.stream()
    .filter(s -> s.length() > 4)
    .forEach(System.out::println);
```

### LinkedList

```java
LinkedList<String> linked = new LinkedList<>();

// Queue operations
linked.offer("First");  // Add to end
linked.poll();  // Remove from front
linked.peek();  // View front

// Deque operations
linked.addFirst("Start");
linked.addLast("End");
linked.removeFirst();
linked.removeLast();

// List operations
linked.get(0);
linked.add(1, "Middle");

// Performance: O(1) at ends, O(n) at middle
```

### Vector vs ArrayList

```java
// Vector is synchronized (thread-safe)
Vector<String> vector = new Vector<>();

// ArrayList is not synchronized (faster)
ArrayList<String> arrayList = new ArrayList<>();

// For thread-safe ArrayList:
List<String> syncList = Collections.synchronizedList(new ArrayList<>());
```

---

## Set Implementations

### HashSet

```java
Set<String> set = new HashSet<>();

set.add("Apple");
set.add("Banana");
set.add("Apple");  // Ignored - duplicates not allowed

// Set operations
Set<String> a = Set.of("A", "B", "C");
Set<String> b = Set.of("B", "C", "D");

Set<String> union = new HashSet<>(a);
union.addAll(b);  // Union: A, B, C, D

Set<String> intersection = new HashSet<>(a);
intersection.retainAll(b);  // Intersection: B, C

Set<String> difference = new HashSet<>(a);
difference.removeAll(b);  // Difference: A

// HashSet uses hashCode() - must override for custom objects
```

### LinkedHashSet

```java
LinkedHashSet<String> linkedSet = new LinkedHashSet<>();

linkedSet.add("One");
linkedSet.add("Two");
linkedSet.add("Three");
linkedSet.add("One");  // Ignored

// Maintains insertion order
linkedSet.forEach(System.out::println);  // One, Two, Three
```

### TreeSet

```java
TreeSet<Integer> tree = new TreeSet<>();

tree.add(5);
tree.add(2);
tree.add(8);
tree.add(1);

// Sorted automatically: 1, 2, 5, 8

// NavigableSet methods
Integer first = tree.first();  // 1
Integer last = tree.last();    // 8
Integer higher = tree.higher(2);  // 5
Integer lower = tree.lower(5);    // 2

// Range operations
SortedSet<Integer> head = tree.headSet(5);  // < 5
SortedSet<Integer> tail = tree.tailSet(5);  // >= 5
SortedSet<Integer> sub = tree.subSet(2, 8);  // [2, 8)

// Custom ordering
TreeSet<Person> people = new TreeSet<>(Comparator.comparing(Person::getName));
```

---

## Map Implementations

### HashMap

```java
Map<String, Integer> map = new HashMap<>();

// Adding entries
map.put("Apple", 1);
map.put("Banana", 2);
map.putIfAbsent("Cherry", 3);  // Only if not exists

// Accessing
int value = map.get("Apple");           // 1
int defaultValue = map.getOrDefault("Grape", 0);  // 0
boolean hasKey = map.containsKey("Apple");
boolean hasValue = map.containsValue(1);

// Updating
map.put("Apple", 10);  // Overwrites
map.merge("Apple", 5, Integer::sum);  // Adds to existing
map.compute("Apple", (k, v) -> v == null ? 1 : v * 2);  // Compute new value

// Removing
map.remove("Banana");
map.remove("Apple", 1);  // Only if value matches

// Bulk operations
map.putAll(otherMap);
map.replaceAll((k, v) -> v * 2);

// Iteration
for (Map.Entry<String, Integer> entry : map.entrySet()) {
    System.out.println(entry.getKey() + ": " + entry.getValue());
}

for (String key : map.keySet()) { }
for (Integer value : map.values()) { }

// Java 8+ forEach
map.forEach((k, v) -> System.out.println(k + ": " + v));
```

### LinkedHashMap

```java
LinkedHashMap<Integer, String> linkedMap = new LinkedHashMap<>();

linkedMap.put(1, "One");
linkedMap.put(2, "Two");
linkedMap.put(3, "Three");

// Maintains insertion order
```

### TreeMap

```java
TreeMap<String, Integer> treeMap = new TreeMap<>();

treeMap.put("Banana", 2);
treeMap.put("Apple", 1);
treeMap.put("Cherry", 3);

// Sorted by keys: Apple, Banana, Cherry

// NavigableMap methods
String firstKey = treeMap.firstKey();
String lowerKey = treeMap.lowerKey("Cherry");  // Banana
String floorKey = treeMap.floorKey("Cherry");  // Cherry
String ceilingKey = treeMap.ceilingKey("Cherry");  // Cherry
String higherKey = treeMap.higherKey("Cherry");  // null

Map<String, Integer> head = treeMap.headMap("Cherry");  // < Cherry
Map<String, Integer> tail = treeMap.tailMap("Cherry");  // >= Cherry
```

### Hashtable

```java
Hashtable<String, Integer> table = new Hashtable<>();

table.put("One", 1);
table.put("Two", 2);

// Thread-safe but doesn't allow null keys/values
// table.put(null, 1);  // Throws NPE
```

---

## Queue Implementations

### PriorityQueue

```java
PriorityQueue<Integer> pq = new PriorityQueue<>();

pq.offer(5);
pq.offer(2);
pq.offer(8);
pq.offer(1);

// Always returns smallest (min-heap by default)
pq.poll();  // Returns 1
pq.poll();  // Returns 2

// Max-heap
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
maxHeap.offer(5);
maxHeap.offer(2);
maxHeap.poll();  // Returns 5

// Custom comparator
PriorityQueue<Person> personQueue = new PriorityQueue<>(
    Comparator.comparing(Person::getAge)
);
```

### ArrayDeque

```java
ArrayDeque<String> deque = new ArrayDeque<>();

// Queue operations (FIFO)
deque.offer("First");
deque.offer("Second");
deque.poll();  // "First"

// Deque operations (both ends)
deque.addFirst("Start");
deque.addLast("End");
deque.removeFirst();
deque.removeLast();

// Stack operations (LIFO)
deque.push("Item");  // addFirst
deque.pop();         // removeFirst
deque.peek();        // first element
```

### BlockingQueue

```java
BlockingQueue<String> queue = new ArrayBlockingQueue<>(5);

// Puts block if full
queue.put("Item");  // Waits if full

// Offers return boolean or timeout
queue.offer("Item", 1, TimeUnit.SECONDS);

// Takes block if empty
String item = queue.take();

// Polls with timeout
String timeout = queue.poll(1, TimeUnit.SECONDS);
```

---

## Performance Comparison

### List Operations (Big-O)

| Operation | ArrayList | LinkedList |
|-----------|-----------|------------|
| get(index) | O(1) | O(n) |
| add(element) | O(1) amortized | O(1) |
| add(index, element) | O(n) | O(1) |
| remove(index) | O(n) | O(1) |
| contains | O(n) | O(n) |
| iterator.remove | O(1) | O(1) |

### Map Operations (Big-O)

| Operation | HashMap | LinkedHashMap | TreeMap |
|-----------|---------|---------------|---------|
| get(key) | O(1) | O(1) | O(log n) |
| put(key, value) | O(1) | O(1) | O(log n) |
| containsKey | O(1) | O(1) | O(log n) |
| containsValue | O(n) | O(n) | O(n) |
| iteration | O(n) | O(n) | O(n) |

### Set Operations (Big-O)

| Operation | HashSet | LinkedHashSet | TreeSet |
|-----------|---------|---------------|---------|
| add | O(1) | O(1) | O(log n) |
| remove | O(1) | O(1) | O(log n) |
| contains | O(1) | O(1) | O(log n) |

---

## Choosing the Right Collection

### Decision Tree

```
Need ordering?
├── No → HashMap/HashSet
├── Insertion order → LinkedHashMap/LinkedHashSet
└── Sorted → TreeMap/TreeSet

Need thread safety?
├── Yes → ConcurrentHashMap, CopyOnWriteArrayList
└── No → Regular collections

Need FIFO queue?
├── Yes → ArrayDeque (general), PriorityQueue (sorted)
└── No → Stack or ArrayList

Need blocking behavior?
└── BlockingQueue implementations
```

### Common Use Cases

| Use Case | Best Choice |
|----------|-------------|
| Frequent lookups by key | HashMap |
| Maintaining order | LinkedHashMap |
| Sorted keys | TreeMap |
| Unique elements | HashSet |
| Sorted unique elements | TreeSet |
| LIFO stack | ArrayDeque |
| FIFO queue | ArrayDeque |
| Priority processing | PriorityQueue |
| Thread-safe list | CopyOnWriteArrayList |
| Thread-safe map | ConcurrentHashMap |

---

## Utility Methods

### Collections Utility

```java
// Sorting
List<Integer> list = new ArrayList<>();
Collections.sort(list);
Collections.sort(list, Comparator.reverseOrder());

// Searching (requires sorted list)
int index = Collections.binarySearch(list, target);

// Shuffling
Collections.shuffle(list);

// Reversing
Collections.reverse(list);

// Filling
Collections.fill(list, "X");

// Copying
Collections.copy(dest, source);

// Finding extremes
Collections.min(collection);
Collections.max(collection);

// Frequency
int count = Collections.frequency(list, element);

// Disjoint
boolean disjoint = Collections.disjoint(col1, col2);

// Empty collections
List<String> empty = Collections.emptyList();
Map<K, V> emptyMap = Collections.emptyMap();
Set<V> emptySet = Collections.emptySet();

// Singleton collections
Set<String> single = Collections.singleton("One");
List<String> singleList = Collections.singletonList("One");
Map<K, V> singletonMap = Collections.singletonMap(key, value);
```

### Arrays Utility

```java
int[] arr = {5, 2, 8, 1, 9};

// Sorting
Arrays.sort(arr);
Arrays.sort(arr, Comparator.reverseOrder());
Arrays.parallelSort(arr);  // Parallel sorting

// Searching (requires sorted array)
int index = Arrays.binarySearch(arr, target);

// Filling
Arrays.fill(arr, 0);

// Copying
int[] copy = Arrays.copyOf(arr, newLength);
int[] range = Arrays.copyOfRange(arr, from, to);

// Comparison
boolean equal = Arrays.equals(arr1, arr2);

// String representation
String str = Arrays.toString(arr);
String deep = Arrays.deepToString(matrix);

// As List
List<Integer> list = Arrays.asList(1, 2, 3);
```

---

## Concurrent Collections

```java
// ConcurrentHashMap
ConcurrentHashMap<String, Integer> concurrentMap = new ConcurrentHashMap<>();
concurrentMap.putIfAbsent("key", 1);
concurrentMap.computeIfAbsent("key", k -> 1);

// CopyOnWriteArrayList
CopyOnWriteArrayList<String> copyOnWrite = new CopyOnWriteArrayList<>();
copyOnWrite.addIfAbsent("element");

// ConcurrentLinkedQueue
ConcurrentLinkedQueue<String> concurrentQueue = new ConcurrentLinkedQueue<>();
concurrentQueue.offer("item");
String item = concurrentQueue.poll();

// BlockingQueue implementations
LinkedBlockingQueue<String> blockingQueue = new LinkedBlockingQueue<>();
ArrayBlockingQueue<String> arrayBlockingQueue = new ArrayBlockingQueue<>(100);
PriorityBlockingQueue<String> priorityBlockingQueue = new PriorityBlockingQueue<>();
```
