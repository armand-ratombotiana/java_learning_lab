# Mock Interview Transcript: Performance Antipatterns

## Interviewer: Staff Engineer, Amazon
## Candidate: Staff Java developer
## Time: 45 minutes
## Focus: Common antipatterns, debugging, profiling-driven optimization

---

**Q1: What are the most common Java performance antipatterns you've seen in production?**

**Candidate**: (1) String concatenation in loops (O(n²) copying). (2) Using `String.split()` (regex compilation each call) or `replaceAll()` in loops. (3) `ArrayList.indexOf()` in lookup loops (O(n²)). (4) Thread.sleep() for timing. (5) Unbounded thread pools. (6) Double-checked locking without volatile. (7) Object allocation in hot paths. (8) `Synchronized` on method instead of specific lock. (9) ThreadLocal memory leaks in thread pools. (10) Capturing lambda in loops (creating many lambda instances).

**Interviewer**: Analyze this code for performance issues.

```java
List<String> process(List<Integer> ids) {
    List<String> result = new ArrayList<>();
    for (Integer id : ids) {
        String data = database.findById(id).toString();
        result.add(data);
    }
    return result;
}
```

**Candidate**: Issues: (1) Sequential database calls — N round trips. Fix: `CompletableFuture.allOf()` for parallel calls. (2) `toString()` after each database call — consider mapping in the query. (3) No error handling — one failure fails all. (4) Database connection per call — use batch query. (5) `Integer` boxing — if `ids` can be `int[]`, use primitive. (6) If `ids` is large, use streaming to avoid holding all results in memory.

**Interviewer**: How do you detect and fix ThreadLocal memory leaks in thread pools?

**Candidate**: ThreadLocal values are stored in the thread's `ThreadLocalMap`. In a thread pool, threads are reused — ThreadLocal values from previous tasks persist, potentially causing: (1) Memory leaks (data holds references). (2) Cross-request data leakage (next request sees previous data). Detection: (1) Heap dump — check ThreadLocalMap entries in pooled threads. (2) `-XX:+PrintThreadLocalStats`. Fix: (1) Always call `ThreadLocal.remove()` in `finally` block. (2) Use `ScopedValue` (Java 21+) instead. (3) `try-finally` cleanup pattern.

**Interviewer**: What's wrong with this caching approach?

```java
class Cache {
    Map<String, byte[]> cache = new HashMap<>();
    
    byte[] get(String key) { return cache.get(key); }
    void put(String key, byte[] data) { cache.put(key, data); }
}
```

**Candidate**: Several issues: (1) No eviction policy — grows unbounded → OOM. Fix: use `Caffeine` or `Guava Cache` with max size and TTL. (2) Not thread-safe. Fix: `ConcurrentHashMap`. (3) Storing `byte[]` of deserialized data may duplicate data already in cache. (4) No null handling — cache returns null for missing, not `Optional`. (5) No metrics — can't monitor hit rate.

**Interviewer**: Identify antipatterns in this concurrent code.

```java
synchronized class Counter {
    private int count;
    public int getCount() { return count; }
    public void increment() { count++; }
}
```

**Candidate**: (1) `synchronized` on class locks the class object (not specific instance) — coarse-grained. Fix: use `AtomicInteger` or synchronized on a private field. (2) `count++` is three operations (read, increment, write) — even with `synchronized`, it's an anti-pattern when `AtomicInteger` exists. (3) Getter returns count without synchronization — broken visibility. Fix: make `count` volatile. Better: replace with `AtomicInteger` entirely.

**Interviewer**: How does `String.split()` cause performance issues? What's the fix?

**Candidate**: `String.split(regex)` compiles the regex Pattern on every call. For `str.split(",")`, a simple `,` doesn't need regex — but `split` treats it as regex anyway. Fix: (1) Pre-compile: `Pattern.compile(",").split(str)`. (2) Use `StringTokenizer` for simple delimiters (but it's legacy). (3) Use `StringUtils.split` from Apache Commons (which doesn't use regex for single char). (4) Manual parsing with `indexOf` and `substring` in high-throughput paths.

**Interviewer**: What's the performance implication of `parallelStream()` on a `LinkedList`?

**Candidate**: `LinkedList` has a terrible Spliterator — it splits by walking the list (O(n) per split). The resulting parallel stream: (1) Split cost outweighs parallel benefit for most cases. (2) Poor locality (linked nodes are scattered in memory). (3) `ArrayList` has an excellent Spliterator (random access by index, O(1) split). Rule: parallel streams work well with random-access collections (ArrayList, arrays, IntStream.range).

**Interviewer**: How do you identify the cause of high lock contention?

**Candidate**: Tool: async-profiler in lock mode: `./profiler.sh -e lock -d 30 -f lock.html <pid>`. JFR: `jdk.JavaMonitorEnter` and `jdk.ThreadPark` events. Look for: (1) Which locks are contended. (2) Which threads are holding the locks. (3) How long threads wait. Fixes: (1) Reduce critical section size. (2) Use `StampedLock` optimistic reads. (3) Use `LongAdder` instead of `AtomicLong` for counters. (4) Lock striping (like ConcurrentHashMap). (5) Lock-free data structures.

**Interviewer**: Final: You're reviewing a PR. What performance antipatterns do you look for?

**Candidate**: (1) Boxing in loops (`int` → `Integer` in collections). (2) `StringBuilder` not pre-sized. (3) Stream `collect(toList())` when `toList()` (Java 16+) works. (4) `synchronized(this)` in a library class. (5) `new Object()` as a lock. (6) `HashMap` without initial capacity when size is known. (7) Catching `Exception` without logging. (8) `Thread.sleep()` in test code. (9) `System.gc()` calls. (10) Reflection in hot paths. (11) Unbounded collection growth. (12) Missing `close()` in resources.

---

## Feedback

**Strengths**:
- Comprehensive antipattern identification
- Real production issue analysis
- Concrete fix recommendations
- Detection tool usage (async-profiler, JFR)
- Code review antipattern checklist

**Areas for Improvement**:
- Could discuss JMH for verifying fixes
- Mention `-XX:+AlwaysPreTouch` for performance consistency

**Score**: 5/5 — Expert performance antipatterns knowledge
