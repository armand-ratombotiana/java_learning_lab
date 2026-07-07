# Linear Programming — Mathematical Foundation

## Standard Form

Minimize c^T x subject to Ax = b, x >= 0. Every LP can be converted: inequalities become equalities via slack/surplus variables; free variables become difference of two non-negative variables; maximization becomes minimization by negating the objective.

## Feasible Region Convexity

The feasible region {x | Ax = b, x >= 0} is a convex set. If x and y are feasible, then tx + (1-t)y is feasible for any t in [0,1]. This follows from the linearity of constraints and the non-negativity of convex combinations of non-negative vectors.

## Fundamental Theorem of Linear Programming

If an LP is feasible and bounded, the optimal solution occurs at a vertex (basic feasible solution). A vertex corresponds to a basis: select m linearly independent columns of A, set corresponding variables (basic) to solve Ax = b, set other variables (non-basic) to 0.

## Duality Theorem

Primal (min c^T x, Ax >= b, x >= 0) has dual (max y^T b, y^T A <= c^T, y >= 0). Weak duality: c^T x >= y^T b for feasible x,y. Strong duality: if either has finite optimum, so does the other, and the optimal values are equal. Complementary slackness: x_j * (c_j - y^T A_j) = 0 and y_i * (A_i x - b_i) = 0 at optimality.

## Sensitivity Analysis

The reduced cost of variable x_j is c_j - c_B^T B^{-1} A_j where B is the basis matrix. A negative reduced cost indicates that increasing x_j improves the objective. Shadow prices y = c_B^T B^{-1} give the rate of change of the objective with respect to the RHS.

## Klee-Minty Cube

The Klee-Minty LP is a deformed hypercube where the simplex method visits all 2^n vertices. This shows that the simplex method has exponential worst-case complexity. However, such instances are rare in practice.