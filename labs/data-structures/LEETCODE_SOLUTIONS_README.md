# LeetCode Solutions — How to Practice Effectively

## How to Use LeetCode Effectively

### The Three-Phase Approach

**Phase 1: Topic Mastery (Weeks 1-4)**
Focus on one data structure at a time. Complete all easy problems, then medium problems for that topic. Do not skip to the next topic until you can solve medium problems comfortably.

```
Week 1: Arrays (Easy: 10, Medium: 15)
Week 2: Linked Lists (Easy: 5, Medium: 10)
Week 3: Trees (Easy: 8, Medium: 12)
Week 4: Hash Tables + Stacks (Easy: 5, Medium: 10)
```

**Phase 2: Pattern Recognition (Weeks 5-8)**
Solve problems grouped by pattern (two-pointer, sliding window, BFS, DFS, etc.) rather than by data structure. Learn to recognize which pattern applies to a given problem.

**Phase 3: Mixed Practice (Weeks 9-12)**
Solve random problems from company-specific lists. Time yourself. Do mock interviews. Build stamina for 45-minute sessions.

### Problem Selection Strategy

| Your Level | Start With | Progress To |
|-----------|------------|-------------|
| Beginner | Easy problems in 1 topic | All easy in that topic |
| Intermediate | Easy + Medium in 2-3 topics | Medium in all topics |
| Advanced | Medium + Hard | Company-specific Hard |
| Expert | Random Hard + Contest | System design rounds |

### When to Look at Solutions

1. **10-minute rule**: If stuck for 10 minutes without progress, look at the solution
2. **Understand, don't memorize**: After viewing the solution, close it and re-implement from scratch
3. **Variation practice**: Solve 2-3 variations of the same problem pattern
4. **Spaced repetition**: Revisit solved problems after 1 day, 1 week, 1 month

---

## Spaced Repetition System for Problem Review

### Review Schedule

| Review # | When | What to Review |
|---------|------|---------------|
| 1 | Same day (evening) | Problems solved that morning |
| 2 | Next day | Problems from 2 days ago |
| 3 | 3 days later | Problems from 5 days ago |
| 4 | 1 week later | Problems from 12 days ago |
| 5 | 2 weeks later | Problems from 26 days ago |
| 6 | 1 month later | Problems from 2 months ago |

### Tracking System

Create a spreadsheet with columns:

| Problem | Date Solved | Pattern | Difficulty | Rev #1 | Rev #2 | Rev #3 | Notes |
|---------|------------|---------|------------|--------|--------|--------|-------|
| Two Sum | 2024-01-01 | HashMap | Easy | 2024-01-01 | 2024-01-02 | 2024-01-04 | Watch for duplicates |
| ... | | | | | | | |

### Active Recall Process

When reviewing a problem:
1. Do NOT look at your previous solution first
2. Attempt to solve from scratch with a timer
3. Compare with your previous solution
4. Note any improvements or different approaches
5. Rate your recall (1-5):
   - 5: Solved perfectly, faster than before
   - 4: Solved with minor hesitation
   - 3: Solved but needed hints
   - 2: Could not solve, had to study solution
   - 1: Could not solve, pattern still unfamiliar

If you rate 1-2, add the problem to your "needs more practice" list and review again in 1 day.

---

## How to Time-Box Practice Sessions

### Structured 2-Hour Session

| Phase | Duration | Activity |
|-------|----------|----------|
| Warm-up | 10 min | Solve 1 easy problem you have seen before |
| New Problem 1 | 30 min | Attempt medium problem |
| Review | 10 min | Study solution, understand patterns |
| New Problem 2 | 30 min | Attempt another medium |
| Review | 10 min | Study solution, write notes |
| Spaced Repetition | 20 min | Review 2-3 older problems from your spreadsheet |
| Summary | 10 min | Update tracking, log patterns learned |

### 1-Hour Quick Session

