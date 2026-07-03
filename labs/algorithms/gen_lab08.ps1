$labDir = "C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\algorithms\08-string-algorithms"

function wf($name, $content) {
    Set-Content -Path (Join-Path $labDir $name) -Value $content
}

wf "README.md" @"
# String Algorithms — Overview

Covers pattern matching, KMP, Rabin-Karp, and Trie data structure.

## Learning Objectives
- Implement naive pattern matching and analyze complexity
- Master KMP algorithm (prefix function, failure links)
- Implement Rabin-Karp with rolling hash
- Build and use trie for string operations

## Prerequisites
- Basic string manipulation in Java
- Arrays and hash tables
- Basic complexity analysis

## Estimated Time
- **Total**: 4–6 hours
"@

wf "THEORY.md" @"
# String Algorithms — Theoretical Foundation

## Naive Pattern Matching
- Slide pattern over text, compare each position
- Time: O(nm) where n = text length, m = pattern length

## Knuth-Morris-Pratt (KMP)
- Precomputes failure function (longest proper prefix that is also suffix)
- Avoids re-examining matched characters
- Time: O(n + m)

## Rabin-Karp
- Uses rolling hash to compare pattern with substrings
- Hash collision resolution needed
- Average: O(n + m), Worst: O(nm)
- Ideal for multiple pattern search

## Trie (Prefix Tree)
- Tree where each node represents a character
- Path from root to node represents a prefix
- Search: O(m) for m-length string
- Space: O(alphabet × total characters)
"@

wf "WHY_IT_EXISTS.md" @"
# Why String Algorithms Exist

String processing is fundamental in text editors, search engines, bioinformatics, and compilers. Efficient pattern matching enables Ctrl+F, DNA sequence alignment, spam filtering, and compiler lexing.
"@

wf "WHY_IT_MATTERS.md" @"
# Why String Algorithms Matter

- Text Editors: Search/replace functionality (Ctrl+F)
- Search Engines: Indexing and query matching
- Bioinformatics: DNA sequence alignment (billions of base pairs)
- Compilers: Lexical analysis (tokenization)
- Network Security: Intrusion detection (pattern matching in packets)
- Auto-complete: Trie-based suggestion systems
- Plagiarism Detection: String matching algorithms
"@

wf "HISTORY.md" @"
# History of String Algorithms

- 1970: Naive pattern matching formalized
- 1977: Knuth-Morris-Pratt algorithm published
- 1987: Rabin-Karp algorithm by Karp and Rabin
- 1960s: Trie coined by Fredkin (from "retrieval")
- 1990s: Aho-Corasick extension of KMP for multiple patterns
- 2000s: Suffix arrays and suffix trees for advanced string search
- 2010s: Bioinformatics drives development of approximate string matching
"@

wf "MENTAL_MODELS.md" @"
# Mental Models

## KMP — "Bookmark After Partial Match"
After partially matching a pattern, you don't start from scratch — you use a bookmark (prefix function) to continue from where the match overlaps.

## Rabin-Karp — "Fingerprint Comparison"
Instead of comparing strings character by character, compute a numeric fingerprint (hash). Compare fingerprints first, verify on match.

## Trie — "Tree of Prefixes"
Like a phone tree menu: each node asks "what's the next character?" and branches accordingly. All strings sharing a prefix share the same path.
"@

wf "HOW_IT_WORKS.md" @"
# How String Algorithms Work

## KMP Prefix Function
```
Pattern: "ABABAC"
π[0] = 0 (by definition)
π[1]: "A" in "AB" → 0 (no proper prefix = suffix)
π[2]: "AB" in "ABA" → 1 ("A")
π[3]: "ABA" in "ABAB" → 2 ("AB")
π[4]: "ABAB" in "ABABA" → 3 ("ABA")
π[5]: "ABABA" in "ABABAC" → 0 (no match)

π = [0, 0, 1, 2, 3, 0]
```

## Rabin-Karp Rolling Hash
```
hash("abc") = a×B² + b×B + c (mod M)
Roll: remove 'a', add 'd'
new hash = (old - a×B²) × B + d (mod M)
```
"@

wf "INTERNALS.md" @"
# String Algorithms — Internal Mechanics

