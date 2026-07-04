# Common Mistakes in Arithmetic

## Integer Division Truncation

```java
// WRONG: returns 1.0 instead of 1.5
double result = 3 / 2;

// RIGHT: promote to double
double result = 3.0 / 2;
// or
double result = (double) 3 / 2;
```

## Modulo Confusion

In Java, `a % b` has the sign of `a`:

```java
-7 % 3 == -1;  // not 2
```

For a non-negative remainder: `((a % b) + b) % b`

## Floating-Point Equality

```java
// WRONG: rarely true
if (x == 0.1) { ... }

// RIGHT: tolerance check
if (Math.abs(x - 0.1) < 1e-10) { ... }
```

## Operator Precedence

```java
// WRONG: evaluates as 2 + (3 * 4) = 14
int x = 2 + 3 * 4;

// RIGHT: explicit parentheses
int x = (2 + 3) * 4;  // 20
```
