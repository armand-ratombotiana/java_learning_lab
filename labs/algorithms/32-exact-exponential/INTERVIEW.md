# Interview Questions: Exact Exponential Algorithms

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| LC 805 Split Array With Same Average | Hard | Google | Meet-in-the-middle |
| LC 2035 Partition Array Into Two Arrays | Hard | Google | Meet-in-the-middle |
| LC 1755 Closest Subsequence Sum | Hard | Google | Meet-in-the-middle |
| LC 1986 Minimum Number of Work Sessions | Medium | Google | DP / backtracking |

Note: Exact exponential algorithms appear primarily in Google hard-level interviews for advanced roles.

## NeetCode Reference
Not covered in NeetCode 150. These are high-difficulty algorithm problems.

## Company-Specific Questions
### Google
- Design a meet-in-the-middle algorithm for subset sum with n=40 elements
- How does split-and-list generalize meet-in-the-middle?
- Explain inclusion-exclusion principle and give a counting example
- Solve TSP exactly for n=20 using DP with bitmask (Held-Karp)
- What is the Exponential-Time Hypothesis (ETH) and its implications?

### Microsoft
- How would you solve subset sum with branch and bound?
- Design an exact algorithm for the traveling salesman problem
- Explain when exponential algorithms are acceptable in practice
- How does Windows Defender use exact matching?

### Meta
- Rare; may ask about subset sum variations for feature flag testing
- How would you find the optimal partition of resources?
- Meet-in-the-middle for social graph analysis (n=30-40 nodes)

### Amazon
- Exact exponential algorithms for small-scale optimization
- How would you solve the knapsack problem exactly when n <= 30?
- Design a meet-in-the-middle approach for product bundling

### Apple
- Exponential algorithms for problems with small input size
- How would you solve TSP for a 15-city delivery route?
- Memory-efficient meet-in-the-middle for mobile devices

### Oracle
- How does Oracle's optimizer enumerate join orders using DP?
- Design an exact algorithm for small query optimization problems
- Explain how inclusion-exclusion works in query cardinality estimation

## Real Production Scenarios
- Scenario 1: Feature flag testing - using subset sum with meet-in-the-middle to find the optimal set of feature flags to enable for a rollout covering exactly 50% of users
- Scenario 2: Portfolio optimization - applying exact exponential DP to find the optimal investment portfolio from 25 assets maximizing return under risk constraints
- Scenario 3: Query optimization - debugging a Held-Karp TSP implementation that produces incorrect tour for asymmetric TSP due to missing distance matrix symmetry

## Interview Tips
- Meet-in-the-middle: split input in half, enumerate both halves, combine via sorting/binary search
- DP with bitmask: O(n^2 * 2^n) for TSP; feasible for n <= 20
- Inclusion-exclusion: |A u B u C| = |A| + |B| + |C| - |A^B| - |A^C| - |B^C| + |A^B^C|
- Common edge cases: odd n in meet-in-the-middle, single element, empty sets, integer overflow

## Java-Specific Considerations
- Meet-in-the-middle: `List<Long>` for sums of each half; sort one half, binary search from the other
- DP with bitmask: `int[][] dp = new int[1 << n][n]`; preallocate `n * 2^n` ints (~80MB for n=20)
- Use `HashMap<Integer, Integer>` for sparse meet-in-the-middle when sums have many duplicates
- Pitfall: OOM from storing all 2^(n/2) combinations for n large (n > 25)
- Pitfall: Theta(n * 2^n) memory for TSP DP (use iterative filling with bitmask iteration)
- `Integer.bitCount(mask)` and `Integer.lowestOneBit(mask)` for bit manipulation in DP
