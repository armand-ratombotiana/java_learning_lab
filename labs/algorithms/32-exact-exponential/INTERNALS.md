# Exact Exponential Algorithms — Internal Implementation Details

The MeetInTheMiddle class for subset sum: splits the input array into two halves (first n/2, rest). For each half, generates all subset sums using bitmask iteration (0 to 2^{k}-1). Stores results in two long arrays. Sorts the second array. For each sum in the first array, binary searches the second for target - sum. Uses Java Arrays.binarySearch. Returns both the found sum and reconstructs the subset.

The InclusionExclusion class uses alternating sums over subsets. For counting subsets of size k in a bipartite graph with no edges (independent set), the inclusion-exclusion formula sums over all edge subsets. Implementation iterates over all 2^m subsets of edges.

The FastSubsetConvolution class implements the ranked zeta transform. For n up to 20 (2^n = 1,048,576), it allocates arrays of size (n+1) x (1<<n). The zeta transform loops: for each bit i, for each mask, if (mask & (1<<i)) != 0, then f[mask] += f[mask ^ (1<<i)]. The ranked version adds the constraint that only subsets of specific sizes are considered, using a 2D array f[size][mask].

Internal memory optimization: the ranked zeta transform can use a single 2D array where dimension 0 is size and dimension 1 is mask, iterating in a specific order. The Moebius transform (inverse) similarly loops but subtracts instead of adds.

The branching algorithm for vertex cover uses the standard branching rule: pick edge (u,v). Branch 1: include u (remove u and incident edges). Branch 2: include v (remove v and incident edges). The recurrence T(n) = T(n-1) + T(n-1) gives O(2^n). With the degree-1 rule (if a vertex has degree 1, include its neighbor) and degree-2 rule (if degree 2, use reduction), the recurrence improves to O(1.618^n).