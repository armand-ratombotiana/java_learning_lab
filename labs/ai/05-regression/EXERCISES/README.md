# Regression - Exercises

## Exercise 1: Linear Regression from Scratch
```python
def linear_regression(X, y):
    X_b = np.c_[np.ones(len(X)), X]  # Add bias
    theta = np.linalg.lstsq(X_b, y, rcond=None)[0]
    return theta
```

## Exercise 2: Ridge with CV
```python
from sklearn.model_selection import KFold

def ridge_cv(X, y, alphas, k=5):
    kf = KFold(n_splits=k)
    best_alpha, best_score = None, -np.inf

    for alpha in alphas:
        scores = []
        for train_idx, val_idx in kf.split(X):
            ridge = Ridge(alpha=alpha)
            ridge.fit(X[train_idx], y[train_idx])
            scores.append(ridge.score(X[val_idx], y[val_idx]))
        if np.mean(scores) > best_score:
            best_score = np.mean(scores)
            best_alpha = alpha

    return best_alpha
```

## Exercise 3: Residual Analysis
```python
import scipy.stats as stats

def residual_analysis(y_true, y_pred):
    residuals = y_true - y_pred

    # Normality test
    stat, p = stats.shapiro(residuals)

    # Heteroscedasticity
    plt.scatter(y_pred, residuals)
    plt.axhline(y=0)
    plt.show()

    return p
```