# LeetCode 76 — Minimum Window Substring — Problem Walkthrough

## Problem Statement

Given two strings `s` and `t`, return the **minimum window substring** of `s` such that every character in `t` (including duplicates) is included in the window. If no such substring exists, return an empty string `""`.

**Constraints:**
- `1 <= s.length, t.length <= 10^5`
- `s` and `t` consist of uppercase and lowercase English letters.

**Examples:**
```
Input:  s = "ADOBECODEBANC", t = "ABC"
Output: "BANC"
Explanation: The minimum window containing A, B, C is "BANC".

Input:  s = "a", t = "a"
Output: "a"

Input:  s = "a", t = "aa"
Output: ""
Explanation: Both 'a's must be in the window, but s has only one.
```

---

## Step-by-Step Solution

### Step 1: Sliding Window Framework

1. Expand the right pointer until the window contains all characters of `t`.
2. Shrink the left pointer as much as possible while still containing all of `t`.
3. Record the minimum window length during shrinking.
4. Repeat until the right pointer reaches the end.

### Step 2: Tracking Character Requirements

- Use a frequency map (or array of size 128 for ASCII) for `t`.
- Maintain a `required` counter = how many distinct characters in `t`.
- Maintain a `formed` counter = how many distinct characters in the current window meet the required frequency.

When `formed == required`, the window is "valid" — it contains all of `t`.

### Step 3: Optimization Notes

- Use `int[128]` instead of `HashMap<Character, Integer>` for O(1) character access.
- Track characters in `t` with a frequency array `targetFreq`.
- Use a `windowFreq` array to track frequencies in the current window.

---

## Full Compilable Solution

```java
import java.util.Arrays;

/**
 * LeetCode 76 — Minimum Window Substring
 *
 * Sliding window with frequency arrays and a "formed" counter.
 *
 * Time:  O(|S| + |T|)
 * Space: O(1) — fixed 128-element arrays
 */
public class MinimumWindowSubstring {

    public String minWindow(String s, String t) {
        int m = s.length(), n = t.length();
        if (m < n) return "";

        int[] targetFreq = new int[128];
        int[] windowFreq = new int[128];

        int required = 0;
        for (char c : t.toCharArray()) {
            if (targetFreq[c] == 0) required++;
            targetFreq[c]++;
        }

        int left = 0, formed = 0;
        int minLen = Integer.MAX_VALUE;
        int minStart = 0;

        for (int right = 0; right < m; right++) {
            char rc = s.charAt(right);
            windowFreq[rc]++;

            if (windowFreq[rc] == targetFreq[rc]) {
                formed++;
            }

            while (left <= right && formed == required) {
                int len = right - left + 1;
                if (len < minLen) {
                    minLen = len;
                    minStart = left;
                }

                char lc = s.charAt(left);
                windowFreq[lc]--;
                if (windowFreq[lc] < targetFreq[lc]) {
                    formed--;
                }
                left++;
            }
        }

        return minLen == Integer.MAX_VALUE ? "" : s.substring(minStart, minStart + minLen);
    }

    public static void main(String[] args) {
        MinimumWindowSubstring s = new MinimumWindowSubstring();

        runTest(s, "ADOBECODEBANC", "ABC", "BANC");
        runTest(s, "a", "a", "a");
        runTest(s, "a", "aa", "");
        runTest(s, "a", "b", "");
        runTest(s, "ab", "b", "b");
        runTest(s, "abc", "ac", "abc");
        runTest(s, "aa", "aa", "aa");
        runTest(s, "abcdef", "xyz", "");
        runTest(s, "abcaaaaaaabc", "abc", "abc");
        runTest(s, "bbaac", "aba", "baa");
        // Large test
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5000; i++) sb.append('a');
        sb.append('b');
        for (int i = 0; i < 5000; i++) sb.append('a');
        String largeS = sb.toString();
        runTest(s, largeS, "b", "b");
    }

    private static void runTest(MinimumWindowSubstring solver, String s, String t,
                                String expected) {
        String result = solver.minWindow(s, t);
        String status = result.equals(expected) ? "PASS" : "FAIL";
        System.out.printf("%s | minWindow(%s, %s) = \"%s\" (expected \"%s\")%n",
            status, s, t, result, expected);
    }
}
```

---

## HashMap Variant (for clarity)

