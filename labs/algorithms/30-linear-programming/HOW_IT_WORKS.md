# How the Simplex Method Works

## Tablaeu Representation

A simplex tableau is a matrix with m+1 rows and n+1 columns:
- Rows 0..m-1: constraint coefficients A[i][j] and RHS b[i].
- Row m (objective row): negative cost coefficients -c[j] and current objective value.
- Basic variables are identified by unit columns in the constraint rows.

## Pivot Operation

Select entering variable x_e (most negative reduced cost). Select leaving variable x_l by minimum ratio test: for each row i where A[i][e] > 0, compute b[i]/A[i][e], choose the row with smallest ratio. If no A[i][e] > 0, the problem is unbounded.

Pivot on A[l][e]:
- Divide row l by A[l][e].
- For all other rows i != l, subtract A[i][e] * row l from row i.
- Update the objective row similarly.

## Two-Phase Method

Phase I: Add artificial variables to constraints, minimize sum of artificial variables using simplex. This either finds a feasible basis (artificial variables leave the basis) or proves infeasibility.

Phase II: Remove artificial variables and their columns. Replace objective row with original objective coefficients. Compute reduced costs for the current basis and proceed with regular simplex.

## Handling Unboundedness

If a column has negative reduced cost but all entries in that column are <= 0, the problem is unbounded: the entering variable can increase indefinitely without violating constraints.

## Degeneracy

When the minimum ratio test selects a row with b[i] = 0, the pivot is degenerate (objective does not change). The algorithm may cycle (return to the same basis). Bland's rule prevents cycling: among all eligible entering variables, choose the smallest index; among eligible leaving variables, choose the smallest index.