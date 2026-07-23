# Netflix Interview Guide — Data Structures Academy

## Interview Process for DS Roles
- **Rounds**: Recruiter screen → 1-2 technical phone screens → 4-5 on-site rounds (3-4 coding/system design, 1 behavioral/culture fit).
- **Timeline**: Phone screens → on-site within 2-3 weeks. Decision in 3-5 days.
- **Format**: CoderPad (remote) or whiteboard (in-person). Some teams use shared screen with IntelliJ.
- **Focus**: System-level thinking and performance at scale. Netflix handles massive traffic and expects you to design with that in mind.
- **Coding Environment**: CoderPad for remote. Some teams allow choice of IDE.
- **Culture**: "Freedom and Responsibility." They expect you to make independent decisions and justify them.

## Top Problems by Lab

### Lab 01-arrays
#### Problem: Product of Array Except Self (LC 238)
- **Difficulty**: Medium | **Frequency**: Very High
- **Statement**: Compute product of all elements except self without division, in O(n).
- **How the interview goes**: Netflix tests prefix/suffix technique. Must discuss why division is not allowed.
- **Approach 1**: Two Pass (Left/Right) — O(n) time, O(1) extra space. Prefix then suffix.
- **Approach 2**: One Pass with Memory — O(n) time, O(n) space. Two auxiliary arrays.
- **Java**:
```java
/**
 * Product except self using two passes.
 * Time: O(n), Space: O(1) extra
 */
public int[] productExceptSelf(int[] nums) {
    int n = nums.length;
    int[] result = new int[n];
    result[0] = 1;
    for (int i = 1; i < n; i++) result[i] = result[i - 1] * nums[i - 1];
    int right = 1;
    for (int i = n - 1; i >= 0; i--) {
        result[i] *= right;
        right *= nums[i];
    }
    return result;
}
```
- **What Netflix evaluates**: O(1) space constraint, understanding of cumulative product, division-free requirement.
- **Follow-up**: Large integer overflow handling (use BigInteger). Streaming input.

#### Problem: Container With Most Water (LC 11)
- **Difficulty**: Medium | **Frequency**: High
- **Statement**: Find two lines that form a container holding the most water.
- **How the interview goes**: Netflix tests two-pointer optimization. Must derive why it works.
- **Approach 1**: Two-Pointer — O(n) time, O(1) space. Move the pointer with shorter height.
- **Approach 2**: Brute Force — O(n²) time, O(1) space. Used only as starting discussion point.
- **Java**:
```java
/**
 * Container with most water using two pointers.
 * Time: O(n), Space: O(1)
 */
public int maxArea(int[] height) {
    int max = 0, lo = 0, hi = height.length - 1;
    while (lo < hi) {
        int area = Math.min(height[lo], height[hi]) * (hi - lo);
        max = Math.max(max, area);
        if (height[lo] < height[hi]) lo++; else hi--;
    }
    return max;
}
```
- **What Netflix evaluates**: Two-pointer correctness, greedy choice rationale, area computation.
- **Follow-up**: Trapping rain water (LC 42). Largest rectangle in histogram (LC 84).

### Lab 08-hashing
#### Problem: LRU Cache (LC 146)
- **Difficulty**: Medium | **Frequency**: Very High
- **Statement**: Design an LRU cache with O(1) get and put operations.
- **How the interview goes**: Netflix tests design of HashMap + Doubly Linked List combo.
- **Approach 1**: LinkedHashMap — O(1) average. Java built-in, best for production.
- **Approach 2**: HashMap + Custom DLL — O(1) average. Tests understanding of both DS.
- **Java**:
```java
/**
 * LRU cache using LinkedHashMap.
 * Time: O(1), Space: O(capacity)
 */
class LRUCache {
    private final Map<Integer, Integer> map;
    private final int capacity;
    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new LinkedHashMap<>(capacity, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
                return size() > capacity;
            }
        };
    }
    public int get(int key) { return map.getOrDefault(key, -1); }
    public void put(int key, int value) { map.put(key, value); }
}
```
- **What Netflix evaluates**: O(1) constraint, understanding of access ordering, production-quality design.
- **Follow-up**: Implement from scratch with Node class. Thread-safe version. LFU cache (LC 460).

