# Interview Questions on Combinatorics

## Easy

1. Compute factorial iteratively and recursively.
2. Count the number of ways to climb $n$ stairs (1 or 2 steps).
3. Count anagrams of a word with repeated letters.

## Medium

4. Generate all permutations of a string.
5. Generate all combinations of $k$ elements from $n$.
6. Compute $n$ choose $k$ without overflow.
7. Count ways to make change (coin change problem).

## Hard

8. Generate all valid parentheses (Catalan numbers).
9. Count derangements (subfactorial).
10. Solve "N-Queens" counting all solutions.
11. Implement the "k-th permutation" (Leetcode 60).

## Java: k-th Permutation

```java
public static String kthPermutation(int n, long k) {
    List<Integer> nums = new ArrayList<>();
    for (int i = 1; i <= n; i++) nums.add(i);
    StringBuilder result = new StringBuilder();
    k--; // 0-indexed
    while (!nums.isEmpty()) {
        long fact = factorial(nums.size() - 1);
        int index = (int)(k / fact);
        result.append(nums.remove(index));
        k %= fact;
    }
    return result.toString();
}
```

## Java: Coin Change (Counting Ways)

```java
public static int countWays(int[] coins, int amount) {
    int[] dp = new int[amount + 1];
    dp[0] = 1;
    for (int coin : coins)
        for (int i = coin; i <= amount; i++)
            dp[i] += dp[i - coin];
    return dp[amount];
}
```
