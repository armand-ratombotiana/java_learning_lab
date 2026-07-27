# Problem Walkthrough: 06-Hash-Tables

## Problem 1: Group Anagrams (LC 49) — Amazon

### Interview Scenario
"Amazon interviewer: 'Given an array of strings strs, group the anagrams together. An anagram is formed by rearranging the letters of a word.'"

### The Problem
Given an array of strings, return grouped lists of anagrams. Each group contains words that use the same characters with the same frequencies.

### Step 1: Clarify (30 seconds)
- **Q:** Are all strings lowercase? **A:** Yes, lowercase English letters.
- **Q:** Can there be empty strings? **A:** Yes, treat it as its own group.
- **Q:** What is the output format? **A:** List of lists of strings in any order.
- **Q:** String length constraints? **A:** Up to 100 chars, array length up to 10^4.
- **Edge cases:** Empty array, single element, all empty strings, no anagrams, all anagrams of each other.

### Step 2: Brute Force (2 min)
- For each pair of strings, check if they're anagrams by sorting both and comparing. Group matching ones.
- **Time:** O(n² * k log k) — each comparison is O(k log k), with n² comparisons. Impossible at scale.
- **Space:** O(n).

### Step 3: Optimize (5 min)
- "Use a HashMap where the key is a sorted version of the string. Strings that are anagrams have the same sorted key. Iterate through the array, add each string to the list for its sorted key."
- O(n * k log k) time (k = max string length) — n sort operations, not n² comparisons.
- Or use a frequency-count key (26-element array as a string) for O(n * k) time.
- **Why Amazon values this:** Grouping/lookup by a hashed key is fundamental. Amazon uses hashing for item categorization in product catalog (ASIN-based lookups).

### Step 4: Code (10 min)

```java
import java.util.*;

/**
 * Groups anagrams in an array of strings.
 * <p>
 * Time: O(n * k log k) | Space: O(n * k)
 */
public class Solution {
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

### Step 5: Test (3 min)
- **Example 1:** strs = ["eat","tea","tan","ate","nat","bat"] → [["bat"],["nat","tan"],["ate","eat","tea"]]
- **Edge:** strs = [""] → [[""]]
- **Edge:** strs = ["a"] → [["a"]]
- **Edge:** strs = ["", ""] → [["", ""]]
- Show the key computation for each word.

### Step 6: Follow-ups
- "What about Unicode strings?" — Use a canonical normalization form (NFD/NFC).
- "What about very long strings (k >> n)?" — Use frequency-key approach (O(n*k) instead of sorting).
- "What about anagrams at massive scale?" — MapReduce: mapper emits (sorted_key, word), reducer collects the group.
- **What Amazon looks for:** Scalability thinking. Can you extend this to a distributed system?

### Company Evaluation Criteria
- **Amazon:** Choosing the right key. Would you sort or use frequency counts? Justify your choice.
- **Google:** Would ask about the character frequency counting variant.
- **Meta:** Would ask about finding all anagrams in a string (sliding window + hashmap).

---

## Problem 2: Longest Substring Without Repeating Characters (LC 3) — Google

### Interview Scenario
"Google interviewer: 'Given a string s, find the length of the longest substring without repeating characters.'"

### The Problem
Find the longest contiguous block of characters where no character repeats.

### Step 1: Clarify (30 seconds)
- **Q:** Character set? **A:** ASCII (128 chars) or Unicode.
- **Q:** Empty string? **A:** Return 0.
- **Q:** Case sensitivity? **A:** 'A' and 'a' are different characters.
- **Edge cases:** All unique (return n), all same char (return 1), empty string, spaces and special characters.

### Step 2: Brute Force (2 min)
- Generate every possible substring, check for uniqueness. Three nested loops.
- **Time:** O(n³) — completely unacceptable.
- **Space:** O(min(n, m)) for the set.

### Step 3: Optimize (5 min)
- "Sliding window with a HashMap: expand the right pointer. If we encounter a repeating character, move the left pointer to the last known position of that character + 1 (or keep it where it is, whichever is larger). Track the max window size."
- O(n) time, O(min(n, m)) space where m is character set size.
- **Why Google values this:** Sliding window is a Google signature pattern. They use it for stream processing, logs analysis, and time-series data.

### Step 4: Code (10 min)

```java
import java.util.HashMap;
import java.util.Map;

/**
 * Finds the length of the longest substring without repeating characters.
 * <p>
 * Time: O(n) | Space: O(min(n, 128))
 */
public class Solution {
    public int lengthOfLongestSubstring(String s) {
        Map<Character, Integer> map = new HashMap<>();
        int maxLen = 0;
        int left = 0;

        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            if (map.containsKey(c)) {
                left = Math.max(left, map.get(c) + 1);
            }
            map.put(c, right);
            maxLen = Math.max(maxLen, right - left + 1);
        }

