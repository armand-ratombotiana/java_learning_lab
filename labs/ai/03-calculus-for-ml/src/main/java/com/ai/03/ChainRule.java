package com.ai03;

public class ChainRule {

    public static double computeDerivative(
            java.util.function.DoubleUnaryOperator outer,
            java.util.function.DoubleUnaryOperator inner,
            double x, double h) {
        double dOuter = (outer.applyAsDouble(inner.applyAsDouble(x) + h)
            - outer.applyAsDouble(inner.applyAsDouble(x) - h)) / (2 * h);
        double dInner = (inner.applyAsDouble(x + h) - inner.applyAsDouble(x - h)) / (2 * h);
        return dOuter * dInner;
    }

    public static double[] chainRuleVector(
            java.util.function.Function<double[], double[]> outerFunc,
            java.util.function.Function<double[], double[]> innerFunc,
            double[] x, double h) {
        double[] inner = innerFunc.apply(x);
        double[] outerInput = inner;
        double[] result = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            double[] xPlus = x.clone(); xPlus[i] += h;
            double[] xMinus = x.clone(); xMinus[i] -= h;
            double[] innerPlus = innerFunc.apply(xPlus);
            double[] innerMinus = innerFunc.apply(xMinus);
            double[] outerPlus = outerFunc.apply(innerPlus);
            double[] outerMinus = outerFunc.apply(innerMinus);
            result[i] = (outerPlus[0] - outerMinus[0]) / (2 * h);
        }
        return result;
    }

    public static double sigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }

    public static void main(String[] args) {
        System.out.println("=== Chain Rule Demo ===");
        java.util.function.DoubleUnaryOperator g = x -> 2 * x + 1;
        java.util.function.DoubleUnaryOperator f = x -> x * x;
        double x = 3;
        double derivative = computeDerivative(f, g, x, 1e-5);
        System.out.println("f(g(x)) where f(x)=x^2, g(x)=2x+1 at x=3");
        System.out.println("df/dx = " + derivative + " (expected: 4*(2*3+1) = 28)");

        double dSigmoid = (sigmoid(1.001) - sigmoid(0.999)) / 0.002;
        System.out.println("sigmoid'(1) ≈ " + dSigmoid);
    }
}
