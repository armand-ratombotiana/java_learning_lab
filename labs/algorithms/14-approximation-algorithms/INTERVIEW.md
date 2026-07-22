# Interview Questions: Approximation Algorithms

## LeetCode Problem Map
No direct LeetCode problems. Theory focus: TSP approximation, vertex cover, set cover.

## NeetCode Reference
Not directly covered. Approximation algorithms appear in advanced algorithm discussions and system design interviews.

## Company-Specific Questions
### Google
- Design a 2-approximation for the metric TSP using MST
- How would you approximate the maximum cut in a graph?
- Explain the greedy set cover algorithm and its O(log n) approximation ratio
- Design a polynomial-time approximation scheme (PTAS) for knapsack

### Microsoft
- How would you approximate the vertex cover problem in a graph?
- Design an approximation algorithm for warehouse location
- Explain the difference between heuristic and approximation (guarantees are key)
- How does the Windows scheduler approximate optimal resource allocation?

### Meta
- Approximation algorithms for social network clustering
- How would you approximate the maximum influence spread in a social graph?
- Design an approximation for the news feed ranking problem
- Explain why approximation ratio matters for production systems

### Amazon
- Design a 2-approximation for the facility location problem
- How does Amazon approximate optimal inventory placement?
- Explain the traveling salesman approximation in last-mile delivery
- Design an approximation algorithm for container packing

### Apple
- Approximation algorithms for power-constrained optimization
- How would you approximate optimal battery charging schedules?
- Design an approximation for memory allocation across apps

### Oracle
- How does Oracle's query optimizer approximate the best execution plan?
- Design an approximation for database index selection
- Explain how cardinality estimation is an approximation problem
- How would you approximate the optimal materialized view set?

## Real Production Scenarios
- Scenario 1: Last-mile delivery - using Christofides algorithm (1.5-approximation for metric TSP) to plan daily delivery routes for 200+ stops per driver
- Scenario 2: Server placement - designing a facility location approximation to determine optimal AWS region placement for edge computing nodes minimizing latency
- Scenario 3: Ad allocation - debugging a greedy ad allocation algorithm that achieves only 60% of optimal revenue and redesigning with approximation guarantees

## Interview Tips
- Approximation algorithms have provable bounds (unlike heuristics)
- Key techniques: greedy with exchange argument, LP rounding, primal-dual, randomization
- Know the classic approximations: Vertex Cover (2-approx), Set Cover (O(log n)), Metric TSP (1.5-approx), MAX-CUT (0.5-approx)
- Common edge cases: disconnected graphs, non-metric distances, unbounded approximation ratios

## Java-Specific Considerations
- Implement MST-based TSP approximation with `PriorityQueue` for Prim's and `UnionFind` for Kruskal's
- For vertex cover: maximal matching using `boolean[] visited` and edge iteration
- Greedy set cover: `BitSet` for representing universe elements and sets
- Pitfall: floating-point precision in approximation algorithms involving distances
- Pitfall: assuming triangle inequality holds when it doesn't (non-metric TSP is harder to approximate)
- Java BigInteger for exact rational bounds in approximation ratio proofs
