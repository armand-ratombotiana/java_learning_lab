# Exact Exponential Algorithms — Visual Guide

## Meet-in-the-Middle for Subset Sum

Target T=10, set S={2,3,4,5,1}. Split: S1={2,3,4}, S2={5,1}. All sums of S1: 0(000), 2(001), 3(010), 4(100), 5(011), 6(101), 7(110), 9(111). Sorted: [0,2,3,4,5,6,7,9]. All sums of S2: 0,5,1,6. For each sum s2: binary search for 10-s2. s2=0 -> search 10 (not found), s2=5 -> search 5 (found), s2=1 -> search 9 (found). Solutions: 5+5 or 9+1. Each sum maps to a subset.

## Inclusion-Exclusion Visual

Counting permutations of {1,2,3} where 1 not before 2: total = 6. Count where 1 before 2: half = 3. Also count where 2 before 1: half = 3. But we want neither 1 before 2 NOR 2 before 1. Using inclusion-exclusion over the relation "i is before j".

## Zeta Transform Visual

f[S] for n=3: f[000]=a, f[001]=b, f[010]=c, f[011]=d, f[100]=e, f[101]=f, f[110]=g, f[111]=h. After zeta transform: F[001]=a+b, F[010]=a+c, F[011]=a+b+c+d (subset sums of {0,1,2}). The transform propagates values upward through subset inclusion.

## Fast Subset Convolution

Two functions f,g on subsets. Naive: for each S, sum over T subset of S: f[T]*g[S\T] = O(3^n). Fast: for each size k, compute ranked zeta, multiply, inverse zeta. Complexity O(n^2 * 2^n). For n=20: naive = 3.5 billion, fast = 400 million.