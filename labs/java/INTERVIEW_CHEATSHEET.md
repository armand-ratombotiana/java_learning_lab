# Java Interview Cheatsheet — Quick Reference

## Java 8/11/17/21 Features

| Feature | Java 8 | Java 11 | Java 17 | Java 21 |
|---------|--------|---------|---------|---------|
| LTS | Yes | Yes (since Sep 2021) | Yes | Yes |
| Lambda expressions | Yes | - | - | - |
| Streams | Yes | - | - | - |
| Optional | Yes | - | - | - |
| CompletableFuture | Yes | - | - | - |
| Date & Time API | Yes | - | - | - |
| `var` (local variable type inference) | - | Yes | - | - |
| `Collection.toArray(IntFunction)` | - | Yes | - | - |
| `String.isBlank()`, `lines()`, `strip()` | - | Yes | - | - |
| `Files.readString()`, `writeString()` | - | Yes | - | - |
| Sealed classes | - | - | Yes | - |
| Records | - | - | Yes (preview in 14) | - |
| Pattern matching for `instanceof` | - | - | Yes | - |
| Switch expressions | - | - | Yes | - |
| Text blocks | - | - | Yes (preview in 13) | - |
| `NullPointerException` messages | - | - | Yes | - |
| Virtual threads | - | - | - | Yes |
| Structured concurrency | - | - | - | Yes (preview) |
| Record patterns | - | - | - | Yes |
| Pattern matching for switch | - | - | - | Yes |
| Sequenced collections | - | - | - | Yes |
| String templates | - | - | - | Yes (preview) |
| Unnamed classes & main methods | - | - | - | Yes (preview) |

---

## Common Algorithm Implementations in Java

### Binary Search
```java
int binarySearch(int[] arr, int target) {
    int lo = 0, hi = arr.length - 1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        if (arr[mid] == target) return mid;
        if (arr[mid] < target) lo = mid + 1;
        else hi = mid - 1;
    }
    return -1;
}
// Time: O(log n), Space: O(1)
```

### Quick Select (kth smallest)
```java
int quickSelect(int[] arr, int k) {
    shuffle(arr);
    int lo = 0, hi = arr.length - 1;
    while (lo < hi) {
        int p = partition(arr, lo, hi);
        if (p < k) lo = p + 1;
        else if (p > k) hi = p - 1;
        else break;
    }
    return arr[k];
}
// Time: O(n) avg, O(n^2) worst; Space: O(1)
```

### Sliding Window (max sum subarray size K)
```java
int maxSumSubarray(int[] arr, int k) {
    int sum = 0, max = 0;
    for (int i = 0; i < arr.length; i++) {
        sum += arr[i];
        if (i >= k - 1) {
            max = Math.max(max, sum);
            sum -= arr[i - (k - 1)];
        }
    }
    return max;
}
// Time: O(n), Space: O(1)
```

### Two Pointers (pair sum)
```java
boolean hasPairSum(int[] arr, int target) {
    Arrays.sort(arr);
    int l = 0, r = arr.length - 1;
    while (l < r) {
        int sum = arr[l] + arr[r];
        if (sum == target) return true;
        if (sum < target) l++;
        else r--;
    }
    return false;
}
// Time: O(n log n), Space: O(1)
```

### BFS (graph)
```java
void bfs(Map<Integer, List<Integer>> graph, int start) {
    Queue<Integer> q = new LinkedList<>();
    Set<Integer> seen = new HashSet<>();
    q.offer(start);
    seen.add(start);
    while (!q.isEmpty()) {
        int node = q.poll();
        for (int neighbor : graph.getOrDefault(node, List.of())) {
            if (seen.add(neighbor)) q.offer(neighbor);
        }
    }
}
// Time: O(V+E), Space: O(V)
```

### DFS (graph)
```java
void dfs(Map<Integer, List<Integer>> graph, int node, Set<Integer> seen) {
    if (!seen.add(node)) return;
    for (int neighbor : graph.getOrDefault(node, List.of())) {
        dfs(graph, neighbor, seen);
    }
}
// Time: O(V+E), Space: O(V)
```

