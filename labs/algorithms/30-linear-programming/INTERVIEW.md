# Interview Questions: Linear Programming

## LeetCode Problem Map
No direct LeetCode problems. Theory focus: simplex, duality, interior point methods.

## NeetCode Reference
Not covered in NeetCode 150. Linear programming is an advanced optimization topic.

## Company-Specific Questions
### Google
- Formulate the max flow problem as a linear program
- Explain the simplex method and when it performs poorly (Klee-Minty cube)
- What is duality and how is it used in optimization?
- Design a linear program for YouTube video transcoding resource allocation
- How does Google's AdWords auction use linear programming?

### Microsoft
- How does the Excel Solver implement linear programming?
- Design a refinery optimization using LP (classic textbook problem)
- Explain interior point methods vs simplex
- How would you solve a transportation problem using LP?

### Meta
- Formulate ad budget allocation as a linear program
- How would you optimize News Feed diversity using LP constraints?
- LP relaxation for combinatorial optimization problems
- Not commonly asked; appears in ML infrastructure roles

### Amazon
- How does Amazon's supply chain use linear programming?
- Formulate the transportation problem for warehouse distribution
- Design an LP for inventory placement across fulfillment centers
- Explain how AWS Auto Scaling uses optimization techniques

### Apple
- LP for manufacturing supply chain optimization
- How would you allocate resources across multiple iOS app services?
- Design a battery management system using constraint optimization

### Oracle
- How does Oracle's Constraint Programming engine work?
- Explain Oracle's R optimization and its LP capabilities
- How does Oracle Advanced Analytics integrate with LP solvers?
- Design a database query optimization using cost-based LP models

## Real Production Scenarios
- Scenario 1: Cloud cost optimization - using linear programming to minimize AWS spend by selecting optimal mix of reserved, spot, and on-demand instances across 50+ instance types under capacity constraints
- Scenario 2: Supply chain optimization - formulating a multi-echelon inventory optimization as an LP to minimize total holding and transportation costs across 100+ warehouse locations
- Scenario 3: Network flow optimization - debugging an LP model for traffic engineering that produces infeasible solutions due to incorrect inequality direction in capacity constraints

## Interview Tips
- Standard form: minimize c^T x subject to Ax <= b, x >= 0
- Simplex explores vertices of the feasible polytope; exponential worst-case but polynomial in practice
- Interior point methods (Karmarkar) are polynomial; better for very large problems
- Common edge cases: infeasible LP, unbounded LP, multiple optimal solutions, degeneracy

## Java-Specific Considerations
- Apache Commons Math provides `SimplexSolver` for LP problems
- `OptaPlanner` (now Timefold) for constraint satisfaction with LP-like capabilities
- Expression representation: `class LinearConstraint` with coefficients and relationship
- Pitfall: numerical instability in simplex due to floating-point rounding; use tolerances
- Pitfall: large sparse matrices; use sparse matrix representation (e.g., `HashMap`-based) not `double[][]`
- For production: interface with native solvers (CPLEX, Gurobi, SCIP) via JNI or REST APIs
