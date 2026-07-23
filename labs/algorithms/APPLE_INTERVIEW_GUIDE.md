# Apple Interview Guide — Algorithms Academy

## Interview Process for Algorithm-Heavy Roles
- **Rounds**: Recruiter screen → 1-2 technical phone screens (45 min each) → Onsite (6-8 rounds, 1 hour each) including lunch. Separate rounds for algorithms, systems, and manager.
- **Timeline**: Recruiter (1 week) → Phone (1-2 weeks) → Onsite (3-5 weeks). Apple is known for having longer hiring timelines. Decision can take 2-3 weeks.
- **Algorithm Difficulty**: Varied from Easy to Hard. Apple likes linked lists, trees, threading/concurrency, and memory management. They value low-level understanding.
- **How algorithm-heavy?**: Moderately heavy. 40-50% of rounds are pure algorithms. Many rounds mix algorithms with system knowledge, especially for performance-critical teams.

## Top Problems by Algorithm Category

### Category: Linked Lists
#### Problem: Add Two Numbers (LC 2)
- **Difficulty/Frequency**: Medium / Very High
- **Problem statement**: Given two non-empty linked lists representing non-negative integers in reverse order, add them and return the sum as a linked list.
- **Interview walkthrough**: Iterate both lists simultaneously. Track carry. Sum values at each position plus carry. Create new node with sum % 10. Update carry = sum / 10. Handle remaining digits and final carry.
- **Solution approaches**: Iterative O(max(m,n)). Recursive O(max(m,n)). Apple expects iterative with dummy head.
- **Java code**:
```java
/**
 * Solution for Add Two Numbers.
 */
public class AddTwoNumbers {

    public static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
    }

    /**
     * Adds two numbers represented as reverse-order linked lists.
     *
     * @param l1 first number list
     * @param l2 second number list
     * @return sum as linked list
     */
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(0);
        ListNode curr = dummy;
        int carry = 0;
        while (l1 != null || l2 != null || carry != 0) {
            int sum = carry;
            if (l1 != null) {
                sum += l1.val;
                l1 = l1.next;
            }
            if (l2 != null) {
                sum += l2.val;
                l2 = l2.next;
            }
            curr.next = new ListNode(sum % 10);
            carry = sum / 10;
            curr = curr.next;
        }
        return dummy.next;
    }
}
```
- **What Apple evaluates**: Linked list manipulation, carry handling, dummy node pattern, edge cases (different lengths, final carry).
- **Follow-ups**: Add two numbers in forward order (reverse first). Multiply two numbers represented as lists. Subtract two numbers.

#### Problem: Reverse Linked List (LC 206)
- **Difficulty/Frequency**: Easy / Very High
- **Problem statement**: Reverse a singly linked list.
- **Interview walkthrough**: Iteratively, maintain prev, curr, next pointers. Set curr.next = prev, then advance. Recursively: reverse head.next, set head.next.next = head, head.next = null.
- **Solution approaches**: Iterative O(n). Recursive O(n). Apple typically wants both versions discussed.
- **Java code**:
```java
/**
 * Solution for Reverse Linked List.
 */
public class ReverseLinkedList {

    public static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
    }

    /**
     * Reverses a singly linked list iteratively.
     *
     * @param head head of list
     * @return new head of reversed list
     */
    public ListNode reverseList(ListNode head) {
        ListNode prev = null;
        ListNode curr = head;
        while (curr != null) {
            ListNode next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }
        return prev;
    }
}
```
- **What Apple evaluates**: Pointer manipulation fundamentals, recursion depth considerations, null safety.
- **Follow-ups**: Reverse between positions m and n. Reverse in groups of k. Reverse alternating K nodes. Check if linked list is palindrome.

### Category: Arrays
#### Problem: Trapping Rain Water (LC 42)
- **Difficulty/Frequency**: Hard / High
- **Problem statement**: Given n non-negative integers representing elevation map, compute how much water it can trap after raining.
- **Interview walkthrough**: Two-pointer approach: left and right pointers, track leftMax and rightMax. At each step, process the smaller of leftMax and rightMax. If current height is less than max, add difference to result. Otherwise update max.
- **Solution approaches**: Brute force O(n^2). DP with left/right max arrays O(n). Stack O(n). Two-pointer O(1) space O(n). Apple expects two-pointer for optimal solution.
- **Java code**:
```java
/**
 * Solution for Trapping Rain Water.
 */
public class TrappingRainWater {

    /**
     * Computes how much water can be trapped.
     *
     * @param height elevation map
     * @return total trapped water
     */
    public int trap(int[] height) {
        int left = 0, right = height.length - 1;
        int leftMax = 0, rightMax = 0, total = 0;
        while (left < right) {
            if (height[left] < height[right]) {
                if (height[left] >= leftMax) {
                    leftMax = height[left];
                } else {
                    total += leftMax - height[left];
                }
                left++;
            } else {
                if (height[right] >= rightMax) {
                    rightMax = height[right];
                } else {
                    total += rightMax - height[right];
                }
                right--;
            }
        }
        return total;
    }
}
```
- **What Apple evaluates**: Two-pointer optimization, understanding of the physical constraint, O(n) time and O(1) space solution.
- **Follow-ups**: Product of array except self. Pour water between buildings. Container with most water.

