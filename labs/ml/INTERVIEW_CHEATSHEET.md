# Interview Cheatsheet

## ML Metrics
| Metric | Formula | Use |
|---|---|---|
| MSE | (1/n)Σ(y − ŷ)² | Regression |
| MAE | (1/n)Σ|y − ŷ| | Regression (robust) |
| R² | 1 − SS_res/SS_tot | Regression fit quality |
| Accuracy | (TP+TN) / (P+N) | Balanced classification |
| Precision | TP / (TP+FP) | Positive predictions quality |
| Recall | TP / (TP+FN) | Coverage of positives |
| F1 | 2·P·R / (P+R) | Harmonic mean P & R |
| AUC | Area under ROC | Ranking quality |
| Log Loss | −Σ[y·log(ŷ) + (1−y)·log(1−ŷ)] | Probabilistic classification |

## ML Formulas
- **Linear Regression**: ŷ = β₀ + β₁x₁ + ... + βₚxₚ
- **Logistic Regression**: P(y=1) = 1 / (1 + e^−(βᵀx))
- **SVM (Primal)**: min ½‖w‖² + C Σ max(0, 1 − yⁱ(w·xⁱ + b))
- **K-Means**: min Σᵢ Σₖ ‖xⁱ − μₖ‖²
- **PCA**: Σ = (1/n)XᵀX; Σ·v = λ·v
- **Naive Bayes**: P(y|x) ∝ P(y) Π P(xᵢ|y)
- **Gradient Boosting**: Fₘ(x) = Fₘ₋₁(x) + η·hₘ(x)

## Optimizers
| Optimizer | Update Rule |
|---|---|
| SGD | w ← w − η·∇J |
| Momentum | v ← γv + η∇J; w ← w − v |
| Adam | Adaptive learning rates + momentum |
| RMSprop | Divide by root-mean-square of gradients |

## Regularization
- **L1 (Lasso)**: + λ Σ|wⱼ| (sparsity)
- **L2 (Ridge)**: + λ Σ wⱼ² (shrinkage)
- **Dropout**: Randomly drop neurons during training
- **Batch Norm**: Normalize layer inputs
- **Early Stopping**: Stop when validation loss increases

## Time Complexities (ML)
| Algorithm | Training | Prediction |
|---|---|---|
| Linear/Logistic Regression | O(n·p) | O(p) |
| Decision Tree | O(n·p·log n) | O(log n) |
| Random Forest | O(T·n·p·log n) | O(T·log n) |
| SVM (linear) | O(n·p) | O(p) |
| SVM (kernel) | O(n²·p) to O(n³) | O(n·p) |
| KNN | O(1) | O(n·p) |
| K-Means | O(n·K·d·iter) | O(K·d) |
| PCA | O(n·p² + p³) | O(p·k) |
| Gradient Boosting | O(T·n·p·log n) | O(T·log n) |

## Activation Functions
| Function | Range | Derivative |
|---|---|---|
| Sigmoid | (0, 1) | σ(z)·(1−σ(z)) |
| Tanh | (−1, 1) | 1 − tanh²(z) |
| ReLU | [0, ∞) | 0 if z<0 else 1 |
| Leaky ReLU | (−∞, ∞) | 0.01 if z<0 else 1 |
| Softmax | (0, 1)ᵏ | σᵢ(δᵢⱼ − σⱼ) |

## Evaluation Mind Map
```
Model Evaluation
├── Regression
│   ├── MSE, MAE, RMSE
│   ├── R², Adjusted R²
│   └── Residual Analysis
├── Classification
│   ├── Confusion Matrix
│   ├── Accuracy, Precision, Recall, F1
│   ├── ROC, AUC
│   └── Log Loss
├── Clustering
│   ├── Inertia (WCSS)
│   ├── Silhouette Score
│   └── Davies-Bouldin Index
└── Validation
    ├── Train/Test Split
    ├── K-Fold CV
    ├── Stratified CV
    └── Leave-One-Out CV
```
