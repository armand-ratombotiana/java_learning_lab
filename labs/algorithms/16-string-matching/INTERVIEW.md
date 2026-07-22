# Interview Questions: String Matching

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| LC 28 Find the Index (strStr) | Easy | Google, Meta, Amazon, Microsoft | KMP / two-pointer |
| LC 686 Repeated String Match | Medium | Google, Amazon | Rolling hash / KMP |
| LC 459 Repeated Substring Pattern | Easy | Google, Amazon | KMP LPS |
| LC 214 Shortest Palindrome | Hard | Google | KMP / rolling hash |
| LC 1392 Longest Happy Prefix | Hard | Google | LPS array |
| LC 796 Rotate String | Easy | Google, Meta | String concatenation / KMP |

## NeetCode Reference
- LC 28 Find the Index (NeetCode 150)
- LC 459 Repeated Substring Pattern (NeetCode All)

## Company-Specific Questions
### Google
- Shortest Palindrome and Longest Happy Prefix are Google signatures
- Master KMP prefix function calculation (LPS array)
- Rolling hash (Rabin-Karp) for pattern matching with collisions handling

### Microsoft
- Implement strStr() with and without library functions
- How does Bing's search engine match patterns in query strings?
- Design a plagiarism detection system using string matching

### Meta
- Repeated String Match for detecting content duplication
- Facebook uses string matching for content filtering
- Rotate String for circular buffer operations

### Amazon
- Pattern matching in product description search
- How would you detect counterfeit products using string similarity?
- Repeated substring pattern for fraud detection

### Apple
- Memory-efficient string matching for iOS search
- Implement strStr() on a memory-constrained device
- How does Spotlight search find files by name fragments?

### Oracle
- How does Oracle implement LIKE '%pattern%' with index?
- Design a text search algorithm for CLOB columns
- Explain Oracle's CONTAINS operator and context indexes

## Real Production Scenarios
- Scenario 1: Plagiarism detection - using Rabin-Karp rolling hash to fingerprint documents and find overlapping passages across millions of academic papers
- Scenario 2: Log pattern matching - searching for known error patterns in multi-gigabyte log files using Aho-Corasick automaton for multi-pattern matching
- Scenario 3: DNA sequence matching - debugging a KMP implementation that fails when pattern length exceeds text length due to buffer overflow in LPS array computation

## Interview Tips
- KMP is O(n+m) time, O(m) space; the LPS array is the key insight
- Rabin-Karp uses rolling hash with modular arithmetic; handle hash collisions
- Boyer-Moore uses bad character and good suffix heuristics for sub-linear average case
- Common edge cases: pattern longer than text, empty pattern, overlapping matches

## Java-Specific Considerations
- `String.indexOf(String)` uses naive O(n*m) for small patterns, `String.indexOf(char)` for char
- Implement KMP with `int[] lps` array computed in O(m) using two pointers
- Rolling hash: use `long` with base 31 or 131 and modulus like 1e9+7 to avoid overflow
- Pitfall: negative hash values from modulo operations; always normalize with ((hash % MOD) + MOD) % MOD
- Pitfall: Integer overflow in hash computation (cast to long before multiplication)
- `java.util.regex.Pattern` uses NFA/DFA automaton for regex matching, not KMP
