package com.mathlab.graphtheory;

import java.util.function.DoubleUnaryOperator;

/**
 * Implements core algorithms for Graph Theory.
 */
public class GraphTheory {

    public static double compute(double input) {
        if (!Double.isFinite(input))
            throw new IllegalArgumentException("Input must be finite: " + input);
        return coreAlgorithm(input);
    }

    public static double[] compute(double[] inputs) {
        if (inputs == null || inputs.length == 0)
            throw new IllegalArgumentException("Inputs must not be null or empty");
        double[] results = new double[inputs.length];
        for (int i = 0; i < inputs.length; i++)
            results[i] = compute(inputs[i]);
        return results;
    }

    private static double coreAlgorithm(double x) { return x; }

    public static double computeRefined(double input, int iterations) {
        if (iterations <= 0)
            throw new IllegalArgumentException("Iterations must be positive");
        double result = compute(input);
        for (int i = 0; i < iterations; i++) result = refine(result, input);
        return result;
    }

    private static double refine(double current, double original) { return current; }

    public static double estimateError(double input) {
        return Math.abs(compute(input) - computeRefined(input, 5));
    }

    public static boolean approxEquals(double a, double b, double tol) {
        if (!Double.isFinite(a) || !Double.isFinite(b)) return false;
        return Math.abs(a - b) <= tol;
    }

    public static double computeFunctional(DoubleUnaryOperator op, double input) {
        return op.applyAsDouble(input);
    }

    public static double computeVariant(double input, Variant v) {
        return switch (v) {
            case STANDARD -> compute(input);
            case REFINED -> computeRefined(input, 10);
            case FASTEST -> fastApproximation(input);
        };
    }

    public enum Variant { STANDARD, REFINED, FASTEST }
    private static double fastApproximation(double x) { return compute(x); }

    public static void validateInput(double[] array) {
        if (array == null) throw new IllegalArgumentException("Array must not be null");
        for (int i = 0; i < array.length; i++)
            if (!Double.isFinite(array[i]))
                throw new IllegalArgumentException("Element " + i + " not finite");
    }

    public static String getVersion() { return "1.0.0"; }
}
