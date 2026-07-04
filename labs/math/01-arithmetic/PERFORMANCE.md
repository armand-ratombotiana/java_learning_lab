# Arithmetic Performance

## Primitive vs. Wrapper

```java
// Fast: primitive int
int sum = 0;
for (int i = 0; i < 1_000_000; i++) sum += i;

// Slow: boxing overhead
Integer sum = 0;
for (int i = 0; i < 1_000_000; i++) sum += i;
```

## Bit Operations vs. Multiplication/Division

```java
// Multiply by 2
int x = n << 1;       // faster than n * 2

// Divide by 2 (positive integers)
int x = n >> 1;       // faster than n / 2

// Modulo power of 2
int x = n & 7;        // faster than n % 8
```

## Math.fma (Fused Multiply-Add)

Java 9+ provides `Math.fma(a, b, c)` which computes $a \times b + c$ in one operation with a single rounding, improving both speed and accuracy.

```java
double result = Math.fma(a, b, c); // a * b + c with 1 rounding
```

## Benchmarking Tip

Always use a microbenchmark harness (JMH) rather than `System.currentTimeMillis()` for arithmetic performance measurements — the JIT compiler optimizes away dead code.
