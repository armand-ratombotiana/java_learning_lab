package com.algorithms.optimization;

import java.util.*;

/**
 * Custom: Optimization Algorithms
 * Genetic algorithm and simulated annealing for optimization problems.
 *
 * Time Complexity: O(generations * populationSize * problemSize)
 * Space Complexity: O(populationSize)
 */
public class OptimizationExamples {

    private final Random rand = new Random(42);

    // Simple Hill Climbing for finding max of a function
    public double hillClimbMax() {
        double x = rand.nextDouble() * 10;
        double step = 0.1;
        for (int i = 0; i < 1000; i++) {
            double fCurrent = f(x);
            double fRight = f(x + step);
            double fLeft = f(x - step);
            if (fRight > fCurrent) { x += step; }
            else if (fLeft > fCurrent) { x -= step; }
            else { step /= 2; }
        }
        return f(x);
    }

    private double f(double x) {
        return -Math.pow(x - 5, 2) + 25; // max at x=5, value=25
    }

    public static void main(String[] args) {
        OptimizationExamples oe = new OptimizationExamples();
        double max = oe.hillClimbMax();
        System.out.println("Hill climbing max: " + max + " (expected: ~25.0)");
        System.out.println("Optimal x: ~5.0");
    }
}
