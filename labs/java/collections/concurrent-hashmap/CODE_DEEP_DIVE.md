# ConcurrentHashMap Code Deep Dive

This lab focuses on the practical usage of `ConcurrentHashMap`, specifically highlighting how developers often misuse it and how to use its atomic compound operations correctly.

## 💻 The "Check-Then-Act" Anti-Pattern

A common mistake is assuming that because the map is thread-safe, all operations on it are thread-safe.

```java file="labs/java/collections/concurrent-hashmap/SOLUTION/ConcurrentAntiPattern.java"
package collections.concurrenthashmap;

import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentAntiPattern {
    
    private static final ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

    // ❌ THE ANTI-PATTERN
    // Even though the map is thread-safe, this method is NOT thread-safe!
    public static void incrementBad(String key) {
        // Thread A and Thread B might both read '0' at the same time
        Integer current = map.get(key); 
        
        if (current == null) {
            map.put(key, 1);
        } else {
            // Both threads write '1', losing an update!
            map.put(key, current + 1); 
        }
    }
}
```
This is a classic **Race Condition**. The `get()` and `put()` are individually atomic, but the combination of them is not.

## 🛡️ The Correct Solution: Atomic Compound Operations

Java 8 introduced functional methods to `ConcurrentHashMap` to solve this exact problem without requiring external locks.

```java file="labs/java/collections/concurrent-hashmap/SOLUTION/ConcurrentBestPractices.java"
package collections.concurrenthashmap;

import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentBestPractices {
    
    private static final ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

    // ✅ THE CORRECT WAY: compute()
    // The entire remapping function is executed atomically under the bucket lock.
    public static void incrementGood(String key) {
        map.compute(key, (k, current) -> (current == null) ? 1 : current + 1);
    }

    // ✅ THE CORRECT WAY: merge()
    // Even cleaner for simple accumulation.
    public static void incrementBest(String key) {
        // If key doesn't exist, put 1. If it does, apply Integer::sum
        map.merge(key, 1, Integer::sum);
    }

    // ✅ THE CORRECT WAY: putIfAbsent() / computeIfAbsent()
    // Ideal for lazy initialization (e.g., creating a cache entry)
    private static final ConcurrentHashMap<String, ExpensiveObject> cache = new ConcurrentHashMap<>();

    public static ExpensiveObject getOrCreate(String key) {
        // The lambda is ONLY executed if the key is missing.
        // It is guaranteed to be executed atomically by exactly one thread.
        return cache.computeIfAbsent(key, k -> new ExpensiveObject(k));
    }
    
    static class ExpensiveObject {
        ExpensiveObject(String id) {
            // Simulate expensive DB call or computation
        }
    }
}
```

## 🔍 Key Takeaways
1. **Never use `get()` followed by `put()`** to update a value in a concurrent environment.
2. Use **`compute()`**, **`merge()`**, or **`computeIfAbsent()`**. These methods leverage the internal node-level locks of `ConcurrentHashMap` to guarantee atomicity of the entire operation.
3. **Do not put blocking operations inside the lambda** of `computeIfAbsent()`. Because it holds the bucket lock, a slow operation (like a network call) will block all other threads trying to access that specific bucket.