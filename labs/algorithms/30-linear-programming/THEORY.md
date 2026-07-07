# Linear Programming — Theoretical Foundation

## Linear Programming Problem

A linear program (LP) consists of a linear objective function subject to linear equality and inequality constraints. In standard form: minimize c^T x subject to Ax = b, x &gt;= 0, where A is an m x n matrix, c is the cost vector, and b is the resource vector. Every LP can be transformed to standard form by adding slack and surplus variables.

## Geometry of Linear Programming

The feasible region of an LP is a convex polytope (possibly unbounded). The optimal solution, if bounded, occurs at a vertex (extreme point) of this polytope. Each vertex corresponds to a basis: n - m variables are set to zero (non-basic), and the remaining m variables (basic) are determined by solving Ax = b.

## Simplex Method

The simplex method moves from vertex to vertex along edges of the polytope, improving the objective value at each step. Starting from a basic feasible solution, it selects a non-basic variable with negative reduced cost to enter the basis, determines the leaving variable using the minimum ratio test, and performs a pivot operation (Gaussian elimination on the tableau). The algorithm terminates when all reduced costs are non-negative.

## Tableau Representation

The simplex tableau is a matrix containing the constraint coefficients, right-hand sides, and objective coefficients in a compact form. Each row corresponds to a basic variable, and the bottom row shows reduced costs. Pivoting involves selecting a pivot element and performing row operations to make the pivot column a unit vector.

## Two-Phase Method

When no obvious basic feasible solution exists (e.g., when constraints include &gt;= or =), the two-phase method introduces artificial variables. Phase I minimizes the sum of artificial variables to find a feasible basis. If the minimum is zero, the feasible basis is used to start Phase II, which solves the original LP.

## Duality

Every LP has a dual LP. The primal-dual relationship provides certificates of optimality: if x is feasible for the primal and y is feasible for the dual, and c^T x = b^T y, then both are optimal. Weak duality says c^T x &gt;= b^T y for feasible solutions. Strong duality says optimal values coincide when either is finite.

## Sensitivity Analysis

Sensitivity analysis studies how changes in parameters (cost coefficients, right-hand sides, constraint coefficients) affect the optimal solution. Shadow prices are the dual variables indicating the marginal value of relaxing a constraint. Ranging analysis determines the interval over which basis optimality is maintained.