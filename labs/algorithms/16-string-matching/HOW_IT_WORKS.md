# How String Matching Works

## KMP Algorithm Walkthrough

Pattern: "ABABAC"
Compute prefix function pi:
- pi[0] = 0
- pi[1]: "A" and "AB" -> longest proper prefix that is suffix: 0
- pi[2]: "AB" = "AB"? No. "A" = "A"? Yes -> pi[2] = 1
- pi[3]: "ABA": "A" = last "A" -> pi[3] = 1. But check "AB" = "BA"? No.
- pi[4]: "ABAB": "AB" = "AB" -> pi[4] = 2
- pi[5]: "ABABA": "ABA" = "ABA" -> pi[5] = 3

pi = [0, 0, 1, 1, 2, 3]

Searching T = "ABABABACABA":
- i=0, j=0: T[0]=A matches P[0]=A -> j=1
- i=1, j=1: T[1]=B matches P[1]=B -> j=2
- i=2, j=2: T[2]=A matches P[2]=A -> j=3
- i=3, j=3: T[3]=B matches P[3]=B -> j=4
- i=4, j=4: T[4]=A matches P[4]=A -> j=5
- i=5, j=5: T[5]=B vs P[5]=C mismatch. j = pi[4] = 2
- i=5, j=2: T[5]=B matches P[2]=A? No. j = pi[1] = 0
- i=5, j=0: T[5]=B vs P[0]=A mismatch. i=6
- ... and so on

## Boyer-Moore Bad Character Rule

Pattern: "EXAMPLE", Text: "HERE IS A SIMPLE EXAMPLE"

Start at position 5 (pattern length-1), scan right to left:
- S vs E mismatch. Bad character 'S' not in pattern. Shift past 'S'.
Current position aligns at "EXAMPLE" starting at text[6].

## Rabin-Karp Rolling Hash

hash("abc") = a * B^2 + b * B + c (mod M)
To compute hash("bcd"): (hash("abc") - a * B^2) * B + d

This O(1) update makes the algorithm O(n+m) on average.

## Z-Algorithm

Concatenate pattern + "$" + text. Compute Z array describing longest prefix matches at each position. Positions where Z[i] >= m indicate pattern occurrences.

## Aho-Corasick Trie

Build a trie of all patterns. Add failure links from each node back to the longest proper suffix that exists in the trie. Traverse the text through the automaton. On each node, follow output links to report matches.
