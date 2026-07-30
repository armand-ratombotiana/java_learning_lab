# Interview: Differential Equations

## Q1: Conceptual Understanding
**Q**: Compare Euler's method and RK4 for ODE solving.
**A**: Euler is O(h) accuracy and simple but unstable for stiff equations. RK4 is O(h⁴) accuracy with much better stability. RK4 requires 4 evaluations per step vs. 1 for Euler.

## Q2: Implementation
**Q**: How would you handle a stiff ODE system?
**A**: Use implicit methods like Backward Euler or TR-BDF2. Alternatively use adaptive step size with error control (RK45, Dormand-Prince). For very stiff systems, use BDF methods.

## Q3: System Design
**Q**: Design a simulation framework for coupled ODEs.
**A**: Abstract solver interface (Euler, RK4, adaptive), ODE system interface (compute derivatives), event detection for state changes, checkpoint/restart capability, and parallel ensemble runs.

## Coding Challenge
Implement RK4 for the Lorenz system: dx/dt = σ(y-x), dy/dt = x(ρ-z)-y, dz/dt = xy-βz.
