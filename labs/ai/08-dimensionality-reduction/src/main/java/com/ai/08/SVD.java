package com.ai08;

public class SVD {
    private double[][] U, S, Vt;
    private int m, n;

    public void fit(double[][] matrix) {
        m = matrix.length;
        n = matrix[0].length;
        double[][] A = copyMatrix(matrix);
        int k = Math.min(m, n);
        U = new double[m][k];
        S = new double[k][k];
        Vt = new double[k][n];
        double[][] ATA = new double[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                for (int l = 0; l < m; l++)
                    ATA[i][j] += A[l][i] * A[l][j];
        double[][] eigenvectors = new double[k][n];
        double[] eigenvalues = new double[k];
        for (int comp = 0; comp < k; comp++) {
            double[] b = new double[n];
            for (int i = 0; i < n; i++) b[i] = 1;
            for (int iter = 0; iter < 100; iter++) {
                double[] newB = new double[n];
                for (int i = 0; i < n; i++)
                    for (int j = 0; j < n; j++)
                        newB[i] += ATA[i][j] * b[j];
                double norm = 0;
                for (double v : newB) norm += v * v;
                norm = Math.sqrt(norm);
                for (int i = 0; i < n; i++) b[i] = newB[i] / norm;
            }
            System.arraycopy(b, 0, eigenvectors[comp], 0, n);
            double eval = 0;
            for (int i = 0; i < n; i++)
                eval += b[i] * (ATA[i][0] * b[0] + ATA[i][1] * b[1]);
            eigenvalues[comp] = Math.abs(eval);
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    ATA[i][j] -= eigenvalues[comp] * eigenvectors[comp][i] * eigenvectors[comp][j];
        }
        for (int i = 0; i < k; i++) {
            System.arraycopy(eigenvectors[i], 0, Vt[i], 0, n);
            S[i][i] = Math.sqrt(Math.max(eigenvalues[i], 0));
        }
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < k; j++) {
                U[i][j] = 0;
                if (S[j][j] > 1e-10)
                    for (int l = 0; l < n; l++)
                        U[i][j] += A[i][l] * Vt[j][l] / S[j][j];
            }
        }
    }

    private double[][] copyMatrix(double[][] m) {
        double[][] c = new double[m.length][m[0].length];
        for (int i = 0; i < m.length; i++)
            System.arraycopy(m[i], 0, c[i], 0, m[0].length);
        return c;
    }

    public double[][] reconstruct() {
        double[][] result = new double[m][n];
        int k = S.length;
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                for (int l = 0; l < k; l++)
                    result[i][j] += U[i][l] * S[l][l] * Vt[l][j];
        return result;
    }

    public double[][] getU() { return U; }
    public double[][] getS() { return S; }
    public double[][] getVt() { return Vt; }

    public static void main(String[] args) {
        System.out.println("=== SVD Demo ===");
        double[][] data = {{1, 0, 0, 0}, {0, 1, 0, 0}, {0, 0, 1, 0}};
        SVD svd = new SVD();
        svd.fit(data);
        System.out.println("U: " + java.util.Arrays.deepToString(svd.getU()));
        System.out.println("S: " + java.util.Arrays.deepToString(svd.getS()));
        System.out.println("Vt: " + java.util.Arrays.deepToString(svd.getVt()));
        double[][] reconstructed = svd.reconstruct();
        System.out.println("Reconstructed:");
        for (double[] row : reconstructed)
            System.out.println("  " + java.util.Arrays.toString(row));
    }
}
