# Mock Interview: Sliding Window (Longest Substring Without Repeating Characters)

## Meta Information

| Aspect | Detail |
|--------|--------|
| Company | Google |
| Level | L4 / SWE |
| Problem | Longest Substring Without Repeating Characters (LeetCode 3) |
| Duration | 45 minutes |
| Paradigm | Sliding Window |

---

## Transcript

### Phase 1: Problem Understanding (0:00–5:00)

**Interviewer:** Given a string, find the length of the longest substring without repeating characters.

**Candidate:** When you say substring, it must be contiguous, right? Not a subsequence?

**Interviewer:** Correct — contiguous characters.

**Candidate:** And by "characters" — are we just dealing with English letters, or any Unicode character?

**Interviewer:** For this problem, assume ASCII characters.

**Candidate:** So `"abcabcbb"` → longest is `"abc"` → length 3. `"bbbbb"` → longest is `"b"` → length 1. `"pwwkew"` → longest is `"wke"` → length 3. Is that correct?

**Interviewer:** Yes.

**Candidate:** What about the empty string? Should return 0.

**Interviewer:** Right.

### Phase 2: Approach Design (5:00–14:00)

**Candidate:** Let me consider the approaches.

**Brute force:** Check every possible substring `(i, j)` — O(n^2) substrings, and for each one check if it has duplicates in O(n) → O(n^3). Too slow.

**Sliding window with HashSet:** Maintain a window `[left, right]` that contains no duplicates. Expand `right` by one. If the new character is already in the window, shrink `left` until it's removed. Track the max window size.

**Sliding window with HashMap:** Same idea but store each character's *most recent index*. When we encounter a character that's already in the window, we jump `left` directly to `max(left, lastIndex[c] + 1)`. This avoids the incremental shrinking.

**Interviewer:** Why `Math.max`? Why not just `left = lastIndex[c] + 1`?

**Candidate:** Consider `"abba"`. When we process the second `b` at index 2:
- `lastIndex['b'] = 1`, so `left = 1 + 1 = 2`. Window becomes `[2,2]` = `"b"`. Correct.

Then we process the second `a` at index 3:
- `lastIndex['a'] = 0`. If we did `left = 0 + 1 = 1`, `left` would *decrease* from 2 to 1, which would incorrectly include `"bb"` in the window.
- With `Math.max`, `left = max(2, 0 + 1) = 2`. The window stays `[2,3]` = `"ba"`. Correct.

The `Math.max` ensures `left` never moves backwards. Characters outside the current window shouldn't affect the left bound.

### Phase 3: Coding (14:00–33:00)

**Candidate:** I'll implement the HashMap version first, then the optimized array version.

```java
class Solution {
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
            maxLen = max(maxLen, right - left + 1);
        }
        return maxLen;
    }
}
```

**Interviewer:** Could you optimize this further?

**Candidate:** Yes — since the problem states ASCII characters, we can use an `int[128]` array instead of a HashMap. This eliminates hashing overhead and boxing/unboxing.

```java
class Solution {
    public int lengthOfLongestSubstring(String s) {
        int[] lastIndex = new int[128];
        Arrays.fill(lastIndex, -1);
        int maxLen = 0, left = 0;

        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            if (lastIndex[c] >= left) {
                left = lastIndex[c] + 1;
            }
            lastIndex[c] = right;
            maxLen = max(maxLen, right - left + 1);
        }
        return maxLen;
    }
}
```

Note the condition change: `if (lastIndex[c] >= left)` instead of `if (map.containsKey(c))`. The `>= left` check is equivalent to the `Math.max` trick — if the character's last occurrence is before the current window, it doesn't affect us.

**Interviewer:** Walk through `"abcabcbb"` with the array version.

**Candidate:**

`lastIndex` initialized to `-1` for all chars. `left = 0`, `maxLen = 0`.

