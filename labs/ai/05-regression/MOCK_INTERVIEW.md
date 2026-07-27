# Mock Interview: Regression

## Question 1: Linear Regression Implementation
**Q**: Implement linear regression using gradient descent from scratch.

**A**:
```python
class LinearRegression:
    def __init__(self, lr=0.01, n_iter=1000):
        self.lr = lr
        self.n_iter = n_iter
        self.w = None
        self.b = 0

    def fit(self, X, y):
        n, d = X.shape
        self.w = np.zeros(d)
        for _ in range(self.n_iter):
            y_pred = X @ self.w + self.b
            dw = (X.T @ (y_pred - y)) / n
            db = np.mean(y_pred - y)
            self.w -= self.lr * dw
            self.b -= self.lr * db
```

**Follow-up**: How would you add L2 regularization?
dw += (lambda / n) * self.w  # Ridge regularization

## Question 2: Closed-Form vs GD
**Q**: Compare normal equation vs gradient descent for linear regression.

**A**: Normal equation: w = (X^T X)^{-1} X^T y
- O(d^3) for inversion, O(nd^2) total
- Exact solution (convex problem)
- Problems when X^T X is singular
- Expensive for large d (>10K)

Gradient descent:
- O(nd) per iteration
- Iterative, approximate
- Works for large d
- Requires tuning learning rate

## Question 3: Model Evaluation
**Q**: Explain R-squared, adjusted R-squared, and when to use each.

**A**: R^2 = 1 - SS_res / SS_tot
- Measures proportion of variance explained by model
- Always increases with more features (even useless ones)

Adjusted R^2 = 1 - (1-R^2)(n-1)/(n-d-1)
- Penalizes adding useless features
- Better for model selection

## Question 4: Assumptions of Linear Regression
**Q**: What are the key assumptions of linear regression? What happens when they're violated?

**A**: 
- **Linearity**: Relationship must be linear (violated: try polynomial features)
- **Independence**: Observations independent (violated: use time series methods)
- **Homoscedasticity**: Constant variance of errors (violated: use weighted least squares)
- **Normality**: Errors normally distributed (violated: use robust std errors, still unbiased)
- **No multicollinearity**: Features not highly correlated (violated: ridge regression, PCA)

## Question 5: Polynomial Regression
**Q**: How would you implement polynomial regression? What are the risks?

**A**: Add polynomial features: for degree d, create features [x^1, x^2, ..., x^d].
Then use standard linear regression.

Risks:
- Overfitting with high degree
- Extrapolation (polynomials diverge rapidly)
- Feature scaling critical (values explode)
- Use cross-validation to select degree

```python
def polynomial_features(X, degree):
    from sklearn.preprocessing import PolynomialFeatures
    return PolynomialFeatures(degree).fit_transform(X)
```
