# Exact Exponential Algorithms — Mathematical Foundation

## Meet-in-the-Middle Complexity

Split input into two halves of size n/2. Enumerate 2^{n/2} subsets for each half. Sorting one half: O(2^{n/2} * n/2). For each subset in the other half, binary search: O(2^{n/2} * log(2^{n/2})) = O(2^{n/2} * n). Total: O(2^{n/2} * n). Memory: O(2^{n/2}). This is a root-exponential improvement over O(2^n).

## Inclusion-Exclusion Formula

| union_{i=1}^n A_i | = sum_{k=1}^n (-1)^{k+1} sum_{1 <= i1 < ... < ik <= n} | intersection_{j=1}^k A_{ij} |. For counting the number of elements in NONE of the sets: sum_{S subseteq [n]} (-1)^{|S|} | intersection_{i in S} A_i |.

## Zeta and Moebius Transforms

Zeta transform: F[S] = sum_{T subseteq S} f[T]. Moebius transform (inverse): f[S] = sum_{T subseteq S} (-1)^{|S|-|T|} F[T]. Both are computed in O(n * 2^n) time using DP over bits: for each bit i, for each mask, if (mask & 1<<i) != 0, then F[mask] += F[mask ^ (1<<i)] (forward) or F[mask] -= F[mask ^ (1<<i)] (inverse).

## Subset Convolution Formula

(f * g)[S] = sum_{T subseteq S} f[T] * g[S \ T] where T and S\T are disjoint. The ranked zeta transform adds the constraint that subsets are partitioned by size: F_k[S] = sum_{T subseteq S, |T| = k} f[T]. Then (f * g)[S] = sum_{k=0}^{|S|} F_k[S] * G_{|S|-k}[S] after Moebius transform.

## ETH and SETH

Exponential-Time Hypothesis: 3-SAT cannot be solved in 2^{o(n)} time. Strong ETH: k-SAT requires 2^{n - n/k + o(n)} time. These hypotheses guide the search for optimal exponential algorithms and imply lower bounds for many problems.