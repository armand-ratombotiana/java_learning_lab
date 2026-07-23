# Meta Interview Guide — Java Academy

## Interview Process for Java Roles

### Rounds & Timeline
- **Phone Screen (45 min):** 1 coding problem, usually LeetCode medium
- **On-site (4-5 rounds):** 2 coding, 1 system design, 1 behavioral (Meta-specific: "tell me about a time leadership failed"), 1 cross-functional
- **Timeline:** 3-5 weeks
- **Java-Specific:** Meta is C++/Python dominant internally (they build on Hack/Python/React). Java is acceptable but **not preferred**. If you use Java, they expect you to know modern Java (21+ features) deeply.

### Java-Specific Evaluation
- Meta cares most about **performance** and **memory efficiency** — Java code should be lean
- They ask **"how many bytes does this object use?"** — know JOL (Java Object Layout)
- **No Spring, no JPA** — Meta uses no Java EE frameworks
- They test **stream fluency** — using `.stream().filter().map().collect()` fluently is seen as good
- Meta expects you to know **Guava** (`ImmutableList`, `Ordering`, `Maps`)
- **Graph problems** are Meta's specialty — Java's `HashMap` and recursion are tested heavily

---

## Top Problems by Module

### Module: Collections & Data Structures

#### Problem: Evaluate Division (Graph Traversal)
- **LC:** 399
- **Difficulty/Frequency:** Medium / High
- **Problem Statement:** Given equations with values like "a/b=2", evaluate queries like "a/c=?".
- **Interview Walkthrough:** Build a weighted graph using `HashMap<String, HashMap<String, Double>>`. Run DFS with a visited set tracking accumulated product. Meta prefers DFS over Union-Find because they want to see if you can think in graph terms.
- **Solution 1 vs Solution 2:** DFS with backtracking is Meta's preferred answer (O(n) per query). Union-Find with path compression is faster but harder to explain. Meta wants DFS — it demonstrates graph thinking.
- **Java Code:**
```java
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Evaluates division queries using weighted graph DFS.
 * Meta expects graph-based approach — they evaluate division as path product.
 *
 * LC 399 — Meta graph traversal standard
 */
public class EvaluateDivision {

    private Map<String, Map<String, Double>> graph;

    /**
     * Builds graph from equations and answers queries.
     * @param equations list of variable pairs
     * @param values    corresponding division values
     * @param queries   queries to evaluate
     * @return array of results, -1.0 for unknown
     */
    public double[] calcEquation(
        List<List<String>> equations,
        double[] values,
        List<List<String>> queries
    ) {
        graph = new HashMap<>();
        for (int i = 0; i < equations.size(); i++) {
            String a = equations.get(i).get(0);
            String b = equations.get(i).get(1);
            graph.computeIfAbsent(a, k -> new HashMap<>()).put(b, values[i]);
            graph.computeIfAbsent(b, k -> new HashMap<>()).put(a, 1.0 / values[i]);
        }

        double[] result = new double[queries.size()];
        for (int i = 0; i < queries.size(); i++) {
            String src = queries.get(i).get(0);
            String dst = queries.get(i).get(1);
            if (!graph.containsKey(src) || !graph.containsKey(dst)) {
                result[i] = -1.0;
            } else if (src.equals(dst)) {
                result[i] = 1.0;
            } else {
                Set<String> visited = new HashSet<>();
                result[i] = dfs(src, dst, visited, 1.0);
            }
        }
        return result;
    }

    private double dfs(String src, String dst, Set<String> visited, double product) {
        visited.add(src);
        for (Map.Entry<String, Double> edge : graph.get(src).entrySet()) {
            String neighbor = edge.getKey();
            if (neighbor.equals(dst)) {
                return product * edge.getValue();
            }
            if (!visited.contains(neighbor)) {
                double result = dfs(neighbor, dst, visited, product * edge.getValue());
                if (result != -1.0) return result;
            }
        }
        visited.remove(src);
        return -1.0;
    }
}
```
- **Company Evaluation Criteria:** Graph modeling, visited set handling, backtracking correctness.
- **Follow-ups:** Union-Find solution; what if queries have 100k nodes (stack overflow)?

