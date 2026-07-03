# Step-by-Step — Approximation

## Analyzing Approximation Ratio
1. Find lower bound on OPT (for minimization)
   - LP relaxation, MST cost, greedy bound
2. Show algorithm output ≤ ρ × lower bound
3. Since OPT ≥ lower bound, algorithm is ρ-approximation

## Designing Approximation Algorithms
1. Try greedy approach first (analyze ratio)
2. Consider LP rounding (solve LP, round to integer solution)
3. Primal-dual methods (simultaneously construct solution and bound)
4. Local search (iteratively improve, bound local optimum)
5. Randomized rounding (randomize LP solution)