        return maxLen;
    }
}
```

### Step 5: Test (3 min)
- **Example 1:** s = "abcabcbb" → 3 ("abc")
- **Example 2:** s = "bbbbb" → 1 ("b")
- **Example 3:** s = "pwwkew" → 3 ("wke" or "kew")
- **Edge:** s = "" → 0
- **Edge:** s = " " → 1
- Walk through the sliding window at each step.

### Step 6: Follow-ups
- "What if it's a stream of characters?" — Same approach, O(1) per character, maintain window.
- "Longest substring with at most k distinct characters?" — Same sliding window, track distinct count in map (LC 340).
- "Longest substring with at least k repeating characters?" — Divide and conquer or sliding window with frequency (LC 395).
- **What Google looks for:** Can you reason about the two-pointer pattern? Do you understand why we use max when updating left?

### Company Evaluation Criteria
- **Google:** Problem-solving rigor. They probe the sliding window logic: "Why Math.max on left update? What if left is already past the last occurrence?"
- **Amazon:** Would ask about streaming and infinite data.
- **Meta:** Would ask about the brute force to optimized path and complexity improvements.

---

## Problem 3: LRU Cache (LC 146) — Meta

### Interview Scenario
"Meta interviewer: 'Design a data structure that follows the constraints of an LRU (Least Recently Used) cache. Get and put in O(1) average time.'"

### The Problem
Implement a cache with fixed capacity. get(key) returns value and marks it as recently used. put(key, value) inserts or updates, evicting the least recently used item when capacity is exceeded.

### Step 1: Clarify (30 seconds)
- **Q:** Can key be null? **A:** No, assume valid keys.
- **Q:** Thread safety? **A:** Not required for this design.
- **Q:** Capacity guaranteed to be positive? **A:** Yes, > 0.
- **Edge cases:** Capacity 1, get on missing key (return -1), update existing key (move to front).

### Step 2: Brute Force (2 min)
- Store items in a list with timestamps. On get, scan for key and update timestamp. On put, scan for key (update) or append (evict oldest if full).
- **Time:** O(n) for both operations — too slow.
- **Space:** O(n).

### Step 3: Optimize (5 min)
- "Use a doubly linked list (for O(1) removal/insertion) + a HashMap (for O(1) lookup). The list maintains access order: most recently used at head, LRU at tail. HashMap maps key to list node."
- get: if key exists, move node to head, return value.
- put: if key exists, update value and move to head. If not, create node at head. If over capacity, remove tail node.
- O(1) for both operations.
- **Why Meta values this:** This is the signature Meta system design question for data structures. It tests understanding of two data structure composition.

### Step 4: Code (10 min)

```java
import java.util.HashMap;
import java.util.Map;

/**
 * LRU cache using HashMap + doubly linked list.
 * <p>
 * Time: O(1) per operation | Space: O(capacity)
 */
public class LRUCache {
    private class Node {
        int key, value;
        Node prev, next;

        Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final Map<Integer, Node> cache;
    private final Node head, tail;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>();
        this.head = new Node(0, 0);
        this.tail = new Node(0, 0);
        head.next = tail;
        tail.prev = head;
    }

    public int get(int key) {
        Node node = cache.get(key);
        if (node == null) return -1;
        moveToHead(node);
        return node.value;
    }

    public void put(int key, int value) {
        Node node = cache.get(key);
        if (node != null) {
            node.value = value;
            moveToHead(node);
        } else {
            node = new Node(key, value);
            cache.put(key, node);
            addToHead(node);
            if (cache.size() > capacity) {
                Node lru = tail.prev;
                removeNode(lru);
                cache.remove(lru.key);
            }
        }
    }

    private void moveToHead(Node node) {
        removeNode(node);
        addToHead(node);
    }

    private void addToHead(Node node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }
}
```

### Step 5: Test (3 min)
- capacity = 2, put(1,1), put(2,2), get(1) → 1, put(3,3) evicts key 2, get(2) → -1
- capacity = 1, put(1,1), put(2,2) → get(1) → -1, get(2) → 2
- Update existing: put(1,1), put(1,2) → get(1) → 2
- Walk through the doubly linked list links.

### Step 6: Follow-ups
- "Can you implement using Java's LinkedHashMap?" — Yes, 5 lines with accessOrder=true.
- "What about thread safety?" — Use synchronized blocks or ReadWriteLock.
- "What about LFU instead of LRU?" — More complex, need frequency tracking (LC 460).
- "What about expiration-based eviction?" — Add TTL field, background cleanup thread.
- **What Meta looks for:** Can you write pointer manipulation correctly? Doubly linked list bugs are the #1 failure point.

### Company Evaluation Criteria
- **Meta:** Pointer correctness in the linked list. Clean code structure (inner class, helper methods).
- **Google:** Would ask about concurrent LRU design.
- **Amazon:** Would ask about distributed cache (Redis-like) with LRU.

---

## Study Notes

### Key Patterns
- **Hash key grouping:** Group anagrams (sorted key), frequency counting
- **Sliding window + hashmap:** Substring with no repeats, substring with k distinct chars
- **Hash + linked list:** LRU cache, insertion order preservation
- **Two-sum pattern:** Complement lookup in a hashmap
- **Frequency counting:** Character occurrence, anagram detection, ransom note

### Common Mistakes
- Using `int[]` of size 26 for counting but forgetting about Unicode
- Moving left pointer incorrectly in sliding window (not using max)
- Forgetting to update both prev and next pointers in doubly linked list
- Null pointer on map.get when key doesn't exist
- Not considering hash collisions in interview discussions

### Time Complexity Cheat Sheet
| Pattern | Time | Space |
|---|---|---|
| HashMap grouping | O(n * k log k) | O(n * k) |
| Freq count grouping | O(n * k) | O(n * k) |
| Sliding window | O(n) | O(min(n, m)) |
| LRU cache | O(1) per op | O(capacity) |
| HashMap basic ops | O(1) avg | O(n) |
