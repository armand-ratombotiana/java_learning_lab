# Google Interview Guide — Java Academy

## Interview Process for Java Roles

### Rounds & Timeline
- **Phone Screen (45 min):** 1-2 coding problems via Google Docs (no IDE)
- **On-site (4-5 rounds):** 2 coding, 1 system design, 1 JVM/Java deep dive, 1 behavioral (Googleyness)
- **Timeline:** 4-8 weeks from application to offer
- **Java-Specific:** Google does not care about language — you can use C++, Python, Go. However, if you choose Java they expect deep JVM knowledge.

### Java-Specific Evaluation
- If you write Java, interviewers will probe `java.util.concurrent`, memory model, GC
- Google loves `com.google.common.collect` (Guava) — knowing `ImmutableList`, `Multimap`, `BiMap` is a signal
- They expect you to understand why `ArrayList` vs `LinkedList` matters at scale
- **No Java EE / Spring questions** — Google builds everything in-house

---

## Top Problems by Module

### Module: Concurrency

#### Problem: Print in Order
- **LC:** 1114
- **Difficulty/Frequency:** Easy / High
- **Problem Statement:** Three threads print "first", "second", "third" in order. Guarantee that `first()` runs before `second()` before `third()`.
- **Interview Walkthrough:** Start with `volatile` + busy-wait (bad). Then show `CountDownLatch`. Finally discuss why `Semaphore` is cleaner. Google wants to see you understand happens-before guarantees.
- **Solution 1 vs Solution 2:** `CountDownLatch` is simple but requires one latch per dependency. `Semaphore(0)` with `release()`/`acquire()` is more scalable. Google prefers the semaphore approach — it mirrors real distributed coordination.
- **Java Code:**
```java
import java.util.concurrent.Semaphore;

/**
 * Guarantees first() -> second() -> third() execution order
 * using Semaphores for inter-thread signaling.
 *
 * LC 1114 — Google interview standard solution
 */
public class Foo {

    private final Semaphore second = new Semaphore(0);
    private final Semaphore third  = new Semaphore(0);

    /**
     * First method — no dependencies, starts immediately.
     */
    public void first(Runnable printFirst) throws InterruptedException {
        printFirst.run();
        second.release();
    }

    /**
     * Second method — waits for first() to complete.
     */
    public void second(Runnable printSecond) throws InterruptedException {
        second.acquire();
        printSecond.run();
        third.release();
    }

    /**
     * Third method — waits for second() to complete.
     */
    public void third(Runnable printThird) throws InterruptedException {
        third.acquire();
        printThird.run();
    }
}
```
- **Company Evaluation Criteria:** Correctness of happens-before chain, avoiding deadlock, not using sleep-based waits.
- **Follow-ups:** Make N threads print in order; add timeout.

#### Problem: FizzBuzz Multithreaded
- **LC:** 1195
- **Difficulty/Frequency:** Medium / Medium
- **Problem Statement:** Four threads print fizz, buzz, fizzbuzz, or number. Coordinate correctly.
- **Interview Walkthrough:** Use a shared counter with `synchronized` `wait()`/`notifyAll()`. Google calls this "thread coordination" — they want to see you handle spurious wakeups.
- **Solution 1 vs Solution 2:** `synchronized` + `wait/notify` is the textbook answer. `ReentrantLock` + `Condition` can be more precise. Google usually accepts either but will ask "what if a thread is interrupted?"
- **Java Code:**
```java
import java.util.function.IntConsumer;

/**
 * Thread-safe FizzBuzz using synchronized wait/notify.
 * Four threads coordinate to print one sequence.
 *
 * LC 1195 — demonstrates thread rendezvous pattern
 */
public class FizzBuzz {

    private final int n;
    private int current = 1;

    /**
     * @param n upper bound to print up to
     */
    public FizzBuzz(int n) {
        this.n = n;
    }

    /**
     * Prints "fizz" if current is divisible by 3 only.
     */
    public synchronized void fizz(Runnable printFizz) throws InterruptedException {
        while (current <= n) {
            if (current % 3 == 0 && current % 5 != 0) {
                printFizz.run();
                current++;
                notifyAll();
            } else {
                wait();
            }
        }
    }

    /**
     * Prints "buzz" if current is divisible by 5 only.
     */
    public synchronized void buzz(Runnable printBuzz) throws InterruptedException {
        while (current <= n) {
            if (current % 5 == 0 && current % 3 != 0) {
                printBuzz.run();
                current++;
                notifyAll();
            } else {
                wait();
            }
        }
    }

    /**
     * Prints "fizzbuzz" if current divisible by both 3 and 5.
     */
    public synchronized void fizzbuzz(Runnable printFizzBuzz) throws InterruptedException {
        while (current <= n) {
            if (current % 3 == 0 && current % 5 == 0) {
                printFizzBuzz.run();
                current++;
                notifyAll();
            } else {
                wait();
            }
        }
    }

    /**
     * Prints the number otherwise.
     */
    public synchronized void number(IntConsumer printNumber) throws InterruptedException {
        while (current <= n) {
            if (current % 3 != 0 && current % 5 != 0) {
                printNumber.accept(current);
                current++;
                notifyAll();
            } else {
                wait();
            }
        }
    }
}
```
- **Company Evaluation Criteria:** Handling of spurious wakeups (always loop), thread safety, deadlock avoidance.
- **Follow-ups:** Use a `CyclicBarrier` instead; what if threads arrive out of order?

