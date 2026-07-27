# Problem Walkthrough: 03-Stacks-Queues

## Problem 1: Valid Parentheses (LC 20) — Amazon

### Interview Scenario
"Amazon interviewer: 'Given a string s containing just the characters (, ), {, }, [, ], determine if it is valid.'"

### The Problem
A string is valid if brackets close in the correct order and every opening bracket has a matching closing bracket of the same type.

### Step 1: Clarify (30 seconds)
- **Q:** Can there be characters other than brackets? **A:** No, just the six bracket characters.
- **Q:** Empty string? **A:** Yes, return true.
- **Q:** Nested brackets? **A:** Yes, "({[]})" is valid.
- **Edge cases:** Single opening bracket, single closing bracket, mismatched types like "(]", deeply nested.

### Step 2: Brute Force (2 min)
- Repeatedly replace "()", "{}", "[]" with empty string until no replacements remain. Check if result is empty.
- **Time:** O(n²) — each scan is O(n), worst-case O(n) scans.
- **Space:** O(n) for the intermediate strings.

### Step 3: Optimize (5 min)
- "Use a stack: push opening brackets onto the stack. When you see a closing bracket, pop the top — it must match. At the end, the stack must be empty."
- O(n) time, O(n) space in the worst case (all opening brackets).
- **Why Amazon cares:** String processing at scale — this pattern appears in parsers, compilers, and config validation across AWS services.

### Step 4: Code (10 min)

```java
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

/**
 * Validates bracket ordering in a string.
 * <p>
 * Time: O(n) | Space: O(n)
 */
public class Solution {
    public boolean isValid(String s) {
        Map<Character, Character> map = Map.of(')', '(', '}', '{', ']', '[');
        Deque<Character> stack = new ArrayDeque<>();

        for (char c : s.toCharArray()) {
            if (map.containsKey(c)) {
                if (stack.isEmpty() || stack.pop() != map.get(c)) {
                    return false;
                }
            } else {
                stack.push(c);
            }
        }

        return stack.isEmpty();
    }
}
```

### Step 5: Test (3 min)
- **Example 1:** s = "()" → true
- **Example 2:** s = "()[]{}" → true
- **Example 3:** s = "(]" → false
- **Example 4:** s = "([)]" → false (wrong nesting)
- **Edge:** s = "" → true
- **Edge:** s = "(((((" → false

### Step 6: Follow-ups
- "What about minimum additions to make it valid?" — Track unmatched count; return count + stack size (LC 921).
- "What about multiple bracket types and a wildcard '*'" — LC 678 Valid Parenthesis String (harder).
- "What if we need to check in a concurrent setting?" — Discuss thread-local stacks vs. synchronization.
- **What Amazon looks for:** Clean, correct code. They test with random large strings in automated tests.

### Company Evaluation Criteria
- **Amazon:** Correctness and edge-case awareness. They test with automated systems.
- **Google:** Would ask about generating all valid strings.
- **Meta:** Would ask about the minimum number of swaps to balance.

---

## Problem 2: Min Stack (LC 155) — Google

### Interview Scenario
"Google interviewer: 'Design a stack that supports push, pop, top, and retrieving the minimum element in constant time.'"

### The Problem
Implement a stack with `getMin()` returning the minimum element in O(1) time.

### Step 1: Clarify (30 seconds)
- **Q:** Can pop be called on an empty stack? **A:** No, assume valid operations.
- **Q:** Can there be duplicate values? **A:** Yes.
- **Q:** Space constraints? **A:** Minimize extra space.
- **Edge cases:** Single element, decreasing sequence, increasing sequence, duplicates.

### Step 2: Brute Force (2 min)
- Store all elements in a list. For getMin(), scan the entire stack O(n).
- **Time:** O(n) for getMin — unacceptable.
- **Space:** O(n).

### Step 3: Optimize (5 min)
- "Maintain a second stack that tracks the current minimum for each state of the main stack. When pushing x, push min(x, currentMin) onto the min stack."
- Alternatively: store the current minimum in each node (extra field).
- O(1) for all operations. O(n) extra space in the worst case.
- **Why Google values this:** It tests object-oriented design alongside algorithm knowledge. They want to see clean abstractions.

### Step 4: Code (10 min)

```java
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * A stack that supports getMin() in O(1) time.
 * <p>
 * Time: O(1) per operation | Space: O(n)
 */
public class MinStack {
    private Deque<Integer> stack;
    private Deque<Integer> minStack;

    public MinStack() {
        stack = new ArrayDeque<>();
        minStack = new ArrayDeque<>();
    }

    public void push(int val) {
        stack.push(val);
        if (minStack.isEmpty() || val <= minStack.peek()) {
            minStack.push(val);
        }
    }

    public void pop() {
        if (stack.pop().equals(minStack.peek())) {
            minStack.pop();
        }
    }

    public int top() {
        return stack.peek();
    }

    public int getMin() {
        return minStack.peek();
    }
}
```

