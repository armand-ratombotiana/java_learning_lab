# LEETCODE_SOLUTION — 232. Implement Queue using Stacks

## Problem
Implement a FIFO queue using two stacks.

## Proxy Approach
Define a `Queue` interface and use a proxy to enforce LIFO‑to‑FIFO semantics.

```java
interface Queue {
    void push(int x);
    int pop();
    int peek();
    boolean empty();
}

public class MyQueue implements Queue {
    private Stack<Integer> in = new Stack<>();
    private Stack<Integer> out = new Stack<>();

    public void push(int x) { in.push(x); }
    public int pop() { peek(); return out.pop(); }
    public int peek() {
        if (out.empty()) while (!in.empty()) out.push(in.pop());
        return out.peek();
    }
    public boolean empty() { return in.empty() && out.empty(); }
}
```

## Key Insight
A proxy could wrap a non‑thread‑safe queue with synchronized access.

## Complexity
- Time: O(1) amortized per operation
- Space: O(n)
