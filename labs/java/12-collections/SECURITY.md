# Collections — Security Implications

## Hash Collision DoS Attacks

HashMap and HashSet use hash codes for bucket assignment. An attacker who knows the hash function can craft keys that all hash to the same bucket:

```java
// If all keys collide, HashMap degrades to O(n) per operation
List<String> attackKeys = generateCollidingKeys();
Map<String, Object> map = new HashMap<>();
for (String key : attackKeys) {
    map.put(key, someData);  // O(n²) total!
}
```

**Java 8+ mitigation**: Treeification — when a bucket exceeds 8 entries, the linked list converts to a red-black tree (O(log n) instead of O(n)). This significantly reduces the attack surface.

**Additional mitigation**: Random hash seed (per-JVM randomization of String.hashCode()).

## Unmodifiable Collection Bypass

```java
// Seemingly safe
List<String> safe = Collections.unmodifiableList(originalList);
// But originalList can still be modified:
originalList.add("malicious");  // Changes visible through safe!

// True immutability:
List<String> immutable = List.copyOf(originalList);  // Defensive copy
```

## Memory Exhaustion

```java
// Attacker can cause OutOfMemoryError by adding unlimited entries:
public void processUserInput(String key, String value) {
    cache.put(key, value);  // Unlimited growth!
}

// Mitigation: Bounded cache
public class BoundedCache<K,V> extends LinkedHashMap<K,V> {
    private final int maxSize;
    public BoundedCache(int maxSize) { super(16, 0.75f, true); this.maxSize = maxSize; }
    @Override protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
        return size() > maxSize;
    }
}
```

## Deserialization Attacks

Serialized collections can carry malicious payloads:

```java
// Vulnerable deserialization:
try (ObjectInputStream ois = new ObjectInputStream(input)) {
    List<Object> list = (List<Object>) ois.readObject();
    // list could contain unexpected types!
}
```

**Mitigation**: Validate types after deserialization, use whitelist filtering with `ObjectInputFilter`.

## Synchronization Issues

Unsychronized collection access from multiple threads can cause subtle bugs:

```java
// Race condition:
if (!map.containsKey(key)) {
    map.put(key, computeValue(key));  // Another thread may have put it!
}

// Safe:
map.computeIfAbsent(key, this::computeValue);  // Atomic
```

## Information Leakage

Collection contents can leak sensitive information:
- Stack traces exposing map contents
- toString() of collections exposing internal data
- Timing attacks on hash maps (use constant-time comparison for security-critical keys)

## Best Practices

1. **Use immutable collections for constants** — prevents modification
2. **Defensive copies** — always copy untrusted input collections
3. **Bounded collections** — prevent resource exhaustion attacks
4. **Atomic operations** — use `computeIfAbsent()`, `merge()`, `putIfAbsent()`
5. **Synchronization wrappers** — use `ConcurrentHashMap` over `Collections.synchronizedMap()`
6. **Limit collection size** from untrusted sources
7. **Validate collection contents** after deserialization
