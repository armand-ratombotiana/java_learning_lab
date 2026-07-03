# How Dynamic Programming Works

## Fibonacci with Memoization
`java
long[] memo = new long[n + 1];
Arrays.fill(memo, -1);
memo[0] = 0; memo[1] = 1;

long fib(int n) {
    if (memo[n] != -1) return memo[n];
    memo[n] = fib(n - 1) + fib(n - 2);
    return memo[n];
}
`

## Fibonacci with Tabulation
`java
long fib(int n) {
    if (n <= 1) return n;
    long[] dp = new long[n + 1];
    dp[0] = 0; dp[1] = 1;
    for (int i = 2; i <= n; i++)
        dp[i] = dp[i - 1] + dp[i - 2];
    return dp[n];
}
`

## Space-Optimized
`java
long fib(int n) {
    if (n <= 1) return n;
    long prev2 = 0, prev1 = 1;
    for (int i = 2; i <= n; i++) {
        long curr = prev1 + prev2;
        prev2 = prev1; prev1 = curr;
    }
    return prev1;
}
`
"@

wf "INTERNALS.md" @"
# DP — Internal Mechanics

## 0/1 Knapsack
`java
int knapsack(int[] weights, int[] values, int capacity) {
    int n = weights.length;
    int[][] dp = new int[n + 1][capacity + 1];
    for (int i = 1; i <= n; i++) {
        for (int w = 1; w <= capacity; w++) {
            if (weights[i - 1] <= w)
                dp[i][w] = Math.max(dp[i - 1][w],
                    values[i - 1] + dp[i - 1][w - weights[i - 1]]);
            else
                dp[i][w] = dp[i - 1][w];
        }
    }
    return dp[n][capacity];
}
`

## Longest Common Subsequence
`java
int lcs(String s1, String s2) {
    int m = s1.length(), n = s2.length();
    int[][] dp = new int[m + 1][n + 1];
    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            if (s1.charAt(i - 1) == s2.charAt(j - 1))
                dp[i][j] = dp[i - 1][j - 1] + 1;
            else
                dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
        }
    }
    return dp[m][n];
}
`
"@

wf "MATH_FOUNDATION.md" @"
# Math Foundation for DP

## Bellman's Principle of Optimality
"An optimal policy has the property that whatever the initial state and decision, remaining decisions must constitute an optimal policy."

## Typical Recurrences
- Fibonacci: dp[i] = dp[i-1] + dp[i-2]
- Knapsack: dp[i][w] = max(dp[i-1][w], value[i] + dp[i-1][w-weight[i]])
- LCS: dp[i][j] = dp[i-1][j-1] + 1 if match, else max(dp[i-1][j], dp[i][j-1])
- Edit Distance: dp[i][j] = min(dp[i-1][j]+1, dp[i][j-1]+1, dp[i-1][j-1]+cost)

## Complexity
- Subproblems = table size
- Time = subproblems × transition cost
- Space = table size (can be optimized)
