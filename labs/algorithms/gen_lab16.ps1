$labDir = "C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\algorithms\16-string-matching"

function wf($name, $content) {
    Set-Content -Path (Join-Path $labDir $name) -Value $content
}

wf "README.md" @"
# String Matching Algorithms — Overview

This lab covers exact string matching algorithms: Knuth-Morris-Pratt (KMP), Boyer-Moore, Rabin-Karp, Z-algorithm, and Aho-Corasick for multi-pattern matching. These algorithms solve the fundamental problem of finding occurrences of a pattern string within a larger text string, with applications ranging from text editors and search engines to bioinformatics and network security.

## Learning Objectives

- Implement KMP with prefix function computation
- Understand Boyer-Moore bad character and good suffix heuristics
- Apply Rabin-Karp rolling hash technique
- Use the Z-algorithm for linear-time pattern matching
- Build and traverse Aho-Corasick automaton for multi-pattern search

## Prerequisites

- Java arrays and strings
- Basic understanding of string manipulation
- Familiarity with hash functions

## Estimated Time

- **Theory**: 90 minutes
- **Practice**: 120 minutes
- **Exercises**: 90 minutes
- **Total**: 5-6 hours
"@

wf "THEORY.md" @"
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
"@

wf "WHY_IT_EXISTS.md" @"
# Why String Matching Algorithms Exist

String matching is one of the oldest and most fundamental problems in computer science. Before efficient algorithms existed, searching for a pattern in text required naive O(nm) approaches, which became prohibitively slow as text sizes grew. The development of linear-time string matching algorithms in the 1970s revolutionized text processing.

The naive approach compares the pattern against every possible starting position in the text. For a text of length n and pattern of length m, this requires O(nm) character comparisons. As documents grew from kilobytes to megabytes to gigabytes, this quadratic behavior became unacceptable.

KMP was developed in 1970-1977 as the first linear-time string matching algorithm. It introduced the concept of using information from previous comparisons to avoid re-examining matched characters. This was a breakthrough because it showed that string matching could be solved in worst-case O(n) time without any preprocessing of the text.

Boyer-Moore (1977) took a different approach by scanning from right to left, enabling it to skip large portions of the text. For typical patterns and natural language text, Boyer-Moore examines only about n/m characters, making it dramatically faster in practice.

Rabin-Karp (1987) introduced a randomized approach using rolling hashes. While its worst-case performance matches naive, its expected linear time and ability to handle two-dimensional pattern matching make it valuable for specific applications like plagiarism detection.

The Z-algorithm (1984) provided an elegant alternative to KMP with simpler implementation. Its linear-time Z-array computation is both simple and powerful.

Aho-Corasick (1975) generalized KMP to handle multiple patterns simultaneously, which is essential for virus scanning, intrusion detection, and bioinformatics applications where many signatures must be checked simultaneously.
"@

wf "WHY_IT_MATTERS.md" @"
# Why String Matching Matters

String matching algorithms are fundamental infrastructure underlying countless applications. Web search engines use sophisticated string matching to find relevant pages. Text editors rely on efficient search to provide real-time highlighting and find-replace functionality. Bioinformatics uses string matching to align DNA sequences and identify genetic patterns. Network intrusion detection systems scan packet payloads against thousands of attack signatures simultaneously using multi-pattern matching.

## Application Impact

Modern grep implementations use Boyer-Moore for fast file searching. Intrusion detection systems like Snort and Suricata use Aho-Corasick for multi-pattern matching at wire speed. Version control systems use string matching for diff generation. IDEs use KMP for code completion and syntax highlighting. The `indexOf` method in Java strings uses a variant of naive matching with optimizations, but for repeated searches, these advanced algorithms dramatically outperform it.

## Interview Relevance

String matching is a favorite topic in technical interviews. KMP is frequently asked at top-tier companies. Understanding the prefix function demonstrates sophisticated algorithmic thinking. Rolling hash techniques appear in many problems beyond string matching (e.g., longest common substring, plagiarism detection). Aho-Corasick demonstrates the power of automata theory in practical algorithm design.

## Performance Impact

For a text of 1 million characters and a pattern of 1000 characters, naive matching requires up to 1 billion comparisons. KMP requires at most 1 million comparisons. Boyer-Moore on English text typically examines only a few thousand characters. The performance difference grows linearly with input size, making these algorithms essential for large-scale text processing.
"@

wf "HISTORY.md" @"
# History of String Matching Algorithms

1970: James Morris and Vaughan Pratt independently discovered a linear-time string matching algorithm. They published their results at the 1970 Symposium on Switching and Automata Theory.

1977: Donald Knuth, James Morris, and Vaughan Pratt published the full KMP algorithm in the SIAM Journal on Computing, providing a rigorous analysis and establishing linear-time worst-case string matching.

