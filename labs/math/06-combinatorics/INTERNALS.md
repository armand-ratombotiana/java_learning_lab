# Internals of Combinatorics

## Computing Binomial Coefficients Efficiently

### Multiplicative Formula (no overflow for large n)

```java
public static long binomial(int n, int k) {
    if (k < 0 || k > n) return 0;
    if (k > n - k) k = n - k;
    long result = 1;
    for (int i = 1; i <= k; i++)
        result = result * (n - k + i) / i;
    return result;
}
```

### Pascal's Triangle (DP)

```java
public static long[][] pascalsTriangle(int n) {
    long[][] tri = new long[n + 1][];
    for (int i = 0; i <= n; i++) {
        tri[i] = new long[i + 1];
        tri[i][0] = tri[i][i] = 1;
        for (int j = 1; j < i; j++)
            tri[i][j] = tri[i - 1][j - 1] + tri[i - 1][j];
    }
    return tri;
}
```

## Stirling Numbers of the Second Kind

Partition $n$ elements into $k$ non-empty subsets:

$$
S(n, k) = k \cdot S(n-1, k) + S(n-1, k-1)
$$

## Bell Numbers

Total partitions of an $n$-element set:

$$
B_n = \sum_{k=1}^n S(n, k)
$$
