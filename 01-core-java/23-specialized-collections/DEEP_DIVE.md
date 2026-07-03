# Deep Dive: Specialized Collections

## 1. Beyond ArrayList and HashMap
While `ArrayList`, `HashSet`, and `HashMap` cover 95% of daily programming needs, the Java Collections Framework provides highly specialized data structures optimized for specific memory constraints, performance requirements, or key types.

## 2. WeakHashMap: Preventing Memory Leaks
A standard `HashMap` holds *strong references* to its keys and values. If you put an object into a `HashMap` as a key, the Garbage Collector (GC) will *never* reclaim that object, even if no other part of the application references it. This is a common source of memory leaks in caches.

`WeakHashMap` solves this. It stores its keys using `WeakReference`s.
*   **How it works**: If a key is no longer strongly referenced anywhere else in the application, the GC is allowed to reclaim the key. When the key is reclaimed, the `WeakHashMap` automatically removes the entire key-value entry from the map.
*   **Use Case**: Caching metadata about objects whose lifecycle you do not control (e.g., caching parsed data for a `ClassLoader` or attaching extra state to a `Thread`).

```java
Map<Object, String> cache = new WeakHashMap<>();
Object key = new Object();
cache.put(key, "Data");

key = null; // The only strong reference is gone
System.gc(); // The GC runs, reclaims the key, and the entry is removed from the cache
```

## 3. IdentityHashMap: Reference Equality
A standard `HashMap` uses the `equals()` and `hashCode()` methods to determine if two keys are the same.
`IdentityHashMap` intentionally violates the `Map` contract: it uses **reference equality** (`==`) instead of object equality (`equals()`). Two keys are considered equal *only* if they are the exact same object in memory.

*   **How it works**: It uses `System.identityHashCode(object)` instead of `object.hashCode()`.
*   **Use Case**: 
    *   Topology preservation during graph traversal or object serialization (keeping track of which exact objects have already been visited to prevent infinite loops in cyclic graphs).
    *   When you need to use objects as keys, but those objects have broken or mutable `equals()`/`hashCode()` implementations.

## 4. EnumSet: High-Performance Bit Vectors
If you need a `Set` where all elements belong to a single `Enum` type, you should never use a `HashSet`. You should use `EnumSet`.

*   **How it works**: Internally, `EnumSet` is represented as a bit vector (usually a single `long` if the enum has 64 or fewer constants). `add()` is a bitwise OR, `remove()` is a bitwise AND NOT, and `contains()` is a bitwise AND.
*   **Performance**: It is incredibly fast and consumes almost no memory compared to a `HashSet` (which requires Node objects, hash calculations, and an array).
*   **Use Case**: Replacing traditional bit flags (e.g., `public static final int FLAG_READ = 1; FLAG_WRITE = 2;`) with type-safe enums while maintaining the exact same performance.

```java
enum Permission { READ, WRITE, EXECUTE }
Set<Permission> perms = EnumSet.of(Permission.READ, Permission.WRITE);
```

## 5. EnumMap: Array-Backed Speed
Similar to `EnumSet`, if you need a `Map` where the keys are enums, use `EnumMap` instead of `HashMap`.

*   **How it works**: Internally, it is just an array. The ordinal value of the enum (`enum.ordinal()`) is used directly as the array index.
*   **Performance**: Extremely fast lookups ($O(1)$ with no hash calculation or collision resolution overhead) and very compact memory footprint.
*   **Use Case**: Mapping enum states to specific handlers or configuration values (e.g., mapping `State.START` to a `StartHandler` object).