# Guide: Non-parametric Tests in Java

## Step 1: Mann-Whitney U Test
Combine both groups, rank all values, sum ranks per group. U = n₁n₂ + n₁(n₁+1)/2 - R₁.

## Step 2: Wilcoxon Signed-Rank Test
Compute differences, rank absolute differences (omit zeros), attach original signs. Sum positive and negative ranks.

## Step 3: Kruskal-Wallis Test
Rank all k groups together. Sum ranks per group. Compute H statistic. Approximate p-value using chi-square distribution (df=k-1).

## Step 4: Friedman Test
Rank within each block (row). Sum ranks per treatment. Compute Q statistic.

## Step 5: Java Implementation
```java
public static double mannWhitneyU(double[] group1, double[] group2) {
    double[] combined = combine(group1, group2);
    double[] ranks = rank(combined);
    double r1 = sum(ranks, 0, group1.length);
    double n1 = group1.length, n2 = group2.length;
    return r1 - n1 * (n1 + 1) / 2;
}
```

## Test Cases
- Group A: {2,4,6,8}, Group B: {1,3,5,7} → U = 10 (no significant difference)
- Paired: before={5,6,7,8}, after={4,3,5,6} → Wilcoxon W identifies difference
- Three groups with distinct medians → Kruskal-Wallis H significant (p < 0.05)
