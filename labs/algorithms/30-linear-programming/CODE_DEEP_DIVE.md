# Linear Programming — Code Deep Dive

The Simplex class maintains a 2D double array tableau of size (m+1) x (n+1). Rows 0..m-1 represent constraints with coefficients and RHS in column n. Row m is the objective function with negative reduced costs in columns 0..n-1 and current objective value in column n.

Initialization: for (int i = 0; i < m; i++) { for (int j = 0; j < n; j++) tableau[i][j] = A[i][j]; tableau[i][n] = b[i]; } for (int j = 0; j < n; j++) tableau[m][j] = -c[j]; tableau[m][n] = 0;

The solve method loops: find entering column q with tableau[m][q] < -EPS (most negative). If none, optimal. Find leaving row p: minimum tableau[p][n] / tableau[p][q] for tableau[p][q] > EPS. If none, unbounded. Pivot on tableau[p][q].

The pivot method: for (int j = 0; j <= n; j++) if (j != q) tableau[p][j] /= tableau[p][q]; tableau[p][q] = 1; For each row i != p: factor = tableau[i][q]; for (int j = 0; j <= n; j++) tableau[i][j] -= factor * tableau[p][j]; tableau[i][q] = 0.

The TwoPhaseSimplex class extends Simplex. Phase I adds artificial variable columns. The objective row minimizes sum of artificial variables. After Phase I, if the minimum > EPS, problem is infeasible. Otherwise, remove artificial columns and replace objective row with original objective.

The LPSolver interface's solve method handles problem conversion: <= constraints get slack variables, >= constraints get surplus and artificial variables, == constraints get artificial variables.