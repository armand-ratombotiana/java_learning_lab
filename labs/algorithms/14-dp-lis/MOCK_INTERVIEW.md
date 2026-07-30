# Mock Interview: LIS Variant (Russian Doll Envelopes)

## Meta Information

| Aspect | Detail |
|--------|--------|
| Company | Amazon |
| Level | L6 / Senior SDE |
| Problem | Russian Doll Envelopes (LeetCode 354) |
| Duration | 45 minutes |
| Paradigm | Longest Increasing Subsequence |

---

## Transcript

### Phase 1: Problem Understanding (0:00–5:00)

**Interviewer:** You're given a list of envelopes, each with a width and height. An envelope A can fit inside envelope B only if both A's width and height are strictly less than B's. What's the maximum number of envelopes you can nest?

**Candidate:** Let me confirm the rules. So if I have `[[5,4],[6,4],[6,7],[2,3]]`, the answer should be 3: `[2,3]` fits into `[5,4]` fits into `[6,7]`. Is `[5,4]` being wider and taller than `[2,3]` the only consideration? No rotation allowed?

**Interviewer:** Correct — no rotation, and both dimensions must be strictly greater.

**Candidate:** And the envelope count can be large?

**Interviewer:** Up to 10^5.

**Candidate:** That rules out O(n^2) solutions. We need something faster.

### Phase 2: Approach Design (5:00–15:00)

**Candidate:** The naive approach would be to consider all pairs — O(n^2), too slow.

This problem looks like a 2D version of Longest Increasing Subsequence. The LIS problem in 1D is O(n log n) using patience sorting. Can we reduce this to 1D LIS?

**Strategy:**
1. Sort envelopes by width ascending.
2. For envelopes with equal width, sort by height descending.
3. Then find the LIS on the height array using O(n log n) patience sorting.

**Interviewer:** Why do we sort height descending for ties?

**Candidate:** Excellent question. If two envelopes have the same width, they can't nest because the width condition requires strict inequality. If we sorted height ascending within the same width, the LIS algorithm might try to nest two envelopes with the same width but increasing heights — which isn't allowed.

By sorting height descending, we ensure that within the same width group, the heights are decreasing. The LIS algorithm, which looks for strictly increasing values, will never pick two envelopes from the same width group because their heights are in descending order.

**Interviewer:** Let's trace through an example.

**Candidate:** Take `[[5,4],[6,4],[6,7],[2,3]]`:

After sorting: `[[2,3],[5,4],[6,7],[6,4]]`

Heights: `[3, 4, 7, 4]`

Now run patience sorting (O(n log n) LIS) on these heights:
- `3`: tails = `[3]`
- `4`: tails = `[3, 4]`
- `7`: tails = `[3, 4, 7]`
- `4`: binary search finds position 2 (between 4 and 7), tails = `[3, 4, 4]`

Length = 3. Correct answer.

If we had sorted height ascending: `[[2,3],[5,4],[6,4],[6,7]]`, heights = `[3, 4, 4, 7]`. LIS would be `[3, 4, 7]` = length 3, but this incorrectly includes `[5,4]` (h=4) and `[6,7]` (h=7, w=6) — which is actually valid since `5 < 6` and `4 < 7`. So both approaches give 3 here. But consider `[[2,3],[2,4]]`: after width-then-height-ascending sort, heights = `[3,4]`, LIS = 2, but you can't nest same-width envelopes! The descending-height sort gives heights = `[4,3]`, LIS = 1 — correct.

### Phase 3: Coding (15:00–35:00)

**Candidate:** I'll implement the complete solution.

```java
class Solution {
    public int maxEnvelopes(int[][] envelopes) {
        Arrays.sort(envelopes, (a, b) -> 
            a[0] == b[0] ? b[1] - a[1] : a[0] - b[0]);

        int n = envelopes.length;
        int[] tails = new int[n];
        int len = 0;

        for (int[] env : envelopes) {
            int h = env[1];
            int idx = Arrays.binarySearch(tails, 0, len, h);
            if (idx < 0) idx = -(idx + 1);
            tails[idx] = h;
            if (idx == len) len++;
        }

        return len;
    }
}
```

**Interviewer:** What's `Arrays.binarySearch` doing when it returns a negative value?

**Candidate:** `Arrays.binarySearch` returns `-(insertion point) - 1` when the key isn't found. The insertion point is the index where the key would be inserted to maintain sorted order. So `-(idx + 1)` recovers the original insertion point.

For example, if `tails = [2, 5, 8]` and we search for `6`:
- `binarySearch` returns `-3` (insertion point = 2, so `-(2) - 1 = -3`).
- `-( -3 + 1 ) = 2`, which is the correct position to insert `6`.

**Interviewer:** Why does the LIS via patience sorting work?

**Candidate:** The patience sorting algorithm maintains an array `tails` where `tails[k]` is the smallest possible tail value of an increasing subsequence of length `k+1`. By construction:
- `tails` is always sorted.
- For each element, we find its position in `tails` (first element ≥ current value).
- Replacing that position with the current value keeps tails as small as possible, maximizing future opportunities to extend subsequences.
- If the element is larger than all tails, we append it, increasing the length.

This gives the length of the LIS, though not the actual subsequence itself (for that we'd need additional tracking).

### Phase 4: Complexity Analysis & Follow-ups (35:00–45:00)

**Interviewer:** Time and space complexity?

**Candidate:** Sorting: O(n log n). LIS loop: O(n log n) because each of the n elements does a binary search on tails, which is O(log n). Total: O(n log n). Space: O(n) for the tails array.

**Interviewer:** What if we also needed the actual sequence of envelopes, not just the count?

**Candidate:** We'd store the index along with each envelope and maintain a `parent` array. When we update `tails[pos]`, we also set `parent[currentIdx] = tails[pos - 1].index` (or -1 if pos == 0). At the end, trace back from the last element of the longest sequence.

**Interviewer:** Could we solve this with segment trees or BITs?

**Candidate:** Yes — we could coordinate-compress heights and use a BIT (Fenwick tree) or segment tree. Sort by width, then for each envelope query the BIT for max LIS length ending at height `h-1`, update position `h` with that length + 1. This is also O(n log n) but avoids the somewhat tricky descending-height sort.

The patience sorting approach is cleaner for this specific problem, but the BIT approach generalizes better to variations like "you can rotate envelopes" or "non-strict dimensions."

**Interviewer:** Good answer. That covers our time.

---

## Key Takeaways

| Topic | Insight |
|-------|---------|
| 2D → 1D Reduction | Sort by one dimension, find LIS on the other |
| Tie-Breaking | Descending sort on tie dimension prevents false nesting |
| Patience Sorting | O(n log n) LIS by maintaining `tails[k]` as minimum tail of length-k+1 subsequence |
| Alternative | BIT/segment tree for more generalizable solution |
