# Data Types — Ultra Deep Dive

## 1. Primitive vs Reference Semantics: The JVM Perspective

Java's type system is divided into two fundamentally different categories at the JVM level (JVMS §2.2 and §2.3):

### Primitives (Value Types in JVM Terminology)
| Type | Size | JVM Category | Default Value | Wrapper | Range |
|------|------|-------------|---------------|---------|-------|
| `boolean` | JVM-dependent (usually 1 byte) | `int`-like (stored as 0/1) | `false` (0) | `Boolean` | true/false |
| `byte` | 8 bits (1 byte) | `int`-like | `0` | `Byte` | -128..127 |
| `short` | 16 bits (2 bytes) | `int`-like | `0` | `Short` | -32768..32767 |
| `char` | 16 bits (2 bytes) | `int`-like | `\u0000` (0) | `Character` | 0..65535 |
| `int` | 32 bits (4 bytes) | `int` | `0` | `Integer` | -2^31..2^31-1 |
| `long` | 64 bits (8 bytes) | `long` (2 stack slots) | `0L` | `Long` | -2^63..2^63-1 |
| `float` | 32 bits (4 bytes) | `float` | `0.0f` | `Float` | ±1.4E-45..±3.4E+38 |
| `double` | 64 bits (8 bytes) | `double` (2 stack slots) | `0.0d` | `Double` | ±4.9E-324..±1.7E+308 |

**Critical observation**: `boolean` has no dedicated JVM type. It's stored as `int` (0/1). Even in arrays, `boolean[]` uses the `baload`/`bastore` instructions which treat the array as a byte array.

### Memory Layout on the Operand Stack

The JVM is a stack-based machine. When a method executes, each call creates a stack frame with an operand stack and local variable array:

```java
int a = 10;    // bipush 10; istore_1
long b = 20L;  // ldc2_w #20; lstore_2
int c = a + 5; // iload_1; iconst_5; iadd; istore 4
```

Bytecode analysis:
- `int a = 10` → `bipush 10; istore_1` (push byte 10 onto stack, pop into local var slot 1)
- `long b = 20L` → `ldc2_w #20; lstore_2` (load 64-bit constant from pool, store into slots 2-3)
- `int c = a + 5` → `iload_1; iconst_5; iadd; istore 4` (load a, push 5, add, store result)

`long` and `double` occupy TWO local variable slots and TWO stack slots. This is a legacy from the 32-bit JVM design and remains in Java 21.

## 2. Primitive Memory Layout in the Heap

Primitives stored in heap objects (as instance fields) follow JVM alignment rules:

```java
public class FieldLayout {
    byte b;     // offset 12 or 16 (after header)
    int i;      // offset 16 or 20 (aligned to 4)
    short s;    // offset 20 or 24
    long l;     // offset 24 or 32 (aligned to 8)
}
```

**Object header** (HotSpot 64-bit):
- Mark word: 8 bytes (identity hash, GC info, lock state)
- Klass pointer: 4 bytes (with compressed OOPs) or 8 bytes (without)
- Fields then follow in declaration order, with padding for alignment

**Field reordering**: The JVM *may* reorder fields for alignment optimization, BUT `sun.misc.Unsafe` field offsets are stable. The declared order is NOT guaranteed — see JVMS §4.11.

## 3. Type Promotion Rules: The JVM Binary Compatibility Perspective

### Binary Numeric Promotion (JLS §5.6.2)

When operators apply to mixed types:
1. If either operand is `double`, the other is converted to `double`
2. Else if either is `float`, the other → `float`
3. Else if either is `long`, the other → `long`
4. Else both are promoted to `int` (even for `byte`, `short`, `char`)

**This means**:
```java
byte a = 10;
byte b = 20;
byte c = a + b;  // COMPILE ERROR! a + b is int
byte c = (byte)(a + b);  // OK
```

**Why?** The JVM has no bytecode instructions for `byte` arithmetic. The `iadd` instruction works on `int`. The compiler emits `iload`, `iadd`, and then `i2b` for narrowing.

### Constant Expression Promotion

Compile-time constants (JLS §15.29) receive special treatment:
```java
final byte a = 10;
final byte b = 20;
byte c = a + b;  // OK! Both are constant expressions → compile-time narrowing
byte d = a + 200; // ERROR: 210 overflows byte range
```

