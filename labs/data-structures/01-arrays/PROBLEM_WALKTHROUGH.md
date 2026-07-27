# Problem Walkthrough: 01-Arrays

## Problem 1: Two Sum (LC 1) — Google

### Interview Scenario
"You're interviewing at Google. The interviewer gives you this problem: 'Given an array of integers nums and an integer target, return indices of the two numbers that add up to target.'"

### The Problem
Given an array of integers `nums` and an integer `target`, return indices of the two numbers such that they add up to `target`. You may assume exactly one solution, and you may not use the same element twice.

### Step 1: Clarify (30 seconds)
- **Q:** Are the numbers sorted? **A:** Not necessarily.
- **Q:** Can there be negative numbers? **A:** Yes.
- **Q:** Are there duplicates? **A:** Possibly, but only one valid pair exists.
- **Q:** What should I return if no solution? **A:** Assume exactly one solution exists.
- **Edge cases:** Only two elements; target is zero with [0, 0]; large values near Integer.MAX_VALUE causing overflow.

### Step 2: Brute Force (2 min)
- Nested loop checking every pair: for i from 0..n, for j from i+1..n, check if nums[i] + nums[j] == target.
- **Time:** O(n²) — too slow for large inputs.
- **Space:** O(1).

### Step 3: Optimize (5 min)
- "We can trade space for time using a HashMap. As we iterate, store each value and its index. For each element, compute complement = target - nums[i]. If complement exists in the map, return [map.get(complement), i]. Otherwise, put nums[i] into the map and continue."
- Single pass; O(n) time, O(n) space.
- **Why this works at Google:** Google evaluates thought process — show you understand the trade-off and can reason through it aloud.

### Step 4: Code (10 min)

```java
import java.util.HashMap;
import java.util.Map;

/**
 * Given an array of integers nums and an integer target, returns the indices
 * of the two numbers that add up to target.
 * <p>
 * Time: O(n) | Space: O(n)
 */
public class Solution {
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (map.containsKey(complement)) {
                return new int[]{map.get(complement), i};
            }
            map.put(nums[i], i);
        }
        throw new IllegalArgumentException("No two sum solution");
    }
}
```

### Step 5: Test (3 min)
- **Example 1:** nums = [2, 7, 11, 15], target = 9 → [0, 1]
- **Example 2:** nums = [3, 2, 4], target = 6 → [1, 2]
- **Example 3:** nums = [3, 3], target = 6 → [0, 1]
- **Edge:** nums = [0, 4, 0], target = 0 → [0, 2]
- **How Google evaluates:** They want to see you test edge cases and explain why the approach works. Walk through the hashmap state at each step.

### Step 6: Follow-ups
- "What if the array is sorted?" — Use two-pointer technique (O(n) time, O(1) space).
- "What if we need all pairs, not just one?" — Sort + two pointers or use hashmap of counts.
- "What about three-sum? Four-sum?" — Reduce to two-sum after fixing elements.
- **What Google looks for:** Can you generalize the pattern? Do you understand the constraints deeply?

### Company Evaluation Criteria
- **Google:** Thought process and clarity of reasoning above all. Talk through every assumption. They value the journey, not just the answer.
- **Amazon:** Optimization focus — why is O(n²) unacceptable? When would O(n) space be a problem?
- **Meta:** Clean, readable code. Minimal lines, proper variable names, no extraneous comments.

---

## Problem 2: Product of Array Except Self (LC 238) — Amazon

### Interview Scenario
"You're at Amazon. The interviewer says: 'Given an integer array nums, return an array answer such that answer[i] is equal to the product of all the elements of nums except nums[i].'"

### The Problem
Return an output array where each element is the product of all input elements except the one at that index. Must solve in O(n) without division.

### Step 1: Clarify (30 seconds)
- **Q:** Can I use division? **A:** No, that's the constraint.
- **Q:** What if there are zeros? **A:** Handle zeros correctly — if one zero, that index gets the product of all others; if two zeros, everything is zero.
- **Q:** Can values overflow? **A:** Assume int range, but discuss overflow handling in follow-ups.
- **Edge cases:** Length 2; single zero; all zeros; negative numbers.

### Step 2: Brute Force (2 min)
- For each index, compute product of all other elements in a nested loop.
- **Time:** O(n²) — "This would fail Amazon's scale."
- **Space:** O(1) excluding output.

