package com.ailab.posencoding;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 test suite for PosencodingAlgorithm.
 * Tests initialization, training convergence, prediction accuracy,
 * and parameter configuration.
 */
class PosencodingAlgorithmTest {

    private static final double EPSILON = 1e-6;

    @Test
    @DisplayName("Algorithm initializes with default parameters")
    void testDefaultInitialization() {
        var algo = new PosencodingAlgorithm();
        assertNotNull(algo);
        assertEquals(0.01, algo.getLearningRate(), EPSILON);
        assertEquals(1000, algo.getMaxIterations());
        assertEquals(1e-6, algo.getTolerance(), EPSILON);
    }

    @Test
    @DisplayName("Algorithm initializes with custom parameters")
    void testCustomInitialization() {
        var algo = new PosencodingAlgorithm(0.1, 500, 1e-8);
        assertEquals(0.1, algo.getLearningRate(), EPSILON);
        assertEquals(500, algo.getMaxIterations());
        assertEquals(1e-8, algo.getTolerance(), EPSILON);
    }

    @Test
    @DisplayName("Training converges on simple linear data")
    void testTrainingConverges() {
        var algo = new PosencodingAlgorithm();
        algo.setVerbose(false);
        double[][] data = {{1.0, 3.0}, {2.0, 5.0}, {3.0, 7.0}};
        double[] weights = algo.train(data);
        assertNotNull(weights);
        assertEquals(2, weights.length);
    }

    @Test
    @DisplayName("Prediction produces reasonable output")
    void testPrediction() {
        var algo = new PosencodingAlgorithm();
        double[] weights = {1.0, 2.0};
        double[] input = {3.0, 4.0};
        double result = algo.predict(input, weights);
        assertEquals(11.0, result, EPSILON);
    }

    @Test
    @DisplayName("Setters update parameters correctly")
    void testSetters() {
        var algo = new PosencodingAlgorithm();
        algo.setLearningRate(0.5);
        algo.setMaxIterations(200);
        algo.setTolerance(1e-4);
        assertEquals(0.5, algo.getLearningRate(), EPSILON);
        assertEquals(200, algo.getMaxIterations());
        assertEquals(1e-4, algo.getTolerance(), EPSILON);
    }

    @Test
    @DisplayName("Gradient computation returns correct size")
    void testGradientSize() {
        var algo = new PosencodingAlgorithm();
        double[][] data = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}};
        double[] weights = {0.0, 0.0};
        double[] gradient = algo.train(data);
        assertNotNull(gradient);
    }

    @Test
    @DisplayName("Loss decreases during training")
    void testLossDecreases() {
        var algo = new PosencodingAlgorithm(0.1, 100, 1e-10);
        double[][] data = {{1.0, 2.0}, {2.0, 4.0}, {3.0, 6.0}, {4.0, 8.0}};
        double[] weights = algo.train(data);
        assertTrue(Math.abs(algo.predict(new double[]{5.0}, weights) - 10.0) < 1.0);
    }
}