1977: Robert S. Boyer and J. Strother Moore published their algorithm in Communications of the ACM, introducing the right-to-left scan with bad character and good suffix heuristics. Their algorithm became the fastest in practice for English text.

1975: Alfred V. Aho and Margaret J. Corasick published their multi-pattern matching algorithm in the Communications of the ACM, generalizing KMP to a trie-based automaton.

1984: Dan Gusfield published the Z-algorithm, providing an alternative linear-time approach with simpler implementation than KMP.

1987: Richard M. Karp and Michael O. Rabin introduced randomized string matching with rolling hashes at the ACM Conference on Principles of Programming Languages.

1990s: Commentz-Walter and Wu-Manber algorithms combined ideas from Boyer-Moore and Aho-Corasick for efficient multi-pattern matching.

2000s: The rise of bioinformatics drove new algorithms for approximate string matching and alignment. BLAST and FASTA became essential tools for genomic sequence matching.

2006: Bit-parallel algorithms (Shift-Or, BNDM) leveraged SIMD instructions for fast string matching on modern hardware.

2010s: Automata-based approaches using finite automata and suffix automata achieved new theoretical and practical results.

2019: Modern regex engines (RE2, Rust regex) combine multiple string matching strategies with automata compilation for linear-time regular expression matching.
"@

wf "MENTAL_MODELS.md" @"
# Mental Models for String Matching

## KMP — "The Bookmark"

Imagine reading a book where you need to find a specific phrase. When you reach a word that doesn't match, instead of starting over from the next page, you use a bookmark to jump back to the last place where the text could possibly restart the phrase. The prefix function tells you exactly where that bookmark goes, so you never reread text you've already matched.

## Boyer-Moore — "The Reverse Inspector"

Picture a detective examining a line of evidence from right to left. When a piece doesn't fit, the detective looks at what the mismatched piece is and how it relates to the evidence pattern. If the mismatched piece never appears in the pattern, the detective can skip the entire segment. This is the bad character rule. If a suffix matched but the preceding character didn't, the detective looks for another occurrence of that suffix that might align with a different preceding character. This is the good suffix rule.

## Rabin-Karp — "The Fingerprint Scanner"

Think of scanning a crowd for a specific person. Instead of comparing every single detail for each person, you compute a quick fingerprint for each person. If the fingerprint matches, you then do a detailed check. The rolling hash lets you efficiently compute the fingerprint for the next window by only adjusting for the character entering and leaving.

## Aho-Corasick — "The Multi-Pattern Automaton"

Imagine a librarian who needs to find any book from a list of banned titles on shelves. Instead of searching for each title separately, the librarian builds a decision tree that can spot any banned title by reading each shelf left to right once. When a partial match is found, the librarian follows shortcuts (failure links) to continue searching without starting over.
"@

wf "HOW_IT_WORKS.md" @"
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
"@

wf "INTERNALS.md" @"
# String Matching — Internal Mechanics

## Prefix Function Computation (KMP)

```java
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
```

## KMP Search

```java
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
```

## Bad Character Table (Boyer-Moore)

```java
int[] buildBadCharTable(String pattern) {
    int[] table = new int[256];
    Arrays.fill(table, -1);
    for (int i = 0; i < pattern.length(); i++) {
        table[pattern.charAt(i)] = i;
    }
    return table;
}
```
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
"@

wf "VISUAL_GUIDE.md" @"
# Visual Guide — String Matching

## KMP Prefix Function Visualization

Pattern: ABABAC
i=0: A          pi[0]=0
i=1: A B        pi[1]=0  (A != B)
i=2: A B A      pi[2]=1  (A matches A)
i=3: A B A B    pi[3]=2  (AB matches AB)
i=4: A B A B A  pi[4]=3  (ABA matches ABA)
i=5: A B A B A C pi[5]=0 (ABABAC no suffix-prefix match)

## Boyer-Moore Shift

Text:    F I N D _ I N _ A _ L A K E _ F I N D _ M E
Pattern:         A _ L A K E
                  ^ mismatch at K vs I -> bad char 'I' at position 2 -> shift by 2

## Rabin-Karp Window Rolling

Window 1: "FIND" -> hash h1
Window 2: "IND_" -> hash h2 = (h1 - 'F'*B^3)*B + '_'
Window 3: "ND_I" -> hash h3 = (h2 - 'I'*B^3)*B + 'I'
"@

wf "CODE_DEEP_DIVE.md" @"
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
"@

wf "STEP_BY_STEP.md" @"
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
"@

wf "COMMON_MISTAKES.md" @"
# Common Mistakes — String Matching