This is handled during constant folding in the compiler's Attr phase.

## 4. Type Inference Deep Dive

### Local Variable Type Inference (`var`)

`var` (JEP 286, Java 10) delegates to the compiler's type inference machinery. The inferred type is the *declared type* of the initializer expression — NOT its runtime type.

```java
var list = new ArrayList<String>();  // Inferred: ArrayList<String>
var list2 = List.of("a", "b");       // Inferred: List<String> (NOT immutable list impl)
var x = (CharSequence) "hello";      // Inferred: CharSequence
```

The inference is entirely compile-time. The bytecode contains no trace of `var`.

### Generic Type Inference (JLS §15.12.2.7)

When you write:
```java
<T> T identity(T t) { return t; }
var result = identity("hello"); // Inferred: String
```

The compiler uses *invocation type inference* — it unifies the type argument with the provided argument type. This is solved by the `com.sun.tools.javac.comp.Infer` class, which implements the JLS inference algorithm approximately as follows:

1. **Reduction**: Reduce the target type constraint `T` from arguments
2. **Incorporation**: Propagate bounds (T <: Object, String <: T → String <: T <: Object → T = String)
3. **Resolution**: Find instantiation satisfying all bounds

### Target-Type Inference (for lambdas)

```java
List<String> list = Collections.emptyList();
// Collections.<String>emptyList() inferred from target type List<String>
```

And in lambdas:
```java
Function<String, Integer> f = s -> s.length();
// Compiler infers: s is String, return type is Integer (from Function<String, Integer>)
```

## 5. Project Valhalla: Value Types (Preview)

JEP 401 (Primitive Classes, still in preview as of Java 21) introduces **value types** — user-defined primitives:

```java
// Hypothetical Valhalla syntax (may change):
value class Point {
    private int x;
    private int y;
    
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
```

**Key properties**:
- **Flat memory layout**: No object header, no pointer indirection
- **Identity-free**: No synchronization, no reference equality, no null
- **Atomicity**: 64-bit-or-smaller values are read/written atomically
- **Null-hostile**: Primitives cannot be null

### Memory Comparison

```java
// Current: Array of Point objects
Point[] points = new Point[1000];
// Memory: 1000 * (16-byte header + 8-byte fields + alignment padding) ≈ 28KB
// Plus: 1000 pointers in the array (8KB with compressed OOPs)
// Total: ~36KB

// Valhalla: Array of Point values
Point[] points = new Point[1000];
// Memory: 1000 * 8 bytes (just x + y) = 8KB
// No headers, no pointers!
```

## 6. Autoboxing/Unboxing: The Hidden Cost

Each boxing operation creates a new object on the heap:

```java
Integer x = 42;  // Integer.valueOf(42) — may use cache
int y = x;       // x.intValue()
```

### The Integer Cache

`Integer.valueOf()` uses a cache for values [-128, 127]:
```java
public static Integer valueOf(int i) {
    if (i >= IntegerCache.low && i <= IntegerCache.high)
        return IntegerCache.cache[i + (-IntegerCache.low)];
    return new Integer(i);
}
```

The cache size can be adjusted via JVM flag `-XX:AutoBoxCacheMax=<size>`. This affects ALL autoboxing of integers in that range.

### Performance Impact

```java
// Slow: creates 10,000 Integer objects
Integer sum = 0;
for (int i = 0; i < 10000; i++) {
    sum += i;  // Desugars to: sum = Integer.valueOf(sum.intValue() + i);
}

// Fast: no boxing
int sum2 = 0;
for (int i = 0; i < 10000; i++) {
    sum2 += i;
}
```

Benchmark (JMH):
```
Benchmark                          Mode  Cnt     Score    Error  Units
BoxingBench.slowBoxing             avgt    5  1234.567 ± 12.34  ns/op
BoxingBench.fastPrimitive          avgt    5     5.678 ±  0.12  ns/op
```

Boxing adds ~200x overhead in this trivial example due to allocation.

## 7. Zero-Length Arrays and Default Values

All primitive arrays are initialized to zero-values:
```java
int[] arr = new int[1000];  // All zeros
boolean[] flags = new boolean[1000];  // All false (0)
```

This is a JVM guarantee (§2.3.1, §2.4). The allocation instruction (`newarray`, `anewarray`, `multianewarray`) zeroes memory as part of allocation.

