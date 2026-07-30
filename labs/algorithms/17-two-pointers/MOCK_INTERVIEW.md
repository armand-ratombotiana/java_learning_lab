# Mock Interview: Two Pointers (3Sum)

## Meta Information

| Aspect | Detail |
|--------|--------|
| Company | Amazon |
| Level | SDE II |
| Problem | 3Sum (LeetCode 15) |
| Duration | 45 minutes |
| Paradigm | Two Pointers |

---

## Transcript

### Phase 1: Problem Understanding (0:00–5:00)

**Interviewer:** Given an integer array, find all unique triplets that sum to zero. Return them as a list of triplets, no duplicates.

**Candidate:** So `[-1, 0, 1, 2, -1, -4]` should return `[[-1, -1, 2], [-1, 0, 1]]`. The two `[-1, 0, 1]` are considered the same triplet regardless of which `-1` is used, and we shouldn't include `[-1, -1, 2]` twice.

**Interviewer:** That's correct.

**Candidate:** And the array length — what's the upper bound?

**Interviewer:** Up to 3000 elements.

**Candidate:** That means O(n^3) brute force is out. O(n^2) should be target.

### Phase 2: Approach Design (5:00–14:00)

**Candidate:** Let me think through the approaches.

**HashSet approach:** For each pair `(i, j)`, check if `-(nums[i] + nums[j])` exists in a set. O(n^2) time, O(n) space. But handling duplicates is messy.

**Sort + Two Pointers:** Sort the array, then for each element `nums[i]`, use two pointers on `nums[i+1..n-1]` to find pairs that sum to `-nums[i]`. This is O(n^2) time, O(1) extra space (excluding output), and handles duplicates cleanly because sorting groups equal elements.

**Interviewer:** Why does the two-pointer approach require sorting?

**Candidate:** The two-pointer technique depends on the array being sorted. When we fix `nums[i]`, the remaining subarray is sorted. We set `lo = i + 1`, `hi = n - 1`. If `nums[lo] + nums[hi] < target`, we need a larger sum, so we increment `lo`. If `nums[lo] + nums[hi] > target`, we decrement `hi`. This works because the sorted order gives us monotonicity — moving left increases the sum, moving right decreases it.

**Interviewer:** How do you handle duplicates?

**Candidate:** Three levels:
1. **Outer loop**: Skip `nums[i]` if it equals `nums[i-1]` (after sorting).
2. **Inner loop — after finding a match**: Skip all duplicate `nums[lo]` by incrementing `lo` while `nums[lo] == nums[lo-1]`.
3. **Inner loop — after finding a match**: Skip all duplicate `nums[hi]` by decrementing `hi` while `nums[hi] == nums[hi+1]`.

This ensures every triplet in the result is unique.

### Phase 3: Coding (14:00–33:00)

**Candidate:** I'll implement the sort + two-pointer approach.

```java
class Solution {
    public List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);
        int n = nums.length;

        for (int i = 0; i < n - 2; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) continue;

            int target = -nums[i];
            int lo = i + 1, hi = n - 1;

            while (lo < hi) {
                int sum = nums[lo] + nums[hi];
                if (sum == target) {
                    result.add(Arrays.asList(nums[i], nums[lo], nums[hi]));
                    lo++;
                    hi--;
                    while (lo < hi && nums[lo] == nums[lo - 1]) lo++;
                    while (lo < hi && nums[hi] == nums[hi + 1]) hi--;
                } else if (sum < target) {
                    lo++;
                } else {
                    hi--;
                }
            }
        }
        return result;
    }
}
```

**Interviewer:** Walk through `[-1, 0, 1, 2, -1, -4]`.

**Candidate:** After sorting: `[-4, -1, -1, 0, 1, 2]`

- `i=0, nums[0] = -4`, target = 4. lo=1, hi=5.
  - sum = -1 + 2 = 1 < 4 → lo=2
  - sum = -1 + 2 = 1 < 4 → lo=3
  - sum = 0 + 2 = 2 < 4 → lo=4
  - sum = 1 + 2 = 3 < 4 → lo=5 → lo ≥ hi, exit.

- `i=1, nums[1] = -1`, target = 1. lo=2, hi=5.
  - sum = -1 + 2 = 1 == target → add [-1, -1, 2]. lo=3, hi=4.
  - Skip duplicates: nums[3]=0 ≠ nums[2]=-1, nums[4]=1 ≠ nums[5]=2.
  - sum = 0 + 1 = 1 == target → add [-1, 0, 1]. lo=4, hi=3 → exit.

- `i=2, nums[2] = -1` → skip (same as nums[1]).

- `i=3, nums[3] = 0`, target = 0. lo=4, hi=5.
  - sum = 1 + 2 = 3 > 0 → hi=4 → lo ≥ hi, exit.

Result: `[[-1, -1, 2], [-1, 0, 1]]`. Correct.

### Phase 4: Complexity & Follow-ups (33:00–45:00)

**Interviewer:** Complexity?

**Candidate:** Time: O(n^2) — sorting takes O(n log n), the outer loop runs O(n) times, and the inner two-pointer scan takes O(n) per iteration. Overall O(n^2). Space: O(1) auxiliary (ignoring the output list and sorting overhead).

**Interviewer:** What optimizations or early exits can we add?

**Candidate:** Several:
1. If `nums[i] > 0`, break. Since the array is sorted, if the smallest element is positive, no three positive numbers can sum to zero.
2. If `nums[i] + nums[i+1] + nums[i+2] > 0`, break. The smallest possible sum from here is already positive.
3. If `nums[i] + nums[n-2] + nums[n-1] < 0`, skip to next `i`. The largest possible sum from here is still negative.

These are small optimizations that don't change the asymptotic complexity but can help in practice.

**Interviewer:** How would you modify this for 4Sum (k-sum)?

**Candidate:** The general approach for k-sum with sorting:
- For k = 2: use two pointers (O(n)).
- For k > 2: fix one element and recursively solve (k-1)-sum on the remaining subarray.
- Base case k = 2: use two pointers.
- To avoid duplicates at each level: skip repeated values.

This gives O(n^(k-1)) time. For k = 4, it's O(n^3). There are hashing-based approaches that can do better on average, but the sorting approach is the most straightforward.

**Interviewer:** What about the follow-up "Can you do it without sorting?"

**Candidate:** Without sorting, we'd use a hash-based approach. For each `i`, use a set to track seen values and find pairs summing to `-nums[i]`. But duplicate handling is much messier — we'd typically use a `Set<Triplet>` with a custom class and hashCode/equals to deduplicate at the end. The sort-based approach is cleaner and equally efficient.

**Interviewer:** You should also consider the case where we're asked for *all* triplets, not just one triplet. That's what you've implemented.

**Candidate:** Right. The two-pointer approach naturally finds all triplets in O(n^2), and the duplicate-skipping logic ensures no duplicates in the output.

**Interviewer:** Good. We're done.

---

## Key Takeaways

| Topic | Insight |
|-------|---------|
| Algorithm | Sort + two pointers; reduces 3Sum to n iterations of Two Sum II |
| Duplicate Handling | Skip at three levels: outer loop i, inner lo, inner hi |
| Early Termination | Break if nums[i] > 0 (can't sum to zero with all-positive) |
| Generalization | Recursive k-sum with base case 2-sum = two pointers |
| Complexity | O(n^2) time, O(1) auxiliary space |