#### Problem: First Unique Character in a String (LC 387)
- **Difficulty**: Easy | **Frequency**: Medium
- **Statement**: Find first non-repeating character in a string.
- **How the interview goes**: Netflix tests frequency counting + order maintenance.
- **Approach 1**: Two-Pass Frequency Array — O(n) time, O(1) space. Count then scan.
- **Approach 2**: One-Pass with LinkedHashMap — O(n) time, O(k) space. Maintain insertion order.
- **Java**:
```java
/**
 * First unique character using frequency array.
 * Time: O(n), Space: O(1)
 */
public int firstUniqChar(String s) {
    int[] freq = new int[26];
    for (char c : s.toCharArray()) freq[c - 'a']++;
    for (int i = 0; i < s.length(); i++) {
        if (freq[s.charAt(i) - 'a'] == 1) return i;
    }
    return -1;
}
```
- **What Netflix evaluates**: Two-pass efficiency, character set handling, O(1) space claim justification.
- **Follow-up**: Stream of characters. First non-repeating in a stream using Queue + HashMap.

### Lab 09-heaps
#### Problem: Find Median from Data Stream (LC 295)
- **Difficulty**: Hard | **Frequency**: Very High
- **Statement**: Design a data structure that supports adding numbers and finding the median.
- **How the interview goes**: Netflix tests two-heap approach (max-heap for lower half, min-heap for upper half).
- **Approach 1**: Two Heaps — O(log n) add, O(1) find. Max-heap (lower) + Min-heap (upper).
- **Approach 2**: Balanced BST — O(log n) operations, O(n) space. More complex but equally valid.
- **Java**:
```java
/**
 * Find median from data stream using two heaps.
 * Time: O(log n) add, O(1) find, Space: O(n)
 */
class MedianFinder {
    private PriorityQueue<Integer> lower = new PriorityQueue<>(Collections.reverseOrder());
    private PriorityQueue<Integer> upper = new PriorityQueue<>();
    public void addNum(int num) {
        lower.offer(num);
        upper.offer(lower.poll());
        if (upper.size() > lower.size()) lower.offer(upper.poll());
    }
    public double findMedian() {
        if (lower.size() == upper.size()) return (lower.peek() + upper.peek()) / 2.0;
        return lower.peek();
    }
}
```
- **What Netflix evaluates**: Heap balancing logic, priority inversion, floating point precision.
- **Follow-up**: Sliding window median (LC 480). Stream of sorted numbers (two-pointer).

#### Problem: K Closest Points to Origin (LC 973)
- **Difficulty**: Medium | **Frequency**: High
- **Statement**: Find K closest points to origin in 2D plane.
- **How the interview goes**: Netflix tests heap vs quickselect trade-off. Discuss distance calculation.
- **Approach 1**: Max-Heap — O(n log k) time, O(k) space. Keep K closest.
- **Approach 2**: Quickselect — O(n) average time, O(1) extra space. Partition-based selection.
- **Java**:
```java
/**
 * K closest points using max-heap.
 * Time: O(n log k), Space: O(k)
 */
public int[][] kClosest(int[][] points, int k) {
    PriorityQueue<int[]> heap = new PriorityQueue<>(
        (a, b) -> Integer.compare(b[0] * b[0] + b[1] * b[1], a[0] * a[0] + a[1] * a[1])
    );
    for (int[] p : points) {
        heap.offer(p);
        if (heap.size() > k) heap.poll();
    }
    int[][] result = new int[k][2];
    for (int i = 0; i < k; i++) result[i] = heap.poll();
    return result;
}
```
- **What Netflix evaluates**: Max-heap for top K, distance comparison, comparator writing.
- **Follow-up**: Large K (use quickselect). 3D points. Nearest neighbor in dynamic set.

