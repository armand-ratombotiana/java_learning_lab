# Optimization - Code Deep Dive

```python
import numpy as np
from sklearn.linear_model import SGDRegressor, Ridge, Lasso, ElasticNet

# === GRADIENT DESCENT ===

def gradient_descent(f, grad, x0, learning_rate=0.01, max_iter=1000, tol=1e-6):
    """Basic gradient descent"""
    x = x0.copy()
    for i in range(max_iter):
        g = grad(x)
        x = x - learning_rate * g
        if np.linalg.norm(g) < tol:
            break
    return x, i

# === STOCHASTIC GRADIENT DESCENT ===

def sgd(X, y, learning_rate=0.01, epochs=100, batch_size=32):
    """SGD for linear regression"""
    n_samples, n_features = X.shape
    weights = np.zeros(n_features)
    bias = 0

    for epoch in range(epochs):
        indices = np.random.permutation(n_samples)
        X_shuf = X[indices]
        y_shuf = y[indices]

        for i in range(0, n_samples, batch_size):
            X_batch = X_shuf[i:i+batch_size]
            y_batch = y_shuf[i:i+batch_size]

            predictions = X_batch @ weights + bias
            errors = predictions - y_batch
            weight_grad = (1/batch_size) * X_batch.T @ errors
            bias_grad = np.mean(errors)

            weights -= learning_rate * weight_grad
            bias -= learning_rate * bias_grad

    return weights, bias

# === MOMENTUM ===

def sgd_momentum(X, y, learning_rate=0.01, momentum=0.9, epochs=100):
    """SGD with momentum"""
    n_samples, n_features = X.shape
    weights = np.zeros(n_features)
    velocity = np.zeros(n_features)

    for epoch in range(epochs):
        for i in range(n_samples):
            pred = X[i] @ weights
            error = pred - y[i]
            grad = error * X[i]

            velocity = momentum * velocity + learning_rate * grad
            weights -= velocity

    return weights

# === ADAM OPTIMIZER ===

class Adam:
    def __init__(self, lr=0.001, beta1=0.9, beta2=0.999, epsilon=1e-8):
        self.lr = lr
        self.beta1 = beta1
        self.beta2 = beta2
        self.epsilon = epsilon
        self.m = None
        self.v = None
        self.t = 0

    def step(self, params, grads):
        if self.m is None:
            self.m = [np.zeros_like(p) for p in params]
            self.v = [np.zeros_like(p) for p in params]

        self.t += 1
        updated_params = []

        for i, (p, g) in enumerate(zip(params, grads)):
            self.m[i] = self.beta1 * self.m[i] + (1 - self.beta1) * g
            self.v[i] = self.beta2 * self.v[i] + (1 - self.beta2) * g**2

            m_hat = self.m[i] / (1 - self.beta1**self.t)
            v_hat = self.v[i] / (1 - self.beta2**self.t)

            p_new = p - self.lr * m_hat / (np.sqrt(v_hat) + self.epsilon)
            updated_params.append(p_new)

        return updated_params

# === REGULARIZATION ===

# Ridge (L2)
ridge = Ridge(alpha=1.0)
ridge.fit(X_train, y_train)

# Lasso (L1)
lasso = Lasso(alpha=1.0)
lasso.fit(X_train, y_train)

# Elastic Net
elastic = ElasticNet(alpha=1.0, l1_ratio=0.5)
elastic.fit(X_train, y_train)

# Using SGD
sgd_ridge = SGDRegressor(penalty='l2', alpha=0.01)
sgd_lasso = SGDRegressor(penalty='l1', alpha=0.01)
sgd_elastic = SGDRegressor(penalty='elasticnet', alpha=0.01, l1_ratio=0.5)

# === CONSTRAINED OPTIMIZATION ===

from scipy.optimize import minimize

def objective(x):
    return x[0]**2 + x[1]**2

def constraint(x):
    return x[0] + x[1] - 1

cons = {'type': 'eq', 'fun': constraint}
result = minimize(objective, x0=[0, 0], method='SLSQP', constraints=cons)

# Inequality constraint
def constraint_ineq(x):
    return x[0]**2 + x[1]**2 - 1

cons_ineq = {'type': 'ineq', 'fun': constraint_ineq}
result = minimize(objective, x0=[0, 0], method='SLSQP', constraints=cons_ineq)

# === GRADIENT CLIPPING ===

def clip_gradients(grads, max_norm):
    """Clip gradients by global norm"""
    total_norm = np.sqrt(sum(np.sum(g**2) for g in grads))
    clip_coef = max_norm / (total_norm + 1e-8)
    if clip_coef < 1:
        return [g * clip_coef for g in grads]
    return grads
```