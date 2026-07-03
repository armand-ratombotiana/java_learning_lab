# Approximation Algorithms — Theoretical Foundation

## Why Approximate?
For NP-hard optimization problems, polynomial-time optimal solutions are unlikely. Approximation algorithms provide polynomial-time solutions with provable quality guarantees.

## Approximation Ratio
Algorithm A has ratio ρ if for all inputs:
- Minimization: A(I) ≤ ρ × OPT(I)
- Maximization: OPT(I) ≤ ρ × A(I)
where ρ ≥ 1 (minimization) or ρ ≥ 1 (maximization)

## Classification
- PTAS: Polynomial-Time Approximation Scheme (1+ε for any ε>0)
- FPTAS: Fully PTAS (polynomial in n AND 1/ε)
- APX: Constant-factor approximation exists
- Log-APX: O(log n) approximation
- Poly-APX: Polynomial approximation
