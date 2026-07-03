# Math Foundation for Data Types

## Number Systems

Understanding binary, hexadecimal, and two's complement representation is important:

- **Binary**: Base 2 — digits 0,1
- **Hexadecimal**: Base 16 — digits 0-9, A-F
- **Two's complement**: How negative integers are represented (invert bits, add 1)

### Two's Complement Example

```
int x = -5;
// 5 in binary (32-bit): 00000000 00000000 00000000 00000101
// Invert bits:          11111111 11111111 11111111 11111010
// Add 1:                11111111 11111111 11111111 11111011 = -5
```

## Floating-Point Arithmetic

IEEE 754 format: sign bit (1), exponent (8/11 bits), mantissa (23/52 bits). Some numbers cannot be represented exactly (e.g., 0.1). Floating-point arithmetic is not associative.

```java
0.1 + 0.2 == 0.3;  // false! Slight rounding error
```

## Integer Overflow

```java
int max = Integer.MAX_VALUE; // 2,147,483,647
max + 1; // -2,147,483,648 (wraps around)
```

Wrapping is defined in Java — no undefined behavior (unlike C/C++).

## Range Calculations

- byte: -2^7 to 2^7-1
- short: -2^15 to 2^15-1
- int: -2^31 to 2^31-1 (≈ ±2.1 billion)
- long: -2^63 to 2^63-1
- char: 0 to 2^16-1

No specific math foundation required beyond basic arithmetic and understanding of powers of 2.