#### Problem: Design Bounded Blocking Queue
- **LC:** 1188
- **Difficulty/Frequency:** Medium / High
- **Problem Statement:** Implement a thread-safe bounded blocking queue with `enqueue` and `dequeue`.
- **Interview Walkthrough:** `ReentrantLock` with two `Condition` objects (notFull, notEmpty). Google expects this exact pattern — it mirrors their internal `BoundedBuffer`.
- **Solution 1 vs Solution 2:** `ArrayBlockingQueue` wrapper is trivial but demonstrates nothing. Hand-rolled `ReentrantLock` + `Condition` shows deep understanding.
- **Java Code:**
```java
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Bounded blocking queue using ReentrantLock and two Conditions.
 * Thread-safe producer-consumer with backpressure.
 *
 * LC 1188 — Google expects this exact hand-rolled approach
 */
public class BoundedBlockingQueue {

    private final Queue<Integer> queue;
    private final int capacity;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();
    private final Condition notFull  = lock.newCondition();

    /**
     * @param capacity maximum elements the queue can hold
     */
    public BoundedBlockingQueue(int capacity) {
        this.capacity = capacity;
        this.queue = new LinkedList<>();
    }

    /**
     * Thread-safe enqueue — blocks if queue is full.
     * @param element value to add
     * @throws InterruptedException if thread is interrupted while waiting
     */
    public void enqueue(int element) throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() == capacity) {
                notFull.await();
            }
            queue.offer(element);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Thread-safe dequeue — blocks if queue is empty.
     * @return next element from the queue
     * @throws InterruptedException if thread is interrupted while waiting
     */
    public int dequeue() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                notEmpty.await();
            }
            int value = queue.poll();
            notFull.signal();
            return value;
        } finally {
            lock.unlock();
        }
    }

    /**
     * @return current number of elements in the queue
     */
    public int size() {
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }
}
```
- **Company Evaluation Criteria:** Correct lock/unlock in finally, proper condition usage, avoiding deadlock.
- **Follow-ups:** Add timeout to operations; implement with `synchronized` + `wait/notify`; compare performance.

---

### Module: Collections & Data Structures

