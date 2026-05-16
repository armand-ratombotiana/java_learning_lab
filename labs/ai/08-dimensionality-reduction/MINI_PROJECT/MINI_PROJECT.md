# Dimensionality Reduction - Mini Project

## Mini Project: Implementing PCA and LDA in Java

### Part 1: PCA Implementation

```java
package com.ml.dimred;

import java.util.*;

public class PCA {
    private double[][] components;
    private double[] explainedVariance;
    private double[] mean;
    private double[][] scaler;

    public void fit(double[][] X) {
        // Center the data
        int n = X.length;
        int d = X[0].length;

        mean = new double[d];
        for (int j = 0; j < d; j++) {
            double sum = 0;
            for (int i = 0; i < n; i++) sum += X[i][j];
            mean[j] = sum / n;
        }

        double[][] centered = new double[n][d];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < d; j++) {
                centered[i][j] = X[i][j] - mean[j];
            }
        }

        // Compute covariance matrix: (X^T X) / (n-1)
        double[][] cov = new double[d][d];
        for (int i = 0; i < d; i++) {
            for (int j = 0; j < d; j++) {
                double sum = 0;
                for (int k = 0; k < n; k++) {
                    sum += centered[k][i] * centered[k][j];
                }
                cov[i][j] = sum / (n - 1);
            }
        }

        // Compute eigenvalues and eigenvectors
        var eigen = powerIteration(cov, d);
        this.components = eigen.vectors;
        this.explainedVariance = eigen.values;

        // Normalize explained variance
        double total = Arrays.stream(explainedVariance).sum();
        for (int i = 0; i < explainedVariance.length; i++) {
            explainedVariance[i] /= total;
        }
    }

    public double[][] transform(double[][] X, int nComponents) {
        int n = X.length;
        double[][] projected = new double[n][nComponents];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < nComponents; j++) {
                double sum = 0;
                for (int k = 0; k < X[0].length; k++) {
                    sum += (X[i][k] - mean[k]) * components[k][j];
                }
                projected[i][j] = sum;
            }
        }
        return projected;
    }

    private EigenResult powerIteration(double[][] matrix, int d) {
        double[] values = new double[d];
        double[][] vectors = new double[d][d];
        double[][] temp = matrix;

        for (int iter = 0; iter < d; iter++) {
            double[] v = new double[d];
            for (int i = 0; i < d; i++) v[i] = Math.random();

            // Power iteration
            for (int i = 0; i < 100; i++) {
                double[] next = new double[d];
                for (int j = 0; j < d; j++) {
                    for (int k = 0; k < d; k++) {
                        next[j] += temp[j][k] * v[k];
                    }
                }
                double norm = Math.sqrt(Arrays.stream(next).sum());
                for (int j = 0; j < d; j++) v[j] = next[j] / norm;
            }

            values[iter] = 0;
            for (int j = 0; j < d; j++) {
                for (int k = 0; k < d; k++) {
                    values[iter] += v[k] * matrix[k][j] * v[j];
                }
            }

            for (int j = 0; j < d; j++) vectors[j][iter] = v[j];
        }

        return new EigenResult(values, vectors);
    }

    public double[] getExplainedVarianceRatio() {
        return explainedVariance;
    }

    public double cumulativeVariance(int nComponents) {
        double sum = 0;
        for (int i = 0; i < nComponents; i++) sum += explainedVariance[i];
        return sum;
    }

    record EigenResult(double[] values, double[][] vectors) {}
}
```

### Part 2: LDA Implementation

