# Recommendation Systems - Complete Theory

## 1. Collaborative Filtering

### 1.1 User-Item Matrix
R_{m x n}: m users, n items
Sparse matrix of ratings

### 1.2 User-based CF
Find similar users, predict from their ratings

### 1.3 Item-based CF
Find similar items, predict from ratings of similar items

### 1.4 Similarity Measures
- Cosine: sim(u, v) = (u dot v) / (||u|| ||v||)
- Pearson: centered cosine
- Jaccard: for binary

## 2. Matrix Factorization

### 2.1 SVD-based
R = U Sigma V^T
Predict: r_ui = U_u^T V_i

### 2.2 ALS (Alternating Least Squares)
Minimize: sum (r_ui - p_u^T q_i)^2 + lambda(||P||^2 + ||Q||^2)

### 2.3 SGD
Randomly update factors

## 3. Content-Based

### 3.1 Feature representation
Item profiles: content features

### 3.2 User profiles
Weighted average of item features

### 3.3 Matching
Recommend items similar to user's profile

## 4. Hybrid Methods

### 4.1 Combined
Collaborative + Content features

### 4.2 Feature Augmentation
Add content features to collaborative model

## 5. Evaluation

### 5.1 Metrics
- RMSE / MAE
- Precision@K, Recall@K
- NDCG
- MAP

### 5.2 Offline Testing
- Train/test split by time
- Leave-one-out

## Java Implementation

```java
public class MatrixFactorization {
    private int k;  // latent factors
    private double lr;  // learning rate
    private double reg;  // regularization
    
    public void fit(Matrix R, int epochs) {
        int m = R.rows();
        int n = R.cols();
        
        Matrix P = Matrix.random(m, k);
        Matrix Q = Matrix.random(n, k);
        
        for (int epoch = 0; epoch < epochs; epoch++) {
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    if (Math.abs(R.get(i, j)) < 1e-10) continue;
                    
                    double pred = predict(P, Q, i, j);
                    double err = R.get(i, j) - pred;
                    
                    for (int f = 0; f < k; f++) {
                        double pif = P.get(i, f);
                        double qjf = Q.get(j, f);
                        
                        P.data[i][f] += lr * (err * qjf - reg * pif);
                        Q.data[j][f] += lr * (err * pif - reg * qjf);
                    }
                }
            }
        }
    }
    
    public double predict(int user, int item) {
        double sum = 0;
        for (int f = 0; f < k; f++) {
            sum += P.get(user, f) * Q.get(item, f);
        }
        return sum;
    }
}
```