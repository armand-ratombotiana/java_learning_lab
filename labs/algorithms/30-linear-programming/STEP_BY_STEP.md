# Linear Programming — Step by Step Guide

## Step 1: Create the Tableau

Given m constraints and n variables, create (m+1) x (n+1) tableau. Rows 0..m-1: coefficients A[i][j] and RHS b[i]. Row m: -c[j] (reduced costs) and 0 (objective value). Initialize basis array: basic variables are the slack variables added to each constraint.

## Step 2: Find Entering Variable

Scan the objective row for the most negative value (column q). If all >= -EPS, we are optimal. The variable x_q enters the basis.

## Step 3: Find Leaving Variable

For each row i with tableau[i][q] > EPS, compute ratio = tableau[i][n] / tableau[i][q]. Select row p with minimum ratio. Variable basis[p] leaves the basis. If no row satisfies, the problem is unbounded.

## Step 4: Pivot

Divide pivot row p by tableau[p][q]. For each row i != p: factor = tableau[i][q]; subtract factor * row p from row i. Set basis[p] = q.

## Step 5: Iterate

Repeat steps 2-4 until optimal (all reduced costs >= -EPS) or unbounded.

## Step 6: Extract Solution

For each variable j, if j is basic (j in basis), x[j] = tableau[row][n] for the row where basis[row] == j. Else x[j] = 0.

## Step 7: Two-Phase Addition

If no obvious BFS exists: add artificial variables. Phase I minimizes sum of artificials. If minimum > 0, infeasible. Else, drop artificials, replace objective row, continue with Phase II simplex.

## Step 8: Test on Small LPs

Test on 2D LPs with known solutions. Verify optimal value matches manual computation.