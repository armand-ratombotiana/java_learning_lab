# Mock Interview: Linear Regression

**Topic:** Derive and implement linear regression with OLS and gradient descent

## Core Questions

### Q1: Derive the OLS estimator for linear regression.

**Answer:**
We have $y = Xw + \epsilon$ where $y \in \mathbb{R}^n$, $X \in \mathbb{R}^{n \times d}$, $w \in \mathbb{R}^d$.

Minimize MSE: $L(w) = \frac{1}{n} \|y - Xw\|^2_2$

Expand: $L(w) = \frac{1}{n}(y^T y - 2w^T X^T y + w^T X^T X w)$

Gradient: $\nabla_w L = \frac{2}{n}(-X^T y + X^T X w)$

Set to zero: $X^T X \hat{w} = X^T y$

Normal equations: $\hat{w} = (X^T X)^{-1} X^T y$

**Follow-up:** When is $(X^T X)$ invertible? What happens with collinearity?

### Q2: Compare OLS closed-form vs. gradient descent.

| Aspect | OLS (Normal Eq.) | Gradient Descent |
|--------|-----------------|-----------------|
| Complexity | $O(d^3 + d^2 n)$ | $O(nd)$ per epoch |
| Large $d$ | Infeasible | Scalable |
| Large $n$ | Memory heavy | Mini-batch works |
| Feature scaling | Not needed | Required |
| Regularization | Trivial add $\lambda I$ | Easy to add |

### Q3: Implement linear regression with gradient descent from scratch.

```python
class LinearRegressionGD:
    def __init__(self, lr=0.01, epochs=1000):
        self.lr = lr
        self.epochs = epochs
        self.w = None
        self.b = None

    def fit(self, X, y):
        n, d = X.shape
        self.w = np.zeros(d)
        self.b = 0.0
        for _ in range(self.epochs):
            y_pred = X @ self.w + self.b
            dw = (2/n) * X.T @ (y_pred - y)
            db = (2/n) * np.sum(y_pred - y)
            self.w -= self.lr * dw
            self.b -= self.lr * db

    def predict(self, X):
        return X @ self.w + self.b
```

## Advanced

- **Ridge regression:** $L(w) = \|y - Xw\|^2 + \lambda\|w\|^2$, solution: $\hat{w} = (X^T X + \lambda I)^{-1} X^T y$
- **Assumptions:** Linearity, independence, homoscedasticity, normality of errors
- **R² score:** $1 - \frac{SS_{res}}{SS_{tot}}$, interpretation as variance explained
- **MLE equivalence:** OLS is MLE under Gaussian noise assumption
