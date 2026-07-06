package com.algo.lab25;

import java.util.function.Function;

/**
 * Common interface for optimization algorithms.
 * Optimizes a function over a given domain dimension.
 */
@FunctionalInterface
public interface FunctionOptimizer {

    double[] optimize(Function<double[], Double> fitnessFunction, int dimensions, int maxIterations);
}
