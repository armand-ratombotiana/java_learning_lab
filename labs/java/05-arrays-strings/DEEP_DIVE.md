# Arrays and Strings — Ultra Deep Dive

## 1. Array Covariance and Its Consequences

Java arrays are **covariant** — if `S` is a subtype of `T`, then `S[]` is a subtype of `T[]`. This was a deliberate design choice (Java 1.0) to allow generic-like operations before generics existed (Java 5).

```java
String[] strings = new String[10];
Object[] objects = strings;  // Covariant: String[] is Object[]
objects[0] = 42;  // ArrayStoreException at RUNTIME!
```

### Why Covariance?

Arrays are covariant because Java 1.0 had no generics. Consider:

```java
// Without generics (Java 1.0 style):
Object[] things = new String[10];  // Allowed due to covariance
things[0] = "hello";  // OK
things[1] = 42;       // ArrayStoreException
```

If arrays were invariant like generics, code like `Arrays.sort(Object[])` couldn't accept `String[]`.

### The ArrayStoreException Mechanism

The JVM inserts a runtime check on every `aastore` (array store) instruction:

```
  aload_1        // array reference
  iconst_0       // index 0
  aload_2        // value to store
  aastore        // CHECK: Is value assignable to array's component type?
                 // If not → throw ArrayStoreException
```

The check is `checkcast`-like — it verifies the runtime type of the value against the runtime component type of the array. This is why arrays have **reified** component types — the runtime knows the actual element type.

### Generics vs Arrays: Invariance

```java
List<String> strings = new ArrayList<>();
List<Object> objects = strings;  // COMPILE ERROR: List<String> is NOT List<Object>
```

Generics are **invariant** — `List<String>` is NOT a subtype of `List<Object>`. This prevents the array store problem at compile time.

### The "Mixing Generics with Arrays" Trap

```java
// Legal (but dangerous):
List<String>[] arrayOfLists = new List[10];  // Raw type array
arrayOfLists[0] = List.of("hello");    // OK
arrayOfLists[1] = List.of(42);         // COMPILE WARNING (unchecked)

// Illegal:
List<String>[] arrayOfLists = new List<String>[10];  // COMPILE ERROR
```

You cannot create arrays of parameterized types because type erasure prevents the JVM from checking the type parameter at runtime.

## 2. String Interning and the String Constant Pool

### How String Interning Works

```java
String a = "hello";           // String literal → interned
String b = "hello";           // Same literal → same object
String c = new String("hello"); // New object on heap
String d = c.intern();        // Returns the interned version

a == b  // true (same object from pool)
a == c  // false (different objects)
a == d  // true (c.intern() returned pool object)
```

### The String Pool Implementation

The string pool is a native hash table inside the JVM. Before Java 7, it lived in the PermGen. Since Java 7, it's in the main heap:

```
Heap Layout (Java 8+):
┌─────────────────────────────────────────┐
│ Young Generation (Eden + S0 + S1)       │
├─────────────────────────────────────────┤
│ Old Generation                          │
│   ┌────────────────────────────────┐    │
│   │ String Pool (hash table)       │    │
│   │ ┌─────┐ ┌─────┐               │    │
│   │ │"abc"│ │"xyz"│ ...            │    │
│   │ └─────┘ └─────┘               │    │
│   └────────────────────────────────┘    │
└─────────────────────────────────────────┘
```

### String Pool Tuning

- `-XX:StringTableSize` (default: 60013 in Java 11+, previously 1009)
- Too small → hash collisions, slower intern()
- Too large → memory waste
- Monitor via: `-XX:+PrintStringTableStatistics`

### The `intern()` Method Internals

```java
public native String intern();
```

The native implementation:
1. Hashes the String's char[] content
2. Looks up in the StringTable
3. If found, returns the pooled instance
4. If not found, adds the String to the pool and returns it
5. The whole operation is synchronized (StringTable lock)

## 3. Compact Strings (Java 9+, JEP 254)

Before Java 9, `String` stored characters as `char[]` (2 bytes per char — UTF-16).

Java 9 changed the internal representation:
```java
// Java 8:
final char[] value;  // Always UTF-16

// Java 9+:
private final byte[] value;  // LATIN-1 or UTF-16
private final byte coder;    // 0 = LATIN-1, 1 = UTF-16
```

### How It Works

```java
// If ALL chars are in ISO-8859-1 (Latin-1) range [0x00-0xFF]:
"hello" → byte[]: [104, 101, 108, 108, 111], coder = 0 (LATIN1)

// If ANY char is outside Latin-1:
"héllo — café" → byte[]: UTF-16 encoding, coder = 1 (UTF16)
```

### Memory Savings

| String content | Java 8 (char[]) | Java 9+ (byte[]) | Savings |
|---------------|-----------------|-----------------|---------|
| `"abc"` | 6 bytes + overhead | 3 bytes + overhead | ~50% |
| `"a b c"` | 10 bytes + overhead | 5 bytes + overhead | ~50% |
| `"日本語"` | 6 bytes + overhead | 6-12 bytes + overhead | 0-50% |

Average saving across typical applications: ~15-20% heap reduction.

### Implementation Impact

