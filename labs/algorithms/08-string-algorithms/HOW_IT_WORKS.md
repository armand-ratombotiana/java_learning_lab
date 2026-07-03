# How String Algorithms Work

## KMP Prefix Function
`
Pattern: "ABABAC"
π[0] = 0 (by definition)
π[1]: "A" in "AB" → 0 (no proper prefix = suffix)
π[2]: "AB" in "ABA" → 1 ("A")
π[3]: "ABA" in "ABAB" → 2 ("AB")
π[4]: "ABAB" in "ABABA" → 3 ("ABA")
π[5]: "ABABA" in "ABABAC" → 0 (no match)

π = [0, 0, 1, 2, 3, 0]
`

## Rabin-Karp Rolling Hash
`
hash("abc") = a×B² + b×B + c (mod M)
Roll: remove 'a', add 'd'
new hash = (old - a×B²) × B + d (mod M)
`
"@

wf "INTERNALS.md" @"
# String Algorithms — Internal Mechanics

## KMP Implementation
`java
public int kmp(String text, String pattern) {
    int n = text.length(), m = pattern.length();
    if (m == 0) return 0;

    int[] pi = computePrefixFunction(pattern);
    int q = 0; // number of matched chars
    for (int i = 0; i < n; i++) {
        while (q > 0 && pattern.charAt(q) != text.charAt(i))
            q = pi[q - 1];
        if (pattern.charAt(q) == text.charAt(i)) q++;
        if (q == m) return i - m + 1;
    }
    return -1;
}

private int[] computePrefixFunction(String pattern) {
    int m = pattern.length();
    int[] pi = new int[m];
    for (int i = 1; i < m; i++) {
        int j = pi[i - 1];
        while (j > 0 && pattern.charAt(i) != pattern.charAt(j))
            j = pi[j - 1];
        if (pattern.charAt(i) == pattern.charAt(j)) j++;
        pi[i] = j;
    }
    return pi;
}
`

## Rabin-Karp Implementation
`java
public int rabinKarp(String text, String pattern) {
    int n = text.length(), m = pattern.length();
    if (m > n) return -1;
    int base = 256, prime = 101;
    int patHash = 0, txtHash = 0, h = 1;
    for (int i = 0; i < m - 1; i++) h = (h * base) % prime;
    for (int i = 0; i < m; i++) {
        patHash = (base * patHash + pattern.charAt(i)) % prime;
        txtHash = (base * txtHash + text.charAt(i)) % prime;
    }
    for (int i = 0; i <= n - m; i++) {
        if (patHash == txtHash) {
            int j = 0;
            while (j < m && text.charAt(i + j) == pattern.charAt(j)) j++;
            if (j == m) return i;
        }
        if (i < n - m) {
            txtHash = (base * (txtHash - text.charAt(i) * h) + text.charAt(i + m)) % prime;
            if (txtHash < 0) txtHash += prime;
        }
    }
    return -1;
}
`
"@

wf "MATH_FOUNDATION.md" @"
# Math Foundation for String Algorithms

## KMP Correctness
The prefix function π[q] = length of longest proper prefix of pattern[0..q] that is also a suffix. This ensures we never need to backtrack the text pointer.

## Rolling Hash
Hash function: h(s) = (s[0]×Bᵐ⁻¹ + s[1]×Bᵐ⁻² + ... + s[m-1]) mod M
- B is a base (e.g., 256 for ASCII)
- M is a large prime for fewer collisions
- Rolling update: h(shifted) = (h - s[i]×Bᵐ⁻¹) × B + s[i+m] (mod M)

## Trie Space
For alphabet size k and total string length L: O(L × k) worst case
Compressed trie (radix tree) reduces to O(L) nodes.
