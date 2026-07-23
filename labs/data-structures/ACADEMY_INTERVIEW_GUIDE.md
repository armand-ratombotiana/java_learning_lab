# Data Structures Academy — Interview Preparation Guide

## Overview

This guide consolidates interview preparation strategies across major tech companies, focusing specifically on data structures. Master these patterns and you will be prepared for any DS-focused interview.

---

## Google Interview: How They Test Data Structures

Google interviews emphasize problem-solving process, scalability, and edge-case handling. DS questions appear in every round.

### Round Structure (Typical)

| Round | DS Focus | Example Problems |
|-------|----------|-----------------|
| Phone Screen | Arrays, Hash Tables, Strings | Two Sum, Group Anagrams, Longest Substring Without Repeating |
| Coding Round 1 | Trees, Graphs, Hash Maps | Serialize/Deserialize BST, Word Ladder, Alien Dictionary |
| Coding Round 2 | Arrays, Heaps, Tries | Median of Two Sorted Arrays, Top K Frequent Words, Design Autocomplete |
| System Design | DS choice justification | Design Google Docs (trie for autocomplete, CRDT for collaboration) |

### Problems by Round

**Phone Screen (45 min):**
- Two Sum (LC 1) — always start with brute force, optimize to HashMap
- Longest Substring Without Repeating Characters (LC 3) — sliding window + HashMap
- Container With Most Water (LC 11) — two-pointer explanation required

**Onsite Coding Round 1 (45 min):**
- Serialize and Deserialize Binary Tree (LC 297) — BFS vs DFS trade-off analysis
- Word Ladder (LC 127) — BFS on implicit graph, bidirectional search refinement
- Alien Dictionary (LC 269) — topological sort on character graph

**Onsite Coding Round 2 (45 min):**
- Median of Two Sorted Arrays (LC 4) — binary search partition, O(log(min(n,m)))
- Top K Frequent Words (LC 692) — min-heap of size K vs bucket sort
- Design Search Autocomplete (LC 642) — trie + top K queries with frequency

### Google-Specific Tips
- Always analyze time/space complexity unprompted
- Discuss trade-offs before coding
- Edge cases: empty inputs, single elements, duplicates, overflow
- Google values clean, readable code over clever one-liners
- Expect follow-ups that increase constraints (streaming data, memory limits)

---

## Microsoft Interview: DS Focus Areas

Microsoft interviews are structured, with emphasis on fundamentals and design.

| Area | DS Focus | Example Problems |
|------|----------|-----------------|
| Trees | Binary trees, BST, N-ary | LCA, Serialize, Validate BST, Right Side View |
| Linked Lists | Singly/doubly, cycle, reverse | LRU Cache, Reverse K-Group, Add Two Numbers |
| Design & Implementation | LRU Cache, LFU Cache | Design LRU (HashMap + DLL), Implement Min Stack |
| Arrays & Strings | Two-pointer, sliding window | Remove Duplicates, Trapping Rain Water, String to Integer |

### Microsoft-Specific Tips
- Microsoft asks "write production-quality code" — handle null, edge cases, use meaningful names
- They test recursion depth understanding (recursive vs iterative)
- `LinkedList` and `HashMap` internals are common Java-specific questions
- Expect system design rounds that involve choosing correct data structures

---

## Meta Interview: DS Patterns

Meta focuses heavily on hash maps, arrays, strings, and recursion.

| Pattern | Frequency | Key Problems |
|---------|-----------|-------------|
| HashMap + Arrays | Very High | Two Sum, Subarray Sum Equals K, Group Anagrams |
| Sliding Window | High | Longest Substring, Minimum Window Substring |
| Tree DFS/BFS | High | Binary Tree Right Side View, Path Sum III, Validate BST |
| Recursion | Moderate | Pow(x,n), Generate Parentheses, Letter Combinations |

### Meta-Specific Tips
- Meta asks variations of the same core problems — practice deeply, not broadly
- They test if you can communicate clearly while coding
- Time pressure is real: 2 problems in 45 minutes
- Follow-ups often involve space optimization from O(n) to O(1)

---

## Amazon Interview: DS for Scale

Amazon focuses on data structures that work at massive scale.

