# Why Linear Programming Exists

Linear programming exists because many real-world optimization problems have linear constraints and linear objectives. Resource allocation, production planning, portfolio optimization, transportation, and scheduling can all be modeled as linear programs. The assumption of linearity is remarkably expressive while remaining computationally tractable.

The simplex method exists because the optimal solution of an LP occurs at a vertex of the feasible polytope. The simplex method navigates vertices along edges, improving the objective at each step. This geometric insight leads to an algorithm that is efficient in practice despite exponential worst-case complexity.

Linear programming exists because it connects to duality—one of the most powerful concepts in optimization. The dual LP provides a certificate of optimality and gives economic interpretation (shadow prices). The dual of a producer's profit-maximization problem is the consumer's cost-minimization problem.

The two-phase method exists because many LPs lack an obvious basic feasible solution. The two-phase approach adds artificial variables and solves an auxiliary LP to find feasibility before optimizing the original objective. This makes the simplex method applicable to any LP without requiring special structure.