#### Problem: Accounts Merge
- **LC:** 721
- **Difficulty/Frequency:** Medium / High
- **Problem Statement:** Merge accounts sharing common email addresses.
- **Interview Walkthrough:** Build email-to-account graph. DFS to find connected components. Meta loves this because it's a real problem from their user merge pipeline.
- **Solution 1 vs Solution 2:** Union-Find is cleaner. DFS graph traversal is more intuitive. Meta accepts both but will ask about time complexity of each.
- **Java Code:**
```java
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Merges accounts sharing common emails using Union-Find.
 * Meta uses this as a graph connectivity problem.
 *
 * LC 721 — Meta's real user merge problem
 */
public class AccountsMerge {

    static class UnionFind {
        int[] parent;
        UnionFind(int n) {
            parent = new int[n];
            for (int i = 0; i < n; i++) parent[i] = i;
        }
        int find(int x) {
            if (parent[x] != x) parent[x] = find(parent[x]);
            return parent[x];
        }
        void union(int a, int b) { parent[find(a)] = find(b); }
    }

    /**
     * Merges accounts by shared emails.
     * @param accounts list of [name, email1, email2, ...]
     * @return merged accounts with sorted emails
     */
    public List<List<String>> accountsMerge(List<List<String>> accounts) {
        Map<String, Integer> emailToId = new HashMap<>();
        Map<String, String> emailToName = new HashMap<>();
        int id = 0;

        for (List<String> acct : accounts) {
            String name = acct.get(0);
            for (int i = 1; i < acct.size(); i++) {
                String email = acct.get(i);
                emailToName.put(email, name);
                if (!emailToId.containsKey(email)) {
                    emailToId.put(email, id++);
                }
            }
        }

        UnionFind uf = new UnionFind(id);
        for (List<String> acct : accounts) {
            if (acct.size() > 2) {
                int firstId = emailToId.get(acct.get(1));
                for (int i = 2; i < acct.size(); i++) {
                    uf.union(firstId, emailToId.get(acct.get(i)));
                }
            }
        }

        Map<Integer, List<String>> rootToEmails = new HashMap<>();
        for (String email : emailToId.keySet()) {
            int root = uf.find(emailToId.get(email));
            rootToEmails.computeIfAbsent(root, k -> new ArrayList<>()).add(email);
        }

        return rootToEmails.values().stream()
            .map(emails -> {
                List<String> merged = new ArrayList<>();
                merged.add(emailToName.get(emails.get(0)));
                Collections.sort(emails);
                merged.addAll(emails);
                return merged;
            })
            .collect(Collectors.toList());
    }
}
```
- **Company Evaluation Criteria:** Correct Union-Find implementation, path compression, handling of single-email accounts.
- **Follow-ups:** What if emails are Unicode? How to handle 10M accounts?

---

### Module: Recursion & Backtracking

#### Problem: Subsets II (with duplicates)
- **LC:** 90
- **Difficulty/Frequency:** Medium / High
- **Problem Statement:** Generate all possible subsets from array with duplicates.
- **Interview Walkthrough:** Meta loves subset/combination problems. Sort array. Use backtracking with `i > start && nums[i] == nums[i-1]` skip. Meta asks "what is the memory footprint of your recursion stack?"
- **Solution 1 vs Solution 2:** Recursive backtracking (standard). Iterative with `for` loops (harder but avoids call stack). Meta wants recursion — they'll ask about stack depth.
- **Java Code:**
```java
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Generates all subsets from array with duplicates (no duplicate subsets).
 * Meta tests recursion + pruning understanding.
 *
 * LC 90 — Meta backtracking interview question
 */
public class SubsetsWithDup {

    /**
     * @param nums array with possible duplicates
     * @return list of unique subsets
     */
    public List<List<Integer>> subsetsWithDup(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);
        backtrack(result, new ArrayList<>(), nums, 0);
        return result;
    }

    private void backtrack(List<List<Integer>> result, List<Integer> temp, int[] nums, int start) {
        result.add(new ArrayList<>(temp));
        for (int i = start; i < nums.length; i++) {
            if (i > start && nums[i] == nums[i - 1]) continue;
            temp.add(nums[i]);
            backtrack(result, temp, nums, i + 1);
            temp.remove(temp.size() - 1);
        }
    }
}
```
- **Company Evaluation Criteria:** Duplicate pruning, sorting choice, recursion depth awareness.
- **Follow-ups:** Iterative solution with bitmask; what if nums length is 30 (2^30 subsets)?