- `right=0, c='a'`: `lastIndex['a'] = -1 < 0` → no update. Set `lastIndex['a'] = 0`. maxLen = max(0, 0-0+1) = 1.
- `right=1, c='b'`: `lastIndex['b'] = -1 < 0` → no update. Set `lastIndex['b'] = 1`. maxLen = max(1, 1-0+1) = 2.
- `right=2, c='c'`: `lastIndex['c'] = -1 < 0` → no update. Set `lastIndex['c'] = 2`. maxLen = max(2, 2-0+1) = 3.
- `right=3, c='a'`: `lastIndex['a'] = 0 >= 0` → `left = 0 + 1 = 1`. Set `lastIndex['a'] = 3`. maxLen = max(3, 3-1+1) = 3.
- `right=4, c='b'`: `lastIndex['b'] = 1 >= 1` → `left = 1 + 1 = 2`. Set `lastIndex['b'] = 4`. maxLen = max(3, 4-2+1) = 3.
- `right=5, c='c'`: `lastIndex['c'] = 2 >= 2` → `left = 2 + 1 = 3`. Set `lastIndex['c'] = 5`. maxLen = max(3, 5-3+1) = 3.
- `right=6, c='b'`: `lastIndex['b'] = 4 >= 3` → `left = 4 + 1 = 5`. Set `lastIndex['b'] = 6`. maxLen = max(3, 6-5+1) = 3.
- `right=7, c='b'`: `lastIndex['b'] = 6 >= 5` → `left = 6 + 1 = 7`. Set `lastIndex['b'] = 7`. maxLen = max(3, 7-7+1) = 3.

Return 3. Correct.

### Phase 4: Follow-ups & Variations (33:00–45:00)

**Interviewer:** What if we wanted the actual longest substring, not just its length?

**Candidate:** We'd track the `left` index where each maximum was found:

```java
int maxStart = 0, maxLen = 0, left = 0;
// ... inside loop:
if (right - left + 1 > maxLen) {
    maxLen = right - left + 1;
    maxStart = left;
}
// return s.substring(maxStart, maxStart + maxLen);
```

**Interviewer:** What about "at most k distinct characters" — a common variation?

**Candidate:** That's LeetCode 340. The approach is similar but instead of checking for duplicates, we track the count of distinct characters. When the window has more than `k` distinct characters, we shrink from the left.

```java
public int lengthOfLongestSubstringKDistinct(String s, int k) {
    int[] count = new int[128];
    int left = 0, distinct = 0, maxLen = 0;
    for (int right = 0; right < s.length(); right++) {
        if (count[s.charAt(right)]++ == 0) distinct++;
        while (distinct > k) {
            if (--count[s.charAt(left)] == 0) distinct--;
            left++;
        }
        maxLen = max(maxLen, right - left + 1);
    }
    return maxLen;
}
```

**Interviewer:** What if we needed O(1) space? Can we solve the original problem in O(1) space?

**Candidate:** For the original problem, if we restrict the alphabet size (e.g., only lowercase letters), the `int[26]` array is O(1). But generally for any ASCII, `int[128]` is also considered O(1) since it doesn't scale with input size. For the k-distinct variant with a small alphabet, the same reasoning applies.

**Interviewer:** What if the input is very large and we can't store all characters in memory?

**Candidate:** If the string is a stream, we can't go back. But the sliding window only moves forward, so it naturally works as an online algorithm. We'd store the last index of each seen character (or a limited-size map if memory is constrained). For extremely large alphabets, we'd maintain a bounded-size cache of recent character positions using LRU eviction. The trade-off is accuracy versus memory.

**Interviewer:** Good. That's a well-structured answer.

---

## Key Takeaways

| Topic | Insight |
|-------|---------|
| Sliding Window | Maintain [left, right] with no repeating characters; expand right, adjust left on conflict |
| HashMap vs Array | int[128] is faster for ASCII; HashMap for general Unicode |
| left = max(left, last + 1) | Prevents left from moving backwards |
| Variations | k distinct characters, return substring, streaming input |
| Complexity | O(n) time, O(1) space with int[128] |
