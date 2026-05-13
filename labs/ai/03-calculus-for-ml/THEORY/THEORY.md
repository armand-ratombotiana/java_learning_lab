# Calculus for Machine Learning - Complete Theory

## Table of Contents
1. [Limits and Continuity](#1-limits-and-continuity)
2. [Derivatives](#2-derivatives)
3. [Partial Derivatives](#3-partial-derivatives)
4. [Gradients](#4-gradients)
5. [Jacobians and Hessians](#5-jacobians-and-hessians)
6. [Taylor Series](#6-taylor-series)
7. [Integration](#7-integration)
8. [Optimization](#8-optimization)
9. [Calculus in ML](#9-calculus-in-ml)
10. [Advanced Topics](#10-advanced-topics)

---

## 1. Limits and Continuity

### 1.1 Limit Definition

$$\lim_{x \to a} f(x) = L$$

Means: For every ε > 0, there exists δ > 0 such that |x - a| < δ implies |f(x) - L| < ε.

### 1.2 One-sided Limits

- Left limit: $\lim_{x \to a^-} f(x) = L$
- Right limit: $\lim_{x \to a^+} f(x) = L$

### 1.3 Continuity

f is continuous at a if:
1. f(a) is defined
2. $\lim_{x \to a} f(x)$ exists
3. $\lim_{x \to a} f(x) = f(a)$

### 1.4 Important Limits

- $\lim_{x \to 0} \frac{\sin x}{x} = 1$
- $\lim_{x \to 0} \frac{e^x - 1}{x} = 1$
- $\lim_{x \to 0} \frac{1 - \cos x}{x^2} = \frac{1}{2}$
- $\lim_{n \to \infty} (1 + \frac{1}{n})^n = e$

---

## 2. Derivatives

### 2.1 Definition

$$f'(x) = \frac{df}{dx} = \lim_{h \to 0} \frac{f(x+h) - f(x)}{h}$$

**Interpretation**: Instantaneous rate of change, slope of tangent line.

### 2.2 Basic Rules

1. **Constant**: $\frac{d}{dx}[c] = 0$

2. **Power**: $\frac{d}{dx}[x^n] = nx^{n-1}$

3. **Scalar multiplication**: $\frac{d}{dx}[cf(x)] = cf'(x)$

4. **Sum**: $\frac{d}{dx}[f + g] = f' + g'$

5. **Product**: $\frac{d}{dx}[fg] = f'g + fg'$

6. **Quotient**: $\frac{d}{dx}[\frac{f}{g}] = \frac{f'g - fg'}{g^2}$

7. **Chain**: $\frac{d}{dx}[f(g(x))] = f'(g(x)) \cdot g'(x)$

### 2.3 Common Derivatives

| Function | Derivative |
|----------|------------|
| c | 0 |
| x^n | nx^{n-1} |
| e^x | e^x |
| a^x | a^x ln(a) |
| ln(x) | 1/x |
| log_a(x) | 1/(x ln(a)) |
| sin(x) | cos(x) |
| cos(x) | -sin(x) |
| tan(x) | sec^2(x) |
| arcsin(x) | 1/√(1-x²) |
| arccos(x) | -1/√(1-x²) |
| arctan(x) | 1/(1+x²) |

### 2.4 Higher Order Derivatives

$$f^{(n)}(x) = \frac{d^n f}{dx^n}$$

### 2.5 Mean Value Theorem

If f is continuous on [a,b] and differentiable on (a,b), then exists c in (a,b) such that:
$$f'(c) = \frac{f(b) - f(a)}{b - a}$$

### 2.6 L'Hôpital's Rule

If $\lim_{x \to a} \frac{f(x)}{g(x)}$ is indeterminate form 0/0 or ∞/∞:
$$\lim_{x \to a} \frac{f(x)}{g(x)} = \lim_{x \to a} \frac{f'(x)}{g'(x)}$$

---

## 3. Partial Derivatives

### 3.1 Definition

For f: R^n → R, the partial derivative with respect to xᵢ:
$$\frac{\partial f}{\partial x_i} = \lim_{h \to 0} \frac{f(x_1, ..., x_i + h, ..., x_n) - f(x)}{h}$$

### 3.2 Interpretation

Treat all other variables as constants, differentiate with respect to one.

### 3.3 Notation

- Leibniz: ∂f/∂xᵢ
- Subscript: f_{xᵢ}
- Vector notation: D_i f

### 3.4 Higher Order Partials

- First order: f_{xᵢ}
- Second order: f_{xᵢxⱼ} = ∂²f/(∂xᵢ∂xⱼ)
- Mixed partials: f_{xy} = ∂²f/(∂x∂y)

**Clairaut's Theorem**: If partials are continuous, f_{xy} = f_{yx}

---

## 4. Gradients

### 4.1 Definition

The gradient of f: R^n → R is a vector:
$$\nabla f = \begin{pmatrix} \frac{\partial f}{\partial x_1} \\ \frac{\partial f}{\partial x_2} \\ \vdots \\ \frac{\partial f}{\partial x_n} \end{pmatrix}$$

### 4.2 Properties

1. **Direction of steepest ascent**: ∇f points in direction of greatest rate of increase.

2. **Normal to level sets**: ∇f is perpendicular to level curves/surfaces.

3. **Linear approximation**: f(x + Δx) ≈ f(x) + ∇f^T Δx

### 4.3 Gradient Rules

1. ∇(f + g) = ∇f + ∇g
2. ∇(cf) = c∇f
3. ∇(fg) = f∇g + g∇f
4. ∇(f/g) = (g∇f - f∇g)/g²

### 4.4 Directional Derivative

Rate of change of f in direction **u** (unit vector):
$$D_{\mathbf{u}}f = \nabla f \cdot \mathbf{u}$$

**Maximum directional derivative**: ||∇f|| in direction of ∇f.

---

## 5. Jacobians and Hessians

### 5.1 Jacobian Matrix

For F: R^n → R^m, the Jacobian is m×n matrix:
$$J_F = \begin{pmatrix} \frac{\partial f_1}{\partial x_1} & \frac{\partial f_1}{\partial x_2} & \cdots & \frac{\partial f_1}{\partial x_n} \\ \frac{\partial f_2}{\partial x_1} & \frac{\partial f_2}{\partial x_2} & \cdots & \frac{\partial f_2}{\partial x_n} \\ \vdots & \vdots & \ddots & \vdots \\ \frac{\partial f_m}{\partial x_1} & \frac{\partial f_m}{\partial x_2} & \cdots & \frac{\partial f_m}{\partial x_n} \end{pmatrix}$$

**Special cases**:
- m = 1: J = ∇f^T (row vector)
- n = 1: J = ∇f (column vector)

### 5.2 Hessian Matrix

For f: R^n → R, the Hessian is n×n matrix:
$$H = \nabla^2 f = \begin{pmatrix} \frac{\partial^2 f}{\partial x_1^2} & \frac{\partial^2 f}{\partial x_1 \partial x_2} & \cdots \\ \frac{\partial^2 f}{\partial x_2 \partial x_1} & \frac{\partial^2 f}{\partial x_2^2} & \cdots \\ \vdots & \vdots & \ddots \end{pmatrix}$$

**Properties**:
- Symmetric if second partials are continuous
- Contains all second-order information

### 5.3 Second Order Taylor Expansion

$$f(\mathbf{x} + \Delta\mathbf{x}) \approx f(\mathbf{x}) + \nabla f^T \Delta\mathbf{x} + \frac{1}{2} \Delta\mathbf{x}^T H \Delta\mathbf{x}$$

---

## 6. Taylor Series

### 6.1 Single Variable Taylor Series

$$f(x) = f(a) + f'(a)(x-a) + \frac{f''(a)}{2!}(x-a)^2 + \frac{f^{(3)}(a)}{3!}(x-a)^3 + ...$$

**Maclaurin series**: Taylor series around x = 0.

### 6.2 Common Taylor Series

$$e^x = 1 + x + \frac{x^2}{2!} + \frac{x^3}{3!} + ...$$

$$\sin(x) = x - \frac{x^3}{3!} + \frac{x^5}{5!} - ...$$

$$\cos(x) = 1 - \frac{x^2}{2!} + \frac{x^4}{4!} - ...$$

$$\ln(1+x) = x - \frac{x^2}{2} + \frac{x^3}{3} - ...$$

### 6.3 Multivariable Taylor Series

$$f(\mathbf{x}) = f(\mathbf{a}) + \nabla f(\mathbf{a})^T (\mathbf{x} - \mathbf{a}) + \frac{1}{2}(\mathbf{x} - \mathbf{a})^T H(\mathbf{a})(\mathbf{x} - \mathbf{a}) + ...$$

### 6.4 Convergence

Taylor series converges if:
- |x - a| < radius of convergence
- Function is infinitely differentiable
- Remainder term → 0

---

## 7. Integration

### 7.1 Definite Integral

$$\int_a^b f(x) dx = \lim_{n \to \infty} \sum_{i=1}^n f(x_i^*) \Delta x$$

**Interpretation**: Area under curve from a to b.

### 7.2 Fundamental Theorem of Calculus

If F' = f, then:
$$\int_a^b f(x) dx = F(b) - F(a)$$

### 7.3 Integration Techniques

1. **Substitution**: Let u = g(x), du = g'(x)dx

2. **Integration by parts**: ∫u dv = uv - ∫v du

3. **Partial fractions**: Decompose rational functions

4. **Trigonometric substitution**: For sqrt(a² - x²) etc.

### 7.4 Common Integrals

| Function | Integral |
|----------|----------|
| x^n | x^{n+1}/(n+1) + C |
| 1/x | ln|x| + C |
| e^x | e^x + C |
| sin(x) | -cos(x) + C |
| cos(x) | sin(x) + C |
| sec²(x) | tan(x) + C |

### 7.5 Multiple Integrals

**Double integral**:
$$\iint_R f(x,y) dA = \int_{y=a}^{b} \int_{x=g(y)}^{h(y)} f(x,y) dx dy$$

**Triple integral**: Similar extension to 3D.

### 7.6 Change of Variables

$$\iint_R f(x,y) dx dy = \iint_S f(g(u,v), h(u,v)) |J| du dv$$

where J is Jacobian determinant.

---

## 8. Optimization

### 8.1 Critical Points

Find where ∇f = **0**.

**First derivative test** (single variable):
- f' > 0 before, f' < 0 after: local maximum
- f' < 0 before, f' > 0 after: local minimum
- Same sign both sides: neither

### 8.2 Second Derivative Test

**Single variable**:
- f''(a) > 0: local minimum
- f''(a) < 0: local maximum
- f''(a) = 0: inconclusive

**Multivariable** (Hessian at critical point):
- H positive definite: local minimum
- H negative definite: local maximum
- H indefinite: saddle point

### 8.3 Positive Definiteness

Matrix H is positive definite if:
- All eigenvalues > 0
- All leading principal minors > 0
- x^T H x > 0 for all x ≠ 0

### 8.4 Constrained Optimization

#### Lagrange Multipliers

Minimize f(x) subject to g(x) = 0.

Solution satisfies:
$$\nabla f = \lambda \nabla g$$

where λ is Lagrange multiplier.

#### KKT Conditions

For inequality constraints h(x) ≤ 0:
- ∇f + Σ λᵢ∇gᵢ + Σ μⱼ∇hⱼ = 0
- λᵢ ≥ 0
- λᵢgᵢ = 0 (complementary slackness)
- Feasibility constraints

---

## 9. Calculus in ML

### 9.1 Gradient Descent

Minimize f(x) by iterative updates:
$$\mathbf{x}_{t+1} = \mathbf{x}_t - \alpha \nabla f(\mathbf{x}_t)$$

where α is learning rate.

### 9.2 Loss Functions

**Mean Squared Error (MSE)**:
$$L = \frac{1}{n} \sum_{i=1}^n (y_i - \hat{y}_i)^2$$

$$\frac{\partial L}{\partial \hat{y}_i} = -\frac{2}{n}(y_i - \hat{y}_i)$$

**Cross-Entropy**:
$$L = -\sum_i y_i \log \hat{y}_i$$

$$\frac{\partial L}{\partial z_i} = \hat{y}_i - y_i$$

### 9.3 Backpropagation

Chain rule for computing gradients in neural networks.

For loss L, weight w_l:
$$\frac{\partial L}{\partial w_l} = \frac{\partial L}{\partial a_l} \cdot \frac{\partial a_l}{\partial w_l}$$

where a_l = f(z_l), z_l = w_l · a_{l-1} + b_l.

### 9.4 Regularization Gradients

**L2 (Ridge)**:
$$\mathcal{L}_{reg} = \mathcal{L} + \lambda \sum w_i^2$$
$$\frac{\partial \mathcal{L}_{reg}}{\partial w_i} = \frac{\partial \mathcal{L}}{\partial w_i} + 2\lambda w_i$$

**L1 (Lasso)**:
$$\mathcal{L}_{reg} = \mathcal{L} + \lambda \sum |w_i|$$
$$\frac{\partial \mathcal{L}_{reg}}{\partial w_i} = \frac{\partial \mathcal{L}}{\partial w_i} + \lambda \cdot \text{sign}(w_i)$$

### 9.5 Activation Function Derivatives

**Sigmoid**: σ(x) = 1/(1 + e^{-x})
$$\sigma'(x) = \sigma(x)(1 - \sigma(x))$$

**Tanh**: tanh(x) = (e^x - e^{-x})/(e^x + e^{-x})
$$\frac{d}{dx}\text{tanh}(x) = 1 - \text{tanh}^2(x)$$

**ReLU**: max(0, x)
$$\frac{d}{dx}\text{ReLU}(x) = \begin{cases} 1 & x > 0 \\ 0 & x \leq 0 \end{cases}$$

**Softmax**: σ(z)_i = e^{z_i}/Σ e^{z_j}
$$\frac{\partial \sigma_i}{\partial z_j} = \sigma_i (\delta_{ij} - \sigma_j)$$

---

## 10. Advanced Topics

### 10.1 Differential Equations

**First order**: dy/dx = f(x, y)

**Separable**: dy/dx = g(x)h(y)
Solution: ∫dy/h(y) = ∫g(x)dx

**Linear**: dy/dx + P(x)y = Q(x)
Solution using integrating factor.

### 10.2 Calculus of Variations

Optimize functionals (functions of functions).

**Euler-Lagrange equation**:
$$\frac{\partial L}{\partial y} - \frac{d}{dx}\frac{\partial L}{\partial y'} = 0$$

**Applications**:
- Geodesics
- Brachistochrone problem
- CNN architectures (Neural ODEs)

### 10.3 Vector Calculus

**Gradient**: ∇f
**Divergence**: ∇ · F
**Curl**: ∇ × F

**Identities**:
- ∇ · (∇ × F) = 0
- ∇ × (∇f) = 0
- ∇ × (∇ × F) = ∇(∇ · F) - ∇²F

### 10.4 Matrix Calculus

**Derivative of scalar w.r.t. matrix**:
$$\frac{\partial f}{\partial X_{ij}}$$

**Key rules**:
- ∂tr(AX)/∂X = A^T
- ∂tr(X^T A)/∂X = A
- ∂tr(AXB)/∂X = A^T B^T
- ∂log det(X)/∂X = (X^{-1})^T

### 10.5 Automatic Differentiation

**Forward mode**: Compute derivatives along with function values.

**Reverse mode (backpropagation)**: Efficient for many inputs, one output.

**Implementation**:
- Computational graph
- Chain rule application
- Accumulate gradients from outputs to inputs

---

## Summary

Key calculus concepts for ML:
1. Derivatives and partial derivatives
2. Gradients and directional derivatives
3. Jacobians and Hessians
4. Taylor series approximations
5. Integration techniques
6. Optimization conditions
7. Calculus applied to loss functions
8. Backpropagation

These foundations enable understanding and implementing ML optimization algorithms.

---

## Further Reading

1. "Deep Learning" - Goodfellow et al. (Chapter on calculus background)
2. "Mathematics for Machine Learning" - Deisenroth et al.
3. "Calculus" - Stewart (standard textbook)