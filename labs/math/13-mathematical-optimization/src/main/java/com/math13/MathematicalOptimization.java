package com.math13;

import java.util.function.Function;
import java.util.function.BiFunction;

public class MathematicalOptimization {

    public interface MultivariateFunction {
        double apply(double[] x);
    }

    public interface GradientFunction {
        double[] apply(double[] x);
    }

    public static double[] gradientDescent(MultivariateFunction f, GradientFunction grad,
                                            double[] start, double learningRate, int iterations) {
        double[] x = start.clone();
        for (int iter = 0; iter < iterations; iter++) {
            double[] g = grad.apply(x);
            for (int i = 0; i < x.length; i++) {
                x[i] -= learningRate * g[i];
            }
        }
        return x;
    }

    public static double[] gradientDescentMomentum(MultivariateFunction f, GradientFunction grad,
                                                     double[] start, double learningRate,
                                                     double momentum, int iterations) {
        double[] x = start.clone();
        double[] v = new double[x.length];
        for (int iter = 0; iter < iterations; iter++) {
            double[] g = grad.apply(x);
            for (int i = 0; i < x.length; i++) {
                v[i] = momentum * v[i] + learningRate * g[i];
                x[i] -= v[i];
            }
        }
        return x;
    }

    public static double[] gradientDescentNesterov(MultivariateFunction f, GradientFunction grad,
                                                     double[] start, double learningRate,
                                                     double momentum, int iterations) {
        double[] x = start.clone();
        double[] v = new double[x.length];
        for (int iter = 0; iter < iterations; iter++) {
            double[] lookahead = x.clone();
            for (int i = 0; i < x.length; i++) lookahead[i] -= momentum * v[i];
            double[] g = grad.apply(lookahead);
            for (int i = 0; i < x.length; i++) {
                v[i] = momentum * v[i] + learningRate * g[i];
                x[i] -= v[i];
            }
        }
        return x;
    }

    public static double[] adagrad(MultivariateFunction f, GradientFunction grad,
                                    double[] start, double learningRate, int iterations) {
        double[] x = start.clone();
        double[] G = new double[x.length];
        double eps = 1e-8;
        for (int iter = 0; iter < iterations; iter++) {
            double[] g = grad.apply(x);
            for (int i = 0; i < x.length; i++) {
                G[i] += g[i] * g[i];
                x[i] -= learningRate * g[i] / (Math.sqrt(G[i]) + eps);
            }
        }
        return x;
    }

    public static double[] adam(MultivariateFunction f, GradientFunction grad,
                                 double[] start, double learningRate,
                                 double beta1, double beta2, int iterations) {
        double[] x = start.clone();
        double[] m = new double[x.length];
        double[] v = new double[x.length];
        double eps = 1e-8;
        for (int t = 1; t <= iterations; t++) {
            double[] g = grad.apply(x);
            for (int i = 0; i < x.length; i++) {
                m[i] = beta1 * m[i] + (1 - beta1) * g[i];
                v[i] = beta2 * v[i] + (1 - beta2) * g[i] * g[i];
                double mHat = m[i] / (1 - Math.pow(beta1, t));
                double vHat = v[i] / (1 - Math.pow(beta2, t));
                x[i] -= learningRate * mHat / (Math.sqrt(vHat) + eps);
            }
        }
        return x;
    }

    public static double newtonMethod1D(Function<Double, Double> f,
                                         Function<Double, Double> fPrime,
                                         Function<Double, Double> fDoublePrime,
                                         double start, int iterations) {
        double x = start;
        for (int i = 0; i < iterations; i++) {
            double fp = fPrime.apply(x);
            double fpp = fDoublePrime.apply(x);
            if (Math.abs(fpp) < 1e-15) break;
            x -= fp / fpp;
        }
        return x;
    }

    public static double findRootNewton(Function<Double, Double> f,
                                         Function<Double, Double> fPrime,
                                         double start, int iterations) {
        double x = start;
        for (int i = 0; i < iterations; i++) {
            double fx = f.apply(x);
            double fpx = fPrime.apply(x);
            if (Math.abs(fpx) < 1e-15) break;
            x -= fx / fpx;
        }
        return x;
    }

    public static double[] lagrangeMultipliers(Function<double[], Double> f,
                                                Function<double[], double[]> constraints,
                                                double[] start, double learningRate, int iterations) {
        int n = start.length;
        int m = 1; // Assume one constraint for simplicity
        double[] x = start.clone();
        double lambda = 0;
        for (int iter = 0; iter < iterations; iter++) {
            double[] gradF = numericalGradient(f, x);
            double[] gradG = numericalGradient(v -> constraints.apply(x)[0], x); // simplified
            double gx = constraints.apply(x)[0];
            for (int i = 0; i < n; i++) {
                x[i] -= learningRate * (gradF[i] + lambda * gradG[i]);
            }
            lambda += learningRate * gx;
        }
        return x;
    }

    public static double[] numericalGradient(MultivariateFunction f, double[] x) {
        double h = 1e-8;
        double[] grad = new double[x.length];
        double fx = f.apply(x);
        for (int i = 0; i < x.length; i++) {
            double[] xPlus = x.clone();
            xPlus[i] += h;
            grad[i] = (f.apply(xPlus) - fx) / h;
        }
        return grad;
    }