#### Problem: Word Search II
- **LC:** 212
- **Difficulty/Frequency:** Hard / High
- **Problem Statement:** Find all words from dictionary in a 2D board.
- **Interview Walkthrough:** Trie + DFS backtracking. Meta loves this because it combines two data structures. Build Trie from words, DFS each board cell, prune when prefix not in Trie.
- **Solution 1 vs Solution 2:** Simple DFS for each word (O(words * board^2)). Trie + DFS (O(board^2 * maxWordLen)). Meta expects Trie solution — it shows systems thinking.
- **Java Code:**
```java
import java.util.ArrayList;
import java.util.List;

/**
 * Word Search using Trie + DFS backtracking.
 * Meta's classic combination problem — tests multiple data structure mastery.
 *
 * LC 212 — Meta's hardest on-site coding question
 */
public class WordSearchII {

    private static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        String word;
    }

    private TrieNode buildTrie(String[] words) {
        TrieNode root = new TrieNode();
        for (String w : words) {
            TrieNode node = root;
            for (char c : w.toCharArray()) {
                int idx = c - 'a';
                if (node.children[idx] == null) node.children[idx] = new TrieNode();
                node = node.children[idx];
            }
            node.word = w;
        }
        return root;
    }

    /**
     * Finds all words from dictionary present in the board.
     * @param board 2D character grid
     * @param words dictionary
     * @return list of found words
     */
    public List<String> findWords(char[][] board, String[] words) {
        List<String> result = new ArrayList<>();
        TrieNode root = buildTrie(words);
        int rows = board.length, cols = board[0].length;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                dfs(board, r, c, root, result);
            }
        }
        return result;
    }

    private void dfs(char[][] board, int r, int c, TrieNode node, List<String> result) {
        char ch = board[r][c];
        if (ch == '#' || node.children[ch - 'a'] == null) return;
        node = node.children[ch - 'a'];
        if (node.word != null) {
            result.add(node.word);
            node.word = null; // deduplicate
        }
        board[r][c] = '#';
        int[][] dirs = {{1,0}, {-1,0}, {0,1}, {0,-1}};
        for (int[] d : dirs) {
            int nr = r + d[0], nc = c + d[1];
            if (nr >= 0 && nr < board.length && nc >= 0 && nc < board[0].length) {
                dfs(board, nr, nc, node, result);
            }
        }
        board[r][c] = ch;
    }
}
```
- **Company Evaluation Criteria:** Trie construction, DFS pruning, cell revisiting prevention, deduplication.
- **Follow-ups:** Runtime complexity; what if board is 2000x2000 (stack overflow)? Use iterative DFS.

---

### Module: Streams & Functional Programming

#### Problem: Top K Frequent Elements
- **LC:** 347
- **Difficulty/Frequency:** Medium / High
- **Problem Statement:** Return top K most frequent elements in an array.
- **Interview Walkthrough:** Meta asks this frequently. HashMap for frequency, then bucket sort or heap. Meta wants to see both the heap and the bucket sort approach.
- **Solution 1 vs Solution 2:** Min-heap O(n log k). Bucket sort O(n) using array of lists. Meta prefers bucket sort for performance — they value efficiency.
- **Java Code:**
```java
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Top K frequent elements using bucket sort — O(n).
 * Meta prefers the O(n) bucket approach over heap.
 *
 * LC 347 — Meta's frequency-based problem
 */
public class TopKFrequentElements {

    /**
     * @param nums input array
     * @param k    number of top frequent elements
     * @return array of k most frequent elements
     */
    public int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (int n : nums) freq.merge(n, 1, Integer::sum);

        List<Integer>[] buckets = new List[nums.length + 1];
        for (int key : freq.keySet()) {
            int f = freq.get(key);
            if (buckets[f] == null) buckets[f] = new ArrayList<>();
            buckets[f].add(key);
        }

        List<Integer> result = new ArrayList<>();
        for (int i = buckets.length - 1; i >= 0 && result.size() < k; i--) {
            if (buckets[i] != null) {
                result.addAll(buckets[i]);
            }
        }
        return result.stream().mapToInt(Integer::intValue).toArray();
    }
}
```
- **Company Evaluation Criteria:** Bucket sort intuition, stream fluency, time/space trade-off discussion.
- **Follow-ups:** What if k is very small (use heap)? What if all elements are unique?

