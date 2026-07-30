# LeetCode Pattern Cheatsheet — Statistics Edition

Statistical thinking applied to coding problems.

## Pattern 1: Sliding Window (Statistics)
```
Moving average, rolling variance, exponential smoothing
→ Maintain window sum, update incrementally O(n)
```

## Pattern 2: Two Pointers (Percentiles)
```
Finding median, quartiles, percentiles
→ Sort array, use two pointers from ends for IQR
```

## Pattern 3: Prefix Sum (Moments)
```
Running mean, running variance (Welford's algorithm)
→ Update mean and variance incrementally in O(1) per element
```

## Pattern 4: Divide and Conquer (Distributions)
```
Histogram equalization, quantile computation
→ Recursively partition sorted data
```

## Pattern 5: Dynamic Programming (Probability)
```
Binomial probability, Markov chains
→ DP[n][k] = C(n,k) * p^k * (1-p)^(n-k)
```

## Pattern 6: Binary Search (Inverse CDF)
```
Find quantile given CDF, inverse transform sampling
→ Binary search on CDF to find x for given probability
```

## Pattern 7: Reservoir Sampling
```
Random sampling from stream of unknown length
→ Keep k elements, replace with probability k/i
```

## Key Algorithmic Techniques

| Technique | Use Case | Complexity |
|-----------|----------|------------|
| Welford's online algorithm | Running variance | O(n) time, O(1) space |
| Quickselect | Median/quantiles | O(n) average |
| Counting sort | Mode for bounded integers | O(n + k) |
| Boole's rule | Numerical integration (PDF→CDF) | O(n) |
