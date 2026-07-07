package com.algo.lab30;

public class TwoPhaseSimplex {

    public double[] solve(double[][] A, double[] b, double[] c) {
        int m = b.length;
        int n = c.length;
        double[][] A2 = new double[m][n + m];
        double[] c2 = new double[n + m];
        for (int i = 0; i < m; i++) {
            System.arraycopy(A[i], 0, A2[i], 0, n);
            A2[i][n + i] = 1;
            c2[n + i] = 1;
        }
        Simplex phase1 = new Simplex(A2, b, c2);
        double[] xPhase1 = phase1.solve();
        if (xPhase1 == null || Math.abs(phase1.getOptimalValue()) > 1e-6) return null;

        double[][] A3 = new double[m][n];
        for (int i = 0; i < m; i++) {
            System.arraycopy(A[i], 0, A3[i], 0, n);
        }
        Simplex phase2 = new Simplex(A3, b, c);
        return phase2.solve();
    }
}
