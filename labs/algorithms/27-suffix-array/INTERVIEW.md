# Interview Questions: Suffix Array

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| LC 1044 Longest Duplicate Substring | Hard | Google, Amazon | Suffix array + LCP |
| LC 1923 Longest Common Subpath | Hard | Google | Suffix array |
| LC 1062 Longest Repeating Substring | Medium | Google | Suffix array / DP |

Note: Suffix array problems are rare on LeetCode. They appear primarily in Google and Amazon interviews for advanced roles.

## NeetCode Reference
Not covered in NeetCode 150. Suffix arrays are advanced string algorithm topics.

## Company-Specific Questions
### Google
- Implement suffix array construction (prefix doubling method)
- How would you find the longest common prefix of two strings using suffix array?
- Design a plagiarism detection system using suffix arrays
- Implement Kasai's algorithm for LCP array construction

### Microsoft
- How would you search for a pattern in a large text using suffix arrays?
- Design a document similarity system using suffix arrays
- How does Bing cache suffix array queries for search?

### Meta
- Suffix arrays for content deduplication
- How would you find the longest repeated substring in user posts?
- Design a system to detect circular plagiarism in documents

### Amazon
- Product description deduplication using suffix arrays
- How would you find common substrings across product reviews?
- Design a compression algorithm using BWT from suffix array

### Apple
- Memory-efficient suffix array construction for mobile
- How would you implement substring search in iBooks?
- Suffix array for DNA sequence matching on device

### Oracle
- How does Oracle Text use suffix structures?
- Design a full-text search index for database CLOB columns
- Explain how Oracle implements CONTAINS() with suffix-like structures

## Real Production Scenarios
- Scenario 1: Genomic sequence analysis - using suffix arrays to find all occurrences of a DNA pattern across the 3 billion base-pair human genome in sublinear query time
- Scenario 2: Plagiarism detection at scale - building a suffix-array-based system that fingerprints 10 million documents and detects overlapping passages in O(log n) per query
- Scenario 3: Compression pipeline - implementing the Burrows-Wheeler Transform via suffix array construction as part of a bzip2-style compression for archival storage

## Interview Tips
- Suffix array construction: prefix-doubling O(n log n) or SA-IS O(n) for advanced roles
- LCP array (Kasai's algorithm) is essential for most suffix-array applications
- Longest repeated substring = max LCP value; distinct substrings = sum(n - sa[i] - lcp[i])
- Common edge cases: single character strings, all same characters, empty strings

## Java-Specific Considerations
- Implement prefix-doubling: `int[] sa` with sorting by `(rank[i], rank[i+k])` using `Arrays.sort` with custom comparator
- For LCP: Kasai's algorithm iterates over string, maintaining `k` for current LCP value
- Suffix array for string `s`: integer array of length n where `s.substring(sa[i])` is the ith suffix in sorted order
- Pitfall: O(n^2 log n) naive implementation for long strings (use prefix doubling or SA-IS)
- Pitfall: integer overflow in rank computation for very long strings
- `String.substring()` in Java 7+ is O(n) (copies char[]); use offset-based access for performance
