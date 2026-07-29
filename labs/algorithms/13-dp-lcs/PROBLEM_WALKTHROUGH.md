# LeetCode 1143 — Longest Common Subsequence — Problem Walkthrough

## Problem Statement

Given two strings `text1` and `text2`, return the **length** of their longest common subsequence (LCS). A subsequence is a sequence that can be derived by deleting some or no characters without changing the order of the remaining characters.

**Constraints:**
- `1 <= text1.length, text2.length <= 1000`
- Strings consist of lowercase English letters.

**Examples:**
```
Input:  text1 = "abcde", text2 = "ace"
Output: 3
Explanation: LCS is "ace".

Input:  text1 = "abc", text2 = "abc"
Output: 3
Explanation: LCS is "abc".

Input:  text1 = "abc", text2 = "def"
Output: 0
Explanation: No common subsequence.
```

---

## Step-by-Step Solution

### Step 1: Define State

Let `dp[i][j]` = length of LCS of `text1[0..i-1]` and `text2[0..j-1]`.

### Step 2: Recurrence

If characters match:
```
dp[i][j] = 1 + dp[i-1][j-1]
```
Otherwise:
```
dp[i][j] = max(dp[i-1][j], dp[i][j-1])
```

### Step 3: Base Cases

`dp[0][j] = 0` and `dp[i][0] = 0` — empty string has LCS length 0.

### Step 4: Path Reconstruction

To recover the actual subsequence, we trace back through the DP table:
- If `text1[i-1] == text2[j-1]`, the character is part of LCS; move diagonally.
- Otherwise, move in the direction of the larger value (up or left).

---

## Full Compilable Solution

```java
import java.util.Arrays;

/**
 * LeetCode 1143 — Longest Common Subsequence
 *
 * Classic DP with O(m*n) time and space.
 * Includes path reconstruction to recover the actual subsequence.
 *
 * Time:  O(m * n)
 * Space: O(m * n)
 */
public class LongestCommonSubsequence {

    public int longestCommonSubsequence(String text1, String text2) {
        int m = text1.length(), n = text2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    dp[i][j] = 1 + dp[i - 1][j - 1];
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp[m][n];
    }

    /**
     * Reconstruct the actual LCS string from the DP table.
     */
    public String lcsString(String text1, String text2) {
        int m = text1.length(), n = text2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    dp[i][j] = 1 + dp[i - 1][j - 1];
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }

        // Trace back
        StringBuilder sb = new StringBuilder();
        int i = m, j = n;
        while (i > 0 && j > 0) {
            if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                sb.append(text1.charAt(i - 1));
                i--; j--;
            } else if (dp[i - 1][j] > dp[i][j - 1]) {
                i--;
            } else {
                j--;
            }
        }
        return sb.reverse().toString();
    }

    public static void main(String[] args) {
        LongestCommonSubsequence s = new LongestCommonSubsequence();

        runTest(s, "abcde", "ace", 3, "ace");
        runTest(s, "abc", "abc", 3, "abc");
        runTest(s, "abc", "def", 0, "");
        runTest(s, "", "abc", 0, "");
        runTest(s, "abcdef", "acef", 4, "acef");
        runTest(s, "AGGTAB", "GXTXAYB", 4, "GTAB");
        runTest(s, "abcdefghij", "cdgi", 4, "cdgi");
        runTest(s, "aaaa", "aa", 2, "aa");
        runTest(s, "abcd", "abcd", 4, "abcd");
        runTest(s, "abcde", "edcba", 1, "a"); // or b,c,d,e — any single common char
    }

    private static void runTest(LongestCommonSubsequence s, String t1, String t2,
                                 int expectedLen, String expectedStr) {
        int len = s.longestCommonSubsequence(t1, t2);
        String lcs = s.lcsString(t1, t2);
        String status = (len == expectedLen) ? "PASS" : "FAIL";
        System.out.printf("%s | LCS(%s, %s) = %d (expected %d), string = \"%s\"%n",
            status, t1, t2, len, expectedLen, lcs);
    }
}
```

---

## Space-Optimized Version (O(n) space)

