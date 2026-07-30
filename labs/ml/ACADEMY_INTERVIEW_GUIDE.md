# ML Academy Interview Guide

## Core ML Concepts
- **Supervised vs Unsupervised Learning**: Labeled data vs unlabeled data.
- **Overfitting vs Underfitting**: Model captures noise vs model misses patterns.
- **Bias-Variance Tradeoff**: High bias = underfitting, high variance = overfitting.
- **Cross-Validation**: K-fold, stratified, leave-one-out.
- **Regularization**: L1 (Lasso), L2 (Ridge), ElasticNet.

## Algorithm Comparison
| Algorithm | Type | Use Case |
|---|---|---|
| Linear Regression | Regression | Continuous target |
| Logistic Regression | Classification | Binary outcome |
| Decision Tree | Both | Interpretable rules |
| Random Forest | Both | Robust ensemble |
| SVM | Both | High-dimensional |
| KNN | Both | Small datasets |
| Naive Bayes | Classification | Text/spam |
| K-Means | Clustering | Unlabeled data |
| PCA | Dimensionality Reduction | Feature compression |
| Gradient Boosting | Both | High performance |

## Evaluation Metrics
- Regression: MSE, MAE, RMSE, R², Adjusted R²
- Classification: Accuracy, Precision, Recall, F1, ROC-AUC, Log Loss
- Clustering: Inertia, Silhouette, Davies-Bouldin

## Common Interview Questions
1. Explain gradient descent and its variants (SGD, Adam, RMSprop).
2. What is the kernel trick? When would you use RBF vs linear kernel?
3. How does a random forest reduce variance compared to a single tree?
4. Explain the difference between bagging and boosting.
5. When would you use PCA? What are the trade-offs?
6. How do you handle imbalanced datasets? (SMOTE, class weights, resampling)
7. What is the bias-variance tradeoff? Give examples.
8. Explain how cross-entropy loss works for classification.
9. What is the curse of dimensionality? How do you mitigate it?
10. Describe the process of k-fold cross-validation and its benefits.

## Formulas to Know
- MSE: (1/n) Σ(y − ŷ)²
- Cross-Entropy: −(1/n) Σ [y log(ŷ) + (1−y) log(1−ŷ)]
- Sigmoid: σ(z) = 1 / (1 + e⁻ᶻ)
- Softmax: σ(z)ᵢ = eᶻⁱ / Σ eᶻʲ
- R²: 1 − SS_res / SS_tot
- F1: 2 · P · R / (P + R)
