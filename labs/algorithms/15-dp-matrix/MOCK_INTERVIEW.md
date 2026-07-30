# Mock Interview: Matrix DP (Unique Paths II)

## Meta Information

| Aspect | Detail |
|--------|--------|
| Company | Microsoft |
| Level | L63 / Senior SWE |
| Problem | Unique Paths II (LeetCode 63) |
| Duration | 45 minutes |
| Paradigm | Grid DP with obstacles |

---

## Transcript

### Phase 1: Problem Understanding (0:00–5:00)

**Interviewer:** We have a robot at the top-left corner of an m×n grid. It can only move down or right. Some cells have obstacles. Find the number of distinct paths to the bottom-right corner avoiding obstacles.

**Candidate:** So the robot can't go through obstacle cells at all? And if the start or end is an obstacle, the answer is 0?

**Interviewer:** Correct.

**Candidate:** And obstacles are represented as 1s in the grid?

**Interviewer:** Yes.

**Candidate:** Let me verify with an example:

```
grid = [[0,0,0],
        [0,1,0],
        [0,0,0]]
```

The robot has 2 paths — one going down the first column then right, another going right then down the last column. The middle cell is blocked.

**Interviewer:** That's right.

### Phase 2: Approach Design (5:00–14:00)

**Candidate:** This is a classic grid DP problem. Let me reason through the approaches.

**Brute force — DFS/BFS enumeration:** Explore all paths recursively. Time: exponential — could be up to C(m+n-2, m-1) paths, which is huge.

**2D DP:** Create a DP table where `dp[i][j]` = number of ways to reach cell `(i, j)`. The recurrence:
- If `grid[i][j]` is an obstacle: `dp[i][j] = 0`.
- Otherwise: `dp[i][j] = dp[i-1][j] + dp[i][j-1]` (paths from above + paths from left).

Base cases: `dp[0][0] = 1` if not an obstacle. For the first row and column, there's only one way to reach any cell — moving right or moving down. But if an obstacle blocks the path, subsequent cells in that row/column are unreachable.

**Interviewer:** Can you optimize the space?

**Candidate:** Yes! Since each `dp[i][j]` only depends on `dp[i-1][j]` (previous row, same column) and `dp[i][j-1]` (same row, previous column), we only need a 1D array.

**1D DP approach:**
- Use an array `dp` of length `n` (number of columns).
- For each row `i`:
  - For each column `j`:
    - If `grid[i][j]` is obstacle: `dp[j] = 0`.
    - Else if `j > 0`: `dp[j] = dp[j] + dp[j-1]`.
- `dp[j]` after processing row `i` represents the number of ways to reach cell `(i, j)`.

The key insight: when we're at row `i`, `dp[j]` still holds the value from row `i-1` (paths from above), and `dp[j-1]` has been updated for row `i` (paths from left).

**Interviewer:** Walk through the 1D DP with an example.

**Candidate:** Consider `grid = [[0,0,0],[0,1,0],[0,0,0]]`:

Initialize `dp = [1, 0, 0]` (dp[0] = 1 because start cell).

Row 0: `[0,0,0]`
- j=0: not obstacle, j=0 so no update. dp = [1, 0, 0].
- j=1: not obstacle, dp[1] = dp[1] + dp[0] = 0 + 1 = 1. dp = [1, 1, 0].
- j=2: not obstacle, dp[2] = dp[2] + dp[1] = 0 + 1 = 1. dp = [1, 1, 1].

Row 1: `[0,1,0]`
- j=0: not obstacle, dp[0] = dp[0] = 1.
- j=1: obstacle! dp[1] = 0. dp = [1, 0, 1].
- j=2: not obstacle, dp[2] = dp[2] + dp[1] = 1 + 0 = 1. dp = [1, 0, 1].

