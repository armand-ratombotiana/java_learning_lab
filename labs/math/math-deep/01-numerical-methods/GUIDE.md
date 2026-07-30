# Numerical Methods — Study Guide

## Core Concepts

### Root Finding
- **Bisection**: O(log n) convergence, guaranteed if sign change exists
- **Newton-Raphson**: x_{n+1} = x_n - f(x_n)/f'(x_n), quadratic convergence near root
- **Secant**: approximates derivative, superlinear convergence, no derivative needed

### Numerical Integration
- **Trapezoidal Rule**: ∫_a^b f(x)dx ≈ (b-a)[f(a)+f(b)]/2, O(h^2) error
- **Simpson's Rule**: ∫_a^b f(x)dx ≈ (b-a)[f(a)+4f(m)+f(b)]/6, O(h^4) error
- **Adaptive Quadrature**: recursive subdivision based on error estimate

### Numerical Differentiation
- **Forward Difference**: f'(x) ≈ [f(x+h)-f(x)]/h, O(h) error
- **Central Difference**: f'(x) ≈ [f(x+h)-f(x-h)]/(2h), O(h^2) error
- **Richardson Extrapolation**: combines two approximations to cancel error terms

## Implementation Checklist
1. Validate inputs (null checks, NaN, Inf)
2. Set max iterations and tolerance for iterative methods
3. Choose appropriate step size h for differentiation (too small → cancellation)
4. Use higher-order rules for better accuracy vs. performance tradeoff

## Common Pitfalls
- Newton-Raphson: requires good initial guess, derivative may be zero
- Integration: Runge phenomenon with equally spaced points for high-degree polynomials
- Differentiation: catastrophic cancellation when h is too small
