# LeetCode 3 — Longest Substring Without Repeating Characters

## Problem

Given a string `s`, find the **length of the longest substring** without repeating characters.

**Constraints:**
- `0 <= s.length <= 5 * 10^4`
- `s` consists of English letters, digits, symbols, and spaces.

---

## Solution 1: Sliding Window with HashMap

```java
import java.util.*;

/**
 * LeetCode 3 — Longest Substring Without Repeating Characters
 * Sliding window with HashMap tracking last index of each character.
 *
 * Time: O(n) | Space: O(min(m, n)) where m = charset size
 */
public class LongestSubstringNoRepeat {

    public int lengthOfLongestSubstring(String s) {
        Map<Character, Integer> map = new HashMap<>();
        int maxLen = 0;
        int left = 0;

        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            if (map.containsKey(c)) {
                left = Math.max(left, map.get(c) + 1);
            }
            map.put(c, right);
            maxLen = Math.max(maxLen, right - left + 1);
        }
        return maxLen;
    }

    public static void main(String[] args) {
        LongestSubstringNoRepeat s = new LongestSubstringNoRepeat();

        System.out.println("Test 1: " + s.lengthOfLongestSubstring("abcabcbb") + " (expected: 3)");
        System.out.println("Test 2: " + s.lengthOfLongestSubstring("bbbbb") + " (expected: 1)");
        System.out.println("Test 3: " + s.lengthOfLongestSubstring("pwwkew") + " (expected: 3)");
        System.out.println("Test 4: " + s.lengthOfLongestSubstring("") + " (expected: 0)");
        System.out.println("Test 5: " + s.lengthOfLongestSubstring(" ") + " (expected: 1)");
        System.out.println("Test 6: " + s.lengthOfLongestSubstring("au") + " (expected: 2)");
        System.out.println("Test 7: " + s.lengthOfLongestSubstring("dvdf") + " (expected: 3)");
        System.out.println("Test 8: " + s.lengthOfLongestSubstring("tmmzuxt") + " (expected: 5)");
    }
}
```

---

## Solution 2: Sliding Window with Integer Array (Faster for ASCII)

```java
import java.util.*;

/**
 * LeetCode 3 — Longest Substring Without Repeating Characters
 * Sliding window with int[128] for ASCII — faster than HashMap.
 *
 * Time: O(n) | Space: O(1)
 */
public class LongestSubstringNoRepeatArray {

    public int lengthOfLongestSubstring(String s) {
        int[] lastIndex = new int[128];
        Arrays.fill(lastIndex, -1);
        int maxLen = 0;
        int left = 0;

        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            if (lastIndex[c] >= left) {
                left = lastIndex[c] + 1;
            }
            lastIndex[c] = right;
            maxLen = Math.max(maxLen, right - left + 1);
        }
        return maxLen;
    }

    public static void main(String[] args) {
        LongestSubstringNoRepeatArray s = new LongestSubstringNoRepeatArray();

        System.out.println("Test 1: " + s.lengthOfLongestSubstring("abcabcbb") + " (expected: 3)");
        System.out.println("Test 2: " + s.lengthOfLongestSubstring("bbbbb") + " (expected: 1)");
        System.out.println("Test 3: " + s.lengthOfLongestSubstring("pwwkew") + " (expected: 3)");
        System.out.println("Test 4: " + s.lengthOfLongestSubstring("") + " (expected: 0)");
        System.out.println("Test 5: " + s.lengthOfLongestSubstring("abcdefghijklmnopqrstuvwxyz")
            + " (expected: 26)");
        System.out.println("Test 6: " + s.lengthOfLongestSubstring("tmmzuxt") + " (expected: 5)");
    }
}
```

---

## Complexity Analysis

| Approach | Time | Space |
|----------|------|-------|
| HashMap Sliding Window | O(n) | O(min(m, n)) |
| Array Sliding Window | O(n) | O(1) |

### Window Invariant

The window `[left, right]` always contains a substring without repeating characters. When a repeat is found at position `right`:
- **HashMap version**: `left` jumps to `lastIndex[c] + 1` (but not backwards, hence `Math.max`).
- **Array version**: Only updates `left` if `lastIndex[c] >= left` (the repeated character is in the current window).

### Why `Math.max(left, map.get(c) + 1)`?

Without `Math.max`, `left` could move backwards. For example, `"abba"`: when processing the second `'a'`, `map.get('a')` is `0`, so `left` would become `1`. But `left` is already at `2` (after processing the second `'b'`). `Math.max` ensures `left` only moves forward.
