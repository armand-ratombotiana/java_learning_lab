# Trigonometry Performance

## Expensive Operations (Relative Cost)

| Operation | Relative Time |
|-----------|--------------|
| Addition | 1x |
| Multiplication | 3x |
| Division | 15x |
| `sqrt` | 30x |
| `sin`/`cos` | 50-100x |
| `atan2` | 80-120x |

## Optimization Strategies

### Lookup Tables

```java
// Precompute for common angles
private static final float[] SIN_TABLE = new float[65536];
static {
    for (int i = 0; i < 65536; i++)
        SIN_TABLE[i] = (float) Math.sin(i * 2 * Math.PI / 65536);
}
```

### Avoid `atan2` with Dot/Cross Product

```java
// INSTEAD of: angle = atan2(cross, dot)
// Use: dot/cross directly for comparisons
if (cross > 0) { /* counter-clockwise */ }
```

### Use `Math.sin` Over Custom Series

JVM intrinsics use hardware `FSIN` instruction — 10-100x faster than any Java implementation.

### FMA for Rotation

```java
// Java 9+ fuses multiply+add for rotations
double x2 = Math.fma(cos, x, -sin * y);
double y2 = Math.fma(sin, x, cos * y);
```
