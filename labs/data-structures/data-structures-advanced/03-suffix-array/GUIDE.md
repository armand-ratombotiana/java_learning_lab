# Guide: Suffix Array

## Overview

A **suffix array** is a sorted array of all suffixes of a given string. For a string `S` of length `n`, the suffix array `SA` is a permutation of indices `[0, n-1]` such that `S[SA[i]:]` (the suffix starting at position `SA[i]`) is lexicographically less than `S[SA[i+1]:]` for all `i`.

The suffix array, combined with the LCP (Longest Common Prefix) array, serves as a space-efficient alternative to the suffix tree for many string processing problems: pattern matching, longest repeated substring, substring counting, and genome analysis.

### Why Not Use a Suffix Tree?

| Aspect | Suffix Tree | Suffix Array |
|--------|------------|--------------|
| Memory | O(n) but high constants (~20n bytes) | O(n) with low constants (~4n bytes) |
| Build | O(n) complex algorithms | O(n) SA-IS or O(n log n) simple |
| Pattern search | O(m) | O(m + log n) or O(m + log n + LCP) |
| Construction complexity | Very high (Ukkonen) | Moderate |
| Practical use | Rarely implemented from scratch | Widely used in practice |

**Key Insight**: Suffix array is the standard choice for production systems (genome aligners, compression tools). Suffix tree is mainly pedagogical.

---

## ASCII Diagram

```
String S = "banana$"  (n = 7, $ is sentinel, lexicographically smallest)

Suffixes (unsorted):
  0: banana$
  1: anana$
  2: nana$
  3: ana$
  4: na$
  5: a$
  6: $

Sorted suffixes (Suffix Array):
  SA[0] = 6  → "$"
  SA[1] = 5  → "a$"
  SA[2] = 3  → "ana$"
  SA[3] = 1  → "anana$"
  SA[4] = 0  → "banana$"
  SA[5] = 4  → "na$"
  SA[6] = 2  → "nana$"

LCP Array (Kasai algorithm):
  LCP[0] = 0  (no previous)
  LCP[1] = 1  ("$" vs "a$": common prefix "")
  LCP[2] = 1  ("a$" vs "ana$": common prefix "a")
  LCP[3] = 3  ("ana$" vs "anana$": common prefix "ana")
  LCP[4] = 0  ("anana$" vs "banana$": common prefix "")
  LCP[5] = 1  ("banana$" vs "na$": common prefix "")
  LCP[6] = 2  ("na$" vs "nana$": common prefix "na")

SA:     [6, 5, 3, 1, 0, 4, 2]
LCP:    [0, 1, 1, 3, 0, 1, 2]
```

### LCP Array Meaning

- LCP[i] = length of longest common prefix between suffix at SA[i-1] and SA[i]
- LCP[0] is undefined (set to 0)
- Sum of LCP gives total duplicated characters across adjacent suffixes

---

## Source Code Walkthrough

The implementation follows the standard prefix-doubling construction.

### Build (lines ~12-40)

```java
public static int[] buildSuffixArray(String s) {
    int n = s.length();
    int[] sa = new int[n];
    int[] rank = new int[n];     // current rank of each suffix
    int[] tmp = new int[n];      // temporary rank storage

    // Initialise: sort by first character
    for (int i = 0; i < n; i++) {
        sa[i] = i;
        rank[i] = s.charAt(i);   // rank = character code
    }

    for (int k = 1; k < n; k *= 2) {
        // Sort by (rank[i], rank[i+k])
        final int step = k;
        Integer[] boxed = new Integer[n];
        for (int i = 0; i < n; i++) boxed[i] = i;
        Arrays.sort(boxed, (a, b) -> {
            if (rank[a] != rank[b]) return Integer.compare(rank[a], rank[b]);
            int ra = a + step < n ? rank[a + step] : -1;
            int rb = b + step < n ? rank[b + step] : -1;
            return Integer.compare(ra, rb);
        });
        for (int i = 0; i < n; i++) sa[i] = boxed[i];

        // Assign new ranks
        tmp[sa[0]] = 0;
        for (int i = 1; i < n; i++) {
            int a = sa[i-1], b = sa[i];
            int ra = a + step < n ? rank[a + step] : -1;
            int rb = b + step < n ? rank[b + step] : -1;
            tmp[b] = tmp[a] + ((rank[a] != rank[b] || ra != rb) ? 1 : 0);
        }
        System.arraycopy(tmp, 0, rank, 0, n);

        if (rank[sa[n-1]] == n-1) break; // all ranks distinct → done
    }
    return sa;
}
```

