# 📝 Collections Framework - Quizzes

## Beginner Level (5 Questions)

### Q1: List vs Set - Basic Difference
**Question**: What is the fundamental difference between a List and a Set?

**Options**:
A) List allows duplicates, Set does not  
B) List is ordered, Set is not  
C) Both A and B  
D) List is faster than Set  

**Answer**: **C) Both A and B**

**Explanation**:
- **List**: Ordered collection (maintains insertion order), allows duplicates
  ```java
  List<String> list = new ArrayList<>();
  list.add("Apple");
  list.add("Apple");  // Allowed!
  // Order: [Apple, Apple]
  ```

- **Set**: Unordered collection (no guaranteed order), no duplicates
  ```java
  Set<String> set = new HashSet<>();
  set.add("Apple");
  set.add("Apple");  // Ignored! Still size = 1
  ```

---

### Q2: HashMap vs TreeMap
**Question**: When would you use TreeMap instead of HashMap?

**Options**:
A) When you need faster access  
B) When you need keys in sorted order  
C) When you need thread-safety  
D) When you have null keys  

**Answer**: **B) When you need keys in sorted order**

**Explanation**:
```java
// HashMap: No order guarantee
HashMap<String, Integer> hmap = new HashMap<>();
hmap.put("Charlie", 30);
hmap.put("Alice", 25);
hmap.put("Bob", 35);
// Order: Charlie, Alice, Bob (unpredictable)

// TreeMap: Sorted by key
TreeMap<String, Integer> tmap = new TreeMap<>();
tmap.put("Charlie", 30);
tmap.put("Alice", 25);
tmap.put("Bob", 35);
// Order: Alice, Bob, Charlie (sorted)

// Performance tradeoff:
// HashMap: O(1) access
// TreeMap: O(log n) access (but sorted)
```

---

### Q3: ArrayList vs LinkedList
**Question**: Which is better for frequent insertions at the beginning?

**Options**:
A) ArrayList (always faster)  
B) LinkedList (O(1) vs O(n))  
C) They're the same  
D) Depends on the size  

**Answer**: **B) LinkedList (O(1) vs O(n))**

**Explanation**:
```java
// ArrayList: O(n) - must shift all elements
ArrayList<String> list = new ArrayList<>(
    Arrays.asList("B", "C", "D")
);
list.add(0, "A");  // Shift B,C,D right → O(n)
// Result: [A, B, C, D]

// LinkedList: O(1) - just update pointers
LinkedList<String> list = new LinkedList<>(
    Arrays.asList("B", "C", "D")
);
list.add(0, "A");  // Update pointers → O(1)
// Result: [A, B, C, D]

// Performance comparison (1000 insertions at head):
// ArrayList: ~500,000 operations
// LinkedList: ~1,000 operations
```

---

### Q4: HashSet Uniqueness
**Question**: How does HashSet ensure uniqueness?

**Options**:
A) By comparing all elements  
B) By using hashCode() and equals()  
C) By sorting elements  
D) By maintaining insertion order  

**Answer**: **B) By using hashCode() and equals()**

**Explanation**:
```java
HashSet<String> set = new HashSet<>();

set.add("Apple");   // hashCode = 1234, equals = true
set.add("Apple");   // Same hashCode and equals → Rejected!
set.add("Apricot"); // Different hashCode → Added

// Two-step process:
// 1. Check hashCode() - if different, definitely different objects
// 2. If same hashCode, use equals() to confirm

// Custom class example:
class Person {
    String name;
    int age;
    
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

Set<Person> people = new HashSet<>();
people.add(new Person("Alice", 30));
people.add(new Person("Alice", 30));  // Rejected (same hash & equals)
```

---

### Q5: Queue Operations
**Question**: What does `poll()` do on an empty Queue?

**Options**:
A) Throws an exception  
B) Returns null  
C) Blocks indefinitely  
D) Returns the last element  

**Answer**: **B) Returns null**

**Explanation**:
```java
Queue<String> queue = new LinkedList<>();

// poll() vs remove() on empty queue:
queue.poll();      // Returns null (safe)
queue.remove();    // Throws NoSuchElementException (unsafe)

// poll() vs peek():
queue.offer("A");
queue.peek();      // Returns "A" (doesn't remove)
queue.poll();      // Returns "A" (removes)
queue.poll();      // Returns null (queue empty)

// BlockingQueue is different:
BlockingQueue<String> bq = new LinkedBlockingQueue<>();
bq.poll();         // Returns null (non-blocking)
bq.take();         // Blocks until element available
```

