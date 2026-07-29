# LeetCode 137 — Single Number II — Problem Walkthrough

## Problem Statement

Given an integer array `nums` where **every element appears three times** except for one element which appears **exactly once**, find the single element and return it.

You must implement a solution with **linear runtime complexity** and use **constant extra space**.

**Constraints:**
- `1 <= nums.length <= 3 * 10^4`
- `-2^31 <= nums[i] <= 2^31 - 1`

**Examples:**
```
Input:  nums = [2, 2, 3, 2]
Output: 3

Input:  nums = [0, 1, 0, 1, 0, 1, 99]
Output: 99
```

---

## Step-by-Step Solution

### Step 1: Bit Manipulation Approach

For each bit position, count how many numbers have that bit set. Since every number except one appears 3 times, the count for each bit will be either `3k` or `3k + 1` (where k is the number of tripled numbers that have that bit set). The bits where `count % 3 == 1` belong to the single number.

### Step 2: Efficient Method Using State Machine

Instead of counting 32 bits individually, we use a **finite state machine** with two variables:

- `ones`: bits that have appeared once (mod 3).
- `twos`: bits that have appeared twice (mod 3).

For each number `num`:
```
ones = (ones ^ num) & ~twos
twos = (twos ^ num) & ~ones
```

**Explanation:** This implements a modulo-3 counter for each bit:
- When a bit appears first time → goes to `ones`.
- Second time → goes to `twos`, cleared from `ones`.
- Third time → cleared from `twos`, back to 0.
- The `~twos` / `~ones` masks ensure correct transitions.

### Step 3: Why It Works

Each bit independently cycles through states 0 → 1 → 2 → 0 (5 → 1 → 2 → 0 with the actual bits). Since the duplicate numbers appear 3 times, they cycle back to 0. The single number's bits settle in state 1 (ones).

---

## Full Compilable Solution

```java
import java.util.Arrays;

/**
 * LeetCode 137 — Single Number II
 *
 * Bit manipulation with finite state machine (mod 3 counter).
 *
 * Time:  O(n)
 * Space: O(1)
 */
public class SingleNumberII {

    public int singleNumber(int[] nums) {
        int ones = 0, twos = 0;
        for (int num : nums) {
            ones = (ones ^ num) & ~twos;
            twos = (twos ^ num) & ~ones;
        }
        return ones;
    }

    public static void main(String[] args) {
        SingleNumberII s = new SingleNumberII();

        runTest(s, new int[]{2, 2, 3, 2}, 3);
        runTest(s, new int[]{0, 1, 0, 1, 0, 1, 99}, 99);
        runTest(s, new int[]{5}, 5);                    // Single element
        runTest(s, new int[]{-1, -1, -1, 2}, 2);        // Negative numbers
        runTest(s, new int[]{1, 1, 1, 2, 2, 2, 3}, 3); // Standard
        runTest(s, new int[]{30000, 30000, 30000, -30000}, -30000); // Large values
        runTest(s, new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE},
            Integer.MIN_VALUE); // Boundary values
        runTest(s, new int[]{0, 0, 0, 1}, 1);           // Zeros + single
    }

    private static void runTest(SingleNumberII s, int[] nums, int expected) {
        int result = s.singleNumber(nums);
        String status = result == expected ? "PASS" : "FAIL";
        System.out.printf("%s | singleNumber(%s) = %d (expected %d)%n",
            status, Arrays.toString(nums), result, expected);
    }
}
```

---

## 32-Bit Counting Approach (More Intuitive)

```java
/**
 * Count each bit position individually.
 * Slower but easier to understand.
 *
 * Time:  O(32 * n) = O(n)
 * Space: O(1)
 */
public class SingleNumberIIBitCount {

    public int singleNumber(int[] nums) {
        int result = 0;

        for (int bit = 0; bit < 32; bit++) {
            int count = 0;
            int mask = 1 << bit;
            for (int num : nums) {
                if ((num & mask) != 0) count++;
            }
            if (count % 3 == 1) {
                result |= mask;
            }
        }
        return result;
    }

    public static void main(String[] args) {
        SingleNumberIIBitCount s = new SingleNumberIIBitCount();
        System.out.println(s.singleNumber(new int[]{2,2,3,2}) + " (expected: 3)");
        System.out.println(s.singleNumber(new int[]{0,1,0,1,0,1,99}) + " (expected: 99)");
    }
}
```

---

## Complexity Analysis

