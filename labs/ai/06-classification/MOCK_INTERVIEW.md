# Mock Interview: Classification

## Question 1: Logistic Regression
**Q**: Implement binary logistic regression from scratch. Derive the gradient.

**A**:
```python
class LogisticRegression:
    def __init__(self, lr=0.01, n_iter=1000):
        self.lr = lr
        self.n_iter = n_iter
        self.w = None
        self.b = 0

    def sigmoid(self, z):
        return 1 / (1 + np.exp(-np.clip(z, -500, 500)))

    def fit(self, X, y):
        n, d = X.shape
        self.w = np.zeros(d)
        for _ in range(self.n_iter):
            z = X @ self.w + self.b
            p = self.sigmoid(z)
            dw = (X.T @ (p - y)) / n
            db = np.mean(p - y)
            self.w -= self.lr * dw
            self.b -= self.lr * db
```

Gradient derivation: L = -y*log(p) - (1-y)*log(1-p), dL/dw = (p-y)*x

## Question 2: Metrics for Imbalanced Data
**Q**: You have 99% negative, 1% positive class. Why is accuracy a bad metric? What should you use?

**A**: Accuracy = (TP+TN)/(TP+TN+FP+FN). Always predicting "negative" gives 99% accuracy.

Better metrics:
- **Precision**: TP/(TP+FP) — "Are positive predictions reliable?"
- **Recall**: TP/(TP+FN) — "Are we catching all positives?"
- **F1**: Harmonic mean of precision and recall
- **PR-AUC**: Area under precision-recall curve (better than ROC for imbalanced)
- **Matthews Correlation Coefficient**: Balanced measure for all classes

## Question 3: Decision Boundary & Non-Linearity
**Q**: How would you handle non-linear decision boundaries?

**A**: Options:
1. **Polynomial features**: Add x^2, x1*x2, etc. (still linear in parameters)
2. **Kernel methods**: RBF kernel SVM
3. **Decision trees / Random Forest**: Naturally non-linear
4. **Neural networks**: Non-linear activation functions

## Question 4: Multi-class Classification
**Q**: Compare one-vs-rest (OvR) vs softmax for multi-class classification.

**A**: 
- **OvR**: Train K binary classifiers. Each predicts one vs all. Pick highest score.
  Pros: Compatible with any binary classifier. Cons: Scale issues, decision boundary doesn't sum to 1.
- **Softmax**: Single model with K outputs, softmax gives probability distribution.
  Pros: Probabilistic, end-to-end trained. Cons: Only works with neural networks / linear models.

## Question 5: Classification vs Regression
**Q**: When would you use logistic regression vs linear regression to predict a binary outcome?

**A**: Always use logistic regression for binary outcomes.
Linear regression can predict probabilities <0 or >1. Logistic regression constrains to [0,1] via sigmoid.

However, linear regression can work as approximation for well-separated classes with balanced data. Not recommended.