```java
/**
 * Space-optimized LCS.
 * Since dp[i][*] only depends on dp[i-1][*], we keep two rows.
 *
 * Time:  O(m * n)
 * Space: O(min(m, n))
 */
public class LongestCommonSubsequenceOpt {

    public int longestCommonSubsequence(String text1, String text2) {
        if (text1.length() < text2.length()) {
            String tmp = text1; text1 = text2; text2 = tmp;
        }
        int m = text1.length(), n = text2.length();
        int[] prev = new int[n + 1];
        int[] curr = new int[n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    curr[j] = 1 + prev[j - 1];
                } else {
                    curr[j] = Math.max(prev[j], curr[j - 1]);
                }
            }
            int[] tmp = prev; prev = curr; curr = tmp;
            // Note: curr is now the old prev (reused)
        }
        return prev[n];
    }

    public static void main(String[] args) {
        LongestCommonSubsequenceOpt s = new LongestCommonSubsequenceOpt();
        System.out.println(s.longestCommonSubsequence("abcde", "ace") + " (expected: 3)");
        System.out.println(s.longestCommonSubsequence("abc", "def") + " (expected: 0)");
    }
}
```

---

## Complexity Analysis

| Version | Time | Space | Notes |
|---------|------|-------|-------|
| Full DP | O(m * n) | O(m * n) | Supports path reconstruction |
| Space-optimized | O(m * n) | O(min(m, n)) | Loses reconstruction ability |

- **Time:** O(m * n) is optimal — any LCS algorithm must compare characters across both strings.
- **Space:** O(m * n) for reconstruction; O(min(m, n)) for length-only.
- **Path reconstruction** adds no asymptotic time overhead (same O(m * n) to fill table + O(m + n) to trace).

---

## Follow-Up: Shortest Common Supersequence (LeetCode 1092)

Given two strings, return the shortest string that has both as subsequences.

**Approach:** Build from LCS: merge characters not in LCS, then insert LCS characters once.

```java
public class ShortestCommonSupersequence {

    public String shortestCommonSupersequence(String str1, String str2) {
        int m = str1.length(), n = str2.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 1; i <= m; i++)
            for (int j = 1; j <= n; j++)
                dp[i][j] = (str1.charAt(i - 1) == str2.charAt(j - 1))
                    ? 1 + dp[i - 1][j - 1]
                    : Math.max(dp[i - 1][j], dp[i][j - 1]);

        StringBuilder sb = new StringBuilder();
        int i = m, j = n;
        while (i > 0 || j > 0) {
            if (i == 0) { sb.append(str2.charAt(--j)); }
            else if (j == 0) { sb.append(str1.charAt(--i)); }
            else if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                sb.append(str1.charAt(i - 1)); i--; j--;
            } else if (dp[i - 1][j] > dp[i][j - 1]) {
                sb.append(str1.charAt(--i));
            } else {
                sb.append(str2.charAt(--j));
            }
        }
        return sb.reverse().toString();
    }

    public static void main(String[] args) {
        ShortestCommonSupersequence s = new ShortestCommonSupersequence();
        System.out.println(s.shortestCommonSupersequence("abac", "cab")
            + " (expected: cabac)");
    }
}
```

---

## Edge Cases & Test Coverage

| Case | text1 | text2 | LCS Len | LCS | Notes |
|------|-------|-------|---------|-----|-------|
| Empty first | "" | "abc" | 0 | "" | One empty |
| Identical | "abc" | "abc" | 3 | "abc" | Full match |
| No match | "abc" | "def" | 0 | "" | No common chars |
| Reverse | "abcde" | "edcba" | 1 | "a" | Single char match |
| Substring | "abcdef" | "bcd" | 3 | "bcd" | One is substring |
| Repeated | "aaaa" | "aa" | 2 | "aa" | Duplicates |

---

## Key Takeaways

1. **LCS is the foundation** of sequence comparison: diff tools, DNA sequence alignment, plagiarism detection.
2. **Path reconstruction** is a common DP follow-up — trace back with a decision matrix.
3. **Space optimization** (rolling arrays) is critical for large inputs (m, n up to 1000 → 1M entries = 8MB).
4. The recurrence `max(dp[i-1][j], dp[i][j-1])` captures the "skip a character" intuition.
5. LCS generalizes to: Longest Palindromic Subsequence (LCS of string and its reverse), Shortest Common Supersequence, Edit Distance.