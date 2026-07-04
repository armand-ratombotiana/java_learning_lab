# Common Mistakes in Geometry

## Checking Perpendicular

```java
// WRONG: only works for vertical/horizontal
if (slope1 * slope2 == -1) { ... }
// RIGHT: dot product
if (Math.abs(dotProduct(v1, v2)) < 1e-10) { ... }
```

## Division by Zero in Slope

```java
// WRONG: vertical line
double slope = (y2 - y1) / (x2 - x1); // x2 == x1 → division by zero
// RIGHT: check for vertical first
if (Math.abs(x2 - x1) < 1e-10) return Double.POSITIVE_INFINITY;
```

## Floating-Point Equality on Coordinates

```java
// WRONG: floating-point inexactness
if (p1 == p2) { ... }
// RIGHT: use epsilon
if (p1.distanceTo(p2) < 1e-10) { ... }
```

## Polygon Winding Order

Clockwise vs counter-clockwise matters for area sign (shoelace) and inside/outside tests. Be consistent.

## Wrong Rotation Direction

In screen coordinates (y-axis down), positive rotation is clockwise, not counter-clockwise as in standard math.
