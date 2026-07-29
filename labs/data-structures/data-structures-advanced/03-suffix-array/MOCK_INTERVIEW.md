# Mock Interview: Suffix Array

## Setting

- **Round**: Onsite coding round
- **Duration**: 45 minutes
- **Focus**: String processing, suffix array/LCP algorithms

---

## Transcript

### Part 1: Warm-up (5 min)

**Interviewer:** Let's start with a warm-up. How would you check if a string is a palindrome?

**Candidate:** Two-pointer technique: left = 0, right = n-1, while left < right, compare chars and move inward. O(n) time, O(1) space.

**Interviewer:** Good. Now, how would you find the longest palindromic substring?

**Candidate:** Expand around center — for each position (and between positions), expand outwards while the substring is a palindrome. O(n²) time, O(1) space. The optimal solution is Manacher's algorithm O(n).

**Interviewer:** Now let's pivot to a different string problem.

---

### Part 2: Core Problem — Longest Repeated Substring (25 min)

**Interviewer:** Given a string S, find the longest substring that appears at least twice. The substrings can overlap. For example, in "banana", the answer is "ana".

**Candidate:** Let me think about this. I need to find the longest common prefix between any two suffixes. The key insight is that a repeated substring of length L means there are two starting positions i ≠ j where S[i:i+L] = S[j:j+L].

**Interviewer:** That's the right direction. How would you implement it?

**Candidate:** I'd use a suffix array with LCP array. The suffix array sorts all suffixes of the string. The LCP array gives the longest common prefix between adjacent suffixes in sorted order. The maximum LCP value tells us the longest repeated substring.

