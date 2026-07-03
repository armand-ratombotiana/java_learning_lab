# Collections — Step-by-Step Tutorial

## Step 1: Choose and Create a List

```java
// For fast random access:
List<String> names = new ArrayList<>();
names.add("Alice");
names.add("Bob");
names.add("Charlie");
System.out.println(names.get(1));  // "Bob"

// For frequent insertions/removals at beginning:
List<String> queue = new LinkedList<>();
queue.addFirst("first");
queue.addLast("last");
```

## Step 2: Sort and Search

```java
List<Integer> numbers = new ArrayList<>(List.of(3, 1, 4, 1, 5, 9));
Collections.sort(numbers);  // [1, 1, 3, 4, 5, 9]
int index = Collections.binarySearch(numbers, 4);  // 3

// Custom sort:
numbers.sort(Comparator.reverseOrder());  // [9, 5, 4, 3, 1, 1]
```

## Step 3: Use a Set for Uniqueness

```java
List<String> withDupes = List.of("a", "b", "a", "c", "b");
Set<String> unique = new HashSet<>(withDupes);  // {a, b, c}

// Preserve order:
Set<String> insertionOrder = new LinkedHashSet<>(withDupes);  // {a, b, c}

// Sorted:
Set<String> sorted = new TreeSet<>(withDupes);  // {a, b, c}
```

## Step 4: Use a Map for Key-Value

```java
Map<String, Integer> scores = new HashMap<>();
scores.put("Alice", 95);
scores.put("Bob", 87);

// Safe retrieval:
int bobScore = scores.getOrDefault("Bob", 0);
int charlieScore = scores.getOrDefault("Charlie", 0);

// Compute if absent:
scores.computeIfAbsent("Charlie", k -> 100);  // Only computes if key missing

// Iterate:
scores.forEach((name, score) -> System.out.println(name + ": " + score));
```

## Step 5: Use Queue for Processing

```java
Queue<String> taskQueue = new LinkedList<>();
taskQueue.offer("task1");
taskQueue.offer("task2");
String task = taskQueue.poll();  // "task1" or null if empty

// Priority queue:
Queue<Task> urgent = new PriorityQueue<>(
    Comparator.comparingInt(Task::priority).reversed()
);
```

## Step 6: Use Deque as Stack

```java
Deque<String> stack = new ArrayDeque<>();
stack.push("bottom");    // addFirst
stack.push("middle");
stack.push("top");
String top = stack.pop();  // "top" — removeFirst
```

## Step 7: Create Immutable Collections

```java
// Java 9+ factory methods:
List<String> immutable = List.of("a", "b", "c");
Set<String> immutableSet = Set.of("a", "b", "c");
Map<String, Integer> immutableMap = Map.of(
    "a", 1,
    "b", 2,
    "c", 3
);

// From existing collection (Java 10+):
List<String> copy = List.copyOf(existingList);

// Stream to list (Java 16+):
List<String> streamed = stream.toList();
```

## Step 8: Concurrent Access

```java
// Thread-safe maps:
Map<String, String> concurrent = new ConcurrentHashMap<>();

// Copy-on-write for infrequent writes:
List<String> safeList = new CopyOnWriteArrayList<>();

// Synchronized wrapper (simple cases):
List<String> syncList = Collections.synchronizedList(new ArrayList<>());
synchronized (syncList) {  // Must sync on wrapper for iteration!
    for (String s : syncList) { ... }
}
```
