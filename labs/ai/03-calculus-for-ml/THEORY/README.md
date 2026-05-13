# Calculus for Machine Learning - Theory

## 1. Differential Calculus

### Derivatives
The derivative measures the rate of change of a function:
```
f'(x) = lim(h→0) [f(x+h) - f(x)] / h
```

### Rules of Differentiation
- **Constant**: d/dx(c) = 0
- **Power**: d/dx(xⁿ) = nxⁿ⁻¹
- **Product**: (fg)' = f'g + fg'
- **Quotient**: (f/g)' = (f'g - fg') / g²
- **Chain**: (f∘g)' = (f'∘g) * g'

### Common Derivatives
- exp(x) → exp(x)
- log(x) → 1/x
- sin(x) → cos(x)
- cos(x) → -sin(x)

## 2. Partial Derivatives

### Definition
For functions of multiple variables:
```
∂f/∂xᵢ = lim(h→0) [f(x₁,...,xᵢ+h,...,xₙ) - f(x)] / h
```

### Gradient Vector
∇f = (∂f/∂x₁, ∂f/∂x₂, ..., ∂f/∂xₙ)

Points in direction of steepest ascent.

### Hessian Matrix
```
H = [∂²f/∂xᵢ∂xⱼ]
```
Second-order partial derivatives.

## 3. Taylor Series

### Single Variable
```
f(x) = f(a) + f'(a)(x-a) + f''(a)/2! (x-a)² + ...
```

### Multi-variable
```
f(x) ≈ f(a) + ∇f(a)·(x-a) + 0.5(x-a)ᵀH(a)(x-a)
```

### Applications
- Function approximation
- Newton-Raphson optimization
- Second-order optimization

## 4. Integral Calculus

### Definite Integrals
```
∫ₐᵇ f(x)dx = F(b) - F(a)
```
Area under the curve.

### Common Integrals
- ∫xⁿdx = xⁿ⁺¹/(n+1) + C
- ∫e^xdx = e^x + C
- ∫1/x dx = log|x| + C

### Integration Techniques
- Substitution
- Integration by parts
- Partial fractions

## 5. Vector Calculus

### Jacobian
For f: ℝⁿ → ℝᵐ:
```
J = [∂fᵢ/∂xⱼ]
```

### Divergence
∇·F = Σ ∂Fᵢ/∂xᵢ

### Curl
∇×F = (∂F₃/∂y - ∂F₂/∂z, ∂F₁/∂z - ∂F₃/∂x, ∂F₂/∂x - ∂F₁/∂y)

## 6. Optimization in ML

### First-Order Methods
- Gradient Descent: x ← x - η∇f(x)
- Momentum, Nesterov acceleration

### Second-Order Methods
- Newton's Method: x ← x - H⁻¹∇f
- Quasi-Newton (BFGS)

### Conditions for Optima
- **Necessary**: ∇f = 0
- **Sufficient (minimum)**: H is positive definite