| Phase | Duration | Activity |
|-------|----------|----------|
| New Problem | 30 min | Attempt 1 medium problem |
| Review | 15 min | Study solution, note key insights |
| Spaced Review | 15 min | Review 1-2 old problems |

### 30-Min Micro Session

| Phase | Duration | Activity |
|-------|----------|----------|
| Problem Attempt | 20 min | Solve 1 easy or previously attempted medium |
| Solution Study | 10 min | Compare, log insights |

### Company-Specific Practice

| Company | Session Type | Focus |
|---------|-------------|-------|
| Google | 60 min | 2 medium problems, heavy complexity analysis |
| Meta | 45 min | 2 problems, one easy one medium, fast coding |
| Amazon | 60 min | 1 medium + 1 medium-hard, focus on scalability |
| Microsoft | 45 min | 2 problems, clean production code |
| Apple | 60 min | 1 design DS + 1 coding, focus on memory |

---

## How to Review Solutions After Solving

### The Four-Part Review

**1. Compare Approaches**
After solving, look at the official solution and top-voted answers:

| Aspect | Your Solution | Optimal Solution |
|--------|---------------|------------------|
| Time complexity | | |
| Space complexity | | |
| Code length | | |
| Readability | | |
| Edge cases handled | | |

**2. Identify Pattern Category**
Tag each problem with its pattern:
- Is this two-pointer, sliding window, BFS, DFS, DP?
- What other problems use this same pattern?
- What are the distinguishing features of this pattern?

**3. Explore Variations**
For each problem, think of 2-3 variations:
- What if the input is sorted differently?
- What if you need to return all solutions instead of one?
- What if the data arrives as a stream?
- What if there are negative numbers?
- What if duplicates are allowed?

**4. Write Down the Key Insight**
In one sentence, capture the essence of the solution:
```
Two Sum: "HashMap stores complement values for O(n) lookup"
LRU Cache: "HashMap<Key, Node> for O(1) get, doubly linked list for O(1) eviction"
```

### Solution Quality Checklist

Before considering a problem "done", verify:

- [ ] Brute force solution understood and analyzed
- [ ] Optimal solution coded and tested
- [ ] Time and space complexity stated clearly
- [ ] Edge cases handled (empty, null, single, duplicates, overflow)
- [ ] At least one alternative approach considered
- [ ] A one-sentence explanation of the core pattern written
- [ ] The problem added to spaced repetition tracking

---

## Common Mistakes in LeetCode Preparation

### Mistake 1: Jumping to Hard Problems Too Early

**The Problem**: Beginners attempt LC Hard problems after only solving 10-20 easy problems.

**Why It Fails**: Hard problems assume mastery of multiple patterns. You cannot recognize which patterns to combine without pattern fluency.

**Fix**: Follow the 80/20 rule — 80% of your practice should be at your current level, 20% stretching to the next level.

| Current Level | Practice Mix |
|-------------|-------------|
| 0-20 problems solved | 90% Easy, 10% Medium |
| 20-50 problems solved | 60% Easy, 35% Medium, 5% Hard |
| 50-100 problems solved | 20% Easy, 60% Medium, 20% Hard |
| 100+ problems solved | 10% Easy, 50% Medium, 40% Hard |

### Mistake 2: Memorizing Solutions Instead of Patterns

**The Problem**: You can solve LC 1 Two Sum but not LC 167 Two Sum II or LC 15 3Sum.

**Fix**: After solving a problem, identify the abstract pattern. Practice 2-3 more problems using the same pattern immediately. The pattern is what transfers, not the specific code.

### Mistake 3: Not Simulating Interview Conditions

**The Problem**: You solve problems with unlimited time, reference materials, and no pressure.

**Fix**: Twice a week, do a timed session:
- No autocomplete
- No pausing to research
- Verbalize your thought process (record yourself)
- 45-minute hard stop, even if incomplete

### Mistake 4: Writing Code Before Thinking

**The Problem**: You start coding within 30 seconds of seeing the problem.

