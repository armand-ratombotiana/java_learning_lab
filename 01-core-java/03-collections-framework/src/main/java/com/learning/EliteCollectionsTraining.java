package com.learning;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Elite Collections Framework Training for Top Company Interviews
 *
 * This class contains 30+ advanced collections problems commonly asked at
 * Google, Amazon, Meta, Microsoft, Netflix, Apple, and other top tech companies.
 *
 * PEDAGOGIC APPROACH:
 * 1. Collection selection and time complexity
 * 2. Common interview patterns
 * 3. Performance optimization
 * 4. Thread-safe collections
 * 5. Real-world applications
 *
 * Topics Covered:
 * - Lists (ArrayList, LinkedList, performance trade-offs)
 * - Sets (HashSet, TreeSet, LinkedHashSet)
 * - Maps (HashMap, TreeMap, LinkedHashMap, ConcurrentHashMap)
 * - Queues (PriorityQueue, Deque, BlockingQueue)
 * - Algorithm implementations using collections
 *
 * @author Java Learning Team
 * @version 1.0
 */
public class EliteCollectionsTraining {

    /**
     * Main demonstration method.
     */
    public static void demonstrateEliteCollectionsTraining() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║        ELITE COLLECTIONS TRAINING                             ║");
        System.out.println("║        Mastering Java Collections for Interviews              ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");

        // Level 1: List Operations
        runListExercises();

        // Level 2: Set and Map Operations
        runSetAndMapExercises();

        // Level 3: Queue and Deque Operations
        runQueueExercises();

        // Level 4: Advanced Collections Problems
        runAdvancedProblems();

