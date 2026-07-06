# String Matching — Internal Mechanics

## Prefix Function Computation (KMP)

`java
int[] computePrefixFunction(String pattern) {
    int m = pattern.length();
    int[] pi = new int[m];
    for (int i = 1; i < m; i++) {
        int j = pi[i - 1];
        while (j > 0 && pattern.charAt(i) != pattern.charAt(j)) {
            j = pi[j - 1];
        }
        if (pattern.charAt(i) == pattern.charAt(j)) {
            j++;
        }
        pi[i] = j;
    }
    return pi;
}
`

## KMP Search

`java
List<Integer> kmpSearch(String text, String pattern) {
    List<Integer> result = new ArrayList<>();
    int[] pi = computePrefixFunction(pattern);
    int j = 0;
    for (int i = 0; i < text.length(); i++) {
        while (j > 0 && text.charAt(i) != pattern.charAt(j)) {
            j = pi[j - 1];
        }
        if (text.charAt(i) == pattern.charAt(j)) {
            j++;
        }
        if (j == pattern.length()) {
            result.add(i - j + 1);
            j = pi[j - 1];
        }
    }
    return result;
}
`

## Bad Character Table (Boyer-Moore)

`java
int[] buildBadCharTable(String pattern) {
    int[] table = new int[256];
    Arrays.fill(table, -1);
    for (int i = 0; i < pattern.length(); i++) {
        table[pattern.charAt(i)] = i;
    }
    return table;
}
`
"@

wf "MATH_FOUNDATION.md" @"
# Math Foundation for String Matching

## KMP Complexity Analysis

The prefix function computation performs at most 2m work because the while loop decreases j, and j increases at most m times. The search phase similarly performs at most 2n work. Total: O(n+m).

## Boyer-Moore Complexity

Worst-case without good suffix rule: O(nm). With good suffix rule: O(n+m) worst case, O(n/m) average for large alphabets.

## Rabin-Karp Hash Function

Using a base B and modulus M, the rolling hash of a substring s[i..i+m-1] is:
H(i) = (sum_{k=0}^{m-1} s[i+k] * B^{m-1-k}) mod M

Update: H(i+1) = (B * (H(i) - s[i] * B^{m-1}) + s[i+m]) mod M

The probability of false positive with M ~ 2^64 is approximately 1/M, negligible for practical purposes.

## Z-Array Properties

The Z-array window [L, R] maintains the rightmost interval matching the prefix. The invariant is that T[L..R] = P[0..R-L]. Within this window, previously computed Z values can be reused.

## Aho-Corasick Automaton

The failure function f(node) gives the longest proper suffix of the string represented by node that is also a prefix of some pattern. The depth of failure links ensures that the total number of failure transitions across the entire search is bounded by O(n + total pattern length).