---

## Intermediate Level (5 Questions)

### Q6: ConcurrentModificationException
**Question**: What causes ConcurrentModificationException?

**Options**:
A) Modifying a collection while iterating  
B) Using multiple threads  
C) Calling remove() on iterator  
D) All of the above  

**Answer**: **A) Modifying a collection while iterating**

**Explanation**:
```java
// ❌ WRONG: Causes ConcurrentModificationException
List<String> list = new ArrayList<>(
    Arrays.asList("A", "B", "C")
);
for (String item : list) {
    if (item.equals("B")) {
        list.remove(item);  // Modifying while iterating!
    }
}

// ✅ CORRECT: Use iterator.remove()
Iterator<String> it = list.iterator();
while (it.hasNext()) {
    String item = it.next();
    if (item.equals("B")) {
        it.remove();  // Safe! Iterator knows about removal
    }
}

// ✅ CORRECT: Use removeIf()
list.removeIf(item -> item.equals("B"));

// ✅ CORRECT: Collect items to remove first
List<String> toRemove = new ArrayList<>();
for (String item : list) {
    if (item.equals("B")) {
        toRemove.add(item);
    }
}
list.removeAll(toRemove);

// How it works internally:
// Iterator maintains modCount
// If collection modified, modCount changes
// Iterator detects mismatch and throws exception
```

---

### Q7: HashMap Collision Handling
**Question**: What happens when two different keys hash to the same bucket?

**Options**:
A) The second key is rejected  
B) Chaining: Both stored in linked list  
C) Linear probing: Find next empty bucket  
D) The first key is overwritten  

**Answer**: **B) Chaining: Both stored in linked list**

**Explanation**:
```java
// Collision example:
HashMap<String, Integer> map = new HashMap<>();

// Assume both hash to bucket 5:
map.put("Apple", 1);    // Bucket 5: Apple → 1
map.put("Apricot", 2);  // Bucket 5: Apple → 1, Apricot → 2

// Internal structure:
// Bucket 5: [Node(Apple, 1) → Node(Apricot, 2) → null]

// Retrieval:
map.get("Apricot");  // Hash to bucket 5, traverse chain, find Apricot

// Performance impact:
// Good case: O(1) - no collisions
// Bad case: O(n) - all keys hash to same bucket

// Load factor controls resizing:
// Default: 0.75
// When size > capacity * 0.75, resize to 2x capacity
// This reduces collisions

// Java 8+ optimization:
// If chain length > 8, convert to Red-Black Tree
// Tree operations: O(log n) instead of O(n)
```

---

### Q8: TreeSet Ordering
**Question**: How does TreeSet maintain sorted order?

**Options**:
A) By sorting elements after each add  
B) By using a Red-Black Tree  
C) By comparing with Comparator  
D) Both B and C  

**Answer**: **D) Both B and C**

**Explanation**:
```java
// Natural ordering (implements Comparable)
TreeSet<Integer> set1 = new TreeSet<>();
set1.add(5);
set1.add(3);
set1.add(7);
set1.add(1);
// Order: [1, 3, 5, 7] (natural order)

// Custom ordering (Comparator)
TreeSet<String> set2 = new TreeSet<>(
    (a, b) -> b.compareTo(a)  // Reverse order
);
set2.add("Apple");
set2.add("Banana");
set2.add("Cherry");
// Order: [Cherry, Banana, Apple] (reverse)

// Red-Black Tree structure:
//        ┌─────┐
//        │  5  │ (Black)
//        └──┬──┘
//          / \
//         /   \
//    ┌───▼─┐ ┌─▼───┐
//    │  3  │ │  7  │ (Red)
//    └──┬──┘ └─────┘
//      / \
//  ┌──▼─┐ ┌─────┐
//  │ 1  │ │  4  │ (Black)
//  └────┘ └─────┘

// Performance:
// Add: O(log n) - tree rebalancing
// Remove: O(log n) - tree rebalancing
// Get: O(log n) - binary search
// Iteration: O(n) - in-order traversal
```

---

### Q9: CopyOnWriteArrayList Use Case
**Question**: When is CopyOnWriteArrayList preferred over ArrayList?

**Options**:
A) When you have many writes  
B) When you have many reads and few writes  
C) When you need the fastest access  
D) When you need null elements  

