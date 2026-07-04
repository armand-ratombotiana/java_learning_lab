# Common Mistakes in Trigonometry

## Degrees vs Radians

```java
// WRONG: Math.sin expects radians
double x = Math.sin(90);  // sin(90 rad) ≈ 0.894, not 1!

// RIGHT:
double x = Math.sin(Math.toRadians(90)); // 1.0
```

## Forgetting the $\pm$ in Inverse Trig

```java
// WRONG: Math.asin returns principal value only
double angle = Math.asin(0.5); // π/6 ≈ 0.524
// But sin(5π/6) = 0.5 too!
```

## Floating-Point Range in Inverse Trig

```java
// WRONG: slightly out of domain
double x = Math.acos(1.0000000000000002); // NaN!

// RIGHT: clamp
double x = Math.acos(Math.max(-1, Math.min(1, value)));
```

## Atan2 Argument Order

```java
// WRONG: atan2(y, x) NOT atan2(x, y)
double angle = Math.atan2(y, x); // correct

// Returns angle from positive x-axis to point (x, y)
```

## Using `tan` Near Asymptotes

```java
Math.tan(Math.PI / 2); // huge number, not infinity
```
