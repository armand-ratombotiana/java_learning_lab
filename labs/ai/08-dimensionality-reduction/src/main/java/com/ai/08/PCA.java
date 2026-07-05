package com.ai08;

public class PCA {
    private double[] mean;
    private double[][] components;
    private int nComponents;

    public PCA(int nComponents) {
        this.nComponents = nComponents;
    }

    public void fit(double[][] data) {
        int n = data.length;
        int dims = data[0].length;
        mean = new double[dims];
        for (int i = 0; i < n; i++)
            for (int d = 0; d < dims; d++)
                mean[d] += data[i][d];
        for (int d = 0; d < dims; d++)
            mean[d] /= n;
        double[][] centered = new double[n][dims];
        for (int i = 0; i < n; i++)
            for (int d = 0; d < dims; d++)
                centered[i][d] = data[i][d] - mean[d];
        double[][] covMatrix = new double[dims][dims];
        for (int i = 0; i < dims; i++)
            for (int j = 0; j < dims; j++)
                for (int k = 0; k < n; k++)
                    covMatrix[i][j] += centered[k][i] * centered[k][j];
        for (int i = 0; i < dims; i++)
            for (int j = 0; j < dims; j++)
                covMatrix[i][j] /= (n - 1);
        components = powerIterationEigenvectors(covMatrix, nComponents);
    }

    private double[][] powerIterationEigenvectors(double[][] matrix, int numComponents) {
        int dims = matrix.length;
        double[][] eigenvectors = new double[numComponents][dims];
        double[][] residual = new double[dims][dims];
        for (int i = 0; i < dims; i++)
            System.arraycopy(matrix[i], 0, residual[i], 0, dims);
        for (int comp = 0; comp < numComponents; comp++) {
            double[] b = new double[dims];
            for (int i = 0; i < dims; i++) b[i] = 1;
            for (int iter = 0; iter < 100; iter++) {
                double[] newB = new double[dims];
                for (int i = 0; i < dims; i++)
                    for (int j = 0; j < dims; j++)
                        newB[i] += residual[i][j] * b[j];
                double norm = 0;
                for (double v : newB) norm += v * v;
                norm = Math.sqrt(norm);
                for (int i = 0; i < dims; i++) b[i] = newB[i] / norm;
            }
            System.arraycopy(b, 0, eigenvectors[comp], 0, dims);
            double eigenvalue = 0;
            for (int i = 0; i < dims; i++)
                eigenvalue += eigenvectors[comp][i] * b[i];
            for (int i = 0; i < dims; i++)
                for (int j = 0; j < dims; j++)
                    residual[i][j] -= eigenvalue * eigenvectors[comp][i] * eigenvectors[comp][j];
        }
        return eigenvectors;
    }

    public double[][] transform(double[][] data) {
        int n = data.length;
        double[][] result = new double[n][nComponents];
        for (int i = 0; i < n; i++) {
            for (int c = 0; c < nComponents; c++) {
                double sum = 0;
                for (int d = 0; d < data[0].length; d++)
                    sum += (data[i][d] - mean[d]) * components[c][d];
                result[i][c] = sum;
            }
        }
        return result;
    }

    public double[] getMean() { return mean; }
    public double[][] getComponents() { return components; }

    public static void main(String[] args) {
        System.out.println("=== PCA Demo ===");
        double[][] data = {
            {2.5, 2.4}, {0.5, 0.7}, {2.2, 2.9},
            {1.9, 2.2}, {3.1, 3.0}, {2.3, 2.7},
            {2.0, 1.6}, {1.0, 1.1}, {1.5, 1.6}, {1.1, 0.9}
        };
        PCA pca = new PCA(1);
        pca.fit(data);
        double[][] transformed = pca.transform(data);
        System.out.println("Mean: " + java.util.Arrays.toString(pca.getMean()));
        System.out.println("Principal component: " + java.util.Arrays.toString(pca.getComponents()[0]));
        System.out.println("Transformed (1D): " + java.util.Arrays.toString(transformed[0]));
    }
}
