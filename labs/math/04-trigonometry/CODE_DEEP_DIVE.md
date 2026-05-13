# Code Deep Dive: Trigonometry Implementations

```java
package com.mathacademy.trigonometry;

public class TrigonometryCalculator {
    
    public static double sin(double x) { return Math.sin(x); }
    public static double cos(double x) { return Math.cos(x); }
    public static double tan(double x) { return Math.tan(x); }
    public static double csc(double x) { return 1 / Math.sin(x); }
    public static double sec(double x) { return 1 / Math.cos(x); }
    public static double cot(double x) { return 1 / Math.tan(x); }
    
    public static double toRadians(double degrees) { return Math.toRadians(degrees); }
    public static double toDegrees(double radians) { return Math.toDegrees(radians); }
    
    public static double[] solveSinEquation(double a, double b, double c) {
        if (a == 0) return new double[]{};
        double discriminant = b*b - 4*a*c;
        if (discriminant < 0) return new double[]{};
        double sqrtD = Math.sqrt(discriminant);
        double sin1 = (-b + sqrtD) / (2*a);
        double sin2 = (-b - sqrtD) / (2*a);
        double[] solutions = new double[4];
        int count = 0;
        if (Math.abs(sin1) <= 1) {
            solutions[count++] = Math.asin(sin1);
            solutions[count++] = Math.PI - Math.asin(sin1);
        }
        if (Math.abs(sin2) <= 1 && sin2 != sin1) {
            solutions[count++] = Math.asin(sin2);
            solutions[count++] = Math.PI - Math.asin(sin2);
        }
        return java.util.Arrays.copyOf(solutions, count);
    }
    
    public static String sinIdentity(double a, double b) {
        return String.format("sin(a+b) = sin(a)cos(b) + cos(a)sin(b) = %.4f", 
            Math.sin(a)*Math.cos(b) + Math.cos(a)*Math.sin(b));
    }
    
    public static double[] lawOfSines(double a, double A, double b) {
        double sinB = b * Math.sin(A) / a;
        if (Math.abs(sinB) > 1) return new double[]{};
        double B1 = Math.asin(sinB);
        double B2 = Math.PI - B1;
        double C1 = Math.PI - A - B1;
        double C2 = Math.PI - A - B2;
        return new double[]{B1, C1, B2, C2};
    }
    
    public static double lawOfCosines(double a, double b, double c) {
        double cosC = (a*a + b*b - c*c) / (2*a*b);
        return Math.acos(cosC);
    }
    
    public static double triangleAreaSAS(double a, double b, double C) {
        return 0.5 * a * b * Math.sin(C);
    }
    
    public static double heronArea(double a, double b, double c) {
        double s = (a + b + c) / 2;
        return Math.sqrt(s * (s - a) * (s - b) * (s - c));
    }
}
```