- **KMP: Off-by-one in prefix function** — Forgetting that pi[j-1] is correct when mismatching at position j
- **KMP: Wrong boundary after match** — Setting j = 0 instead of j = pi[j-1] after finding a complete match
- **Boyer-Moore: Bad char table size** — Only allocating for letters when input may contain extended ASCII
- **Boyer-Moore: Both heuristics needed** — Using only bad character rule gives worst-case O(nm)
- **Rabin-Karp: Modulo negative** — Java's % operator can return negative; always add M before % M
- **Rabin-Karp: Hash overflow** — Not using long for intermediate multiplication before modulo
- **Z-algorithm: Z-array index confusion** — Forgetting to account for the sentinel character offset
- **Aho-Corasick: Missing output links** — Only checking matches at the current node, not following output links
- **Aho-Corasick: Trie memory** — Not using compact storage for large alphabets
- **Generic: Pattern longer than text** — Not checking early and returning empty result
"@

wf "DEBUGGING.md" @"
# Debugging — String Matching

## Prefix Function Verification

```java
// Print prefix function for debugging
System.out.println("Prefix: " + Arrays.toString(pi));
```

For pattern "ABABAC", expected pi = [0, 0, 1, 1, 2, 0].

## Search Step Tracing

```java
// Trace each comparison
System.out.println("i=" + i + " j=" + j + " text=" + text.charAt(i) + " pattern=" + pattern.charAt(j));
```

## Rolling Hash Verification

Print hash values for each window and verify that equal substrings produce equal hashes. If not, check base and modulus values.

## Aho-Corasick Trie Visualization

Print the trie with node indices, characters, and failure links to verify automaton correctness.

## Common Test Cases

- Pattern equals text
- Pattern longer than text (empty result)
- Multiple overlapping matches (e.g., "AAA" in "AAAA")
- No match present
- Pattern and text with repeating characters
"@

wf "REFACTORING.md" @"
# Refactoring — String Matching

## Strategy Pattern for Matching Algorithms

```java
interface StringMatcher {
    List<Integer> search(String text, String pattern);
}
```

This allows easy switching between algorithms and benchmarking.

## Extracting Hash Function

```java
class RollingHash {
    private final long hash;
    private final long power;
    public RollingHash next(char out, char in) { ... }
}
```

## Generic Character Sequences

Support CharSequence instead of String for flexibility with StringBuilder and other character sources.

## Parallel Search

For very large texts, split the text into chunks (with overlap of m-1 characters for boundary patterns) and search in parallel.

## Memory Optimization

For Aho-Corasick, replace arrays with ArrayLists for sparse alphabets, or use HashMap transitions to reduce memory when the alphabet is large but usage is sparse.
"@

wf "PERFORMANCE.md" @"
# Performance — String Matching

## Theoretical Performance

| Algorithm | Preprocessing | Search (worst) | Search (avg) | Space |
|-----------|--------------|----------------|--------------|-------|
| Naive | O(1) | O(nm) | O(nm) | O(1) |
| KMP | O(m) | O(n) | O(n) | O(m) |
| Boyer-Moore | O(m+sigma) | O(n+m) | O(n/m) | O(m+sigma) |
| Rabin-Karp | O(m) | O(nm) | O(n+m) | O(1) |
| Z-algorithm | O(m) | O(n) | O(n) | O(n+m) |
| Aho-Corasick | O(total m) | O(n) | O(n) | O(total m) |

## Practical Benchmarks

For a 10MB English text file searching for a 5-character word:
- Naive: ~50ms
- KMP: ~10ms
- Boyer-Moore: ~2ms
- Rabin-Karp: ~15ms
- Z-algorithm: ~10ms

## Optimization Tips

- Use char[] instead of String for tight loops
- Precompute powers for rolling hash once
- For Boyer-Moore, precompute both heuristics upfront
- For Aho-Corasick, use lookup tables for small alphabets
"@

wf "SECURITY.md" @"
# Security — String Matching

## Hash Collision Attacks

Rabin-Karp with a fixed modulus is vulnerable to algorithmic complexity attacks. An attacker can craft input that causes many hash collisions, degrading performance to O(nm). Use randomized modulus or rolling hash with two moduli.

## Pattern Injection

If patterns come from user input, very long patterns can cause excessive memory allocation. Validate pattern length before building data structures.

## ReDoS (Regex Denial of Service)

While these algorithms are safe, regex engines using backtracking can be catastrophically slow on crafted input. Prefer automaton-based approaches for untrusted input.

## Timing Side Channels

Constant-time string comparison may be needed when matching against sensitive tokens or passwords. Standard string matching algorithms are not constant-time.

## Memory Exhaustion

Aho-Corasick automaton size grows with total pattern length. For many patterns from untrusted sources, this can exhaust memory.
"@

