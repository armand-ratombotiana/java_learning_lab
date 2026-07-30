# Game Theory — Study Guide

## Core Concepts

### Nash Equilibrium
- A strategy profile where no player can benefit by unilaterally deviating
- Pure NE: deterministic strategies; Mixed NE: probability distributions
- Nash's theorem: every finite game has at least one mixed NE

### Zero-Sum Games
- One player's gain equals the other's loss: u1 + u2 = 0
- Minimax theorem: max_{p} min_{q} u(p,q) = min_{q} max_{p} u(p,q) = value
- Solved with linear programming or iterated elimination

### Prisoner's Dilemma
- Two prisoners: cooperate or defect
- Dominant strategy: defect (rational self-interest)
- Pareto optimum: both cooperate

## Implementation Checklist
1. Represent payoff matrices as 2D double arrays
2. Check for pure NE by iterating over all strategy pairs
3. Compute mixed NE using linear programming (simplex or Lemke-Howson)
4. For zero-sum: use minimax or linear programming

## Common Pitfalls
- Confusing Pareto optimality with Nash equilibrium
- Assuming players always play pure strategies
- Forgetting that mixed NE requires indifference conditions
