# LeetCode 72 — Edit Distance

## Problem

Given two strings `word1` and `word2`, return the **minimum number of operations** required to convert `word1` to `word2`.

You may perform three operations on a character:
- **Insert** a character
- **Delete** a character
- **Replace** a character

**Constraints:**
- `0 <= word1.length, word2.length <= 500`

---

## Solution: DP with Path Reconstruction

```java
import java.util.*;

/**
 * LeetCode 72 — Edit Distance
 * Full DP table with path reconstruction.
 *
 * Time: O(m * n) | Space: O(m * n)
 */
public class EditDistance {

    public int minDistance(String word1, String word2) {
        int m = word1.length(), n = word2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(
                        dp[i - 1][j],    // delete
                        Math.min(
                            dp[i][j - 1],    // insert
                            dp[i - 1][j - 1] // replace
                        )
                    );
                }
            }
        }
        return dp[m][n];
    }

    /**
     * Reconstructs the sequence of edit operations.
     * Returns a list of strings like "Delete 'a' at position 2".
     */
    public List<String> reconstruct(String word1, String word2) {
        int m = word1.length(), n = word2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(
                        dp[i - 1][j],
                        Math.min(dp[i][j - 1], dp[i - 1][j - 1])
                    );
                }
            }
        }

        List<String> ops = new ArrayList<>();
        int i = m, j = n;
        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && word1.charAt(i - 1) == word2.charAt(j - 1)) {
                i--; j--;
            } else if (i > 0 && dp[i][j] == dp[i - 1][j] + 1) {
                ops.add("Delete '" + word1.charAt(i - 1) + "' at index " + (i - 1));
                i--;
            } else if (j > 0 && dp[i][j] == dp[i][j - 1] + 1) {
                ops.add("Insert '" + word2.charAt(j - 1) + "' at index " + (j - 1));
                j--;
            } else if (i > 0 && j > 0) {
                ops.add("Replace '" + word1.charAt(i - 1) + "' with '" + word2.charAt(j - 1) + "' at index " + (i - 1));
                i--; j--;
            }
        }
        Collections.reverse(ops);
        return ops;
    }

    public static void main(String[] args) {
        EditDistance s = new EditDistance();

        // Test 1: Standard case
        int r1 = s.minDistance("horse", "ros");
        System.out.println("Test 1: " + r1 + " (expected: 3)");
        System.out.println("  Path: " + s.reconstruct("horse", "ros"));

        // Test 2: Insertions only
        int r2 = s.minDistance("", "abc");
        System.out.println("Test 2: " + r2 + " (expected: 3)");

        // Test 3: Deletions only
        int r3 = s.minDistance("abc", "");
        System.out.println("Test 3: " + r3 + " (expected: 3)");

        // Test 4: Identical strings
        int r4 = s.minDistance("hello", "hello");
        System.out.println("Test 4: " + r4 + " (expected: 0)");

        // Test 5: Full replacement
        int r5 = s.minDistance("intention", "execution");
        System.out.println("Test 5: " + r5 + " (expected: 5)");
        System.out.println("  Path: " + s.reconstruct("intention", "execution"));
    }
}
```

---

## Complexity Analysis

| Aspect | Value |
|--------|-------|
| Time Complexity | O(m * n) — fill the entire DP table |
| Space Complexity | O(m * n) — full DP table for reconstruction |

### Understanding the Recurrence

```
dp[i][j] = minimum edits to convert word1[0..i-1] to word2[0..j-1]

If word1[i-1] == word2[j-1]:
    dp[i][j] = dp[i-1][j-1]    // characters match, no operation needed
Else:
    dp[i][j] = 1 + min(
        dp[i-1][j],      // delete word1[i-1]
        dp[i][j-1],      // insert word2[j-1]
        dp[i-1][j-1]     // replace word1[i-1] with word2[j-1]
    )
```

### Path Reconstruction

Walking backwards through the DP table:
- If chars match: move diagonally (no operation)
- If value came from top: delete
- If value came from left: insert
- If value came from diagonal: replace

This produces an edit script showing the exact sequence of operations.
