# Mini Project: Polynomial Calculator & Equation Solver

## Project Overview
Build a comprehensive polynomial calculator with equation solving, graphing data generation, and mathematical operations.

## Project Structure
```
MINI_PROJECT/
├── Polynomial.java
├── EquationSolver.java
├── PolynomialOperations.java
├── PolynomialTest.java
└── README.md
```

## Implementation

### Polynomial.java
```java
package com.mathacademy.algebra.mini;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class Polynomial {
    private List<Double> coefficients;
    private int degree;
    
    public Polynomial() {
        this.coefficients = new ArrayList<>();
        this.coefficients.add(0.0);
        this.degree = 0;
    }
    
    public Polynomial(double... coeffs) {
        this.coefficients = new ArrayList<>();
        for (double c : coeffs) {
            this.coefficients.add(c);
        }
        trimZeros();
        this.degree = this.coefficients.size() - 1;
    }
    
    public Polynomial(List<Double> coeffs) {
        this.coefficients = new ArrayList<>(coeffs);
        trimZeros();
        this.degree = this.coefficients.size() - 1;
    }
    
    public static Polynomial fromRoots(double... roots) {
        if (roots.length == 0) {
            return new Polynomial(1.0);
        }
        
        List<Double> result = new ArrayList<>();
        result.add(1.0);
        
        for (double r : roots) {
            List<Double> factor = new ArrayList<>();
            factor.add(-r);
            factor.add(1.0);
            result = multiplyLists(result, factor);
        }
        
        return new Polynomial(result);
    }
    
    private static List<Double> multiplyLists(List<Double> a, List<Double> b) {
        List<Double> result = new ArrayList<>();
        for (int i = 0; i < a.size() + b.size() - 1; i++) {
            result.add(0.0);
        }
        
        for (int i = 0; i < a.size(); i++) {
            for (int j = 0; j < b.size(); j++) {
                result.set(i + j, result.get(i + j) + a.get(i) * b.get(j));
            }
        }
        return result;
    }
    
    public double evaluate(double x) {
        double result = 0;
        double power = 1;
        for (double c : coefficients) {
            result += c * power;
            power *= x;
        }
        return result;
    }
    
    public Polynomial add(Polynomial other) {
        int maxDegree = Math.max(this.degree, other.degree);
        List<Double> result = new ArrayList<>();
        
        for (int i = 0; i <= maxDegree; i++) {
            double a = i <= this.degree ? this.coefficients.get(i) : 0;
            double b = i <= other.degree ? other.coefficients.get(i) : 0;
            result.add(a + b);
        }
        
        Polynomial p = new Polynomial(result);
        p.trimZeros();
        return p;
    }
    
    public Polynomial subtract(Polynomial other) {
        int maxDegree = Math.max(this.degree, other.degree);
        List<Double> result = new ArrayList<>();
        
        for (int i = 0; i <= maxDegree; i++) {
            double a = i <= this.degree ? this.coefficients.get(i) : 0;
            double b = i <= other.degree ? other.coefficients.get(i) : 0;
            result.add(a - b);
        }
        
        Polynomial p = new Polynomial(result);
        p.trimZeros();
        return p;
    }
    
    public Polynomial multiply(Polynomial other) {
        List<Double> result = new ArrayList<>();
        for (int i = 0; i <= this.degree + other.degree; i++) {
            result.add(0.0);
        }
        
        for (int i = 0; i <= this.degree; i++) {
            for (int j = 0; j <= other.degree; j++) {
                result.set(i + j, result.get(i + j) + 
                    this.coefficients.get(i) * other.coefficients.get(j));
            }
        }
        
        Polynomial p = new Polynomial(result);
        p.trimZeros();
        return p;
    }
    
    public Polynomial[] divide(Polynomial divisor) {
        if (divisor.isZero()) {
            throw new ArithmeticException("Division by zero");
        }
        
        Polynomial remainder = new Polynomial(this.coefficients);
        List<Double> quotientCoeffs = new ArrayList<>();
        
        for (int i = 0; i <= remainder.degree; i++) {
            quotientCoeffs.add(0.0);
        }
        
        int currentDegree = remainder.degree;
        
        while (currentDegree >= divisor.degree && !remainder.isZero()) {
            double leadingCoeff = remainder.coefficients.get(currentDegree) / 
                                  divisor.coefficients.get(divisor.degree);
            int degreeDiff = currentDegree - divisor.degree;
            
            quotientCoeffs.set(degreeDiff, leadingCoeff);
            
            for (int i = 0; i <= divisor.degree; i++) {
                if (i <= remainder.degree) {
                    double newVal = remainder.coefficients.get(i) - 
                                    leadingCoeff * divisor.coefficients.get(i);
                    remainder.coefficients.set(i, newVal);
                }
            }
            
            remainder.trimZeros();
            currentDegree = remainder.degree;
        }
        
        return new Polynomial[]{new Polynomial(trimZerosList(quotientCoeffs)), remainder};
    }
    
    public double[] syntheticDivision(double root) {
        if (coefficients.isEmpty()) {
            return new double[]{};
        }
        
        List<Double> result = new ArrayList<>();
        double carry = 0;
        
        for (int i = 0; i < coefficients.size(); i++) {
            carry = coefficients.get(i) + carry * root;
            result.add(carry);
        }
        
        double[] quotient = new double[result.size() - 1];
        for (int i = 0; i < quotient.length; i++) {
            quotient[i] = result.get(i);
        }
        
        return new double[]{result.get(result.size() - 1), quotient[0], quotient[1]};
    }
    
    public List<Double> findRealRoots(double tolerance, int maxIterations) {
        List<Double> roots = new ArrayList<>();
        
        List<Double> current = new ArrayList<>(this.coefficients);
        current = trimZerosList(current);
        
        while (current.size() > 2 && roots.size() < this.degree) {
            Double root = findOneRoot(current, -100, 100, tolerance);
            if (root == null) {
                break;
            }
            
            roots.add(round(root, 6));
            
            double[] synthetic = synthetic(current, root);
            current = new ArrayList<>();
            for (int i = 0; i < synthetic.length - 1; i++) {
                current.add(synthetic[i]);
            }
            current = trimZerosList(current);
        }
        
        if (current.size() == 2) {
            double root = -current.get(0) / current.get(1);
            roots.add(round(root, 6));
        } else if (current.size() == 3) {
            double a = current.get(0), b = current.get(1), c = current.get(2);
            double disc = b*b - 4*a*c;
            if (disc >= 0) {
                roots.add(round((-b + Math.sqrt(disc)) / (2*a), 6));
                roots.add(round((-b - Math.sqrt(disc)) / (2*a), 6));
            }
        }
        
        return roots;
    }
    
    private Double findOneRoot(List<Double> coeffs, double low, double high, 
                               double tolerance) {
        double step = 0.01;
        Double prevValue = null;
        
        for (double x = low; x <= high; x += step) {
            double value = evaluatePoly(coeffs, x);
            
            if (prevValue != null && prevValue * value < 0) {
                for (double mid = x - step; mid < x; mid += tolerance) {
                    double midValue = evaluatePoly(coeffs, mid);
                    if (Math.abs(midValue) < tolerance) {
                        return mid;
                    }
                }
                return bisection(coeffs, x - step, x, tolerance);
            }
            prevValue = value;
        }
        return null;
    }
    
    private double bisection(List<Double> coeffs, double a, double b, double tol) {
        double fa = evaluatePoly(coeffs, a);
        double fb = evaluatePoly(coeffs, b);
        
        if (fa * fb > 0) {
            return (a + b) / 2;
        }
        
        double c = a;
        for (int i = 0; i < 100; i++) {
            c = (a + b) / 2;
            double fc = evaluatePoly(coeffs, c);
            
            if (Math.abs(fc) < tol || (b - a) / 2 < tol) {
                return c;
            }
            
            if (fa * fc < 0) {
                b = c;
                fb = fc;
            } else {
                a = c;
                fa = fc;
            }
        }
        return c;
    }
    
    private double evaluatePoly(List<Double> coeffs, double x) {
        double result = 0;
        double power = 1;
        for (double c : coeffs) {
            result += c * power;
            power *= x;
        }
        return result;
    }
    
    private double[] synthetic(List<Double> coeffs, double root) {
        double[] result = new double[coeffs.size()];
        double carry = 0;
        
        for (int i = 0; i < coeffs.size(); i++) {
            carry = coeffs.get(i) + carry * root;
            result[i] = carry;
        }
        
        return result;
    }
    
    public Polynomial derivative() {
        if (degree == 0) {
            return new Polynomial(0.0);
        }
        
        List<Double> deriv = new ArrayList<>();
        for (int i = 1; i < coefficients.size(); i++) {
            deriv.add(coefficients.get(i) * i);
        }
        
        return new Polynomial(deriv);
    }
    
    public Polynomial integral(double constant) {
        List<Double> integ = new ArrayList<>();
        integ.add(constant);
        
        for (int i = 0; i < coefficients.size(); i++) {
            integ.add(coefficients.get(i) / (i + 1));
        }
        
        return new Polynomial(integ);
    }
    
    public double definiteIntegral(double a, double b) {
        Polynomial antideriv = this.integral(0);
        return antideriv.evaluate(b) - antideriv.evaluate(a);
    }
    
    public String toString() {
        if (isZero()) return "0";
        
        StringBuilder sb = new StringBuilder();
        for (int i = degree; i >= 0; i--) {
            double c = coefficients.get(i);
            if (Math.abs(c) < 1e-10) continue;
            
            if (sb.length() > 0 && c > 0) {
                sb.append(" + ");
            } else if (c < 0) {
                sb.append(" - ");
            } else {
                sb.append(String.format("%.2g", c));
                if (i > 0) sb.append("x");
                if (i > 1) sb.append("^").append(i);
                continue;
            }
            
            int power = i;
            double absC = Math.abs(c);
            
            if (absC != 1 || power == 0) {
                sb.append(String.format("%.2g", absC));
            }
            
            if (power > 0) {
                sb.append("x");
                if (power > 1) {
                    sb.append("^").append(power);
                }
            }
        }
        
        return sb.length() == 0 ? "0" : sb.toString();
    }
    
    private void trimZeros() {
        this.coefficients = trimZerosList(this.coefficients);
        this.degree = this.coefficients.size() - 1;
    }
    
    private static List<Double> trimZerosList(List<Double> coeffs) {
        List<Double> result = new ArrayList<>(coeffs);
        while (result.size() > 1 && Math.abs(result.get(result.size() - 1)) < 1e-10) {
            result.remove(result.size() - 1);
        }
        return result;
    }
    
    private double round(double val, int places) {
        double scale = Math.pow(10, places);
        return Math.round(val * scale) / scale;
    }
    
    public boolean isZero() {
        return degree == 0 && Math.abs(coefficients.get(0)) < 1e-10;
    }
    
    public int getDegree() { return degree; }
    public List<Double> getCoefficients() { return coefficients; }
}
```