**Answer**: **B) When you have many reads and few writes**

**Explanation**:
```java
// CopyOnWriteArrayList strategy:
// - Reads: No synchronization (fast!)
// - Writes: Copy entire array (slow!)

// Good use case: Event listeners
List<EventListener> listeners = new CopyOnWriteArrayList<>();

// Many reads (fire events):
for (EventListener listener : listeners) {
    listener.onEvent(event);  // No lock needed!
}

// Few writes (add/remove listeners):
listeners.add(newListener);   // Copy array, add, replace
listeners.remove(oldListener); // Copy array, remove, replace

// Bad use case: Frequent updates
CopyOnWriteArrayList<Integer> list = new CopyOnWriteArrayList<>();
for (int i = 0; i < 1000; i++) {
    list.add(i);  // Copies entire array 1000 times! O(n²)
}

// Performance comparison:
// ArrayList: 1000 adds = O(n) amortized
// CopyOnWriteArrayList: 1000 adds = O(n²) worst case
```

---

### Q10: PriorityQueue vs Queue
**Question**: What's the difference between PriorityQueue and LinkedList as Queue?

**Options**:
A) PriorityQueue is always faster  
B) PriorityQueue returns elements in priority order  
C) LinkedList is thread-safe  
D) They're identical  

**Answer**: **B) PriorityQueue returns elements in priority order**

**Explanation**:
```java
// LinkedList as Queue: FIFO (First In, First Out)
Queue<Integer> queue = new LinkedList<>();
queue.offer(5);
queue.offer(3);
queue.offer(7);
queue.offer(1);
queue.poll();  // 5 (first added)
queue.poll();  // 3
queue.poll();  // 7
queue.poll();  // 1

// PriorityQueue: Priority order (min-heap by default)
Queue<Integer> pq = new PriorityQueue<>();
pq.offer(5);
pq.offer(3);
pq.offer(7);
pq.offer(1);
pq.poll();  // 1 (smallest)
pq.poll();  // 3
pq.poll();  // 5
pq.poll();  // 7

// Custom priority (max-heap):
Queue<Integer> maxHeap = new PriorityQueue<>(
    (a, b) -> b - a
);
maxHeap.offer(5);
maxHeap.offer(3);
maxHeap.offer(7);
maxHeap.offer(1);
maxHeap.poll();  // 7 (largest)
maxHeap.poll();  // 5
maxHeap.poll();  // 3
maxHeap.poll();  // 1

// Performance:
// LinkedList: offer O(1), poll O(1)
// PriorityQueue: offer O(log n), poll O(log n)
```

---

## Advanced Level (5 Questions)

### Q11: HashMap Resizing
**Question**: What happens internally when HashMap reaches its load factor threshold?

**Options**:
A) Elements are sorted  
B) Capacity is doubled and all entries rehashed  
C) Old entries are removed  
D) A new HashMap is created  

**Answer**: **B) Capacity is doubled and all entries rehashed**

**Explanation**:
```java
// Load factor = size / capacity
// Default: 0.75

HashMap<String, Integer> map = new HashMap<>();
// Initial capacity: 16
// Threshold: 16 * 0.75 = 12

// Add 12 elements:
for (int i = 0; i < 12; i++) {
    map.put("key" + i, i);
}
// size = 12, capacity = 16, threshold = 12

// Add 13th element:
map.put("key12", 12);
// Triggers resize!
// New capacity: 32
// New threshold: 32 * 0.75 = 24
// All entries rehashed to new buckets

// Rehashing process:
// Old bucket index: hash(key) & (16 - 1)
// New bucket index: hash(key) & (32 - 1)

// Example:
// Key "Apple" with hash = 1234
// Old: 1234 & 15 = 2 (bucket 2)
// New: 1234 & 31 = 18 (bucket 18)

// Why rehash?
// - Reduce collisions
// - Maintain O(1) average performance
// - Spread entries across more buckets

// Cost:
// - O(n) operation (must rehash all entries)
// - Happens rarely (logarithmic frequency)
// - Amortized O(1) per add
```

---

### Q12: Comparable vs Comparator
**Question**: What's the difference between Comparable and Comparator?

**Options**:
A) Comparable is for sorting, Comparator is for comparing  
B) Comparable is internal, Comparator is external  
C) Comparator is always better  
D) They're the same thing  