## KMP Implementation
```java
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
```

## Rabin-Karp Implementation
```java
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
```
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
"@

wf "VISUAL_GUIDE.md" @"
# Visual Guide — String Algorithms

## KMP Matching
```
Text:    A B A B A B A C
Pattern: A B A B A C

Step 1: A B A B A B A C
         A B A B A C    ← mismatch at C vs B, q=5→π[4]=3
         ↑ q=5
Step 2: A B A B A B A C
             A B A B A C  ← continue from q=3
             ↑ q=3
Step 3: match found!
```

## Trie Structure
```
        root
       / |  \
      a  b   c
     /   |    \
    t    a     a
   /     |      \
  e      t      t
        / \     /
       s   ?   ?
```
"@

wf "CODE_DEEP_DIVE.md" @"
# Code Deep Dive — String Algorithms

## Trie Implementation
```java
class Trie {
    static class Node {
        Node[] children = new Node[26];
        boolean isEnd;
    }
    private Node root = new Node();

    public void insert(String word) {
        Node node = root;
        for (char c : word.toCharArray()) {
            int idx = c - 'a';
            if (node.children[idx] == null)
                node.children[idx] = new Node();
            node = node.children[idx];
        }
        node.isEnd = true;
    }

    public boolean search(String word) {
        Node node = root;
        for (char c : word.toCharArray()) {
            int idx = c - 'a';
            if (node.children[idx] == null) return false;
            node = node.children[idx];
        }
        return node.isEnd;
    }

    public boolean startsWith(String prefix) {
        Node node = root;
        for (char c : prefix.toCharArray()) {
            int idx = c - 'a';
            if (node.children[idx] == null) return false;
            node = node.children[idx];
        }
        return true;
    }
}
```
"@

wf "STEP_BY_STEP.md" @"
# Step-by-Step — String Algorithms

## KMP
1. Compute prefix function for pattern
2. Initialize matched count q = 0
3. For each character in text:
4.   While q > 0 and pattern[q] ≠ text[i], q = π[q-1]
5.   If pattern[q] == text[i], q++
6.   If q == m, pattern found at i-m+1

## Rabin-Karp
1. Compute h = Bᵐ⁻¹ mod prime
2. Compute pattern hash and first text window hash
3. For each window in text:
4.   If hashes match, verify character by character
5.   Roll hash to next window

## Trie
1. Root node has 26 children (for lowercase a-z)
2. Insert: walk/create nodes for each character, mark last as end
3. Search: walk nodes, check if last node is end
4. Prefix: walk nodes, return true if walk succeeds
"@

wf "COMMON_MISTAKES.md" @"
# Common Mistakes

- KMP: Off-by-one in prefix function computation
- KMP: Confusing prefix function with simple matching
- Rabin-Karp: Hash collision not handled (must verify)
- Rabin-Karp: Integer overflow in hash computation
- Rabin-Karp: Negative hash after rolling subtraction
- Trie: Forgetting 26-letter array size (non-lowercase input)
- Trie: Not resetting children array for each new node
"@

wf "DEBUGGING.md" @"
# Debugging — String Algorithms

## Print Prefix Function
```java
System.out.println("π = " + Arrays.toString(pi));
```

## KMP Step by Step
```java
System.out.printf("i=%d, q=%d, char=%c%n", i, q, text.charAt(i));
```

## Rabin-Karp Hash Debug
```java
System.out.printf("Window [%d,%d): hash=%d, patternHash=%d%n", i, i+m, txtHash, patHash);
```
"@

wf "REFACTORING.md" @"
# Refactoring — String Algorithms

## KMP with Iterator
```java
class KmpMatcher implements Iterator<Integer> {
    // returns all match positions lazily
}
```

## Trie with Generic Alphabet
```java
class Trie<T> {
    Map<T, Trie<T>> children;
    boolean isEnd;
}
```
"@

wf "PERFORMANCE.md" @"
# Performance — String Algorithms

| Algorithm | Preprocess | Search | Space |
|-----------|-----------|--------|-------|
| Naive | O(1) | O(nm) | O(1) |
| KMP | O(m) | O(n) | O(m) |
| Rabin-Karp | O(m) | O(n) avg | O(1) |
| Trie | O(L) | O(m) | O(L × A) |

