# Suffix Array — Common Mistakes

1. Off-by-one in prefix-doubling: the sentinel for i+k >= n should be smaller than any valid rank. Using -1 is correct, but forgetting that -1 < 0 ensures it sorts first.

2. Not handling duplicate ranks correctly: when pairs (rank[i], rank[i+k]) are identical, they must receive the same new rank. Otherwise, the algorithm never terminates.

3. LCP array indexing: LCP[i] corresponds to the LCP of suffix at SA[i] and SA[i-1], for i = 1..n-1. LCP[0] is typically undefined or set to 0. The Kasai algorithm fills lcp[invSA[i]].

4. In Kasai's algorithm, k should not exceed n - i or n - j. Array bounds checks are essential.

5. Pattern matching binary search: compare function must handle the case where the pattern is longer than the suffix. In that case, the suffix is lexicographically smaller if it is a prefix of pattern.

6. Not resetting k = 0 at the start of each Kasai iteration for the last suffix. The last suffix in sorted order has no next suffix to compare with.

7. Using String.substring in the compare function creates O(n) copies per comparison. Always compare character by character using charAt.

8. For distinct substring counting, forgetting that the formula subtracts sum of LCP, which includes both the first fake entry (0) and the real entries. Ensure LCP array indexing is correct.