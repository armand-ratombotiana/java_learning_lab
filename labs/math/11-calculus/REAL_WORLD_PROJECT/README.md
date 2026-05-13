# Real World Project: Physics Simulation Engine

```java
package com.mathacademy.calculus.realworld;

public class PhysicsEngine {
    public double[] projectileMotion(double v0, double angle, double g, double t) {
        double x = v0 * Math.cos(angle) * t;
        double y = v0 * Math.sin(angle) * t - 0.5 * g * t * t;
        return new double[]{x, y};
    }
    
    public double findMaxHeight(double v0, double angle, double g) {
        double t = v0 * Math.sin(angle) / g;
        return v0 * Math.sin(angle) * t - 0.5 * g * t * t;
    }
}
```