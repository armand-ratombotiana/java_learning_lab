# Amazon Interview Guide — Algorithms Academy

## Interview Process for Algorithm-Heavy Roles
- **Rounds**: Phone screen (1 hour) → Onsite (4-5 rounds: 2-3 coding, 1 system design, 1 bar raiser). Coding rounds are 60 minutes.
- **Timeline**: Initial recruiter screen within a week. Phone screen within 2 weeks. Onsite within 3-4 weeks of phone screen. Decision within 5 business days.
- **Algorithm Difficulty**: LeetCode Medium to Hard. Amazon loves arrays, strings, BFS/DFS, and design patterns. They focus less on niche algorithms and more on fundamental data structures.
- **How algorithm-heavy?**: Very heavy but practical. 60% of rounds are pure algorithm problems. The system design round also requires algorithm knowledge for scalability discussions.

## Top Problems by Algorithm Category

### Category: Arrays and HashMaps
#### Problem: Two Sum (LC 1)
- **Difficulty/Frequency**: Easy / Very High
- **Problem statement**: Given an array of integers and a target, return indices of the two numbers that add up to target.
- **Interview walkthrough**: Clarify if exactly one solution exists. Use a HashMap to store complement values as we iterate. For each number, check if target - nums[i] is in the map. If yes, return indices. Otherwise, store current number and its index.
- **Solution approaches**: Brute force O(n^2). HashMap O(n). Amazon expects the HashMap approach and may ask you to handle multiple pairs or negative numbers.
- **Java code**:
```java
import java.util.HashMap;
import java.util.Map;

/**
 * Solution for Two Sum problem.
 * Returns indices of two numbers that add up to target.
 */
public class TwoSum {

    /**
     * Finds indices of two numbers that sum to target.
     *
     * @param nums   input array
     * @param target target sum
     * @return array of two indices
     */
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (map.containsKey(complement)) {
                return new int[]{map.get(complement), i};
            }
            map.put(nums[i], i);
        }
        throw new IllegalArgumentException("No solution found");
    }
}
```
- **What Amazon evaluates**: Space-time trade-off thinking, HashMap proficiency, handling edge cases (empty array, no solution).
- **Follow-ups**: Three Sum. Four Sum. What if array is sorted (two-pointer). What if we need all unique pairs.

#### Problem: Group Anagrams (LC 49)
- **Difficulty/Frequency**: Medium / High
- **Problem statement**: Given an array of strings, group anagrams together.
- **Interview walkthrough**: For each string, sort its characters to form a key, or count character frequencies. Use HashMap with key -> list mapping. Return all values as a list of lists.
- **Solution approaches**: Sorting approach O(n*k*logk). Character count approach O(n*k). Amazon likes both and may discuss which is better for different constraints.
- **Java code**:
```java
import java.util.*;

/**
 * Solution for Group Anagrams.
 */
public class GroupAnagrams {

    /**
     * Groups anagrams from the input strings.
     *
     * @param strs array of strings
     * @return list of grouped anagrams
     */
    public List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> map = new HashMap<>();
        for (String s : strs) {
            char[] chars = s.toCharArray();
            Arrays.sort(chars);
            String key = new String(chars);
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
        }
        return new ArrayList<>(map.values());
    }
}
```
- **What Amazon evaluates**: HashMap cleverness, amortized cost analysis of sorting vs counting, clean code with modern Java APIs.
- **Follow-ups**: Group anagrams efficiently for very long strings. Anagram checking without sorting using prime product.

#### Problem: Top K Frequent Elements (LC 347)
- **Difficulty/Frequency**: Medium / High
- **Problem statement**: Given an array of integers, return the k most frequent elements.
- **Interview walkthrough**: Count frequencies with HashMap. Use a bucket list where index = frequency. Iterate from highest frequency bucket to build result. Or use a min-heap of size k.
- **Solution approaches**: Min-heap O(n log k). Bucket sort O(n). QuickSelect O(n) average. Amazon expects at least the bucket sort or heap solution.
- **Java code**:
```java
import java.util.*;

/**
 * Solution for Top K Frequent Elements using bucket sort.
 */
public class TopKFrequent {

    /**
     * Returns the k most frequent elements.
     *
     * @param nums input array
     * @param k    number of top elements to return
     * @return array of k most frequent elements
     */
    public int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (int n : nums) {
            freq.put(n, freq.getOrDefault(n, 0) + 1);
        }
        List<Integer>[] buckets = new List[nums.length + 1];
        for (int key : freq.keySet()) {
            int f = freq.get(key);
            if (buckets[f] == null) {
                buckets[f] = new ArrayList<>();
            }
            buckets[f].add(key);
        }
        int[] result = new int[k];
        int idx = 0;
        for (int i = buckets.length - 1; i >= 0 && idx < k; i--) {
            if (buckets[i] != null) {
                for (int num : buckets[i]) {
                    result[idx++] = num;
                    if (idx == k) break;
                }
            }
        }
        return result;
    }
}
```
- **What Amazon evaluates**: Frequency counting with bucket sort logic, handling of edge cases (k larger than distinct elements), O(n) solution thinking.
- **Follow-ups**: What if the input is a stream? What if you need to return elements in frequency order?

