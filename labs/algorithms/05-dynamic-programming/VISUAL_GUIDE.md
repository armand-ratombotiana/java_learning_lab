# Visual Guide — DP

## LCS Table for "ABCBDAB" × "BDCAB"
`
     Ø  B  D  C  A  B
  Ø  0  0  0  0  0  0
  A  0  0  0  0  1  1
  B  0  1  1  1  1  2
  C  0  1  1  2  2  2
  B  0  1  1  2  2  3
  D  0  1  2  2  2  3
  A  0  1  2  2  3  3
  B  0  1  2  2  3  4
`
LCS length = 4 (BCAB or BDAB)

## Knapsack DP Table
Items: (w=2,v=3), (w=3,v=4), (w=4,v=5), Capacity=5
`
     w=0 w=1 w=2 w=3 w=4 w=5
i=0   0   0   0   0   0   0
i=1   0   0   3   3   3   3
i=2   0   0   3   4   4   7
i=3   0   0   3   4   5   7
`
"@

wf "CODE_DEEP_DIVE.md" @"
# Code Deep Dive — DP

## Edit Distance (Levenshtein)
`java
public int minDistance(String word1, String word2) {
    int m = word1.length(), n = word2.length();
    int[][] dp = new int[m + 1][n + 1];
    for (int i = 0; i <= m; i++) dp[i][0] = i;
    for (int j = 0; j <= n; j++) dp[0][j] = j;
    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            if (word1.charAt(i - 1) == word2.charAt(j - 1))
                dp[i][j] = dp[i - 1][j - 1];
            else
                dp[i][j] = 1 + Math.min(dp[i - 1][j],
                    Math.min(dp[i][j - 1], dp[i - 1][j - 1]));
        }
    }
    return dp[m][n];
}
`

## Coin Change (Minimum Coins)
`java
public int coinChange(int[] coins, int amount) {
    int[] dp = new int[amount + 1];
    Arrays.fill(dp, amount + 1);
    dp[0] = 0;
    for (int i = 1; i <= amount; i++) {
        for (int coin : coins) {
            if (coin <= i)
                dp[i] = Math.min(dp[i], dp[i - coin] + 1);
        }
    }
    return dp[amount] > amount ? -1 : dp[amount];
}
`
"@

wf "STEP_BY_STEP.md" @"
# Step-by-Step — DP

## DP Problem-Solving Framework
1. Define state: What does dp[i][j] represent?
2. Find recurrence: How does dp[i][j] relate to smaller subproblems?
3. Identify base cases: Initial values?
4. Determine iteration order: Bottom-up or top-down?
5. Optimize space: Can we reduce dimensions?

## Recognizing DP Problems
- "Find minimum/maximum of something"
- "Count the number of ways to..."
- "Find longest/shortest..."
- Choices with overlapping subproblems
- Brute-force is exponential

## Common DP Patterns
- Linear DP: Fibonacci, house robber
- 2D DP: LCS, edit distance, knapsack
- Interval DP: Matrix chain multiplication
- Tree DP: Diameter of tree
- State Machine DP: Stock with cooldown