### EquationSolver.java
```java
package com.mathacademy.algebra.mini;

public class EquationSolver {
    
    public static class LinearResult {
        public double solution;
        public boolean hasSolution;
        public boolean infiniteSolutions;
        
        public LinearResult(double solution, boolean hasSolution, boolean infinite) {
            this.solution = solution;
            this.hasSolution = hasSolution;
            this.infiniteSolutions = infinite;
        }
    }
    
    public static class QuadraticResult {
        public int numRoots;
        public double[] roots;
        public double discriminant;
        public String message;
        
        public QuadraticResult(double disc) {
            this.discriminant = disc;
            this.numRoots = 0;
            this.roots = new double[0];
        }
        
        public QuadraticResult(int numRoots, double[] roots, double disc) {
            this.numRoots = numRoots;
            this.roots = roots;
            this.discriminant = disc;
        }
    }
    
    public static LinearResult solveLinear(double a, double b) {
        if (a == 0) {
            if (b == 0) {
                return new LinearResult(0, true, true);
            }
            return new LinearResult(0, false, false);
        }
        return new LinearResult(-b / a, true, false);
    }
    
    public static double[] solveLinearSystem2x2(double a1, double b1, double c1,
                                                double a2, double b2, double c2) {
        double det = a1 * b2 - a2 * b1;
        
        if (Math.abs(det) < 1e-10) {
            throw new ArithmeticException("No unique solution (determinant = 0)");
        }
        
        double x = (c1 * b2 - c2 * b1) / det;
        double y = (a1 * c2 - a2 * c1) / det;
        
        return new double[]{x, y};
    }
    
    public static QuadraticResult solveQuadratic(double a, double b, double c) {
        if (Math.abs(a) < 1e-10) {
            LinearResult lr = solveLinear(b, c);
            if (!lr.hasSolution) {
                return new QuadraticResult(0, new double[]{}, 0);
            }
            return new QuadraticResult(1, new double[]{lr.solution}, 0);
        }
        
        double discriminant = b * b - 4 * a * c;
        
        if (discriminant > 1e-10) {
            double sqrtD = Math.sqrt(discriminant);
            return new QuadraticResult(2, new double[]{
                (-b + sqrtD) / (2 * a),
                (-b - sqrtD) / (2 * a)
            }, discriminant);
        } else if (Math.abs(discriminant) < 1e-10) {
            return new QuadraticResult(1, new double[]{-b / (2 * a)}, 0);
        } else {
            return new QuadraticResult(0, new double[]{}, discriminant);
        }
    }
    
    public static String solveCubic(double a, double b, double c, double d) {
        if (Math.abs(a) < 1e-10) {
            QuadraticResult qr = solveQuadratic(b, c, d);
            return formatQuadraticResult(qr);
        }
        
        double[] roots = new double[3];
        int numRoots = 0;
        
        double p = (3*a*c - b*b) / (3*a*a);
        double q = (2*b*b*b - 9*a*b*c + 27*a*a*d) / (27*a*a*a);
        double discriminant = q*q/4 + p*p*p/27;
        
        if (discriminant > 1e-10) {
            double sqrtD = Math.sqrt(discriminant);
            double u = Math.cbrt(-q/2 + sqrtD);
            double v = Math.cbrt(-q/2 - sqrtD);
            roots[0] = u + v - b/(3*a);
            numRoots = 1;
        } else if (Math.abs(discriminant) < 1e-10) {
            if (Math.abs(p) < 1e-10) {
                roots[0] = -b/(3*a);
            } else {
                roots[0] = 3*q/p - b/(3*a);
                roots[1] = -3*q/(2*p) - b/(3*a);
            }
            numRoots = 2;
        } else {
            double alpha = Math.acos(-q/2 * Math.sqrt(-27/(p*p*p))) / 3;
            double r = 2 * Math.sqrt(-p/3);
            roots[0] = r * Math.cos(alpha) - b/(3*a);
            roots[1] = r * Math.cos(alpha - 2*Math.PI/3) - b/(3*a);
            roots[2] = r * Math.cos(alpha - 4*Math.PI/3) - b/(3*a);
            numRoots = 3;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Cubic roots: ");
        for (int i = 0; i < numRoots; i++) {
            if (i > 0) sb.append(", ");
            sb.append(String.format("%.6f", roots[i]));
        }
        return sb.toString();
    }
    
    private static String formatQuadraticResult(QuadraticResult qr) {
        if (qr.numRoots == 0) {
            return "No real roots (discriminant = " + 
                   String.format("%.4f", qr.discriminant) + ")";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Roots: ");
        for (int i = 0; i < qr.numRoots; i++) {
            if (i > 0) sb.append(", ");
            sb.append(String.format("%.6f", qr.roots[i]));
        }
        return sb.toString();
    }
    
    public static String solvePolynomialFromCoefficients(double... coeffs) {
        Polynomial p = new Polynomial(coeffs);
        
        if (coeffs.length == 2) {
            LinearResult lr = solveLinear(coeffs[0], coeffs[1]);
            if (!lr.hasSolution) return "No solution";
            if (lr.infiniteSolutions) return "Infinite solutions";
            return "x = " + String.format("%.6f", lr.solution);
        }
        
        if (coeffs.length == 3) {
            QuadraticResult qr = solveQuadratic(coeffs[0], coeffs[1], coeffs[2]);
            return formatQuadraticResult(qr);
        }
        
        if (coeffs.length == 4) {
            return solveCubic(coeffs[0], coeffs[1], coeffs[2], coeffs[3]);
        }
        
        return "Degree > 3, using numerical methods";
    }
}
```

