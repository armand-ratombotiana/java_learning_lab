# Debugging Hash Tables

## Common Issues

| Symptom | Likely Cause |
|---------|-------------|
| HashMap returns null for existing key | Mutable key changed after insertion |
| Performance degradation | Poor hashCode distribution → long chains |
| `ConcurrentModificationException` | Concurrent modification during iteration |
| `NullPointerException` | Null key/value in ConcurrentHashMap |
| Wrong bucket index | Power-of-2 capacity not maintained |

## Debugging Techniques

### Print Hash Distribution

```java
void printDistribution(Map<?, ?> map) {
    // Reflection needed to access internal table
    // Instead, measure bucket chain length
    System.out.println("Size: " + map.size());
    System.out.println("Collisions expected: " +
        (map.size() - (int)(map.size() / 0.75)));
}
```

### Check hashCode Quality

```java
void checkHashDistribution(Collection<?> keys) {
    Map<Integer, Integer> hashCount = new HashMap<>();
    for (Object key : keys) {
        int h = key.hashCode();
        hashCount.merge(h, 1, Integer::sum);
    }
    long collisions = hashCount.values().stream()
        .filter(c -> c > 1).count();
    System.out.println("Unique hashes: " + hashCount.size()
        + " of " + keys.size() + " keys");
    System.out.println("Collisions: " + collisions);
}
```

### Unit Testing

```java
@Test
void testHashMapContract() {
    Map<String, Integer> map = new HashMap<>();
    // Insert and retrieve
    assertNull(map.put("a", 1));
    assertEquals(Integer.valueOf(1), map.get("a"));
    // Update existing
    assertEquals(Integer.valueOf(1), map.put("a", 2));
    assertEquals(Integer.valueOf(2), map.get("a"));
    // Null key
    map.put(null, 0);
    assertEquals(Integer.valueOf(0), map.get(null));
    // Missing key
    assertNull(map.get("nonexistent"));
}

@Test
void testHashCodeQuality() {
    Set<Integer> buckets = new HashSet<>();
    for (int i = 0; i < 10000; i++) {
        buckets.add(Integer.valueOf(i).hashCode() % 16);
    }
    // Most buckets should be represented
    assertTrue(buckets.size() > 12);
}
```