## 8. Conversion Categories

JLS §5 defines five conversion categories:

1. **Identity**: Same type → no conversion needed
2. **Widening primitive**: `byte → short → int → long → float → double`
3. **Narrowing primitive**: Reverse of widening (requires explicit cast)
4. **Widening reference**: Subtype → supertype
5. **Narrowing reference**: Supertype → subtype (requires checkcast at bytecode level)

**Widening primitive conversions can lose precision**:
```java
int big = 1234567890;
float f = big;           // f = 1.23456794E9 (precision loss!)
long l = 1234567890123456789L;
double d = l;           // Precision loss for large long values
```

## 9. The `String` Type: Not a Primitive, But Special

`String` is a reference type, but it receives special treatment:
- String concatenation uses `invokedynamic` (Java 9+, JEP 280)
- String literals are interned automatically
- String switch statements use hash-code + equals logic
- String constants live in the class constant pool

## 10. Records as Nominal Tuples

Records (JEP 395, Java 16) provide a compact syntax for data carriers:
```java
public record Point(int x, int y) { }
```

The compiler generates:
- A final class extending `java.lang.Record`
- `private final` fields
- Canonical constructor
- Accessor methods (`x()`, `y()`)
- `equals()`, `hashCode()`, `toString()`

Records are *transparent* — the component names and types are accessible via reflection (`RecordComponent`).

## 11. Type Inference and Generics: The `Infer` Class

The `com.sun.tools.javac.comp.Infer` class implements the type inference algorithm from JLS §18. The algorithm works through:

1. **Constraint Reduction**: For each expression in the method invocation, generate type constraints
2. **Incorporation**: Propagate new bounds from existing ones (e.g., `T <: Integer` and `Integer <: T` → `T = Integer`)
3. **Resolution**: Find the most specific type that satisfies all bounds

### Example: Inference with Wildcards

```java
List<? extends Number> list = new ArrayList<Integer>();
// The compiler infers: ? extends Number captures as CAP#1
// CAP#1 has upper bound Number
// list.add(Integer.valueOf(1)) → COMPILE ERROR: CAP#1 might not be Integer
```

The capture conversion (JLS §5.1.10) creates fresh type variables for wildcards. This is why you can't add to a `List<? extends Number>` — the compiler doesn't know the specific captured type.

## 12. `switch` on Primitives: Type Promotion in Action

```java
switch (byteVal) {
    case 1:  // byte constant
    case 2:  // byte constant
}
```

The `tableswitch`/`lookupswitch` instructions work on `int`. The JVM promotes `byte`, `short`, `char` to `int` before the switch. The compiler ensures case label constants fit in the type's range.

## 13. The `java.lang.foreign.MemoryLayout` (Java 22+)

Project Panama's Foreign Function & Memory API allows precise control over memory layouts:

```java
MemoryLayout structLayout = MemoryLayout.structLayout(
    ValueLayout.JAVA_INT.withName("x"),
    ValueLayout.JAVA_INT.withName("y"),
    MemoryLayout.paddingLayout(4)
);
```

This mirrors C structs exactly — no object headers, no alignment padding beyond what you specify.

## 14. Value Types: Performance Characteristics

The Valhalla `inline class` (preview) will fundamentally change the type system:

```java
// Current: Complex is a reference type with header overhead
// Valhalla: Complex is a value type with no identity
inline class Complex {
    private double re;
    private double im;
}
```

**Expected performance improvements**:
- Array of 1000 Complex values: 16KB (vs ~32KB with headers)
- No pointer chasing for field access
- GC-friendly: no per-element tracing

## 15. Type Annotations and Type Use

Java 8+ allows annotations on type uses:

```java
@NonNull String name;  // Type-use annotation
List<@NonNull String> list;
```

The annotation is stored in the class file's `RuntimeVisibleTypeAnnotations` attribute. The JVM does NOT enforce `@NonNull` — it's for tools like Checker Framework and IDE support.

## Appendix: Key JVMS Sections for Data Types

| Section | Topic |
|---------|-------|
| JVMS §2.2 | Primitive types and values |
| JVMS §2.3 | Reference types and values |
| JVMS §2.4 | Variables |
| JVMS §2.6 | Local variables |
| JVMS §3.2 | Lexical structure of class files |
| JVMS §4.5 | Field descriptors |