wf "ARCHITECTURE.md" @"
# Architecture — String Matching

## Layered Design

```
String Matching Library
├── Matcher Interface
│   ├── KMPMatcher
│   ├── BoyerMooreMatcher
│   ├── RabinKarpMatcher
│   ├── ZMatcher
│   └── AhoCorasickMatcher
├── Preprocessor
│   ├── PrefixFunction
│   ├── BadCharTable
│   └── GoodSuffixTable
├── Hash
│   └── RollingHash
└── Automaton
    ├── Trie
    ├── FailureLinks
    └── OutputLinks
```

## Integration Patterns

- Use as a library with a simple search(String text, String pattern) API
- Support streaming search for large files
- Provide builder pattern for configuration (choice of algorithm, hash parameters)
"@

wf "EXERCISES.md" @"
# Exercises — String Matching

## Beginner
1. Implement naive string matching on char arrays
2. Compute the prefix function for "AAAA" and "ABCDABD"
3. Write a function to compute rolling hash of a string
4. Find all occurrences of "abc" in "abcabcabc" using any algorithm

## Intermediate
5. Implement KMP search on a text file
6. Build a bad character table for Boyer-Moore
7. Compare performance of KMP vs Boyer-Moore on random strings
8. Implement Rabin-Karp with double hashing

## Advanced
9. Build a full Aho-Corasick automaton and search for 100 patterns
10. Implement the Z-algorithm from scratch
11. Modify KMP to count total character comparisons
12. Implement a regex subset using automaton-based matching
"@

wf "QUIZ.md" @"
# Quiz — String Matching

1. What is the worst-case time complexity of KMP?
2. Which heuristic gives Boyer-Moore its sublinear average performance?
3. Why does Rabin-Karp use a rolling hash?
4. What does the prefix function represent in KMP?
5. How does Aho-Corasick handle multiple patterns?
6. What is the Z-array at position i?
7. When would you choose Boyer-Moore over KMP?
8. What is a hash collision in Rabin-Karp?
9. How do failure links in Aho-Corasick work?
10. What character does the Z-algorithm use as a sentinel?
"@

wf "FLASHCARDS.md" @"
# Flashcards — String Matching

- Q: KMP time complexity? → A: O(n+m) worst-case
- Q: Boyer-Moore main heuristics? → A: Bad character + good suffix
- Q: Rabin-Karp best use case? → A: Multiple pattern searches, 2D pattern matching
- Q: What does Z[0] equal? → A: 0 (by definition)
- Q: Aho-Corasick failure link? → A: Longest proper suffix that exists as a prefix
- Q: KMP prefix function purpose? → A: Avoid re-examining matched characters
- Q: Boyer-Moore scanning direction? → A: Right to left
- Q: Rolling hash complexity per window? → A: O(1)
"@

wf "INTERVIEW.md" @"
# Interview Questions — String Matching

1. "Implement strStr() in Java." — Use KMP or two-pointer
2. "How would you find all anagrams of a pattern in a text?" — Rabin-Karp with character frequency windows
3. "Design a plagiarism detection system." — Rolling hash with document fingerprinting
4. "How does grep work internally?" — Boyer-Moore for single patterns, Aho-Corasick for multiple
5. "Search for patterns in a stream without storing the entire stream." — Automaton-based matching
6. "Find the longest palindromic substring." — Z-algorithm on concatenated string
"@

wf "REFLECTION.md" @"
# Reflection — String Matching

- How does KMP guarantee linear time despite backtracking in the while loop?
- Why is Boyer-Moore faster in practice despite worse worst-case bounds?
- What are the tradeoffs between hash-based and automaton-based matching?
- How would you extend these algorithms for Unicode strings with variable-length encoding?
- When would naive string matching actually be the best choice?
- How do these algorithms relate to modern regex engines?
"@

wf "REFERENCES.md" @"
# References — String Matching

- Knuth, D.E., Morris, J.H., Pratt, V.R. "Fast Pattern Matching in Strings." SIAM Journal on Computing, 1977.
- Boyer, R.S., Moore, J.S. "A Fast String Searching Algorithm." Communications of the ACM, 1977.
- Aho, A.V., Corasick, M.J. "Efficient String Matching: An Aid to Bibliographic Search." Communications of the ACM, 1975.
- Karp, R.M., Rabin, M.O. "Efficient Randomized Pattern-Matching Algorithms." IBM Journal of Research and Development, 1987.
- Gusfield, D. "Algorithms on Strings, Trees, and Sequences." Cambridge University Press, 1997.
- Cormen, T.H. et al. "Introduction to Algorithms." MIT Press, 4th Edition, 2022.
"@

Write-Host "16-string-matching: All 24 markdown files created"