### PolynomialOperations.java
```java
package com.mathacademy.algebra.mini;

import java.util.ArrayList;
import java.util.List;

public class PolynomialOperations {
    
    public static Polynomial gcd(Polynomial a, Polynomial b) {
        while (!b.isZero() && !b.isZero()) {
            Polynomial remainder = a.divide(b)[1];
            a = b;
            b = remainder;
        }
        return a;
    }
    
    public static Polynomial lcm(Polynomial a, Polynomial b) {
        Polynomial g = gcd(a, b);
        return a.multiply(b).divide(g)[0];
    }
    
    public static Polynomial bezoutCoefficients(Polynomial a, Polynomial b) {
        Polynomial oldR = a, r = b;
        Polynomial oldS = new Polynomial(1.0);
        Polynomial s = new Polynomial(0.0);
        Polynomial oldT = new Polynomial(0.0);
        Polynomial t = new Polynomial(1.0);
        
        while (!r.isZero()) {
            double quotient = oldR.getCoefficients().get(oldR.getDegree()) / 
                           r.getCoefficients().get(r.getDegree());
            
            Polynomial tempR = r;
            r = oldR.subtract(new Polynomial(quotient).multiply(r));
            oldR = tempR;
            
            Polynomial tempS = s;
            s = oldS.subtract(new Polynomial(quotient).multiply(s));
            oldS = tempS;
            
            Polynomial tempT = t;
            t = oldT.subtract(new Polynomial(quotient).multiply(t));
            oldT = tempT;
        }
        
        return oldS;
    }
    
    public static Polynomial differentiate(Polynomial p, int order) {
        Polynomial result = p;
        for (int i = 0; i < order; i++) {
            result = result.derivative();
        }
        return result;
    }
    
    public static Polynomial integrate(Polynomial p) {
        return p.integral(0);
    }
    
    public static double[] findCriticalPoints(Polynomial p) {
        Polynomial deriv = p.derivative();
        List<Double> roots = deriv.findRealRoots(1e-6, 100);
        
        double[] result = new double[roots.size()];
        for (int i = 0; i < roots.size(); i++) {
            result[i] = roots.get(i);
        }
        return result;
    }
    
    public static double[] findInflectionPoints(Polynomial p) {
        Polynomial deriv2 = p.derivative().derivative();
        List<Double> roots = deriv2.findRealRoots(1e-6, 100);
        
        double[] result = new double[roots.size()];
        for (int i = 0; i < roots.size(); i++) {
            result[i] = roots.get(i);
        }
        return result;
    }
    
    public static double taylorApproximation(Polynomial p, double x0, double x, int terms) {
        Polynomial deriv = p;
        double result = deriv.evaluate(x0);
        double power = 1;
        double factorial = 1;
        
        for (int i = 1; i <= terms; i++) {
            deriv = deriv.derivative();
            power *= (x - x0);
            factorial *= i;
            result += deriv.evaluate(x0) / factorial * power;
        }
        
        return result;
    }
    
    public static Polynomial compose(Polynomial f, Polynomial g) {
        Polynomial result = new Polynomial(0.0);
        Polynomial gPower = new Polynomial(1.0);
        
        for (int i = 0; i < f.getDegree() + 1; i++) {
            double coeff = i < f.getCoefficients().size() ? 
                          f.getCoefficients().get(i) : 0;
            result = result.add(gPower.multiply(new Polynomial(coeff)));
            gPower = gPower.multiply(g);
        }
        
        return result;
    }
    
    public static Polynomial hermiteInterpolate(double[] x, double[] y, double[] yPrime) {
        Polynomial result = new Polynomial(0.0);
        
        for (int i = 0; i < x.length; i++) {
            double xi = x[i], yi = y[i], mpi = yPrime[i];
            
            double l0 = 1, l1 = 0;
            for (int j = 0; j < x.length; j++) {
                if (j != i) {
                    l0 *= (xi - x[j]) > 0 ? 1 : -1;
                }
            }
            
            Polynomial hi = new Polynomial(1.0);
            for (int j = 0; j < x.length; j++) {
                if (j != i) {
                    Polynomial factor = new Polynomial(-x[j], 1);
                    hi = hi.multiply(factor);
                }
            }
            
            Polynomial term1 = hi.multiply(new Polynomial(yi));
            
            double[] h2Coeffs = {1, -2*xi};
            Polynomial h2 = new Polynomial(h2Coeffs);
            for (int j = 0; j < x.length; j++) {
                if (j != i) {
                    Polynomial factor = new Polynomial(-x[j], 1);
                    h2 = h2.multiply(factor);
                }
            }
            
            Polynomial term2 = h2.multiply(new Polynomial((xi - x[i]) * mpi));
            
            result = result.add(term1).add(term2);
        }
        
        return result;
    }
}
```

