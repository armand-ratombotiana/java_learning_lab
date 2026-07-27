# Mock Interview: Calculus for ML

## Question 1: Gradient & Optimization
**Q**: Explain gradient descent. Derive the gradient for logistic regression loss.

**A**: Gradient descent iteratively moves parameters in direction of steepest descent.
w_{t+1} = w_t - lr * dL/dw

For logistic regression with binary cross-entropy loss:
L = -y * log(sigma(w*x)) - (1-y) * log(1 - sigma(w*x))
dL/dw = (sigma(w*x) - y) * x

```python
def logistic_gradient(X, y, w):
    logits = X @ w
    probs = 1 / (1 + np.exp(-logits))
    return X.T @ (probs - y) / len(y)
```

## Question 2: Chain Rule & Backpropagation
**Q**: Apply the chain rule to compute dL/dw for a 2-layer neural network.

**A**: Neural net: L = loss(f(f(x * W1 + b1) * W2 + b2), y)
Chain rule: dL/dW2 = dL/dz2 * da1 * dL/da1 * dz2/dW2
= (a2 - y) * a1
dL/dW1 = dL/dz2 * da2/dz2 * dL/da2 * dz1/dW1
= W2^T * (y - a2) * f'(z1) * x

## Question 3: Multivariable Calculus
**Q**: Compute the gradient of f(x, y) = x^2 * y + sin(xy). What's the Hessian?

**A**: Gradient:
df/dx = 2xy + y*cos(xy)
df/dy = x^2 + x*cos(xy)

Hessian (2x2 matrix of second derivatives):
d^2f/dx^2 = 2y - y^2*sin(xy)
d^2f/dxdy = 2x + cos(xy) - xy*sin(xy)
d^2f/dydx = 2x + cos(xy) - xy*sin(xy)
d^2f/dy^2 = -x^2*sin(xy)

## Question 4: Optimization & Convexity
**Q**: What makes a function convex? Why does convexity matter in ML?

**A**: A function f is convex if f(tx + (1-t)y) <= t*f(x) + (1-t)*f(y) for all x,y and t in [0,1].

For convex functions:
- Any local minimum is global minimum
- Gradient descent guarantees convergence
- Hessian is positive semi-definite

Linear regression MSE and logistic regression log-loss are convex. Neural networks are non-convex.

## Question 5: Constrained Optimization
**Q**: Use Lagrange multipliers to derive the ridge regression solution.

**A**: Minimize ||Xw - y||^2 subject to ||w||^2 <= t.
Lagrangian: L(w, lambda) = ||Xw - y||^2 + lambda * (||w||^2 - t)
dL/dw = 2X^T(Xw - y) + 2*lambda*w = 0
(X^T X + lambda*I)*w = X^T*y
w = (X^T X + lambda*I)^{-1} * X^T * y

This is the closed-form ridge regression solution.