### Category: Trees
#### Problem: Binary Tree Maximum Path Sum (LC 124)
- **Difficulty/Frequency**: Hard / High
- **Problem statement**: Given a binary tree, find the maximum path sum. Path can start and end at any node.
- **Interview walkthrough**: Post-order traversal. For each node, compute max sum through left child, through right child, and through node only. The max path through node = node.val + max(0, leftGain) + max(0, rightGain). Return node.val + max(0, leftGain, rightGain) to parent.
- **Solution approaches**: Recursive DFS O(n). Apple expects clean recursion with global max variable.
- **Java code**:
```java
/**
 * Solution for Binary Tree Maximum Path Sum.
 */
public class MaxPathSum {

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int val) { this.val = val; }
    }

    private int maxSum = Integer.MIN_VALUE;

    /**
     * Returns the maximum path sum in the binary tree.
     *
     * @param root root of tree
     * @return maximum path sum
     */
    public int maxPathSum(TreeNode root) {
        gainFromSubtree(root);
        return maxSum;
    }

    private int gainFromSubtree(TreeNode node) {
        if (node == null) return 0;
        int leftGain = Math.max(0, gainFromSubtree(node.left));
        int rightGain = Math.max(0, gainFromSubtree(node.right));
        maxSum = Math.max(maxSum, leftGain + rightGain + node.val);
        return node.val + Math.max(leftGain, rightGain);
    }
}
```
- **What Apple evaluates**: Tree traversal recursion, careful handling of negative values, global state management for max.
- **Follow-ups**: Sum of longest path from root to leaf. Diameter of binary tree. Path sum III (any path, target sum).

#### Problem: Invert Binary Tree (LC 226)
- **Difficulty/Frequency**: Easy / High
- **Problem statement**: Invert a binary tree (mirror it).
- **Interview walkthrough**: Swap left and right children, then recursively invert both subtrees. Also possible iteratively with BFS.
- **Solution approaches**: Recursive O(n). Iterative BFS O(n). Apple expects the recursive one-liner style.
- **Java code**:
```java
/**
 * Solution for Invert Binary Tree.
 */
public class InvertBinaryTree {

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int val) { this.val = val; }
    }

    /**
     * Inverts a binary tree (mirror).
     *
     * @param root root of tree
     * @return root of inverted tree
     */
    public TreeNode invertTree(TreeNode root) {
        if (root == null) return null;
        TreeNode temp = root.left;
        root.left = invertTree(root.right);
        root.right = invertTree(temp);
        return root;
    }
}
```
- **What Apple evaluates**: Clean recursion, understanding of tree structure, simplicity.
- **Follow-ups**: Symmetric tree. Merge two binary trees. Flip equivalent binary trees.

### Category: Dynamic Programming
#### Problem: Longest Palindromic Substring (LC 5)
- **Difficulty/Frequency**: Medium / Very High
- **Problem statement**: Given a string, find the longest palindromic substring.
- **Interview walkthrough**: Expand around center: every palindrome expands from its center. For each position, check odd-length (single center) and even-length (two centers). Expand outward while characters match. Track max.
- **Solution approaches**: Brute force O(n^3). Expand around center O(n^2). DP O(n^2). Manacher's algorithm O(n). Apple expects expand around center for interview.
- **Java code**:
```java
/**
 * Solution for Longest Palindromic Substring.
 */
public class LongestPalindromicSubstring {

    private int start = 0, maxLen = 0;

    /**
     * Returns the longest palindromic substring.
     *
     * @param s input string
     * @return longest palindrome substring
     */
    public String longestPalindrome(String s) {
        if (s == null || s.length() < 2) return s;
        for (int i = 0; i < s.length(); i++) {
            expand(s, i, i);
            expand(s, i, i + 1);
        }
        return s.substring(start, start + maxLen);
    }

    private void expand(String s, int left, int right) {
        while (left >= 0 && right < s.length()
                && s.charAt(left) == s.charAt(right)) {
            left--;
            right++;
        }
        int len = right - left - 1;
        if (len > maxLen) {
            maxLen = len;
            start = left + 1;
        }
    }
}
```
- **What Apple evaluates**: Center expansion technique, handling of odd/even length palindromes, substring extraction.
- **Follow-ups**: Count palindromic substrings. Longest palindromic subsequence. Shortest palindrome by adding characters in front.

