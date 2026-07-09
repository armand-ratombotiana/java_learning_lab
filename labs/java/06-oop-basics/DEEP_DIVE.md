# OOP Basics — Ultra Deep Dive

## 1. Object Header Layout

Every Java object has a fixed-size header before its instance fields. On HotSpot 64-bit JVM:

### Mark Word (8 bytes)

The mark word encodes multiple pieces of information using a technique called **pointer compression with tag bits**:

| State | Mark Word Contents (64 bits) |
|-------|------------------------------|
| **Neutral (unlocked)** | `[hash:25] | [age:4] | [0:1] | [01:2]` — biased locking possible |
| **Biased** | `[thread:54] | [epoch:2] | [age:4] | [1:1] | [01:2]` |
| **Lightweight Locked** | `[ptr_to_lock_record:62] | [00:2]` |
| **Heavyweight Locked** | `[ptr_to_monitor:62] | [10:2]` |
| **GC Marking** | `[11:2]` |

- **hash**: Identity hash code (lazily computed, 25 bits)
- **age**: GC generation age (4 bits, max 15)
- **thread**: Thread ID for biased locking (54 bits)
- **epoch**: Biased locking revocation epoch
- **lock record/monitor**: Pointer to stack-lock record or OS monitor

### Klass Pointer (4 or 8 bytes)

Points to the object's class metadata (`Klass` structure):

- **Compressed OOPs enabled** (-XX:+UseCompressedClassPointers, default): 4 bytes
- **Compressed OOPs disabled**: 8 bytes

### Total Header Size

| Configuration | Header Size |
|--------------|-------------|
| 64-bit with compressed OOPs | 12 bytes (8 mark + 4 klass) |
| 64-bit without compressed OOPs | 16 bytes (8 mark + 8 klass) |
| 32-bit JVM | 8 bytes (4 mark + 4 klass) |

### Alignment

All objects are aligned to 8-byte boundaries (`-XX:ObjectAlignmentInBytes=8`). This means:

```java
// Object with just 1 byte field:
class OneByte {
    byte b;  // 1 byte
}
// Total: 12 (header) + 1 (field) + 3 (padding) = 16 bytes

// Object with 8-byte field:
class EightByte {
    long l;  // 8 bytes
}
// Total: 12 (header) + 4 (padding) + 8 (field) = 24 bytes
```

## 2. Field Ordering and `sun.misc.Unsafe` Offsets

The JVM may reorder fields for optimal alignment:

```java
class Mixed {
    byte b;      // offset 12
    int i;       // offset 16 (aligned to 4) → moved after alignment padding
    long l;      // offset 24 (aligned to 8) → or offset 20 depending on layout
    short s;     // offset 32 or 22
}
```

The declared order is NOT guaranteed. The JVM uses these rules (JVMS §4.11):
1. Fields of type `long` and `double` are 8-byte aligned
2. Fields of type `int` and `float` are 4-byte aligned
3. Fields of type `short` and `char` are 2-byte aligned
4. Fields of type `boolean`, `byte` are 1-byte aligned
5. References are 4-byte aligned (compressed) or 8-byte aligned (uncompressed)

The JVM sorts fields by size (long/double first, then int/float, then short/char, then byte/boolean, then references). This minimizes padding.

### Checking Field Offsets

```java
import sun.misc.Unsafe;
// ...
Unsafe unsafe = getUnsafe();  // Reflective access needed
long offsetB = unsafe.objectFieldOffset(Mixed.class.getDeclaredField("b"));
long offsetI = unsafe.objectFieldOffset(Mixed.class.getDeclaredField("i"));
// These offsets tell you the ACTUAL memory layout
```

## 3. OOP Compression (Compressed Object Pointers)

By default, HotSpot 64-bit uses 32-bit pointers to address heap objects (`-XX:+UseCompressedOops`).

### How Compression Works

Heap base is aligned to 8GB (or 32GB depending on configuration):
```
If heap ≤ 4GB: OOP = base + (encoded_offset × 8)
If heap ≤ 32GB: OOP = base + (encoded_offset × 8) (3-bit shift)
```

The encoded offset is a 32-bit value that, when decoded, can address:
- 4GB × 8 = 32GB of heap (with 3-bit shift)
- 4GB × 1 = 4GB of heap (zero-based, no shift)

### Trade-offs

| Aspect | Compressed OOPs | Non-compressed |
|--------|-----------------|----------------|
| Max heap addressable | ~32GB | Unlimited |
| Object header | 12 bytes | 16 bytes |
| Reference field size | 4 bytes | 8 bytes |
| Memory consumed by references | ~40% less | Full 64-bit |
| CPU for encode/decode | Slight overhead | None |

**Threshold**: When heap > 32GB, compressed OOPs are automatically disabled.

## 4. Pointer Chasing and Cache Effects

Every dereference of a reference field causes a pointer chase:

```java
class Node {
    int value;
    Node next;
}
```