**Fix**: Use the first 5 minutes to:
1. Restate the problem in your own words
2. Ask clarifying questions (input size, negative numbers, duplicates, memory limits)
3. Discuss naive approach and analyze its complexity
4. Design the optimal approach on paper / whiteboard
5. Only then write code

### Mistake 5: Ignoring Company-Specific Patterns

**The Problem**: You practice 500 random problems but still fail interviews because you don't know what your target company asks.

**Fix**: Once you have basic pattern fluency (50+ problems), research your target company:
- Google: Focus on optimization, DP, graphs, trees, and follow-ups
- Meta: Focus on arrays, strings, trees, recursion, time management
- Amazon: Focus on design DS, scalability discussions, LRU, Bloom filter
- Microsoft: Focus on clean code, linked lists, trees, recursion vs iteration

### Mistake 6: Not Analyzing Complexity

**The Problem**: You can solve the problem but cannot explain why it is optimal.

**Fix**: For every problem, state time and space complexity before coding. Justify why a better solution does not exist. Common justifications:
- "We must examine each element once, so omega(n) is the lower bound"
- "Sorting is required, so O(n log n) is optimal"
- "We need to compare all pairs in worst case, so O(n^2) is necessary"

### Mistake 7: Neglecting Edge Cases

**The Problem**: Code passes sample tests but fails hidden tests.

**Fix**: Before running the first test, mentally walk through:

| Edge Case | Example |
|-----------|---------|
| Empty input | `[]`, `""`, null |
| Single element | `[1]` |
| Two elements | `[1, 2]` |
| All same values | `[1,1,1,1]` |
| Already sorted | `[1,2,3,4]` |
| Reverse sorted | `[4,3,2,1]` |
| Negative numbers | `[-1, -2, -3]` |
| Mixed positive/negative | `[-1, 0, 1]` |
| Overflow | `Integer.MAX_VALUE`, `Integer.MIN_VALUE` |
| Duplicates | `[1,2,2,3]` |

### Mistake 8: Not Practicing Communication

**The Problem**: You can solve perfectly in silence but freeze when asked to explain.

**Fix**: Practice out loud:
- Echo: Repeat what the interviewer says
- Think aloud: "I'm considering using a HashMap because we need O(1) lookup..."
- Ask: "Should I handle the case where..."
- Summarize: "So my approach is to iterate once, storing complements in a HashMap..."

### Mistake 9: Solution Hoarding

**The Problem**: You have 300 solved problems but cannot recall how to solve any of them.

**Fix**: Quality over quantity. Solve 100 problems deeply with spaced repetition rather than 300 problems superficially.

### Mistake 10: Not Reviewing Your Performance

**The Problem**: You finish a problem and immediately move to the next without reflection.

**Fix**: After each problem, spend 5 minutes on:
- What was the key insight?
- What mistakes did I make?
- Could I have solved it differently?
- What similar problems exist?
- Log the problem in your tracking system

---

## Recommended Problem Progression

### Beginner Path (0-30 Days)

| Step | Problems | Goal |
|------|----------|------|
| Arrays basics | LC 1, 26, 27, 35, 53, 66, 88, 108, 118, 121, 136, 169, 217, 242, 268, 283, 344, 349, 350, 387 | Understand array operations |
| Hash Table basics | LC 1, 3, 49, 136, 202, 204, 205, 217, 219, 242, 290, 299, 349, 350, 383, 387, 389, 409, 438, 447, 451, 463, 496, 500, 506, 509, 535, 554, 560, 575, 594, 599, 604, 609, 624, 632, 645, 648, 652, 653, 657, 676, 677, 681, 690, 692, 694, 697, 705, 706, 710, 711, 718, 720, 722, 723, 726, 729, 734, 735, 737, 739, 742, 743, 744, 745, 746, 747, 748, 749, 750, 751, 752 | HashMap/Set patterns |
| String basics | LC 5, 6, 7, 8, 13, 14, 20, 28, 38, 49, 58, 67, 71, 91, 93, 125, 151, 157, 159, 161, 165, 168, 171, 179, 186, 205, 242, 246, 249, 257, 266, 267, 271, 273, 290, 293, 299, 306, 316, 318, 320, 331, 336, 340, 344, 345, 358, 383, 385, 387, 389, 394, 408, 409, 415, 418, 420, 422, 423, 434, 443, 459, 468, 481, 482, 520, 521, 522, 524, 527, 536, 537, 539, 541, 544, 551, 553, 555, 556, 557, 564, 583, 591, 604, 606, 609, 616, 624, 632, 635, 647, 649, 657, 678, 680, 681, 683, 686, 692, 696, 709, 722, 730, 736, 758 | String manipulation |

