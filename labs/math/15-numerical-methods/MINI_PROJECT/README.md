# Mini-Project: Pendulum Simulator

## Objective
Simulate a simple pendulum using RK4 ODE integration.

## Requirements
1. Model pendulum as second-order ODE: θ'' = -(g/L)sin(θ)
2. Convert to system of first-order ODEs
3. Implement RK4 integration
4. Simulate for various initial angles (5°, 30°, 90°, 179°)
5. Plot θ(t) and compare small-angle approximation vs. full nonlinear

## Extensions
- Add damping: θ'' = -(g/L)sin(θ) - bθ'
- Add driving force (forced pendulum, chaos)
- Add double pendulum simulation
- Compare Euler, Heun, and RK4 accuracy

## Evaluation Criteria
- Energy conservation (or dissipation for damped)
- Correct period (compare to analytical for small angles)
- Numerical stability (long-term behavior)
- Visualization of results
