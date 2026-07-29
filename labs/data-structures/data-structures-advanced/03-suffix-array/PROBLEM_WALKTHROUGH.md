# Problem Walkthrough: Longest Repeated Substring in DNA

## Problem Statement

**Title**: DNA Sequence Repeat Finder

**Difficulty**: Hard

**Category**: String Processing, Suffix Array, LCP

---

### Problem

A DNA sequence is represented as a string over the alphabet {A, C, G, T}. Given a DNA sequence of length n (up to 10⁶), find the longest substring that appears at least twice in the sequence. If multiple substrings have the same maximum length, return the lexicographically smallest one.

### Constraints

- `1 ≤ n ≤ 1,000,000`
- Input string contains only A, C, G, T (uppercase)
- Output the longest repeated substring. If none, return an empty string.

### Examples

**Example 1:**
```
Input: "ACGTACGT"
Output: "ACGT"
Explanation: "ACGT" appears at positions 0 and 4 (length 4)
```

**Example 2:**
```
Input: "GATTACA"
Output: "A"
Explanation: "A" appears 3 times (positions 1, 4, 6). All longer substrings are unique.
```

**Example 3:**
```
Input: "AAAA"
Output: "AAA"
Explanation: "AAA" appears at positions 0 and 1 (length 3)
```

**Example 4:**
```
Input: "ABCDEF"
Output: ""
Explanation: No substring repeats
```

---

## Step-by-Step Walkthrough

### Step 1: Understanding the Problem

We need to find the longest substring that appears ≥ 2 times. This is a classic LCP problem.

**Key insight**: If two suffixes share a common prefix of length L at positions i and j, then S[i:i+L] = S[j:j+L]. The longest such common prefix across any pair of suffixes gives the longest repeated substring.

### Step 2: Brute Force Approach

**Idea**: Generate all substrings, count frequencies using HashMap.

```java
for length from n down to 1:
    for each start:
        substring = s.substring(start, start + length)
        if map contains substring → return substring
        else map.put(substring, start)
```

**Complexity**: O(n³) — impossible for n = 10⁶. Even O(n²) substrings × O(L) comparison is ~10¹².

### Step 3: Optimal Solution — Suffix Array + LCP

**Idea**: 
1. Build suffix array
2. Build LCP array (LCP[i] = common prefix length between adjacent suffixes in SA)
3. The maximum LCP value gives the longest repeated substring
4. For lexicographically smallest tiebreaker, pick the earliest occurrence with max LCP

### Step 4: Java 21+ Compilable Solution

