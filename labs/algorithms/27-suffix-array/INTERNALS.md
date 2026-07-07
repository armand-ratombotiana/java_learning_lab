# Suffix Array — Internal Implementation Details

The SuffixArray class uses prefix-doubling with an inner loop that sorts by pairs (rank[i], rank[i+k]). The implementation uses Java Arrays.sort with a custom comparator on pairs encoded as integers or using explicit (first, second) comparisons.

Key internal structures:
- rank[]: current rank of each suffix (integer from 0 to n-1, where lower = lexicographically smaller).
- tmp[]: new rank array for the next iteration.
- sa[]: the suffix array being constructed.
- k: current prefix length (doubles each iteration: 1, 2, 4, ..., >= n).

For each iteration, suffixes are sorted by (rank[i], rank[i+k]). If i+k >= n, the second component is treated as -1 (smaller than any valid rank). After sorting, new ranks are assigned: if two consecutive sorted suffixes have identical pairs, they receive the same rank.

The sorting step is O(n log n) per iteration, and there are O(log n) iterations, giving O(n log^2 n) total. This can be optimized to O(n log n) by using counting sort for the pairs.

The LCPArray class implements Kasai's algorithm. It uses invSA[] (inverse of SA: invSA[SA[i]] = i) and the original text as a char[]. The core loop iterates through original positions i = 0..n-1, computing LCP between the suffix starting at i and the suffix starting at j = SA[rank[i] + 1]. The key optimization maintains k (the current LCP value) and decrements it by at most 1 per iteration.

Pattern matching uses binary search on SA. The compare function avoids creating substrings: it directly compares characters of the pattern with characters of the suffix at SA[mid].