### Step 5: Test (3 min)
- push(-2), push(0), push(-3), getMin() → -3, pop(), top() → 0, getMin() → -2
- push all increasing: getMin() always first element
- push all decreasing: min stack has every element
- **Edge:** push same value twice, pop once → min stays same

### Step 6: Follow-ups
- "Can you do it with O(1) extra space?" — Store (value, currentMin) pairs in one stack, or encode difference from min.
- "What about thread safety?" — Add synchronized keyword or use Lock.
- **What Google looks for:** Clean separation of concerns. They'll push on the O(1) space variant.

### Company Evaluation Criteria
- **Google:** Design quality, abstraction, constant-time claims backed by proof.
- **Amazon:** Would ask about max stack variant.
- **Meta:** Would ask about pop order after push in min-order.

---

## Problem 3: Daily Temperatures (LC 739) — Meta

### Interview Scenario
"Meta interviewer: 'Given an array of daily temperatures, return an array answer where answer[i] is the number of days you have to wait until a warmer temperature.'"

### The Problem
For each day, find the next day with a greater temperature. If none, answer[i] = 0.

### Step 1: Clarify (30 seconds)
- **Q:** Temperature range? **A:** 30 to 100, inclusive.
- **Q:** Array size? **A:** Up to 10^5.
- **Q:** What if no warmer day? **A:** Return 0.
- **Edge cases:** Decreasing temperatures (all 0), increasing (all 1 except last), single element, all same temperature.

### Step 2: Brute Force (2 min)
- For each index i, scan j from i+1..n to find first greater temperature.
- **Time:** O(n²) — too slow for 10^5.
- **Space:** O(1).

### Step 3: Optimize (5 min)
- "Use a monotonic decreasing stack. Iterate from right to left (or left to right). Maintain indices of temperatures in decreasing order. For each temperature, pop until we find a warmer one. The distance is the index difference."
- O(n) time, O(n) space.
- **Why Meta likes this:** It's a real-world problem — Meta's News Feed and Ads systems use similar patterns for time-series data.

### Step 4: Code (10 min)

```java
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Computes next warmer temperature distance for each day.
 * <p>
 * Time: O(n) | Space: O(n)
 */
public class Solution {
    public int[] dailyTemperatures(int[] temperatures) {
        int n = temperatures.length;
        int[] answer = new int[n];
        Deque<Integer> stack = new ArrayDeque<>();

        for (int i = 0; i < n; i++) {
            while (!stack.isEmpty() && temperatures[i] > temperatures[stack.peek()]) {
                int prev = stack.pop();
                answer[prev] = i - prev;
            }
            stack.push(i);
        }

        return answer;
    }
}
```

### Step 5: Test (3 min)
- **Example:** temps = [73, 74, 75, 71, 69, 72, 76, 73] → [1, 1, 4, 2, 1, 1, 0, 0]
- **Edge:** temps = [30, 40, 50, 60] → [1, 1, 1, 0]
- **Edge:** temps = [90, 80, 70, 60] → [0, 0, 0, 0]
- Show stack state at each step on the whiteboard.

### Step 6: Follow-ups
- "What if the temperature is given as a stream?" — Same approach, process as data arrives.
- "What about next smaller element?" — Same pattern, reverse comparison.
- "Can you do it in one pass from left to right?" — Yes, the solution above already does that.
- **What Meta looks for:** Real-world applicability. Can you solve problems that look like internal Meta infrastructure?

### Company Evaluation Criteria
- **Meta:** Pattern recognition — monotonic stack is a Meta favorite. Clean, readable code.
- **Amazon:** Would ask about next greater element in a circular array.
- **Google:** Would explore a divide-and-conquer approach.

---

## Study Notes

### Key Patterns
- **Stack for matching:** Parentheses, HTML tags, JSON validation
- **Two-stack design:** Min/Max stack, stack with tracking
- **Monotonic stack:** Next greater/smaller element, daily temperatures, stock span, histogram area
- **Queue with stack:** Implement queue using two stacks (amortized O(1))

### Common Mistakes
- Using `==` instead of `.equals()` for Integer comparison in MinStack pop
- Forgetting to check stack emptiness before peek/pop
- Off-by-one in the monotonic stack distance calculation
- Not handling the case of no match (should be 0, not -1)

### Time Complexity Cheat Sheet
| Pattern | Time | Space |
|---|---|---|
| Stack matching | O(n) | O(n) |
| MinStack | O(1) per op | O(n) |
| Monotonic stack | O(n) | O(n) |
| Queue via two stacks | O(1) amortized | O(n) |
| Circular queue | O(1) | O(k) |