---

### Module: Dynamic Programming

#### Problem: Word Break
- **LC:** 139
- **Difficulty/Frequency:** Medium / High
- **Problem Statement:** Given a string and a dictionary, determine if string can be segmented into dictionary words.
- **Interview Walkthrough:** Meta asks DP + BFS versions. DP bottom-up: `dp[i] = true if dp[j] && dict.contains(s.substring(j,i))`. Meta will ask about JVM string substring memory implications.
- **Solution 1 vs Solution 2:** DP O(n^2) time. BFS/Trie is faster for large dictionaries. Meta wants DP — then discuss optimization with HashSet lookup.
- **Java Code:**
```java
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Word Break using bottom-up DP.
 * Meta tests DP reasoning + Java substring memory awareness.
 *
 * LC 139 — Meta DP interview question
 */
public class WordBreak {

    /**
     * Determines if string can be segmented into dictionary words.
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
- **Company Evaluation Criteria:** Correct DP recurrence, HashSet optimization, understanding of `substring()` (JDK 8 vs 17+).
- **Follow-ups:** Return all possible sentences (LC 140); use Trie for dictionary lookup.

---

### Module: Performance & Memory

#### Problem: Serialize and Deserialize N-ary Tree
- **LC:** 428
- **Difficulty/Frequency:** Hard / Medium
- **Problem Statement:** Serialize/deserialize N-ary tree to/from string.
- **Interview Walkthrough:** Meta asks this for their News Feed ranking teams. Level-order with sentinel nodes. Discuss JVM string encoding (UTF-16 vs UTF-8) for serialized format.
- **Solution 1 vs Solution 2:** Pre-order with child count (compact). Level-order with sentinels (readable). Meta wants pre-order with child count — it's what they use internally.
- **Java Code:**
```java
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

/**
 * Serializes/deserializes N-ary tree using pre-order + child count.
 * Meta uses this for tree-structured data storage.
 *
 * LC 428 — Meta's tree serialization interview question
 */
public class CodecNary {

    static class Node {
        int val;
        List<Node> children;
        Node(int val) { this.val = val; this.children = new ArrayList<>(); }
    }

    private static final String SEP = ",";
    private static final String END = "#";

    /**
     * Encodes N-ary tree to compact string.
     * @param root tree root
     * @return serialized string
     */
    public String serialize(Node root) {
        if (root == null) return "";
        StringBuilder sb = new StringBuilder();
        serializeHelper(root, sb);
        return sb.toString();
    }

    private void serializeHelper(Node node, StringBuilder sb) {
        sb.append(node.val).append(SEP);
        sb.append(node.children.size()).append(SEP);
        for (Node child : node.children) {
            serializeHelper(child, sb);
        }
    }

    /**
     * Decodes string back to N-ary tree.
     * @param data serialized string
     * @return tree root
     */
    public Node deserialize(String data) {
        if (data == null || data.isEmpty()) return null;
        Deque<String> queue = new ArrayDeque<>(Arrays.asList(data.split(SEP)));
        return deserializeHelper(queue);
    }