#### Problem: Insert Delete GetRandom O(1)
- **LC:** 380
- **Difficulty/Frequency:** Medium / High
- **Problem Statement:** Design a data structure supporting insert, remove, getRandom in O(1) average time.
- **Interview Walkthrough:** HashMap + ArrayList. Swap with last element on remove. Google loves this because it tests creative use of standard collections.
- **Solution 1 vs Solution 2:** HashMap + ArrayList (AC). LinkedHashSet is trivial but doesn't demonstrate understanding.
- **Java Code:**
```java
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * O(1) insert, remove, getRandom using HashMap + ArrayList.
 * Swap-with-last strategy avoids O(n) remove cost.
 *
 * LC 380 — Google's favorite "use standard collections creatively" problem
 */
public class RandomizedSet {

    private final Map<Integer, Integer> valueToIndex;
    private final List<Integer> values;
    private final Random rand;

    /** Initialize data structures. */
    public RandomizedSet() {
        valueToIndex = new HashMap<>();
        values = new ArrayList<>();
        rand = new Random();
    }

    /**
     * Inserts a value. Returns false if already present.
     * @param val value to insert
     * @return true if inserted, false if already exists
     */
    public boolean insert(int val) {
        if (valueToIndex.containsKey(val)) {
            return false;
        }
        valueToIndex.put(val, values.size());
        values.add(val);
        return true;
    }

    /**
     * Removes a value by swapping it with the last element.
     * @param val value to remove
     * @return true if removed, false if not found
     */
    public boolean remove(int val) {
        if (!valueToIndex.containsKey(val)) {
            return false;
        }
        int index = valueToIndex.get(val);
        int lastIndex = values.size() - 1;
        if (index != lastIndex) {
            int lastValue = values.get(lastIndex);
            values.set(index, lastValue);
            valueToIndex.put(lastValue, index);
        }
        values.remove(lastIndex);
        valueToIndex.remove(val);
        return true;
    }

    /**
     * Returns a random element with uniform probability.
     * @return random element from current set
     */
    public int getRandom() {
        return values.get(rand.nextInt(values.size()));
    }
}
```
- **Company Evaluation Criteria:** Maintainability of indices on remove, edge cases (single element, duplicate remove).
- **Follow-ups:** Thread-safe version; support weighted random.

---

### Module: JVM Internals & Memory

#### Problem: LRU Cache
- **LC:** 146
- **Difficulty/Frequency:** Medium / Very High
- **Problem Statement:** Design an LRU cache with O(1) get and put.
- **Interview Walkthrough:** `LinkedHashMap` is the easy path. Hand-rolled `HashMap` + doubly-linked list shows deep understanding of JVM object references and memory layout.
- **Solution 1 vs Solution 2:** `LinkedHashMap.removeEldestEntry()` — 3 lines, but Google says "this tells us nothing." Hand-rolled with Node class demonstrates pointer manipulation skills.
- **Java Code:**
```java
import java.util.HashMap;
import java.util.Map;

/**
 * LRU Cache using HashMap + doubly-linked list.
 * O(1) get and put. Evicts least recently used on capacity exceeded.
 *
 * LC 146 — Google expects the hand-rolled linked list version
 */
public class LRUCache {

    private static class Node {
        int key, value;
        Node prev, next;
        Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final Map<Integer, Node> map;
    private final Node head, tail;

    /**
     * @param capacity maximum number of entries before eviction
     */
    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();
        this.head = new Node(-1, -1);
        this.tail = new Node(-1, -1);
        head.next = tail;
        tail.prev = head;
    }

    /**
     * Returns value if key exists, else -1. Moves accessed node to head.
     * @param key lookup key
     * @return cached value or -1
     */
    public int get(int key) {
        Node node = map.get(key);
        if (node == null) return -1;
        removeNode(node);
        addToHead(node);
        return node.value;
    }

    /**
     * Inserts or updates a key-value pair. Evicts LRU if at capacity.
     * @param key cache key
     * @param value cache value
     */
    public void put(int key, int value) {
        Node node = map.get(key);
        if (node != null) {
            node.value = value;
            removeNode(node);
            addToHead(node);
            return;
        }
        if (map.size() == capacity) {
            Node lru = tail.prev;
            removeNode(lru);
            map.remove(lru.key);
        }
        Node newNode = new Node(key, value);
        map.put(key, newNode);
        addToHead(newNode);
    }

    private void addToHead(Node node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }
}
```
- **Company Evaluation Criteria:** Correct pointer manipulation, handling capacity=1, thread safety discussion.
- **Follow-ups:** Make it thread-safe with `ReadWriteLock`; implement LFU; persistent LRU on disk.

---

### Module: Modern Java (Streams & Lambdas)