**Answer**: **B) Comparable is internal, Comparator is external**

**Explanation**:
```java
// Comparable: Class defines its own ordering
class Student implements Comparable<Student> {
    String name;
    int age;
    
    @Override
    public int compareTo(Student other) {
        return this.age - other.age;  // Sort by age
    }
}

// Usage:
List<Student> students = new ArrayList<>();
students.add(new Student("Alice", 25));
students.add(new Student("Bob", 20));
Collections.sort(students);  // Uses compareTo()
// Result: Bob (20), Alice (25)

// Comparator: External ordering strategy
Comparator<Student> byName = (a, b) -> a.name.compareTo(b.name);
Comparator<Student> byAge = (a, b) -> a.age - b.age;
Comparator<Student> byAgeDesc = (a, b) -> b.age - a.age;

// Usage:
Collections.sort(students, byName);     // Sort by name
Collections.sort(students, byAge);      // Sort by age
Collections.sort(students, byAgeDesc);  // Sort by age (desc)

// TreeSet with Comparator:
TreeSet<Student> set = new TreeSet<>(byName);
set.add(new Student("Alice", 25));
set.add(new Student("Bob", 20));
// Sorted by name: Alice, Bob

// Key differences:
// Comparable:
// - Implemented by the class itself
// - Single natural ordering
// - Used by Collections.sort() by default
// - Modifies class definition

// Comparator:
// - External to the class
// - Multiple orderings possible
// - Passed to Collections.sort()
// - Doesn't modify class definition
```

---

### Q13: ConcurrentHashMap Segmentation
**Question**: How does ConcurrentHashMap achieve better concurrency than synchronized HashMap?

**Options**:
A) By using multiple locks (segments)  
B) By avoiding locks entirely  
C) By using read-write locks  
D) By copying data on write  

**Answer**: **A) By using multiple locks (segments)**

**Explanation**:
```java
// Synchronized HashMap: One lock for entire map
synchronized (map) {
    map.put("key1", "value1");
}
// Only one thread can access at a time
// Other threads wait...

// ConcurrentHashMap: Multiple locks (segments)
// Default: 16 segments, 16 locks

// Thread 1 writes to segment 0 (Lock 0)
// Thread 2 writes to segment 5 (Lock 5)
// Both can proceed concurrently!

// Segment structure:
// ┌─────────────────────────────────┐
// │ ConcurrentHashMap               │
// ├─────────────────────────────────┤
// │ segments[0] ──┐                 │
// │ segments[1] ──┼─ Lock 0         │
// │ segments[2] ──┘                 │
// │                                 │
// │ segments[3] ──┐                 │
// │ segments[4] ──┼─ Lock 1         │
// │ segments[5] ──┘                 │
// │                                 │
// │ ...                             │
// └─────────────────────────────────┘

// Performance:
// Synchronized HashMap:
// - 10 threads writing: 1 thread at a time (sequential)
// - Throughput: 1 write per time unit

// ConcurrentHashMap:
// - 10 threads writing: Up to 16 threads concurrently
// - Throughput: 16 writes per time unit

// Atomic operations:
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

// putIfAbsent: Atomic check-and-put
map.putIfAbsent("key", 1);  // Add only if absent

// compute: Atomic read-modify-write
map.compute("key", (k, v) -> v == null ? 1 : v + 1);

// replace: Atomic conditional replace
map.replace("key", 1, 2);  // Replace only if current value is 1
```

---

### Q14: Stream vs Iterator
**Question**: What's the advantage of using Streams over traditional iteration?

**Options**:
A) Streams are always faster  
B) Streams enable functional programming and lazy evaluation  
C) Streams are thread-safe  
D) Streams use less memory  

**Answer**: **B) Streams enable functional programming and lazy evaluation**

**Explanation**:
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

// Traditional iteration (eager):
List<Integer> result = new ArrayList<>();
for (Integer num : numbers) {
    if (num > 5) {
        result.add(num * 2);
    }
}
// All operations executed immediately
// Result: [12, 14, 16, 18, 20]

// Stream (lazy evaluation):
List<Integer> result = numbers.stream()
    .filter(num -> num > 5)      // Lazy: not executed yet
    .map(num -> num * 2)         // Lazy: not executed yet
    .collect(Collectors.toList()); // Terminal: executes pipeline
// Result: [12, 14, 16, 18, 20]

