# Interview Questions: Suffix Array

## 17 FAANG-Style Interview Questions

### Question 1
> Implement a suffix array for a given string. What's the time complexity?

**Answer:**
Prefix doubling: O(n log n) build, O(n) space.

```java
int[] buildSuffixArray(String s) {
    int n = s.length();
    Integer[] idx = new Integer[n];
    int[] rank = new int[n], tmp = new int[n];
    for (int i = 0; i < n; i++) { idx[i] = i; rank[i] = s.charAt(i); }

    for (int k = 1; k < n; k *= 2) {
        int step = k;
        Arrays.sort(idx, (a, b) -> {
            if (rank[a] != rank[b]) return Integer.compare(rank[a], rank[b]);
            int ra = a + step < n ? rank[a + step] : -1;
            int rb = b + step < n ? rank[b + step] : -1;
            return Integer.compare(ra, rb);
        });
        tmp[idx[0]] = 0;
        for (int i = 1; i < n; i++) {
            int a = idx[i-1], b = idx[i];
            tmp[b] = tmp[a] + (rank[a] == rank[b] && (a+step<n?rank[a+step]:-1) == (b+step<n?rank[b+step]:-1) ? 0 : 1);
        }
        int[] swap = rank; rank = tmp; tmp = swap;
        if (rank[idx[n-1]] == n-1) break;
    }
    int[] sa = new int[n];
    for (int i = 0; i < n; i++) sa[i] = idx[i];
    return sa;
}
```

---

### Question 2
> Given a suffix array, compute the LCP array. What's the algorithm?

**Answer:**
Kasai's algorithm: O(n) time, O(n) space. Walk the string in original order, using SA inverse to find the previous suffix in SA order.

```java
int[] buildLCP(String s, int[] sa) {
    int n = s.length();
    int[] lcp = new int[n], rank = new int[n];
    for (int i = 0; i < n; i++) rank[sa[i]] = i;
    int len = 0;
    for (int i = 0; i < n; i++) {
        if (rank[i] > 0) {
            int j = sa[rank[i] - 1];
            while (i + len < n && j + len < n && s.charAt(i+len) == s.charAt(j+len)) len++;
            lcp[rank[i]] = len;
            if (len > 0) len--;
        }
    }
    return lcp;
}
```

---

### Question 3
> Find the longest repeated substring in a string using O(n) time after suffix array construction.

**Answer:**
Build SA + LCP. The longest repeated substring corresponds to the maximum value in the LCP array at the corresponding positions.

```java
String longestRepeated(String s) {
    int[] sa = buildSuffixArray(s);
    int[] lcp = buildLCP(s, sa);
    int maxIdx = 0;
    for (int i = 1; i < lcp.length; i++) {
        if (lcp[i] > lcp[maxIdx]) maxIdx = i;
    }
    return lcp[maxIdx] > 0 ? s.substring(sa[maxIdx], sa[maxIdx] + lcp[maxIdx]) : "";
}
```

**Complexity**: O(n log n) (dominated by SA build). LCP scan is O(n).

---

### Question 4
> Count the number of distinct substrings of a string.

**Answer:**
Total substrings = n(n+1)/2. Subtract duplicates = sum of LCP array.

```java
long distinctSubstrings(String s) {
    int[] sa = buildSuffixArray(s);
    int[] lcp = buildLCP(s, sa);
    long total = (long) s.length() * (s.length() + 1) / 2;
    long sumLCP = 0;
    for (int v : lcp) sumLCP += v;
    return total - sumLCP;
}
```

**Explanation**: Each suffix contributes n-sa[i] substrings. Adjacent suffixes in SA share LCP[i] substrings that were already counted. Total - sum(LCP) = distinct.

---

### Question 5
> Find the k-th lexicographically smallest substring. Can you do it efficiently?

**Answer:**
Use SA + LCP to compute cumulative distinct substrings per suffix. Each suffix SA[i] contributes (n - SA[i]) - LCP[i] new substrings. Walk the SA, accumulating counts until reaching k, then emit the substring from SA[i] with appropriate length.

**Complexity**: O(n) after SA+LCP build.

---

### Question 6
> Given a string S and a pattern P, find all occurrences of P in S using suffix array.

**Answer:**
Binary search on SA to find the first and last occurrence. The first match gives the smallest index, the last gives the largest. All occurrences are contiguous in SA (by lexicographic property).

```java
int countOccurrences(String s, int[] sa, String p) {
    int m = p.length(), n = s.length();
    int lo = 0, hi = n - 1;
    // Find first occurrence
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        String suffix = s.substring(sa[mid], Math.min(sa[mid] + m, n));
        if (p.compareTo(suffix) <= 0) hi = mid - 1;
        else lo = mid + 1;
    }
    int first = lo;
    // Find last occurrence
    lo = 0; hi = n - 1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        String suffix = s.substring(sa[mid], Math.min(sa[mid] + m, n));
        if (p.compareTo(suffix) >= 0) lo = mid + 1;
        else hi = mid - 1;
    }
    int last = hi;
    return last - first + 1;
}
```

---

### Question 7
> Explain how the Burrows-Wheeler Transform (BWT) is derived from the suffix array.

**Answer:**
Given SA for string S$:
```
BWT[i] = S[(SA[i] - 1 + n) % n]
```
The BWT takes the character preceding each suffix. This permutes the string so that characters preceding similar contexts are adjacent, enabling run-length encoding (used in bzip2).

