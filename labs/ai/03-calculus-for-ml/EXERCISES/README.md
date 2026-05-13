# Calculus - Exercises

## Exercise 1: Derivatives
```python
# Compute derivatives numerically and compare with analytical

import numpy as np

def f(x):
    return x**3 - 2*x**2 + 5*x - 3

def analytical_derivative(x):
    return 3*x**2 - 4*x + 5

x = 2.0
h = 1e-7

numerical = (f(x + h) - f(x - h)) / (2 * h)
analytical = analytical_derivative(x)

print(f"Numerical: {numerical}, Analytical: {analytical}")
```

## Exercise 2: Gradient Descent
```python
# Implement gradient descent to find minimum

def f(x):
    return x[0]**2 + 5*x[1]**2

def grad_f(x):
    return np.array([2*x[0], 10*x[1]])

x = np.array([10.0, 10.0])
lr = 0.1

for i in range(100):
    x = x - lr * grad_f(x)
    if i % 20 == 0:
        print(f"Iteration {i}: x = {x}, f(x) = {f(x)}")
```

## Exercise 3: Chain Rule
```python
# Chain rule: dz/dx = dz/dy * dy/dx
# If z = f(y) and y = g(x)

def f(y):
    return np.exp(-y**2)

def g(x):
    return x**2 + 1

def chain_derivative(x):
    # dz/dy = -2y * exp(-y^2)
    # dy/dx = 2x
    y = g(x)
    return -2*y * np.exp(-y**2) * 2*x
```

## Exercise 4: Multivariable Integration
```python
# Double integral: ∫∫ xy dx dy over [0,1]x[0,1]
# = ∫₀¹ [∫₀¹ xy dx] dy = ∫₀¹ y/2 dy = 1/4

from scipy import integrate

result, error = integrate.dblquad(
    lambda y, x: x*y, 0, 1, 0, 1
)
print(f"Result: {result}, Expected: 0.25")
```