package com.leetcode.stack_queue;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * LeetCode 155: Min Stack
 * https://leetcode.com/problems/min-stack/
 *
 * Design a stack that supports push, pop, top, and retrieving the minimum element in O(1) time.
 *
 * Time Complexity: O(1) for all operations
 * Space Complexity: O(n)
 */
public class MinStack {

    private final Deque<Integer> stack;
    private final Deque<Integer> minStack;

    public MinStack() {
        stack = new ArrayDeque<>();
        minStack = new ArrayDeque<>();
    }

    /**
     * Approach 1: Two stacks
     * Main stack stores all elements, minStack stores the minimum at each level.
     */
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

    /**
     * Approach 2: Single stack with min tracking
     * Store the minimum along with each value in a pair.
     */

    public static void main(String[] args) {
        MinStack minStack = new MinStack();
        minStack.push(-2);
        minStack.push(0);
        minStack.push(-3);
        System.out.println("getMin: " + minStack.getMin() + " (expected: -3)");
        minStack.pop();
        System.out.println("top: " + minStack.top() + " (expected: 0)");
        System.out.println("getMin: " + minStack.getMin() + " (expected: -2)");

        // Edge: single element
        MinStack ms2 = new MinStack();
        ms2.push(42);
        System.out.println("getMin (single): " + ms2.getMin() + " (expected: 42)");
    }
}