**Prefix doubling algorithm:**
1. Start with rank = character values
2. Sort by (rank[i], rank[i+k]) for k = 1, 2, 4, ...
3. Re-rank: adjacent equal pairs get same rank
4. Stop when all ranks are distinct (max rank = n-1)

**Complexity**: O(n log n) with O(n) extra space.

### LCP Array (Kasai) — lines ~42-60

```java
public static int[] buildLCP(String s, int[] sa) {
    int n = s.length();
    int[] lcp = new int[n];
    int[] rank = new int[n];     // inverse of SA: rank[i] = position of suffix i in SA

    for (int i = 0; i < n; i++) rank[sa[i]] = i;

    int len = 0;
    for (int i = 0; i < n; i++) {
        if (rank[i] > 0) {
            int j = sa[rank[i] - 1]; // previous suffix in SA
            while (i + len < n && j + len < n && s.charAt(i + len) == s.charAt(j + len))
                len++;
            lcp[rank[i]] = len;
            if (len > 0) len--;
        }
    }
    // lcp[0] is undefined, set to 0
    return lcp;
}
```

**Kasai algorithm**: Given SA, compute LCP in O(n). Key property: LCP decreases by at most 1 between consecutive suffixes in original order.

### Pattern Search — lines ~62-85

```java
public static int search(String s, int[] sa, String pattern) {
    int n = s.length(), m = pattern.length();
    int lo = 0, hi = n - 1;

    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        String suffix = s.substring(sa[mid], Math.min(sa[mid] + m, n));
        int cmp = pattern.compareTo(suffix);

        if (cmp == 0) return sa[mid];       // found
        else if (cmp < 0) hi = mid - 1;     // pattern < suffix → go left
        else lo = mid + 1;                  // pattern > suffix → go right
    }
    return -1; // not found
}
```

O(m log n) binary search on suffix array. Each comparison extracts substring of length up to m (O(m)).

---

## Complexity Table

| Operation | Time | Space | Notes |
|-----------|------|-------|-------|
| SA Build (prefix doubling) | O(n log n) | O(n) | Simple, widely used |
| SA Build (SA-IS) | O(n) | O(n) | Complex, linear |
| Pattern search | O(m log n) | O(1) | Binary search |
| Pattern search (LCP-optimised) | O(m + log n) | O(1) | Manber-Myers improvement |
| LCP Array (Kasai) | O(n) | O(n) | From SA in linear time |
| Longest repeated substring | O(n) | O(n) | Max LCP value |
| Distinct substrings | O(n) | O(1) | n(n+1)/2 - sum(LCP) |
| K-th lexicographic substring | O(n) | O(1) | Cumulative on LCP |

---

## Comparison with Alternatives

| Feature | Suffix Array | Suffix Tree | Rolling Hash | KMP |
|---------|-------------|-------------|-------------|-----|
| Build | O(n) optimal | O(n) | O(n) | — |
| Pattern search | O(m log n) | O(m) | O(n) expected | O(n+m) |
| Memory | O(n) | O(20n) | O(n) | O(m) |
| LCP queries | Yes | Yes | No | No |
| Longest repeated substr | O(n) | O(n) | O(n²) | N/A |
| Distinct substrings | O(n) | O(n) | O(n²) | N/A |
| Implementation difficulty | Medium | Hard | Easy | Medium |

**When NOT to use suffix array:**
- Single pattern search: KMP or Boyer-Moore is faster (no preprocessing)
- Rolling hash with expectation of few matches: simpler
- Streaming data: cannot build SA incrementally
- Small n (<100): simpler approaches dominate

---

## Use Cases

### 1. Genome Sequence Mapping (BWA, Bowtie)
**System**: DNA read alignment to reference genome (3B base pairs)
**Why SA**: Build once (SA-IS O(n)), then search millions of reads. Reference genome index fits in ~12GB.
**Search**: Each read (100-150 bp) binary-searched in SA.

### 2. Plagiarism Detection (MOSS)
**System**: Measure code similarity across submissions
**Why SA**: LCP array finds longest common substrings between documents
**Algorithm**: Concatenate submissions with sentinels, build SA, find all LCP > threshold

