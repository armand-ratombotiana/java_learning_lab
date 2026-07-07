# How Suffix Arrays Work

## Prefix-Doubling Construction

Initialize rank[i] = s[i] (character code). For k = 1; k < n; k *= 2:
- Create pairs (rank[i], rank[i+k]) for each suffix i.
- Sort suffixes by these pairs. Use counting sort for O(n) per iteration.
- Assign new ranks: compare consecutive pairs, assign same rank if equal.
When all ranks are unique, sorting is complete.

Implementation detail: handle suffixes where i+k >= n by assigning a sentinel rank of -1, which sorts before all valid characters.

## Kasai's LCP Algorithm

Given SA (suffix array) and the text S:
- Compute invSA: invSA[SA[i]] = i (the position of each suffix in the sorted order).
- Initialize k = 0.
- For each suffix starting at i (0 to n-1):
  - Let rank = invSA[i].
  - If rank == n-1 (last suffix), set k = 0 and continue.
  - Let j = SA[rank+1] (the next suffix in sorted order).
  - While S[i+k] == S[j+k], increment k.
  - Set LCP[rank] = k.
  - If k > 0, decrement k (key optimization: LCP[i-1] >= LCP[i] - 1).
This runs in O(n) because k only decreases at most n times total.

## Pattern Matching

Given pattern P, find the range [lo, hi) of SA indices where suffixes start with P:
- Binary search for the first suffix >= P lexicographically (lower bound).
- Binary search for the first suffix > P lexicographically (upper bound).
- All indices in [lo, hi) are occurrence positions.
Optimization: use LCP to accelerate comparisons (LCP-enhanced binary search).

## Longest Repeated Substring

Find max LCP[i]; the substring S[SA[i]:SA[i]+LCP[i]] is the longest repeated substring. Multiple maxima indicate ties. For non-overlapping repeats, additional checking of SA[i] and SA[i-1] positions is needed.

## Distinct Substrings Count

Total distinct substrings = n*(n+1)/2 - sum(LCP[0..n-2]). Reasoning: each suffix contributes n - SA[i] substrings (all prefixes of that suffix), but LCP[i] of those are already counted from the previous suffix. The sum of LCP array subtracts all duplicates exactly once.