# Code Deep Dive: Trigonometry Implementations in Java

## 1. Basic Trigonometric Functions

### 1.1 TrigonometryCalculator Class

```java
package com.mathacademy.trigonometry;

public class TrigonometryCalculator {
    
    public static double sin(double x) {
        return Math.sin(x);
    }
    
    public static double cos(double x) {
        return Math.cos(x);
    }
    
    public static double tan(double x) {
        if (Math.abs(cos(x)) < 1e-15) {
            throw new ArithmeticException("Undefined: cosine is zero");
        }
        return Math.tan(x);
    }
    
    public static double csc(double x) {
        double sinVal = sin(x);
        if (Math.abs(sinVal) < 1e-15) {
            throw new ArithmeticException("Undefined: sine is zero");
        }
        return 1.0 / sinVal;
    }
    
    public static double sec(double x) {
        double cosVal = cos(x);
        if (Math.abs(cosVal) < 1e-15) {
            throw new ArithmeticException("Undefined: cosine is zero");
        }
        return 1.0 / cosVal;
    }
    
    public static double cot(double x) {
        double tanVal = tan(x);
        if (Math.abs(tanVal) < 1e-15) {
            throw new ArithmeticException("Undefined: tangent is zero");
        }
        return 1.0 / tanVal;
    }
    
    public static double toRadians(double degrees) {
        return Math.toRadians(degrees);
    }
    
    public static double toDegrees(double radians) {
        return Math.toDegrees(radians);
    }
}
```

## 2. Inverse Trigonometric Functions

### 2.1 InverseTrigFunctions Class

```java
package com.mathacademy.trigonometry;

public class InverseTrigFunctions {
    
    public static double arcsin(double x) {
        if (x < -1 || x > 1) {
            throw new IllegalArgumentException("Domain error: |x| > 1");
        }
        return Math.asin(x);
    }
    
    public static double arccos(double x) {
        if (x < -1 || x > 1) {
            throw new IllegalArgumentException("Domain error: |x| > 1");
        }
        return Math.acos(x);
    }
    
    public static double arctan(double x) {
        return Math.atan(x);
    }
    
    public static double arccsc(double x) {
        if (Math.abs(x) < 1) {
            throw new IllegalArgumentException("Domain error: |x| < 1");
        }
        return Math.asin(1.0 / x);
    }
    
    public static double arcsec(double x) {
        if (Math.abs(x) < 1) {
            throw new IllegalArgumentException("Domain error: |x| < 1");
        }
        return Math.acos(1.0 / x);
    }
    
    public static double arccot(double x) {
        return Math.atan(1.0 / x);
    }
    
    public static double arctan2(double y, double x) {
        return Math.atan2(y, x);
    }
}
```

## 3. Trigonometric Identities

### 3.1 TrigIdentityVerifier Class

```java
package com.mathacademy.trigonometry;

public class TrigIdentityVerifier {
    
    public static double pythagoreanIdentity(double angle) {
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        return sin * sin + cos * cos;
    }
    
    public static double sumFormulaSin(double a, double b) {
        return Math.sin(a + b) - (Math.sin(a) * Math.cos(b) + Math.cos(a) * Math.sin(b));
    }
    
    public static double sumFormulaCos(double a, double b) {
        return Math.cos(a + b) - (Math.cos(a) * Math.cos(b) - Math.sin(a) * Math.sin(b));
    }
    
    public static double differenceFormulaSin(double a, double b) {
        return Math.sin(a - b) - (Math.sin(a) * Math.cos(b) - Math.cos(a) * Math.sin(b));
    }
    
    public static double differenceFormulaCos(double a, double b) {
        return Math.cos(a - b) - (Math.cos(a) * Math.cos(b) + Math.sin(a) * Math.sin(b));
    }
    
    public static double doubleAngleSin(double angle) {
        return Math.sin(2 * angle) - 2 * Math.sin(angle) * Math.cos(angle);
    }
    
    public static double doubleAngleCos(double angle) {
        return Math.cos(2 * angle) - (Math.cos(angle) * Math.cos(angle) - Math.sin(angle) * Math.sin(angle));
    }
    
    public static double halfAngleSin(double angle) {
        return Math.sin(angle / 2) - Math.sqrt((1 - Math.cos(angle)) / 2);
    }
    
    public static double halfAngleCos(double angle) {
        return Math.cos(angle / 2) - Math.sqrt((1 + Math.cos(angle)) / 2);
    }
    
    public static double tanSum(double a, double b) {
        return Math.tan(a + b) - (Math.tan(a) + Math.tan(b)) / (1 - Math.tan(a) * Math.tan(b));
    }
}
```

## 4. Solving Trigonometric Equations

### 4.1 TrigEquationSolver Class

