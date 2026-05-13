# Mini Project: Triangle Solver

```java
package com.mathacademy.trigonometry.mini;

public class TriangleSolver {
    public double[] solveSSS(double a, double b, double c) {
        double A = Math.acos((b*b + c*c - a*a) / (2*b*c));
        double B = Math.acos((a*a + c*c - b*b) / (2*a*c));
        double C = Math.PI - A - B;
        return new double[]{A, B, C};
    }
    
    public double areaSAS(double a, double b, double C) {
        return 0.5 * a * b * Math.sin(C);
    }
}
```