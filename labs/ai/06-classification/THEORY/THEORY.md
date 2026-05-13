# Classification - Complete Theory

## 1. Logistic Regression

### 1.1 Sigmoid Function
sigma(z) = 1/(1 + e^{-z})

### 1.2 Binary Classification
P(y=1|x) = sigma(w^T x + b)

### 1.3 Cross-Entropy Loss
L = -[y log(p) + (1-y) log(1-p)]

### 1.4 Gradient
dL/dw = (p - y) x

## 2. Softmax Regression
P(y=k|x) = e^{z_k}/sum(e^{z_j})
Generalization of logistic to K classes

## 3. Support Vector Machines

### 3.1 Maximum Margin Classifier
w^T x + b = 0 defines decision boundary
Margin = 2/||w||

### 3.2 Soft Margin (C parameter)
Minimize: ||w||^2/2 + C sum(xi)
Subject to: y(w^T x + b) >= 1 - xi, xi >= 0

### 3.3 Kernel SVM
Map to high-dimensional space
K(x, z) = phi(x)^T phi(z)

## 4. Decision Trees

### 4.1 Information Gain
IG = H(parent) - sum(w_j) H(child_j)

### 4.2 Gini Impurity
Gini = 1 - sum(p_k^2)

### 4.3 Entropy
H = -sum(p_k log(p_k))

## 5. Ensemble Methods

### 5.1 Random Forest
- Bootstrap sampling
- Feature randomness
- Vote for classification

### 5.2 Gradient Boosting
Sequential learners
Focus on residuals

## Java Implementation

```java
public class LogisticRegression {
    public void fit(Matrix X, Vector y, double lr, int epochs) {
        int n = X.rows();
        this.w = VectorOperations.zeros(X.cols());
        this.b = 0;
        
        for (int epoch = 0; epoch < epochs; epoch++) {
            for (int i = 0; i < n; i++) {
                double z = VectorOperations.dot(X.getRow(i), w) + b;
                double pred = sigmoid(z);
                double err = y.get(i) - pred;
                
                for (int j = 0; j < w.size(); j++) {
                    w.data[j] += lr * err * X.get(i, j);
                }
                b += lr * err;
            }
        }
    }
    
    public int predict(Vector x) {
        double z = VectorOperations.dot(x, w) + b;
        return z > 0 ? 1 : 0;
    }
    
    private double sigmoid(double z) {
        return 1.0 / (1.0 + Math.exp(-z));
    }
}
```