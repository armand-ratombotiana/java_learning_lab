# Mock Interview: Knapsack DP (Coin Change)

## Meta Information

| Aspect | Detail |
|--------|--------|
| Company | Meta |
| Level | E5 / Senior SWE |
| Problem | Coin Change (LeetCode 322) |
| Duration | 45 minutes |
| Paradigm | Unbounded Knapsack DP |

---

## Transcript

### Phase 1: Problem Understanding (0:00–6:00)

**Interviewer:** We have an array of coin denominations and a target amount. We need the minimum number of coins to make that amount. If it's impossible, return -1. You can use each coin unlimited times.

**Candidate:** Let me clarify a few things. The coins array — are all denominations positive integers? Can they be unsorted? And is the amount guaranteed to be non-negative?

**Interviewer:** Denominations are positive integers, array isn't necessarily sorted, amount is non-negative.

**Candidate:** Got it. So for `coins = [1, 2, 5]`, `amount = 11`, the answer is `3` because `5 + 5 + 1 = 11`. For `coins = [2]`, `amount = 3`, it should be `-1`. Let me think about the appropriate approach.

**Interviewer:** What's your initial thought?

**Candidate:** This looks like a textbook unbounded knapsack DP problem. The key properties:

1. **Optimal substructure**: The minimum coins for amount `i` depends on the minimum coins for `i - coin` for each coin denomination.
2. **Overlapping subproblems**: Computing coin change for `i` repeatedly involves sub-amounts that are reused.
3. **Unbounded nature**: Each coin can be used unlimited times — unlike 0/1 knapsack where each item is used at most once.

### Phase 2: Approach Design (6:00–14:00)

**Candidate:** Let me walk through my reasoning from brute force to optimal.

**Brute force — recursive enumeration:**
Try every combination of coins recursively. For `amount = 11` with `coins = [1, 2, 5]`, we'd recursively try subtracting each coin and finding the min of the subproblems. Time: O(k^n) where `k` = number of coins — exponential.

**Greedy approach?**
We might be tempted to always pick the largest coin first. For standard US coin denominations `[1, 5, 10, 25]`, greedy works: make change by always picking the largest possible coin. But this fails in the general case. For `coins = [1, 3, 4]`, `amount = 6`: greedy picks `4 + 1 + 1 = 3` coins, but optimal is `3 + 3 = 2` coins.

**DP — top-down with memoization:**
Define `f(rem)` = minimum coins to make `rem`. Recurrence: `f(rem) = min(1 + f(rem - coin))` for each coin. Base: `f(0) = 0`. If `rem < 0`, return `-1`. Memoize results. Complexity: O(amount * coins.length) time, O(amount) space.

**DP — bottom-up tabulation:**
`dp[i]` = min coins to make amount `i`. Initialize `dp[0] = 0`, all others to `amount + 1` (sentinel for "impossible"). For each amount `i` from 1 to `amount`, for each `coin <= i`, `dp[i] = min(dp[i], dp[i - coin] + 1)`. Return `dp[amount]` if `<= amount`, else `-1`.

Complexity: O(amount * coins.length) time, O(amount) space.

**Interviewer:** Is there a way to reduce the constant factor?

**Candidate:** We could sort coins and break early when `coin > i`. We could also use BFS since this is a shortest-path problem in a graph where nodes are amounts and edges are coins. BFS would find the minimum steps, but its worst-case complexity is similar.

### Phase 3: Coding (14:00–33:00)

**Candidate:** I'll implement the bottom-up tabulation — it's cleaner and avoids recursion stack concerns.

```java
class Solution {
    public int coinChange(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1);
        dp[0] = 0;

        for (int i = 1; i <= amount; i++) {
            for (int coin : coins) {
                if (coin <= i) {
                    dp[i] = Math.min(dp[i], dp[i - coin] + 1);
                }
            }
        }

        return dp[amount] > amount ? -1 : dp[amount];
    }
}
```

**Interviewer:** Why did you initialize `dp` with `amount + 1` specifically?

**Candidate:** `amount + 1` is a safe sentinel that's larger than any possible answer. The worst-case scenario is using all 1-cent coins, which would take exactly `amount` coins. So `amount + 1` is one more than the theoretical maximum, serving as our "infinity" value. If after processing, `dp[amount]` is still `amount + 1`, no combination was found.

