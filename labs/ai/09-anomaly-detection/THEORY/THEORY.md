# Anomaly Detection - Complete Theory

## 1. Statistical Methods

### 1.1 Z-Score
z = (x - mu) / sigma
Flag if |z| > threshold

### 1.2 IQR Method
Outlier if x < Q1 - 1.5*IQR or x > Q3 + 1.5*IQR

### 1.3 Mahalanobis Distance
d = sqrt((x - mu)^T S^{-1} (x - mu))
For multivariate data

## 2. Density-Based Methods

### 2.1 Kernel Density Estimation
P(x) = (1/n) sum K((x - x_i)/h)

### 2.2 LOF (Local Outlier Factor)
Compare local density of point to neighbors
LOF > 1 indicates outlier

## 3. Isolation Forest

### 3.1 Algorithm
- Build random trees
- Points isolated quickly = outliers
- Average path length measures anomaly score

### 3.2 Score
s(x) = 2^{-E[h(x)]/c(n)}

## 4. One-Class SVM

### 4.1 Find hyperplane separating data from origin
Maximize distance from origin

### 4.2 Kernel trick
Non-linear boundary

## 5. Autoencoders

### 5.1 Train on normal data
Learn reconstruction of normal patterns

### 5.2 Detection
High reconstruction error = anomaly

### 5.3 Threshold
Set based on validation set

## 6. Ensemble Methods

### 6.1 Multiple perspectives
- Feature-based
- proximity-based
- reconstruction-based

## Java Implementation

```java
public class AnomalyDetector {
    private Vector mean;
    private Matrix cov;
    private double threshold;
    
    public void fit(Matrix Xtrain) {
        mean = computeMean(Xtrain);
        cov = Covariance.compute(Xtrain);
        // Set threshold based on training anomalies
    }
    
    public boolean isAnomaly(Vector x) {
        double md = mahalanobisDistance(x, mean, cov);
        return md > threshold;
    }
    
    private double mahalanobisDistance(Vector x, Vector mu, Matrix S) {
        Vector diff = VectorOperations.subtract(x, mu);
        Matrix Sinv = AdvancedMatrixOps.inverse(S);
        Vector temp = MatrixOperations.multiply(Sinv, diff);
        return Math.sqrt(VectorOperations.dot(diff, temp));
    }
}
```