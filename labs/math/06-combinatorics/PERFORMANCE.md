# Combinatorics Performance

## Precomputation (Dynamic Programming)

```java
// Precompute all binomial coefficients up to n
public static long[][] precomputeBinomial(int n) {
    long[][] C = new long[n + 1][n + 1];
    for (int i = 0; i <= n; i++) {
        C[i][0] = C[i][i] = 1;
        for (int j = 1; j < i; j++)
            C[i][j] = C[i - 1][j - 1] + C[i - 1][j];
    }
    return C;
}
```

## Avoid Recomputation with Memoization

```java
// Map of computed values
Map<String, Long> memo = new HashMap<>();
public static long combinationsMemo(int n, int k) {
    if (k == 0 || k == n) return 1;
    String key = n + "," + k;
    return memo.computeIfAbsent(key, k2 ->
        combinationsMemo(n - 1, k - 1) + combinationsMemo(n - 1, k));
}
```

## Parallel Generation

```java
// Parallel subset generation using bit masks
IntStream.range(0, 1 << n).parallel()
    .forEach(mask -> processSubset(mask));
```

## Big O of Common Operations

| Operation | Time Complexity |
|-----------|----------------|
| Factorial $n!$ | $O(n)$ |
| Binomial multiplicative | $O(k)$ |
| Pascal's triangle $n \times n$ | $O(n^2)$ |
| Generate all permutations | $O(n!)$ |
| Generate all subsets | $O(2^n)$ |
