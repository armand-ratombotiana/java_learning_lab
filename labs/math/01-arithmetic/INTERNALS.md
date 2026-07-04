# Internals of Arithmetic

## Binary Representation

All modern computers use **binary (base-2)** arithmetic:

| Decimal | Binary | Hex |
|---------|--------|-----|
| 0 | 0000 | 0x0 |
| 1 | 0001 | 0x1 |
| 2 | 0010 | 0x2 |
| 15 | 1111 | 0xF |

## Integer Overflow

Java integers are 32-bit signed two's complement:

$$
\text{MAX\_INT} = 2^{31} - 1 = 2147483647
$$

Adding 1 wraps to $-2^{31}$:

```java
int x = Integer.MAX_VALUE; // 2147483647
x++;                       // -2147483648 (overflow)
```

## Floating Point (IEEE 754)

$$
\text{value} = (-1)^s \times 1.m \times 2^{e-127}
$$

- 1 sign bit, 8 exponent bits, 23 mantissa bits (float)
- 1 sign bit, 11 exponent bits, 52 mantissa bits (double)

## Precision Pitfalls

```java
0.1 + 0.2 == 0.3; // false!
```

Use `BigDecimal` for exact decimal arithmetic:

```java
BigDecimal a = new BigDecimal("0.1");
BigDecimal b = new BigDecimal("0.2");
BigDecimal c = a.add(b); // 0.3
```
