# Common Mistakes in Algebra

## Sign Errors

```java
// WRONG: -(a - b) = -a - b  ← incorrect!
// -(a - b) = -a + b

// WRONG: expanding (x + 1)^2
// x^2 + 1   ← incorrect!
// x^2 + 2x + 1
```

## Division by Variable (Lost Roots)

```java
// WRONG: dividing both sides by x
// x^2 = x  →  x = 1   ← loses x = 0!

// RIGHT: factor instead
// x^2 - x = 0
// x(x - 1) = 0
// x = 0 or x = 1
```

## Forgetting the $\pm$ in Quadratic Formula

```java
// WRONG: only one root
double x = (-b + Math.sqrt(d)) / (2 * a);

// RIGHT: both roots
double x1 = (-b + Math.sqrt(d)) / (2 * a);
double x2 = (-b - Math.sqrt(d)) / (2 * a);
```

## Matrix Multiplication Order

```java
// WRONG: AB != BA in general
double[][] C = multiply(A, B);
```
