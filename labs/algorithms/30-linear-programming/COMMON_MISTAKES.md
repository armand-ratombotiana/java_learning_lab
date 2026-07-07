# Linear Programming — Common Mistakes

1. Numeric stability: using == 0 for floating-point comparisons causes severe issues. Always use an epsilon tolerance (1e-9).

2. Not detecting unboundedness: if the entering variable column has no positive entries in any constraint row, the problem is unbounded. Continuing the loop will cause division by zero.

3. Incorrect ratio test: the leaving variable is selected by minimum ratio b[i] / A[i][q], but only for rows where A[i][q] > EPS. Including rows with A[i][q] <= 0 gives incorrect or undefined ratios.

4. Cycling: degenerate pivots (where RHS is zero) can cause cycling, where the algorithm returns to a previously visited basis. Bland's rule (smallest index for entering and leaving) prevents this.

5. Two-phase method: forgetting to remove artificial variables from the basis before Phase II. If an artificial variable remains basic with non-zero value, the solution is not feasible.

6. Incorrect problem conversion: constraints of the form >= require surplus variables (subtracted) AND artificial variables (added). Only adding slack variables is wrong.

7. Not handling infeasibility: Phase I will give a positive minimum if the problem is infeasible. This should be detected and reported.

8. Extracting solution: only basic variables have non-zero values; non-basic variables are zero. The RHS column gives values for basic variables in the order of the basis array.