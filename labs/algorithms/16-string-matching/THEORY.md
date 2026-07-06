# String Matching — Theoretical Foundation

## Exact String Matching Problem

Given a text T of length n and a pattern P of length m, find all occurrences of P in T. The naive approach checks every position O(nm), but more sophisticated algorithms achieve O(n+m) time.

## Knuth-Morris-Pratt (KMP)

KMP precomputes a prefix function pi that indicates the longest proper prefix of the pattern that is also a suffix for each position. When a mismatch occurs, the pattern shifts by more than one position using pi, avoiding redundant comparisons. The amortized linear time arises because each character in the text is compared at most twice.

## Boyer-Moore

Boyer-Moore scans the pattern from right to left and uses two heuristics. The bad character rule shifts the pattern so the mismatched text character aligns with its last occurrence in the pattern. The good suffix rule shifts the pattern so the matched suffix reoccurs elsewhere in the pattern. Together, these heuristics can skip large portions of the text, giving sublinear performance on average.

## Rabin-Karp

Rabin-Karp uses a rolling hash function to compute the hash of each window of the text. It compares the pattern hash with each window hash. On a hash match, it verifies character-by-character to avoid false positives. Expected time is O(n+m) with a good hash function; worst case is O(nm) if many hash collisions occur.

## Z-Algorithm

The Z-algorithm computes an array Z where Z[i] is the length of the longest substring starting at i that matches the pattern prefix. By concatenating the pattern, a sentinel, and the text, we can find all pattern occurrences in linear time. Z-array computation uses a window that maintains the rightmost matching prefix segment.

## Aho-Corasick

Aho-Corasick extends KMP to multiple patterns by building a trie with failure links (suffix links). The automaton is traversed character by character through the text, following failure links on mismatch. Output links collect matched patterns at each node. The total time is O(n + total pattern length + number of matches).
