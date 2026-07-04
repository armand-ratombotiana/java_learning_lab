# Internals of Trigonometry

## CORDIC Algorithm

Most CPUs implement trig via CORDIC (COordinate Rotation DIgital Computer):

```java
public static double cordicSin(double theta) {
    double[] angles = {45, 26.565, 14.036, 7.125, 3.576,
                       1.790, 0.895, 0.448, 0.224, 0.112};
    double x = 0.60725; // K constant = 1/Π cos(arctan(2^-i))
    double y = 0;
    double z = theta;
    for (int i = 0; i < angles.length; i++) {
        double d = (z >= 0) ? 1 : -1;
        double newX = x - d * y * (1 << i);
        y = y + d * x * (1 << i);
        x = newX;
        z -= d * Math.toRadians(angles[i]);
    }
    return y;
}
```

## IEEE 754 Implementation

`Math.sin()` and `Math.cos()` are typically implemented via:
1. Range reduction (argument modulo $2\pi$)
2. Polynomial approximation (Chebyshev/minimax) on reduced range
3. Adjustment based on original quadrant

## Lookup Tables

For performance-critical code, precomputed tables:

```java
float[] sinTable = new float[1024];
for (int i = 0; i < 1024; i++)
    sinTable[i] = (float) Math.sin(i * 2 * Math.PI / 1024);
```
