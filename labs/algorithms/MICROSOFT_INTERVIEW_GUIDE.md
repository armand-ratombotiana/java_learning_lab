# Microsoft Interview Guide — Algorithms Academy

## Interview Process for Algorithm-Heavy Roles
- **Rounds**: Phone screen (1-2 rounds, 45 min) → Onsite (4 rounds, 45 min each). 1 coding, 1 system design, 1 component design, 1 behavioral + technical.
- **Timeline**: Recruiter reachout (1 week) → Phone screen (1-2 weeks) → Onsite (2-3 weeks). Decision in 5-10 days. Some teams do a "loop" across one day.
- **Algorithm Difficulty**: LeetCode Medium. Microsoft rarely asks LeetCode Hard problems. They focus on fundamentals: trees, sorting, strings, and DP.
- **How algorithm-heavy?**: Moderately heavy. 50% of rounds are algorithmic. The other rounds focus on design, architecture, and behavioral. Microsoft values correctness and clear communication.

## Top Problems by Algorithm Category

### Category: Trees and BST
#### Problem: Lowest Common Ancestor of BST (LC 235)
- **Difficulty/Frequency**: Easy / Very High
- **Problem statement**: Given a BST and two nodes, find their lowest common ancestor.
- **Interview walkthrough**: Since it's a BST, use the property: if both nodes are less than root, go left. If both greater, go right. Otherwise, root is LCA.
- **Solution approaches**: Iterative O(h). Recursive O(h). Microsoft expects the iterative approach for efficiency.
- **Java code**:
```java
/**
 * Solution for Lowest Common Ancestor of BST.
 */
public class LowestCommonAncestor {

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int val) { this.val = val; }
    }

    /**
     * Finds the LCA of two nodes in a BST.
     *
     * @param root root of BST
     * @param p    first node
     * @param q    second node
     * @return LCA node
     */
    public TreeNode lowestCommonAncestor(TreeNode root,
                                         TreeNode p, TreeNode q) {
        while (root != null) {
            if (p.val < root.val && q.val < root.val) {
                root = root.left;
            } else if (p.val > root.val && q.val > root.val) {
                root = root.right;
            } else {
                return root;
            }
        }
        return null;
    }
}
```
- **What Microsoft evaluates**: BST property usage, iterative vs recursive, null safety.
- **Follow-ups**: LCA of binary tree (not BST). LCA with parent pointers. LCA of K nodes.

#### Problem: Serialize and Deserialize Binary Tree (LC 297)
- **Difficulty/Frequency**: Hard / Medium-High
- **Problem statement**: Design algorithms to serialize a binary tree into a string and deserialize back.
- **Interview walkthrough**: Use preorder traversal with a delimiter and sentinel for nulls. For deserialization, use a queue of values. Recursively build the tree. Alternatively, use level order (BFS) serialization.
- **Solution approaches**: DFS preorder with queue O(n). BFS level order O(n). Microsoft expects preorder as it's simpler to reason about.
- **Java code**:
```java
import java.util.*;

/**
 * Solution for Serialize and Deserialize Binary Tree.
 */
public class SerializeDeserialize {

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int val) { this.val = val; }
    }

    private static final String NULL = "null";
    private static final String SEP = ",";

    /**
     * Serializes a tree to a string.
     *
     * @param root root of tree
     * @return serialized string
     */
    public String serialize(TreeNode root) {
        StringBuilder sb = new StringBuilder();
        serializeHelper(root, sb);
        return sb.toString();
    }

    private void serializeHelper(TreeNode node, StringBuilder sb) {
        if (node == null) {
            sb.append(NULL).append(SEP);
            return;
        }
        sb.append(node.val).append(SEP);
        serializeHelper(node.left, sb);
        serializeHelper(node.right, sb);
    }

    /**
     * Deserializes a string back to a tree.
     *
     * @param data serialized string
     * @return root of reconstructed tree
     */
    public TreeNode deserialize(String data) {
        Queue<String> queue = new LinkedList<>(
                Arrays.asList(data.split(SEP)));
        return deserializeHelper(queue);
    }

    private TreeNode deserializeHelper(Queue<String> queue) {
        String val = queue.poll();
        if (val.equals(NULL)) return null;
        TreeNode node = new TreeNode(Integer.parseInt(val));
        node.left = deserializeHelper(queue);
        node.right = deserializeHelper(queue);
        return node;
    }
}
```
- **What Microsoft evaluates**: Understanding of tree traversal, recursion for construction, delimiter-based encoding, edge cases (empty tree).
- **Follow-ups**: Serialize BST (more compact). Serialize N-ary tree. Use JSON/XML format.