### Step 3: Optimize (5 min)
- "We can compute prefix products and suffix products. First pass: answer[i] = product of all elements to the left of i. Second pass: multiply by product of all elements to the right."
- Two-pass O(n) without division, O(1) extra space (output array doesn't count).
- **Why Amazon cares:** They want solutions that scale. O(n²) on millions of transactions would be catastrophic.

### Step 4: Code (10 min)

```java
/**
 * Returns an array where answer[i] is the product of all nums elements except nums[i].
 * <p>
 * Time: O(n) | Space: O(1) (excluding output)
 */
public class Solution {
    public int[] productExceptSelf(int[] nums) {
        int n = nums.length;
        int[] answer = new int[n];

        answer[0] = 1;
        for (int i = 1; i < n; i++) {
            answer[i] = answer[i - 1] * nums[i - 1];
        }

        int suffix = 1;
        for (int i = n - 1; i >= 0; i--) {
            answer[i] *= suffix;
            suffix *= nums[i];
        }

        return answer;
    }
}
```

### Step 5: Test (3 min)
- **Example 1:** nums = [1, 2, 3, 4] → [24, 12, 8, 6]
- **Example 2:** nums = [-1, 1, 0, -3, 3] → [0, 0, 9, 0, 0]
- **Edge:** nums = [0, 0] → [0, 0]
- **Edge:** nums = [5] — clarified that n >= 2 typically
- Walk through the prefix/suffix state at each step on the whiteboard.

### Step 6: Follow-ups
- "What if the array is very large (doesn't fit in memory)?" — Process in chunks, streaming approach.
- "Can you do it in one pass?" — Yes, but the two-pass is standard and more readable.
- "What about using logarithms?" — Discuss precision issues, floating-point errors at Amazon's scale.
- **What Amazon looks for:** Can you build fault-tolerant, scalable code? Do you consider edge cases around zero?

### Company Evaluation Criteria
- **Amazon:** Optimization under constraints. BAR-raising — they push until you can't go further. Leadership principles like "Deliver Results."
- **Google:** Would ask for a divide-and-conquer follow-up.
- **Meta:** Code simplicity and naming conventions.

---

## Problem 3: Maximum Subarray (LC 53) — Meta

### Interview Scenario
"Meta interviewer: 'Given an integer array nums, find the subarray with the largest sum and return its sum.'"

### The Problem
Find the maximum sum of any contiguous subarray.

### Step 1: Clarify (30 seconds)
- **Q:** Can the subarray be empty? **A:** No, at least one element.
- **Q:** All negative numbers? **A:** Yes, return the largest (closest to zero) element.
- **Q:** What about overflow? **A:** Values fit in int range.
- **Edge cases:** Single element; all negative; mix of positive and negative; all zeros.

### Step 2: Brute Force (2 min)
- For each starting index, try all ending indices, computing running sum.
- **Time:** O(n²) — "Meta expects better."
- **Space:** O(1).

### Step 3: Optimize (5 min)
- "Kadane's algorithm: keep track of current max ending here and global max. At each step, decide whether to extend the current subarray or start a new one."
- This is the textbook optimal solution. O(n) time, O(1) space.
- **Why Meta likes this:** Clean, elegant, one pass. It shows you know classic algorithms cold.

### Step 4: Code (10 min)

```java
/**
 * Finds the maximum sum of any contiguous subarray.
 * <p>
 * Time: O(n) | Space: O(1)
 */
public class Solution {
    public int maxSubArray(int[] nums) {
        int maxSoFar = nums[0];
        int maxEndingHere = nums[0];

        for (int i = 1; i < nums.length; i++) {
            maxEndingHere = Math.max(nums[i], maxEndingHere + nums[i]);
            maxSoFar = Math.max(maxSoFar, maxEndingHere);
        }

        return maxSoFar;
    }
}
```

### Step 5: Test (3 min)
- **Example 1:** nums = [-2, 1, -3, 4, -1, 2, 1, -5, 4] → 6
- **Example 2:** nums = [1] → 1
- **Example 3:** nums = [5, 4, -1, 7, 8] → 23
- **Edge:** nums = [-1, -2, -3] → -1
- Walk through the algorithm state at each step.

### Step 6: Follow-ups
- "Return the actual subarray, not just the sum." — Track start/end indices.
- "What if the array is circular?" — Apply Kadane twice (non-wrapping and wrapping).
- "Can you do it with divide and conquer?" — Yes, merge step computes cross sum: O(n log n).
- **What Meta looks for:** Can you extend a known algorithm to variations? Do you write code that ships?

### Company Evaluation Criteria
- **Meta:** Clean, readable, production-ready code. Variable names matter. No unnecessary abstraction.
- **Amazon:** Would ask about scaling to multi-dimensional arrays.
- **Google:** Would explore the divide-and-conquer version and ask about recurrence relation.

---

## Study Notes

### Key Patterns
- **Complement pattern:** Two Sum → HashMap lookup of target - current
- **Prefix/Suffix pattern:** Product of Array Except Self → two passes building cumulative products
- **Running optimum pattern:** Maximum Subarray → Kadane's, maintain local and global optima
- **Sliding window:** Common in array problems with contiguous constraints

### Common Mistakes
- Forgetting duplicate handling in Two Sum
- Using division when it's explicitly forbidden
- Off-by-one errors in prefix/suffix index calculations
- Not initializing Kadane correctly with the first element
- Integer overflow — use `long` if needed and clamp

### Time Complexity Cheat Sheet
| Pattern | Time | Space |
|---|---|---|
| Nested loop | O(n²) | O(1) |
| HashMap pass | O(n) | O(n) |
| Two-pointer (sorted) | O(n) | O(1) |
| Prefix/Suffix | O(n) | O(1) |
| Kadane | O(n) | O(1) |
| Sliding window | O(n) | O(1) |
