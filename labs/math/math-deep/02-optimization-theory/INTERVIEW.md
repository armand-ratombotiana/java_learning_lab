# Interview: Optimization Theory

## Q1: Conceptual Understanding
**Q**: When would you choose gradient descent over Newton's method?
**A**: Gradient descent when the problem is large-scale (high-dimensional) since it is O(n) per iteration. Newton's method when precision matters and the Hessian is tractable (O(n³) per iteration).

## Q2: Implementation
**Q**: How do you handle constraints in gradient-based optimization?
**A**: Use projected gradient descent (clip after each step), barrier methods (log barrier for inequality constraints), or the augmented Lagrangian method.

## Q3: Numerical Analysis
**Q**: What is the condition number of the Hessian and why does it matter?
**A**: The condition number κ = λ_max/λ_min measures ill-conditioning. Large κ makes gradient descent slow (zigzagging); Newton's method is affine-invariant and handles it naturally.

## Coding Challenge
Implement gradient descent with Armijo backtracking line search for a convex quadratic function.