| Data Structure | Why Amazon Cares | Example |
|---------------|-----------------|---------|
| Bloom Filter | DynamoDB uses for partition existence checks | Design a bloom filter, choose hash functions |
| LRU Cache | Product recommendation caching | Implement LRU with O(1) get/put |
| Concurrent DS | High-throughput services | ConcurrentHashMap internals, lock-free queues |
| Trie | Autocomplete in search | Design autocomplete with top K suggestions |
| Segment Tree | Inventory/warehouse range queries | Range sum with updates |

### Amazon Leadership Principles and DS
- **Bias for Action**: Choose the simplest DS that meets requirements
- **Deliver Results**: Optimize for the bottleneck (usually time complexity)
- **Dive Deep**: Expect deep questions about HashMap internals, rehashing, collision resolution

### Amazon-Specific Tips
- Expect at least one design-a-data-structure question
- Bar raiser rounds often combine DS with system design
- Always discuss trade-offs between array and linked-list based structures
- Time/space analysis must include mention of real-world scaling

---

## Apple Interview: Memory-Constrained DS

Apple cares about embedded systems and memory efficiency.

| DS Topic | Apple Focus | Example |
|----------|------------|---------|
| Bit Arrays | Memory-efficient boolean storage | Implement a bitset, space analysis |
| Compact Structures | Packed arrays, cache-oblivious | Implement a rope for text editing |
| In-place Operations | Low memory overhead | Invert binary tree, reverse linked list |
| Circular Buffers | Audio/video processing | Ring buffer for camera frames |

### Apple-Specific Tips
- Memory footprint is more important than runtime for many problems
- Consider cache-line alignment and memory layout
- Embedded systems constraints: watch out for recursion depth, heap fragmentation
- Apple values visual problem-solving: draw data structure state

---

## Oracle Interview: DS for Enterprise

Oracle focuses on enterprise-grade, reliable, and concurrent data structures.

| Area | DS Focus | Example |
|------|----------|---------|
| B-Trees | Database indexing | B+ tree implementation, fan-out optimization |
| Hash Maps | Enterprise caching | ConcurrentHashMap, rehashing, load factor tuning |
| Bloom Filters | Distributed systems | HBase level filtering, false positive analysis |
| Trees | TreeMap/TreeSet internals | Red-Black tree rotations, insertion cases |

### Oracle-Specific Tips
- Deep Java-specific knowledge expected (collections framework internals)
- Concurrency: understand ConcurrentHashMap, CopyOnWriteArrayList
- Know the Java Memory Model and how it affects DS implementations
- Oracle values correctness and reliability over micro-optimization

---

## LeetCode Roadmap: Which DS to Study in What Order

### Phase 1: Foundation (Weeks 1-2)

| Order | Data Structure | Key Concepts | NeetCode 150 |
|-------|---------------|-------------|-------------|
| 1 | Arrays | Two-pointer, sliding window, prefix sum | Arrays & Hashing (9 problems) |
| 2 | Hash Tables | Frequency counting, collision resolution | Arrays & Hashing (included) |
| 3 | Strings | Substring search, palindrome | Strings section |

### Phase 2: Linked Structures (Week 3)

| Order | Data Structure | Key Concepts | NeetCode 150 |
|-------|---------------|-------------|-------------|
| 4 | Linked Lists | Fast/slow pointer, reversal, merging | Linked List (11 problems) |
| 5 | Stacks & Queues | Monotonic stack, deque, priority | Stack section |

### Phase 3: Trees & Heaps (Weeks 4-5)

| Order | Data Structure | Key Concepts | NeetCode 150 |
|-------|---------------|-------------|-------------|
| 6 | Trees | BFS, DFS, BST, LCA | Trees (14 problems) |
| 7 | Heaps | Top K, median, merge K | Heap / Priority Queue |

### Phase 4: Graphs & Advanced (Weeks 6-8)

| Order | Data Structure | Key Concepts | NeetCode 150 |
|-------|---------------|-------------|-------------|
| 8 | Graphs | BFS, DFS, topological, union-find | Graphs (10 problems) |
| 9 | Tries | Prefix search, autocomplete | Tries section |
| 10 | Advanced | Segment tree, BIT, LRU | Advanced |

### NeetCode 150 Progression
```
Arrays → Hash Tables → Strings → Linked Lists → Stacks → Trees → Heaps → Graphs → Tries → Advanced
(1-9)    (included)    (included) (11)           (included) (14)    (included)(10)      (included) 
```

---

## Study Plans

### 4-Week Accelerated Plan