Accessing `node.next.value` involves:
1. Read `node` reference (4-8 bytes)
2. Dereference to `Node` object for `node.next`
3. Read `next` reference (4-8 bytes)
4. Dereference to next `Node` object
5. Read `value` field (4 bytes)

This is 2 pointer dereferences and 3 field reads. Each dereference can be a cache miss (L1 → L2 → L3 → RAM), costing 100+ cycles.

### Data-Oriented Design Alternative

```java
// Original: Array of objects with pointers
Node[] nodes = new Node[1000];
// Memory: 1000 objects × (16B header + 8B fields) + array (4016B) ≈ 24016B
// Plus random heap allocation — poor cache behavior

// Optimized: Struct-of-arrays
class NodeArray {
    int[] values = new int[1000];
    long[] nexts = new long[1000];  // Indices, not pointers
}
// Memory: int[1000] (4000B) + long[1000] (8000B) = 12000B
// Sequential access → excellent cache prefetching
```

## 5. Object Identity vs Value Equality

### Identity (`==`)
- Compares reference values (heap addresses)
- O(1), single instruction
- True only if both references point to the same object

### Value Equality (`equals()`)
- Typically compares field values
- O(n) where n = number of significant fields
- True for logically equivalent objects

### The Contract Paradox

```java
Integer a = new Integer(42);  // Explicit new → fresh object
Integer b = new Integer(42);  // Fresh object
Integer c = Integer.valueOf(42);   // From cache
Integer d = 42;                     // Autoboxed → cache

a == b       // false
a == c       // false
c == d       // true (same cache entry)
a.equals(b)  // true
```

### Identity and Mutation

```java
// Immutable objects: identity is less important
String s1 = "hello";
String s2 = ("hel" + "lo").intern();
s1 == s2  // true (both interned)

// Mutable objects: identity matters for synchronization
synchronized (mutex) {  // mutex identity is critical
    criticalSection();
}
```

## 6. The `hashCode()` and `equals()` Contract

The `Object` specification requires:
1. If `a.equals(b)`, then `a.hashCode() == b.hashCode()`
2. `hashCode()` must be consistent (same object → same hash across one execution)
3. If `!a.equals(b)`, `hashCode()` may still be equal (hash collisions are OK)

### Default Implementations

```java
// Object.hashCode():
// - Typically based on memory address or random number
// - Stored in the mark word (lazily computed on first call)
public native int hashCode();

// Object.equals():
public boolean equals(Object obj) {
    return (this == obj);  // Reference equality
}
```

### The `record` Solution

Records (Java 16+) automatically generate value-based equals/hashCode/toString:
```java
record Point(int x, int y) {}
// equals compares x and y components
// hashCode = 31 * Integer.hashCode(x) + Integer.hashCode(y)
```

## 7. The `toString()` Method and String Conversion

```java
Object obj = ...;
String s = "" + obj;  // Desugars to: String.valueOf(obj)
```

`String.valueOf(Object)`:
```java
public static String valueOf(Object obj) {
    return (obj == null) ? "null" : obj.toString();
}
```

Array toString is NOT overridden — arrays inherit `Object.toString()`:
```java
int[] arr = {1, 2, 3};
System.out.println(arr);  // Prints: [I@1a2b3c (class name + hashCode)
System.out.println(Arrays.toString(arr));  // Prints: [1, 2, 3]
```

## 8. The `clone()` Method

```java
class MyClass implements Cloneable {
    int[] data;
    
    @Override
    protected Object clone() throws CloneNotSupportedException {
        MyClass cloned = (MyClass) super.clone();  // Shallow copy
        cloned.data = this.data.clone();  // Deep copy the array
        return cloned;
    }
}
```

`Object.clone()`:
- Allocates memory by copying the source object's bit pattern
- Does NOT call any constructor
- Creates a shallow copy (reference fields point to same objects)
- Checks `Cloneable` marker interface — throws `CloneNotSupportedException` if not implemented
- Has special JVM support (`JVM_Clone` native method)

## 9. Static Field Memory Layout

Static fields live in the class's `InstanceKlass` metadata (in Metaspace), not in heap objects:

```java
class Counter {
    static int count = 0;      // In Metaspace (class data)
    static final int MAX = 100; // In Metaspace (constant pool)
    int instanceCount;          // In heap (object instances)
}
```

Compile-time constants (`static final` with literal initializers) are inlined by the compiler:
```java
System.out.println(Counter.MAX);  // Bytecode: bipush 100 (no field access!)
```

## 10. Inner Classes and Synthetic Fields

Non-static inner classes hold an implicit reference to the enclosing instance:

```java
class Outer {
    class Inner {
        // Synthetic field: final Outer this$0;
        void access() {
            Outer.this.doSomething();  // Uses this$0
        }
    }
}
```

The compiler generates:
- A synthetic `this$0` field of type `Outer`
- A synthetic constructor parameter
- The `$` in class names: `Outer$Inner.class`