// Lazy evaluation benefit:
List<Integer> result = numbers.stream()
    .filter(num -> {
        System.out.println("Filtering: " + num);
        return num > 5;
    })
    .map(num -> {
        System.out.println("Mapping: " + num);
        return num * 2;
    })
    .limit(2)  // Only need 2 results
    .collect(Collectors.toList());

// Output:
// Filtering: 1
// Filtering: 2
// ...
// Filtering: 6
// Mapping: 6
// Filtering: 7
// Mapping: 7
// (Stops here! Didn't process 8, 9, 10)

// Functional programming:
// Traditional:
int sum = 0;
for (Integer num : numbers) {
    if (num % 2 == 0) {
        sum += num;
    }
}

// Stream:
int sum = numbers.stream()
    .filter(num -> num % 2 == 0)
    .mapToInt(Integer::intValue)
    .sum();

// Parallel processing:
int sum = numbers.parallelStream()
    .filter(num -> num % 2 == 0)
    .mapToInt(Integer::intValue)
    .sum();
// Automatically parallelized!
```

---

### Q15: Collections.unmodifiableList vs ImmutableList
**Question**: What's the difference between Collections.unmodifiableList() and creating an immutable list?

**Options**:
A) They're the same  
B) unmodifiableList is a wrapper, immutable is truly immutable  
C) Immutable is slower  
D) unmodifiableList allows null elements  

**Answer**: **B) unmodifiableList is a wrapper, immutable is truly immutable**

**Explanation**:
```java
// Collections.unmodifiableList: Wrapper (view)
List<String> original = new ArrayList<>(
    Arrays.asList("A", "B", "C")
);
List<String> unmodifiable = Collections.unmodifiableList(original);

unmodifiable.add("D");  // Throws UnsupportedOperationException

// But the original can still be modified:
original.add("D");
System.out.println(unmodifiable);  // [A, B, C, D]
// The unmodifiable list reflects changes!

// Immutable list: Truly immutable (Java 9+)
List<String> immutable = List.of("A", "B", "C");

immutable.add("D");  // Throws UnsupportedOperationException
immutable.set(0, "X");  // Throws UnsupportedOperationException

// No way to modify it
// Doesn't reflect changes to underlying data

// Performance:
// unmodifiableList: O(1) - just wraps
// List.of(): O(n) - copies data

// Use cases:
// unmodifiableList: Prevent accidental modification
// List.of(): Truly immutable, safe to share

// Example:
class Config {
    private static final List<String> ALLOWED_HOSTS = 
        List.of("localhost", "example.com");  // Immutable
    
    public static List<String> getAllowedHosts() {
        return ALLOWED_HOSTS;  // Safe to return directly
    }
}

// vs

class Config {
    private static final List<String> ALLOWED_HOSTS = 
        Collections.unmodifiableList(
            new ArrayList<>(Arrays.asList("localhost", "example.com"))
        );
    
    public static List<String> getAllowedHosts() {
        return ALLOWED_HOSTS;  // Still safe, but less efficient
    }
}
```

---

## Interview Tricky Questions (7 Questions)

### Q16: HashMap with Custom Objects
**Question**: Why must you override both hashCode() and equals() for HashMap keys?

**Answer**:
```java
// Problem: Only overriding equals()
class Person {
    String name;
    int age;
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Person)) return false;
        Person other = (Person) obj;
        return name.equals(other.name) && age == other.age;
    }
    // hashCode() NOT overridden!
}

HashMap<Person, String> map = new HashMap<>();
Person p1 = new Person("Alice", 30);
Person p2 = new Person("Alice", 30);

map.put(p1, "Engineer");
System.out.println(map.get(p2));  // null! (Expected: "Engineer")

// Why?
// p1.equals(p2) = true
// p1.hashCode() != p2.hashCode() (different objects)
// HashMap looks in wrong bucket!

