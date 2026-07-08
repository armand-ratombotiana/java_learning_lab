package com.math13;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MathematicalOptimizationTest {

    @Test
    void testGradientDescent() {
        // Minimize f(x) = x^2 + 2x + 1 (minimum at x = -1)
        MathematicalOptimization.MultivariateFunction f = x -> x[0] * x[0] + 2 * x[0] + 1;
        MathematicalOptimization.GradientFunction grad = x -> new double[]{2 * x[0] + 2};
        double[] result = MathematicalOptimization.gradientDescent(f, grad, new double[]{5}, 0.1, 100);
        assertEquals(-1.0, result[0], 0.01);
    }

    @Test
    void testGradientDescent2D() {
        // f(x,y) = x^2 + y^2, minimum at (0,0)
        MathematicalOptimization.MultivariateFunction f = x -> x[0] * x[0] + x[1] * x[1];
        MathematicalOptimization.GradientFunction grad = x -> new double[]{2 * x[0], 2 * x[1]};
        double[] result = MathematicalOptimization.gradientDescent(f, grad, new double[]{3, 4}, 0.1, 100);
        assertEquals(0.0, result[0], 0.01);
        assertEquals(0.0, result[1], 0.01);
    }

    @Test
    void testMomentum() {
        MathematicalOptimization.MultivariateFunction f = x -> x[0] * x[0];
        MathematicalOptimization.GradientFunction grad = x -> new double[]{2 * x[0]};
        double[] result = MathematicalOptimization.gradientDescentMomentum(f, grad, new double[]{10}, 0.1, 0.9, 100);
        assertEquals(0.0, result[0], 0.1);
    }

    @Test
    void testNesterov() {
        MathematicalOptimization.MultivariateFunction f = x -> x[0] * x[0];
        MathematicalOptimization.GradientFunction grad = x -> new double[]{2 * x[0]};
        double[] result = MathematicalOptimization.gradientDescentNesterov(f, grad, new double[]{10}, 0.1, 0.9, 100);
        assertEquals(0.0, result[0], 0.1);
    }

    @Test
    void testAdagrad() {
        MathematicalOptimization.MultivariateFunction f = x -> x[0] * x[0] + x[1] * x[1];
        MathematicalOptimization.GradientFunction grad = x -> new double[]{2 * x[0], 2 * x[1]};
        double[] result = MathematicalOptimization.adagrad(f, grad, new double[]{5, 5}, 1.0, 100);
        assertEquals(0.0, result[0], 0.1);
        assertEquals(0.0, result[1], 0.1);
    }

    @Test
    void testAdam() {
        MathematicalOptimization.MultivariateFunction f = x -> x[0] * x[0] + x[1] * x[1];
        MathematicalOptimization.GradientFunction grad = x -> new double[]{2 * x[0], 2 * x[1]};
        double[] result = MathematicalOptimization.adam(f, grad, new double[]{3, 4}, 0.1, 0.9, 0.999, 200);
        assertEquals(0.0, result[0], 0.1);
        assertEquals(0.0, result[1], 0.1);
    }

    @Test
    void testNewtonMethod1D() {
        // Find minimum of f(x) = x^2 (minimum at x = 0)
        double result = MathematicalOptimization.newtonMethod1D(
            x -> x * x, x -> 2 * x, x -> 2.0, 5, 10);
        assertEquals(0.0, result, 0.01);
    }

    @Test
    void testFindRootNewton() {
        // Find root of f(x) = x^2 - 4 (roots at ±2)
        double result = MathematicalOptimization.findRootNewton(
            x -> x * x - 4, x -> 2 * x, 3, 10);
        assertEquals(2.0, result, 0.01);
    }

    @Test
    void testNumericalGradient() {
        MathematicalOptimization.MultivariateFunction f = x -> 3 * x[0] * x[0] + 2 * x[0] * x[1] + x[1] * x[1];
        double[] g = MathematicalOptimization.numericalGradient(f, new double[]{1, 2});
        assertEquals(8.0, g[0], 0.01); // ∂f/∂x = 6x + 2y = 6 + 4 = 10... wait let's recalc
        // f = 3x^2 + 2xy + y^2, ∂f/∂x = 6x + 2y = 6+4 = 10, ∂f/∂y = 2x + 2y = 2+4 = 6
        assertEquals(10.0, g[0], 0.01);
        assertEquals(6.0, g[1], 0.01);
    }

    @Test
    void testGoldenSectionSearch() {
        // Minimum of x^2 on [-5, 5] is at x = 0
        double result = MathematicalOptimization.goldenSectionSearch(x -> x * x, -5, 5, 50);
        assertEquals(0.0, result, 0.01);
    }

    @Test
    void testGradientDescentRosenbrock() {
        // Rosenbrock function: f(x,y) = (1-x)^2 + 100(y-x^2)^2
        MathematicalOptimization.MultivariateFunction f = x ->
            Math.pow(1 - x[0], 2) + 100 * Math.pow(x[1] - x[0] * x[0], 2);
        MathematicalOptimization.GradientFunction grad = x -> new double[]{
            -2 * (1 - x[0]) - 400 * x[0] * (x[1] - x[0] * x[0]),
            200 * (x[1] - x[0] * x[0])
        };
        double[] result = MathematicalOptimization.gradientDescent(f, grad, new double[]{-1, 1}, 0.001, 5000);
        assertEquals(1.0, result[0], 0.1);
        assertEquals(1.0, result[1], 0.1);
    }

    @Test
    void testBfgsRosenbrock() {
        MathematicalOptimization.MultivariateFunction f = x ->
            Math.pow(1 - x[0], 2) + 100 * Math.pow(x[1] - x[0] * x[0], 2);
        MathematicalOptimization.GradientFunction grad = x -> new double[]{
            -2 * (1 - x[0]) - 400 * x[0] * (x[1] - x[0] * x[0]),
            200 * (x[1] - x[0] * x[0])
        };
        double[] result = MathematicalOptimization.bfgs(f, grad, new double[]{-1, 1}, 50);
        assertEquals(1.0, result[0], 0.1);
        assertEquals(1.0, result[1], 0.1);
    }
}
