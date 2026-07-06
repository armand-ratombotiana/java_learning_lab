package com.algo.lab25;

import java.util.random.RandomGenerator;
import java.util.function.Function;

/**
 * Particle Swarm Optimization (PSO).
 * Simulates a swarm of particles moving through the search space.
 * Time: O(population * iterations * dimensions), Space: O(population * dimensions)
 */
public class ParticleSwarm implements FunctionOptimizer {

    private final RandomGenerator rng = RandomGenerator.getDefault();
    private final int swarmSize;
    private final double inertia;
    private final double cognitiveWeight;
    private final double socialWeight;
    private final double lowerBound;
    private final double upperBound;

    public ParticleSwarm(int swarmSize, double inertia, double cognitiveWeight,
                         double socialWeight, double lowerBound, double upperBound) {
        this.swarmSize = swarmSize;
        this.inertia = inertia;
        this.cognitiveWeight = cognitiveWeight;
        this.socialWeight = socialWeight;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    @Override
    public double[] optimize(Function<double[], Double> fitness, int dimensions, int maxIterations) {
        double[][] positions = new double[swarmSize][dimensions];
        double[][] velocities = new double[swarmSize][dimensions];
        double[][] personalBest = new double[swarmSize][dimensions];
        double[] personalBestFitness = new double[swarmSize];
        double[] globalBest = new double[dimensions];
        double globalBestFitness = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < swarmSize; i++) {
            for (int j = 0; j < dimensions; j++) {
                positions[i][j] = lowerBound + rng.nextDouble() * (upperBound - lowerBound);
                velocities[i][j] = (rng.nextDouble() - 0.5) * (upperBound - lowerBound) * 0.1;
            }
            personalBest[i] = positions[i].clone();
            personalBestFitness[i] = fitness.apply(positions[i]);
            if (personalBestFitness[i] > globalBestFitness) {
                globalBestFitness = personalBestFitness[i];
                globalBest = personalBest[i].clone();
            }
        }
        for (int iter = 0; iter < maxIterations; iter++) {
            for (int i = 0; i < swarmSize; i++) {
                for (int j = 0; j < dimensions; j++) {
                    double r1 = rng.nextDouble();
                    double r2 = rng.nextDouble();
                    velocities[i][j] = inertia * velocities[i][j]
                        + cognitiveWeight * r1 * (personalBest[i][j] - positions[i][j])
                        + socialWeight * r2 * (globalBest[j] - positions[i][j]);
                    positions[i][j] += velocities[i][j];
                    positions[i][j] = Math.max(lowerBound, Math.min(upperBound, positions[i][j]));
                }
                double currentFitness = fitness.apply(positions[i]);
                if (currentFitness > personalBestFitness[i]) {
                    personalBestFitness[i] = currentFitness;
                    personalBest[i] = positions[i].clone();
                }
                if (currentFitness > globalBestFitness) {
                    globalBestFitness = currentFitness;
                    globalBest = positions[i].clone();
                }
            }
        }
        return globalBest;
    }
}