```java
package com.mathacademy.trigonometry;

import java.util.ArrayList;
import java.util.List;

public class TrigEquationSolver {
    
    public static double[] solveSinEqualsK(double k, double lowerBound, double upperBound) {
        List<Double> solutions = new ArrayList<>();
        
        if (k < -1 || k > 1) {
            return new double[0];
        }
        
        double principal = Math.asin(k);
        double[] candidates = {
            principal,
            Math.PI - principal
        };
        
        for (double sol : candidates) {
            double normalized = normalizeAngle(sol);
            if (normalized >= lowerBound && normalized <= upperBound) {
                solutions.add(normalized);
            }
        }
        
        double[] result = new double[solutions.size()];
        for (int i = 0; i < solutions.size(); i++) {
            result[i] = solutions.get(i);
        }
        return result;
    }
    
    public static double[] solveCosEqualsK(double k, double lowerBound, double upperBound) {
        List<Double> solutions = new ArrayList<>();
        
        if (k < -1 || k > 1) {
            return new double[0];
        }
        
        double principal = Math.acos(k);
        double[] candidates = {
            principal,
            2 * Math.PI - principal
        };
        
        for (double sol : candidates) {
            double normalized = normalizeAngle(sol);
            if (normalized >= lowerBound && normalized <= upperBound) {
                solutions.add(normalized);
            }
        }
        
        double[] result = new double[solutions.size()];
        for (int i = 0; i < solutions.size(); i++) {
            result[i] = solutions.get(i);
        }
        return result;
    }
    
    public static double[] solveTanEqualsK(double k, double lowerBound, double upperBound) {
        List<Double> solutions = new ArrayList<>();
        
        double principal = Math.atan(k);
        double period = Math.PI;
        
        double start = Math.floor((lowerBound - principal) / period) * period + principal;
        
        for (double sol = start; sol <= upperBound; sol += period) {
            solutions.add(normalizeAngle(sol));
        }
        
        double[] result = new double[solutions.size()];
        for (int i = 0; i < solutions.size(); i++) {
            result[i] = solutions.get(i);
        }
        return result;
    }
    
    public static double[] solveQuadraticTrig(String function, double a, double b, double c) {
        double discriminant = b * b - 4 * a * c;
        
        if (discriminant < 0) {
            return new double[0];
        }
        
        double sqrtD = Math.sqrt(discriminant);
        double root1 = (-b + sqrtD) / (2 * a);
        double root2 = (-b - sqrtD) / (2 * a);
        
        if (function.equals("sin")) {
            return solveSinEqualsK(root1, 0, 2 * Math.PI);
        } else if (function.equals("cos")) {
            return solveCosEqualsK(root1, 0, 2 * Math.PI);
        }
        return new double[0];
    }
    
    private static double normalizeAngle(double angle) {
        angle = angle % (2 * Math.PI);
        if (angle < 0) {
            angle += 2 * Math.PI;
        }
        return angle;
    }
}
```

## 5. Law of Sines and Cosines

### 5.1 TriangleSolver Class

```java
package com.mathacademy.trigonometry;

public class TriangleSolver {
    
    public static double[] lawOfSines(double a, double A, double b) {
        if (a <= 0 || b <= 0 || A <= 0 || A >= Math.PI) {
            throw new IllegalArgumentException("Invalid triangle parameters");
        }
        
        double sinB = b * Math.sin(A) / a;
        
        if (Math.abs(sinB) > 1) {
            throw new ArithmeticException("No solution: invalid triangle");
        }
        
        double B1 = Math.asin(sinB);
        double B2 = Math.PI - B1;
        
        double C1 = Math.PI - A - B1;
        double C2 = Math.PI - A - B2;
        
        if (C1 <= 0 || C2 <= 0) {
            return new double[]{B1, C1};
        }
        
        return new double[]{B1, C1, B2, C2};
    }
    
    public static double lawOfCosinesAngle(double a, double b, double c) {
        if (a <= 0 || b <= 0 || c <= 0) {
            throw new IllegalArgumentException("Side lengths must be positive");
        }
        
        double cosC = (a * a + b * b - c * c) / (2 * a * b);
        
        if (cosC < -1 || cosC > 1) {
            throw new ArithmeticException("Invalid triangle: cosC out of range");
        }
        
        return Math.acos(cosC);
    }
    
    public static double lawOfCosinesSide(double a, double b, double C) {
        return Math.sqrt(a * a + b * b - 2 * a * b * Math.cos(C));
    }
    
    public static double triangleAreaSAS(double a, double b, double C) {
        return 0.5 * a * b * Math.sin(C);
    }
    
    public static double triangleAreaASA(double A, double b, double C) {
        double a = b * Math.sin(A) / Math.sin(C);
        return 0.5 * a * b * Math.sin(C);
    }
    
    public static double heronArea(double a, double b, double c) {
        if (a + b <= c || a + c <= b || b + c <= a) {
            throw new IllegalArgumentException("Invalid triangle: triangle inequality violated");
        }
        
        double s = (a + b + c) / 2;
        double area = s * (s - a) * (s - b) * (s - c);
        
        if (area < 0) {
            throw new ArithmeticException("Invalid triangle");
        }
        
        return Math.sqrt(area);
    }
}
```

## 6. Hyperbolic Trigonometric Functions

### 6.1 HyperbolicFunctions Class

