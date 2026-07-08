# Exercises: Numerical Methods

## Theoretical Exercises

### Problem 1: Error Analysis
Derive the error term for the trapezoidal rule. Show that it is O(h²).

### Problem 2: Simpson's Rule
Prove that Simpson's rule is exact for polynomials up to degree 3.

### Problem 3: Bisection Method
Show that the bisection method reduces the error interval by half each iteration. How many iterations are needed for 10⁻¹² precision?

### Problem 4: Newton's Method Convergence
Derive the convergence rate of Newton's method. Show that it is quadratic under standard assumptions.

### Problem 5: Runge-Kutta Stability
Explain the stability region of the RK4 method. Why is it not A-stable?

### Problem 6: Finite Differences
Derive the central difference approximation for f'(x). Show that the error is O(h²).

### Problem 7: Interpolation Error
Derive the error bound for Lagrange interpolation. Explain the Runge phenomenon.

### Problem 8: Richardson Extrapolation
Explain how Richardson extrapolation works. Why can it achieve high-order accuracy from low-order methods?

## Programming Exercises

### Exercise 1: Rectangle Rule
Implement the rectangle rule for numerical integration. Compare left, right, and midpoint variants.

### Exercise 2: Trapezoidal Rule
Implement the trapezoidal rule. Test on ∫₀¹ x² dx = 1/3.

### Exercise 3: Simpson's Rule
Implement Simpson's rule. Compare its accuracy to the trapezoidal rule for the same n.

### Exercise 4: Romberg Integration
Implement Romberg integration. Show that it achieves higher accuracy than Simpson's rule for the same number of function evaluations.

### Exercise 5: Gaussian Quadrature
Implement 2-point Gauss-Legendre quadrature. Compare its accuracy to Simpson's rule.

### Exercise 6: Bisection Method
Find the root of x³ - x - 2 = 0 using bisection. How many iterations for 10⁻⁶ precision?

### Exercise 7: Newton-Raphson
Find the root of x³ - x - 2 = 0 using Newton's method. Compare iterations needed vs. bisection.

### Exercise 8: Secant Method
Implement the secant method. Compare its convergence to Newton's method (no derivative needed).

### Exercise 9: Lagrange Interpolation
Interpolate sin(x) at Chebyshev nodes vs. equidistant nodes. Show the Runge phenomenon.

### Exercise 10: RK4 ODE Solver
Solve y' = y, y(0) = 1 using RK4. Compare accuracy vs. Euler's method for the same step size.

### Exercise 11: Finite Differences
Compute the derivative of sin(x) at x = π/4 using forward, backward, and central differences. Find the optimal h.

### Exercise 12: Heat Equation
Implement a finite difference solver for the 1D heat equation. Verify that the solution satisfies the expected properties.

## Mini-Project: ODE Pendulum Simulator
Simulate a simple pendulum using RK4. The ODE is θ'' = -(g/L)sin(θ). Convert to a system of first-order ODEs. Plot θ vs. time for various initial angles. Compare small-angle approximation vs. full nonlinear solution.

## Real-World Project: Scientific Computing Toolkit
Build a comprehensive numerical computing library that includes:
1. Numerical integration (trapezoidal, Simpson's, Romberg, Gaussian)
2. Root finding (bisection, Newton, secant, false position, Brent)
3. Interpolation (Lagrange, Newton, cubic splines)
4. ODE solvers (Euler, Heun, RK4, with adaptive step size)
5. Finite differences (first and second derivatives, gradient computation)
6. Each method should include error estimation
7. Provide a unified API with configurable parameters
8. Write comprehensive tests for all components
