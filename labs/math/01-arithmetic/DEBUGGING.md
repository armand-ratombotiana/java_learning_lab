# Debugging Arithmetic in Java

## Common Issues

### Overflow Detection

```java
public static int safeAdd(int a, int b) {
    if (b > 0 && a > Integer.MAX_VALUE - b ||
        b < 0 && a < Integer.MIN_VALUE - b) {
        throw new ArithmeticException("Integer overflow");
    }
    return a + b;
}
```

### Lossy Conversion

```java
long big = 12345678912345L;
float f = big;               // precision loss!
System.out.println((long) f); // 12345678823424 -- wrong!
```

## Debugging Checklist

- [ ] Mixed `int`/`double` causing truncation?
- [ ] Overflow silently wrapping?
- [ ] Floating-point equality comparison?
- [ ] Modulo of negative numbers?
- [ ] Division by zero (integer throws, floating gives Infinity/NaN)?
- [ ] BigDecimal created from `double` instead of `String`?

```java
// WRONG: uses floating-point representation
new BigDecimal(0.1);
// RIGHT: exact decimal
new BigDecimal("0.1");
```
