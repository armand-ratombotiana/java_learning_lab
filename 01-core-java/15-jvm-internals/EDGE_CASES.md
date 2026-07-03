# Module 15: JVM Internals - Edge Cases & Pitfalls

---

## Pitfall 1: Memory Leaks

### ❌ Wrong
Keeping strong references to objects that are no longer needed prevents the GC from reclaiming them.
```java
// Adding objects to a static collection and never removing them
public static List<Object> cache = new ArrayList<>();
public void doWork(Object obj) {
    cache.add(obj); // Never removed!
}
```

### ✅ Correct
Use appropriate data structures (like `WeakHashMap`) or ensure you remove objects when they are no longer needed.

---

## Pitfall 2: StackOverflowError

### ❌ Wrong
```java
public void recursive() {
    recursive(); // Infinite recursion exhausts the thread stack
}
```

### ✅ Correct
Ensure recursive methods have a base case to terminate execution before exceeding stack capacity.

---

## Pitfall 3: OutOfMemoryError: Java heap space

### ❌ Wrong
Loading massive files entirely into memory or instantiating too many large objects simultaneously.

### ✅ Correct
Process large data streams incrementally, increase heap size (`-Xmx`), or optimize memory footprint.