# Debugging Trigonometry in Java

## Common Issues

### NaN from `acos` or `asin`

Caused by arguments slightly outside $[-1, 1]$:

```java
// Fix: clamp
double clamped = Math.max(-1.0, Math.min(1.0, value));
double angle = Math.acos(clamped);
```

### Incorrect Quadrant from `asin`

`Math.asin` always returns in $[-\pi/2, \pi/2]$. Use `Math.atan2(y, x)` to get full $[-\pi, \pi]$ range.

### Phase Wrapping

Angles should be normalized to $[0, 2\pi)$ or $[-\pi, \pi)$:

```java
public static double normalizeAngle(double theta) {
    theta = theta % (2 * Math.PI);
    if (theta < 0) theta += 2 * Math.PI;
    return theta;
}
```

## Debugging Checklist

- [ ] Radians vs degrees correct?
- [ ] Arguments to `asin`/`acos` in range $[-1, 1]$?
- [ ] Using `atan2(y, x)` (not `atan2(x, y)`)?
- [ ] Quadrant properly considered?
- [ ] Angle normalized to expected range?
- [ ] Numerical stability near asymptotes?