### Category: Trees
#### Problem: Binary Tree Level Order Traversal (LC 102)
- **Difficulty/Frequency**: Medium / Very High
- **Problem statement**: Given a binary tree, return its level order traversal (left to right, level by level).
- **Interview walkthrough**: Use BFS with a queue. Track size of current level to know when to start a new list. Process all nodes of current level before moving to next.
- **Solution approaches**: BFS with queue O(n). DFS with recursion and level tracking. Amazon expects BFS first.
- **Java code**:
```java
import java.util.*;

/**
 * Solution for Binary Tree Level Order Traversal.
 */
public class LevelOrderTraversal {

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int val) { this.val = val; }
    }

    /**
     * Returns level order traversal of a binary tree.
     *
     * @param root root of the tree
     * @return list of levels, each level is a list of node values
     */
    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) return result;
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            List<Integer> level = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                level.add(node.val);
                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }
            result.add(level);
        }
        return result;
    }
}
```
- **What Amazon evaluates**: BFS fundamentals, null handling, size-based level separation.
- **Follow-ups**: Zigzag level order. Vertical order traversal. Level order bottom-up.

#### Problem: Validate Binary Search Tree (LC 98)
- **Difficulty/Frequency**: Medium / High
- **Problem statement**: Given a binary tree, determine if it is a valid BST.
- **Interview walkthrough**: Provide min/max bounds recursively. For each node, ensure its value is within (min, max). Update bounds for left child as (min, node.val) and right child as (node.val, max). Use Long.MIN_VALUE/MAX_VALUE for bounds to handle Integer edge cases.
- **Solution approaches**: In-order traversal checking sorted order O(n). Recursive bounds O(n). Amazon prefers the recursive approach with bounds.
- **Java code**:
```java
/**
 * Solution for Validate Binary Search Tree.
 */
public class ValidateBST {

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int val) { this.val = val; }
    }

    /**
     * Checks if the given tree is a valid BST.
     *
     * @param root root of the tree
     * @return true if valid BST
     */
    public boolean isValidBST(TreeNode root) {
        return validate(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    private boolean validate(TreeNode node, long min, long max) {
        if (node == null) return true;
        if (node.val <= min || node.val >= max) return false;
        return validate(node.left, min, node.val)
                && validate(node.right, node.val, max);
    }
}
```
- **What Amazon evaluates**: Edge case handling (Integer.MIN_VALUE), understanding of BST definition, recursive reasoning.
- **Follow-ups**: Find the lowest common ancestor in BST. Serialize/deserialize BST.

