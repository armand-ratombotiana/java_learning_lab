# Interview: Game Theory

## Q1: Conceptual Understanding
**Q**: Explain the Prisoner's dilemma and its real-world implications.
**A**: Two prisoners each choose to cooperate or defect. Defecting is dominant but leads to a worse collective outcome. It models arms races, price wars, and tragedy of the commons.

## Q2: Implementation
**Q**: How would you compute a mixed Nash equilibrium for a 2×2 game?
**A**: Set up indifference equations: player 1's expected payoff from each pure strategy must equal under player 2's mixed strategy. Solve linear system for probabilities.

## Q3: System Design
**Q**: Design a system for automated trading using game theory.
**A**: Model as repeated game with learning (fictitious play, regret matching). Use Nash equilibrium for fair pricing, track opponent strategies, adapt through reinforcement learning.

## Coding Challenge
Implement the minimax algorithm for a zero-sum game given a payoff matrix.