| Week | Focus | Daily Commitment | Problems |
|------|-------|-----------------|----------|
| 1 | Arrays + Hash Tables + Strings | 3-4 hrs | 20 problems |
| 2 | Linked Lists + Stacks + Trees | 3-4 hrs | 25 problems |
| 3 | Heaps + Graphs + Tries | 4 hrs | 20 problems |
| 4 | Advanced + Mock Interviews | 4-5 hrs | 15 problems + 5 mocks |

### 8-Week Standard Plan

| Week | Focus | Problems |
|------|-------|----------|
| 1 | Arrays: two-pointer, sliding window | 10 |
| 2 | Hash Tables: frequency, caching | 8 |
| 3 | Linked Lists + Stacks | 10 |
| 4 | Trees: traversal, BST, LCA | 12 |
| 5 | Heaps + Tries | 8 |
| 6 | Graphs: BFS, DFS, topological | 10 |
| 7 | Advanced DS + Revision | 12 |
| 8 | Company-specific + Mock interviews | 15-20 |

### 12-Week Comprehensive Plan

| Week | Focus | Details |
|------|-------|---------|
| 1 | Arrays — two-pointer patterns | LC 1, 11, 15, 16, 26, 27, 42, 75, 167, 283 |
| 2 | Arrays — sliding window + prefix sum | LC 3, 53, 76, 209, 238, 325, 438, 560, 713, 904 |
| 3 | Hash Tables — collision, design | LC 49, 128, 136, 202, 217, 242, 349, 350, 387, 454 |
| 4 | Strings + Math | LC 5, 6, 7, 8, 13, 14, 28, 38, 43, 125 |
| 5 | Linked Lists | LC 2, 19, 21, 23, 24, 25, 82, 83, 86, 92, 138, 141, 142, 143, 147, 148, 160, 203, 206, 234, 237, 328, 445, 725, 817, 876, 1019, 1171, 1290, 1367 |
| 6 | Stacks & Queues | LC 20, 22, 32, 42, 71, 84, 85, 94, 103, 144, 145, 150, 155, 173, 224, 225, 227, 232, 239, 255, 272, 316, 331, 341, 385, 394, 402, 445, 456, 496, 503, 581, 636, 678, 682, 735, 739, 772, 844, 856, 880, 895, 901, 907, 921, 946, 1003, 1006, 1047, 1063, 1081, 1130, 1190, 1209, 1249, 1381, 1441, 1472, 1475, 1504, 1541, 1552, 1598, 1653, 1673, 1687, 1700, 1717, 1752, 1762, 1765, 1776, 1813, 1823, 1834, 1856, 1896, 1904, 1944, 1950, 1963, 1996, 2030, 2071, 2104, 2116, 2130, 2197, 2216, 2257, 2273, 2281, 2289, 2296, 2305, 2334, 2344, 2355, 2373, 2375, 2390, 2393, 2406, 2411, 2423, 2428, 2454, 2484, 2487, 2534, 2535, 2554, 2565, 2568, 2573, 2577, 2589, 2599, 2602, 2606, 2613, 2617, 2625, 2641, 2663, 2679, 2680, 2696, 2698, 2701, 2706, 2712, 2731, 2736, 2742, 2747, 2753, 2756, 2757, 2758, 2759, 2760, 2762, 2764, 2765, 2770, 2772, 2773, 2774, 2779, 2780, 2789, 2796, 2800 |
| 7 | Trees — BFS, DFS | LC 94, 95, 96, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 110, 111, 112, 113, 114, 116, 117, 124, 129, 144, 145, 156, 173, 199, 222, 226, 230, 235, 236, 250, 255, 257, 264, 270, 271, 272, 285, 297, 298, 314, 331, 333, 337, 366, 404, 426, 428, 429, 431, 437, 449, 450, 501, 508, 510, 513, 515, 536, 538, 543, 545, 549, 558, 559, 563, 572, 582, 589, 590, 606, 617, 623, 652, 653, 654, 655, 662, 663, 666, 669, 671, 684, 687, 700, 701, 742, 776, 783, 814, 834, 865, 872, 889, 894, 897, 919, 938, 951, 958, 965, 968, 971, 979, 987, 988, 993, 998, 1008, 1022, 1026, 1028, 1038, 1042, 1079, 1080, 1090, 1104, 1105, 1110, 1114, 1120, 1123, 1130, 1137, 1145, 1150, 1155, 1161, 1169, 1172, 1192, 1195, 1197, 1203, 1210, 1214, 1218, 1226, 1232, 1240, 1245, 1247, 1250, 1252, 1254, 1257, 1261, 1265, 1273, 1279, 1282, 1286, 1288, 1290, 1292, 1302, 1305, 1313, 1315, 1325, 1339, 1343, 1348, 1352, 1359, 1361, 1367, 1372, 1373, 1376, 1379, 1382, 1391, 1395, 1400, 1401, 1403, 1409, 1415, 1418, 1422, 1425, 1430, 1432, 1434, 1435, 1443, 1448, 1450, 1451, 1452, 1455, 1457, 1458, 1460, 1462, 1464, 1466, 1469, 1474, 1476, 1477, 1480, 1483, 1485, 1486, 1489, 1490, 1492, 1493, 1495, 1496, 1497, 1498, 1499, 1500 |
| 8 | Heaps + Tries | LC 23, 215, 218, 239, 253, 264, 295, 313, 347, 355, 358, 373, 378, 407, 436, 451, 480, 502, 505, 621, 632, 658, 659, 692, 703, 719, 743, 759, 767, 774, 778, 786, 787, 792, 793, 846, 855, 857, 864, 871, 882, 895, 973, 1046, 1054, 1057, 1086, 1090, 1094, 1102, 1116, 1121, 1129, 1135, 1146, 1152, 1163, 1167, 1174, 1181, 1183, 1186, 1188, 1191, 1195, 1199, 1201, 1202, 1206, 1215, 1217, 1220, 1222, 1228, 1233, 1235, 1239, 1241, 1242, 1244, 1246, 1248, 1249, 1253, 1254, 1256, 1262, 1263, 1266, 1268, 1271, 1272, 1274, 1275, 1278, 1281, 1283, 1284, 1285, 1286, 1287, 1289, 1293, 1296, 1297, 1298, 1299, 1300, 1301, 1305, 1306, 1307, 1311, 1312, 1314, 1316, 1320, 1322, 1325, 1326, 1328, 1329, 1330, 1331, 1333, 1334, 1335, 1337, 1338, 1340, 1341, 1342, 1343, 1344, 1345, 1348, 1349, 1350, 1351, 1352, 1353, 1354, 1356, 1357, 1358, 1360, 1361, 1362, 1363, 1366, 1367, 1368, 1370, 1371, 1373, 1375, 1376, 1377, 1378, 1380, 1382, 1383, 1385, 1386, 1387, 1388, 1389, 1390, 1391 |
| 9 | Graphs — BFS + DFS | LC 133, 200, 207, 210, 261, 286, 323, 332, 399, 417, 433, 463, 490, 505, 529, 542, 547, 559, 675, 684, 685, 690, 694, 695, 721, 733, 734, 743, 749, 752, 753, 765, 773, 778, 785, 787, 797, 802, 803, 815, 819, 827, 829, 834, 837, 839, 841, 842, 844, 847, 849, 851, 852, 853, 854, 855, 857, 862, 863, 864, 865, 866, 867, 868, 869, 870, 871, 872, 873, 874, 875, 876, 877, 878, 879, 880, 881, 882, 883, 884, 885, 886, 887, 888, 889, 890, 891, 892, 893, 894, 895, 896, 897, 898, 899, 900 |
| 10 | Graphs — advanced + Union-Find | LC 261, 323, 547, 684, 685, 721, 737, 765, 785, 886, 924, 928, 947, 952, 959, 990, 1061, 1101, 1102, 1135, 1168, 1202, 1258, 1263, 1283, 1319, 1361, 1391, 1489, 1562, 1569, 1579, 1584, 1627, 1631, 1632, 1697, 1722, 1724, 1766, 1779, 1782, 1858, 1866, 1905, 1932, 1970, 1971, 1998, 2001, 2032, 2049, 2076, 2092, 2097, 2101, 2127, 2128, 2157, 2179, 2192, 2204, 2217, 2226, 2236, 2258, 2261, 2262, 2307, 2312, 2316, 2320, 2334, 2343, 2356, 2368, 2370, 2371, 2376, 2382, 2392, 2398, 2401, 2408, 2415, 2421, 2425, 2428, 2430, 2435, 2440, 2455, 2458, 2461, 2463, 2466, 2467, 2471, 2473, 2477, 2480, 2482, 2484, 2492, 2493, 2498, 2503, 2508, 2512, 2513, 2518, 2521, 2523, 2526, 2529, 2533, 2541, 2544, 2547, 2549, 2550, 2554, 2559, 2560, 2561, 2565, 2569, 2570, 2573, 2575, 2577, 2579, 2581, 2583, 2585, 2588, 2590, 2591, 2593, 2594, 2598, 2599, 2600, 2603, 2604, 2607, 2608, 2609, 2610, 2611, 2612, 2615, 2617, 2618, 2620, 2622, 2625, 2627, 2629, 2630, 2631, 2632, 2634, 2636, 2637, 2639, 2642, 2643, 2644, 2645, 2646, 2647, 2651, 2652, 2653, 2658, 2661, 2662, 2664, 2665, 2666, 2667, 2668, 2670, 2671, 2672, 2673, 2674, 2675, 2680, 2685, 2686, 2687, 2688, 2690, 2691, 2692, 2693, 2694, 2695, 2696, 2697, 2698, 2699, 2700, 2701, 2702, 2703, 2704, 2706, 2707, 2708, 2709, 2710, 2711, 2713, 2714, 2715, 2716, 2717, 2718, 2719, 2720, 2721, 2722, 2723, 2724, 2725, 2726 |
| 11 | Advanced DS: LRU, Segment Tree, BIT | LC 146, 208, 211, 212, 218, 307, 308, 315, 327, 352, 354, 381, 432, 460, 493, 588, 604, 642, 676, 677, 715, 716, 729, 731, 792, 795, 846, 855, 880, 895, 900, 901, 911, 915, 919, 933, 937, 953, 954, 955, 956, 957, 958, 960, 961, 962, 963, 964, 965, 966, 967, 968, 969, 970, 971, 972, 973, 974, 975, 976, 977, 978, 979, 980, 981, 982, 983, 984, 985, 986, 987, 988, 989, 990, 991, 992, 993, 994, 995, 996, 997, 998, 999, 1000 |
| 12 | Mock interviews + Company-specific | Full-length mocks for target company |

