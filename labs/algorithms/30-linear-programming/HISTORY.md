# History of Linear Programming

1827: Joseph Fourier (yes, the same Fourier) first described a method for solving systems of linear inequalities, predating formal linear programming.

1939: Leonid Kantorovich formulated the linear programming problem for production planning in the Soviet Union. His work was largely unknown in the West.

1947: George Dantzig invented the simplex method for solving LPs. He developed it while working for the US Air Force on logistics problems. The simplex method remains one of the most important algorithms of the 20th century.

1947: John von Neumann recognized the duality principle in linear programming, establishing the deep connection between primal and dual problems.

1950s: The simplex method was implemented on early computers, solving problems with hundreds of constraints. It became the standard method for decades.

1972: Victor Klee and George Minty constructed worst-case examples where the simplex method visits an exponential number of vertices, proving the simplex method is not polynomial.

1979: Leonid Khachiyan published the ellipsoid method, the first polynomial-time algorithm for linear programming. However, it was slower than simplex in practice.

1984: Narendra Karmarkar developed the interior point method, a polynomial-time algorithm that competed with and often outperformed simplex on large problems.

1990s: CPLEX, Gurobi, and other commercial solvers incorporated both simplex and interior point methods, solving LPs with millions of variables.

2000s: Open-source solvers (GLPK, COIN-OR, lp_solve) made LP technology widely accessible. LP remains a core technology in operations research and machine learning.