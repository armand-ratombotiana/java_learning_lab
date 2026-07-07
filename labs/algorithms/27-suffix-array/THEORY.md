# Suffix Array &amp; LCP Array — Theoretical Foundation

## Suffix Array Definition

Given a string S of length n, the suffix array SA is a permutation of the indices 0..n-1 such that S[SA[i]:] (the suffix starting at SA[i]) is lexicographically smaller than S[SA[i+1]:] for all i. In other words, SA contains the starting positions of all suffixes sorted lexicographically. The suffix array can be constructed in O(n log n) time using prefix-doubling, or in O(n) time using SA-IS or induced sorting.

## Prefix-Doubling Algorithm

The prefix-doubling algorithm sorts suffixes by their first k characters, then doubles k in each iteration. Initially, suffixes are sorted by their first character (ranked by character value). In each step, each suffix is assigned a pair (rank[i], rank[i+k]) where rank is the current rank. Sorting these pairs produces the ordering for the next iteration. After log n iterations, the suffixes are fully sorted.

## Kasai's LCP Algorithm

The LCP array stores the length of the longest common prefix between consecutive suffixes in the suffix array. Kasai's algorithm computes LCP in O(n) time using the observation that lcp(i, j) >= lcp(i-1, j-1) - 1 when suffixes are processed in order of decreasing starting index. The algorithm maintains the previous LCP value and decrements it as needed when advancing through the suffix array.

## Applications

Pattern matching: Given a pattern P, find all occurrences in S by binary searching the suffix array for the range of suffixes that start with P. Complexity: O(m log n) or O(m + log n) with LCP-accelerated search.

Longest repeated substring: Find the maximum element in the LCP array. The substring S[SA[i]:SA[i]+LCP[i]] for the i with maximum LCP[i] is the answer.

Distinct substrings: The total number of distinct substrings is n(n+1)/2 - sum(LCP). Each LCP[i] counts suffixes that share a prefix with the previous suffix, thus overlapping substrings already counted.

## Suffix Array vs Suffix Tree

The suffix array is a space-efficient alternative to the suffix tree (which stores O(n) nodes). The suffix array uses O(n) integers compared to the suffix tree's O(n) nodes with pointers. Many suffix tree operations can be simulated on a suffix array combined with the LCP array and RMQ data structures.