n = text length, m = pattern length, L = total string length, A = alphabet size

## Benchmarks (n=1,000,000, m=100)
| Algorithm | Time |
|-----------|------|
| Naive | ~50ms (avg), ~10s (worst) |
| KMP | ~2ms |
| Rabin-Karp | ~3ms |
"@

wf "SECURITY.md" @"
# Security — String Algorithms

- Hash collision DoS: Attacker crafts strings with same hash → O(nm) worst case for Rabin-Karp
- Hash seed: Use random hash base/prime to prevent collision attacks
- Pattern injection: Ensure pattern length validation to prevent DoS
- Trie memory: Large alphabet trie can exhaust memory
- Regex injection: If pattern matching is regex-based, guard against ReDoS
"@

wf "ARCHITECTURE.md" @"
# Architecture — String Algorithms

## Java Standard Library
- String.indexOf() — naive with optimizations (Boyer-Moore in modern JDK)
- String.matches() — regex-based pattern matching
- java.util.regex — NFA/DFA-based regex engine
- HashMap/HashSet — hash-based string operations

## Real-World Systems
- grep: Boyer-Moore algorithm
- Google Search: Index-based (inverted index)
- Spell Checkers: Trie + edit distance
- DNA Search: Burrows-Wheeler transform + FM-index
- Auto-complete: Trie with frequency ranking
- Plagiarism Detection: Rabin-Karp (fingerprinting documents)
"@

wf "EXERCISES.md" @"
# Exercises — String Algorithms

## Beginner
1. Implement naive pattern matching
2. Count occurrences of pattern in text
3. Implement KMP prefix function
4. Print shift values for matches

## Intermediate
5. Implement full KMP search
6. Implement Rabin-Karp with rolling hash
7. Implement Trie with insert, search, startsWith
8. Find longest common prefix of strings using trie

## Advanced
9. Implement Aho-Corasick (multiple pattern matching)
10. Implement Boyer-Moore (bad character rule)
11. Implement Z-algorithm (linear time pattern matching)
12. Build autocomplete feature with trie and priorities
"@

wf "QUIZ.md" @"
# Quiz — String Algorithms

1. What is the time complexity of naive pattern matching?
2. What does KMP's prefix function represent?
3. Why does Rabin-Karp need hash collision verification?
4. What is the space complexity of a trie?
5. When would you use KMP over Rabin-Karp?
6. How does rolling hash update in constant time?
"@

wf "FLASHCARDS.md" @"
# Flashcards

- Q: KMP time? → A: O(n+m)
- Q: Rabin-Karp avg time? → A: O(n+m)
- Q: Trie search time? → A: O(m) for m-length string
- Q: Prefix function? → A: Longest proper prefix that is also suffix
- Q: Rolling hash base? → A: Usually 256 for ASCII
- Q: KMP preprocess time? → A: O(m)
"@

wf "INTERVIEW.md" @"
# Interview Questions

1. "Implement KMP or explain the algorithm." — Classic hard question
2. "Implement Trie (insert, search, startsWith)." — T9 auto-complete
3. "Find all anagrams in a string." — Sliding window + hash
4. "Longest substring without repeating characters." — Sliding window
5. "Word search II." — Trie + DFS backtracking
6. "Longest word in dictionary." — Trie-based prefix check
7. "Implement strStr()." — KMP or Rabin-Karp
"@

wf "REFLECTION.md" @"
# Reflection

- Why is naive pattern matching unacceptable for large text?
- How does preprocessing the pattern enable linear-time matching?
- What trade-offs exist between KMP and Rabin-Karp?
- How does a trie efficiently encode shared prefixes?
- When would you use suffix array/tree over trie?
"@

wf "REFERENCES.md" @"
# References

- CLRS, Chapter 32 (String Matching)
- Gusfield, D. "Algorithms on Strings, Trees, and Sequences"
- Knuth, D. "The Art of Computer Programming, Vol. 3" (String searching)
- Sedgewick, R. "Algorithms", Chapter 5 (Strings)
- Java String.indexOf() source code (OpenJDK)
"@

Write-Host "08-string-algorithms: All 24 files created"