All String methods check the coder:
```java
public int length() {
    return value.length >> coder();  // If LATIN1, same length; if UTF16, half
}

public char charAt(int index) {
    if (isLatin1()) {
        return (char)(value[index] & 0xff);
    }
    return (char)(((value[index << 1] & 0xff) << 8) |
                  (value[(index << 1) + 1] & 0xff));
}
```

## 4. String Concatenation Optimization

### Java 8: StringBuilder

```java
String s = a + b + c;
// Desugared:
String s = new StringBuilder()
    .append(a)
    .append(b)
    .append(c)
    .toString();
```

**Problem**: Each concatenation in a loop creates a new StringBuilder:
```java
String s = "";
for (int i = 0; i < 1000; i++) {
    s += i;  // Creates new StringBuilder each iteration!
}
// Equivalent to:
for (int i = 0; i < 1000; i++) {
    s = new StringBuilder().append(s).append(i).toString();
}
```

This is O(n²) time and creates O(n) garbage objects.

### Java 9+: invokedynamic

```java
String s = a + b + c;
// Bytecode (Java 9+):
//  aload_1
//  aload_2
//  aload_3
//  invokedynamic #makeConcatWithConstants
```

The `StringConcatFactory` bootstrap method selects the strategy:

1. **BC_SB** (bytecode StringBuilder): Like Java 8 - fallback
2. **BC_SB_SIZED**: Pre-sized StringBuilder
3. **BC_SB_SIZED_EXACT**: Exact-size StringBuilder
4. **MH_SB_SIZED**: MethodHandle-based with size
5. **MH_SB_SIZED_EXACT**: MethodHandle-based exact size
6. **MH_INLINE_SIZE_EXACT** (default): Inline byte array copy

### Strategy 6: Inline Copy (Default)

```
1. Compute total length = a.length() + b.length() + c.length()
2. Allocate byte[] of exact size
3. Copy each string's bytes into the array
4. new String(byte[], coder)
```

No intermediate objects! The JIT can inline this entirely.

## 5. String Switch Compilation

```java
switch (s) {
    case "foo": return 1;
    case "bar": return 2;
    default: return 0;
}
```

The compiler generates:
```java
// 1. Check for null, then compute hashCode
int h = s.hashCode();
switch (h) {
    case 101574:  // "foo".hashCode()
        if (s.equals("foo")) return 1;
        else goto default;
    case 97299:   // "bar".hashCode()
        if (s.equals("bar")) return 2;
        else goto default;
    default: return 0;
}
```

Hash collisions are handled via the `if (s.equals(...))` guard. This makes string switches O(1) average case, O(n) worst case (all strings have same hashCode).

## 6. Array Initialization and Anonymous Arrays

```java
int[] array1 = {1, 2, 3, 4, 5};           // Array initializer
int[] array2 = new int[]{1, 2, 3, 4, 5}; // Anonymous array
```

Compiled bytecode:
```
  iconst_5
  newarray int
  dup
  iconst_0
  iconst_1
  iastore
  dup
  iconst_1
  iconst_2
  iastore
  // ... for each element
  astore array1
```

The `newarray` instruction allocates and zeroes memory. Then each element is stored individually. Large arrays may use `multianewarray` for multi-dimensional arrays.

## 7. Multi-Dimensional Arrays

Java represents multi-dimensional arrays as **arrays of arrays**:

```java
int[][] matrix = new int[3][4];
```

Memory layout:
```
matrix (int[][])          → [0x100, 0x200, 0x300]
  [0x100] (int[])         → [0, 0, 0, 0]
  [0x200] (int[])         → [0, 0, 0, 0]
  [0x300] (int[])         → [0, 0, 0, 0]
```

Each row is a separate heap object. `new int[3][4]` creates 4 objects (1 array of arrays + 3 row arrays).

Ragged arrays are possible:
```java
int[][] ragged = new int[3][];
ragged[0] = new int[1];
ragged[1] = new int[2];
ragged[2] = new int[3];
```

## 8. The `Arrays` Helper Class

`java.util.Arrays` provides critical methods:

```java
// Parallel sorting (ForkJoinPool):
Arrays.parallelSort(array);       // O(n log n) using multiple threads
Arrays.parallelPrefix(array, op); // Parallel prefix (scan)

// Binary search (requires sorted):
int idx = Arrays.binarySearch(array, key);

// Vectorized operations (JDK 16+):
int[] result = Arrays.copyOf(source, newLength); // Uses System.arraycopy (native)
```

## 9. The `Array` Reflection API

Dynamically creating arrays:
```java
int[] arr = (int[]) Array.newInstance(int.class, 10);
Array.set(arr, 0, 42);
int val = Array.getInt(arr, 0);
```

The `Array` class works with the JVM's internal array representation, bypassing the type system when needed.

## 10. String Deduplication (G1 GC Feature)

Since Java 8u20, the G1 garbage collector can **deduplicate** strings:

```
-XX:+UseStringDeduplication (G1 only)
```

When GC scans the heap, it identifies strings with identical `char[]`/`byte[]` content and makes them share the same backing array. This can save 10-30% of heap in string-heavy applications.
