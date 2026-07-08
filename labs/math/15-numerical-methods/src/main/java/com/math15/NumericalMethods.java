package com.math15;

import java.util.function.BiFunction;
import java.util.function.Function;

public class NumericalMethods {

    // ===== Numerical Integration =====

    public static double rectangleRule(Function<Double, Double> f, double a, double b, int n) {
        double h = (b - a) / n;
        double sum = 0;
        for (int i = 0; i < n; i++) {
            sum += f.apply(a + i * h);
        }
        return sum * h;
    }

    public static double trapezoidalRule(Function<Double, Double> f, double a, double b, int n) {
        double h = (b - a) / n;
        double sum = 0.5 * (f.apply(a) + f.apply(b));
        for (int i = 1; i < n; i++) {
            sum += f.apply(a + i * h);
        }
        return sum * h;
    }

    public static double simpsonsRule(Function<Double, Double> f, double a, double b, int n) {
        if (n % 2 != 0) n++;
        double h = (b - a) / n;
        double sum = f.apply(a) + f.apply(b);
        for (int i = 1; i < n; i++) {
            sum += (i % 2 == 0 ? 2 : 4) * f.apply(a + i * h);
        }
        return sum * h / 3;
    }

    public static double rombergIntegration(Function<Double, Double> f, double a, double b, int order) {
        int n = order + 1;
        double[][] R = new double[n][n];
        double h = b - a;
        R[0][0] = 0.5 * h * (f.apply(a) + f.apply(b));
        for (int i = 1; i < n; i++) {
            h /= 2;
            double sum = 0;
            for (int k = 1; k <= (1 << (i - 1)); k++) {
                sum += f.apply(a + (2 * k - 1) * h);
            }
            R[i][0] = 0.5 * R[i - 1][0] + h * sum;
            for (int j = 1; j <= i; j++) {
                R[i][j] = R[i][j - 1] + (R[i][j - 1] - R[i - 1][j - 1]) / (Math.pow(4, j) - 1);
            }
        }
        return R[n - 1][n - 1];
    }

    public static double gaussLegendre2Point(Function<Double, Double> f, double a, double b) {
        double c1 = 1.0 / Math.sqrt(3);
        double mid = (a + b) / 2;
        double half = (b - a) / 2;
        return half * (f.apply(mid - half * c1) + f.apply(mid + half * c1));
    }

    // ===== Root Finding =====

    public static double bisection(Function<Double, Double> f, double a, double b, int maxIter) {
        double fa = f.apply(a), fb = f.apply(b);
        if (fa * fb > 0) throw new IllegalArgumentException("f(a) and f(b) must have opposite signs");
        for (int i = 0; i < maxIter; i++) {
            double c = (a + b) / 2;
            double fc = f.apply(c);
            if (Math.abs(fc) < 1e-15 || (b - a) / 2 < 1e-15) return c;
            if (fa * fc < 0) {
                b = c;
                fb = fc;
            } else {
                a = c;
                fa = fc;
            }
        }
        return (a + b) / 2;
    }

    public static double newtonRaphson(Function<Double, Double> f, Function<Double, Double> fPrime,
                                        double start, int maxIter) {
        double x = start;
        for (int i = 0; i < maxIter; i++) {
            double fx = f.apply(x);
            if (Math.abs(fx) < 1e-15) return x;
            double fpx = fPrime.apply(x);
            if (Math.abs(fpx) < 1e-15) break;
            x = x - fx / fpx;
        }
        return x;
    }

    public static double secant(Function<Double, Double> f, double x0, double x1, int maxIter) {
        for (int i = 0; i < maxIter; i++) {
            double f0 = f.apply(x0), f1 = f.apply(x1);
            if (Math.abs(f1) < 1e-15) return x1;
            double x2 = x1 - f1 * (x1 - x0) / (f1 - f0);
            x0 = x1;
            x1 = x2;
        }
        return x1;
    }

    public static double falsePosition(Function<Double, Double> f, double a, double b, int maxIter) {
        double fa = f.apply(a), fb = f.apply(b);
        if (fa * fb > 0) throw new IllegalArgumentException("f(a) and f(b) must have opposite signs");
        for (int i = 0; i < maxIter; i++) {
            double c = (a * fb - b * fa) / (fb - fa);
            double fc = f.apply(c);
            if (Math.abs(fc) < 1e-15) return c;
            if (fa * fc < 0) {
                b = c;
                fb = fc;
            } else {
                a = c;
                fa = fc;
            }
        }
        return (a * fb - b * fa) / (fb - fa);
    }