### Category: Sorting and Searching
#### Problem: Search in Rotated Sorted Array (LC 33)
- **Difficulty/Frequency**: Medium / Very High
- **Problem statement**: Given a rotated sorted array, search for a target and return its index. O(log n) required.
- **Interview walkthrough**: Modified binary search. Find which half is sorted. If left half is sorted (nums[left] <= nums[mid]), check if target is in that range. Otherwise target is in right half. Adjust pointers accordingly.
- **Solution approaches**: Binary search O(log n). Microsoft expects the clean while-loop version.
- **Java code**:
```java
/**
 * Solution for Search in Rotated Sorted Array.
 */
public class SearchRotatedArray {

    /**
     * Searches target in rotated sorted array.
     *
     * @param nums   rotated sorted array
     * @param target value to search
     * @return index of target, or -1
     */
    public int search(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) return mid;
            if (nums[left] <= nums[mid]) {
                if (target >= nums[left] && target < nums[mid]) {
                    right = mid - 1;
                } else {
                    left = mid + 1;
                }
            } else {
                if (target > nums[mid] && target <= nums[right]) {
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }
        }
        return -1;
    }
}
```
- **What Microsoft evaluates**: Binary search modification, handling of duplicates (if allowed), clean conditionals.
- **Follow-ups**: Array with duplicates (LC 81). Find minimum in rotated sorted array. Search in rotated sorted array II.

#### Problem: Find First and Last Position of Element in Sorted Array (LC 34)
- **Difficulty/Frequency**: Medium / High
- **Problem statement**: Given a sorted array and a target, find the first and last position of target. O(log n) required.
- **Interview walkthrough**: Two binary searches: one for the first occurrence (move left on equal), one for the last (move right on equal). Write a helper with a boolean parameter.
- **Solution approaches**: Two binary searches O(log n). Microsoft expects the clean two-search approach.
- **Java code**:
```java
/**
 * Solution for Find First and Last Position.
 */
public class FirstLastPosition {

    /**
     * Returns first and last position of target in sorted array.
     *
     * @param nums   sorted array
     * @param target value to find
     * @return array [first, last], [-1, -1] if not found
     */
    public int[] searchRange(int[] nums, int target) {
        int first = findBound(nums, target, true);
        if (first == -1) return new int[]{-1, -1};
        int last = findBound(nums, target, false);
        return new int[]{first, last};
    }

    private int findBound(int[] nums, int target, boolean isFirst) {
        int left = 0, right = nums.length - 1, bound = -1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) {
                bound = mid;
                if (isFirst) {
                    right = mid - 1;
                } else {
                    left = mid + 1;
                }
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return bound;
    }
}
```
- **What Microsoft evaluates**: Binary search with bound search, code reuse via helper function, handling of not-found case.
- **Follow-ups**: Count occurrences of target. Find peak in mountain array. Search in a nearly sorted array.

### Category: Strings
#### Problem: Reverse Words in a String (LC 151)
- **Difficulty/Frequency**: Medium / High
- **Problem statement**: Given an input string, reverse the order of words. Trim leading/trailing spaces and reduce multiple spaces between words to single.
- **Interview walkthrough**: Trim, split by spaces, iterate in reverse building result. Or do in-place: reverse entire string, then reverse each word. Microsoft likes the two-pointer in-place approach.
- **Solution approaches**: Split and reverse O(n). In-place reversal O(n). Microsoft expects either but appreciates the in-place method.
- **Java code**:
```java
/**
 * Solution for Reverse Words in a String.
 */
public class ReverseWords {

    /**
     * Reverses the order of words in the string.
     *
     * @param s input string
     * @return reversed words string
     */
    public String reverseWords(String s) {
        String[] words = s.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = words.length - 1; i >= 0; i--) {
            sb.append(words[i]);
            if (i > 0) sb.append(' ');
        }
        return sb.toString();
    }
}
```
- **What Microsoft evaluates**: String manipulation, regex split, edge cases (empty string, multiple spaces), understanding of immutability.
- **Follow-ups**: Reverse words in-place using char array. Reverse the order of characters in each word (not word order). Reverse words with two-pointer.