Row 2: `[0,0,0]`
- j=0: not obstacle, dp[0] = 1.
- j=1: not obstacle, dp[1] = dp[1] + dp[0] = 0 + 1 = 1. dp = [1, 1, 1].
- j=2: not obstacle, dp[2] = dp[2] + dp[1] = 1 + 1 = 2. dp = [1, 1, 2].

Result: `dp[2] = 2`. Correct.

### Phase 3: Coding (14:00–33:00)

**Candidate:** I'll implement the 1D DP version.

```java
class Solution {
    public int uniquePathsWithObstacles(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        if (grid[0][0] == 1 || grid[m - 1][n - 1] == 1) return 0;

        int[] dp = new int[n];
        dp[0] = 1;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1) {
                    dp[j] = 0;
                } else if (j > 0) {
                    dp[j] += dp[j - 1];
                }
            }
        }
        return dp[n - 1];
    }
}
```

**Interviewer:** Why do you check `grid[0][0] == 1` separately at the start?

**Candidate:** Because if the start is blocked, the robot can't even begin. We could also handle this in the loop — `dp[0]` would be set to 0 at the first row, first column — but the early return makes the intent explicit and avoids unnecessary computation.

Similarly, if the destination is blocked, no path can reach it, so we return 0 early.

**Interviewer:** What happens if `m = 1` (single row)?

**Candidate:** The loop still works correctly. For a single row, `dp[0] = 1`. For each column j > 0:
- If obstacle: `dp[j] = 0`.
- Else: `dp[j] += dp[j-1]`.

This essentially becomes a walk along a line: if there's an obstacle, all cells to its right are unreachable (since dp becomes 0 and stays 0). Actually wait — for a single row, you can only move right. If there's an obstacle at position k, cells 0..k-1 have 1 path each, and cells k..n-1 have 0 paths. The DP handles this because once dp is 0, addition of 0 from the left keeps it 0.

### Phase 4: Edge Cases & Follow-ups (33:00–45:00)

**Interviewer:** What edge cases should we test?

**Candidate:**
1. Single cell, no obstacle → 1 path.
2. Single cell, obstacle → 0 paths.
3. Single row, no obstacles → 1 path (just move right all the way).
4. Single column, no obstacles → 1 path (just move down).
5. Obstacle in first cell → return 0 early.
6. Obstacle in last cell → return 0 early.
7. Obstacle blocking all paths — e.g., entire first row filled with obstacles (except start).

**Interviewer:** How would you modify this if the robot could also move diagonally?

**Candidate:** That adds `dp[i-1][j-1]` to the recurrence:
`dp[i][j] = dp[i-1][j] + dp[i][j-1] + dp[i-1][j-1]`

The 1D optimization becomes trickier because we need to track `dp[i-1][j-1]` which is the old `dp[j-1]` before it was updated for the current row. We'd need to save it:

```java
for (int j = 0; j < n; j++) {
    int old = dp[j];        // dp[i-1][j]
    if (grid[i][j] == 1) {
        dp[j] = 0;
    } else {
        if (j > 0) dp[j] += dp[j-1];  // + left
        if (savedDiag != -1) dp[j] += savedDiag;  // + diagonal
    }
    savedDiag = old;  // becomes dp[i-1][j-1] for next column
}
```

**Interviewer:** What if the grid had different costs for each cell?

**Candidate:** Then it becomes a shortest-path problem — we'd use the same DP but with a min operation instead of sum:

```java
dp[j] = grid[i][j] + Math.min(dp[j], dp[j-1]);
```

This is the minimum path sum problem. The 1D optimization still works.

**Interviewer:** Excellent. That's a comprehensive answer.

---

## Key Takeaways

| Topic | Insight |
|-------|---------|
| 2D → 1D DP Reduction | Since dp[i][j] depends only on dp[i-1][j] and dp[i][j-1], a 1D array suffices |
| Obstacle Handling | Set dp[j] = 0 when obstacle encountered; propagates correctly |
| Early Termination | Check start/end obstacles immediately |
| Generalization | Min-cost path and diagonal movement are simple extensions |
