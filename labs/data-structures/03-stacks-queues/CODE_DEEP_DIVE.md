# Code Deep Dive: Stacks & Queues

## MinStack — O(1) Get Minimum

```java
public class MinStack {
    private final Deque<Integer> stack = new ArrayDeque<>();
    private final Deque<Integer> minStack = new ArrayDeque<>();

    public void push(int val) {
        stack.push(val);
        // Push to minStack if it's the new minimum (or equal)
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

Space optimization: store only diffs for truly O(1) space.

## Monotonic Queue — Sliding Window Maximum

```java
public int[] maxSlidingWindow(int[] nums, int k) {
    int n = nums.length;
    int[] result = new int[n - k + 1];
    Deque<Integer> deque = new ArrayDeque<>();  // stores indices

    for (int i = 0; i < n; i++) {
        // Remove indices outside window
        while (!deque.isEmpty() && deque.peekFirst() < i - k + 1) {
            deque.pollFirst();
        }
        // Remove smaller elements from back (maintain decreasing order)
        while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) {
            deque.pollLast();
        }
        deque.addLast(i);
        // First window starts at i = k - 1
        if (i >= k - 1) {
            result[i - k + 1] = nums[deque.peekFirst()];
        }
    }
    return result;
}
```

O(n) time — each element is added and removed at most once.

## Balanced Parentheses

```java
public boolean isValid(String s) {
    Deque<Character> stack = new ArrayDeque<>();
    Map<Character, Character> map = Map.of(')', '(', '}', '{', ']', '[');

    for (char c : s.toCharArray()) {
        if (map.containsKey(c)) {
            // closing bracket
            char top = stack.isEmpty() ? '#' : stack.pop();
            if (top != map.get(c)) return false;
        } else {
            // opening bracket
            stack.push(c);
        }
    }
    return stack.isEmpty();
}
```
