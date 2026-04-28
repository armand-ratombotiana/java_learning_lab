# ⚠️ Collections Framework - Edge Cases & Pitfalls

## Table of Contents
1. [List Edge Cases](#list-edge-cases)
2. [Set Edge Cases](#set-edge-cases)
3. [Map Edge Cases](#map-edge-cases)
4. [Queue Edge Cases](#queue-edge-cases)
5. [Thread-Safety Pitfalls](#thread-safety-pitfalls)
6. [Performance Pitfalls](#performance-pitfalls)
7. [Memory & GC Issues](#memory--gc-issues)

---

## List Edge Cases

### Pitfall 1: ArrayList Capacity Explosion

**Problem**: Repeatedly adding to ArrayList without knowing capacity

```java
// ❌ WRONG: Inefficient
ArrayList<Integer> list = new ArrayList<>();
for (int i = 0; i < 1000000; i++) {
    list.add(i);  // Resizes multiple times
}

// Timeline:
// 0-10: capacity = 10
// 11-15: capacity = 15 (resize)
// 16-22: capacity = 22 (resize)
// 23-33: capacity = 33 (resize)
// ... (many more resizes)
// Total resizes: ~20, copying millions of elements

// ✅ CORRECT: Preallocate capacity
ArrayList<Integer> list = new ArrayList<>(1000000);
for (int i = 0; i < 1000000; i++) {
    list.add(i);  // No resizing needed
}

// Benchmark:
// Without preallocate: ~500ms
// With preallocate: ~50ms (10x faster!)
```

**Prevention**:
- Estimate size if known
- Use `ensureCapacity()` if adding many elements
- Monitor memory usage for large collections

---

### Pitfall 2: LinkedList Head/Tail Confusion

**Problem**: Using LinkedList for random access

```java
// ❌ WRONG: O(n) for each access
LinkedList<String> list = new LinkedList<>(
    Arrays.asList("A", "B", "C", "D", "E")
);

for (int i = 0; i < list.size(); i++) {
    String item = list.get(i);  // O(n) each time!
}
// Total: O(n²) - very slow!

// ✅ CORRECT: Use iterator
for (String item : list) {
    // O(1) per iteration
}

// ✅ CORRECT: Use ArrayList for random access
ArrayList<String> list = new ArrayList<>(
    Arrays.asList("A", "B", "C", "D", "E")
);

for (int i = 0; i < list.size(); i++) {
    String item = list.get(i);  // O(1) each time
}

// Benchmark (1000 elements):
// LinkedList with get(): ~500ms
// LinkedList with iterator: ~1ms
// ArrayList with get(): ~1ms
```

**Prevention**:
- Use LinkedList only for head/tail operations
- Use ArrayList for random access
- Use iterator for sequential access

---

### Pitfall 3: CopyOnWriteArrayList Write Overhead

**Problem**: Using CopyOnWriteArrayList for write-heavy workloads

```java
// ❌ WRONG: Write-heavy workload
CopyOnWriteArrayList<Integer> list = new CopyOnWriteArrayList<>();

long start = System.nanoTime();
for (int i = 0; i < 10000; i++) {
    list.add(i);  // Copies entire array each time!
}
long duration = System.nanoTime() - start;
// Duration: ~5000ms (very slow!)

// ✅ CORRECT: Use ArrayList for writes
ArrayList<Integer> list = new ArrayList<>();

long start = System.nanoTime();
for (int i = 0; i < 10000; i++) {
    list.add(i);  // O(1) amortized
}
long duration = System.nanoTime() - start;
// Duration: ~10ms (500x faster!)

// ✅ CORRECT: Use CopyOnWriteArrayList for read-heavy
CopyOnWriteArrayList<Integer> list = new CopyOnWriteArrayList<>();
// Add initial data
for (int i = 0; i < 100; i++) {
    list.add(i);
}

// Many reads, few writes
for (int i = 0; i < 1000000; i++) {
    for (Integer item : list) {
        // Read without synchronization
    }
}
```

**Prevention**:
- Profile before choosing CopyOnWriteArrayList
- Use only for read-heavy workloads
- Consider alternatives for write-heavy scenarios

---

### Pitfall 4: List.subList() Gotcha

**Problem**: Modifying sublist affects original list

```java
// ❌ WRONG: Unexpected behavior
List<String> original = new ArrayList<>(
    Arrays.asList("A", "B", "C", "D", "E")
);

List<String> sublist = original.subList(1, 4);  // [B, C, D]

sublist.add("X");  // Modifies original!

System.out.println(original);  // [A, B, C, D, X, E]
// Unexpected! sublist is a VIEW, not a copy

// ✅ CORRECT: Create a copy if needed
List<String> sublist = new ArrayList<>(
    original.subList(1, 4)
);

sublist.add("X");  // Only modifies copy

System.out.println(original);  // [A, B, C, D, E] (unchanged)

// ✅ CORRECT: Understand sublist is a view
List<String> original = new ArrayList<>(
    Arrays.asList("A", "B", "C", "D", "E")
);

List<String> sublist = original.subList(1, 4);

// Modifying original invalidates sublist:
original.add("F");

sublist.add("X");  // ConcurrentModificationException!
// sublist is no longer valid

// Correct usage:
List<String> sublist = original.subList(1, 4);
sublist.clear();  // Removes B, C, D from original
System.out.println(original);  // [A, E]
```

**Prevention**:
- Remember: subList() returns a VIEW, not a copy
- Create a copy if you need independence
- Don't modify original while using sublist

---

## Set Edge Cases

### Pitfall 5: HashSet with Mutable Objects

**Problem**: Modifying HashSet elements after adding

```java
// ❌ WRONG: Mutable objects in HashSet
class Person {
    String name;
    int age;
    
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Person)) return false;
        Person other = (Person) obj;
        return name.equals(other.name) && age == other.age;
    }
}

HashSet<Person> set = new HashSet<>();
Person alice = new Person("Alice", 30);

set.add(alice);
System.out.println(set.contains(alice));  // true

// Modify alice:
alice.age = 31;  // hashCode changes!

System.out.println(set.contains(alice));  // false!
// Alice is still in set, but at wrong bucket

// ✅ CORRECT: Use immutable objects
class ImmutablePerson {
    final String name;
    final int age;
    
    public ImmutablePerson(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ImmutablePerson)) return false;
        ImmutablePerson other = (ImmutablePerson) obj;
        return name.equals(other.name) && age == other.age;
    }
}

HashSet<ImmutablePerson> set = new HashSet<>();
ImmutablePerson alice = new ImmutablePerson("Alice", 30);

set.add(alice);
System.out.println(set.contains(alice));  // true

// Can't modify alice (immutable)
// set.contains() always works correctly

// ✅ CORRECT: Remove, modify, re-add
HashSet<Person> set = new HashSet<>();
Person alice = new Person("Alice", 30);

set.add(alice);
set.remove(alice);  // Remove first
alice.age = 31;     // Modify
set.add(alice);     // Re-add
```

**Prevention**:
- Use immutable objects in HashSet/HashMap
- If mutable, remove before modifying
- Document that objects must not be modified

---

### Pitfall 6: TreeSet with Inconsistent Comparator

**Problem**: Comparator doesn't match equals()

```java
// ❌ WRONG: Inconsistent comparator
class Student implements Comparable<Student> {
    String name;
    int score;
    
    public Student(String name, int score) {
        this.name = name;
        this.score = score;
    }
    
    @Override
    public int compareTo(Student other) {
        return this.score - other.score;  // Compare by score
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Student)) return false;
        Student other = (Student) obj;
        return name.equals(other.name) && score == other.score;
    }
}

TreeSet<Student> set = new TreeSet<>();
Student alice1 = new Student("Alice", 80);
Student alice2 = new Student("Alice", 80);

set.add(alice1);
set.add(alice2);

System.out.println(set.size());  // 1 (not 2!)
// compareTo() says they're equal (same score)
// But equals() says they're different (different objects)

// ✅ CORRECT: Make comparator consistent with equals()
class Student implements Comparable<Student> {
    String name;
    int score;
    
    public Student(String name, int score) {
        this.name = name;
        this.score = score;
    }
    
    @Override
    public int compareTo(Student other) {
        int scoreCompare = this.score - other.score;
        if (scoreCompare != 0) return scoreCompare;
        return this.name.compareTo(other.name);  // Tiebreaker
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Student)) return false;
        Student other = (Student) obj;
        return name.equals(other.name) && score == other.score;
    }
}

TreeSet<Student> set = new TreeSet<>();
Student alice1 = new Student("Alice", 80);
Student alice2 = new Student("Alice", 80);

set.add(alice1);
set.add(alice2);

System.out.println(set.size());  // 2 (correct!)
```

**Prevention**:
- Ensure compareTo() is consistent with equals()
- Use tiebreakers in comparator
- Test with duplicate values

---

## Map Edge Cases

### Pitfall 7: HashMap Null Key/Value Confusion

**Problem**: Confusing null key with missing key

```java
// ❌ WRONG: Assuming null means missing
HashMap<String, Integer> map = new HashMap<>();

map.put("Alice", 30);
map.put("Bob", null);  // Null value

Integer age = map.get("Bob");
if (age == null) {
    System.out.println("Bob not found");  // Wrong!
    // Bob IS in the map, but with null value
}

// ✅ CORRECT: Use containsKey()
if (map.containsKey("Bob")) {
    Integer age = map.get("Bob");
    System.out.println("Bob found: " + age);  // null
} else {
    System.out.println("Bob not found");
}

// ✅ CORRECT: Use getOrDefault()
Integer age = map.getOrDefault("Bob", 0);
// Returns null if Bob exists with null value
// Returns 0 if Bob doesn't exist

// ❌ WRONG: Null key confusion
HashMap<String, Integer> map = new HashMap<>();

map.put(null, 0);  // Null key allowed
map.put("Alice", 30);

Integer value = map.get(null);  // 0
if (value == null) {
    System.out.println("Null key not found");  // Wrong!
}

// ✅ CORRECT: Use containsKey() for null
if (map.containsKey(null)) {
    Integer value = map.get(null);
    System.out.println("Null key found: " + value);
}
```

**Prevention**:
- Use `containsKey()` to check existence
- Use `getOrDefault()` for safe access
- Document null handling in your code
- Consider using Optional instead

---

### Pitfall 8: ConcurrentHashMap Atomicity Misconception

**Problem**: Assuming compound operations are atomic

```java
// ❌ WRONG: Not atomic
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

map.put("counter", 0);

// Multiple threads:
Integer current = map.get("counter");  // Read
map.put("counter", current + 1);       // Write
// Race condition! Two threads might read same value

// ✅ CORRECT: Use atomic operations
map.compute("counter", (k, v) -> v == null ? 1 : v + 1);

// ✅ CORRECT: Use putIfAbsent()
map.putIfAbsent("counter", 0);

// ✅ CORRECT: Use replace()
map.replace("counter", 0, 1);  // Atomic compare-and-set

// Example of race condition:
// Thread 1: read counter = 0
// Thread 2: read counter = 0
// Thread 1: write counter = 1
// Thread 2: write counter = 1
// Expected: 2, Actual: 1

// With compute():
// Thread 1: compute (atomic) → 1
// Thread 2: compute (atomic) → 2
// Result: 2 (correct!)
```

**Prevention**:
- Use atomic operations (compute, putIfAbsent, replace)
- Don't assume get-then-put is atomic
- Use synchronized block if needed
- Consider AtomicInteger for simple counters

---

### Pitfall 9: TreeMap with Null Keys

**Problem**: TreeMap doesn't allow null keys

```java
// ❌ WRONG: Null key in TreeMap
TreeMap<String, Integer> map = new TreeMap<>();

map.put("Alice", 30);
map.put(null, 0);  // NullPointerException!
// TreeMap uses compareTo(), which fails on null

// ✅ CORRECT: Use HashMap for null keys
HashMap<String, Integer> map = new HashMap<>();

map.put("Alice", 30);
map.put(null, 0);  // Allowed

// ✅ CORRECT: Use custom comparator that handles null
TreeMap<String, Integer> map = new TreeMap<>(
    (a, b) -> {
        if (a == null && b == null) return 0;
        if (a == null) return -1;
        if (b == null) return 1;
        return a.compareTo(b);
    }
);

map.put("Alice", 30);
map.put(null, 0);  // Now allowed

// Note: TreeMap also doesn't allow null values in some implementations
// Better to avoid null altogether
```

**Prevention**:
- Use HashMap if you need null keys
- Use custom comparator if null is necessary
- Consider using Optional instead of null

---

## Queue Edge Cases

### Pitfall 10: Queue Method Confusion

**Problem**: Confusing different queue methods

```java
// Queue has three methods for each operation:
Queue<String> queue = new LinkedList<>();

// Add operations:
queue.add("A");        // Throws exception if full
queue.offer("B");      // Returns false if full
queue.put("C");        // Blocks if full (BlockingQueue)

// Remove operations:
queue.remove();        // Throws exception if empty
queue.poll();          // Returns null if empty
queue.take();          // Blocks if empty (BlockingQueue)

// View operations:
queue.element();       // Throws exception if empty
queue.peek();          // Returns null if empty

// ❌ WRONG: Using wrong method
Queue<String> queue = new LinkedList<>();

String item = queue.remove();  // NoSuchElementException if empty

// ✅ CORRECT: Use safe method
String item = queue.poll();    // Returns null if empty

// ✅ CORRECT: Check before removing
if (!queue.isEmpty()) {
    String item = queue.remove();
}

// ✅ CORRECT: Use peek() to view
String item = queue.peek();    // null if empty
if (item != null) {
    queue.poll();
}
```

**Prevention**:
- Remember: add/remove/element throw exceptions
- Remember: offer/poll/peek return null/false
- Use safe methods (poll, peek) in production
- Document which method you're using

---

### Pitfall 11: PriorityQueue Ordering Assumption

**Problem**: Assuming PriorityQueue is fully sorted

```java
// ❌ WRONG: Assuming full sort
PriorityQueue<Integer> pq = new PriorityQueue<>();

pq.offer(5);
pq.offer(3);
pq.offer(7);
pq.offer(1);
pq.offer(9);

// Internal structure (min-heap):
//        1
//       / \
//      3   7
//     / \
//    5   9

// Iteration order is NOT sorted:
for (Integer item : pq) {
    System.out.println(item);  // 1, 3, 7, 5, 9 (not sorted!)
}

// ✅ CORRECT: Poll to get sorted order
while (!pq.isEmpty()) {
    System.out.println(pq.poll());  // 1, 3, 5, 7, 9 (sorted!)
}

// ✅ CORRECT: Use TreeSet for fully sorted
TreeSet<Integer> set = new TreeSet<>();
set.add(5);
set.add(3);
set.add(7);
set.add(1);
set.add(9);

for (Integer item : set) {
    System.out.println(item);  // 1, 3, 5, 7, 9 (sorted!)
}

// Performance comparison:
// PriorityQueue: O(log n) add, O(log n) remove
// TreeSet: O(log n) add, O(log n) remove
// But TreeSet maintains full sort, PriorityQueue only maintains heap property
```

**Prevention**:
- Remember: PriorityQueue is a heap, not fully sorted
- Use poll() to get sorted order
- Use TreeSet if you need full sort
- Document ordering guarantees

---

## Thread-Safety Pitfalls

### Pitfall 12: Synchronized Collections Iteration

**Problem**: Iteration not protected by synchronization

```java
// ❌ WRONG: Iteration not synchronized
List<String> list = Collections.synchronizedList(
    new ArrayList<>(Arrays.asList("A", "B", "C"))
);

// Thread 1:
for (String item : list) {
    System.out.println(item);  // ConcurrentModificationException!
}

// Thread 2:
list.add("D");  // Modifies while Thread 1 iterating

// ✅ CORRECT: Synchronize iteration
List<String> list = Collections.synchronizedList(
    new ArrayList<>(Arrays.asList("A", "B", "C"))
);

synchronized (list) {
    for (String item : list) {
        System.out.println(item);  // Safe
    }
}

// ✅ CORRECT: Use CopyOnWriteArrayList
List<String> list = new CopyOnWriteArrayList<>(
    Arrays.asList("A", "B", "C")
);

for (String item : list) {
    System.out.println(item);  // Safe (no lock needed)
}

// ✅ CORRECT: Use ConcurrentHashMap
Map<String, Integer> map = new ConcurrentHashMap<>();

for (Map.Entry<String, Integer> entry : map.entrySet()) {
    System.out.println(entry);  // Safe
}
```

**Prevention**:
- Synchronize iteration of synchronized collections
- Use concurrent collections for iteration
- Document thread-safety requirements
- Test with multiple threads

---

### Pitfall 13: Double-Checked Locking

**Problem**: Incorrect synchronization pattern

```java
// ❌ WRONG: Double-checked locking (broken)
class Cache {
    private HashMap<String, String> cache;
    
    public String get(String key) {
        if (cache == null) {  // Check 1
            synchronized (this) {
                if (cache == null) {  // Check 2
                    cache = new HashMap<>();
                }
            }
        }
        return cache.get(key);  // Not synchronized!
    }
}

// Problem: cache.get() is not synchronized
// Another thread might modify cache while reading

// ✅ CORRECT: Synchronize entire method
class Cache {
    private HashMap<String, String> cache = new HashMap<>();
    
    public synchronized String get(String key) {
        return cache.get(key);
    }
}

// ✅ CORRECT: Use ConcurrentHashMap
class Cache {
    private ConcurrentHashMap<String, String> cache = 
        new ConcurrentHashMap<>();
    
    public String get(String key) {
        return cache.get(key);  // No synchronization needed
    }
}

// ✅ CORRECT: Use volatile with proper synchronization
class Cache {
    private volatile HashMap<String, String> cache;
    
    public String get(String key) {
        if (cache == null) {
            synchronized (this) {
                if (cache == null) {
                    cache = new HashMap<>();
                }
            }
        }
        synchronized (cache) {  // Synchronize access
            return cache.get(key);
        }
    }
}
```

**Prevention**:
- Avoid double-checked locking
- Use concurrent collections
- Synchronize entire method if needed
- Use volatile carefully

---

## Performance Pitfalls

### Pitfall 14: String Concatenation in Loop

**Problem**: Building strings inefficiently

```java
// ❌ WRONG: String concatenation in loop
List<String> items = Arrays.asList("A", "B", "C", "D", "E");

String result = "";
for (String item : items) {
    result += item + ", ";  // Creates new String each time!
}
// Time: O(n²) - very slow!

// ✅ CORRECT: Use StringBuilder
StringBuilder sb = new StringBuilder();
for (String item : items) {
    sb.append(item).append(", ");
}
String result = sb.toString();
// Time: O(n) - much faster!

// ✅ CORRECT: Use String.join()
String result = String.join(", ", items);

// Benchmark (1000 items):
// String concatenation: ~500ms
// StringBuilder: ~1ms
// String.join(): ~1ms
```

**Prevention**:
- Use StringBuilder for building strings
- Use String.join() for joining collections
- Avoid += in loops
- Profile string operations

---

### Pitfall 15: Unnecessary Copying

**Problem**: Creating unnecessary copies of collections

```java
// ❌ WRONG: Unnecessary copy
List<String> original = new ArrayList<>(
    Arrays.asList("A", "B", "C")
);

List<String> copy = new ArrayList<>(original);  // Unnecessary
for (String item : copy) {
    System.out.println(item);
}

// ✅ CORRECT: Use original if not modifying
List<String> original = new ArrayList<>(
    Arrays.asList("A", "B", "C")
);

for (String item : original) {
    System.out.println(item);
}

// ✅ CORRECT: Copy only if modifying
List<String> original = new ArrayList<>(
    Arrays.asList("A", "B", "C")
);

List<String> copy = new ArrayList<>(original);
copy.add("D");  // Modify copy, not original

// ✅ CORRECT: Use unmodifiable view
List<String> original = new ArrayList<>(
    Arrays.asList("A", "B", "C")
);

List<String> view = Collections.unmodifiableList(original);
// No copy created, but can't modify
```

**Prevention**:
- Only copy when necessary
- Use views when possible
- Profile memory usage
- Document copy requirements

---

## Memory & GC Issues

### Pitfall 16: Memory Leak with Static Collections

**Problem**: Static collections holding references

```java
// ❌ WRONG: Memory leak
class EventManager {
    private static List<EventListener> listeners = new ArrayList<>();
    
    public static void addListener(EventListener listener) {
        listeners.add(listener);  // Never removed!
    }
}

// Listeners are never garbage collected
// Even if application no longer needs them
// Memory leak!

// ✅ CORRECT: Use WeakHashMap
class EventManager {
    private static WeakHashMap<Object, List<EventListener>> listeners = 
        new WeakHashMap<>();
    
    public static void addListener(Object source, EventListener listener) {
        listeners.computeIfAbsent(source, k -> new ArrayList<>())
                 .add(listener);
    }
}

// When source is GC'd, listeners are automatically removed

// ✅ CORRECT: Explicit removal
class EventManager {
    private static List<EventListener> listeners = new ArrayList<>();
    
    public static void addListener(EventListener listener) {
        listeners.add(listener);
    }
    
    public static void removeListener(EventListener listener) {
        listeners.remove(listener);  // Explicit removal
    }
}

// ✅ CORRECT: Use try-with-resources
class EventManager implements AutoCloseable {
    private List<EventListener> listeners = new ArrayList<>();
    
    public void addListener(EventListener listener) {
        listeners.add(listener);
    }
    
    @Override
    public void close() {
        listeners.clear();  // Cleanup on close
    }
}

try (EventManager manager = new EventManager()) {
    manager.addListener(listener);
}  // Automatically cleaned up
```

**Prevention**:
- Avoid static collections
- Use WeakHashMap for caches
- Implement explicit removal
- Use try-with-resources for cleanup

---

### Pitfall 17: Large Collection Memory Overhead

**Problem**: Underestimating memory usage

```java
// Memory overhead per element:
// ArrayList: 8 bytes (reference) + 8 bytes (object header) = 16 bytes
// LinkedList: 8 bytes (reference) + 24 bytes (node object) = 32 bytes
// HashMap: 8 bytes (reference) + 48 bytes (entry object) = 56 bytes

// ❌ WRONG: Not considering overhead
LinkedList<Integer> list = new LinkedList<>();
for (int i = 0; i < 1000000; i++) {
    list.add(i);  // 32 bytes per element
}
// Total: 32MB just for the list!

// ✅ CORRECT: Use ArrayList for large collections
ArrayList<Integer> list = new ArrayList<>(1000000);
for (int i = 0; i < 1000000; i++) {
    list.add(i);  // 16 bytes per element
}
// Total: 16MB (half the memory!)

// ✅ CORRECT: Use primitive arrays if possible
int[] array = new int[1000000];
for (int i = 0; i < 1000000; i++) {
    array[i] = i;  // 4 bytes per element
}
// Total: 4MB (8x less memory!)

// Memory comparison (1M elements):
// LinkedList<Integer>: 32MB
// ArrayList<Integer>: 16MB
// int[]: 4MB
```

**Prevention**:
- Choose collection type based on memory
- Use primitive arrays for large collections
- Monitor memory usage
- Profile before optimizing

---

## Checklist for Safe Collection Usage

```
✅ List Usage
  □ Use ArrayList for random access
  □ Use LinkedList only for head/tail operations
  □ Preallocate capacity if size known
  □ Use iterator for sequential access
  □ Remember subList() is a view

✅ Set Usage
  □ Use immutable objects in HashSet
  □ Ensure comparator consistent with equals() in TreeSet
  □ Override both hashCode() and equals()
  □ Don't modify objects after adding to set

✅ Map Usage
  □ Use containsKey() to check existence
  □ Use getOrDefault() for safe access
  □ Use atomic operations in ConcurrentHashMap
  □ Don't assume null means missing

✅ Queue Usage
  □ Use poll() instead of remove()
  □ Use peek() instead of element()
  □ Remember PriorityQueue is a heap, not fully sorted
  □ Use BlockingQueue for producer-consumer

✅ Thread-Safety
  □ Synchronize iteration of synchronized collections
  □ Use concurrent collections for multi-threaded access
  □ Use atomic operations for compound operations
  □ Avoid double-checked locking

✅ Performance
  □ Profile before optimizing
  □ Choose right collection type
  □ Preallocate capacity
  □ Use StringBuilder for string building
  □ Avoid unnecessary copying

✅ Memory
  □ Avoid static collections
  □ Use WeakHashMap for caches
  □ Monitor memory usage
  □ Clean up large collections
```

---

**Next**: Practice with real-world scenarios and code reviews!