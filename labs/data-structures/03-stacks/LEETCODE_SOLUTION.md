# Min Stack (LeetCode 155)

**Problem:** Design a stack that supports push, pop, top, and retrieving the minimum element in **O(1)** time.

Implement `MinStack`:

- `MinStack()` — Initializes the stack object.
- `void push(int val)` — Pushes the element val onto the stack.
- `void pop()` — Removes the element on the top of the stack.
- `int top()` — Gets the top element of the stack.
- `int getMin()` — Retrieves the minimum element in the stack.

## Approach

We maintain two stacks:

1. **`stack`** — the main stack storing all elements.
2. **`minStack`** — an auxiliary stack that stores the minimum value at each level. On every `push`, we push `min(currentTop, val)` onto `minStack`. This guarantees that `minStack.peek()` always returns the global minimum in O(1).

An alternative approach (space-optimized) stores `min` only when a new minimum is encountered, but the dual-stack approach is simpler and still O(n) space. We use `ArrayDeque` as the stack implementation for efficiency.

## Java Solution

```java
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;

/**
 * MinStack implementation that supports O(1) push, pop, top, and getMin.
 *
 * <p>Uses an auxiliary stack that tracks the current minimum at each level.
 * Each element in the auxiliary stack stores the minimum value <i>after</i>
 * the corresponding push operation.</p>
 *
 * <h2>Complexity Analysis</h2>
 * <ul>
 *   <li><b>push(val)</b> — O(1)</li>
 *   <li><b>pop()</b> — O(1)</li>
 *   <li><b>top()</b> — O(1)</li>
 *   <li><b>getMin()</b> — O(1)</li>
 * </ul>
 *
 * <b>Space:</b> O(n) — both stacks store up to n elements.
 */
public class MinStack {

    private final Deque<Integer> stack;
    private final Deque<Integer> minStack;

    /** Constructs an empty MinStack. */
    public MinStack() {
        stack = new ArrayDeque<>();
        minStack = new ArrayDeque<>();
    }

    /**
     * Pushes the given value onto the stack.
     * Updates the current minimum if necessary.
     *
     * @param val the value to push
     */
    public void push(int val) {
        stack.push(val);
        if (minStack.isEmpty() || val <= minStack.peek()) {
            minStack.push(val);
        } else {
            minStack.push(minStack.peek());
        }
    }

    /**
     * Removes the top element of the stack.
     *
     * @throws NoSuchElementException if the stack is empty
     */
    public void pop() {
        if (stack.isEmpty()) {
            throw new NoSuchElementException("Stack is empty");
        }
        stack.pop();
        minStack.pop();
    }

    /**
     * Returns the top element without removing it.
     *
     * @return the top element
     * @throws NoSuchElementException if the stack is empty
     */
    public int top() {
        if (stack.isEmpty()) {
            throw new NoSuchElementException("Stack is empty");
        }
        return stack.peek();
    }

    /**
     * Retrieves the minimum element in the stack in O(1) time.
     *
     * @return the current minimum
     * @throws NoSuchElementException if the stack is empty
     */
    public int getMin() {
        if (minStack.isEmpty()) {
            throw new NoSuchElementException("Stack is empty");
        }
        return minStack.peek();
    }
}
```

## Test Cases