```java
import java.util.*;

/**
 * HashMap-based sliding window.
 * Slower but easier to understand.
 *
 * Time:  O(|S| + |T|)
 * Space: O(|T|)
 */
public class MinimumWindowSubstringMap {

    public String minWindow(String s, String t) {
        Map<Character, Integer> target = new HashMap<>();
        for (char c : t.toCharArray())
            target.put(c, target.getOrDefault(c, 0) + 1);

        int left = 0, formed = 0, required = target.size();
        int minLen = Integer.MAX_VALUE, minStart = 0;
        Map<Character, Integer> window = new HashMap<>();

        for (int right = 0; right < s.length(); right++) {
            char rc = s.charAt(right);
            window.put(rc, window.getOrDefault(rc, 0) + 1);
            if (target.containsKey(rc) && window.get(rc).intValue() == target.get(rc).intValue())
                formed++;

            while (left <= right && formed == required) {
                if (right - left + 1 < minLen) {
                    minLen = right - left + 1;
                    minStart = left;
                }
                char lc = s.charAt(left);
                window.put(lc, window.get(lc) - 1);
                if (target.containsKey(lc) && window.get(lc) < target.get(lc))
                    formed--;
                left++;
            }
        }
        return minLen == Integer.MAX_VALUE ? "" : s.substring(minStart, minStart + minLen);
    }

    public static void main(String[] args) {
        MinimumWindowSubstringMap s = new MinimumWindowSubstringMap();
        System.out.println(s.minWindow("ADOBECODEBANC", "ABC") + " (expected: BANC)");
    }
}
```

---

## Complexity Analysis

| Version | Time | Space | Notes |
|---------|------|-------|-------|
| Frequency array | O(m + n) | O(1) | 128 fixed size — optimal |
| HashMap | O(m + n) | O(|t|) | More general but slower |

### Why O(1) Space with Arrays

- ASCII has 128 characters (enough for lowercase + uppercase letters).
- Two fixed-size arrays mean constant space regardless of input size.
- Character-to-index conversion is implicit (just cast to int).

---

## Follow-Up: Minimum Window Subsequence (LeetCode 727)

Unlike substring (contiguous characters from `t`), subsequence must preserve order but need not be contiguous.

**Approach:** DP where `dp[j]` = start index in `s` for matching `t[0..j]`.

```java
public class MinimumWindowSubsequence {

    public String minWindow(String s, String t) {
        int m = s.length(), n = t.length();
        int[] dp = new int[n];
        Arrays.fill(dp, -1);

        String result = "";

        for (int i = 0; i < m; i++) {
            if (s.charAt(i) == t.charAt(0)) dp[0] = i;

            for (int j = n - 1; j >= 1; j--) {
                if (s.charAt(i) == t.charAt(j) && dp[j - 1] != -1) {
                    dp[j] = dp[j - 1];
                }
            }

            if (dp[n - 1] != -1) {
                int len = i - dp[n - 1] + 1;
                if (result.isEmpty() || len < result.length()) {
                    result = s.substring(dp[n - 1], i + 1);
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        MinimumWindowSubsequence s = new MinimumWindowSubsequence();
        System.out.println(s.minWindow("abcdebdde", "bde") + " (expected: bcde)");
    }
}
```

---

## Edge Cases & Test Coverage

| Case | s | t | Expected | Notes |
|------|---|----|----------|-------|
| Single match | `"a"` | `"a"` | `"a"` | Single char |
| No match | `"a"` | `"b"` | `""` | No chars |
| Duplicates | `"aa"` | `"aa"` | `"aa"` | Need both |
| Unreachable | `"a"` | `"aa"` | `""` | Not enough chars |
| Exact match | `"abc"` | `"abc"` | `"abc"` | Full string |
| Repeated target | `"abcaaaaaaabc"` | `"abc"` | `"abc"` | Earliest |
| Large alphabet | `"aAbBcC"` | `"ABC"` | `"aAbBcC"` | Case-sensitive |

---

## Key Takeaways

1. **Sliding window + frequency tracking** is the standard pattern for substring problems.
2. **`formed == required`** replaces nested frequency comparisons with a simple counter.
3. **Array-based frequency** (size 128) is faster than HashMap for ASCII inputs.
4. The **expanding-right, contracting-left** loop structure generalizes to many substring problems (Longest Substring Without Repeating, Longest Repeating Character Replacement, etc.).
5. Always compare `Integer` values with `.equals()` or `.intValue()` when using HashMap to avoid boxing comparison pitfalls.