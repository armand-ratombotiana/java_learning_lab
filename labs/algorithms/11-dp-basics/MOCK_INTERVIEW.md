# Mock Interview: Dynamic Programming Basics (Climbing Stairs)

## Meta Information

| Aspect | Detail |
|--------|--------|
| Company | Google |
| Level | L4 / Senior SWE |
| Problem | Climbing Stairs (LeetCode 70) |
| Duration | 45 minutes |
| Paradigm | Dynamic Programming |

---

## Transcript

### Phase 1: Problem Understanding (0:00–5:00)

**Interviewer:** Let's start with a classic problem. You're climbing a staircase with `n` steps. You can take either 1 step or 2 steps at a time. How many distinct ways can you climb to the top?

**Candidate:** I want to make sure I understand the problem correctly. So if `n = 1`, there's only 1 way: a single step. If `n = 2`, we could do 1+1 or directly 2, so 2 ways. Is that correct?

**Interviewer:** Exactly right.

**Candidate:** Are there any constraints on `n` I should know about? Upper bound? Can `n` be zero?

**Interviewer:** `n` is between 1 and 45 inclusive.

**Candidate:** Great, so we don't need to worry about integer overflow since the result fits in a 32-bit integer. Let me think about the recurrence.

### Phase 2: Approach Design (5:00–12:00)

**Candidate:** Let me start with a brute-force approach. At each step, I can either take 1 or 2 steps. This naturally forms a recursion tree. For step `i`, the number of ways is `ways(i-1) + ways(i-2)` — because from step `i`, you must have come from either `i-1` or `i-2`.

**Interviewer:** What would the time complexity of that naive recursion be?

**Candidate:** O(2^n) — exponential, because each call branches into two, and the recursion depth is up to `n`. That's unacceptable for `n = 45`.

**Interviewer:** How would you optimize?

**Candidate:** This has overlapping subproblems. For example, to compute `ways(5)`, we need `ways(4)` and `ways(3)`, but `ways(4)` also needs `ways(3)`. We're computing `ways(3)` multiple times.

The solution is DP — either memoization (top-down) or tabulation (bottom-up).

**Memoization approach:**
- Create a memo array of size `n+1`, initialized to 0.
- Define a recursive function `dfs(i)` that returns the number of ways to reach step `i`.
- Base cases: `dfs(0) = 1`, `dfs(1) = 1`.
- Recursive case: `dfs(i) = dfs(i-1) + dfs(i-2)` — but before computing, check if `memo[i]` already has a value.
- Time: O(n), Space: O(n).

**Tabulation approach:**
- Build a `dp` array bottom-up.
- `dp[0] = 1`, `dp[1] = 1`.
- For `i` from `2` to `n`: `dp[i] = dp[i-1] + dp[i-2]`.
- Return `dp[n]`.
- Time: O(n), Space: O(n).

**Interviewer:** Can we reduce the space further?

**Candidate:** Yes! Notice that `dp[i]` only depends on `dp[i-1]` and `dp[i-2]`. We don't need the entire array — just the last two values.

**Space-optimized approach:**
- Keep two variables: `prev = 1` (representing `dp[0]` or `dp[i-2]`) and `curr = 1` (representing `dp[1]` or `dp[i-1]`).
- For `i` from `2` to `n`: `next = prev + curr`, then shift: `prev = curr`, `curr = next`.
- Return `curr`.
- Time: O(n), Space: O(1).

**Interviewer:** Good. This is essentially the Fibonacci sequence.

**Candidate:** Exactly. `ways(n) = Fib(n+1)` where `Fib(1) = 1, Fib(2) = 1`. In fact, if we use Binet's formula, we could even get O(log n) with matrix exponentiation, but the O(n) solution is more practical and expected for this problem.

### Phase 3: Coding (12:00–35:00)

**Candidate:** I'll implement the space-optimized version since it's the most efficient.

```java
class Solution {
    public int climbStairs(int n) {
        if (n <= 1) return 1;
        int prev = 1, curr = 1;
        for (int i = 2; i <= n; i++) {
            int next = prev + curr;
            prev = curr;
            curr = next;
        }
        return curr;
    }
}
```

**Interviewer:** Walk me through the edge cases.

**Candidate:** For `n = 1`, the loop doesn't execute, and we return `curr = 1`. For `n = 2`, we do one iteration: `next = 2`, `prev = 1`, `curr = 2`, return `2`. This matches our expected values.

### Phase 4: Testing & Complexity (35:00–45:00)

**Interviewer:** Let's test with `n = 5`.

**Candidate:** Starting state: `prev = 1`, `curr = 1`.

- `i = 2`: `next = 2`, `prev = 1`, `curr = 2`.
- `i = 3`: `next = 3`, `prev = 2`, `curr = 3`.
- `i = 4`: `next = 5`, `prev = 3`, `curr = 5`.
- `i = 5`: `next = 8`, `prev = 5`, `curr = 8`.

Return `8`. Let's verify manually: `n = 5`:
- 1+1+1+1+1
- 1+1+1+2
- 1+1+2+1
- 1+2+1+1
- 2+1+1+1
- 1+2+2
- 2+1+2
- 2+2+1

That's 8 ways. Correct.

**Interviewer:** What's the time and space complexity?

**Candidate:** Time is O(n) — a single loop up to `n`. Space is O(1) — only two integer variables regardless of input size.

**Interviewer:** Could you write a version that works for very large `n`, say `n = 10^12`?

**Candidate:** For that we'd need matrix exponentiation. The recurrence can be expressed as:

```
[Fib(n+1)]   = [1 1]^n * [1]
[Fib(n)  ]     [1 0]     [1]
```

Using fast exponentiation (exponentiation by squaring), this becomes O(log n). I'd implement it as:

```java
public int climbStairs(int n) {
    long[][] M = {{1, 1}, {1, 0}};
    long[][] result = matrixPow(M, n);
    return (int) result[0][0];
}
```

But for the constraints given (n ≤ 45), the iterative O(n) O(1) approach is ideal.

**Interviewer:** Excellent. One more thing — what if instead of 1 or 2 steps, you could take steps from an arbitrary set `{a, b, c, ...}`? How would the solution change?

**Candidate:** The recurrence generalizes to `dp[i] = sum(dp[i - step]) for each step in steps where step <= i`. The space-optimized approach wouldn't work as cleanly because `dp[i]` now depends on more than just the previous two values. We'd need the full O(n) array, or at least an O(maxStep) sliding window. Time becomes O(n * k) where `k` is the number of allowed step sizes.

**Interviewer:** That's a thoughtful answer. I think we're done here.

---

## Key Takeaways

| Topic | Insight |
|-------|---------|
| DP Recognition | Optimal substructure (solution depends on subproblems) + overlapping subproblems (same subproblem computed multiple times) |
| Space Optimization | When recurrence only looks back `k` values, keep only those `k` values — reduces O(n) to O(k) |
| Generalization | Climbing Stairs = Fibonacci. Generalized: coin-change-like DP with limited steps |
| Matrix Exponentiation | O(log n) alternative for large `n` using linear algebra |
