# PCA Code Deep Dive

This lab provides a pure Java implementation of Principal Component Analysis (PCA). 
*Note: In a production environment, you would use a highly optimized linear algebra library like Apache Commons Math or ND4J to compute the Eigen Decomposition, as writing a robust eigenvalue solver from scratch is extremely complex and error-prone.*

## 💻 Pure Java Implementation (Conceptual)

```java file="labs/data-science/03-feature-engineering/dimensionality-reduction/SOLUTION/PCA.java"
package datascience.dimensionality;

/**
 * A conceptual implementation of PCA.
 * For brevity and reliability, this example assumes the existence of a matrix library
 * to handle the complex Eigen Decomposition step.
 */
public class PCA {

    /**
     * Step 1: Mean Centering
     */
    public static double[][] centerData(double[][] data) {
        int n = data.length;
        int d = data[0].length;
        double[][] centered = new double[n][d];
        
        // Calculate mean for each column (feature)
        double[] means = new double[d];
        for (int j = 0; j < d; j++) {
            for (int i = 0; i < n; i++) {
                means[j] += data[i][j];
            }
            means[j] /= n;
        }
        
        // Subtract mean from data
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < d; j++) {
                centered[i][j] = data[i][j] - means[j];
            }
        }
        return centered;
    }

    /**
     * Step 2: Covariance Matrix Calculation
     */
    public static double[][] calculateCovarianceMatrix(double[][] centeredData) {
        int n = centeredData.length;
        int d = centeredData[0].length;
        double[][] covariance = new double[d][d];
        
        // Matrix multiplication: (X^T * X) / (n - 1)
        for (int i = 0; i < d; i++) {
            for (int j = 0; j < d; j++) {
                double sum = 0;
                for (int k = 0; k < n; k++) {
                    sum += centeredData[k][i] * centeredData[k][j];
                }
                covariance[i][j] = sum / (n - 1);
            }
        }
        return covariance;
    }

    /**
     * Step 5: Project Data onto Principal Components
     * 
     * @param centeredData The mean-centered original data (n x d)
     * @param topEigenvectors The selected K eigenvectors (d x k)
     * @return The reduced data (n x k)
     */
    public static double[][] projectData(double[][] centeredData, double[][] topEigenvectors) {
        int n = centeredData.length;
        int d = centeredData[0].length;
        int k = topEigenvectors[0].length;
        
        double[][] reduced = new double[n][k];
        
        // Matrix multiplication: X_centered * W
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < k; j++) {
                double sum = 0;
                for (int x = 0; x < d; x++) {
                    sum += centeredData[i][x] * topEigenvectors[x][j];
                }
                reduced[i][j] = sum;
            }
        }
        return reduced;
    }

    public static void main(String[] args) {
        // Example Dataset: 4 samples, 3 features (3D space)
        double[][] data = {
            {2.5, 2.4, 0.5},
            {0.5, 0.7, 0.1},
            {2.2, 2.9, 0.8},
            {1.9, 2.2, 0.3}
        };

        // 1. Center the data
        double[][] centered = centerData(data);
        
        // 2. Calculate Covariance
        double[][] covariance = calculateCovarianceMatrix(centered);
        
        // 3 & 4. Eigen Decomposition (Simulated)
        // In reality, you pass 'covariance' to a library like Apache Commons Math:
        // EigenDecomposition ed = new EigenDecomposition(new Array2DRowRealMatrix(covariance));
        // RealMatrix V = ed.getV(); // The eigenvectors
        
        // Pretend these are the top 2 eigenvectors returned by the library (reducing 3D to 2D)
        double[][] simulatedTop2Eigenvectors = {
            {0.677, -0.735},
            {0.735,  0.677},
            {0.000,  0.000}
        };

        // 5. Project the data
        double[][] reducedData = projectData(centered, simulatedTop2Eigenvectors);
        
        System.out.println("Reduced Data (2D):");
        for (double[] row : reducedData) {
            System.out.printf("[%.4f, %.4f]\n", row[0], row[1]);
        }
    }
}
```

## 🔍 Key Takeaways
1. **The Covariance Matrix**: Look at `calculateCovarianceMatrix`. It results in a $d \times d$ matrix. If you have 10,000 features, this creates a $10,000 \times 10,000$ matrix (100 million elements). This is why PCA can be computationally expensive on very high-dimensional data, often requiring techniques like Truncated SVD instead.
2. **The Projection**: The final step is simply a matrix multiplication. We are taking the dot product of our original data points with the new "axes" (eigenvectors) we discovered. This rotates and squashes the data into the new, denser coordinate system.