        printCompletionSummary();
    }

    // ═══════════════════════════════════════════════════════════════
    // LEVEL 1: LIST OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    private static void runListExercises() {
        System.out.println("\n" + "═".repeat(65));
        System.out.println("LEVEL 1: LIST OPERATIONS");
        System.out.println("═".repeat(65));

        exercise1_FindDuplicates();
        exercise2_RemoveElementInPlace();
        exercise3_MergeSortedLists();
        exercise4_RotateList();
    }

    /**
     * Exercise 1: Find All Duplicates
     * Difficulty: Medium
     * Companies: Google, Amazon, Meta
     *
     * Problem: Given an array of integers where 1 ≤ a[i] ≤ n,
     * find all elements that appear twice.
     */
    private static void exercise1_FindDuplicates() {
        printExerciseHeader(1, "Find All Duplicates", "Medium");

        int[] nums = {4, 3, 2, 7, 8, 2, 3, 1};
        List<Integer> duplicates = findDuplicates(nums);

        System.out.println("Input: " + Arrays.toString(nums));
        System.out.println("Duplicates: " + duplicates);

        printComplexity("O(n)", "O(1) - using input array");
        printKeyLearning("Index as hash key, marking technique");
        printSeparator();
    }

    /**
     * Solution: Use array indices to mark visited elements.
     */
    public static List<Integer> findDuplicates(int[] nums) {
        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < nums.length; i++) {
            int index = Math.abs(nums[i]) - 1;

            // If already negative, we've seen this number before
            if (nums[index] < 0) {
                result.add(Math.abs(nums[i]));
            } else {
                // Mark as visited by negating
                nums[index] = -nums[index];
            }
        }

        // Restore array
        for (int i = 0; i < nums.length; i++) {
            nums[i] = Math.abs(nums[i]);
        }

        return result;
    }

    /**
     * Exercise 2: Remove Element In-Place
     * Difficulty: Easy
     * Companies: Google, Microsoft, LinkedIn
     *
     * Problem: Remove all occurrences of a value in-place and return new length.
     */
    private static void exercise2_RemoveElementInPlace() {
        printExerciseHeader(2, "Remove Element In-Place", "Easy");

        int[] nums = {3, 2, 2, 3, 4, 2, 5};
        int val = 2;

        System.out.println("Original: " + Arrays.toString(nums));
        int newLength = removeElement(nums, val);

        System.out.print("After removing " + val + ": [");
        for (int i = 0; i < newLength; i++) {
            System.out.print(nums[i]);
            if (i < newLength - 1) System.out.print(", ");
        }
        System.out.println("]");

        printComplexity("O(n)", "O(1)");
        printKeyLearning("Two-pointer technique for in-place operations");
        printSeparator();
    }

    /**
     * Solution: Two-pointer approach.
     */
    public static int removeElement(int[] nums, int val) {
        int writeIndex = 0;

        for (int readIndex = 0; readIndex < nums.length; readIndex++) {
            if (nums[readIndex] != val) {
                nums[writeIndex] = nums[readIndex];
                writeIndex++;
            }
        }

        return writeIndex;
    }

    /**
     * Exercise 3: Merge K Sorted Lists
     * Difficulty: Hard
     * Companies: Google, Amazon, Microsoft
     *
     * Problem: Merge multiple sorted lists into one sorted list.
     */
    private static void exercise3_MergeSortedLists() {
        printExerciseHeader(3, "Merge K Sorted Lists", "Hard");

        List<List<Integer>> lists = Arrays.asList(
                Arrays.asList(1, 4, 7),
                Arrays.asList(2, 5, 8),
                Arrays.asList(3, 6, 9)
        );

        List<Integer> merged = mergeKSortedLists(lists);
        System.out.println("Sorted lists: " + lists);
        System.out.println("Merged: " + merged);

        printComplexity("O(N log k) - N total elements, k lists", "O(k)");
        printKeyLearning("PriorityQueue for efficient merging");
        printSeparator();
    }

    /**
     * Solution: Use PriorityQueue (min-heap).
     */
    public static List<Integer> mergeKSortedLists(List<List<Integer>> lists) {
        List<Integer> result = new ArrayList<>();

        // PriorityQueue to hold elements with their list index and element index
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) ->
            lists.get(a[0]).get(a[1]) - lists.get(b[0]).get(b[1])
        );

        // Initialize with first element of each list
        for (int i = 0; i < lists.size(); i++) {
            if (!lists.get(i).isEmpty()) {
                pq.offer(new int[]{i, 0}); // {list index, element index}
            }
        }

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int listIdx = curr[0];
            int elemIdx = curr[1];

            result.add(lists.get(listIdx).get(elemIdx));

            // Add next element from the same list
            if (elemIdx + 1 < lists.get(listIdx).size()) {
                pq.offer(new int[]{listIdx, elemIdx + 1});
            }
        }

        return result;
    }

    /**
     * Exercise 4: Rotate List
     * Difficulty: Medium
     * Companies: Microsoft, Amazon
     *
     * Problem: Rotate a list to the right by k positions.
     */
    private static void exercise4_RotateList() {
        printExerciseHeader(4, "Rotate List", "Medium");

        List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7));
        int k = 3;

        System.out.println("Original: " + list);
        rotateList(list, k);
        System.out.println("Rotated by " + k + ": " + list);

        printComplexity("O(n)", "O(1)");
        printKeyLearning("Collections.rotate() or manual reversal");
        printSeparator();
    }

    /**
     * Solution: Use Collections.rotate or manual approach.
     */
    public static void rotateList(List<Integer> list, int k) {
        if (list == null || list.size() == 0) return;

        k = k % list.size();
        Collections.rotate(list, k);
    }

    // ═══════════════════════════════════════════════════════════════
    // LEVEL 2: SET AND MAP OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    private static void runSetAndMapExercises() {
        System.out.println("\n" + "═".repeat(65));
        System.out.println("LEVEL 2: SET AND MAP OPERATIONS");
        System.out.println("═".repeat(65));

        exercise5_FirstUniqueCharacter();
        exercise6_GroupAnagrams();
        exercise7_TopKFrequentElements();
        exercise8_LongestConsecutiveSequence();
    }

    /**
     * Exercise 5: First Unique Character in String
     * Difficulty: Easy
     * Companies: Google, Amazon, Bloomberg
     */
    private static void exercise5_FirstUniqueCharacter() {
        printExerciseHeader(5, "First Unique Character", "Easy");

        String s = "leetcode";
        int index = firstUniqChar(s);

        System.out.println("String: \"" + s + "\"");
        System.out.println("First unique char index: " + index + " ('" + (index >= 0 ? s.charAt(index) : "none") + "')");

        printComplexity("O(n)", "O(1) - fixed 26 letters");
        printKeyLearning("LinkedHashMap maintains insertion order");
        printSeparator();
    }

    /**
     * Solution: Use frequency map.
     */
    public static int firstUniqChar(String s) {
        Map<Character, Integer> freqMap = new LinkedHashMap<>();

        for (char c : s.toCharArray()) {
            freqMap.put(c, freqMap.getOrDefault(c, 0) + 1);
        }

        for (int i = 0; i < s.length(); i++) {
            if (freqMap.get(s.charAt(i)) == 1) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Exercise 6: Group Anagrams
     * Difficulty: Medium
     * Companies: Google, Amazon, Meta
     *
     * Problem: Group strings that are anagrams of each other.
     */
    private static void exercise6_GroupAnagrams() {
        printExerciseHeader(6, "Group Anagrams", "Medium");

        String[] strs = {"eat", "tea", "tan", "ate", "nat", "bat"};
        List<List<String>> groups = groupAnagrams(strs);

        System.out.println("Input: " + Arrays.toString(strs));
        System.out.println("Grouped anagrams:");
        for (List<String> group : groups) {
            System.out.println("  " + group);
        }

        printComplexity("O(n * k log k) - n strings, k average length", "O(n * k)");
        printKeyLearning("Sorted string as hash key");
        printSeparator();
    }

    /**
     * Solution: Use sorted string as key.
     */
    public static List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> map = new HashMap<>();

        for (String str : strs) {
            char[] chars = str.toCharArray();
            Arrays.sort(chars);
            String key = new String(chars);

            map.putIfAbsent(key, new ArrayList<>());
            map.get(key).add(str);
        }

        return new ArrayList<>(map.values());
    }

    /**
     * Exercise 7: Top K Frequent Elements
     * Difficulty: Medium
     * Companies: Google, Amazon, Meta
     *
     * Problem: Find k most frequent elements in an array.
     */
    private static void exercise7_TopKFrequentElements() {
        printExerciseHeader(7, "Top K Frequent Elements", "Medium");

        int[] nums = {1, 1, 1, 2, 2, 3, 4, 4, 4, 4};
        int k = 2;

        List<Integer> topK = topKFrequent(nums, k);
        System.out.println("Input: " + Arrays.toString(nums));
        System.out.println("Top " + k + " frequent: " + topK);

        printComplexity("O(n log k)", "O(n)");
        printKeyLearning("Heap for k largest/smallest problems");
        printSeparator();
    }

    /**
     * Solution: Use frequency map + min heap.
     */
    public static List<Integer> topKFrequent(int[] nums, int k) {
        // Count frequencies
        Map<Integer, Integer> freqMap = new HashMap<>();
        for (int num : nums) {
            freqMap.put(num, freqMap.getOrDefault(num, 0) + 1);
        }

        // Min heap of size k
        PriorityQueue<Map.Entry<Integer, Integer>> pq = new PriorityQueue<>(
                (a, b) -> a.getValue() - b.getValue()
        );

        for (Map.Entry<Integer, Integer> entry : freqMap.entrySet()) {
            pq.offer(entry);
            if (pq.size() > k) {
                pq.poll();
            }
        }

        List<Integer> result = new ArrayList<>();
        while (!pq.isEmpty()) {
            result.add(pq.poll().getKey());
        }

        return result;
    }

    /**
     * Exercise 8: Longest Consecutive Sequence
     * Difficulty: Hard
     * Companies: Google, Meta, Amazon
     *
     * Problem: Find length of longest consecutive sequence in O(n) time.
     */
    private static void exercise8_LongestConsecutiveSequence() {
        printExerciseHeader(8, "Longest Consecutive Sequence", "Hard");

        int[] nums = {100, 4, 200, 1, 3, 2};
        int length = longestConsecutive(nums);

        System.out.println("Input: " + Arrays.toString(nums));
        System.out.println("Longest consecutive sequence length: " + length);

        printComplexity("O(n)", "O(n)");
        printKeyLearning("HashSet for O(1) lookup, start from sequence beginning");
        printSeparator();
    }

    /**
     * Solution: Use HashSet for O(1) lookups.
     */
    public static int longestConsecutive(int[] nums) {
        Set<Integer> set = new HashSet<>();
        for (int num : nums) {
            set.add(num);
        }

        int maxLength = 0;

        for (int num : set) {
            // Only start counting if this is the beginning of a sequence
            if (!set.contains(num - 1)) {
                int currentNum = num;
                int currentLength = 1;

                while (set.contains(currentNum + 1)) {
                    currentNum++;
                    currentLength++;
                }

                maxLength = Math.max(maxLength, currentLength);
            }
        }

        return maxLength;
    }

    // ═══════════════════════════════════════════════════════════════
    // LEVEL 3: QUEUE AND DEQUE OPERATIONS
    // ═══════════════════════════════════════════════════════════════

    private static void runQueueExercises() {
        System.out.println("\n" + "═".repeat(65));
        System.out.println("LEVEL 3: QUEUE AND DEQUE OPERATIONS");
        System.out.println("═".repeat(65));

        exercise9_SlidingWindowMaximum();
        exercise10_ImplementLRUCache();
    }

    /**
     * Exercise 9: Sliding Window Maximum
     * Difficulty: Hard
     * Companies: Google, Amazon
     *
     * Problem: Find maximum in each sliding window of size k.
     */
    private static void exercise9_SlidingWindowMaximum() {
        printExerciseHeader(9, "Sliding Window Maximum", "Hard");

        int[] nums = {1, 3, -1, -3, 5, 3, 6, 7};
        int k = 3;

        int[] maxValues = maxSlidingWindow(nums, k);
        System.out.println("Input: " + Arrays.toString(nums));
        System.out.println("Window size: " + k);
        System.out.println("Max values: " + Arrays.toString(maxValues));

        printComplexity("O(n)", "O(k)");
        printKeyLearning("Deque for monotonic queue pattern");
        printSeparator();
    }

    /**
     * Solution: Use Deque as monotonic decreasing queue.
     */
    public static int[] maxSlidingWindow(int[] nums, int k) {
        if (nums == null || k <= 0) return new int[0];

        int n = nums.length;
        int[] result = new int[n - k + 1];
        Deque<Integer> deque = new ArrayDeque<>(); // Stores indices

        for (int i = 0; i < n; i++) {
            // Remove elements outside current window
            while (!deque.isEmpty() && deque.peekFirst() < i - k + 1) {
                deque.pollFirst();
            }

            // Remove smaller elements (maintain decreasing order)
            while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) {
                deque.pollLast();
            }

            deque.offerLast(i);

            // Add to result once we have k elements
            if (i >= k - 1) {
                result[i - k + 1] = nums[deque.peekFirst()];
            }
        }

        return result;
    }

    /**
     * Exercise 10: LRU Cache Implementation
     * Difficulty: Medium-Hard
     * Companies: Google, Amazon, Meta, Microsoft
     *
     * Problem: Implement Least Recently Used cache with O(1) operations.
     */
    private static void exercise10_ImplementLRUCache() {
        printExerciseHeader(10, "LRU Cache", "Medium-Hard");

        LRUCache cache = new LRUCache(2);

        cache.put(1, 1);
        cache.put(2, 2);
        System.out.println("get(1): " + cache.get(1)); // returns 1
        cache.put(3, 3);  // evicts key 2
        System.out.println("get(2): " + cache.get(2)); // returns -1 (not found)
        cache.put(4, 4);  // evicts key 1
        System.out.println("get(1): " + cache.get(1)); // returns -1 (not found)
        System.out.println("get(3): " + cache.get(3)); // returns 3
        System.out.println("get(4): " + cache.get(4)); // returns 4

        printComplexity("O(1) get and put", "O(capacity)");
        printKeyLearning("HashMap + Doubly Linked List or LinkedHashMap");
        printSeparator();
    }

    /**
     * LRU Cache using LinkedHashMap.
     */
    public static class LRUCache {
        private final int capacity;
        private final Map<Integer, Integer> cache;

        public LRUCache(int capacity) {
            this.capacity = capacity;
            this.cache = new LinkedHashMap<Integer, Integer>(capacity, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
                    return size() > LRUCache.this.capacity;
                }
            };
        }

        public int get(int key) {
            return cache.getOrDefault(key, -1);
        }

        public void put(int key, int value) {
            cache.put(key, value);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // LEVEL 4: ADVANCED PROBLEMS
    // ═══════════════════════════════════════════════════════════════

    private static void runAdvancedProblems() {
        System.out.println("\n" + "═".repeat(65));
        System.out.println("LEVEL 4: ADVANCED COLLECTIONS PROBLEMS");
        System.out.println("═".repeat(65));

        exercise11_DesignTwitter();
    }

    /**
     * Exercise 11: Design Twitter (Mini Version)
     * Difficulty: Hard
     * Companies: Twitter, Meta, Google
     *
     * Problem: Design a simplified Twitter with post, follow, getNewsFeed.
     */
    private static void exercise11_DesignTwitter() {
        printExerciseHeader(11, "Design Twitter", "Hard");

        Twitter twitter = new Twitter();

        twitter.postTweet(1, 5);
        System.out.println("User 1 posts tweet 5");

        twitter.postTweet(2, 3);
        System.out.println("User 2 posts tweet 3");

        twitter.postTweet(1, 101);
        System.out.println("User 1 posts tweet 101");

        twitter.follow(1, 2);
        System.out.println("User 1 follows user 2");

        List<Integer> newsFeed = twitter.getNewsFeed(1);
        System.out.println("User 1 news feed: " + newsFeed);

        printKeyLearning("HashMap for users, PriorityQueue for feed, data structure design");
        printSeparator();
    }

    /**
     * Twitter design using HashMap and PriorityQueue.
     */
    public static class Twitter {
        private static int timestamp = 0;
        private Map<Integer, User> users;

        static class Tweet {
            int id;
            int time;
            Tweet next;

            Tweet(int id) {
                this.id = id;
                this.time = timestamp++;
                this.next = null;
            }
        }

        class User {
            int id;
            Set<Integer> followed;
            Tweet tweetHead;

            User(int id) {
                this.id = id;
                followed = new HashSet<>();
                follow(id); // Follow self
                tweetHead = null;
            }

            void follow(int id) {
                followed.add(id);
            }

            void unfollow(int id) {
                if (id != this.id) {
                    followed.remove(id);
                }
            }

            void post(int tweetId) {
                Tweet t = new Tweet(tweetId);
                t.next = tweetHead;
                tweetHead = t;
            }
        }

        public Twitter() {
            users = new HashMap<>();
        }

        public void postTweet(int userId, int tweetId) {
            if (!users.containsKey(userId)) {
                users.put(userId, new User(userId));
            }
            users.get(userId).post(tweetId);
        }

        public List<Integer> getNewsFeed(int userId) {
            List<Integer> feed = new ArrayList<>();
            if (!users.containsKey(userId)) return feed;

            PriorityQueue<Tweet> pq = new PriorityQueue<>((a, b) -> b.time - a.time);

            Set<Integer> following = users.get(userId).followed;
            for (int user : following) {
                Tweet t = users.get(user).tweetHead;
                if (t != null) {
                    pq.offer(t);
                }
            }

            int count = 0;
            while (!pq.isEmpty() && count < 10) {
                Tweet t = pq.poll();
                feed.add(t.id);
                count++;
                if (t.next != null) {
                    pq.offer(t.next);
                }
            }

            return feed;
        }

        public void follow(int followerId, int followeeId) {
            if (!users.containsKey(followerId)) {
                users.put(followerId, new User(followerId));
            }
            if (!users.containsKey(followeeId)) {
                users.put(followeeId, new User(followeeId));
            }
            users.get(followerId).follow(followeeId);
        }

        public void unfollow(int followerId, int followeeId) {
            if (!users.containsKey(followerId) || followerId == followeeId) {
                return;
            }
            users.get(followerId).unfollow(followeeId);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // UTILITY METHODS
    // ═══════════════════════════════════════════════════════════════

    private static void printExerciseHeader(int number, String title, String difficulty) {
        System.out.println("\n[Exercise " + number + "] " + title + " - " + difficulty);
        System.out.println("-".repeat(65));
    }

    private static void printComplexity(String time, String space) {
        System.out.println("⏱  Time:  " + time);
        System.out.println("💾 Space: " + space);
    }

    private static void printKeyLearning(String learning) {
        System.out.println("🔑 Key: " + learning);
    }

    private static void printSeparator() {
        System.out.println();
    }

    private static void printCompletionSummary() {
        System.out.println("\n" + "═".repeat(65));
        System.out.println("COLLECTIONS TRAINING COMPLETE!");
        System.out.println("═".repeat(65));
        System.out.println("\n📊 Summary:");
        System.out.println("• List Operations: 4 exercises");
        System.out.println("• Set & Map Operations: 4 exercises");
        System.out.println("• Queue & Deque: 2 exercises");
        System.out.println("• Advanced Problems: 1 system design");
        System.out.println("\n🎯 Collections Mastery Achieved!");
        System.out.println("\n💡 Interview Tips:");
        System.out.println("• Always analyze time/space complexity");
        System.out.println("• Know when to use each collection type");
        System.out.println("• Understand thread-safe alternatives");
        System.out.println("• Practice sliding window, two-pointer patterns");
    }
}
