# How Exact Exponential Algorithms Work

## Meet-in-the-Middle for Subset Sum

Given set S of n integers and target T:
- Split S into S1 (first n/2 elements) and S2 (last n/2 elements).
- Enumerate all subset sums of S1: list1 = [sum(mask) for all masks 0..2^{n/2}].
- Enumerate all subset sums of S2: list2 = [sum(mask) for all masks 0..2^{n/2}].
- Sort list2. For each s1 in list1, binary search list2 for T - s1.
- If found, sum of the two subsets equals T. Time: O(2^{n/2} * log(2^{n/2})) = O(2^{n/2} * n).

Optimization: also store the mask for reconstruction. For counting subsets that sum to T, use hash maps instead of sorted lists and binary search.

## Inclusion-Exclusion for Counting

To count the number of elements of a set that do NOT have any of k forbidden properties:
- total = 0. For each subset F of forbidden properties:
  - Count elements that have ALL properties in F (intersection).
  - If |F| is odd, subtract the count; if even, add.
- The result is the count of elements that avoid all forbidden properties.
This generalizes to counting matchings, Hamiltonian cycles, and colorings.

## Fast Subset Convolution

Given functions f and g defined on subsets:
- For k = 0 to n: compute f_k[S] = f[S] if |S| == k else 0. Similarly for g_k.
- For each k: compute zeta transform of f_k: F_k[S] = sum_{T subseteq S} f_k[T].
- For each k: compute zeta transform of g_k: G_k[S] = sum_{T subseteq S} g_k[T].
- For each k, j: h_{k+j}[S] += F_k[S] * G_j[S] (pointwise multiplication).
- For each k: apply Moebius transform (inverse zeta) to h_k to recover subset convolution.
Total: O(n^2 * 2^n) operations.