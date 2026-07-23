# NeetCode / Blind 75 / Grind 75 → Java Academy Roadmap

> **Maps every major interview problem to a Java Academy lab + LEETCODE_SOLUTION file + Java-specific implementation notes**

---

## Table of Contents
1. [NeetCode 150 — Java Mappings](#neetcode-150--java-mappings)
2. [Blind 75 — Java Mappings](#blind-75--java-mappings)
3. [Grind 75 (Top 50) — Java Mappings](#grind-75-top-50--java-mappings)
4. [Java-Specific Study Plans](#java-specific-study-plans)
5. [Company Focus Areas](#company-focus-areas)

---

## NeetCode 150 — Java Mappings

### Arrays & Hashing

| Problem | Java Academy Lab | Java API / Pattern | Template Reference |
|---------|-----------------|--------------------|-------------------|
| 1 Two Sum | 12-collections / 05-hashmap-internals | HashMap with `computeIfAbsent` | `JAVA_CODING_TEMPLATES.md` |
| 49 Group Anagrams | 05-arrays-strings / 27-string-handling | `HashMap<String, List<String>>`, char[] sorting | `LEETCODE_PATTERN_CHEATSHEET.md` |
| 128 Longest Consecutive Sequence | 12-collections / 01-hashmap-internals | HashSet, O(1) lookups | `JAVA_CODING_TEMPLATES.md` |
| 217 Contains Duplicate | 12-collections | `HashSet.add()` returns boolean | `JAVA_CODING_TEMPLATES.md` |
| 242 Valid Anagram | 27-string-handling | `int[26]` frequency | `LEETCODE_PATTERN_CHEATSHEET.md` |
| 347 Top K Frequent Elements | 06-priority-queue | `PriorityQueue<Map.Entry>` | `JAVA_CODING_TEMPLATES.md` |
| 238 Product of Array Except Self | 05-arrays-strings | Prefix/suffix arrays | `JAVA_CODING_TEMPLATES.md` |

### Two Pointers

| Problem | Java Academy Lab | Java API / Pattern | Template Reference |
|---------|-----------------|--------------------|-------------------|
| 125 Valid Palindrome | 05-arrays-strings | `Character.isLetterOrDigit()` | `LEETCODE_PATTERN_CHEATSHEET.md` |
| 167 Two Sum II | 05-arrays-strings | Two-pointer, sorted array | `JAVA_CODING_TEMPLATES.md` |
| 15 3Sum | 05-arrays-strings | Sort + two-pointer, `Arrays.sort()` | `JAVA_CODING_TEMPLATES.md` |
| 11 Container With Most Water | 05-arrays-strings | Two-pointer, `Math.min()` | `LEETCODE_PATTERN_CHEATSHEET.md` |

### Sliding Window

| Problem | Java Academy Lab | Java API / Pattern | Template Reference |
|---------|-----------------|--------------------|-------------------|
| 121 Best Time to Buy Stock | 05-arrays-strings | Single pass, min tracking | `JAVA_CODING_TEMPLATES.md` |
| 3 Longest Substring No Repeat | 27-string-handling | `int[256]` freq or `HashMap<Character, Integer>` | `LEETCODE_PATTERN_CHEATSHEET.md` |
| 424 Longest Repeating Char Repl. | 27-string-handling | Sliding window with `int[26]`, `Math.max()` | `LEETCODE_PATTERN_CHEATSHEET.md` |
| 76 Minimum Window Substring | 27-string-handling | `HashMap<Character, Integer>` window counters | `JAVA_CODING_TEMPLATES.md` |
| 239 Sliding Window Maximum | 06-priority-queue / 04-arraylist-deep | `ArrayDeque` monotonic queue | `LEETCODE_PATTERN_CHEATSHEET.md` |

### Stack

| Problem | Java Academy Lab | Java API / Pattern | Template Reference |
|---------|-----------------|--------------------|-------------------|
| 20 Valid Parentheses | 03-control-flow | `ArrayDeque<Character>` as stack | `JAVA_CODING_TEMPLATES.md` |
| 155 Min Stack | 12-collections / 08-concurrent-linked-queue | Two `ArrayDeque` or `Deque<int[]>` | `JAVA_CODING_TEMPLATES.md` |
| 739 Daily Temperatures | 12-collections | Monotonic stack, `ArrayDeque<Integer>` indices | `LEETCODE_PATTERN_CHEATSHEET.md` |
| 84 Largest Rectangle in Histogram | 06-priority-queue | Monotonic stack, `ArrayDeque<Integer>` | `JAVA_CODING_TEMPLATES.md` |
| 853 Car Fleet | 03-control-flow | Stack with `double[]` times | `LEETCODE_PATTERN_CHEATSHEET.md` |

### Binary Search

| Problem | Java Academy Lab | Java API / Pattern | Template Reference |
|---------|-----------------|--------------------|-------------------|
| 704 Binary Search | 04-methods | `Arrays.binarySearch()` or manual | `JAVA_CODING_TEMPLATES.md` |
| 74 Search 2D Matrix | 05-arrays-strings | Flattened binary search | `JAVA_CODING_TEMPLATES.md` |
| 875 Koko Eating Bananas | 04-methods | Binary search on answer | `JAVA_CODING_TEMPLATES.md` |
| 981 Time Based KV Store | 05-treemap-treeset | `TreeMap.floorKey()` | `LEETCODE_PATTERN_CHEATSHEET.md` |

### Linked List

| Problem | Java Academy Lab | Java API / Pattern | Template Reference |
|---------|-----------------|--------------------|-------------------|
| 206 Reverse Linked List | 06-oop-basics | Iterative (prev/curr/next) or recursive | `JAVA_CODING_TEMPLATES.md` |
| 141 Linked List Cycle | 06-oop-basics | Slow/fast pointer, `HashSet<ListNode>` | `JAVA_CODING_TEMPLATES.md` |
| 21 Merge Two Sorted Lists | 06-oop-basics | Dummy head, merge while loop | `JAVA_CODING_TEMPLATES.md` |
| 143 Reorder List | 06-oop-basics | Find middle + reverse + merge | `JAVA_CODING_TEMPLATES.md` |
| 19 Remove Nth From End | 06-oop-basics | Dummy node + two-pointer | `JAVA_CODING_TEMPLATES.md` |

### Trees

| Problem | Java Academy Lab | Java API / Pattern | Template Reference |
|---------|-----------------|--------------------|-------------------|
| 102 Level Order | 06-oop-basics | `Deque<TreeNode>`, BFS | `JAVA_CODING_TEMPLATES.md` |
| 104 Max Depth | 06-oop-basics | Recursion, `Math.max()` | `JAVA_CODING_TEMPLATES.md` |
| 226 Invert Tree | 06-oop-basics | Recursive swap children | `JAVA_CODING_TEMPLATES.md` |
| 98 Validate BST | 06-oop-basics | In-order traversal or range check | `LEETCODE_PATTERN_CHEATSHEET.md` |
| 235 LCA of BST | 06-oop-basics | BST properties, iterative | `JAVA_CODING_TEMPLATES.md` |
| 297 Serialize Binary Tree | 30-jvm-internals / 35-serialization | BFS/DFS + StringBuilder | `JAVA_CODING_TEMPLATES.md` |

### Tries

| Problem | Java Academy Lab | Java API / Pattern | Template Reference |
|---------|-----------------|--------------------|-------------------|
| 208 Implement Trie | 06-oop-basics | `TrieNode[]` children, `isWord` boolean | `JAVA_CODING_TEMPLATES.md` |
| 211 Word Search II | 06-oop-basics | Trie + backtracking | `JAVA_CODING_TEMPLATES.md` |

### Heap / PriorityQueue

| Problem | Java Academy Lab | Java API / Pattern | Template Reference |
|---------|-----------------|--------------------|-------------------|
| 215 Kth Largest | 06-priority-queue | `PriorityQueue<Integer>` min-heap of size k | `JAVA_CODING_TEMPLATES.md` |
| 295 Find Median | 06-priority-queue | Two-heap: max + min `PriorityQueue` | `JAVA_CODING_TEMPLATES.md` |
| 973 K Closest Points | 06-priority-queue | `PriorityQueue<int[]>` comparator on distance | `JAVA_CODING_TEMPLATES.md` |

### Backtracking

| Problem | Java Academy Lab | Java API / Pattern | Template Reference |
|---------|-----------------|--------------------|-------------------|
| 78 Subsets | 04-methods | Backtracking with `List<List<Integer>>` | `JAVA_CODING_TEMPLATES.md` |
| 90 Subsets II | 04-methods | Sort + skip duplicates | `JAVA_CODING_TEMPLATES.md` |
| 46 Permutations | 04-methods | Backtracking with used boolean[] | `JAVA_CODING_TEMPLATES.md` |
| 51 N-Queens | 05-arrays-strings | Backtracking with `char[][]` board | `JAVA_CODING_TEMPLATES.md` |

### Graphs

| Problem | Java Academy Lab | Java API / Pattern | Template Reference |
|---------|-----------------|--------------------|-------------------|
| 200 Number of Islands | 32-networking / 16-concurrency | `boolean[][]` visited, DFS/BFS | `JAVA_CODING_TEMPLATES.md` |
| 207 Course Schedule | 12-collections | Topological sort, adjacency list + indegree | `LEETCODE_PATTERN_CHEATSHEET.md` |
| 133 Clone Graph | 06-oop-basics | `HashMap<Node, Node>` visited | `JAVA_CODING_TEMPLATES.md` |
| 417 Pacific Atlantic | 05-arrays-strings | DFS from ocean borders | `LEETCODE_PATTERN_CHEATSHEET.md` |

### Dynamic Programming

| Problem | Java Academy Lab | Java API / Pattern | Template Reference |
|---------|-----------------|--------------------|-------------------|
| 70 Climbing Stairs | 04-methods | `int[]` DP, space-optimized | `JAVA_CODING_TEMPLATES.md` |
| 198 House Robber | 04-methods | `int[]` DP, prev/curr optimization | `JAVA_CODING_TEMPLATES.md` |
| 300 LIS | 12-collections | `Arrays.binarySearch()` + `dp[]` | `JAVA_CODING_TEMPLATES.md` |
| 1143 Longest Common Subseq | 11-generics | `int[][]` memo DP | `JAVA_CODING_TEMPLATES.md` |
| 322 Coin Change | 12-collections | `Arrays.fill(dp, INF)`, `Math.min()` | `JAVA_CODING_TEMPLATES.md` |
| 416 Partition Equal Subset | 38-memory-model | `boolean[]` subset sum DP | `JAVA_CODING_TEMPLATES.md` |

---

## Blind 75 — Java Mappings

| # | Problem | Java Academy Lab | Java Class/API | Key Pattern |
|---|---------|-----------------|----------------|-------------|
| 1 | 1 Two Sum | 12-collections / 01-hashmap-internals | `HashMap<Integer, Integer>` | complement lookup |
| 2 | 121 Best Time to Buy Stock | 05-arrays-strings | `int` minPrice, maxProfit | single pass |
| 3 | 3 Longest Substring No Repeat | 27-string-handling | `HashMap<Character, Integer>` | sliding window |
| 4 | 238 Product of Array Except Self | 05-arrays-strings | `int[]` prefix, suffix | two-pass |
| 5 | 53 Maximum Subarray | 05-arrays-strings | `int` maxEndingHere, maxSoFar | Kadane's algorithm |
| 6 | 152 Max Product Subarray | 05-arrays-strings | `int` min, max, result | dual Kadane |
| 7 | 153 Min in Rotated Sorted Array | 05-arrays-strings | `Arrays.binarySearch()` | binary search |
| 8 | 33 Search Rotated Sorted | 05-arrays-strings | manual binary search | rotated binary |
| 9 | 15 3Sum | 05-arrays-strings | `Arrays.sort()` + two-pointer | sort + two-pointer |
| 10 | 11 Container Most Water | 05-arrays-strings | two-pointer `Math.min()` | two-pointer |
| 11 | 371 Sum Two Integers | 09-abstraction-interfaces | bitwise `^` `&` `<<` | bit manipulation |
| 12 | 191 Number of 1 Bits | 09-abstraction-interfaces | `Integer.bitCount()` | bit manipulation |
| 13 | 338 Counting Bits | 09-abstraction-interfaces | `int[]` dp: `dp[i] = dp[i>>1] + (i&1)` | DP + bit |
| 14 | 190 Reverse Bits | 09-abstraction-interfaces | `Integer.reverse()` | bit manipulation |
| 15 | 268 Missing Number | 05-arrays-strings | XOR or `n*(n+1)/2 - sum` | math / XOR |
| 16 | 100 Same Tree | 06-oop-basics | recursive tree comparison | tree recursion |
| 17 | 572 Subtree of Tree | 06-oop-basics | same tree check on each node | tree recursion |
| 18 | 235 LCA of BST | 06-oop-basics | BST property iteration | BST traversal |
| 19 | 124 Binary Tree Max Path | 06-oop-basics | post-order recursion | tree recursion |
| 20 | 102 Level Order | 06-oop-basics | `Deque<TreeNode>` | tree BFS |
| 21 | 297 Serialize Binary Tree | 35-serialization | `StringBuilder` + BFS | tree serialization |
| 22 | 105 Construct from Pre/In | 06-oop-basics | `HashMap<Integer, Integer>` index map | tree building |
| 23 | 206 Reverse Linked List | 06-oop-basics | iterative prev/curr | linked list |
| 24 | 141 Linked List Cycle | 06-oop-basics | slow/fast pointer | Floyd's algorithm |
| 25 | 21 Merge Two Sorted Lists | 06-oop-basics | dummy head | linked list |
| 26 | 23 Merge K Sorted Lists | 06-priority-queue | `PriorityQueue<ListNode>` | heap |
| 27 | 19 Remove Nth From End | 06-oop-basics | dummy + two-pointer | linked list |
| 28 | 143 Reorder List | 06-oop-basics | middle + reverse + merge | linked list |
| 29 | 417 Pacific Atlantic | 05-arrays-strings | DFS from borders | graph DFS |
| 30 | 200 Number of Islands | 32-networking | `boolean[][]` DFS | graph DFS |
| 31 | 128 Longest Consecutive | 12-collections | `HashSet<Integer>` O(n) | set iteration |
| 32 | 73 Set Matrix Zeroes | 05-arrays-strings | `boolean[]` row/col | matrix manipulation |
| 33 | 54 Spiral Matrix | 05-arrays-strings | boundary pointers | matrix traversal |
| 34 | 48 Rotate Image | 05-arrays-strings | transpose + reverse | matrix manipulation |
| 35 | 79 Word Search | 06-oop-basics | backtracking with visited | graph DFS |
| 36 | 76 Minimum Window Substring | 27-string-handling | `HashMap<Character, Integer>` | sliding window |
| 37 | 424 Longest Repeating Char | 27-string-handling | `int[26]` sliding window | sliding window |
| 38 | 242 Valid Anagram | 27-string-handling | `int[26]` frequency | string counting |
| 39 | 49 Group Anagrams | 27-string-handling | `HashMap<String, List<String>>` | string sorting |
| 40 | 347 Top K Frequent | 06-priority-queue | `PriorityQueue<Map.Entry>` | heap |
| 41 | 271 Encode/Decode Strings | 35-serialization | StringBuilder with delimiter | encoding |
| 42 | 20 Valid Parentheses | 03-control-flow | `ArrayDeque<Character>` | stack |
| 43 | 155 Min Stack | 12-collections | `Deque<int[]>` storing min | stack |
| 44 | 739 Daily Temperatures | 12-collections | monotonic `Deque<Integer>` | stack |
| 45 | 150 Evaluate Reverse Polish | 03-control-flow | `Deque<Integer>` stack | stack |
| 46 | 22 Generate Parentheses | 04-methods | backtracking | recursion |
| 47 | 39 Combination Sum | 04-methods | backtracking with sort | recursion |
| 48 | 78 Subsets | 04-methods | backtracking | recursion |
| 49 | 207 Course Schedule | 12-collections | adjacency + indegree array | topological sort |
| 50 | 208 Implement Trie | 06-oop-basics | `TrieNode[26]` | trie |
| 51 | 211 Word Search II | 06-oop-basics | trie + backtracking | trie + DFS |
| 52 | 212 Word Search II | 06-oop-basics | trie + DFS pruning | trie + DFS |
| 53 | 704 Binary Search | 04-methods | `l + (r-l)/2` | binary search |
| 54 | 74 Search 2D Matrix | 05-arrays-strings | binary search on rows | binary search |
| 55 | 875 Koko Bananas | 04-methods | binary search on answer | binary search |
| 56 | 981 Time KV Store | 05-treemap-treeset | `TreeMap.floorKey()` | binary search on map |
| 57 | 70 Climbing Stairs | 04-methods | `int[]` DP | DP |
| 58 | 198 House Robber | 04-methods | prev/curr DP | DP |
| 59 | 213 House Robber II | 04-methods | two passes of house robber | DP |
| 60 | 5 Longest Palindromic | 27-string-handling | expand around center | string |
| 61 | 647 Palindromic Substrings | 27-string-handling | expand around center | string |
| 62 | 91 Decode Ways | 04-methods | `int[]` DP | DP |
| 63 | 139 Word Break | 12-collections | `HashSet<String>` + DP | DP + set |
| 64 | 300 Longest Increasing | 12-collections | `Arrays.binarySearch()` + dp | DP + binary |
| 65 | 416 Partition Equal | 38-memory-model | `boolean[]` subset sum | DP |
| 66 | 1143 Longest Common Subseq | 11-generics | `int[][]` DP | DP |
| 67 | 322 Coin Change | 12-collections | `Arrays.fill(dp, INF)` | DP |
| 68 | 62 Unique Paths | 04-methods | `int[][]` DP | DP |
| 69 | 55 Jump Game | 05-arrays-strings | greedy maxReach | greedy |
| 70 | 45 Jump Game II | 05-arrays-strings | BFS-like greedy | greedy |
| 71 | 56 Merge Intervals | 12-collections | `Arrays.sort()` on start | interval |
| 72 | 57 Insert Interval | 12-collections | linear scan | interval |
| 73 | 435 Non-overlapping Intervals | 12-collections | sort + greedy | interval |
| 74 | 252 Meeting Rooms | 12-collections | sort + compare end/start | interval |
| 75 | 253 Meeting Rooms II | 06-priority-queue | `PriorityQueue<Integer>` end times | heap + interval |

---

## Grind 75 (Top 50) — Java Mappings

| # | Problem | Java Academy Lab | Java API | Key Pattern |
|---|---------|-----------------|----------|-------------|
| 1 | 1 Two Sum | 01-hashmap-internals | `HashMap` | complement lookup |
| 2 | 20 Valid Parentheses | 03-control-flow | `ArrayDeque` | stack |
| 3 | 21 Merge Two Sorted Lists | 06-oop-basics | dummy ListNode | linked list |
| 4 | 121 Best Time to Buy | 05-arrays-strings | min/max tracking | single pass |
| 5 | 125 Valid Palindrome | 05-arrays-strings | two-pointer char | string |
| 6 | 226 Invert Tree | 06-oop-basics | recursive swap | tree |
| 7 | 242 Valid Anagram | 27-string-handling | `int[26]` | string freq |
| 8 | 704 Binary Search | 04-methods | manual binary search | binary search |
| 9 | 733 Flood Fill | 05-arrays-strings | DFS `int[][]` | graph DFS |
| 10 | 235 LCA of BST | 06-oop-basics | BST iteration | BST |
| 11 | 110 Balanced Tree | 06-oop-basics | height check recursion | tree |
| 12 | 141 Linked List Cycle | 06-oop-basics | slow/fast | Floyd's |
| 13 | 232 Queue with Stacks | 12-collections | `Deque<Integer>` | stack as queue |
| 14 | 278 First Bad Version | 04-methods | binary search | binary search |
| 15 | 383 Ransom Note | 27-string-handling | `int[26]` | string freq |
| 16 | 70 Climbing Stairs | 04-methods | `int` prev/curr | DP |
| 17 | 409 Longest Palindrome | 27-string-handling | `HashSet<Character>` | string |
| 18 | 206 Reverse Linked List | 06-oop-basics | prev/curr/next | linked list |
| 19 | 169 Majority Element | 12-collections | HashMap or Boyer-Moore | voting |
| 20 | 67 Add Binary | 05-arrays-strings | `StringBuilder`, carry | math |
| 21 | 543 Diameter of Tree | 06-oop-basics | post-order, height calc | tree |
| 22 | 876 Middle of List | 06-oop-basics | slow/fast | linked list |
| 23 | 104 Max Depth Tree | 06-oop-basics | `Math.max()` recursion | tree |
| 24 | 217 Contains Duplicate | 12-collections | `HashSet.add()` | set |
| 25 | 53 Maximum Subarray | 05-arrays-strings | Kadane's algorithm | DP |
| 26 | 57 Insert Interval | 12-collections | linear scan | interval |
| 27 | 542 01 Matrix | 05-arrays-strings | multi-source BFS | BFS |
| 28 | 973 K Closest Points | 06-priority-queue | `PriorityQueue<int[]>` | heap |
| 29 | 3 Longest Substring | 27-string-handling | `HashMap<Character, Integer>` | sliding window |
| 30 | 15 3Sum | 05-arrays-strings | `Arrays.sort()` + two-pointer | two-pointer |
| 31 | 102 Level Order | 06-oop-basics | `Deque<TreeNode>` BFS | tree BFS |
| 32 | 133 Clone Graph | 06-oop-basics | `HashMap<Node, Node>` | graph |
| 33 | 150 Reverse Polish | 03-control-flow | `Deque<Integer>` | stack |
| 34 | 207 Course Schedule | 12-collections | indegree array | topological sort |
| 35 | 208 Implement Trie | 06-oop-basics | `TrieNode[26]` | trie |
| 36 | 322 Coin Change | 12-collections | `Arrays.fill(dp, INF)` | DP |
| 37 | 238 Product Except Self | 05-arrays-strings | prefix/suffix | array |
| 38 | 295 Find Median | 06-priority-queue | two-heap | heap |
| 39 | 56 Merge Intervals | 12-collections | `Arrays.sort()` | interval |
| 40 | 236 LCA Binary Tree | 06-oop-basics | recursive LCA | tree |
| 41 | 981 Time KV Store | 05-treemap-treeset | `TreeMap.floorKey()` | binary search |
| 42 | 124 Max Path Sum | 06-oop-basics | post-order recursion | tree |
| 43 | 5 Longest Palindromic | 27-string-handling | expand around center | string |
| 44 | 76 Min Window Substring | 27-string-handling | `HashMap<Character, Integer>` | sliding window |
| 45 | 297 Serialize Tree | 35-serialization | `StringBuilder` + BFS | tree |
| 46 | 1152 Analyze History | 12-collections | `HashMap<String, List<String>>` | map |
| 47 | 42 Trapping Rain Water | 05-arrays-strings | two-pointer, left/right max | two-pointer |
| 48 | 127 Word Ladder | 27-string-handling | BFS on word transformations | graph BFS |
| 49 | 773 Sliding Puzzle | 05-arrays-strings | BFS on board state | graph BFS |
| 50 | 224 Basic Calculator | 03-control-flow | `Deque<Integer>` + sign tracking | stack |

---

## Java-Specific Study Plans

### 4-Week "Java Interview Crash Course"

| Week | Focus | Labs | Problems |
|------|-------|------|----------|
| 1 | Collections + Streams | 12-collections, 13-streams, 14-lambdas | 10 Blind 75 |
| 2 | Concurrency + Threading | 16-concurrency, 17-virtual-threads, 41-threading-deep-dive | 10 Blind 75 |
| 3 | Modern Java + API Mastery | 21-java-21-features, 22-records, 23-sealed-classes, 25-optional | 10 Blind 75 |
| 4 | Mock Interviews + Speed | All per-lab MOCK_INTERVIEW.md | Full Blind 75 review |

### 8-Week "Java Deep Dive"

| Week | Focus | Labs | Problems |
|------|-------|------|----------|
| 1-2 | Collections Deep Dive | collections-deep (01-10) | 3 NeetCode array/hash |
| 3-4 | Concurrency Deep Dive | concurrency-deep (01-10) | 3 NeetCode concurrency |
| 5-6 | JVM Deep Dive | jvm-deep (01-10) | 2 NeetCode DP |
| 7 | Memory + Performance | memory-deep (01-05), 37-performance-profiling | 2 NeetCode system design |
| 8 | Mock Interviews | All MODULE_INTERVIEW_GUIDE.md files | Full mock loop |

### 12-Week "Java Mastery"

| Weeks | Focus | Labs | NeetCode Progress |
|-------|-------|------|-------------------|
| 1-4 | Java Deep Dive (8-week plan) | All core + deep-dive labs | NeetCode 150: Arrays, Strings, Hash |
| 5-6 | Reactive + Networking | 36-reactive-programming, 32-networking, reactive-deep | NeetCode 150: Trees, Graphs |
| 7-8 | Serialization + Testing | 35-serialization, 31-testing, testing-deep, serialization-deep | NeetCode 150: DP, Backtracking |
| 9-10 | Modern Java + Performance | modern-java-deep, performance-deep, 44-jit-compilation, 46-jvm-tuning | NeetCode 150: Remaining |
| 11 | System Design | SYSTEM_DESIGN_JAVA_MAPPING.md, SYSTEM_DESIGN_CHEATSHEET.md | Mock system design rounds |
| 12 | Full Mock Loops | All MOCK_INTERVIEW.md, COMPANY_INTERVIEW_GUIDE.md | 5 full mock interviews |

---

## Company Focus Areas

| Company | Primary Labs | Study Priority | Deep Dive Modules |
|---------|-------------|----------------|-------------------|
| **Google** | jvm-deep, concurrency-deep, 30-jvm-internals | JVM > Concurrency > Collections | 01-hashmap-internals, 02-concurrent-hashmap |
| **Amazon** | 12-collections, 13-streams, 16-concurrency | Collections > Streams > Performance | 04-arraylist-deep, 37-performance-profiling |
| **Meta** | 05-arrays-strings, 14-lambdas, 15-functional-programming | Streams > Functional > Testing | 31-testing, testing-deep |
| **Apple** | 38-memory-model, 49-off-heap-memory, 50-object-layout-memory | Memory > Off-heap > JVM | memory-deep (01-05) |
| **Oracle** | 30-jvm-internals, 43-class-loading-bytecode, 44-jit-compilation, 45-gc-deep-dive | JVM > Bytecode > GC > JLS | jvm-deep (01-10), 46-jvm-tuning |
| **Netflix** | 36-reactive-programming, 16-concurrency, 37-performance-profiling | Reactive > Concurrency > Cloud | reactive-deep, performance-deep |
| **Uber** | 32-networking, 16-concurrency, 35-serialization | Networking > Concurrency > Distributed | networking-deep, 42-locking-synchronization |
| **Stripe** | 21-java-21-features, 22-records, 25-optional, 12-collections | Modern Java > Collections > Correctness | modern-java-deep, 28-enums |
| **Bloomberg** | 12-collections, 16-concurrency, 41-threading-deep-dive | Data Structures > Concurrency > Low-latency | concurrency-deep, 06-priority-queue |
| **Palantir** | 35-serialization, 32-networking, 38-memory-model | Serialization > Distributed > Fault Tolerance | serialization-deep, networking-deep |
| **Microsoft** | 16-concurrency, 36-reactive-programming, 40-best-practices | Async > Concurrency > Azure Patterns | 48-structured-concurrency, reactive-deep |
