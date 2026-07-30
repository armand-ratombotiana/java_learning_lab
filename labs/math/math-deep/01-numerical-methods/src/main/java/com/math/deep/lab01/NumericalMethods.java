package com.math.deep.lab01;

import java.util.function.DoubleUnaryOperator;
import java.util.function.DoubleBinaryOperator;

public class NumericalMethods {

    private static final int MAX_ITER = 1000;
    private static final double EPS = 1e-12;

    public static double newtonRaphson(DoubleUnaryOperator f, DoubleUnaryOperator df, double x0) {
        double x = x0;
        for (int i = 0; i < MAX_ITER; i++) {
            double fx = f.applyAsDouble(x);
            if (Math.abs(fx) < EPS) return x;
            double dfx = df.applyAsDouble(x);
            if (Math.abs(dfx) < EPS) throw new ArithmeticException("Derivative too small");
            double dx = fx / dfx;
            x = x - dx;
            if (Math.abs(dx) < EPS) return x;
        }
        throw new ArithmeticException("Newton-Raphson did not converge");
    }

    public static double bisection(DoubleUnaryOperator f, double a, double b) {
        double fa = f.applyAsDouble(a);
        double fb = f.applyAsDouble(b);
        if (fa * fb > 0) throw new IllegalArgumentException("No sign change on interval");
        for (int i = 0; i < MAX_ITER; i++) {
            double m = (a + b) / 2.0;
            double fm = f.applyAsDouble(m);
            if (Math.abs(fm) < EPS || (b - a) / 2.0 < EPS) return m;
            if (fa * fm < 0) { b = m; fb = fm; }
            else { a = m; fa = fm; }
        }
        throw new ArithmeticException("Bisection did not converge");
    }

    public static double trapezoidal(DoubleUnaryOperator f, double a, double b, int n) {
        if (n <= 0) throw new IllegalArgumentException("n must be positive");
        double h = (b - a) / n;
        double sum = 0.5 * (f.applyAsDouble(a) + f.applyAsDouble(b));
        for (int i = 1; i < n; i++) sum += f.applyAsDouble(a + i * h);
        return sum * h;
    }

    public static double simpson(DoubleUnaryOperator f, double a, double b, int n) {
        if (n <= 0 || n % 2 != 0) throw new IllegalArgumentException("n must be positive even");
        double h = (b - a) / n;
        double sum = f.applyAsDouble(a) + f.applyAsDouble(b);
        for (int i = 1; i < n; i++) {
            double x = a + i * h;
            sum += (i % 2 == 0 ? 2.0 : 4.0) * f.applyAsDouble(x);
        }
        return sum * h / 3.0;
    }

    public static double centralDifference(DoubleUnaryOperator f, double x, double h) {
        if (h <= 0) throw new IllegalArgumentException("h must be positive");
        return (f.applyAsDouble(x + h) - f.applyAsDouble(x - h)) / (2.0 * h);
    }

    public static double forwardDifference(DoubleUnaryOperator f, double x, double h) {
        if (h <= 0) throw new IllegalArgumentException("h must be positive");
        return (f.applyAsDouble(x + h) - f.applyAsDouble(x)) / h;
    }

    public static double richardsonExtrapolation(DoubleUnaryOperator f, double x, double h, int order) {
        if (order < 1) throw new IllegalArgumentException("order must be >= 1");
        double d1 = centralDifference(f, x, h);
        double d2 = centralDifference(f, x, h / 2.0);
        return (Math.pow(2, order) * d2 - d1) / (Math.pow(2, order) - 1);
    }

    public static double adaptiveQuadrature(DoubleUnaryOperator f, double a, double b, double tol) {
        double whole = simpson(f, a, b, 2);
        double left = simpson(f, a, (a + b) / 2.0, 2);
        double right = simpson(f, (a + b) / 2.0, b, 2);
        double est = left + right;
        if (Math.abs(est - whole) < 15.0 * tol) return est;
        return adaptiveQuadrature(f, a, (a + b) / 2.0, tol / 2.0)
             + adaptiveQuadrature(f, (a + b) / 2.0, b, tol / 2.0);
    }
}