**Example S = "banana$":**
SA = [6,5,3,1,0,4,2]; BWT = S[-1+n], S[4], S[2], S[0], S[5], S[3], S[1] = "annb$aa"

---

### Question 8
> Find the shortest unique substring that appears only once in a string.

**Answer:**
For each position in SA (considering suffix SA[i]), the shortest unique substring starting at SA[i] has length = max(LCP[i], LCP[i+1]) + 1. Take the minimum across all i.

```java
String shortestUnique(String s) {
    int[] sa = buildSuffixArray(s);
    int[] lcp = buildLCP(s, sa);
    int n = s.length();
    int minLen = n + 1, pos = -1;
    for (int i = 0; i < n; i++) {
        int prevLCP = (i == 0) ? 0 : lcp[i];
        int nextLCP = (i == n-1) ? 0 : lcp[i+1];
        int len = Math.max(prevLCP, nextLCP) + 1;
        if (sa[i] + len <= n && len < minLen) {
            minLen = len;
            pos = sa[i];
        }
    }
    return pos >= 0 ? s.substring(pos, pos + minLen) : "";
}
```

---

### Question 9
> Compare suffix array, suffix tree, and trie for pattern matching. When to use each?

**Answer:**
| Structure | Build Time | Pattern Search | Memory | Use Case |
|-----------|-----------|---------------|--------|----------|
| Trie | O(NL) | O(L) | O(NL·Σ) | Dictionary / prefix |
| Suffix Tree | O(n) | O(m) | O(20n) | Substring queries |
| Suffix Array | O(n log n) | O(m log n) | O(4n) | Large-scale string |

**Use suffix array** when memory is constrained (genome indexing).
**Use suffix tree** when O(m) pattern search needed (real-time matching).
**Use trie** for dictionary word search (not substring).

---

### Question 10
> Design a plagiarism detection system using suffix arrays.

**Answer:**
1. Concatenate all documents with unique sentinels (e.g., doc1 + '$' + doc2 + '#' + doc3 + '%')
2. Build SA + LCP for the concatenated string
3. Find all LCP entries > threshold T (e.g., 20 characters)
4. Map each LCP interval back to source documents
5. Flag intervals that span different documents as plagiarised

**Optimisation**: For 10K documents, use rolling hash first for candidate pairs, then suffix array for confirmation.

---

### Question 11
> Given two strings A and B, find their longest common substring.

**Answer:**
Concatenate A + '$' + B + '#', build SA + LCP. Find max LCP where the two suffixes come from different original strings (checked by position).

**Complexity**: O((|A|+|B|) log (|A|+|B|))

---

### Question 12
> How would you find the longest palindrome in a string using a suffix array?

**Answer:**
Create S' = S + "$" + reverse(S) + "#". Build SA + LCP. For each position i in S, find the longest prefix common between suffix starting at i in S and suffix starting at corresponding position in reversed string. The LCP of these two positions gives half-palindrome length.

**Alternative**: Use Manacher's algorithm O(n). Suffix array approach is O(n log n) — mainly pedagogical.

---

### Question 13
> Given a string, find the lexicographically smallest rotation.

**Answer:**
The smallest suffix in the suffix array gives the smallest rotation starting point. But we need full rotation (same length as original). Build SA for S + S (doubled), then the first suffix in SA with length ≥ n gives the smallest rotation.

---

### Question 14
> Explain the SA-IS algorithm at a high level.

**Answer:**
SA-IS (Induced Sorting) builds the suffix array in O(n) time:
1. Classify characters as L-type (larger than next) or S-type (smaller than next)
2. Identify LMS (Left-Most S) characters — positions where type changes from L to S
3. Sort LMS suffixes recursively
4. Use induced sorting to insert remaining suffixes between LMS positions

**Key**: Linear time by using the recursive structure of suffix sorting.

---

### Question 15
> Given a large text (10GB) that doesn't fit in memory, how would you build a suffix array?

**Answer:**
**External memory suffix array construction:**
1. **Split**: Divide text into blocks that fit in memory (e.g., 256MB)
2. **Build partial SA**: Build SA for each block
3. **Merge**: External merge sort of partial SAs
4. **Optimisation**: Use prefix doubling's O(n log n) I/O efficiency

**Alternative**: Use the FM-index which stores the BWT in compressed form (~1 bit per character for DNA).

---

### Question 16
> Given a suffix array, how do you simulate a suffix tree (child, suffix link)?

**Answer:**
Enhanced suffix array adds:
- **LCP-based intervals**: An interval [l, r] in SA with LCP value d represents a suffix tree node at depth d
- **Child table**: For each node, the first child interval
- **Suffix links**: next occurrence of same substring starting at next position

**Operations**: Parent, child, suffix link all in O(1) using RMQ on LCP array.

---

### Question 17
> Find the most frequent substring of length k in a string.

**Answer:**
1. Build SA + LCP
2. Sliding window over LCP: for positions where LCP[i] ≥ k, the substring S[SA[i]:SA[i]+k] repeats
3. Group consecutive positions with LCP ≥ k (they share the same k-length prefix)
4. The largest group gives the most frequent substring of length k

**Complexity**: O(n log n) for SA build, O(n) for scan.