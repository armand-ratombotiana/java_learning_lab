# Real-World Project: Scientific Computing Toolkit

## Objective
Build a comprehensive numerical computing library with multiple integration, root-finding, interpolation, ODE, and finite difference methods.

## Architecture
1. **Integration** (trapezoidal, Simpson's, Romberg, Gauss-Legendre)
2. **Root Finding** (bisection, Newton, secant, false position, Brent)
3. **Interpolation** (Lagrange, Newton, cubic splines)
4. **ODE Solvers** (Euler, Heun, RK4, adaptive RKF45)
5. **Finite Differences** (first/second derivatives, gradients)

## Components
- NumericIntegrator interface + implementations
- RootFinder interface + implementations
- Interpolator interface + implementations
- ODESolver interface + implementations
- Differentiator class (finite differences)
- AdaptiveStepSize for ODE solvers
- Error estimation for all methods

## Evaluation Criteria
- Correct implementation of all methods
- Error analysis and comparison between methods
- Adaptive step size works correctly
- Performance benchmarks
- Clean API design (Strategy pattern)
- Comprehensive test suite