```java
/**
 * Unit tests for MinStack.
 */
public class MinStackTest {

    public static void main(String[] args) {
        // --- Test 1: Example from LeetCode ---
        MinStack stack = new MinStack();
        stack.push(-2);
        stack.push(0);
        stack.push(-3);
        assert stack.getMin() == -3 : "min should be -3";
        stack.pop();
        assert stack.top() == 0 : "top should be 0";
        assert stack.getMin() == -2 : "min should be -2";

        // --- Test 2: Single element ---
        MinStack s2 = new MinStack();
        s2.push(42);
        assert s2.top() == 42 : "top should be 42";
        assert s2.getMin() == 42 : "min should be 42";
        s2.pop();

        // --- Test 3: Monotonically increasing ---
        MinStack s3 = new MinStack();
        s3.push(1); s3.push(2); s3.push(3); s3.push(4);
        assert s3.getMin() == 1 : "min should always be 1";
        s3.pop();
        assert s3.getMin() == 1 : "min should still be 1";
        s3.pop();
        assert s3.getMin() == 1 : "min should still be 1";

        // --- Test 4: Monotonically decreasing ---
        MinStack s4 = new MinStack();
        s4.push(5); s4.push(4); s4.push(3); s4.push(2);
        assert s4.getMin() == 2 : "min should be 2";
        s4.pop();
        assert s4.getMin() == 3 : "min should be 3";
        s4.pop();
        assert s4.getMin() == 4 : "min should be 4";

        // --- Test 5: Duplicate minima ---
        MinStack s5 = new MinStack();
        s5.push(1); s5.push(1); s5.push(1);
        assert s5.getMin() == 1 : "min should be 1";
        s5.pop();
        assert s5.getMin() == 1 : "min should still be 1";
        s5.pop();
        assert s5.getMin() == 1 : "min should still be 1";

        // --- Test 6: Negative numbers ---
        MinStack s6 = new MinStack();
        s6.push(-10); s6.push(-20); s6.push(0);
        assert s6.getMin() == -20 : "min should be -20";
        s6.pop();
        assert s6.getMin() == -20 : "min should still be -20";
        s6.pop();
        assert s6.getMin() == -10 : "min should be -10";

        // --- Test 7: Pop until empty then verify exception ---
        MinStack s7 = new MinStack();
        s7.push(5);
        s7.pop();
        try {
            s7.top();
            assert false : "should have thrown exception";
        } catch (NoSuchElementException e) { /* expected */ }
        try {
            s7.getMin();
            assert false : "should have thrown exception";
        } catch (NoSuchElementException e) { /* expected */ }
        try {
            s7.pop();
            assert false : "should have thrown exception";
        } catch (NoSuchElementException e) { /* expected */ }

        // --- Test 8: Large sequence of operations ---
        MinStack s8 = new MinStack();
        int minVal = Integer.MAX_VALUE;
        for (int i = 0; i < 1000; i++) {
            int v = (int)(Math.random() * 2000 - 1000);
            s8.push(v);
            minVal = Math.min(minVal, v);
            assert s8.getMin() == minVal : "min should track correctly";
        }
        for (int i = 0; i < 1000; i++) {
            s8.pop();
        }
        assert s8.getMin() == Integer.MAX_VALUE : "expected min after empty";
    } catch (Exception ignored) {}
        // Reinitialize
        s8 = new MinStack();
        s8.push(100);
        for (int i = 0; i < 500; i++) {
            s8.push(i);
        }
        assert s8.getMin() == 0 : "min should be 0 after 500 pushes";

        // --- Test 9: pop until specific min ---
        MinStack s9 = new MinStack();
        s9.push(5); s9.push(4); s9.push(3); s9.push(4); s9.push(5);
        assert s9.getMin() == 3 : "min should be 3";
        s9.pop(); assert s9.getMin() == 3 : "min should still be 3";
        s9.pop(); assert s9.getMin() == 3 : "min should still be 3";
        s9.pop(); assert s9.getMin() == 4 : "min should now be 4";
        s9.pop(); assert s9.getMin() == 5 : "min should now be 5";

        // --- Test 10: Verify getMin returns the stored min and not stale data ---
        MinStack s10 = new MinStack();
        s10.push(10); s10.push(8); s10.push(9); s10.push(7);
        assert s10.getMin() == 7 : "min should be 7";
        s10.pop(); // pop 7
        assert s10.getMin() == 8 : "min should now be 8";
        s10.pop(); // pop 9
        assert s10.getMin() == 8 : "min should still be 8";
        s10.push(6);
        assert s10.getMin() == 6 : "min should be 6 after push";
        s10.push(100);
        assert s10.getMin() == 6 : "min should remain 6";

        System.out.println("All MinStack tests passed!");
    }
}
```
