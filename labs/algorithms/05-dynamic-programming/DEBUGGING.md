# Debugging — DP

## Print DP Table
`java
private static void printTable(int[][] dp, String[] rowLabels, String[] colLabels) {
    System.out.print("\t");
    for (String col : colLabels) System.out.print(col + "\t");
    System.out.println();
    for (int i = 0; i < dp.length; i++) {
        System.out.print(rowLabels[i] + "\t");
        for (int j = 0; j < dp[i].length; j++)
            System.out.print(dp[i][j] + "\t");
        System.out.println();
    }
}
`

## Small Test Cases
`java
assertEquals(3, editDistance("cat", "dog"));
assertEquals(0, editDistance("", ""));
assertEquals(1, editDistance("a", ""));
`
"@

wf "REFACTORING.md" @"
# Refactoring — DP

## Space Optimization
`java
// 2D → 1D for knapsack
int[] dp = new int[capacity + 1];
for (int i = 0; i < n; i++)
    for (int w = capacity; w >= weights[i]; w--)
        dp[w] = Math.max(dp[w], dp[w - weights[i]] + values[i]);
`

## Memoization → Tabulation
- Eliminates recursion overhead
- No stack overflow risk
- Easier to space-optimize
