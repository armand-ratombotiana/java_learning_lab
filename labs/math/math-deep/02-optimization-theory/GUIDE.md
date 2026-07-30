# Optimization Theory — Study Guide

## Core Concepts

### Convexity
- A set S is convex if for any x,y in S, tx+(1-t)y in S for t in [0,1]
- A function f is convex if f(tx+(1-t)y) <= tf(x)+(1-t)f(y)
- For twice-differentiable f: convex iff Hessian is positive semi-definite

### Gradient Descent
- x_{k+1} = x_k - α_k ∇f(x_k)
- Fixed step size, Armijo backtracking, or Barzilai-Borwein
- Linear convergence for strongly convex functions

### Newton's Method
- x_{k+1} = x_k - [∇²f(x_k)]⁻¹ ∇f(x_k)
- Quadratic convergence near optimum
- Requires Hessian computation and inversion

### Lagrange Multipliers
- For min f(x) s.t. g(x)=0: L(x,λ)=f(x)+λg(x)
- KKT: ∇L=0, primal feasibility, dual feasibility, complementarity

## Implementation Checklist
1. Check convexity before choosing global vs. local optimization
2. Implement line search (backtracking Armijo) for robust step size
3. For Newton: ensure Hessian is positive definite (add regularization)
4. Validate gradients with finite difference check

## Common Pitfalls
- Non-convex problems: gradient descent may find only local minima
- Ill-conditioned Hessians: Newton's method can diverge
- Wrong Lagrange multiplier sign convention
