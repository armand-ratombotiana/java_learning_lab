package com.algo.lab25;

import java.util.random.RandomGenerator;
import java.util.function.Function;

/**
 * Simulated Annealing for optimization (e.g., TSP).
 * Gradually reduces temperature to escape local optima.
 * Time: O(maxIterations * dimensions), Space: O(dimensions)
 */
public class SimulatedAnnealing implements FunctionOptimizer {

    private final RandomGenerator rng = RandomGenerator.getDefault();
    private final double initialTemperature;
    private final double coolingRate;
    private final double lowerBound;
    private final double upperBound;

    public SimulatedAnnealing(double initialTemperature, double coolingRate,
                              double lowerBound, double upperBound) {
        this.initialTemperature = initialTemperature;
        this.coolingRate = coolingRate;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    @Override
    public double[] optimize(Function<double[], Double> fitness, int dimensions, int maxIterations) {
        double[] current = new double[dimensions];
        for (int i = 0; i < dimensions; i++) {
            current[i] = lowerBound + rng.nextDouble() * (upperBound - lowerBound);
        }
        double[] best = current.clone();
        double bestFitness = fitness.apply(best);
        double temperature = initialTemperature;
        for (int iter = 0; iter < maxIterations; iter++) {
            double[] next = perturb(current);
            double currentFitness = fitness.apply(current);
            double nextFitness = fitness.apply(next);
            if (nextFitness > currentFitness || rng.nextDouble() < Math.exp((nextFitness - currentFitness) / temperature)) {
                current = next;
                if (nextFitness > bestFitness) {
                    bestFitness = nextFitness;
                    best = next.clone();
                }
            }
            temperature *= coolingRate;
        }
        return best;
    }

    private double[] perturb(double[] solution) {
        double[] next = solution.clone();
        int idx = rng.nextInt(solution.length);
        next[idx] += (rng.nextDouble() - 0.5) * (upperBound - lowerBound) * 0.1;
        next[idx] = Math.max(lowerBound, Math.min(upperBound, next[idx]));
        return next;
    }
}
