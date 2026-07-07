# Suffix Array — Mathematical Foundation

## Lexicographic Order

String S is lexicographically less than T if at the first position where they differ, S[i] < T[i], or if S is a proper prefix of T. Lexicographic order is a total order over strings. The suffix array exploits this: suffixes are sorted by lexicographic order.

## Prefix-Doubling Correctness

Let rank_k[i] be the rank of suffix i when sorted by the first k characters. The pair (rank_k[i], rank_k[i+k]) determines the order by the first 2k characters. If rank_k[i] != rank_k[j], their order is already determined by the first k characters. If rank_k[i] == rank_k[j], then we need the next k characters, which is rank_k[i+k]. After log n iterations, k >= n, and all suffixes are sorted.

## LCP Array Combinatorics

The number of distinct substrings of a string S is n(n+1)/2 - sum_i LCP[i]. Each suffix contributes n - SA[i] substrings (all its prefixes). The total number of substrings counting duplicates is n(n+1)/2. Each duplicate is counted exactly when a suffix shares a prefix with the previous suffix in sorted order. The LCP value is the length of that shared prefix, so subtracting it removes duplicates.

## Kasai's Algorithm Correctness

For suffixes starting at i and i-1: let L = LCP[invSA[i]]. Then L >= LCP[invSA[i-1]] - 1. This is because if suffix i-1 shares a prefix of length L with the next suffix j, then suffix i (starting one position later) shares a prefix of length at least L-1 with the corresponding next suffix (starting one position after j). This inequality enables the linear-time algorithm.