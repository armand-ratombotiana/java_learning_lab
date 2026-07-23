package com.leetcode.stack_queue;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

/**
 * LeetCode 20: Valid Parentheses
 * https://leetcode.com/problems/valid-parentheses/
 *
 * Determine if a string of brackets is valid (open and close in correct order).
 *
 * Time Complexity: O(n)
 * Space Complexity: O(n)
 */
public class ValidParentheses {

    private static final Map<Character, Character> MAP = Map.of(
        ')', '(', '}', '{', ']', '['
    );

    /**
     * Approach 1 (Optimal): Stack
     * Push opening brackets, pop and match on closing.
     * Time: O(n), Space: O(n)
     */
    public boolean isValid(String s) {
        Deque<Character> stack = new ArrayDeque<>();

        for (char c : s.toCharArray()) {
            if (MAP.containsKey(c)) {
                char top = stack.isEmpty() ? '#' : stack.pop();
                if (top != MAP.get(c)) return false;
            } else {
                stack.push(c);
            }
        }
        return stack.isEmpty();
    }

    public static void main(String[] args) {
        ValidParentheses vp = new ValidParentheses();

        System.out.println("Test 1: " + vp.isValid("()") + " (expected: true)");
        System.out.println("Test 2: " + vp.isValid("()[]{}") + " (expected: true)");
        System.out.println("Test 3: " + vp.isValid("(]") + " (expected: false)");
        System.out.println("Test 4: " + vp.isValid("([)]") + " (expected: false)");
        System.out.println("Test 5: " + vp.isValid("{[]}") + " (expected: true)");
        System.out.println("Test 6: " + vp.isValid("") + " (expected: true)");
    }
}