### Lab 17-trie
#### Problem: Palindrome Pairs (LC 336)
- **Difficulty**: Hard | **Frequency**: Medium
- **Statement**: Find all pairs of words that concatenate to form a palindrome.
- **How the interview goes**: Netflix tests combined use of Trie + palindrome checking. Complex optimization.
- **Approach 1**: Trie + Reverse Lookup — O(n·k²) time, O(n·k) space. Store words in trie, check reverse.
- **Approach 2**: HashMap — O(n·k²) time, O(n·k) space. Simpler, no trie needed.
- **Java**:
```java
/**
 * Palindrome pairs using hash map (simplified approach).
 * Time: O(n * k^2), Space: O(n * k)
 */
public List<List<Integer>> palindromePairs(String[] words) {
    Map<String, Integer> map = new HashMap<>();
    for (int i = 0; i < words.length; i++) map.put(words[i], i);
    List<List<Integer>> result = new ArrayList<>();
    for (int i = 0; i < words.length; i++) {
        String w = words[i];
        for (int j = 0; j <= w.length(); j++) {
            String prefix = w.substring(0, j), suffix = w.substring(j);
            if (isPalindrome(prefix)) {
                String rev = new StringBuilder(suffix).reverse().toString();
                Integer idx = map.get(rev);
                if (idx != null && idx != i) result.add(Arrays.asList(idx, i));
            }
            if (!suffix.isEmpty() && isPalindrome(suffix)) {
                String rev = new StringBuilder(prefix).reverse().toString();
                Integer idx = map.get(rev);
                if (idx != null && idx != i) result.add(Arrays.asList(i, idx));
            }
        }
    }
    return result;
}
private boolean isPalindrome(String s) {
    int lo = 0, hi = s.length() - 1;
    while (lo < hi) if (s.charAt(lo++) != s.charAt(hi--)) return false;
    return true;
}
```
- **What Netflix evaluates**: Palindrome checking, substring splitting logic, avoiding double counting.
- **Follow-up**: Optimize with Trie to avoid O(k²) per word. Find longest palindrome prefix.

#### Problem: Design Search Autocomplete System (LC 642)
- **Difficulty**: Hard | **Frequency**: Medium
- **Statement**: Design autocomplete returning top 3 hot sentences given a current prefix.
- **How the interview goes**: Netflix tests Trie + Heap combination. Must handle character-by-character input.
- **Approach 1**: Trie with HashMap at each node — O(L + n log k) per search, O(total characters) space.
- **Approach 2**: HashMap + Sorting — Simpler but slower for large datasets.
- **Java**:
```java
/**
 * Autocomplete system (simplified Trie + sorting approach).
 * Time: O(L + n log n) per search, Space: O(total sentences)
 */
class AutocompleteSystem {
    class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        Map<String, Integer> counts = new HashMap<>();
    }
    private TrieNode root = new TrieNode();
    private String prefix = "";
    public AutocompleteSystem(String[] sentences, int[] times) {
        for (int i = 0; i < sentences.length; i++) add(sentences[i], times[i]);
    }
    private void add(String s, int count) {
        TrieNode node = root;
        for (char c : s.toCharArray()) {
            node = node.children.computeIfAbsent(c, k -> new TrieNode());
            node.counts.merge(s, count, Integer::sum);
        }
    }
    public List<String> input(char c) {
        if (c == '#') { add(prefix, 1); prefix = ""; return List.of(); }
        prefix += c;
        TrieNode node = root;
        for (char ch : prefix.toCharArray()) {
            if (!node.children.containsKey(ch)) return List.of();
            node = node.children.get(ch);
        }
        return node.counts.entrySet().stream()
            .sorted((a, b) -> a.getValue().equals(b.getValue()) ?
                a.getKey().compareTo(b.getKey()) : b.getValue() - a.getValue())
            .limit(3).map(Map.Entry::getKey).collect(Collectors.toList());
    }
}
```
- **What Netflix evaluates**: Real-time system design, Trie traversal, top K with custom ordering.
- **Follow-up**: Optimize top K with heap at each node. Handle prefix not found case.