#### Problem: Climbing Stairs (LC 70)
- **Difficulty/Frequency**: Easy / Very High
- **Problem statement**: You are climbing a staircase with n steps. Each time you can climb 1 or 2 steps. In how many distinct ways can you climb to the top?
- **Interview walkthrough**: This is Fibonacci: ways(n) = ways(n-1) + ways(n-2). Use iterative DP with two variables for O(1) space.
- **Solution approaches**: Recursion O(2^n). DP O(n). Matrix exponentiation O(log n). Apple expects the iterative O(n) version.
- **Java code**:
```java
/**
 * Solution for Climbing Stairs.
 */
public class ClimbingStairs {

    /**
     * Returns number of distinct ways to climb n steps.
     *
     * @param n number of steps
     * @return number of ways
     */
    public int climbStairs(int n) {
        if (n <= 2) return n;
        int prev2 = 1, prev1 = 2;
        for (int i = 3; i <= n; i++) {
            int current = prev1 + prev2;
            prev2 = prev1;
            prev1 = current;
        }
        return prev1;
    }
}
```
- **What Apple evaluates**: DP basics, Fibonacci-like recurrence, space optimization.
- **Follow-ups**: Climb with 1, 2, or 3 steps. Climb with cost. Min cost climbing stairs.

### Category: Bit Manipulation
#### Problem: Single Number (LC 136)
- **Difficulty/Frequency**: Easy / Very High
- **Problem statement**: Given a non-empty array where every element appears twice except one, find that single element.
- **Interview walkthrough**: XOR all elements. a XOR a = 0. So pairs cancel out, leaving the single element.
- **Solution approaches**: HashMap O(n) space. HashSet O(n). XOR O(1) space. Apple expects XOR.
- **Java code**:
```java
/**
 * Solution for Single Number using XOR.
 */
public class SingleNumber {

    /**
     * Returns the element that appears only once.
     *
     * @param nums array where every element appears twice except one
     * @return the single element
     */
    public int singleNumber(int[] nums) {
        int result = 0;
        for (int num : nums) {
            result ^= num;
        }
        return result;
    }
}
```
- **What Apple evaluates**: Bitwise XOR properties, O(1) space thinking, understanding of why XOR works.
- **Follow-ups**: Single Number II (each appears 3 times). Single Number III (two singles). Find missing number.

#### Problem: Number of 1 Bits (LC 191)
- **Difficulty/Frequency**: Easy / High
- **Problem statement**: Write a function that returns the number of '1' bits in an integer (Hamming weight).
- **Interview walkthrough**: Use n & (n-1) trick to clear the lowest set bit. Count iterations until n = 0.
- **Solution approaches**: Loop over 32 bits O(32). n & (n-1) O(number of 1 bits). Built-in Integer.bitCount. Apple expects the n & (n-1) method.
- **Java code**:
```java
/**
 * Solution for Number of 1 Bits.
 */
public class NumberOf1Bits {

    /**
     * Returns the number of 1 bits in the binary representation.
     *
     * @param n input integer (unsigned)
     * @return count of 1 bits
     */
    public int hammingWeight(int n) {
        int count = 0;
        while (n != 0) {
            count++;
            n = n & (n - 1);
        }
        return count;
    }
}
```
- **What Apple evaluates**: Bit manipulation trick (n & n-1), understanding of binary representation, efficiency awareness.
- **Follow-ups**: Counting bits for numbers 1 to n. Reverse bits. Power of two. Power of four.

