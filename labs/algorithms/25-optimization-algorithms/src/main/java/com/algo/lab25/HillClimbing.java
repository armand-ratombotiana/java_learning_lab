package com.algo.lab25;

import java.util.random.RandomGenerator;
import java.util.function.Function;

/**
 * Stochastic Hill Climbing for optimization.
 * Iteratively moves to a better neighbor solution.
 * Time: O(maxIterations * dimensions), Space: O(dimensions)
 */
public class HillClimbing implements FunctionOptimizer {

    private final RandomGenerator rng = RandomGenerator.getDefault();
    private final double stepSize;
    private final double lowerBound;
    private final double upperBound;

    public HillClimbing(double stepSize, double lowerBound, double upperBound) {
        this.stepSize = stepSize;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    @Override
    public double[] optimize(Function<double[], Double> fitness, int dimensions, int maxIterations) {
        double[] current = new double[dimensions];
        for (int i = 0; i < dimensions; i++) {
            current[i] = lowerBound + rng.nextDouble() * (upperBound - lowerBound);
        }
        double currentFitness = fitness.apply(current);
        for (int iter = 0; iter < maxIterations; iter++) {
            double[] neighbor = generateNeighbor(current);
            double neighborFitness = fitness.apply(neighbor);
            if (neighborFitness > currentFitness) {
                current = neighbor;
                currentFitness = neighborFitness;
            }
        }
        return current;
    }

    private double[] generateNeighbor(double[] solution) {
        double[] neighbor = solution.clone();
        int idx = rng.nextInt(solution.length);
        neighbor[idx] += (rng.nextDouble() - 0.5) * 2 * stepSize;
        neighbor[idx] = Math.max(lowerBound, Math.min(upperBound, neighbor[idx]));
        return neighbor;
    }
}