    public static MultivariateFunction numericalGradient(MultivariateFunction f) {
        return x -> numericalGradient(f, x);
    }

    public static double lineSearch(Function<Double, Double> f, double start, double direction, double maxStep) {
        double alpha = 1.0;
        double fx = f.apply(start);
        double fPrime0 = (f.apply(start + 1e-8) - fx) / 1e-8;
        for (int i = 0; i < 20; i++) {
            double fxNew = f.apply(start + alpha * direction);
            if (fxNew <= fx + 0.5 * alpha * fPrime0 * direction) break;
            alpha *= 0.5;
        }
        return alpha;
    }

    public static double goldenSectionSearch(Function<Double, Double> f, double a, double b, int iterations) {
        double phi = (Math.sqrt(5) - 1) / 2;
        double c = b - phi * (b - a);
        double d = a + phi * (b - a);
        for (int i = 0; i < iterations; i++) {
            if (f.apply(c) < f.apply(d)) {
                b = d;
            } else {
                a = c;
            }
            c = b - phi * (b - a);
            d = a + phi * (b - a);
        }
        return (a + b) / 2;
    }

    public static double[] bfgs(MultivariateFunction f, GradientFunction grad,
                                 double[] start, int iterations) {
        int n = start.length;
        double[] x = start.clone();
        double[][] H = identity(n);
        for (int iter = 0; iter < iterations; iter++) {
            double[] g = grad.apply(x);
            double[] p = multiplyMatrixVector(H, scaleVector(g, -1));
            double alpha = 1.0;
            for (int i = 0; i < 20; i++) {
                double[] xNew = x.clone();
                for (int j = 0; j < n; j++) xNew[j] += alpha * p[j];
                double[] gNew = grad.apply(xNew);
                boolean sufficient = true;
                for (int j = 0; j < n; j++) {
                    if (gNew[j] > g[j] + 0.5 * alpha * dotProduct(g, g)) {
                        sufficient = false;
                        break;
                    }
                }
                if (sufficient) break;
                alpha *= 0.5;
            }
            double[] s = new double[n];
            for (int i = 0; i < n; i++) s[i] = alpha * p[i];
            double[] xNew = x.clone();
            for (int i = 0; i < n; i++) xNew[i] += s[i];
            double[] gNew = grad.apply(xNew);
            double[] y = new double[n];
            for (int i = 0; i < n; i++) y[i] = gNew[i] - g[i];
            double rho = 1.0 / dotProduct(y, s);
            double[][] I = identity(n);
            double[][] term1 = outerProduct(y, s);
            double[][] term2 = outerProduct(s, y);
            for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) term1[i][j] *= rho;
            double[][] left = subtractMatrices(I, term1);
            double[][] right = subtractMatrices(I, term2);
            H = multiplyMatrices(multiplyMatrices(left, H), right);
            double[][] ssTerm = outerProduct(s, s);
            for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) ssTerm[i][j] *= rho;
            H = addMatrices(H, ssTerm);
            x = xNew;
        }
        return x;
    }

    private static double[][] identity(int n) {
        double[][] I = new double[n][n];
        for (int i = 0; i < n; i++) I[i][i] = 1;
        return I;
    }

    private static double[] multiplyMatrixVector(double[][] m, double[] v) {
        double[] r = new double[v.length];
        for (int i = 0; i < m.length; i++)
            for (int j = 0; j < v.length; j++)
                r[i] += m[i][j] * v[j];
        return r;
    }

    private static double[] scaleVector(double[] v, double s) {
        double[] r = new double[v.length];
        for (int i = 0; i < v.length; i++) r[i] = v[i] * s;
        return r;
    }

    private static double dotProduct(double[] a, double[] b) {
        double s = 0;
        for (int i = 0; i < a.length; i++) s += a[i] * b[i];
        return s;
    }

    private static double[][] outerProduct(double[] a, double[] b) {
        double[][] m = new double[a.length][b.length];
        for (int i = 0; i < a.length; i++)
            for (int j = 0; j < b.length; j++)
                m[i][j] = a[i] * b[j];
        return m;
    }

    private static double[][] addMatrices(double[][] a, double[][] b) {
        double[][] r = new double[a.length][a[0].length];
        for (int i = 0; i < a.length; i++)
            for (int j = 0; j < a[0].length; j++)
                r[i][j] = a[i][j] + b[i][j];
        return r;
    }

    private static double[][] subtractMatrices(double[][] a, double[][] b) {
        double[][] r = new double[a.length][a[0].length];
        for (int i = 0; i < a.length; i++)
            for (int j = 0; j < a[0].length; j++)
                r[i][j] = a[i][j] - b[i][j];
        return r;
    }

    private static double[][] multiplyMatrices(double[][] a, double[][] b) {
        int r1 = a.length, c1 = a[0].length, c2 = b[0].length;
        double[][] r = new double[r1][c2];
        for (int i = 0; i < r1; i++)
            for (int j = 0; j < c2; j++)
                for (int k = 0; k < c1; k++)
                    r[i][j] += a[i][k] * b[k][j];
        return r;
    }
}
