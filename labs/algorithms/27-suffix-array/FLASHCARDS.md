# Flashcards — Suffix Array

Q: What is a suffix array?
A: Sorted array of starting indices of all suffixes

Q: Prefix-doubling complexity?
A: O(n log^2 n) naive, O(n log n) with radix sort

Q: Kasai's LCP algorithm complexity?
A: O(n)

Q: What does LCP[i] store?
A: Longest common prefix between SA[i] and SA[i-1]

Q: Distinct substrings formula?
A: n(n+1)/2 - sum(LCP)

Q: Pattern matching with suffix array?
A: Binary search on SA for range where suffixes start with pattern

Q: Longest repeated substring?
A: Find max value in LCP array

Q: Burrows-Wheeler transform uses?
A: Compressed full-text indexing (FM-index)