    private Node deserializeHelper(Deque<String> queue) {
        int val = Integer.parseInt(queue.poll());
        int childCount = Integer.parseInt(queue.poll());
        Node node = new Node(val);
        for (int i = 0; i < childCount; i++) {
            node.children.add(deserializeHelper(queue));
        }
        return node;
    }
}
```
- **Company Evaluation Criteria:** Pre-order with count encoding, handling empty/null, recursion depth awareness.
- **Follow-ups:** Support cyclic graphs; iterative deserialization using stack.

---

## JVM/Concurrency Deep Dive Questions

Meta asks performance-centric JVM questions:

1. **How does the JVM represent an array?** — Object header (mark word + klass pointer) + length + element data. Meta expects you to know exact byte layout and padding.
2. **Explain escape analysis and stack allocation** — Meta uses this for optimization. "Can the JVM allocate this object on the stack? What conditions prevent escape analysis?"
3. **How does String.substring() work in JDK 8 vs JDK 17?** — In JDK 8, substring shares the underlying char[]. In JDK 17, it copies. Meta will ask which is better for a large text processing service.
4. **Explain G1GC humongous allocations** — "A 12MB array is allocated. Walk through G1GC handling of humongous regions." Meta tests this for their ML pipeline teams.
5. **What is a biased lock and why was it removed?** — JDK 15 removed biased locking. Meta asks "should we re-enable it for our services?"

## System Design with Java

1. **Design Facebook News Feed** — Java `ConcurrentSkipListMap` for ranking. Discuss push vs pull with `BlockingQueue`. Meta expects Java discuss of memory-mapped files for feed storage.
2. **Design Typeahead Search** — Trie implementation in Java. Discuss `HashMap<Character, TrieNode>` memory overhead vs array-based Trie. How many bytes per node?
3. **Design Facebook Messenger** — Java NIO for long-polling. `ConcurrentLinkedQueue` for message delivery ordering. Discuss `ByteBuffer` vs `String` for WebSocket frames.

## Behavioral Questions (STAR)

Meta's behavioral round evaluates "Move Fast" culture fit.

1. **"Tell me about a time you moved fast."** — *S: Critical bug in production data pipeline. T: Restore within SLA. A: Identified `OutOfMemoryError` in Java stream collecting to `toList()` without pagination. Hot-patched with `Stream.iterator()` + batch processing. R: Restored in 30 min, later rewrote with pagination.*
2. **"Tell me about a time you had a technical disagreement."** — *S: Engineer wanted XML config, I wanted Java records. T: Choose configuration approach. A: Built prototype of both — Java records were 40% fewer lines, compile-time safety. R: Team migrated 12 modules to records.*
3. **"Tell me about a time you failed publicly."** — *S: Deployed Java 21 migration without testing virtual thread pinning. T: Fix production incident. A: Rolled back, learned about synchronized block pinning, added structured concurrency tests. R: Successful migration 2 weeks later.*
4. **"Tell me about what you built that you're proud of."** — *S: Built internal profiling tool using JVMTI (JVM Tool Interface). T: Help teams see GC-free allocation paths. A: Used `jvmti` to capture allocation stack traces, built dashboard. R: 15 teams adopted it, found 200+ GC bottlenecks.*
5. **"Tell me about a time [Meta value]."** — Craft responses around Meta's seven values: Move Fast, Focus on Impact, Be Bold, Build Social Value, Be Direct, Respect Others, Live in the Future.

## Study Plan

| Priority | Labs | Focus |
|----------|------|-------|
| P0 | 12-collections, 13-streams, 14-lambdas, 15-functional-programming | Collections & streams |
| P0 | 37-performance-profiling, 38-memory-model, 50-object-layout-memory | Performance & memory |
| P1 | 24-pattern-matching, 22-records, 23-sealed-classes, 21-java-21-features | Modern Java |
| P1 | 16-concurrency, 41-threading-deep-dive | Concurrency basics |
| P2 | 45-gc-deep-dive, 44-jit-compilation | JVM internals |
| P2 | 30-jvm-internals, 43-class-loading-bytecode | Class loading (less focus) |

**Preparation Path:** Solve LC 399, 721, 90, 212, 347, 139, 428 in Java. Learn JOL for object size measurement. Practice streams fluently. Prepare Meta behavioral stories — expect bold, direct conversation.

## Tips

- **Java is not Meta's native language** — You must justify why you're using Java. Say "I chose Java for its mature concurrency libraries and tooling."
- **Meta values speed** — If you spend 5 minutes setting up a class hierarchy, they'll ask you to stop
- **Expect "follow-up after follow-up"** — Meta interviewers keep asking "what if?" until you say "I don't know"
- **Know JOL (Java Object Layout)** — Meta interviewers love asking "how many bytes does `new ArrayList()` take?"
- **Be ready for "can you make this faster?"** — Every solution gets challenged on performance
- **Graph problems are Meta's favorite** — Practice BFS, DFS, Union-Find, topological sort in Java
- **Meta behavioral is intense** — They will interrupt you. Be direct, don't hedge. Use "I decided", "I built", "I failed" — no passive voice