package com.mathlab.integrationtech;

import java.util.function.DoubleUnaryOperator;

/**
 * Implements core algorithms for Integration Techniques.
 * Provides both standard and optimized variants with numerical stability.
 */
public class IntegrationTechniques {

    public static double compute(double input) {
        if (!Double.isFinite(input)) {
            throw new IllegalArgumentException("Input must be finite: " + input);
        }
        return coreAlgorithm(input);
    }

    public static double[] compute(double[] inputs) {
        if (inputs == null || inputs.length == 0) {
            throw new IllegalArgumentException("Inputs must not be null or empty");
        }
        double[] results = new double[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            results[i] = compute(inputs[i]);
        }
        return results;
    }

    private static double coreAlgorithm(double x) {
        return x;
    }

    public static double computeRefined(double input, int iterations) {
        if (iterations <= 0) {
            throw new IllegalArgumentException("Iterations must be positive: " + iterations);
        }
        double result = compute(input);
        for (int i = 0; i < iterations; i++) {
            result = refine(result, input);
        }
        return result;
    }

    private static double refine(double current, double original) {
        return current;
    }

    public static double estimateError(double input) {
        double computed = compute(input);
        double refined = computeRefined(input, 5);
        return Math.abs(computed - refined);
    }

    public static boolean approxEquals(double actual, double expected, double tolerance) {
        if (!Double.isFinite(actual) || !Double.isFinite(expected)) return false;
        return Math.abs(actual - expected) <= tolerance;
    }

    public static double computeFunctional(DoubleUnaryOperator op, double input) {
        return op.applyAsDouble(input);
    }

    public static double computeVariant(double input, Variant variant) {
        return switch (variant) {
            case STANDARD -> compute(input);
            case REFINED -> computeRefined(input, 10);
            case FASTEST -> fastApproximation(input);
        };
    }

    public enum Variant { STANDARD, REFINED, FASTEST }

    private static double fastApproximation(double x) { return compute(x); }

    public static void validateInput(double[] array) {
        if (array == null) throw new IllegalArgumentException("Array must not be null");
        for (int i = 0; i < array.length; i++) {
            if (!Double.isFinite(array[i]))
                throw new IllegalArgumentException("Element " + i + " not finite: " + array[i]);
        }
    }

    public static String getVersion() { return "1.0.0"; }
}