| Version | Time | Space | Notes |
|---------|------|-------|-------|
| FSM (ones/twos) | O(n) | O(1) | Optimal — 2 integer variables |
| Bit counting | O(32 * n) = O(n) | O(1) | Simpler but does 32 passes |

### Why FSM Is Better

- Single pass through the array (n iterations) vs. 32 passes.
- Bit operations are extremely fast.
- The FSM approach generalizes to "every element appears k times except one" (see below).

---

## Generalization: Single Number for Any k

For "every element appears `k` times except one" (k ≥ 2):

```java
/**
 * General solution: Single Number where each element appears k times except one.
 *
 * Use k-bit state machines (here shown for k=3).
 * Time:  O(n)
 * Space: O(1)
 */
public class SingleNumberGeneral {

    public int singleNumber(int[] nums, int k) {
        // Use k counters (bits) for mod-k state machine
        int[] state = new int[k];
        state[0] = ~0; // all bits in state 0 initially

        for (int num : nums) {
            int mask = ~0;
            int[] next = new int[k];
            for (int i = k - 1; i >= 0; i--) {
                next[(i + 1) % k] = (state[i] & ~num) | (state[(i + k - 1) % k] & num);
            }
            state = next;
        }
        // The single number's bits are in state 1 (for k=3, or state 1 in general)
        return state[1];
    }

    public static void main(String[] args) {
        SingleNumberGeneral s = new SingleNumberGeneral();
        System.out.println(s.singleNumber(new int[]{2,2,3,2}, 3) + " (expected: 3)");
        System.out.println(s.singleNumber(new int[]{1,1,2,2,3}, 2) + " (expected: 3)"); // k=2
    }
}
```

---

## Follow-Up: Single Number (LeetCode 136 — k=2)

Every element appears twice except one. Use simple XOR:

```java
public class SingleNumberI {

    public int singleNumber(int[] nums) {
        int result = 0;
        for (int num : nums) result ^= num;
        return result;
    }

    public static void main(String[] args) {
        SingleNumberI s = new SingleNumberI();
        System.out.println(s.singleNumber(new int[]{2,2,1}) + " (expected: 1)");
        System.out.println(s.singleNumber(new int[]{4,1,2,1,2}) + " (expected: 4)");
    }
}
```

## Follow-Up: Single Number III (LeetCode 260)

Two elements appear exactly once; all others appear exactly twice.

**Approach:**
1. XOR all numbers → `xor = a ^ b` (the XOR of the two singles).
2. Find any set bit in `xor` (a differentiating bit).
3. Partition nums by that bit → XOR each group → get `a` and `b`.

```java
public class SingleNumberIII {

    public int[] singleNumber(int[] nums) {
        int xor = 0;
        for (int num : nums) xor ^= num;

        int diffBit = xor & (-xor); // rightmost set bit
        int a = 0, b = 0;
        for (int num : nums) {
            if ((num & diffBit) == 0) {
                a ^= num;
            } else {
                b ^= num;
            }
        }
        return new int[]{a, b};
    }

    public static void main(String[] args) {
        SingleNumberIII s = new SingleNumberIII();
        int[] r1 = s.singleNumber(new int[]{1,2,1,3,2,5});
        System.out.println(Arrays.toString(r1) + " (expected: [3, 5] or [5, 3])");
    }
}
```

---

## Edge Cases & Test Coverage

| Case | Input | Expected | Notes |
|------|-------|----------|-------|
| Single element | `[5]` | 5 | n=1 |
| Standard | `[2,2,3,2]` | 3 | Classic case |
| Negatives | `[-1,-1,-1,2]` | 2 | Negative numbers work |
| Large boundary | `[INT_MAX, INT_MAX, INT_MAX, INT_MIN]` | INT_MIN | All bits tested |
| Zeros | `[0,0,0,1]` | 1 | Zero handling |
| Max constraint | `[x,x,x,y]` repeated | y | Any values work |

---

## Key Takeaways

1. **Mod-k counter with bit operations** is the optimal approach for "appears k times except one" problems.
2. The **FSM approach** (`ones`/`twos`) generalizes to any k by adding more state variables.
3. **XOR alone** solves the case k=2 (Single Number I).
4. **Partitioning by a differentiating bit** solves Single Number III (two singles).
5. Bit manipulation solutions are O(n) time and O(1) space — the hardest constraints to satisfy.
6. The 32-bit counting approach is more intuitive and still linear time with O(1) space.