### Intermediate Path (30-60 Days)

| Step | Problems | Goal |
|------|----------|------|
| Linked Lists | LC 2, 19, 21, 23, 24, 25, 82, 83, 86, 92, 138, 141, 142, 143, 147, 148, 160, 203, 206, 234, 237, 328, 445, 725, 817, 876, 1019, 1171, 1290, 1367 | Pointer manipulation |
| Trees | LC 94, 96, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 110, 111, 112, 113, 114, 116, 117, 124, 129, 130, 133, 144, 145, 156, 173, 199, 200, 207, 208, 210, 222, 226, 230, 235, 236, 250, 255, 257, 261, 269, 270, 271, 272, 285, 297, 298, 310, 314, 323, 329, 331, 332, 333, 337, 339, 341, 366, 404, 417, 426, 428, 429, 431, 437, 449, 450, 501, 508, 510, 513, 515, 536, 538, 543, 545, 549, 558, 559, 563, 572, 582, 589, 590, 606, 617, 623, 652, 653, 654, 655, 662, 663, 666, 669, 671, 684, 687, 700, 701, 742, 776, 783, 814, 834, 865, 872, 889, 894, 897, 919, 938, 951, 958, 965, 968, 971, 979, 987, 988, 993, 998, 1008, 1022, 1026, 1028, 1038, 1042, 1079, 1080, 1090, 1104, 1105, 1110, 1114, 1120, 1123, 1130, 1137, 1145, 1150, 1155, 1161, 1169, 1172, 1192, 1195, 1197, 1203, 1210, 1214, 1218, 1226, 1232, 1240, 1245, 1247, 1250, 1252, 1254, 1257, 1261, 1265, 1273, 1279, 1282, 1286, 1288, 1290, 1292, 1302, 1305, 1313, 1315, 1325, 1339, 1343, 1348, 1352, 1359, 1361, 1367, 1372, 1373, 1376, 1379, 1382, 1391, 1395, 1400, 1401, 1403, 1409, 1415, 1418, 1422, 1425, 1430, 1432, 1434, 1435, 1443, 1448, 1450, 1451, 1452, 1455, 1457, 1458, 1460, 1462, 1464, 1466, 1469, 1474, 1476, 1477, 1480, 1483, 1485, 1486, 1489, 1490, 1492, 1493, 1495, 1496, 1497, 1498, 1499, 1500 | Tree traversals |

### Advanced Path (60-90 Days)

| Step | Problems | Goal |
|------|----------|------|
| Heaps + Tries | LC 23, 146, 208, 211, 212, 215, 218, 239, 253, 264, 295, 307, 308, 313, 315, 327, 347, 352, 354, 358, 373, 378, 381, 432, 436, 451, 460, 480, 493, 502, 505, 588, 604, 621, 632, 642, 648, 658, 659, 676, 677, 692, 703, 715, 716, 719, 729, 731, 743, 759, 767, 774, 778, 786, 787, 792, 793, 795, 846, 855, 857, 864, 871, 882, 895, 900, 901, 911, 915, 919, 933, 937, 953, 954, 955, 956, 957, 958, 960, 961, 962, 963, 964, 965, 966, 967, 968, 969, 970, 971, 972, 973, 974, 975, 976, 977, 978, 979, 980, 981, 982, 983, 984, 985, 986, 987, 988, 989, 990, 991, 992, 993, 994, 995, 996, 997, 998, 999, 1000 | Advanced DS |

