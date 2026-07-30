package com.math.deep.lab02;

import java.util.function.DoubleUnaryOperator;
import java.util.function.DoubleBinaryOperator;

public class OptimizationTheory {

    private static final int MAX_ITER = 10000;
    private static final double EPS = 1e-10;

    public static double gradientDescent(DoubleUnaryOperator f, DoubleUnaryOperator df, double x0, double lr) {
        double x = x0;
        for (int i = 0; i < MAX_ITER; i++) {
            double grad = df.applyAsDouble(x);
            if (Math.abs(grad) < EPS) return x;
            x = x - lr * grad;
        }
        return x;
    }

    public static double gradientDescentArmijo(DoubleUnaryOperator f, DoubleUnaryOperator df, double x0) {
        double x = x0;
        for (int i = 0; i < MAX_ITER; i++) {
            double grad = df.applyAsDouble(x);
            if (Math.abs(grad) < EPS) return x;
            double fx = f.applyAsDouble(x);
            double alpha = 1.0;
            while (f.applyAsDouble(x - alpha * grad) > fx - 0.5 * alpha * grad * grad) {
                alpha *= 0.5;
                if (alpha < 1e-20) break;
            }
            x = x - alpha * grad;
        }
        return x;
    }

    public static double newtonMethod(DoubleUnaryOperator f, DoubleUnaryOperator df, DoubleUnaryOperator d2f, double x0) {
        double x = x0;
        for (int i = 0; i < MAX_ITER; i++) {
            double grad = df.applyAsDouble(x);
            if (Math.abs(grad) < EPS) return x;
            double hess = d2f.applyAsDouble(x);
            if (Math.abs(hess) < EPS) throw new ArithmeticException("Hessian too small");
            x = x - grad / hess;
        }
        return x;
    }

    public static double lagrangeMultiplier(DoubleUnaryOperator f, DoubleUnaryOperator g, double lambda0, double x0, double lr) {
        double lambda = lambda0;
        double x = x0;
        for (int i = 0; i < MAX_ITER; i++) {
            double gradL_x = dfdx(f, x) + lambda * dfdx(g, x);
            double gradL_lambda = g.applyAsDouble(x);
            x = x - lr * gradL_x;
            lambda = lambda + lr * gradL_lambda;
            if (Math.abs(gradL_x) < EPS && Math.abs(gradL_lambda) < EPS) break;
        }
        return x;
    }

    public static boolean isConvex(DoubleUnaryOperator d2f, double x) {
        return d2f.applyAsDouble(x) >= -EPS;
    }

    private static double dfdx(DoubleUnaryOperator f, double x) {
        double h = 1e-8;
        return (f.applyAsDouble(x + h) - f.applyAsDouble(x - h)) / (2.0 * h);
    }

    public static double projectedGradientDescent(DoubleUnaryOperator f, DoubleUnaryOperator df,
                                                   double x0, double lr, double lower, double upper) {
        double x = x0;
        for (int i = 0; i < MAX_ITER; i++) {
            double grad = df.applyAsDouble(x);
            if (Math.abs(grad) < EPS) return Math.max(lower, Math.min(upper, x));
            x = x - lr * grad;
            x = Math.max(lower, Math.min(upper, x));
        }
        return Math.max(lower, Math.min(upper, x));
    }
}