---

## Resources

### Books
| Book | Coverage | Skill Level |
|------|----------|-------------|
| Introduction to Algorithms (CLRS) | Complete theoretical coverage of all DS | Advanced |
| Algorithm Design Manual (Skiena) | War stories, practical advice | Intermediate |
| Data Structures and Algorithms in Java (Goodrich) | Java-specific implementations | Beginner-Intermediate |
| Algorithms (Sedgewick) | Elegant implementations, visual | Intermediate |
| Cracking the Coding Interview (McDowell) | Interview-focused DS review | All levels |

### Online Courses
| Course | Platform | Focus |
|--------|----------|-------|
| MIT 6.006 Introduction to Algorithms | MIT OCW | Core DS + algorithms |
| Princeton Algorithms Part I & II | Coursera (Sedgewick) | Java implementations |
| Data Structures in Java | AlgoExpert | Interview-specific |
| Grokking the Coding Interview | DesignGurus | Pattern-based |

### Websites & Tools
| Resource | Use Case |
|----------|----------|
| LeetCode | Primary practice platform |
| NeetCode.io | Curated problem lists + video solutions |
| Visualgo.net | DS visualization |
| USACO Guide | Competitive DS training |
| GeeksforGeeks | Quick reference, Java implementations |
| HackerRank Data Structures | Topic-specific practice |

### YouTube Channels
| Channel | Best For |
|---------|----------|
| NeetCode | Clean pattern-based explanations |
| Back To Back SWE | Deep theoretical dives |
| Tech Dose | Problem walkthroughs |
| Kevin Naughton Jr. | LC problem discussions |
| CS Dojo | Beginner-friendly DS concepts |

---

## Final Checklist

Before your interview, ensure you can:

- [ ] Implement HashMap with collision resolution from scratch
- [ ] Write BFS and DFS for trees and graphs
- [ ] Implement LRU Cache in under 20 minutes
- [ ] Code a trie with insert/search/prefix methods
- [ ] Explain Red-Black tree invariants and rotations
- [ ] Compare ArrayList vs LinkedList vs ArrayDeque in detail
- [ ] Implement union-find with path compression and union by rank
- [ ] Solve any two-pointer problem quickly
- [ ] Handle edge cases: null, empty, single element, duplicates
- [ ] Analyze time and space complexity without prompting
