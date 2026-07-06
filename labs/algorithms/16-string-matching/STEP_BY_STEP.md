# Step-by-Step — String Matching Implementation

## KMP Implementation Steps

1. Compute pattern length m and text length n
2. Build prefix array pi of size m with pi[0] = 0
3. For i from 1 to m-1:
   a. Set j = pi[i-1]
   b. While j > 0 and pattern[i] != pattern[j], set j = pi[j-1]
   c. If pattern[i] == pattern[j], increment j
   d. Set pi[i] = j
4. Initialize text index i = 0, pattern index j = 0
5. For each i from 0 to n-1:
   a. While j > 0 and text[i] != pattern[j], set j = pi[j-1]
   b. If text[i] == pattern[j], increment j
   c. If j == m, record match at (i - m + 1), set j = pi[j-1]
6. Return list of match positions

## Rabin-Karp Implementation Steps

1. Choose base B (e.g., 131) and modulus M (e.g., 10^9+7)
2. Compute B^(m-1) mod M using fast exponentiation
3. Compute pattern hash and first window hash
4. For each window position i from 0 to n-m:
   a. If pattern hash == window hash, verify character-by-character
   b. If match verified, record position
   c. If i < n-m, update window hash for next position
5. Return list of match positions