### Category: Concurrency and Multithreading
#### Problem: Print in Order (LC 1114)
- **Difficulty/Frequency**: Easy / Medium (Apple-specific relevance)
- **Problem statement**: Given three threads that call first(), second(), third() respectively, ensure they execute in order.
- **Interview walkthrough**: Use a CountDownLatch or synchronized with volatile flags. Thread 2 waits for thread 1 to finish. Thread 3 waits for thread 2.
- **Solution approaches**: CountDownLatch. Semaphore. volatile + busy wait. synchronized with wait/notify. Apple expects knowledge of Java concurrency primitives.
- **Java code**:
```java
import java.util.concurrent.CountDownLatch;

/**
 * Solution for Print in Order using CountDownLatch.
 */
public class PrintInOrder {

    private CountDownLatch latch1 = new CountDownLatch(1);
    private CountDownLatch latch2 = new CountDownLatch(1);

    /**
     * Called by first thread.
     */
    public void first(Runnable printFirst) {
        printFirst.run();
        latch1.countDown();
    }

    /**
     * Called by second thread, waits for first.
     */
    public void second(Runnable printSecond) throws InterruptedException {
        latch1.await();
        printSecond.run();
        latch2.countDown();
    }

    /**
     * Called by third thread, waits for second.
     */
    public void third(Runnable printThird) throws InterruptedException {
        latch2.await();
        printThird.run();
    }
}
```
- **What Apple evaluates**: Concurrency primitive knowledge, thread coordination, handling of shared state.
- **Follow-ups**: Print FooBar alternately. FizzBuzz multithreaded. Dining philosophers problem.

## Company-Specific Algorithm Focus
Apple places heavy emphasis on **linked lists** and **low-level memory operations**. They are unique in their focus on **bit manipulation** and **concurrency** — reflecting their OS-level and hardware work. Apple also emphasizes **trees** (especially for their graphics and UI frameworks). They value **elegant, clean code** and deeply understand the performance characteristics of their algorithms. Apple rarely asks advanced math or number theory outside of cryptography teams.

## System Design with Algorithms
1. **Design AirDrop Discovery Protocol** — Requires Bonjour service discovery using multicast DNS, bloom filters for device capability advertising, and graph-based routing for multi-hop file transfer. Discuss BFS for device discovery and greedy relay selection.
2. **Design Photos Facial Recognition** — Requires face detection using Viola-Jones algorithm, feature extraction with LBP (Local Binary Patterns), k-NN for nearest neighbor search in embedding space, and k-d tree for efficient similarity search at scale.

## Behavioral Questions (STAR)
1. **Tell me about a time you solved a performance problem**: A graphics pipeline was stall-bound due to poor memory layout. I reorganized the data structure from array of structs to struct of arrays, improving cache locality. The rendering algorithm changed from random access to sequential streaming, resulting in 3x throughput improvement.
2. **Tell me about a time you navigated ambiguity**: Product requirements changed mid-sprint and the algorithm we built for image segmentation no longer applied. I designed a parameterized pipeline using strategy pattern where we could swap segmentation algorithms (watershed, k-means, or graph-cut) without changing the rest of the code.
3. **Tell me about a time you had to ensure quality**: I built a fuzz testing framework for a compression algorithm. I generated random inputs and verified that decompression always produced the original. This caught a buffer overflow bug caused by an incorrect arithmetic encoding boundary condition.
4. **Tell me about a time you made a hardware-software trade-off**: We needed real-time video processing. I compared a software-only algorithm (optimized with SIMD) vs a GPU offloading approach. The GPU path was faster but consumed more power. I chose the SIMD software approach to maximize battery life for mobile devices.
5. **Tell me about a time you worked cross-functionally**: I worked with the silicon team to implement a hardware-accelerated AES encryption algorithm. I designed the software layer using the crypto API while the hardware team exposed custom CPU instructions. We optimized the key schedule computation using pre-computation and on-the-fly generation based on memory vs speed trade-off analysis.

## Study Plan
Prioritize these labs in order:
1. Lab 2: Linked Lists
2. Lab 1: Arrays
3. Lab 15: Bit Manipulation
4. Lab 6: Tree Algorithms
5. Lab 4: Dynamic Programming I
6. Lab 17: Concurrency
7. Lab 10: Array and String Algorithms

## Tips
- Apple interviews are **more technical and detail-oriented** than other FAANG interviews. Expect questions about the algorithm's performance on specific hardware.
- **Memory management awareness** is critical. Discuss stack vs heap allocation, object pooling, and garbage collection avoidance.
- Apple interviewers **value simplicity** — the simplest correct solution is often preferred over a complex optimized one.
- Be ready to **implement algorithms from scratch** without using built-in libraries. Apple teams often work on embedded systems where library usage is restricted.
- **Discuss concurrency implications** of your algorithm. Would it be thread-safe? How would you parallelize it?
- Apple cares about **user experience**. Tie algorithm decisions back to user-facing metrics like responsiveness, battery life, and animation smoothness.
- **Prepare for questions about specific Apple technologies**: Core Data (graph traversal for faulting), Core Animation (quatree for spatial indexing), and Metal (GPU compute algorithms).
