# Exact Exponential Algorithms — Step by Step Guide

## Step 1: Implement Meet-in-the-Middle

Split input array into two halves. For each half, generate all subset sums using bitmask loops. Store in arrays (sorted for second half). For each sum in first list, binary search for target - sum in second list. Return true if found.

## Step 2: Add Reconstruction

When a match is found, reconstruct the actual subset. Store masks alongside sums. For matched pairs, convert masks back to set elements independently for each half, then combine.

## Step 3: Test Subset Sum

Test with n=20 random values, verify against brute force for small n. Time the performance for n=40 (should be fast with meet-in-the-middle).

## Step 4: Implement Inclusion-Exclusion

Identify the set of forbidden properties. For each subset of properties (iterate 0..2^k-1), compute count of elements satisfying all selected properties. Add or subtract based on parity. The result is the count of elements avoiding all properties.

## Step 5: Implement Zeta Transform

For (int i = 0; i < n; i++) for (int mask = 0; mask < (1<<n); mask++) if ((mask & (1<<i)) != 0) f[mask] += f[mask ^ (1<<i)]. This computes F[S] = sum_{T subseteq S} f[T] in O(n*2^n).

## Step 6: Implement Fast Subset Convolution

Build ranked arrays f_k[mask] = f[mask] if |mask|==k else 0. Apply zeta to each f_k. For each pair (k,j), multiply pointwise and accumulate into h_{k+j}. Apply Moebius to each size layer of h.

## Step 7: Test Convolution

Verify that (f * g)[S] = sum_{T subseteq S} f[T] * g[S\T] on random functions. Compare with naive O(3^n) computation for n <= 12.