#### Problem: Group Anagrams
- **LC:** 49
- **Difficulty/Frequency:** Medium / High
- **Problem Statement:** Given array of strings, group anagrams together.
- **Interview Walkthrough:** Sort each string as key, or use char count as key. Google loves the char count approach because it's O(n*k) vs O(n*k*log k).
- **Solution 1 vs Solution 2:** Sorted string key (simpler). Char count key (faster, GC pressure from arrays). Google prefers char count but will discuss memory trade-off.
- **Java Code:**
```java
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Groups anagrams using character-count fingerprint as key.
 * O(n * k) time using int[26] arrays as Map keys.
 *
 * LC 49 — Google tests HashMap + stream fluency
 */
public class GroupAnagrams {

    /**
     * Groups strings by their anagram signature.
     * @param strs input array of strings
     * @return list of groups, each group is a list of anagrams
     */
    public List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> map = new HashMap<>();
        for (String s : strs) {
            String key = fingerprint(s);
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
        }
        return new ArrayList<>(map.values());
    }

    /**
     * Computes a canonical fingerprint for anagram grouping.
     * Uses int[26] count encoded as a delimited string.
     * @param s input string
     * @return fingerprint string unique to anagram family
     */
    private String fingerprint(String s) {
        int[] count = new int[26];
        for (char c : s.toCharArray()) {
            count[c - 'a']++;
        }
        StringBuilder sb = new StringBuilder();
        for (int c : count) {
            sb.append(c).append('#');
        }
        return sb.toString();
    }
}
```
- **Company Evaluation Criteria:** Understanding of HashMap internals, string hashing, char encoding.
- **Follow-ups:** Group anagrams from a large file (memory-constrained); anagrams in non-English Unicode.

---

### Module: Serialization & Networking

#### Problem: Serialize and Deserialize Binary Tree
- **LC:** 297
- **Difficulty/Frequency:** Hard / High
- **Problem Statement:** Design an algorithm to serialize and deserialize a binary tree.
- **Interview Walkthrough:** Pre-order traversal with sentinel markers for null nodes. Google uses this to test both recursion depth understanding and string encoding decisions.
- **Solution 1 vs Solution 2:** BFS/level-order uses more space but avoids recursion depth issues. Pre-order DFS is simpler. Google wants DFS + sentinel because it mirrors their RPC serialization patterns.
- **Java Code:**
```java
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Serializes/deserializes binary tree to/from compact string.
 * Uses pre-order traversal with 'X' sentinel for null nodes.
 *
 * LC 297 — Google tests serialization format decision-making
 */
public class Codec {

    private static final String DELIMITER = ",";
    private static final String NULL      = "X";

    /**
     * Encodes a tree to a single string using pre-order traversal.
     * @param root tree root
     * @return serialized string
     */
    public String serialize(TreeNode root) {
        StringBuilder sb = new StringBuilder();
        serializeHelper(root, sb);
        return sb.toString();
    }

    private void serializeHelper(TreeNode node, StringBuilder sb) {
        if (node == null) {
            sb.append(NULL).append(DELIMITER);
            return;
        }
        sb.append(node.val).append(DELIMITER);
        serializeHelper(node.left, sb);
        serializeHelper(node.right, sb);
    }

    /**
     * Decodes a serialized string back to a binary tree.
     * @param data serialized string
     * @return tree root
     */
    public TreeNode deserialize(String data) {
        Queue<String> queue = new LinkedList<>(Arrays.asList(data.split(DELIMITER)));
        return deserializeHelper(queue);
    }

    private TreeNode deserializeHelper(Queue<String> queue) {
        String val = queue.poll();
        if (NULL.equals(val)) {
            return null;
        }
        TreeNode node = new TreeNode(Integer.parseInt(val));
        node.left = deserializeHelper(queue);
        node.right = deserializeHelper(queue);
        return node;
    }

    /** Binary tree node definition. */
    private static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }
}
```
- **Company Evaluation Criteria:** Handling nulls, nested structures, recursion depth limits, encoding choices.
- **Follow-ups:** Support cyclic graphs; use JSON format; what if the tree is very deep (stack overflow)?

---

## JVM/Concurrency Deep Dive Questions

Google asks the most aggressive JVM questions of any company:

1. **Explain Java's happens-before relationship** — give examples from `synchronized`, `volatile`, `final`, and `java.util.concurrent` classes. Google wants you to trace JMM guarantees for each.
2. **How does JIT inline a method?** — Walk through `-XX:+PrintCompilation`, inline thresholds, "hot method" detection. They've asked "what is the inline cache in C2?"
3. **GC deep dive** — "Your app has 50ms GC pauses. Walk through G1GC, ZGC, Shenandoah and choose one. Explain remembered sets, card tables, concurrent marking."
4. **What is a safepoint?** — When does the JVM stop the world? How do biased locks interact with safepoints? (Biased locking was removed in JDK 15 — they'll ask if you know.)
5. **Explain memory ordering on x86 vs ARM** — "Your concurrent code works on x86 but fails on Graviton. Why?" Tests understanding of store-store vs load-load reordering.

## System Design with Java

1. **Design Google Search's autocomplete (trie)** — Implement in Java using `HashMap<Character, TrieNode>`. Discuss memory overhead vs compressed trie for JVM heap. Google expects you to consider `-Xmx`, GC pressure from char[] objects.
2. **Design Google Docs real-time collaboration** — Java `java.util.concurrent` locks? No — they want to see OT (operational transformation) or CRDT. Java implementation using `ConcurrentSkipListMap` for version vectors.
3. **Design web crawler (distributed)** — Java NIO for fetching, `BlockingQueue` for URL frontier, Bloom filter for deduplication. Google asks about `ByteBuffer` vs `String` memory efficiency.

## Behavioral Questions (STAR)

1. **"Tell me about a time you improved a system."** — Java project: reduced GC pauses from 2s to 100ms by migrating from `String` concatenation to `StringBuilder` and tuning G1GC. *S: 200ms GC pauses every 5 minutes. T: Reduce to <200ms. A: Profiled with async-profiler, found String.concat in hot path, replaced with pooled StringBuilder, tuned -XX:G1HeapRegionSize. R: P50 latency dropped 40%.*
2. **"Why Google?"** — "I want to work on systems where Java runs at extreme scale. I've studied Google's JVM tuning for Borg and I want to contribute to the infrastructure that runs the world's largest Java fleet."
3. **"Tell me about a conflict."** — Lead argued for `synchronized`, I argued for `StampedLock`. *S: Contention bottleneck on shared cache. T: Reduce thread blocking. A: Built A/B test — `StampedLock` showed 2.3x throughput under high contention. R: Adopted `StampedLock`, shared findings in team lunch presentation.*
4. **"Tell me about a time you took initiative."** — Introduced JUnit 5 + Testcontainers in a team using JUnit 3 + manual DB tests. *S: 8-hour manual regression. T: Automate. A: Prototyped migration, demo'd at engineering all-hands. R: 40-minute fully automated CI pipeline.*
5. **"Tell me about a failure."** — Deployed misconfigured G1GC leading to OOM in production. *S: New service launched with default JVM settings. T: Understand G1GC better. A: Deep-dived into GC logs, wrote team guide on G1GC tuning for our workload. R: Zero OOM incidents in 6 months.*

## Study Plan

| Priority | Labs | Focus |
|----------|------|-------|
| P0 | 30-jvm-internals, 45-gc-deep-dive, 44-jit-compilation, 38-memory-model | JVM internals |
| P0 | 16-concurrency, 41-threading-deep-dive, 42-locking-synchronization, 48-structured-concurrency | Concurrency |
| P1 | 12-collections, 13-streams, 14-lambdas, 21-java-21-features | Modern Java & data structures |
| P1 | 37-performance-profiling, 47-profiling-observability, 52-performance-antipatterns | Performance |
| P2 | 32-networking, 18-io-nio, 35-serialization | I/O & serialization |
| P2 | 31-testing, 36-reactive-programming | Testing & reactive (less focus) |

**Preparation Path:** Solve LC 146, 380, 297, 1114, 1188, 1195 in Java. Study every JVM flag mentioned in labs 44-46. Write a concurrent data structure from scratch. Practice STAR stories aligning to Google's "Googleyness" rubric.

## Tips

- **Don't use Java EE / Spring / Hibernate** — Google does not use them and may view it negatively
- **Know your Guava** — `ImmutableMap`, `Multiset`, `FluentIterable` are cultural signals
- **JVM flags matter** — If you write `-XX:+UseG1GC -XX:+UseStringDeduplication` in a system design, that's a strong signal
- **Google loves micro-optimization discussion** — `StringBuilder` vs `+`, `EnumMap` vs `HashMap`, `ArrayList` vs `LinkedList` — prepare to defend choices at scale
- **Practice without IDE** — Google phone screens are raw docs. Write compilable Java in a plain text editor
- **The "Googleyness" round is real** — Prepare 3 failure stories. Google values humility and intellectual curiosity above all
- **Do not assume Java is your advantage** — Many Google interviewers prefer C++ or Go. If you pick Java, you must know JVM internals cold