### Hard Mode (90-180 Days)

| Step | Problems | Goal |
|------|----------|------|
| Graphs + Advanced | LC 127, 133, 200, 207, 210, 261, 269, 286, 310, 323, 329, 332, 399, 417, 433, 463, 490, 505, 542, 547, 675, 684, 685, 690, 694, 695, 721, 733, 737, 743, 749, 752, 753, 765, 773, 778, 785, 787, 797, 802, 803, 815, 819, 827, 829, 834, 837, 839, 841, 842, 844, 847, 849, 851, 852, 853, 854, 855, 857, 862, 863, 864, 865, 866, 867, 868, 869, 870, 871, 872, 873, 874, 875, 876, 877, 878, 879, 880, 881, 882, 883, 884, 885, 886, 887, 888, 889, 890, 891, 892, 893, 894, 895, 896, 897, 898, 899, 900, 924, 928, 947, 952, 959, 990, 1061, 1101, 1102, 1135, 1168, 1202, 1258, 1263, 1283, 1319, 1361, 1391, 1489, 1562, 1569, 1579, 1584, 1627, 1631, 1632, 1697, 1722, 1724, 1766, 1779, 1782, 1858, 1866, 1905, 1932, 1970, 1971, 1998, 2001, 2032, 2049, 2076, 2092, 2097, 2101, 2127, 2128, 2157, 2179, 2192, 2204, 2217, 2226, 2236, 2258, 2261, 2262, 2307, 2312, 2316, 2320, 2334, 2343, 2356, 2368, 2370, 2371, 2376, 2382, 2392, 2398, 2401, 2408, 2415, 2421, 2425, 2428, 2430, 2435, 2440, 2455, 2458, 2461, 2463, 2466, 2467, 2471, 2473, 2477, 2480, 2482, 2484, 2492, 2493, 2498, 2503, 2508, 2512, 2513, 2518, 2521, 2523, 2526, 2529, 2533, 2541, 2544, 2547, 2549, 2550, 2554, 2559, 2560, 2561, 2565, 2569, 2570, 2573, 2575, 2577, 2579, 2581, 2583, 2585, 2588, 2590, 2591, 2593, 2594, 2598, 2599, 2600, 2603, 2604, 2607, 2608, 2609, 2610, 2611, 2612, 2615, 2617, 2618, 2620, 2622, 2625, 2627, 2629, 2630, 2631, 2632, 2634, 2636, 2637, 2639, 2642, 2643, 2644, 2645, 2646, 2647, 2651, 2652, 2653, 2658, 2661, 2662, 2664, 2665, 2666, 2667, 2668, 2670, 2671, 2672, 2673, 2674, 2675, 2680, 2685, 2686, 2687, 2688, 2690, 2691, 2692, 2693, 2694, 2695, 2696, 2697, 2698, 2699, 2700, 2701, 2702, 2703, 2704, 2706, 2707, 2708, 2709, 2710, 2711, 2713, 2714, 2715, 2716, 2717, 2718, 2719, 2720, 2721, 2722, 2723, 2724, 2725, 2726 | Graph algorithms |

---

## Final Tips

### Before the Interview

- **Week before**: Only review material you have already studied. Do not learn new topics.
- **Day before**: Light practice (2-3 easy problems). Get good sleep.
- **Morning of**: Review your one-sentence pattern summaries. No coding.
- **During interview**: Breathe. Talk through your approach. Ask clarifying questions. Write clean code.

### Resources for Continued Practice

- **NeetCode.io**: Curated problem lists with video solutions
- **AlgoExpert**: Company-specific problem sets
- **LeetCode Discuss**: Company interview experiences (filter by company tag)
- **LeetCode Premium**: Company problem frequency data
- **InterviewBit**: Topic-wise timed practice
- **Pramp / interviewing.io**: Free mock interviews with peers