Let me outline:
1. Build suffix array (I'll use prefix doubling, O(n log n))
2. Build LCP array using Kasai's algorithm (O(n))
3. Scan LCP for maximum value → that's our answer

**Interviewer:** Walk me through "banana" example.

**Candidate:**
String = "banana" (I'll add a sentinel '$')

Suffixes sorted:
```
$                  (pos 6)
a$                 (pos 5)
ana$               (pos 3)
anana$             (pos 1)
banana$            (pos 0)
na$                (pos 4)
nana$              (pos 2)
```

LCP array:
```
lcp[0] = 0 (undefined)
lcp[1] = 1  (a$ vs ana$ → "a")
lcp[2] = 3  (ana$ vs anana$ → "ana")
lcp[3] = 0  (anana$ vs banana$ → "")
lcp[4] = 0  (banana$ vs na$ → "")
lcp[5] = 2  (na$ vs nana$ → "na")
```

Max LCP = 3 at position 2 → substring starting at SA[2]=3 with length 3 = "ana"

**Interviewer:** Good. Now code it. Start with the suffix array construction.

**Candidate:**

```java
int[] buildSuffixArray(String s) {
    int n = s.length();
    Integer[] idx = new Integer[n];
    int[] rank = new int[n];
    int[] tmp = new int[n];

    for (int i = 0; i < n; i++) {
        idx[i] = i;
        rank[i] = s.charAt(i);
    }

    for (int k = 1; k < n; k *= 2) {
        int step = k;
        Arrays.sort(idx, (a, b) -> {
            int cmp = Integer.compare(rank[a], rank[b]);
            if (cmp != 0) return cmp;
            int ra = a + step < n ? rank[a + step] : -1;
            int rb = b + step < n ? rank[b + step] : -1;
            return Integer.compare(ra, rb);
        });

        tmp[idx[0]] = 0;
        for (int i = 1; i < n; i++) {
            int a = idx[i-1], b = idx[i];
            boolean same = rank[a] == rank[b]
                && (a + step < n ? rank[a + step] : -1)
                == (b + step < n ? rank[b + step] : -1);
            tmp[b] = tmp[a] + (same ? 0 : 1);
        }

        int[] swap = rank; rank = tmp; tmp = swap;
        if (rank[idx[n-1]] == n-1) break;
    }

    int[] sa = new int[n];
    for (int i = 0; i < n; i++) sa[i] = idx[i];
    return sa;
}
```

**Interviewer:** What's the complexity?

**Candidate:** O(n log n) time. There are log n doubling steps, each sorting n items. The comparator is O(1). Space is O(n) for the arrays.

**Interviewer:** Now the LCP array.

**Candidate:**

```java
int[] buildLCP(String s, int[] sa) {
    int n = s.length();
    int[] lcp = new int[n];
    int[] rank = new int[n];
    for (int i = 0; i < n; i++) rank[sa[i]] = i;

    int len = 0;
    for (int i = 0; i < n; i++) {
        if (rank[i] > 0) {
            int j = sa[rank[i] - 1];
            while (i + len < n && j + len < n && s.charAt(i + len) == s.charAt(j + len))
                len++;
            lcp[rank[i]] = len;
            if (len > 0) len--;  // crucial: max drops by 1
        }
    }
    return lcp;
}
```

**Interviewer:** Why does `len--` work?

**Candidate:** This is the key insight of Kasai's algorithm. If suffix at position i shares a prefix of length L with its predecessor in SA, then suffix at position i+1 shares a prefix of length at least L-1 with its predecessor. So we can reuse work and never compare characters we already know match.

**Interviewer:** Good. Now tie it together.

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

**Interviewer:** What's the space?

**Candidate:** O(n). We have SA (n ints), LCP (n ints), and rank (n ints). Plus the temporary arrays during build. About 20n bytes total.

---

### Part 3: Follow-up (10 min)

**Interviewer:** How would you handle a string that doesn't fit in memory?

**Candidate:** For external memory:
1. **Divide**: Split string into blocks (e.g., 256MB)
2. **Build partial SA**: Build SA for each block in memory
3. **Merge**: Use external merge sort on partial SAs, comparing suffixes by reading from disk

Each SA entry is stored with its block ID. Comparison may need to read from two different blocks, which we cache.

**Alternative**: Use the distributed approach (MapReduce). Partition by first k characters, then recursively.

**Interviewer:** How would you find ALL longest repeated substrings (not just one)?

**Candidate:** I'd scan the LCP array. When I find a new maximum, clear the result set and add this substring. When I find a tie, add it to the set. Need to be careful about duplicates: suffixes SA[i] and SA[i-1] with LCP[i] = max and SA[j] and SA[j-1] with LCP[j] = max might produce the same substring — use a Set.

**Interviewer:** What about finding repeated substrings that don't overlap?

**Candidate:** When evaluating LCP[i], check if |SA[i] - SA[i-1]| >= LCP[i]. If they overlap, the substring overlaps itself, which may not be desired. We need max LCP where the positions are at least LCP apart.

---

### Part 4: System Design Tie-in (5 min)

**Interviewer:** Design a plagiarism detection service for a coding platform.

**Candidate:**
1. **Input**: Code submissions (~100K chars each, 10K submissions per day)
2. **Preprocess**: Tokenize code (strip comments, normalise whitespace, normalise identifiers)
3. **Compare pairs**: For each new submission, compare against historical submissions using LCS (longest common subsequence via suffix array)
4. **Parallelise**: MapReduce — partition submissions into chunks, build suffix array per chunk, find long common substrings across chunk boundaries
5. **Threshold**: Flag if >30% of submission is a common substring with another submission

**Key optimisation**: For N submissions, we don't compare all pairs O(N²). We use Locality-Sensitive Hashing (LSH) to find candidate pairs, then suffix array for exact comparison.

---

## Debrief

### What Went Well
- Knew suffix array + LCP approach without prompting
- Explained Kasai's algorithm and the "len--" trick
- Handled the "banana" trace correctly
- Good follow-up on external memory and non-overlapping repeats

### Areas for Growth
- Could have mentioned suffix array without sentinel
- Memory analysis could include the Integer[] boxing overhead
- Patulous about time complexity (O(n log n) vs O(n²) for naive)

### Score
| Category | Score (1-5) |
|----------|-------------|
| Problem Understanding | 5 |
| Algorithm Design | 5 |
| Code Quality | 4 |
| Complexity Analysis | 5 |
| Follow-up Handling | 4 |
| Communication | 4 |
| **Overall** | **4.5 / 5** |