### Category: Graphs
#### Problem: Word Ladder (LC 127)
- **Difficulty/Frequency**: Hard / High
- **Problem statement**: Given beginWord, endWord, and a dictionary, find the length of the shortest transformation sequence from beginWord to endWord where only one letter can be changed per step and each intermediate word must exist in the dictionary.
- **Interview walkthrough**: BFS is ideal because it finds shortest path in an unweighted graph. Model each word as a node, edges connect words differing by one character. Use pattern matching: create intermediate state keys (e.g., "h*t" for "hot"). BFS from beginWord until endWord is found.
- **Solution approaches**: BFS with pattern matching O(M^2 * N). Bidirectional BFS for optimization. Amazon expects BFS at minimum.
- **Java code**:
```java
import java.util.*;

/**
 * Solution for Word Ladder using BFS.
 */
public class WordLadder {

    /**
     * Returns length of shortest transformation sequence.
     *
     * @param beginWord starting word
     * @param endWord   target word
     * @param wordList  dictionary of allowed words
     * @return length of shortest path, 0 if none
     */
    public int ladderLength(String beginWord, String endWord,
                            List<String> wordList) {
        Set<String> dict = new HashSet<>(wordList);
        if (!dict.contains(endWord)) return 0;
        Queue<String> queue = new LinkedList<>();
        queue.offer(beginWord);
        int level = 1;
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                String curr = queue.poll();
                char[] chars = curr.toCharArray();
                for (int j = 0; j < chars.length; j++) {
                    char original = chars[j];
                    for (char c = 'a'; c <= 'z'; c++) {
                        if (c == original) continue;
                        chars[j] = c;
                        String next = new String(chars);
                        if (next.equals(endWord)) return level + 1;
                        if (dict.contains(next)) {
                            dict.remove(next);
                            queue.offer(next);
                        }
                    }
                    chars[j] = original;
                }
            }
            level++;
        }
        return 0;
    }
}
```
- **What Amazon evaluates**: Graph modeling where nodes = words, BFS level tracking, string manipulation efficiency, set usage for O(1) lookups.
- **Follow-ups**: Return all shortest transformation sequences. What if you can change multiple letters? Use bit-manipulation for DNA sequences.

### Category: Dynamic Programming
#### Problem: Maximum Subarray (LC 53)
- **Difficulty/Frequency**: Medium / Very High
- **Problem statement**: Given an integer array, find the contiguous subarray with the largest sum.
- **Interview walkthrough**: Kadane's algorithm. Maintain current sum and max sum. For each element, decide whether to start a new subarray or extend the current one. current = max(num, current + num).
- **Solution approaches**: Brute force O(n^3) or O(n^2). Divide and conquer O(n log n). Kadane's O(n). Amazon expects Kadane's.
- **Java code**:
```java
/**
 * Solution for Maximum Subarray using Kadane's Algorithm.
 */
public class MaximumSubarray {

    /**
     * Returns the largest sum of any contiguous subarray.
     *
     * @param nums input array
     * @return maximum subarray sum
     */
    public int maxSubArray(int[] nums) {
        int current = nums[0];
        int max = nums[0];
        for (int i = 1; i < nums.length; i++) {
            current = Math.max(nums[i], current + nums[i]);
            max = Math.max(max, current);
        }
        return max;
    }
}
```
- **What Amazon evaluates**: Kadane's algorithm knowledge, greedy DP thinking, ability to prove correctness.
- **Follow-ups**: Return the actual subarray. Maximum subarray in a circular array. Maximum product subarray.

#### Problem: Best Time to Buy and Sell Stock (LC 121)
- **Difficulty/Frequency**: Easy / Very High
- **Problem statement**: Given an array of stock prices where prices[i] is the price on day i, find the maximum profit from buying and selling one share (buy before sell).
- **Interview walkthrough**: Track minimum price seen so far and max profit. For each day, calculate profit if sold today, update min price.
- **Solution approaches**: Brute force O(n^2). Single pass O(n). Amazon expects the single pass solution.
- **Java code**:
```java
/**
 * Solution for Best Time to Buy and Sell Stock.
 */
public class BestTimeToBuyStock {

    /**
     * Returns maximum profit from one buy-sell transaction.
     *
     * @param prices array of daily stock prices
     * @return maximum profit possible
     */
    public int maxProfit(int[] prices) {
        int minPrice = Integer.MAX_VALUE;
        int maxProfit = 0;
        for (int price : prices) {
            if (price < minPrice) {
                minPrice = price;
            } else if (price - minPrice > maxProfit) {
                maxProfit = price - minPrice;
            }
        }
        return maxProfit;
    }
}
```
- **What Amazon evaluates**: Greedy optimization thinking, variable tracking strategy, clean O(n) approach.
- **Follow-ups**: Multiple transactions. With transaction fees. With cooldown. K transactions max.

