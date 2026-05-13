# Calculus - Code Deep Dive

```python
import numpy as np
from scipy import optimize
from sympy import symbols, diff, integrate, exp, log, sin, cos

# === NUMERICAL DERIVATIVES ===

def numerical_derivative(f, x, h=1e-7):
    """Forward difference"""
    return (f(x + h) - f(x)) / h

def central_derivative(f, x, h=1e-7):
    """Central difference (more accurate)"""
    return (f(x + h) - f(x - h)) / (2 * h)

# For multidimensional
def gradient_numerical(f, x):
    """Numerical gradient using central differences"""
    grad = np.zeros_like(x)
    for i in range(len(x)):
        x_plus = x.copy()
        x_minus = x.copy()
        x_plus[i] += 1e-7
        x_minus[i] -= 1e-7
        grad[i] = (f(x_plus) - f(x_minus)) / (2 * 1e-7)
    return grad

# === SYMBOLIC DERIVATIVES (SymPy) ===

x, y = symbols('x y')

# Simple derivative
f = x**3 + 2*x**2 - 5*x + 1
df = diff(f, x)  # 3*x**2 + 4*x - 5
d2f = diff(f, x, 2)  # 6*x + 4

# Partial derivatives
f = x**2*y + exp(y)*sin(x)
df_dx = diff(f, x)
df_dy = diff(f, y)

# Gradient vector: (∂f/∂x, ∂f/∂y)
# Hessian matrix
from sympy import hessian
f = x**3 + y**3 - 3*x*y
H = hessian(f, (x, y))

# === INTEGRATION ===

# Symbolic integration
F = integrate(x**2, x)  # x**3/3
F = integrate(exp(-x), (x, 0, np.inf))  # 1

# Numerical integration
from scipy import integrate

result, error = integrate.quad(lambda x: x**2, 0, 1)  # 1/3
result, error = integrate.dblquad(lambda x, y: x*y, 0, 1, 0, 2)  # Double integral

# === OPTIMIZATION ===

# Unconstrained
def f(x):
    return x**4 - 3*x**3 + 2

result = optimize.minimize_scalar(f, bounds=(-10, 10), method='bounded')
result = optimize.minimize(f, x0=0, method='BFGS')
result = optimize.minimize(f, x0=0, method='Newton-CG', jac=lambda x: 4*x**3 - 9*x**2)

# Multidimensional
def rosen(x):
    """Rosenbrock function"""
    return sum(100*(x[1:]-x[:-1]**2)**2 + (1-x[:-1])**2)

result = optimize.minimize(rosen, x0=[0, 0], method='L-BFGS-B')

# === TAYLOR SERIES ===

def taylor_series(f, x0, n):
    """Compute Taylor series coefficients"""
    x = symbols('x')
    f_sym = f if isinstance(f, sympy.core.add.Add) else f(x)
    series = f_sym.series(x, x0, n).removeO()
    return series

# === JACOBIAN AND HESSIAN ===

from scipy.optimize import approx_fprime

def f(x):
    return [x[0]**2 + x[1]**2, x[0]*x[1]]

x0 = [1.0, 1.0]
jac = optimize.approx_fprime(x0, lambda x: np.array(f(x)), 1e-7)
# For true Jacobian, use scipy.optimize.check_grad

# === MULTIVARIATE CALCULUS ===

def f(x):
    return x[0]**2 + x[1]**2 + np.sin(x[0]*x[1])

x = np.array([1.0, 2.0])
grad = gradient_numerical(f, x)

# Hessian approximation
def hessian_numerical(f, x, h=1e-5):
    n = len(x)
    H = np.zeros((n, n))
    for i in range(n):
        for j in range(n):
            x_plus = x.copy()
            x_plus[i] += h
            x_plus[j] += h
            x_i = x.copy()
            x_i[i] += h
            x_j = x.copy()
            x_j[j] += h
            H[i,j] = (f(x_plus) - f(x_i) - f(x_j) + f(x)) / h**2
    return H
```