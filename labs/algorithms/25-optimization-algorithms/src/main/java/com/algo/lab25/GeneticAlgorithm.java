package com.algo.lab25;

import java.util.*;
import java.util.random.RandomGenerator;
import java.util.function.Function;

/**
 * Genetic Algorithm for function optimization.
 * Uses selection, crossover, and mutation to evolve solutions.
 * Time: O(population * generations * dimensions), Space: O(population * dimensions)
 */
public class GeneticAlgorithm implements FunctionOptimizer {

    private final RandomGenerator rng = RandomGenerator.getDefault();
    private final int populationSize;
    private final double mutationRate;
    private final double crossoverRate;
    private final double lowerBound;
    private final double upperBound;

    public GeneticAlgorithm(int populationSize, double mutationRate, double crossoverRate,
                            double lowerBound, double upperBound) {
        this.populationSize = populationSize;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    @Override
    public double[] optimize(Function<double[], Double> fitness, int dimensions, int maxIterations) {
        double[][] population = initializePopulation(dimensions);
        double[] bestSolution = null;
        double bestFitness = Double.NEGATIVE_INFINITY;
        for (int iter = 0; iter < maxIterations; iter++) {
            double[] fitnessValues = new double[populationSize];
            for (int i = 0; i < populationSize; i++) {
                fitnessValues[i] = fitness.apply(population[i]);
                if (fitnessValues[i] > bestFitness) {
                    bestFitness = fitnessValues[i];
                    bestSolution = population[i].clone();
                }
            }
            double[][] newPopulation = new double[populationSize][dimensions];
            for (int i = 0; i < populationSize; i++) {
                double[] parent1 = select(population, fitnessValues);
                double[] parent2 = select(population, fitnessValues);
                double[] child = crossover(parent1, parent2);
                mutate(child);
                newPopulation[i] = child;
            }
            population = newPopulation;
        }
        return bestSolution;
    }

    private double[][] initializePopulation(int dimensions) {
        double[][] pop = new double[populationSize][dimensions];
        for (int i = 0; i < populationSize; i++) {
            for (int j = 0; j < dimensions; j++) {
                pop[i][j] = lowerBound + rng.nextDouble() * (upperBound - lowerBound);
            }
        }
        return pop;
    }

    private double[] select(double[][] population, double[] fitnessValues) {
        double total = 0;
        for (double f : fitnessValues) total += f;
        double r = rng.nextDouble() * total;
        double cumulative = 0;
        for (int i = 0; i < populationSize; i++) {
            cumulative += fitnessValues[i];
            if (cumulative >= r) return population[i];
        }
        return population[populationSize - 1];
    }

    private double[] crossover(double[] p1, double[] p2) {
        int dim = p1.length;
        double[] child = new double[dim];
        if (rng.nextDouble() < crossoverRate) {
            int point = rng.nextInt(dim);
            for (int i = 0; i < dim; i++) {
                child[i] = (i < point) ? p1[i] : p2[i];
            }
        } else {
            for (int i = 0; i < dim; i++) {
                child[i] = (p1[i] + p2[i]) / 2;
            }
        }
        return child;
    }

    private void mutate(double[] solution) {
        for (int i = 0; i < solution.length; i++) {
            if (rng.nextDouble() < mutationRate) {
                solution[i] = lowerBound + rng.nextDouble() * (upperBound - lowerBound);
            }
        }
    }
}
