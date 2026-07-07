package com.algo.lab30;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LinearProgrammingTest {

    @Test
    void testSimplexBasic() {
        double[][] A = {{1, 2}, {2, 1}};
        double[] b = {10, 10};
        double[] c = {1, 1};
        Simplex simplex = new Simplex(A, b, c);
        double[] x = simplex.solve();
        assertNotNull(x);
        assertEquals(20.0 / 3, simplex.getOptimalValue(), 1e-6);
    }

    @Test
    void testSimplexSingleVariable() {
        double[][] A = {{1}};
        double[] b = {5};
        double[] c = {1};
        Simplex simplex = new Simplex(A, b, c);
        double[] x = simplex.solve();
        assertNotNull(x);
        assertEquals(5, simplex.getOptimalValue(), 1e-6);
    }

    @Test
    void testTwoPhaseFeasible() {
        double[][] A = {{1, 1, 1}, {2, 1, 0}};
        double[] b = {6, 5};
        double[] c = {3, 2, 1};
        TwoPhaseSimplex tps = new TwoPhaseSimplex();
        double[] x = tps.solve(A, b, c);
        assertNotNull(x);
    }

    @Test
    void testLPSolverInterface() {
        LPSolver solver = (c, A, b, sense) -> {
            Simplex s = new Simplex(A, b, c);
            return s.solve();
        };
        double[][] A = {{1, 1}};
        double[] b = {5};
        double[] c = {1, 1};
        double[] x = solver.solve(c, A, b, new String[]{"<="});
        assertNotNull(x);
    }
}