### 3. Data Compression (bzip2)
**System**: bzip2 uses Burrows-Wheeler Transform (BWT)
**Why SA**: BWT can be computed from SA in O(n): BWT[i] = S[(SA[i]-1+n) % n]
**Result**: Suffix array enables fast BWT construction for compression

### 4. Full-Text Search
**System**: Search within documents (less common than inverted index)
**Why SA**: Better for phrase queries (adjacent words in LCP)
**Inverted index vs SA**: Inverted index is better for keyword search, SA for substring search

### 5. String Mining / Bioinformatics
**Why SA+LCP**: Find repeated patterns (LCP peaks), unique regions (LCP valleys), k-mer coverage analysis. Used in genomic repeat detection.

### 6. Shortest Unique Substring
**Problem**: Find shortest substring that appears only once
**Solution**: LCP[i] vs LCP[i+1] — substring is unique if its depth exceeds both neighbours

---

## Common Pitfalls

### 1. Sentinel Character
The sentinel `$` must be lexicographically smaller than all other characters. ASCII `$` (36) works for ASCII strings but may not for extended characters.

### 2. 0-Index vs 1-Index Confusion
All suffix arrays in this guide use 0-indexed positions. When porting to 1-indexed languages (Fortran, R), add 1.

### 3. O(n²) Build by Mistake
Using substring extraction in comparator: `s.substring(a).compareTo(s.substring(b))` is O(n² log n). Always use rank-based comparison.

### 4. LCP Array Off-by-One
LCP[i] corresponds to adjacent suffixes SA[i-1] and SA[i]. LCP[0] is undefined. Some implementations use LCP of size n+1 with LCP[0] = LCP[n] = 0.

### 5. Memory for Large Strings
For n = 10⁸ (human genome), SA needs 4n bytes = 400MB. LCP needs 4n more. With Java overhead, >1GB. Use external memory algorithms.

---

## Advanced Variants

### SA-IS (Suffix Array Induced Sorting)
Linear-time O(n) SA construction. Complex but the fastest in practice. Uses LMS (Left-Most S) character types.

### FM-Index (Full-text Index in Minute space)
Suffix array + BWT + wavelet tree. Pattern search in O(m) with sublinear space (~2-5 bits per character). Used in Bowtie2, BWA-MEM.

### Enhanced Suffix Array
Suffix array + LCP + RMQ (range minimum query) for simulating suffix tree operations. Suffix tree functionality with suffix array space.

### Burrows-Wheeler Transform (BWT)
Permutation of string derived from SA. Core of bzip2 compression. BWT[i] = S[(SA[i]-1+n) % n]. Used in sequence aligners.

---

## Testing the Implementation

```java
String s = "banana";
int[] sa = buildSuffixArray(s);
int[] lcp = buildLCP(s, sa);

// SA: positions of sorted suffixes
assert sa[0] == 5 : "$ position should be last char";
assert sa[1] == 3 : "a$";

// LCP
assert lcp[2] == 1 : "a$ vs ana$: LCP 'a' = 1";

// Search
assert search(s, sa, "ana") == 3;
assert search(s, sa, "ban") == 0;
assert search(s, sa, "xyz") == -1;
```

### Edge Case Tests
```java
// Single character
int[] sa1 = buildSuffixArray("a");
assert sa1[0] == 0;

// Repeated characters
int[] sa2 = buildSuffixArray("aaaa");
// SA: [3, 2, 1, 0] for "a", "aa", "aaa", "aaaa"

// Empty string (handle separately)
// buildSuffixArray("") → return empty array
```

---

## Key Interview Takeaways

1. **Suffix array = fast substring search** for large texts with preprocessing cost.

2. **LCP array enables string mining**: longest repeated substring = max LCP; distinct substrings = total - sum(LCP).

3. **SA-IS is O(n)** but you don't need to implement it. Prefix doubling O(n log n) is fine for interviews.

4. **Pattern search in O(m log n)**: binary search, each comparison extracts up to m chars.

5. **FM-Index**: the production version of suffix arrays for genomics, supporting O(m) search in compressed space.

6. **Know the LCP property**: when sorted adjacently, suffixes share a common prefix of length LCP[i]. This is the source of most optimisation.