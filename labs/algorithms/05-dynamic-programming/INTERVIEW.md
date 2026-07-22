# Interview Questions: Dynamic Programming

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| LC 70 Climbing Stairs | Easy | Google, Meta, Amazon, Microsoft | Fibonacci DP |
| LC 198 House Robber | Medium | Google, Meta, Amazon | Linear DP |
| LC 322 Coin Change | Medium | Google, Meta, Amazon | Unbounded knapsack |
| LC 300 Longest Increasing Subsequence | Medium | Google, Meta, Amazon | Patience sorting |
| LC 1143 Longest Common Subsequence | Medium | Google, Meta, Amazon | 2D DP |
| LC 72 Edit Distance | Medium | Google, Meta, Microsoft | 2D DP |
| LC 416 Partition Equal Subset Sum | Medium | Google, Meta | 0/1 knapsack |
| LC 518 Coin Change II | Medium | Google, Amazon | Unbounded knapsack |
| LC 139 Word Break | Medium | Google, Meta, Amazon | DP + trie / set |
| LC 312 Burst Balloons | Hard | Google, Meta | Interval DP |

## NeetCode Reference
- LC 70 Climbing Stairs (NeetCode 150)
- LC 198 House Robber (NeetCode 150)
- LC 322 Coin Change (NeetCode 150)
- LC 300 Longest Increasing Subsequence (NeetCode 150)
- LC 1143 Longest Common Subsequence (NeetCode 150)
- LC 72 Edit Distance (NeetCode 150)
- LC 416 Partition Equal Subset Sum (NeetCode 150)
- LC 139 Word Break (NeetCode 150)
- LC 312 Burst Balloons (NeetCode 150 - hard)

## Company-Specific Questions
### Google
- Burst Balloons, Coin Change, and Edit Distance are Google staples
- Focus on optimal substructure identification and state definition
- Expect follow-ups asking to optimize space from O(n^2) to O(n) or handling constraints

### Microsoft
- DP questions are generally straightforward (Climbing Stairs, House Robber)
- Focus on memoization vs tabulation trade-offs
- May combine DP with string processing (Edit Distance, LCS)

### Meta
- DP is heavily tested; expect variations of Coin Change, Word Break, House Robber
- Meta often gives problems requiring O(n) or O(n^2) DP with space optimization
- Exhaustive testing: they verify that you handle all corner cases

### Amazon
- DP questions tied to resource allocation and optimization
- Subset sum and knapsack variants are common for Amazon
- Expect "real-world" framing: "You have X units of capacity..."

### Apple
- Memory-constrained DP solutions with space optimization
- Linear DP problems (House Robber, Climbing Stairs) most common
- Expect questions where DP state can be reduced to a few variables

### Oracle
- Less DP-heavy; when it appears, it's usually classic problems (LCS, Edit Distance)
- Focus on understanding recurrence relations
- May combine with database concepts (sequence alignment in bioinformatics)

## Real Production Scenarios
- Scenario 1: Cloud cost optimization - using DP to allocate resources across VM instances minimizing cost while meeting capacity constraints across multiple regions
- Scenario 2: Video transcoding pipeline - using DP to determine optimal bitrate ladder for different network conditions maximizing QoE under bandwidth constraints
- Scenario 3: Recommendation budget allocation - using knapsack DP to allocate promotional budget across product categories to maximize revenue

## Interview Tips
- State definition is 90% of the solution: clearly define dp[i], dp[i][j], or dp[i][j][k]
- Start with brute force recursion, add memoization, then convert to bottom-up
- For space optimization, look at the recurrence: if dp[i] depends only on dp[i-1], use O(1)
- Common edge cases: empty input, single element, all elements same, negative numbers

## Java-Specific Considerations
- Use `int[]`, `int[][]`, or `HashMap` for memoization based on state space size
- `Arrays.fill(dp, -1)` marks uncomputed states; use sentinel values carefully
- For large dp arrays, consider memory layout (row-major vs column-major)
- Pitfall: not using `long` for intermediate sums that can overflow `int`
- Pitfall: off-by-one in DP array dimensions (dp[n] vs dp[n+1] for 0-indexed)
- Stream API is NOT suitable for DP; traditional loops are both clearer and faster
