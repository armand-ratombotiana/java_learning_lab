# Code Deep Dive — String Matching

## KMP Implementation Details

The prefix function is the heart of KMP. It computes for each prefix of the pattern the longest proper prefix that is also a suffix. The iterative computation uses previously computed values: if pi[i-1] = j and pattern[i] != pattern[j], we try pi[j-1], repeatedly until a match or j=0.

The search phase maintains j as the length of the current match. On a character match, both i and j advance. On mismatch, j is reduced using pi, keeping i at the current text position. This guarantees linear time because each text character is examined exactly once.

## Boyer-Moore Dual Heuristics

The bad character table stores the last occurrence of each character in the pattern. On mismatch at pattern position j with text character c, the shift is max(1, j - badChar[c]). This heuristic handles most real-world cases efficiently.

The good suffix table is more complex. For each position j, it computes the smallest shift that aligns the currently matched suffix with another occurrence of that suffix in the pattern. The combined shift is the maximum of the two heuristics.

## Rabin-Karp Hash Selection

Choosing B and M is critical. A common choice is B=31 or 131 (large primes) and M as a large prime like 10^9+7 or using long arithmetic to allow natural overflow (modulo 2^64). Java's default hashCode for strings uses B=31.

## Z-Array Algorithm

The Z-array computation maintains the interval [L, R] where Z[L] = R-L+1 and T[L..R] is a prefix of the combined string. For position i within this interval, Z[i] can be initialized from Z[i-L], clamped to the remaining interval. This guarantees O(n) total time.

## Aho-Corasick Construction

Building the automaton requires three steps: (1) insert all patterns into a trie, (2) compute failure links using BFS, and (3) compute output links that efficiently report all matches at each node. Memory can be optimized by storing transitions as arrays of size alphabet (fast) or hash maps (compact for sparse tries).
