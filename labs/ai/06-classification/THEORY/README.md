# Classification - Theory

## 1. Logistic Regression

### Binary Classification
```
P(y=1|x) = σ(w₀ + w₁x₁ + ... + wₙxₙ)
σ(z) = 1 / (1 + e^(-z))
```

### Multiclass: One-vs-All & One-vs-One

### Loss: Cross-Entropy
```
L = -[y log(ŷ) + (1-y) log(1-ŷ)]
```

## 2. Support Vector Machines (SVM)

### Maximum Margin Classifier
- Find hyperplane with maximum margin
- Margin = distance to nearest points (support vectors)

### Soft Margin (with regularization)
```
minimize ||w||² + C * Σξᵢ
```

### Kernels
- **Linear**: K(x, z) = x·z
- **Polynomial**: K(x, z) = (γx·z + r)^d
- **RBF/Gaussian**: K(x, z) = exp(-γ||x-z||²)

## 3. Decision Trees

### Splitting Criteria
- **Gini Impurity**: 1 - Σp²
- **Entropy**: -Σp log₂(p)
- **Information Gain**: Entropy(parent) - Weighted Entropy(children)

### Pruning
- Pre-pruning: max_depth, min_samples_split
- Post-pruning: cost-complexity pruning

## 4. Random Forest

### Ensemble of Decision Trees
- Bootstrap sampling (row sampling)
- Feature sampling (column sampling)
- Voting/averaging

### Properties
- Reduces variance
- Handles missing values
- Feature importance

## 5. Gradient Boosting

### Additive Model
```
F(x) = Σₘ γₘhₘ(x)
```

### XGBoost/LightGBM
- Regularized objective
- Efficient computation
- Sparse handling

## 6. Evaluation Metrics

### Confusion Matrix
- TP, TN, FP, FN
- Accuracy = (TP+TN)/(TP+TN+FP+FN)
- Precision = TP/(TP+FP)
- Recall = TP/(TP+FN)
- F1 = 2 * Precision * Recall / (Precision + Recall)

### ROC Curve & AUC
### Precision-Recall Curve