// Solution: Override both
class Person {
    String name;
    int age;
    
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

// Now:
map.put(p1, "Engineer");
System.out.println(map.get(p2));  // "Engineer" ✓

// Contract:
// If a.equals(b) == true, then a.hashCode() == b.hashCode()
// If a.hashCode() != b.hashCode(), then a.equals(b) == false
```

---

### Q17: ArrayList Capacity vs Size
**Question**: What's the difference between capacity and size in ArrayList?

**Answer**:
```java
ArrayList<String> list = new ArrayList<>();

// Initial state:
// size = 0 (number of elements)
// capacity = 10 (internal array length)

list.add("A");
list.add("B");
list.add("C");

// After 3 adds:
// size = 3
// capacity = 10 (unchanged)

// Add 8 more:
for (int i = 0; i < 8; i++) {
    list.add("Item" + i);
}

// After 11 adds:
// size = 11
// capacity = 15 (resized!)

// Accessing:
list.size();      // 11 (actual elements)
list.get(10);     // "Item7" (valid)
list.get(11);     // IndexOutOfBoundsException (beyond size)
list.get(14);     // IndexOutOfBoundsException (beyond size)

// Memory:
// Only size elements are used
// Capacity - size elements are wasted space

// Optimization:
ArrayList<String> list = new ArrayList<>(1000);
// Preallocate capacity = 1000
// Avoids resizing if you know size in advance

// Trimming:
list.trimToSize();  // Reduce capacity to size
// Useful if list won't grow further
```

---

### Q18: TreeSet with Mutable Objects
**Question**: What happens if you modify a TreeSet element after adding it?

**Answer**:
```java
// Problem: Mutable objects in TreeSet
class Student implements Comparable<Student> {
    String name;
    int score;
    
    public Student(String name, int score) {
        this.name = name;
        this.score = score;
    }
    
    @Override
    public int compareTo(Student other) {
        return this.score - other.score;
    }
}

TreeSet<Student> set = new TreeSet<>();
Student alice = new Student("Alice", 80);
Student bob = new Student("Bob", 70);

set.add(alice);  // Sorted by score: 70, 80
set.add(bob);

// Modify alice's score:
alice.score = 60;  // Now should be: 60, 70

// But TreeSet doesn't know!
System.out.println(set.contains(alice));  // true (but wrong position!)
System.out.println(set.first().score);    // 70 (should be 60!)

// Why?
// TreeSet uses compareTo() to find position
// When you modify, compareTo() changes
// But TreeSet doesn't rebalance!

// Solution: Remove, modify, re-add
set.remove(alice);
alice.score = 60;
set.add(alice);

// Or use immutable objects:
class ImmutableStudent implements Comparable<ImmutableStudent> {
    final String name;
    final int score;
    
    public ImmutableStudent(String name, int score) {
        this.name = name;
        this.score = score;
    }
    
    @Override
    public int compareTo(ImmutableStudent other) {
        return this.score - other.score;
    }
}

// Now safe to use in TreeSet
```

---

### Q19: Fail-Fast Iterator
**Question**: How does fail-fast iterator work in ArrayList?

**Answer**:
```java
// Fail-fast mechanism:
ArrayList<String> list = new ArrayList<>(
    Arrays.asList("A", "B", "C", "D")
);

// Iterator maintains modCount
// ArrayList increments modCount on each modification

Iterator<String> it = list.iterator();

it.next();  // "A"
it.next();  // "B"

list.add("E");  // modCount++

it.next();  // ConcurrentModificationException!
// Iterator detects modCount changed

// Internal implementation:
class ArrayList {
    protected transient int modCount = 0;
    
    public Iterator<String> iterator() {
        return new Itr();
    }
    
    private class Itr implements Iterator<String> {
        int expectedModCount = modCount;
        
        public boolean hasNext() {
            return cursor != size;
        }
        
        public String next() {
            checkForComodification();
            // ...
        }
        
        final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }
}

// Safe ways to modify during iteration:
// 1. Use iterator.remove()
Iterator<String> it = list.iterator();
while (it.hasNext()) {
    String item = it.next();
    if (item.equals("B")) {
        it.remove();  // Safe! Updates expectedModCount
    }
}

// 2. Use removeIf()
list.removeIf(item -> item.equals("B"));

// 3. Collect items to remove first
List<String> toRemove = new ArrayList<>();
for (String item : list) {
    if (item.equals("B")) {
        toRemove.add(item);
    }
}
list.removeAll(toRemove);

// 4. Use CopyOnWriteArrayList (no fail-fast)
List<String> list = new CopyOnWriteArrayList<>(
    Arrays.asList("A", "B", "C", "D")
);
for (String item : list) {
    if (item.equals("B")) {
        list.remove(item);  // Safe! Creates new array
    }
}
```

---

### Q20: Collections.sort() Stability
**Question**: Is Collections.sort() stable? What does that mean?

**Answer**:
```java
// Stable sort: Maintains relative order of equal elements
class Student {
    String name;
    int score;
    
