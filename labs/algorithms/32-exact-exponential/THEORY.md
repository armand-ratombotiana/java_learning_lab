# Exact Exponential Algorithms — Theoretical Foundation

## Motivation

Many important problems (SAT, vertex cover, subset sum, TSP) are NP-hard, meaning no polynomial-time algorithm is known. However, brute force O(2^n) is often too slow. Exact exponential algorithms aim to reduce the base of the exponential, achieving running times like O(1.618^n) or O(2^{n/2}) through clever combinatorial reasoning.

## Meet-in-the-Middle

Meet-in-the-middle splits the input into two halves, enumerates all possibilities for each half, and then combines results using efficient data structures (sorting + two-pointer or hash maps). For subset sum: split the n elements into two groups of size n/2, enumerate all 2^{n/2} subset sums for each group, sort one list, and for each sum in the other list, search for target - sum using binary search. This gives O(2^{n/2}) time and space.

## Inclusion-Exclusion Principle

The inclusion-exclusion principle counts the union of sets: |A union B| = |A| + |B| - |A intersect B|. For exact algorithms, this can be used to count objects avoiding a set of forbidden configurations. The principle generalizes to alternating sums over all subsets of constraints: |union A_i| = sum_{nonempty J} (-1)^{|J|+1} |intersection_{i in J} A_i|.

## Fast Subset Convolution

Subset convolution computes (f * g)(S) = sum_{T subseteq S} f(T) * g(S \ T) for all subsets S. Naive computation takes O(3^n). Fast subset convolution uses the zeta transform and ranked zeta transform to achieve O(n^2 * 2^n). This technique is used in DP over subsets where the transition splits the subset into two disjoint parts.

## Split-and-List

Split-and-list generalizes meet-in-the-middle. The input is split into k parts, and each part is enumerated independently. The results are combined by solving a k-sum problem. For many problems, k = 2 (meet-in-the-middle) gives the best trade-off, but some problems benefit from k &gt; 2 with appropriate data structures.

## Branching Algorithms

Branching algorithms solve NP-hard problems by recursively reducing the problem size. The key is the branching number: if a branching rule creates subproblems of size n - a_1, ..., n - a_k, the recurrence T(n) = T(n - a_1) + ... + T(n - a_k) yields a solution c^n where c is the unique positive root. For vertex cover, the standard branching on the highest-degree vertex gives O(1.618^n). More sophisticated rules achieve O(1.199^n).