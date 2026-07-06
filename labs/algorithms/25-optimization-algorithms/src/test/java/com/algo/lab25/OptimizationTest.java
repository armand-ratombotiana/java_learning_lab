package com.algo.lab25;

import org.junit.jupiter.api.Test;
import java.util.function.Function;
import static org.junit.jupiter.api.Assertions.*;

class OptimizationTest {

    private final Function<double[], Double> sphere = x -> {
        double sum = 0;
        for (double v : x) sum -= v * v;
        return sum;
    };

    private final Function<double[], Double> rastrigin = x -> {
        double sum = 0;
        for (double v : x) sum -= (v * v - 10 * Math.cos(2 * Math.PI * v) + 10);
        return sum;
    };

    @Test
    void testGeneticAlgorithm() {
        GeneticAlgorithm ga = new GeneticAlgorithm(50, 0.1, 0.8, -10, 10);
        double[] result = ga.optimize(sphere, 2, 100);
        assertNotNull(result);
        assertEquals(2, result.length);
    }

    @Test
    void testGeneticAlgorithmSphere() {
        GeneticAlgorithm ga = new GeneticAlgorithm(100, 0.05, 0.9, -5.12, 5.12);
        double[] result = ga.optimize(sphere, 2, 200);
        double fitness = sphere.apply(result);
        assertTrue(fitness > -100);
    }

    @Test
    void testSimulatedAnnealing() {
        SimulatedAnnealing sa = new SimulatedAnnealing(100, 0.95, -10, 10);
        double[] result = sa.optimize(sphere, 2, 1000);
        assertNotNull(result);
        assertEquals(2, result.length);
    }

    @Test
    void testSimulatedAnnealingRastrigin() {
        SimulatedAnnealing sa = new SimulatedAnnealing(100, 0.99, -5.12, 5.12);
        double[] result = sa.optimize(rastrigin, 2, 500);
        assertNotNull(result);
    }

    @Test
    void testHillClimbing() {
        HillClimbing hc = new HillClimbing(0.1, -10, 10);
        double[] result = hc.optimize(sphere, 2, 1000);
        assertNotNull(result);
        assertEquals(2, result.length);
    }

    @Test
    void testHillClimbingSingleDimension() {
        HillClimbing hc = new HillClimbing(0.5, -10, 10);
        double[] result = hc.optimize(x -> -(x[0] * x[0]), 1, 500);
        assertNotNull(result);
    }

    @Test
    void testParticleSwarm() {
        ParticleSwarm pso = new ParticleSwarm(30, 0.7, 1.5, 1.5, -10, 10);
        double[] result = pso.optimize(sphere, 2, 100);
        assertNotNull(result);
        assertEquals(2, result.length);
    }

    @Test
    void testParticleSwarmRastrigin() {
        ParticleSwarm pso = new ParticleSwarm(50, 0.7, 1.5, 1.5, -5.12, 5.12);
        double[] result = pso.optimize(rastrigin, 2, 200);
        assertNotNull(result);
    }

    @Test
    void testFunctionOptimizerInterface() {
        FunctionOptimizer optimizer = (f, dim, iter) -> new double[dim];
        double[] result = optimizer.optimize(sphere, 3, 10);
        assertEquals(3, result.length);
    }

    @Test
    void testOptimizersConvergeBasic() {
        Function<double[], Double> simple = x -> -(x[0] - 3) * (x[0] - 3);
        HillClimbing hc = new HillClimbing(0.1, -10, 10);
        double[] result = hc.optimize(simple, 1, 5000);
        double fitness = simple.apply(result);
        assertTrue(fitness > -10);
    }
}