    // ===== Interpolation =====

    public static double lagrangeInterpolation(double[] xs, double[] ys, double x) {
        double result = 0;
        for (int i = 0; i < xs.length; i++) {
            double term = ys[i];
            for (int j = 0; j < xs.length; j++) {
                if (i != j) term *= (x - xs[j]) / (xs[i] - xs[j]);
            }
            result += term;
        }
        return result;
    }

    public static double[] newtonDividedDifference(double[] xs, double[] ys) {
        int n = xs.length;
        double[] coeffs = ys.clone();
        for (int j = 1; j < n; j++) {
            for (int i = n - 1; i >= j; i--) {
                coeffs[i] = (coeffs[i] - coeffs[i - 1]) / (xs[i] - xs[i - j]);
            }
        }
        return coeffs;
    }

    public static double newtonInterpolation(double[] xs, double[] coeffs, double x) {
        double result = coeffs[coeffs.length - 1];
        for (int i = coeffs.length - 2; i >= 0; i--) {
            result = result * (x - xs[i]) + coeffs[i];
        }
        return result;
    }

    // ===== ODE Solvers =====

    public static double[] eulerMethod(BiFunction<Double, Double, Double> f,
                                        double t0, double y0, double h, int steps) {
        double[] ys = new double[steps + 1];
        double t = t0;
        double y = y0;
        ys[0] = y0;
        for (int i = 1; i <= steps; i++) {
            y = y + h * f.apply(t, y);
            t = t + h;
            ys[i] = y;
        }
        return ys;
    }

    public static double[] rungeKutta4(BiFunction<Double, Double, Double> f,
                                        double t0, double y0, double h, int steps) {
        double[] ys = new double[steps + 1];
        double t = t0, y = y0;
        ys[0] = y0;
        for (int i = 1; i <= steps; i++) {
            double k1 = f.apply(t, y);
            double k2 = f.apply(t + h / 2, y + h * k1 / 2);
            double k3 = f.apply(t + h / 2, y + h * k2 / 2);
            double k4 = f.apply(t + h, y + h * k3);
            y = y + (h / 6) * (k1 + 2 * k2 + 2 * k3 + k4);
            t = t + h;
            ys[i] = y;
        }
        return ys;
    }

    public static double[] heunsMethod(BiFunction<Double, Double, Double> f,
                                        double t0, double y0, double h, int steps) {
        double[] ys = new double[steps + 1];
        double t = t0, y = y0;
        ys[0] = y0;
        for (int i = 1; i <= steps; i++) {
            double k1 = f.apply(t, y);
            double k2 = f.apply(t + h, y + h * k1);
            y = y + (h / 2) * (k1 + k2);
            t = t + h;
            ys[i] = y;
        }
        return ys;
    }

    // ===== Finite Differences =====

    public static double forwardDifference(Function<Double, Double> f, double x, double h) {
        return (f.apply(x + h) - f.apply(x)) / h;
    }

    public static double backwardDifference(Function<Double, Double> f, double x, double h) {
        return (f.apply(x) - f.apply(x - h)) / h;
    }

    public static double centralDifference(Function<Double, Double> f, double x, double h) {
        return (f.apply(x + h) - f.apply(x - h)) / (2 * h);
    }

    public static double secondDerivativeCentral(Function<Double, Double> f, double x, double h) {
        return (f.apply(x + h) - 2 * f.apply(x) + f.apply(x - h)) / (h * h);
    }

    public static double[] finiteDifferenceGradient(Function<double[], Double> f, double[] x, double h) {
        double[] grad = new double[x.length];
        double fx = f.apply(x);
        for (int i = 0; i < x.length; i++) {
            double[] xp = x.clone();
            xp[i] += h;
            grad[i] = (f.apply(xp) - fx) / h;
        }
        return grad;
    }

    public static double richardsonExtrapolation(Function<Double, Double> fDerivative,
                                                   double x, double h, int order) {
        double[] D = new double[order + 1];
        for (int i = 0; i <= order; i++) {
            D[i] = fDerivative.apply(h / Math.pow(2, i));
        }
        for (int k = 1; k <= order; k++) {
            for (int i = order; i >= k; i--) {
                D[i] = D[i] + (D[i] - D[i - 1]) / (Math.pow(2, k) - 1);
            }
        }
        return D[order];
    }
}