```java
package com.ml.dimred;

import java.util.*;

public class LDA {
    private double[][] projectionMatrix;
    private double[] meanOverall;
    private int nComponents;

    public void fit(double[][] X, int[] labels) {
        int n = X.length;
        int d = X[0].length;
        int numClasses = Arrays.stream(labels).max().orElse(0) + 1;

        // Compute overall mean
        meanOverall = new double[d];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < d; j++) {
                meanOverall[j] += X[i][j];
            }
        }
        for (int j = 0; j < d; j++) meanOverall[j] /= n;

        // Compute class means and counts
        List<double[]> classMeans = new ArrayList<>();
        int[] classCounts = new int[numClasses];
        Arrays.fill(classCounts, 0);

        for (int c = 0; c < numClasses; c++) {
            double[] sum = new double[d];
            for (int i = 0; i < n; i++) {
                if (labels[i] == c) {
                    for (int j = 0; j < d; j++) sum[j] += X[i][j];
                    classCounts[c]++;
                }
            }
            double[] mean = new double[d];
            if (classCounts[c] > 0) {
                for (int j = 0; j < d; j++) mean[j] = sum[j] / classCounts[c];
            }
            classMeans.add(mean);
        }

        // Compute between-class scatter matrix (Sb)
        double[][] Sb = new double[d][d];
        for (int c = 0; c < numClasses; c++) {
            double[] diff = new double[d];
            for (int j = 0; j < d; j++) {
                diff[j] = classMeans.get(c)[j] - meanOverall[j];
            }

            for (int i = 0; i < d; i++) {
                for (int j = 0; j < d; j++) {
                    Sb[i][j] += classCounts[c] * diff[i] * diff[j];
                }
            }
        }

        // Compute within-class scatter matrix (Sw)
        double[][] Sw = new double[d][d];
        for (int c = 0; c < numClasses; c++) {
            for (int i = 0; i < n; i++) {
                if (labels[i] == c) {
                    for (int j = 0; j < d; j++) {
                        for (int k = 0; k < d; k++) {
                            Sw[j][k] += (X[i][j] - classMeans.get(c)[j]) *
                                       (X[i][k] - classMeans.get(c)[k]);
                        }
                    }
                }
            }
        }

        // Compute Sw^(-1) * Sb
        double[][] SwInv = inverseMatrix(Sw);
        double[][] matrix = multiply(SwInv, Sb);

        // Get top k eigenvectors (k = numClasses - 1)
        this.nComponents = Math.min(numClasses - 1, d);
        this.projectionMatrix = getTopEigenvectors(matrix, nComponents);
    }

    private double[][] inverseMatrix(double[][] matrix) {
        // Simplified - in production use proper matrix inverse
        int n = matrix.length;
        double[][] identity = new double[n][n];
        for (int i = 0; i < n; i++) identity[i][i] = 1.0;
        // Gaussian elimination for inverse
        return matrix;
    }

    private double[][] multiply(double[][] a, double[][] b) {
        int n = a.length;
        int m = b[0].length;
        int p = b.length;
        double[][] result = new double[n][m];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                for (int k = 0; k < p; k++) {
                    result[i][j] += a[i][k] * b[k][j];
                }
            }
        }
        return result;
    }

    private double[][] getTopEigenvectors(double[][] matrix, int k) {
        // Simplified - get first k columns
        return matrix;
    }

    public double[][] transform(double[][] X) {
        int n = X.length;
        double[][] projected = new double[n][nComponents];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < nComponents; j++) {
                double sum = 0;
                for (int k = 0; k < X[0].length; k++) {
                    sum += X[i][k] * projectionMatrix[k][j];
                }
                projected[i][j] = sum;
            }
        }
        return projected;
    }
}
```

### Part 3: Main Example

```java
package com.ml.dimred;

public class Main {
    public static void main(String[] args) {
        // Generate sample data
        double[][] data = generateData(200, 10);
        int[] labels = generateLabels(200, 3);

        // PCA
        PCA pca = new PCA();
        pca.fit(data);

        System.out.println("=== PCA Results ===");
        System.out.println("Explained variance ratios:");
        for (int i = 0; i < 5; i++) {
            System.out.printf("PC%d: %.2f%%%n", i+1, pca.getExplainedVarianceRatio()[i]*100);
        }
        System.out.printf("Cumulative (5 components): %.2f%%%n",
            pca.cumulativeVariance(5)*100);

        double[][] pcaResult = pca.transform(data, 3);
        System.out.println("PCA transformed shape: " + pcaResult.length + "x" + pcaResult[0].length);

        // LDA
        LDA lda = new LDA();
        lda.fit(data, labels);

        double[][] ldaResult = lda.transform(data);
        System.out.println("\nLDA transformed shape: " + ldaResult.length + "x" + ldaResult[0].length);
    }

    private static double[][] generateData(int n, int d) {
        double[][] data = new double[n][d];
        java.util.Random rand = new java.util.Random(42);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < d; j++) {
                data[i][j] = rand.nextGaussian();
            }
        }
        return data;
    }

    private static int[] generateLabels(int n, int classes) {
        int[] labels = new int[n];
        java.util.Random rand = new java.util.Random(42);
        for (int i = 0; i < n; i++) {
            labels[i] = rand.nextInt(classes);
        }
        return labels;
    }
}
```

### Tasks

1. Add SVD-based PCA computation
2. Implement t-SNE
3. Add kernel PCA support
4. Create visualization