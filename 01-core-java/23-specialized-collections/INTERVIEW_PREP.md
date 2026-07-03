# Interview Preparation: Specialized Collections

This document covers advanced questions related to WeakHashMap, IdentityHashMap, and Enum collections.

## Q1: How does a `WeakHashMap` prevent memory leaks, and what is its primary use case?
**Answer:**
A `WeakHashMap` stores its keys using `WeakReference` objects. In Java, if an object is only reachable via weak references, the Garbage Collector is allowed to reclaim it. 
When the GC reclaims a key, the `WeakHashMap` detects this (using a `ReferenceQueue`) and automatically removes the corresponding entry (both key and value) from the map.
**Primary Use Case**: Caching metadata for objects whose lifecycles you do not control. For example, if you want to attach extra data to a `Thread` object, using a standard `HashMap<Thread, Data>` would prevent the thread from ever being garbage collected after it dies. A `WeakHashMap` allows the thread to be collected, automatically cleaning up the cached data.

## Q2: What is the "Value Trap" in a `WeakHashMap`?
**Answer:**
The "Value Trap" occurs when the *value* in a `WeakHashMap` holds a strong reference back to its *key*.
Because the map holds a strong reference to the value, and the value holds a strong reference to the key, the key is now strongly reachable. This prevents the key from ever being garbage collected, defeating the purpose of the `WeakHashMap` and causing a memory leak.

## Q3: Explain why and when you would use an `IdentityHashMap` instead of a standard `HashMap`.
**Answer:**
An `IdentityHashMap` uses reference equality (`==`) instead of object equality (`equals()`) to compare keys. It also uses `System.identityHashCode()` instead of the object's overridden `hashCode()` method.
You use it when:
1.  You need to track exact object instances in memory, regardless of their logical equality. This is crucial for algorithms that traverse object graphs (like serialization or deep cloning) to detect circular references and prevent infinite loops.
2.  You need to use objects as keys, but those objects have broken, mutable, or expensive `equals()`/`hashCode()` implementations.

## Q4: Why is `EnumSet` preferred over `HashSet` when storing Enum values?
**Answer:**
`EnumSet` is a highly specialized, specialized implementation designed specifically for enums.
1.  **Performance**: Internally, it is represented as a bit vector (a single `long` if the enum has <= 64 constants). Operations like `add()`, `remove()`, and `contains()` are executed as extremely fast, single CPU instructions (bitwise OR, AND NOT, AND). `HashSet` requires computing hash codes, resolving collisions, and managing `Node` objects.
2.  **Memory**: `EnumSet` requires almost zero memory overhead (just the object header and a `long` primitive). `HashSet` creates a backing array and a wrapper `Node` object for every element.

## Q5: Can you use a String literal as a key in a `WeakHashMap`? Why or why not?
**Answer:**
You *can* syntactically, but you **should not**.
String literals (e.g., `"myKey"`) are interned and stored in the JVM's String Pool. The JVM holds strong references to strings in the pool so they can be reused. Because the string literal is strongly referenced by the JVM, it will never be garbage collected. Therefore, the entry in the `WeakHashMap` will never be automatically removed, resulting in a permanent cache entry (a memory leak if done repeatedly).