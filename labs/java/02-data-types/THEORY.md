# Data Types — Theoretical Foundation

## Primitive Types

Java has exactly 8 primitive types, fixed in size across all platforms — a key design decision for portability.

| Type | Size | Range | Default | Wrapper |
|------|------|-------|---------|---------|
| `byte` | 8 bits | -128 to 127 | 0 | `Byte` |
| `short` | 16 bits | -32,768 to 32,767 | 0 | `Short` |
| `int` | 32 bits | -2^31 to 2^31-1 | 0 | `Integer` |
| `long` | 64 bits | -2^63 to 2^63-1 | 0L | `Long` |
| `float` | 32 bits | ±3.4e-38 to ±3.4e+38 | 0.0f | `Float` |
| `double` | 64 bits | ±1.7e-308 to ±1.7e+308 | 0.0d | `Double` |
| `char` | 16 bits | 0 to 65,535 (UTF-16) | '\u0000' | `Character` |
| `boolean` | JVM-dependent | true or false | false | `Boolean` |

### Integer Types

All integer types are signed, using two's complement representation. Java has no unsigned integer primitives (unlike C/C++). `byte` and `short` are often promoted to `int` in expressions.

### Floating-Point Types

`float` and `double` follow IEEE 754 standard. They include special values:
- `NaN` (Not a Number): `0.0 / 0.0`
- `+Infinity` / `-Infinity`: `1.0 / 0.0`
- `-0.0`: Negative zero

### Boolean Type

`boolean` is a distinct type — not convertible to/from `int` (unlike C). This prevents bugs like `if (x = 5)`.

### Char Type

`char` is a 16-bit unsigned integer representing a UTF-16 code unit. Surrogate pairs (supplementary characters) require two `char` values.

## Reference Types

Reference types store a reference (pointer) to an object, not the object itself. They include:
- Classes (e.g., `String`, `ArrayList`)
- Interfaces (e.g., `List`, `Runnable`)
- Arrays (e.g., `int[]`)
- Enums (e.g., `DayOfWeek`)
- Records (e.g., `record Point(int x, int y)`)

## Type Conversion

### Widening (Implicit) — Safe

```java
byte → short → int → long → float → double
                ↑
               char
```

No data loss, performed automatically by the compiler.

### Narrowing (Explicit) — Potential Data Loss

Requires a cast: `(targetType) value`. May lose precision or magnitude.

```java
double d = 3.14;
int i = (int) d;  // i = 3 (truncated)
```

### Expression Type Promotion

Binary numeric promotion rules:
1. If either operand is `double`, the other is promoted to `double`
2. Otherwise if either is `float`, the other is promoted to `float`
3. Otherwise if either is `long`, the other is promoted to `long`
4. Otherwise both are promoted to `int`

```java
byte a = 10;
byte b = 20;
int c = a + b;  // a and b are promoted to int
```

## Autoboxing and Unboxing

Autoboxing: automatic conversion of primitive to wrapper (Java 5+).
Unboxing: automatic conversion of wrapper to primitive.

```java
Integer x = 42;           // Autoboxing: int → Integer
int y = x;                // Unboxing: Integer → int
```

The JVM caches `Integer` values from -128 to 127 (configurable). Autoboxing outside this range creates new objects.

## Memory Layout

- Primitives on the stack (local variables) or inline in objects (fields)
- References are 32-bit or 64-bit pointers (compressed OOPs with heaps < 32GB)
- Objects have header overhead (mark word + klass pointer) — typically 12-16 bytes on 64-bit JVM

## Default Values

Instance fields and array elements get default values. Local variables do NOT — they must be explicitly initialized.