```java
package com.mathacademy.trigonometry;

public class HyperbolicFunctions {
    
    public static double sinh(double x) {
        return (Math.exp(x) - Math.exp(-x)) / 2;
    }
    
    public static double cosh(double x) {
        return (Math.exp(x) + Math.exp(-x)) / 2;
    }
    
    public static double tanh(double x) {
        return sinh(x) / cosh(x);
    }
    
    public static double csch(double x) {
        if (Math.abs(x) < 1e-15) {
            throw new ArithmeticException("Undefined: x = 0");
        }
        return 1.0 / sinh(x);
    }
    
    public static double sech(double x) {
        return 1.0 / cosh(x);
    }
    
    public static double coth(double x) {
        if (Math.abs(x) < 1e-15) {
            throw new ArithmeticException("Undefined: x = 0");
        }
        return cosh(x) / sinh(x);
    }
    
    public static double asinh(double x) {
        return Math.log(x + Math.sqrt(x * x + 1));
    }
    
    public static double acosh(double x) {
        if (x < 1) {
            throw new IllegalArgumentException("Domain error: x < 1");
        }
        return Math.log(x + Math.sqrt(x * x - 1));
    }
    
    public static double atanh(double x) {
        if (Math.abs(x) >= 1) {
            throw new IllegalArgumentException("Domain error: |x| >= 1");
        }
        return 0.5 * Math.log((1 + x) / (1 - x));
    }
}
```

## 7. Testing and Validation

### 7.1 TrigonometryTest Class

```java
package com.mathacademy.trigonometry;

public class TrigonometryTest {
    
    public static void main(String[] args) {
        testBasicFunctions();
        testIdentities();
        testEquationSolving();
        testTriangleSolving();
    }
    
    private static void testBasicFunctions() {
        System.out.println("=== Basic Trigonometric Functions ===");
        
        double[] angles = {0, Math.PI / 6, Math.PI / 4, Math.PI / 3, Math.PI / 2};
        
        for (double angle : angles) {
            System.out.printf("Angle: %.4f rad%n", angle);
            System.out.printf("  sin(%.4f) = %.4f%n", angle, TrigonometryCalculator.sin(angle));
            System.out.printf("  cos(%.4f) = %.4f%n", angle, TrigonometryCalculator.cos(angle));
            System.out.printf("  tan(%.4f) = %.4f%n", angle, TrigonometryCalculator.tan(angle));
        }
        
        double deg = 45;
        double rad = TrigonometryCalculator.toRadians(deg);
        System.out.printf("%d degrees = %.4f radians%n", (int) deg, rad);
        System.out.printf("sin(%d°) = %.4f%n", (int) deg, TrigonometryCalculator.sin(rad));
    }
    
    private static void testIdentities() {
        System.out.println("\n=== Trigonometric Identities ===");
        
        double angle = Math.PI / 4;
        double identity = TrigIdentityVerifier.pythagoreanIdentity(angle);
        System.out.printf("sin²(x) + cos²(x) at x=π/4: %.10f (should be 1.0)%n", identity);
        
        double a = Math.PI / 3;
        double b = Math.PI / 6;
        
        double sumSinError = TrigIdentityVerifier.sumFormulaSin(a, b);
        System.out.printf("sin(a+b) identity error: %.10f%n", sumSinError);
        
        double doubleAngleError = TrigIdentityVerifier.doubleAngleSin(a);
        System.out.printf("sin(2x) identity error at x=π/3: %.10f%n", doubleAngleError);
    }
    
    private static void testEquationSolving() {
        System.out.println("\n=== Equation Solving ===");
        
        double[] solutions = TrigEquationSolver.solveSinEqualsK(0.5, 0, 2 * Math.PI);
        System.out.print("sin(x) = 0.5 solutions: ");
        for (double s : solutions) {
            System.out.printf("%.4f ", s);
        }
        System.out.println();
        
        solutions = TrigEquationSolver.solveCosEqualsK(0.5, 0, 2 * Math.PI);
        System.out.print("cos(x) = 0.5 solutions: ");
        for (double s : solutions) {
            System.out.printf("%.4f ", s);
        }
        System.out.println();
        
        solutions = TrigEquationSolver.solveTanEqualsK(1, 0, 2 * Math.PI);
        System.out.print("tan(x) = 1 solutions: ");
        for (double s : solutions) {
            System.out.printf("%.4f ", s);
        }
        System.out.println();
    }
    
    private static void testTriangleSolving() {
        System.out.println("\n=== Triangle Solving ===");
        
        double[] lawSines = TriangleSolver.lawOfSines(5, Math.PI / 3, 4);
        System.out.print("Law of Sines: a=5, A=60°, b=4: ");
        for (double val : lawSines) {
            System.out.printf("%.4f ", val);
        }
        System.out.println();
        
        double angleC = TriangleSolver.lawOfCosinesAngle(3, 4, 5);
        System.out.printf("Law of Cosines: angle opposite side 5 = %.4f radians (%.2f°)%n", 
            angleC, Math.toDegrees(angleC));
        
        double area = TriangleSolver.heronArea(3, 4, 5);
        System.out.printf("Heron's formula area (3,4,5): %.4f%n", area);
    }
}
```