### Lab 05-graphs
#### Problem: Clone Graph (LC 133)
- **Difficulty**: Medium | **Frequency**: High
- **Statement**: Deep copy an undirected connected graph.
- **How the interview goes**: Netflix tests BFS/DFS + HashMap for mapping original to clone.
- **Approach 1**: BFS with Queue — O(V + E) time, O(V) space. Level-order clone.
- **Approach 2**: DFS Recursive — O(V + E) time, O(V) space. Uses recursion stack.
- **Java**:
```java
/**
 * Clone graph using BFS.
 * Time: O(V+E), Space: O(V)
 */
public Node cloneGraph(Node node) {
    if (node == null) return null;
    Map<Node, Node> map = new HashMap<>();
    Queue<Node> q = new LinkedList<>();
    map.put(node, new Node(node.val));
    q.offer(node);
    while (!q.isEmpty()) {
        Node curr = q.poll();
        for (Node neighbor : curr.neighbors) {
            if (!map.containsKey(neighbor)) {
                map.put(neighbor, new Node(neighbor.val));
                q.offer(neighbor);
            }
            map.get(curr).neighbors.add(map.get(neighbor));
        }
    }
    return map.get(node);
}
```
- **What Netflix evaluates**: Deep copy understanding, cycle avoidance via visited map, BFS vs DFS choice.
- **Follow-up**: Clone with random pointer. Disconnected graph. N-ary tree clone.

#### Problem: Number of Connected Components in an Undirected Graph (LC 323)
- **Difficulty**: Medium | **Frequency**: Medium
- **Statement**: Count connected components in an undirected graph.
- **How the interview goes**: Netflix tests union-find and DFS for graph connectivity.
- **Approach 1**: Union-Find — O(V·α(V) + E·α(V)) time, O(V) space. Optimal for dynamic connections.
- **Approach 2**: DFS — O(V + E) time, O(V) space. Mark visited, traverse each component.
- **Java**:
```java
/**
 * Count connected components using DFS.
 * Time: O(V+E), Space: O(V)
 */
public int countComponents(int n, int[][] edges) {
    List<List<Integer>> graph = new ArrayList<>(n);
    for (int i = 0; i < n; i++) graph.add(new ArrayList<>());
    for (int[] e : edges) { graph.get(e[0]).add(e[1]); graph.get(e[1]).add(e[0]); }
    boolean[] visited = new boolean[n];
    int count = 0;
    for (int i = 0; i < n; i++) {
        if (!visited[i]) { dfs(graph, visited, i); count++; }
    }
    return count;
}
private void dfs(List<List<Integer>> graph, boolean[] visited, int node) {
    visited[node] = true;
    for (int neighbor : graph.get(node)) {
        if (!visited[neighbor]) dfs(graph, visited, neighbor);
    }
}
```
- **What Netflix evaluates**: Graph representation choice, visited tracking, component counting logic.
- **Follow-up**: Number of islands II (LC 305) — dynamic additions with union-find.