#### Problem: String to Integer (atoi) (LC 8)
- **Difficulty/Frequency**: Medium / Medium
- **Problem statement**: Implement atoi to convert a string to an integer, handling whitespace, sign, overflow, and invalid characters.
- **Interview walkthrough**: Skip leading whitespace. Handle optional sign. Process digits until non-digit. Check overflow before each addition using Integer.MAX_VALUE/10 comparison.
- **Solution approaches**: Deterministic finite automaton (DFA). Straightforward iterative parsing O(n). Microsoft expects the iterative approach with careful overflow handling.
- **Java code**:
```java
/**
 * Solution for String to Integer (atoi).
 */
public class StringToInteger {

    /**
     * Converts string to 32-bit signed integer.
     *
     * @param s input string
     * @return parsed integer, clamped to [Integer.MIN_VALUE, Integer.MAX_VALUE]
     */
    public int myAtoi(String s) {
        int i = 0, sign = 1, result = 0;
        if (s.length() == 0) return 0;
        while (i < s.length() && s.charAt(i) == ' ') i++;
        if (i < s.length() && (s.charAt(i) == '+' || s.charAt(i) == '-')) {
            sign = (s.charAt(i) == '-') ? -1 : 1;
            i++;
        }
        while (i < s.length() && Character.isDigit(s.charAt(i))) {
            int digit = s.charAt(i) - '0';
            if (result > (Integer.MAX_VALUE - digit) / 10) {
                return sign == 1 ? Integer.MAX_VALUE : Integer.MIN_VALUE;
            }
            result = result * 10 + digit;
            i++;
        }
        return result * sign;
    }
}
```
- **What Microsoft evaluates**: Comprehensive edge case handling, overflow detection, state machine thinking.
- **Follow-ups**: Encode number to English words. Roman to integer. Validate a numeric string.

### Category: Dynamic Programming
#### Problem: Word Break (LC 139)
- **Difficulty/Frequency**: Medium / High
- **Problem statement**: Given a string and a dictionary of words, determine if the string can be segmented into a space-separated sequence of dictionary words.
- **Interview walkthrough**: DP where dp[i] = true if s[0..i] can be segmented. For each position, check all dictionary words that end at this position. Optimize with a set for O(1) lookups.
- **Solution approaches**: DP O(n^2 * k) naive. DP with set O(n^2). BFS approach. Microsoft expects DP with set.
- **Java code**:
```java
import java.util.*;

/**
 * Solution for Word Break.
 */
public class WordBreak {

    /**
     * Determines if string can be segmented into dictionary words.
     *
     * @param s        input string
     * @param wordDict list of dictionary words
     * @return true if segmentable
     */
    public boolean wordBreak(String s, List<String> wordDict) {
        Set<String> dict = new HashSet<>(wordDict);
        boolean[] dp = new boolean[s.length() + 1];
        dp[0] = true;
        for (int i = 1; i <= s.length(); i++) {
            for (int j = 0; j < i; j++) {
                if (dp[j] && dict.contains(s.substring(j, i))) {
                    dp[i] = true;
                    break;
                }
            }
        }
        return dp[s.length()];
    }
}
```
- **What Microsoft evaluates**: DP formulation on strings, set optimization, substring handling (performance note).
- **Follow-ups**: Word Break II (return all sentences). Minimum number of unrecognized characters. Word break with wildcards.

#### Problem: House Robber (LC 198)
- **Difficulty/Frequency**: Medium / Very High
- **Problem statement**: Given an array of money in houses, find the maximum amount you can rob without robbing adjacent houses.
- **Interview walkthrough**: DP: dp[i] = max(dp[i-1], dp[i-2] + nums[i]). Optimize to O(1) space with two variables.
- **Solution approaches**: DP array O(n) space. Rolling variables O(1) space. Microsoft expects the O(1) space version.
- **Java code**:
```java
/**
 * Solution for House Robber.
 */
public class HouseRobber {

    /**
     * Returns maximum amount that can be robbed without alerting police.
     *
     * @param nums array of money in each house
     * @return maximum amount
     */
    public int rob(int[] nums) {
        if (nums.length == 0) return 0;
        int prev2 = 0, prev1 = 0;
        for (int num : nums) {
            int temp = prev1;
            prev1 = Math.max(prev2 + num, prev1);
            prev2 = temp;
        }
        return prev1;
    }
}
```
- **What Microsoft evaluates**: Linear DP, space optimization, clear recurrence.
- **Follow-ups**: House Robber II (circular houses). House Robber III (tree houses). Paint house (with colors).

### Category: Math and Number Theory
#### Problem: Pow(x, n) (LC 50)
- **Difficulty/Frequency**: Medium / High
- **Problem statement**: Implement pow(x, n), calculating x raised to the power n.
- **Interview walkthrough**: Exponentiation by squaring. Handle negative exponents by using 1/x. Use recursion: if n is even, pow(x, n/2)^2. If odd, x * pow(x, n-1). Iterative approach also possible.
- **Solution approaches**: Recursive O(log n). Iterative O(log n). Microsoft expects the recursive or iterative log approach.
- **Java code**:
```java
/**
 * Solution for Pow(x, n).
 */
public class PowXN {

    /**
     * Computes x raised to the power n.
     *
     * @param x base
     * @param n exponent
     * @return x^n
     */
    public double myPow(double x, int n) {
        if (n == 0) return 1.0;
        if (n < 0) {
            x = 1 / x;
            n = -n;
        }
        double result = 1.0;
        while (n > 0) {
            if ((n & 1) == 1) result *= x;
            x *= x;
            n >>= 1;
        }
        return result;
    }
}
```
- **What Microsoft evaluates**: Exponentiation by squaring, handling of Integer.MIN_VALUE (negation overflow), bit manipulation for efficiency.
- **Follow-ups**: Sqrt(x). Divide two integers without division. Super pow (with large exponent array).

