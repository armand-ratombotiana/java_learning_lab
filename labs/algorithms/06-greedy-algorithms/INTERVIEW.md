# Interview Questions: Greedy Algorithms

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| LC 55 Jump Game | Medium | Google, Meta, Amazon | Greedy reachability |
| LC 45 Jump Game II | Medium | Google, Meta, Amazon | BFS/greedy jumps |
| LC 56 Merge Intervals | Medium | Google, Meta, Amazon, Microsoft | Sort by start |
| LC 435 Non-overlapping Intervals | Medium | Google, Meta | Greedy by end |
| LC 452 Burst Balloons | Medium | Google, Amazon | Greedy by end |
| LC 621 Task Scheduler | Medium | Google, Meta, Microsoft | Greedy + idle |
| LC 1029 Two City Scheduling | Medium | Google, Amazon | Sorting by diff |
| LC 763 Partition Labels | Medium | Google, Meta | Last occurrence |
| LC 406 Queue Reconstruction | Medium | Google | Insertion sort |

## NeetCode Reference
- LC 55 Jump Game (NeetCode 150)
- LC 45 Jump Game II (NeetCode 150)
- LC 56 Merge Intervals (NeetCode 150)
- LC 435 Non-overlapping Intervals (NeetCode 150)
- LC 621 Task Scheduler (NeetCode 150)
- LC 763 Partition Labels (NeetCode 150)

## Company-Specific Questions
### Google
- Jump Game and Merge Intervals are Google classics
- Focus on greedy choice proof (exchange argument)
- Expect follow-ups that change constraints to test if greedy still holds

### Microsoft
- Interval scheduling problems are common
- How would you schedule meetings across time zones with availability constraints?
- Task scheduling with cooldown periods (Task Scheduler variant)

### Meta
- Greedy is less common than DP at Meta, but Jump Game and Partition Labels appear
- Expect a follow-up asking "Can you prove this greedy choice is optimal?"
- Merge Intervals with overlapping user sessions for analytics

### Amazon
- Merge Intervals for optimizing warehouse space utilization
- Task Scheduler for order fulfillment pipeline
- Greedy with sorting is a common pattern (Two City, Queue Reconstruction)

### Apple
- Jump Game variants for power-constrained execution scheduling
- Focus on simple greedy proofs with clear intuition
- Partition Labels for efficient memory allocation

### Oracle
- Interval scheduling for database transaction scheduling
- How would Oracle's query scheduler prioritize concurrent queries?
- Greedy algorithms for database backup scheduling

## Real Production Scenarios
- Scenario 1: Ad scheduling - using interval scheduling to maximize ad revenue by selecting non-overlapping ad slots for a video streaming platform
- Scenario 2: Container packing - applying greedy algorithms to optimize container loading on cargo ships minimizing number of containers while respecting weight limits
- Scenario 3: CDN request routing - debugging a greedy routing algorithm that causes suboptimal traffic distribution due to local maxima

## Interview Tips
- Greedy works when local optimal choice leads to global optimum (prove with exchange argument)
- Sorting is usually the first step in greedy interval problems
- If greedy doesn't work, consider DP (greedy is often a special case)
- Common edge cases: empty input, single interval, intervals that exactly touch

## Java-Specific Considerations
- `Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]))` for intervals
- `PriorityQueue` for greedy selection with dynamic ordering (e.g., Task Scheduler)
- Custom `Comparator` for sorting by multiple criteria
- Pitfall: using `Comparator.comparingInt(a -> a[0])` can cause type inference issues with arrays
- Pitfall: modifying array elements while iterating during greedy selection
- `TreeMap` for ceiling/floor lookups in greedy allocation problems