    public Student(String name, int score) {
        this.name = name;
        this.score = score;
    }
    
    @Override
    public String toString() {
        return name + "(" + score + ")";
    }
}

List<Student> students = new ArrayList<>();
students.add(new Student("Alice", 80));
students.add(new Student("Bob", 80));
students.add(new Student("Charlie", 80));

// Original order: Alice, Bob, Charlie (all score 80)

Collections.sort(students, (a, b) -> a.score - b.score);

// After sort: Alice, Bob, Charlie (same order!)
// Because sort is STABLE

// Why stable?
// Java uses Timsort (hybrid of merge sort + insertion sort)
// Merge sort is stable
// Maintains relative order of equal elements

// Unstable sort example:
// If we used quicksort, order might be: Charlie, Alice, Bob

// Practical example:
List<Student> students = new ArrayList<>();
students.add(new Student("Alice", 80));
students.add(new Student("Bob", 90));
students.add(new Student("Charlie", 80));
students.add(new Student("David", 90));

// Sort by score:
Collections.sort(students, (a, b) -> a.score - b.score);

// Result (stable):
// Alice(80), Charlie(80), Bob(90), David(90)
// Within same score, maintains original order

// Multi-level sorting (using stability):
// 1. Sort by score
Collections.sort(students, (a, b) -> a.score - b.score);

// 2. Sort by name (stable, preserves score order)
Collections.sort(students, (a, b) -> a.name.compareTo(b.name));

// Result: Sorted by name, but within same name, score order preserved
```

---

### Q21: WeakHashMap Use Case
**Question**: When would you use WeakHashMap instead of HashMap?

**Answer**:
```java
// WeakHashMap: Keys are weak references
// If key is no longer referenced elsewhere, it can be garbage collected

// Use case: Caching with automatic cleanup
WeakHashMap<String, byte[]> cache = new WeakHashMap<>();

String key = "image.jpg";
byte[] imageData = new byte[1000000];  // 1MB

cache.put(key, imageData);

// As long as 'key' is referenced, entry stays in cache
System.out.println(cache.size());  // 1

// When 'key' goes out of scope:
key = null;  // No more strong references

System.gc();  // Trigger garbage collection

System.out.println(cache.size());  // 0 (entry removed!)

// Practical example: Listener registry
class EventManager {
    private WeakHashMap<Object, List<EventListener>> listeners = 
        new WeakHashMap<>();
    
    public void addListener(Object source, EventListener listener) {
        listeners.computeIfAbsent(source, k -> new ArrayList<>())
                 .add(listener);
    }
    
    public void fireEvent(Object source, Event event) {
        List<EventListener> list = listeners.get(source);
        if (list != null) {
            for (EventListener listener : list) {
                listener.onEvent(event);
            }
        }
    }
}

// When source is garbage collected, listeners are automatically removed
// No memory leak!

// Comparison:
// HashMap: Keeps entries forever (memory leak risk)
// WeakHashMap: Removes entries when key is GC'd (automatic cleanup)

// Performance:
// HashMap: O(1) access
// WeakHashMap: O(1) access (same)

// Overhead:
// WeakHashMap: Extra memory for weak references
// Use only when automatic cleanup is needed
```

---

## Summary

### Key Concepts to Master
1. **List vs Set vs Map**: Understand fundamental differences
2. **ArrayList vs LinkedList**: Know when to use each
3. **HashMap vs TreeMap**: Ordered vs unordered tradeoffs
4. **Concurrent Collections**: Thread-safety strategies
5. **Iterator Pattern**: Safe iteration and modification
6. **Performance**: Know time complexities
7. **Custom Objects**: hashCode() and equals() contracts
8. **Immutability**: Mutable vs immutable collections

### Common Mistakes
- ❌ Modifying collection while iterating
- ❌ Not overriding hashCode() with equals()
- ❌ Using mutable objects in TreeSet
- ❌ Choosing wrong collection type
- ❌ Ignoring thread-safety requirements
- ❌ Not understanding capacity vs size

### Best Practices
- ✅ Use interfaces (List, Set, Map, Queue)
- ✅ Choose implementation based on use case
- ✅ Override hashCode() and equals() together
- ✅ Use immutable collections when possible
- ✅ Consider thread-safety from the start
- ✅ Measure performance with real data

---

**Next**: Study EDGE_CASES.md to learn about common pitfalls!