```java
import java.util.*;

public class LongestRepeatedSubstring {

    // ---------- Suffix Array (prefix doubling) ----------
    public static int[] buildSuffixArray(String s) {
        int n = s.length();
        Integer[] idx = new Integer[n];
        int[] rank = new int[n];
        int[] tmp = new int[n];

        for (int i = 0; i < n; i++) {
            idx[i] = i;
            rank[i] = s.charAt(i);
        }

        for (int k = 1;; k *= 2) {
            int step = k;
            Arrays.sort(idx, (a, b) -> {
                if (rank[a] != rank[b]) return Integer.compare(rank[a], rank[b]);
                int ra = a + step < n ? rank[a + step] : -1;
                int rb = b + step < n ? rank[b + step] : -1;
                return Integer.compare(ra, rb);
            });

            tmp[idx[0]] = 0;
            for (int i = 1; i < n; i++) {
                int a = idx[i - 1], b = idx[i];
                int ra1 = rank[a], rb1 = rank[b];
                int ra2 = a + step < n ? rank[a + step] : -1;
                int rb2 = b + step < n ? rank[b + step] : -1;
                tmp[b] = tmp[a] + (ra1 == rb1 && ra2 == rb2 ? 0 : 1);
            }

            int[] swap = rank;
            rank = tmp;
            tmp = swap;

            if (rank[idx[n - 1]] == n - 1) break;
        }

        int[] sa = new int[n];
        for (int i = 0; i < n; i++) sa[i] = idx[i];
        return sa;
    }

    // ---------- LCP Array (Kasai) ----------
    public static int[] buildLCP(String s, int[] sa) {
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
                if (len > 0) len--;
            }
        }
        return lcp;
    }

    // ---------- Main Logic ----------
    public static String longestRepeatedSubstring(String s) {
        int n = s.length();
        if (n <= 1) return "";

        int[] sa = buildSuffixArray(s);
        int[] lcp = buildLCP(s, sa);

        // Find max LCP value and corresponding position
        int maxLCP = 0;
        int pos = -1;

        for (int i = 1; i < n; i++) {
            if (lcp[i] > maxLCP) {
                maxLCP = lcp[i];
                pos = sa[i];
            } else if (lcp[i] == maxLCP && maxLCP > 0) {
                // Tie-break: lexicographically smallest
                int candidatePos = sa[i];
                String currentBest = s.substring(pos, pos + maxLCP);
                String candidate = s.substring(candidatePos, candidatePos + maxLCP);
                if (candidate.compareTo(currentBest) < 0) {
                    pos = candidatePos;
                }
            }
        }

        if (maxLCP == 0) return "";
        return s.substring(pos, pos + maxLCP);
    }

    // ---------- Test Harness ----------
    public static void main(String[] args) {
        // Example 1
        String res1 = longestRepeatedSubstring("ACGTACGT");
        System.out.println("Test 1: " + res1);
        assert res1.equals("ACGT") : "Expected ACGT, got " + res1;

        // Example 2
        String res2 = longestRepeatedSubstring("GATTACA");
        System.out.println("Test 2: " + res2);
        assert res2.equals("A") : "Expected A, got " + res2;

        // Example 3: all same chars
        String res3 = longestRepeatedSubstring("AAAA");
        System.out.println("Test 3: " + res3);
        assert res3.equals("AAA") : "Expected AAA, got " + res3;

        // Example 4: no repeats
        String res4 = longestRepeatedSubstring("ABCDEF");
        System.out.println("Test 4: '" + res4 + "'");
        assert res4.equals("") : "Expected empty, got " + res4;

        // Edge: empty string
        String res5 = longestRepeatedSubstring("");
        System.out.println("Test 5: '" + res5 + "'");
        assert res5.equals("") : "Expected empty for empty input";

        // Edge: single char
        String res6 = longestRepeatedSubstring("X");
        System.out.println("Test 6: '" + res6 + "'");
        assert res6.equals("") : "Expected empty for single char";

        // Edge: two chars, different
        String res7 = longestRepeatedSubstring("AB");
        System.out.println("Test 7: '" + res7 + "'");
        assert res7.equals("") : "Expected empty for AB";

        // Edge: two chars, same
        String res8 = longestRepeatedSubstring("AA");
        System.out.println("Test 8: " + res8);
        assert res8.equals("A") : "Expected A, got " + res8;

        // Test: DNA sequence with pattern
        String dna = "ATCGATCGATCG";
        String res9 = longestRepeatedSubstring(dna);
        System.out.println("Test 9: " + res9);
        assert res9.equals("ATCG") : "Expected ATCG, got " + res9;

        // Test: overlapping repeats
        String res10 = longestRepeatedSubstring("ABABABA");
        System.out.println("Test 10: " + res10);
        assert res10.equals("ABABA") : "Expected ABABA, got " + res10;

        // Large test: verify performance
        StringBuilder sb = new StringBuilder(1_000_000);
        for (int i = 0; i < 100_000; i++) sb.append("ACGT");
        String large = sb.toString();
        long start = System.currentTimeMillis();
        String resLarge = longestRepeatedSubstring(large);
        long elapsed = System.currentTimeMillis() - start;
        System.out.println("Large test (100K chars): len=" + resLarge.length()
            + " time=" + elapsed + "ms");
        assert elapsed < 30_000 : "Too slow: " + elapsed + "ms";

        System.out.println("\nAll tests passed!");
    }
}
```

### Step 5: Complexity Analysis

**Time Complexity**: O(n log n) dominated by suffix array construction (prefix doubling)
- SA build: O(n log n) with n up to 10⁶ → ~20M comparisons
- LCP build (Kasai): O(n)
- Scan: O(n)
- Total: O(n log n) time, O(n) space

**Space Complexity**: O(n)
- SA: 4n bytes
- LCP: 4n bytes
- Rank/temp: 4n bytes each
- Integer[] boxed: 4n bytes + object overhead
- Total: ~20n bytes for n = 1M → ~20MB

### Step 6: Edge Cases

| Case | Input | Expected | Description |
|------|-------|----------|-------------|
| Empty | "" | "" | No input |
| Single | "X" | "" | Single character can't repeat |
| No repeats | "ABC" | "" | All chars unique |
| All same | "AAAA" | "AAA" | Overlapping repeats |
| Tie-breaking | "ABAB" | "AB" | AB and BA both length 2, AB is lexicographically smaller |

### Step 7: Follow-Up Discussion

**Q: How would you find the longest repeated substring that does NOT overlap?**

Modify the approach: when evaluating LCP[i], check if the two suffix positions are non-overlapping. The longest non-overlapping repeated substring requires |sa[i] - sa[i-1]| ≥ LCP[i].

**Q: How would you find k occurrences instead of 2?**

Use a sliding window on LCP. Find the longest prefix common across k consecutive suffixes in SA. This is equivalent to RMQ on consecutive LCP values — any substring of length L repeats k times if LCP values in the range keep L ≥ L.

**Q: How to handle multiple longest substrings (return all, not just one)?**

Iterate LCP array, for each max-LCP position, extract the substring and add to a set if length equals max. Return all unique longest substrings.

**Q: Can we use rolling hash instead?**

Yes. Binary search on answer length L, use rolling hash (Rabin-Karp) to find if any substring of length L repeats. O(n log n) expected time, O(n) space. This is simpler to implement but has hash collision risk. The suffix array approach is deterministic.