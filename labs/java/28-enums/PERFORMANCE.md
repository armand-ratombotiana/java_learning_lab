# Performance: Enums

## Enum vs Constants

### Comparison
```java
// Constant
public static final int STATUS_ACTIVE = 1;
if (status == STATUS_ACTIVE) { ... }      // int comparison

// Enum
if (status == Status.ACTIVE) { ... }       // reference comparison
```
Enum comparison uses reference equality (==), which is as fast as int comparison (a single JVM instruction).

## EnumSet Performance
EnumSet<Day> with ≤64 constants uses a long bitmask:
- add/remove/contains: O(1) bitwise operations
- Iterator: O(n) to scan bits
- Bulk operations (union, intersect): O(1) bitwise OR/AND
- Memory: 8 bytes for long + object overhead

## EnumMap Performance
EnumMap uses ordinal-indexed array:
- get/put: O(1) direct array access (no hashing)
- Iterator: O(n) sequential array traversal
- Memory: array of size n, no hash table overhead

## Switch on Enum
The JVM compiles switch on enum using:
- tableswitch: O(1) jump table (when ordinals are dense/consecutive)
- lookupswitch: O(log n) binary search (when sparse, e.g., ordinal gaps)

## Memory Overhead
Each enum constant is a singleton object:
- Object header: ~12-16 bytes (HotSpot)
- Enum fields: name (reference to interned String), ordinal (int)
- Class definition: ~1-2KB for the enum class

## Performance Tips
- Cache Enum.values() if called frequently (creates clone each time)
- Use EnumSet for bitmask-style operations
- Use EnumMap instead of HashMap when keys are enums
- Prefer switch with default for completeness (JIT can optimize better)
- Enum ordinal-based logic is fast but fragile — use sparingly