### Lab 24-topological-sort
#### Problem: Course Schedule II (LC 210)
- **Difficulty**: Medium | **Frequency**: High
- **Statement**: Return course ordering to take all courses given prerequisites.
- **How the interview goes**: Netflix tests topological sort output, not just detection.
- **Approach 1**: BFS (Kahn's Algorithm) — O(V + E) time, O(V + E) space. Track indegrees.
- **Approach 2**: DFS Post-Order — O(V + E) time, O(V + E) space. Add to result after all children processed.
- **Java**:
```java
/**
 * Course schedule II using BFS topological sort.
 * Time: O(V+E), Space: O(V+E)
 */
public int[] findOrder(int numCourses, int[][] prerequisites) {
    List<List<Integer>> graph = new ArrayList<>(numCourses);
    for (int i = 0; i < numCourses; i++) graph.add(new ArrayList<>());
    int[] indegree = new int[numCourses];
    for (int[] p : prerequisites) {
        graph.get(p[1]).add(p[0]);
        indegree[p[0]]++;
    }
    Queue<Integer> q = new LinkedList<>();
    for (int i = 0; i < numCourses; i++) if (indegree[i] == 0) q.offer(i);
    int[] result = new int[numCourses];
    int idx = 0;
    while (!q.isEmpty()) {
        int course = q.poll();
        result[idx++] = course;
        for (int neighbor : graph.get(course)) {
            if (--indegree[neighbor] == 0) q.offer(neighbor);
        }
    }
    if (idx != numCourses) return new int[0];
    return result;
}
```
- **What Netflix evaluates**: Complete topological sort, cycle detection via count mismatch, output ordering.
- **Follow-up**: Minimum height trees (LC 310). Alien dictionary (LC 269) for implicit ordering.

### Lab 13-dynamic-programming
#### Problem: Edit Distance (LC 72)
- **Difficulty**: Medium | **Frequency**: High
- **Statement**: Minimum number of operations (insert, delete, replace) to convert one string to another.
- **How the interview goes**: Netflix tests 2D DP table setup. Discuss space optimization.
- **Approach 1**: 2D DP Table — O(m·n) time, O(m·n) space. Classic Levenshtein distance.
- **Approach 2**: Space-Optimized DP — O(m·n) time, O(min(m,n)) space. Only keep two rows.
- **Java**:
```java
/**
 * Edit distance using 2D DP.
 * Time: O(m*n), Space: O(m*n)
 */
public int minDistance(String word1, String word2) {
    int m = word1.length(), n = word2.length();
    int[][] dp = new int[m + 1][n + 1];
    for (int i = 1; i <= m; i++) dp[i][0] = i;
    for (int j = 1; j <= n; j++) dp[0][j] = j;
    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                dp[i][j] = dp[i - 1][j - 1];
            } else {
                dp[i][j] = 1 + Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1]));
            }
        }
    }
    return dp[m][n];
}
```
- **What Netflix evaluates**: DP table construction, operation cost modeling, space optimization.
- **Follow-up**: One edit distance (LC 161). Return the actual operations. Longest common subsequence (LC 1143).

## System Design Questions
1. **Design Netflix Video Recommendation Engine** — Graph (Lab 05) for user-viewing patterns, Heap (Lab 09) for top N recommendations, Hashing (Lab 08) for caching profiles.
2. **Design Netflix Search** — Trie (Lab 17) for autocomplete, Sorting (Lab 15) for relevance ranking, Queue (Lab 07) for async indexing.
3. **Design Content Delivery Network (CDN) Caching** — LRU Cache (Lab 08 + Lab 03) for video segments, B-Tree (Lab 30) for geo-indexing, Heap (Lab 09) for popularity-based eviction.

## Behavioral Questions (STAR)
1. **Situation**: "Tell me about a time you made a data-driven decision." — **Task**: Chose between BFS and DFS for a crawler. **Action**: Benchmarked both (Lab 05) on real traffic patterns. **Result**: BFS chosen — 30% better cache hit rate.
2. **Situation**: "Describe a time you had to work with ambiguity." — **Task**: No clear data structure for new feature. **Action**: Prototyped HashMap, Trie, and BST (Labs 08/17/10). **Result**: Trie was best for prefix-heavy workload.
3. **Situation**: "How did you handle a high-stakes situation?" — **Task**: Production incident with O(n²) bottleneck. **Action**: Refactored to HashMap (Lab 08) for O(1) lookups. **Result**: P95 latency dropped from 500ms to 5ms.
4. **Situation**: "Tell me about a time you disagreed with a peer." — **Task**: Peer wanted ArrayList, I wanted HashMap. **Action**: Benchmarked on production-like data. **Result**: HashMap was 1000× faster for random access.
5. **Situation**: "Describe a time you failed and recovered." — **Task**: Incorrect search index design. **Action**: Redesigned with Trie (Lab 17) instead of binary search (Lab 16). **Result**: Search latency improved 5×.

## Study Plan
1. **Weeks 1-2**: Lab 01-arrays, Lab 08-hashing — Core for high-scale systems
2. **Weeks 3-4**: Lab 09-heaps, Lab 17-trie — Ranking and search patterns
3. **Week 5**: Lab 05-graphs, Lab 24-topological-sort — Relationship modeling
4. **Week 6**: Lab 13-dynamic-programming — Sequence optimization
5. **Weeks 7-8**: Mock interviews with focus on scalability discussions and system-level thinking

## Interview Tips
- **Pace**: Think at system scale. Even small coding problems should consider how they'd perform with millions of users.
- **Communication**: Frame every decision through scalability lens. "This approach works for N=1000 but here's how we'd scale it."
- **What impresses**: Discussing distributed considerations, memory footprint optimization, choosing the right DS for the right reason.
- **Avoid**: Ignoring scale, writing monolithic code, not discussing alternatives.
- **Coding style**: Production-quality with error handling. Netflix runs in production on day one.
