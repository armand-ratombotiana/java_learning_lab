# Simplex — Internal Implementation Details

The Simplex class implements the tableau simplex method. Internal structures:
- int m: number of constraints.
- int n: number of variables (including slack/surplus).
- double[][] tableau: (m+1) x (n+1) array. Rows 0..m-1 are constraints, row m is objective. Columns 0..n-1 are variables, column n is RHS.
- int[] basis: basis[i] stores the index of the basic variable for constraint i.
- double EPS: tolerance for zero detection (1e-9).

Pivot selection:
- Entering variable: column with most negative reduced cost (tableau[m][col] < -EPS). If none, optimal.
- Leaving variable: row with minimum ratio tableau[i][n] / tableau[i][col] where tableau[i][col] > EPS. If none, unbounded.

The pivot operation divides the pivot row by tableau[pivotRow][pivotCol], then eliminates the pivot column from all other rows.

The TwoPhaseSimplex class extends this with artificial variables. Phase I adds artificial variables to constraints that lack slack variables, minimizes sum of artificial variables, then removes them when they leave the basis.

Numerical stability: partial pivoting (not needed in tableau form, but ratio test uses positive denominator). The EPS threshold prevents division by near-zero values. Degeneracy handling uses Bland's rule to prevent cycling.

The LPSolver interface provides a high-level API: solve(c, A, b, sense) where sense indicates <=, >=, or == constraints. It handles the conversion to standard form and calls either Simplex or TwoPhaseSimplex as needed.