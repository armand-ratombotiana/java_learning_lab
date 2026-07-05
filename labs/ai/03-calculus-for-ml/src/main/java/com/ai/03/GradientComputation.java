package com.ai03;

public class GradientComputation {

    public static double numericalGradient(java.util.function.DoubleUnaryOperator f, double x, double h) {
        return (f.applyAsDouble(x + h) - f.applyAsDouble(x - h)) / (2 * h);
    }

    public static double centralDifference(java.util.function.DoubleUnaryOperator f, double x, double h) {
        return (f.applyAsDouble(x + h) - f.applyAsDouble(x - h)) / (2 * h);
    }

    public static double forwardDifference(java.util.function.DoubleUnaryOperator f, double x, double h) {
        return (f.applyAsDouble(x + h) - f.applyAsDouble(x)) / h;
    }

    public static double[] gradient2D(java.util.function.BiFunction<Double, Double, Double> f, double x, double y, double h) {
        double dfdx = (f.apply(x + h, y) - f.apply(x - h, y)) / (2 * h);
        double dfdy = (f.apply(x, y + h) - f.apply(x, y - h)) / (2 * h);
        return new double[]{dfdx, dfdy};
    }

    public static double[] numericalGradientVector(
            java.util.function.Function<double[], Double> f, double[] x, double h) {
        double[] gradient = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            double[] xPlus = x.clone();
            double[] xMinus = x.clone();
            xPlus[i] += h;
            xMinus[i] -= h;
            gradient[i] = (f.apply(xPlus) - f.apply(xMinus)) / (2 * h);
        }
        return gradient;
    }

    public static void main(String[] args) {
        System.out.println("=== Gradient Computation Demo ===");
        java.util.function.DoubleUnaryOperator f = x -> x * x;
        double x = 2.0;
        double grad = numericalGradient(f, x, 1e-5);
        System.out.println("f(x)=x^2, gradient at x=2: " + grad + " (expected: 4)");

        java.util.function.BiFunction<Double, Double, Double> f2 = (a, b) -> a * a + b * b;
        double[] g = gradient2D(f2, 1, 2, 1e-5);
        System.out.println("f(x,y)=x^2+y^2, gradient at (1,2): [" + g[0] + ", " + g[1] + "]");

        java.util.function.Function<double[], Double> f3 = v -> v[0] * v[0] + v[1] * v[1] + v[2] * v[2];
        double[] x3 = {1, 2, 3};
        double[] grad3 = numericalGradientVector(f3, x3, 1e-5);
        System.out.println("Gradient of sum of squares at (1,2,3): " + java.util.Arrays.toString(grad3));
    }
}
