# Real World Project: ML Pipeline with Linear Algebra

## Project: Image Classification Feature Extraction Pipeline

### Overview
Build a production-ready ML pipeline that uses linear algebra for image feature extraction and classification.

### Architecture

```
Image Input -> Preprocessing -> Feature Extraction -> Classifier -> Output
```

### Implementation

```java
package com.ml.pipeline;

public class ImagePipeline {
    private Matrix mean;
    private Matrix principalComponents;
    private Vector weights;
    private double bias;
    
    public void train(Matrix X, int[] labels, int nComponents) {
        // 1. Center the data
        mean = computeMean(X);
        Matrix Xcentered = centerData(X, mean);
        
        // 2. Compute covariance matrix
        Matrix cov = computeCovariance(Xcentered);
        
        // 3. SVD for principal components
        SVD.SVDResult svd = SVD.compute(cov);
        principalComponents = extractComponents(svd, nComponents);
        
        // 4. Project to lower dimension
        Matrix Xprojected = project(Xcentered, principalComponents);
        
        // 5. Train classifier
        weights = trainLogisticRegression(Xprojected, labels);
    }
    
    public int predict(double[] image) {
        Vector x = new Vector(image);
        Vector xCentered = VectorOperations.subtract(x, mean);
        Vector xProj = projectVector(xCentered, principalComponents);
        return classify(xProj);
    }
}
```

### Data Flow

```java
public static void main(String[] args) {
    // Load dataset (784 features per image for MNIST)
    Matrix Xtrain = loadTrainingData("train.csv");
    int[] ytrain = loadLabels("train_labels.csv");
    
    ImagePipeline pipeline = new ImagePipeline();
    pipeline.train(Xtrain, ytrain, 50);
    
    double[] testImage = loadImage("test_sample.png");
    int prediction = pipeline.predict(testImage);
    System.out.println("Predicted digit: " + prediction);
}
```

### Linear Algebra Operations Used

1. Matrix subtraction: X - mean (centering)
2. Matrix multiplication: X^T X (covariance)
3. SVD: Dimensionality reduction
4. Matrix-vector multiplication: Feature projection
5. Vector addition/subtraction: Gradient computation
6. Dot product: Logistic regression

### Performance Optimization

```java
// Use LU decomposition for solving normal equations
Matrix XtX = MatrixOperations.multiply(Xt, X);
Matrix XtX_reg = MatrixOperations.add(XtX, 
    MatrixOperations.scale(Matrix.identity(d), lambda));
LUDecomposition lu = new LUDecomposition(XtX_reg);
Vector w = lu.solve(MatrixOperations.multiply(Xt, yVector));
```

### Testing

```java
@Test
public void testPCA() {
    Matrix data = Matrix.random(100, 10);
    SVD.SVDResult svd = SVD.compute(data);
    
    // Verify singular values are sorted
    for (int i = 1; i < 10; i++) {
        assertTrue(svd.S.get(i-1,i-1) >= svd.S.get(i,i));
    }
    
    // Verify reconstruction
    Matrix reconstructed = SVD.reconstruct(svd);
    double error = MatrixOperations.subtract(data, reconstructed).frobeniusNorm();
    assertTrue(error < 1e-10);
}
```

### Deployment Considerations

1. Serialization: Save trained components
2. Streaming: Process images one at a time
3. Batch processing: Use matrix operations for efficiency
4. Memory: Use sparse matrices for large datasets

### Metrics

- Training time: O(n d^2) for PCA
- Prediction time: O(d) per image
- Storage: O(d * k) for k components