### Category: Intervals
#### Problem: Merge Intervals (LC 56)
- **Difficulty/Frequency**: Medium / Very High
- **Problem statement**: Given an array of intervals, merge all overlapping intervals.
- **Interview walkthrough**: Sort intervals by start time. Iterate through sorted intervals, merge if current interval's start <= last merged interval's end.
- **Solution approaches**: Sorting + merge O(n log n). Amazon expects this approach and may ask for in-place merge.
- **Java code**:
```java
import java.util.*;

/**
 * Solution for Merge Intervals.
 */
public class MergeIntervals {

    /**
     * Merges all overlapping intervals.
     *
     * @param intervals array of intervals [start, end]
     * @return merged intervals
     */
    public int[][] merge(int[][] intervals) {
        if (intervals.length <= 1) return intervals;
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));
        List<int[]> merged = new ArrayList<>();
        int[] current = intervals[0];
        merged.add(current);
        for (int[] interval : intervals) {
            if (interval[0] <= current[1]) {
                current[1] = Math.max(current[1], interval[1]);
            } else {
                current = interval;
                merged.add(current);
            }
        }
        return merged.toArray(new int[merged.size()][]);
    }
}
```
- **What Amazon evaluates**: Sorting custom comparator, list to array conversion, greedy interval merging logic.
- **Follow-ups**: Insert interval. Non-overlapping intervals. Meeting rooms (minimum meeting rooms required).

## Company-Specific Algorithm Focus
Amazon emphasizes **arrays, strings, and hash maps** above all. They frequently test **DFS/BFS on trees and graphs**. Amazon also loves **sliding window** and **two-pointer** techniques. They rarely ask advanced math or number theory. Their problems tend to be more practical — they want solutions that could be applied to real Amazon systems like inventory management or product recommendations. They place high weight on **time complexity analysis** and **scalability discussion**.

## System Design with Algorithms
1. **Design Amazon's Product Recommendations** — Requires collaborative filtering algorithms (cosine similarity, matrix factorization), nearest neighbor search using k-d trees or locality-sensitive hashing, and map-reduce for large-scale processing.
2. **Design a Delivery Route Optimizer** (Last Mile) — Requires traveling salesman approximation (nearest neighbor, Christofides), Dijkstra for road segments, and dynamic programming for load balancing across delivery vans.

## Behavioral Questions (Using STAR)
1. **Tell me about a time you had to make a trade-off**: I had to choose between A* and Dijkstra for pathfinding in a logistics application. A* was faster but required an admissible heuristic. I built a profiler, tested on real data, and found A* reduced computation by 60%. I presented both options to my team with data, and we chose A* with fallback to Dijkstra.
2. **Tell me about a time you dived deep**: A sorting algorithm was performing poorly on production data. I profiled and found quicksort was hitting worst-case O(n^2) on nearly-sorted data. I implemented Timsort, which uses galloping mode for runs, fixing the performance regression.
3. **Tell me about a time you delivered results under pressure**: A core data pipeline needed to process 10x the normal volume during a flash sale. I redesigned the merging algorithm from O(n^2) to O(n log n) using a min-heap of sorted runs, reducing processing time from 4 hours to 20 minutes.
4. **Tell me about a time you invented and simplified**: I noticed redundant hash computations across services. I implemented a Bloom filter cache that reduced duplicate computations by 90%, with a tunable false positive rate. This required analyzing space-time trade-offs for different probability thresholds.
5. **Tell me about a time you were right but disagreed**: I argued against using a greedy algorithm for resource allocation because it lacked optimality for our specific cost function. I built a counterexample simulation, then implemented a DP solution that provided optimal allocation, saving $200K/month in compute costs.

## Study Plan
Prioritize these labs in order:
1. Lab 10: Array and String Algorithms
2. Lab 3: HashMaps and Sets
3. Lab 6: Tree Algorithms (BST, traversal)
4. Lab 7: Graph Algorithms I (BFS, DFS)
5. Lab 14: Sorting and Searching
6. Lab 4: Dynamic Programming I
7. Lab 12: Recursion and Backtracking

## Tips
- Amazon uses a **strict rubric** for hiring. You must score "highly proficient" in at least 3 of the 4 areas: algorithm, data structures, complexity, and coding.
- **Talk about trade-offs constantly**. Amazon interviewers want to hear you weigh time vs space, optimal vs practical.
- **Use the array/list — reuse input** to save space when possible. Amazon appreciates in-place modifications.
- The **bar raiser** round combines algorithms and leadership principles. Expect a harder algorithm problem combined with a behavioral question.
- Always ask clarifying questions about input size and constraints — Amazon explicitly evaluates this.
- **Handle edge cases** at the beginning of your code (null, empty input, single element). This is non-negotiable for Amazon.
- Practice **writing code on paper or whiteboard** without copy-paste. Amazon's coding platform is minimal.
