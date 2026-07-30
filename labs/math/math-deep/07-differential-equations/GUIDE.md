# Differential Equations — Study Guide

## Core Concepts

### Euler's Method
- y_{n+1} = y_n + h * f(t_n, y_n)
- O(h) local truncation error, O(h) global error
- Simple but unstable for stiff equations

### Runge-Kutta RK4
- k1 = f(t_n, y_n)
- k2 = f(t_n + h/2, y_n + h*k1/2)
- k3 = f(t_n + h/2, y_n + h*k2/2)
- k4 = f(t_n + h, y_n + h*k3)
- y_{n+1} = y_n + h/6 * (k1 + 2*k2 + 2*k3 + k4)
- O(h⁵) local truncation error

### PDE Classification
- **Elliptic**: Laplace ∇²u = 0 — steady-state, boundary value
- **Parabolic**: Heat u_t = α∇²u — time-dependent diffusion
- **Hyperbolic**: Wave u_tt = c²∇²u — time-dependent wave propagation

## Implementation Checklist
1. Define the ODE system as a function f(t, y) returning dy/dt
2. Choose step size h small enough for stability (CFL condition for PDEs)
3. For RK4: compute all four slopes before updating
4. Use adaptive step size (RK45) when solution stiffness is unknown

## Common Pitfalls
- Too large step size leads to instability (especially for Euler)
- Stiff equations require implicit methods (Backward Euler, TR-BDF2)
- Boundary conditions are critical for PDE well-posedness
