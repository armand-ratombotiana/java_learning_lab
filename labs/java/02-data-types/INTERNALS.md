# Data Types — Internal Mechanics

## JVM Internal Representation

Primitives are stored in their natural binary form. `int` = 32-bit two's complement. `float`/`double` = IEEE 754. `boolean` = JVM-dependent (often int, 0=false, 1=true, but not guaranteed).

## Object Header Layout

An `Integer` object on a 64-bit JVM (with compressed OOPs):
```
[Mark word: 8 bytes] [Klass pointer: 4 bytes] [int value: 4 bytes] = 16 bytes
```
vs a plain `int`: 4 bytes. Autoboxing has a 4x memory overhead.

## Integer Cache

`Integer.valueOf(n)` for -128 to 127 returns cached instances:
```java
Integer a = 100;  Integer b = 100;  // a == b (same cached object)
Integer c = 200;  Integer d = 200;  // c != d (different objects)
```

## Null in Wrappers

Primitives cannot be null. Wrappers can — causes NullPointerException on unboxing:
```java
Integer x = null;
int y = x;  // NullPointerException at runtime
```

## String Pool

Java 7+ stores the String pool in heap memory. Previously in PermGen. Interned strings via `String.intern()`.