**Interviewer:** Is there a subtle bug if `amount = 0`?

**Candidate:** Good catch. For `amount = 0`, the loop `for (i = 1; i <= 0; i++)` doesn't execute, and `dp[0] = 0` is returned. That's correct — no coins needed.

**Interviewer:** What about the order of loops? You're iterating amount first, then coins. Does it matter?

**Candidate:** For unbounded knapsack where order of items doesn't matter (we just want min count, not combinations), the loop order isn't critical as long as we always look back at `dp[i - coin]` which is already computed. Since we iterate `i` from 1 to amount, and `coin <= i`, `dp[i - coin]` is always known.

### Phase 4: Testing & Edge Cases (33:00–45:00)

**Candidate:** Let me trace through `coins = [1, 2, 5], amount = 11`:

```
dp[0] = 0
i=1: coin=1 → dp[1] = min(12, 0+1) = 1
i=2: coin=1 → dp[2] = min(12, 1+1) = 2; coin=2 → dp[2] = min(2, 0+1) = 1
i=3: coin=1 → dp[3] = min(12, 1+1) = 2; coin=2 → dp[3] = min(2, 1+1) = 2
i=4: coin=1 → 3; coin=2 → dp[2]+1=2 → dp[4]=2
i=5: coin=1 → 3; coin=2 → 3; coin=5 → dp[0]+1=1 → dp[5]=1
i=6: coin=1 → 2; coin=2 → dp[4]+1=3; coin=5 → dp[1]+1=2 → dp[6]=2
i=7: coin=1 → 3; coin=2 → dp[5]+1=2; coin=5 → dp[2]+1=2 → dp[7]=2
i=8: coin=1 → 3; coin=2 → dp[6]+1=3; coin=5 → dp[3]+1=3 → dp[8]=3
i=9: coin=1 → 4; coin=2 → dp[7]+1=3; coin=5 → dp[4]+1=3 → dp[9]=3
i=10: coin=1 → 4; coin=2 → dp[8]+1=4; coin=5 → dp[5]+1=2 → dp[10]=2
i=11: coin=1 → 3; coin=2 → dp[9]+1=4; coin=5 → dp[6]+1=3 → dp[11]=3
```

Returns 3. Correct.

**Edge cases to test:**
- `amount = 0` → returns 0.
- Single coin equal to amount → returns 1.
- No coin equals 1, amount small → `coins=[2], amount=3` → dp[1] stays sentinel, dp[2]=1, dp[3]=dp[1]+1 fails → returns -1.
- Large amount with optimal > 1 coin → `coins=[2,5,10,25], amount=99` → optimal is `25*3 + 10*2 + 2*2 = 7`.

**Interviewer:** How would you modify this if each coin could only be used once?

**Candidate:** That becomes the 0/1 knapsack variant. We'd reverse the loops: iterate coins in the outer loop and amount in the inner loop going backwards (`for i from amount down to coin`). This prevents reusing the same coin:

```java
int[] dp = new int[amount + 1];
Arrays.fill(dp, amount + 1);
dp[0] = 0;
for (int coin : coins) {
    for (int i = amount; i >= coin; i--) {
        dp[i] = Math.min(dp[i], dp[i - coin] + 1);
    }
}
```

**Interviewer:** What if we need to know which coins to use, not just the count?

**Candidate:** We'd need path reconstruction. Alongside `dp[i]`, store `lastCoin[i]` — the coin used to achieve the optimal for amount `i`. Then trace backwards from `amount` to `0`:

```java
int[] lastCoin = new int[amount + 1];
Arrays.fill(lastCoin, -1);
// ... during DP update:
if (dp[i - coin] + 1 < dp[i]) {
    dp[i] = dp[i - coin] + 1;
    lastCoin[i] = coin;
}
// reconstruction:
List<Integer> used = new ArrayList<>();
for (int i = amount; i > 0; i -= lastCoin[i]) {
    used.add(lastCoin[i]);
}
```

**Interviewer:** Excellent. This covers the necessary depth.

---

## Key Takeaways

| Topic | Insight |
|-------|---------|
| Knapsack Variants | Coin order (unbounded vs 0/1) changes loop direction |
| Greedy vs DP | Greedy fails for non-canonical coin systems; DP always works |
| Sentinel Pattern | `amount + 1` as infinity since max coins ≤ amount |
| Path Reconstruction | Store choice alongside optimal value |