### Category: Greedy
#### Problem: Jump Game (LC 55)
- **Difficulty/Frequency**: Medium / High
- **Problem statement**: Given an array where each element is max jump length from that position, determine if you can reach the last index.
- **Interview walkthrough**: Greedy: maintain the farthest reachable index. For each position, if it's beyond the farthest reachable, fail. Update farthest = max(farthest, i + nums[i]).
- **Solution approaches**: Greedy O(n). DP O(n^2). Microsoft expects greedy.
- **Java code**:
```java
/**
 * Solution for Jump Game.
 */
public class JumpGame {

    /**
     * Determines if the last index can be reached.
     *
     * @param nums array of max jump lengths
     * @return true if reachable
     */
    public boolean canJump(int[] nums) {
        int farthest = 0;
        for (int i = 0; i < nums.length; i++) {
            if (i > farthest) return false;
            farthest = Math.max(farthest, i + nums[i]);
            if (farthest >= nums.length - 1) return true;
        }
        return false;
    }
}
```
- **What Microsoft evaluates**: Greedy reasoning proof, handling of edge cases (all zeros), optimization awareness.
- **Follow-ups**: Jump Game II (minimum jumps). Jump Game III (with jumps to index i+nums[i] or i-nums[i]). Reach a given index.

## Company-Specific Algorithm Focus
Microsoft emphasizes **trees and BST operations** more than any other FAANG company. They also focus on **sorting and searching** with binary search variants. Microsoft values **mathematical reasoning** (exponentiation, sqrt, division). They also care about **string algorithms** and **greedy approaches**. Microsoft rarely asks advanced graph algorithms like network flow. Their interview style is more conversational — they want to see how you think.

## System Design with Algorithms
1. **Design Excel Spreadsheet** — Requires topological sort for cell dependency resolution (like DP-based circuit evaluation), expression parsing (shunting yard algorithm), and lazy evaluation optimizations using directed acyclic graph traversal.
2. **Design Bing Search Indexer** — Requires inverted index construction (merge sort of postings lists), page rank computation using power iteration (sparse matrix multiplication), and document similarity using cosine similarity with TF-IDF vectors.

## Behavioral Questions (STAR)
1. **Tell me about a time you solved a difficult technical problem**: I needed to implement a spell-checker for a search feature. I used the Levenshtein distance DP algorithm, then optimized using a BK-tree for prefix-based nearest neighbor search. This reduced correction time from 500ms to 5ms per query.
2. **Tell me about a time you collaborated effectively**: I worked with three teams to design a distributed locking system. I implemented the core algorithm — a consensus-based lock using the Raft protocol with FFT-based leader election timeout jittering. This required deep understanding of distributed systems algorithms.
3. **Tell me about how you approach design**: For a rate limiter, I compared token bucket vs sliding window algorithms. Token bucket allowed bursts, sliding window was smoother. I prototyped both. Token bucket was chosen because it had simpler math and fit our traffic model.
4. **Tell me about a time you had to learn quickly**: I had to implement a bloom filter for a cache deduplication system in one week. I studied the trade-off between false positive rate and number of hash functions, implemented MurmurHash, and designed the bit array sizing formula. It achieved a 0.1% false positive rate with 3x memory savings.
5. **Tell me about a time you adapted a solution**: A scheduling algorithm using standard topological sort did not work because tasks had shared resources. I adapted it using a resource-constrained project scheduling algorithm (critical path with resource leveling using min-heap priority dispatching).

## Study Plan
Prioritize these labs in order:
1. Lab 6: Tree Algorithms
2. Lab 14: Sorting and Searching
3. Lab 10: Array and String Algorithms
4. Lab 4: Dynamic Programming I
5. Lab 9: Greedy Algorithms
6. Lab 13: Number Theory
7. Lab 15: Bit Manipulation

## Tips
- Microsoft interviews are **conversational**, not adversarial. They want to see your thought process, not just the final answer.
- **Ask questions** about the problem. Microsoft interviewers explicitly evaluate your ability to gather requirements.
- Microsoft cares about **testing**. After writing code, walk through test cases verbally: normal cases, edge cases, corner cases.
- Expect **follow-ups that generalize** the problem. "What if we have N dimensions instead of 2?" or "What if the input is a stream?"
- Microsoft likes **component design** questions (design Excel, design PowerPoint) that blend algorithms with systems thinking.
- **Write maintainable code**. The interviewer will read it line by line. Use clear variable names and modular functions.
- If you use recursion, consider the **stack depth** and discuss when you would switch to iteration.