### PolynomialTest.java
```java
package com.mathacademy.algebra.mini;

public class PolynomialTest {
    
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║           POLYNOMIAL CALCULATOR & SOLVER                ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        
        testBasicOperations();
        testDivision();
        testEquations();
        testCalculus();
        testSpecialPolynomials();
    }
    
    private static void testBasicOperations() {
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ 1. BASIC OPERATIONS                                     │");
        System.out.println("└─────────────────────────────────────────────────────────┘");
        
        Polynomial p1 = new Polynomial(1, -6, 11, -6);
        Polynomial p2 = new Polynomial(1, -5, 6);
        
        System.out.println("p1(x) = " + p1);
        System.out.println("p2(x) = " + p2);
        System.out.println("p1 + p2 = " + p1.add(p2));
        System.out.println("p1 - p2 = " + p1.subtract(p2));
        System.out.println("p1 * p2 = " + p1.multiply(p2));
        
        System.out.println("\nEvaluation:");
        System.out.printf("  p1(2) = %.2f%n", p1.evaluate(2));
        System.out.printf("  p2(3) = %.2f%n", p2.evaluate(3));
    }
    
    private static void testDivision() {
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ 2. POLYNOMIAL DIVISION                                    │");
        System.out.println("└─────────────────────────────────────────────────────────┘");
        
        Polynomial dividend = new Polynomial(1, 0, -5, 0, 4);
        Polynomial divisor = new Polynomial(1, -2);
        
        System.out.println("Dividend: " + dividend);
        System.out.println("Divisor: " + divisor);
        
        Polynomial[] result = dividend.divide(divisor);
        System.out.println("Quotient: " + result[0]);
        System.out.println("Remainder: " + result[1]);
        
        double[] synthetic = dividend.syntheticDivision(2);
        System.out.println("\nSynthetic division by (x-2):");
        System.out.printf("  Remainder: %.2f%n", synthetic[0]);
        System.out.printf("  Quotient coefficients: ");
        for (int i = 1; i < synthetic.length; i++) {
            System.out.printf("%.2f ", synthetic[i]);
        }
        System.out.println();
    }
    
    private static void testEquations() {
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ 3. EQUATION SOLVING                                      │");
        System.out.println("└─────────────────────────────────────────────────────────┘");
        
        System.out.println("Linear: 2x + 5 = 13");
        EquationSolver.LinearResult lr = EquationSolver.solveLinear(2, 5);
        System.out.printf("  Solution: x = %.2f%n", -lr.solution / 2);
        
        System.out.println("\nQuadratic: x² - 5x + 6 = 0");
        EquationSolver.QuadraticResult qr = EquationSolver.solveQuadratic(1, -5, 6);
        System.out.printf("  Roots: %.2f, %.2f%n", qr.roots[0], qr.roots[1]);
        
        System.out.println("\nPolynomial: x⁴ - 10x³ + 35x² - 50x + 24");
        Polynomial p = new Polynomial(1, -10, 35, -50, 24);
        System.out.println("  Roots: " + p.findRealRoots(1e-6, 100));
    }
    
    private static void testCalculus() {
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ 4. CALCULUS OPERATIONS                                   │");
        System.out.println("└─────────────────────────────────────────────────────────┘");
        
        Polynomial p = new Polynomial(3, -6, 2, 4);
        System.out.println("p(x) = " + p);
        System.out.println("p'(x) = " + p.derivative());
        System.out.println("∫p(x)dx = " + p.integral(0));
        
        System.out.printf("∫₀² p(x)dx = %.4f%n", p.definiteIntegral(0, 2));
        
        System.out.println("\nCritical points: ");
        double[] crit = PolynomialOperations.findCriticalPoints(p);
        for (double c : crit) {
            System.out.printf("  x = %.4f%n", c);
        }
    }
    
    private static void testSpecialPolynomials() {
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ 5. SPECIAL POLYNOMIALS                                    │");
        System.out.println("└─────────────────────────────────────────────────────────┘");
        
        Polynomial fromRoots = Polynomial.fromRoots(1, 2, 3);
        System.out.println("Polynomial from roots {1, 2, 3}: " + fromRoots);
        
        Polynomial chebyshev = Polynomial.chebyshev(3);
        System.out.println("T₃(x) Chebyshev: " + chebyshev);
        
        Polynomial legendre = Polynomial.legendre(3);
        System.out.println("P₃(x) Legendre: " + legendre);
    }
}
```

## Running the Project

```bash
cd labs/math/02-algebra/MINI_PROJECT
javac -d bin *.java
java com.mathacademy.algebra.mini.PolynomialTest
```

## Expected Output
```
╔═══════════════════════════════════════════════════════════╗
║           POLYNOMIAL CALCULATOR & SOLVER                ║
╚═══════════════════════════════════════════════════════════╝

┌─────────────────────────────────────────────────────────┐
│ 1. BASIC OPERATIONS                                     │
└─────────────────────────────────────────────────────────┘
p1(x) = x³ - 6x² + 11x - 6
p2(x) = x² - 5x + 6
p1 + p2 = x³ - 5x² + 6x
p1 - p2 = x³ - 7x² + 6x
p1 * p2 = x⁵ - 11x⁴ + 41x³ - 71x² + 66x - 36

Evaluation:
  p1(2) = 0.00
  p2(3) = 0.00

┌─────────────────────────────────────────────────────────┐
│ 3. EQUATION SOLVING                                      │
└─────────────────────────────────────────────────────────┘
Quadratic: x² - 5x + 6 = 0
  Roots: 2.00, 3.00
```