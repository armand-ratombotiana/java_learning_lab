# Security Considerations for Hash Tables

## HashDoS Attack

The most famous hash table security vulnerability: an attacker sends many keys that hash to the same bucket, causing O(n²) total time.

### Java 7 Vulnerability (CVE-2012-2739)

In Java 7, `HashMap` used separate chaining with linked lists. An attacker could craft many colliding string keys (knowing the hash function), causing:
- All inserts go to one bucket
- Each insert is O(n) (scanning the chain)
- Total time: O(n²) for n inserts
- Result: denial of service (web server slow or down)

### Java 8 Mitigation

Java 8 introduced **treeification**: when a bucket chain exceeds `TREEIFY_THRESHOLD = 8`, the linked list is converted to a Red-Black tree. This bounds per-bucket operations to O(log n), turning the attack into a minor slowdown instead of a full DoS.

### Additional Mitigations

- **Randomized hash seed**: Java 8+ randomizes string hash seed per JVM instance (caused a regression that was later refined)
- **Hashing with random salt**: each JVM uses different hash seed for strings

## Information Leakage

Hash-based data structures can leak information through:
- **Timing**: how long a `get()` takes reveals whether key exists (in open addressing)
- **Order**: `HashMap` iteration order may leak information about hash distribution
- **Size**: `map.size()` can reveal cardinality information

## Injections

```java
// User-controlled keys
String userInput = request.getParameter("key");
map.put(userInput, value);  // Possible injection of special keys
```

If user input is used as keys, ensure proper validation — especially if keys are used in security decisions.

## Denial of Service

- **Huge maps**: unbounded map growth can exhaust heap memory
- **Maps with mutable keys**: can cause undetectable key loss and memory leaks
- **Hash code computation**: expensive hashCode implementations can be exploited (e.g., deep hashCode on complex object graphs)

## Thread Safety

HashMap is **not thread-safe**. Concurrent access can cause:
- Infinite loops during resize (Java 7 — fixed in Java 8)
- Lost updates
- Null pointer exceptions

Use `ConcurrentHashMap` for concurrent access, or `Collections.synchronizedMap()` as a fallback.

## Secure Practices

- Use `EnumMap` for enum keys (faster, more secure against hash collisions)
- Use `IdentityHashMap` when reference identity matters
- Never store sensitive data as keys if iteration order could leak information
- Clear maps containing sensitive data explicitly (not relying on GC)
