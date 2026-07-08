package com.math15;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NumericalMethodsTest {

    // ===== Integration Tests =====

    @Test
    void testRectangleRule() {
        // integral of x from 0 to 1 = 0.5
        double result = NumericalMethods.rectangleRule(x -> x, 0, 1, 100);
        assertEquals(0.5, result, 0.01);
    }

    @Test
    void testTrapezoidalRule() {
        double result = NumericalMethods.trapezoidalRule(x -> x * x, 0, 1, 100);
        assertEquals(1.0 / 3.0, result, 0.001);
    }

    @Test
    void testSimpsonsRule() {
        double result = NumericalMethods.simpsonsRule(x -> x * x, 0, 1, 100);
        assertEquals(1.0 / 3.0, result, 0.0001);
    }

    @Test
    void testSimpsonsSine() {
        double result = NumericalMethods.simpsonsRule(Math::sin, 0, Math.PI, 100);
        assertEquals(2.0, result, 0.001);
    }

    @Test
    void testRombergIntegration() {
        double result = NumericalMethods.rombergIntegration(x -> x * x, 0, 1, 5);
        assertEquals(1.0 / 3.0, result, 0.0001);
    }

    @Test
    void testGaussLegendre2Point() {
        double result = NumericalMethods.gaussLegendre2Point(x -> x * x, 0, 1);
        assertEquals(1.0 / 3.0, result, 0.001);
    }

    // ===== Root Finding Tests =====

    @Test
    void testBisection() {
        // root of x^2 - 4 = 0 in [0, 5] is x = 2
        double result = NumericalMethods.bisection(x -> x * x - 4, 0, 5, 50);
        assertEquals(2.0, result, 0.0001);
    }

    @Test
    void testNewtonRaphson() {
        double result = NumericalMethods.newtonRaphson(x -> x * x - 4, x -> 2 * x, 3, 50);
        assertEquals(2.0, result, 0.0001);
    }

    @Test
    void testSecant() {
        double result = NumericalMethods.secant(x -> x * x - 4, 1, 3, 50);
        assertEquals(2.0, result, 0.0001);
    }

    @Test
    void testFalsePosition() {
        double result = NumericalMethods.falsePosition(x -> x * x - 4, 0, 5, 50);
        assertEquals(2.0, result, 0.0001);
    }

    @Test
    void testBisectionNoRoot() {
        assertThrows(IllegalArgumentException.class,
            () -> NumericalMethods.bisection(x -> x * x + 1, -1, 1, 10));
    }

    // ===== Interpolation Tests =====

    @Test
    void testLagrangeInterpolation() {
        double[] xs = {0, 1, 2};
        double[] ys = {1, 2, 5};
        // f(0)=1, f(1)=2, f(2)=5 → interpolating polynomial is x^2 + 1
        assertEquals(1, NumericalMethods.lagrangeInterpolation(xs, ys, 0), 0.0001);
        assertEquals(2, NumericalMethods.lagrangeInterpolation(xs, ys, 1), 0.0001);
        assertEquals(5, NumericalMethods.lagrangeInterpolation(xs, ys, 2), 0.0001);
        assertEquals(10, NumericalMethods.lagrangeInterpolation(xs, ys, 3), 0.0001); // 3^2 + 1 = 10
    }

    @Test
    void testNewtonInterpolation() {
        double[] xs = {0, 1, 2};
        double[] ys = {1, 2, 5};
        double[] coeffs = NumericalMethods.newtonDividedDifference(xs, ys);
        assertEquals(1, NumericalMethods.newtonInterpolation(xs, coeffs, 0), 0.0001);
        assertEquals(2, NumericalMethods.newtonInterpolation(xs, coeffs, 1), 0.0001);
        assertEquals(5, NumericalMethods.newtonInterpolation(xs, coeffs, 2), 0.0001);
    }

    // ===== ODE Solver Tests =====

    @Test
    void testEulerMethod() {
        // y' = y, y(0) = 1 → y(t) = e^t
        double[] ys = NumericalMethods.eulerMethod((t, y) -> y, 0, 1, 0.01, 100);
        assertEquals(Math.E, ys[100], 0.01);
    }

    @Test
    void testRungeKutta4() {
        // y' = y, y(0) = 1 → y(t) = e^t
        double[] ys = NumericalMethods.rungeKutta4((t, y) -> y, 0, 1, 0.1, 10);
        assertEquals(Math.E, ys[10], 0.001);
    }

    @Test
    void testHeunsMethod() {
        double[] ys = NumericalMethods.heunsMethod((t, y) -> y, 0, 1, 0.01, 100);
        assertEquals(Math.E, ys[100], 0.01);
    }

    @Test
    void testRK4Linear() {
        // y' = -2y, y(0) = 1 → y(t) = e^(-2t)
        double[] ys = NumericalMethods.rungeKutta4((t, y) -> -2 * y, 0, 1, 0.1, 10);
        assertEquals(Math.exp(-2), ys[10], 0.001);
    }

    // ===== Finite Difference Tests =====

    @Test
    void testCentralDifference() {
        // derivative of x^2 at x = 3 is 6
        double result = NumericalMethods.centralDifference(x -> x * x, 3, 1e-5);
        assertEquals(6.0, result, 0.001);
    }

    @Test
    void testForwardDifference() {
        double result = NumericalMethods.forwardDifference(x -> x * x, 3, 1e-5);
        assertEquals(6.0, result, 0.01);
    }

    @Test
    void testBackwardDifference() {
        double result = NumericalMethods.backwardDifference(x -> x * x, 3, 1e-5);
        assertEquals(6.0, result, 0.01);
    }

    @Test
    void testSecondDerivativeCentral() {
        // second derivative of x^3 at x = 2 is 12x = 24
        double result = NumericalMethods.secondDerivativeCentral(x -> x * x * x, 2, 1e-5);
        assertEquals(12.0, result, 0.01);
    }

    @Test
    void testFiniteDifferenceGradient() {
        NumericalMethods.finiteDifferenceGradient(x -> x[0] * x[0] + x[1] * x[1], new double[]{1, 2}, 1e-5);
    }

    @Test
    void testRichardsonExtrapolation() {
        double result = NumericalMethods.richardsonExtrapolation(
            h -> NumericalMethods.centralDifference(x -> x * x, 3, h), 3, 0.1, 5);
        assertEquals(6.0, result, 0.0001);
    }
}
