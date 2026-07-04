# Debugging Geometry in Java

## Common Issues

### Off-by-One in Polygons

The last vertex must connect back to the first. The shoelace formula's wrap-around (`(i + 1) % n`) handles this.

### Degenerate Triangles

```java
if (area < 1e-10) {
    // Points are collinear or coincident
}
```

### Very Small Angles

```java
// Comparing angles can fail near 0 or π
double angle = Math.atan2(cross, dot); // More numerically stable
```

## Debugging Checklist

- [ ] Are coordinates in the right order (x, y)?
- [ ] Is the y-axis flipped (screen vs. math coordinates)?
- [ ] Are you using the correct winding order?
- [ ] Are transformations applied in the correct order?
- [ ] Floating-point epsilon large enough?
- [ ] Edge cases: vertical lines, zero-area polygons, coincident points?