### Trie
```java
class Trie {
    static class Node { Node[] children = new Node[26]; boolean isEnd; }
    Node root = new Node();
    void insert(String w) {
        Node cur = root;
        for (char c : w.toCharArray()) {
            int idx = c - 'a';
            if (cur.children[idx] == null) cur.children[idx] = new Node();
            cur = cur.children[idx];
        }
        cur.isEnd = true;
    }
    boolean search(String w) {
        Node cur = root;
        for (char c : w.toCharArray()) {
            cur = cur.children[c - 'a'];
            if (cur == null) return false;
        }
        return cur.isEnd;
    }
}
```

---

## Collections Framework Quick Reference

| Class | Ordering | Null Keys | Null Values | Thread-Safe | Notes |
|-------|----------|-----------|-------------|-------------|-------|
| `HashMap` | No | Yes | Yes | No | O(1) put/get, treeify on bucket >8 |
| `LinkedHashMap` | Insertion/Access | Yes | Yes | No | LRU via `removeEldestEntry` |
| `TreeMap` | Sorted (Comparable) | No | Yes | No | Red-black tree, O(log n) |
| `ConcurrentHashMap` | No | No | No | Yes | Lock striping, Java 8+ CAS |
| `ArrayList` | Index | N/A | Yes | No | O(1) get, O(n) insert/delete |
| `LinkedList` | Insertion | N/A | Yes | No | O(n) get, O(1) head/tail |
| `HashSet` | No | Yes | N/A | No | Backed by HashMap |
| `LinkedHashSet` | Insertion | Yes | N/A | No | Backed by LinkedHashMap |
| `TreeSet` | Sorted | No | N/A | No | Backed by TreeMap |
| `PriorityQueue` | Priority | No | No | No | Min-heap, O(log n) offer/poll |
| `ArrayDeque` | Insertion | No | Yes | No | Resizable array, O(1) head/tail |
| `CopyOnWriteArrayList` | Index | N/A | Yes | Yes | Snapshot iterator, good for read-heavy |
| `ConcurrentLinkedDeque` | Insertion | No | No | Yes | Non-blocking, CAS-based |

---

## Concurrency Cheat Sheet

### Thread Safety Techniques (in order of preference)
1. **Immutability** — `record`, `final`, `Collections.unmodifiable*`
2. **Thread confinement** — stack-local variables, `ThreadLocal`
3. **Synchronized** — `synchronized` block, `Collections.synchronized*`
4. **Locks** — `ReentrantLock`, `ReadWriteLock`, `StampedLock`
5. **Atomic classes** — `AtomicInteger`, `AtomicReference`, `LongAdder`
6. **Concurrent collections** — `ConcurrentHashMap`, `CopyOnWriteArrayList`
7. **`volatile`** — visibility guarantee (no atomicity)

### Key Classes

| Class | What It Does |
|-------|-------------|
| `CountDownLatch` | Wait for N operations to complete. Single-use. |
| `CyclicBarrier` | N threads wait at a barrier. Reusable. |
| `Phaser` | Dynamic barrier — parties can register/deregister. |
| `Semaphore` | Limits concurrent access to a resource. |
| `Exchanger` | Two threads exchange objects at a synchronization point. |
| `CompletableFuture` | Async computation with composition (`thenApply`, `thenCompose`, `thenCombine`). |
| `ForkJoinPool` | Work-stealing pool for divide-and-conquer. |
| `ReentrantLock` | Reentrant mutual exclusion with fairness option. |
| `StampedLock` | Optimistic read + ReentrantReadWriteLock behavior. |
| `VarHandle` (Java 9+) | Memory access with volatile/opaque/acquire/release semantics. |

### `synchronized` vs `ReentrantLock`
| Aspect | synchronized | ReentrantLock |
|--------|-------------|---------------|
| Fairness | No | Optional |
| tryLock | No | Yes |
| Condition | Single wait set | Multiple `Condition` objects |
| Interruptible | No | Yes (`lockInterruptibly()`) |
| Performance | Optimized (biased locking) | Slightly more overhead |

### `volatile` Semantics
- Guarantees **visibility**: write to volatile variable happens-before any read.
- Does **NOT** guarantee atomicity of compound operations (e.g., `count++`).
- Use for flags, status indicators, but NOT for counters.

---

## Stream API Operations Summary

