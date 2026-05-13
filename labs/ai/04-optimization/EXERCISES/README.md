# Optimization - Exercises

## Exercise 1: Implement SGD
```python
def sgd(X, y, lr=0.01, epochs=100):
    n, d = X.shape
    w = np.zeros(d)
    for epoch in range(epochs):
        for i in range(n):
            pred = X[i] @ w
            error = y[i] - pred
            w += lr * error * X[i]
    return w
```

## Exercise 2: Compare Optimizers
```python
# Compare GD, Momentum, Adam on Rosenbrock function

def rosenbrock(x):
    return sum(100*(x[1:]-x[:-1]**2)**2 + (1-x[:-1])**2)
```

## Exercise 3: Ridge from Scratch
```python
def ridge_regression(X, y, alpha):
    I = np.eye(X.shape[1])
    return np.linalg.solve(X.T @ X + alpha * I, X.T @ y)
```

## Exercise 4: Gradient Clipping
```python
def clip_grad(grad, max_norm):
    norm = np.linalg.norm(grad)
    if norm > max_norm:
        grad = grad * max_norm / norm
    return grad
```