### Intermediate (lazy, return Stream<T>)
| Operation | Description |
|-----------|-------------|
| `filter(Predicate)` | Keep elements matching predicate |
| `map(Function)` | Transform each element |
| `flatMap(Function<T, Stream<R>>)` | Flatten nested streams |
| `distinct()` | Removes duplicates (uses `equals`) |
| `sorted()` / `sorted(Comparator)` | Sort elements |
| `peek(Consumer)` | Side-effect (debugging) |
| `limit(long)` | Truncate to N elements |
| `skip(long)` | Discard first N elements |
| `takeWhile(Predicate)` (Java 9+) | Take until false |
| `dropWhile(Predicate)` (Java 9+) | Drop until false |

### Terminal (eager, produce result)
| Operation | Returns |
|-----------|---------|
| `forEach(Consumer)` | `void` |
| `toList()` (Java 16+) | `List<T>` |
| `collect(Collector)` | Various |
| `reduce(BinaryOperator)` | `Optional<T>` |
| `count()` | `long` |
| `anyMatch(Predicate)` | `boolean` |
| `allMatch(Predicate)` | `boolean` |
| `noneMatch(Predicate)` | `boolean` |
| `findFirst()` | `Optional<T>` |
| `findAny()` | `Optional<T>` |
| `min(Comparator)` | `Optional<T>` |
| `max(Comparator)` | `Optional<T>` |

### Collectors
```java
Collectors.toList()
Collectors.toSet()
Collectors.toMap(keyMapper, valueMapper)
Collectors.toUnmodifiableList()   // Java 10+
Collectors.groupingBy(classifier)
Collectors.partitioningBy(predicate)
Collectors.joining(delimiter)
Collectors.summarizingInt(ToIntFunction)
Collectors.reducing(identity, mapper, op)
```

---

## JVM Flags Cheat Sheet

### Heap
```
-Xms4g              // Initial heap size
-Xmx4g              // Max heap size
-Xmn2g              // Young generation size
-XX:NewRatio=2      // Old:Young ratio
-XX:SurvivorRatio=8 // Eden:Survivor ratio
-XX:MaxMetaspaceSize=256m
```

### GC Selection
```
-XX:+UseG1GC                // G1 (default since Java 9)
-XX:+UseZGC                 // ZGC (Java 15+)
-XX:+UseShenandoahGC        // Shenandoah
-XX:+UseParallelGC          // Parallel (throughput)
-XX:+UseSerialGC            // Serial (single-threaded)
```

### GC Logging (Java 9+)
```
-Xlog:gc*                  // All GC logging
-Xlog:gc:file=gc.log       // To file
-Xlog:gc+heap=debug        // Heap details
-Xlog:gc+age=trace         // Age table
```

### Performance
```
-XX:+AlwaysPreTouch        // Touch heap at startup
-XX:+UseStringDeduplication // Deduplicate String objects
-XX:+UseCompressedOops     // Compressed object pointers
-XX:+UnlockExperimentalVMOptions
-XX:+DisableExplicitGC     // Disable System.gc()
-XX:MaxInlineLevel=15      // Inlining depth
```

### Debugging
```
-XX:+PrintFlagsFinal       // Print all JVM flags
-XX:+UnlockDiagnosticVMOptions
-XX:+PrintAssembly         // Print JIT assembly (need hsdis)
-XX:+TraceClassLoading     // Log class loading
-XX:+TraceClassUnloading   // Log class unloading
-XX:NativeMemoryTracking=summary  // NMT
-XX:StartFlightRecording=duration=60s,filename=rec.jfr
```

---

## GC Algorithms Comparison

| GC | Pause | Throughput | Heap | Java Version | Best For |
|----|-------|-----------|------|-------------|----------|
| Serial | Long | Low | <100M | Always | Single-core, tiny heap |
| Parallel | Medium | High | <4G | Always | Throughput-focused batch |
| G1 | Short | High | Up to 512G | Java 8+ (default 9+) | Balanced, most workloads |
| ZGC | <1ms | Medium | Up to 16TB | Java 15+ (production 21+) | Large heap, low latency |
| Shenandoah | <5ms | Medium | Up to 512G | Java 12+ (production 15+) | Low latency, moderate heap |
| Epsilon | None | Max | Any | Java 11+ | Testing, short-lived tasks |

---

*Last updated: July 2026. Cross-reference with `CRACKING_THE_INTERVIEW_GUIDE.md` for detailed strategies.*