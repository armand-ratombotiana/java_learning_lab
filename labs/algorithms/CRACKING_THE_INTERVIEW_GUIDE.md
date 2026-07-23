# Cracking the Coding Interview — Algorithms Academy

## The Interview Process: What to Expect

### Phone Screen (30-45 min)
- **Format**: Video call with shared code editor
- **Content**: 1-2 medium algorithm problems
- **Focus**: Problem-solving approach, communication, basic optimization
- **Pass Rate**: ~30-50% advance to onsite

### Onsite (4-6 rounds, 45-60 min each)
- **Algorithm Rounds** (2-4): DP, graphs, recursion, string algorithms
- **System Design** (1-2): Applying algorithms at scale
- **Behavioral** (1): STAR method, leadership, conflict resolution
- **Coding** (1): Implementation-heavy algorithm problem

### Virtual Onsite
- All rounds remote via Zoom/Google Meet
- Code on your own machine with shared screen
- Practice without autocomplete to simulate real conditions

---

## What Each Company Evaluates

| Company | Algorithm Depth | System Design | Behavioral | LeetCode Difficulty |
|---------|----------------|---------------|------------|-------------------|
| Google | ★★★★★ | ★★★★ | ★★★ | Hard |
| Meta | ★★★★ | ★★★ | ★★ | Medium-Hard |
| Amazon | ★★★★ | ★★★★★ | ★★★★★ | Medium |
| Microsoft | ★★★★ | ★★★★ | ★★★ | Medium |
| Apple | ★★★★ | ★★★ | ★★★ | Medium |
| Netflix | ★★★★ | ★★★★★ | ★★ | Medium |
| Stripe | ★★★★★ | ★★★★ | ★★ | Hard |
| Palantir | ★★★★★ | ★★ | ★★★ | Hard |

---

## The 5-Step Method for Any Algorithm Problem

### Step 1: Understand (3-5 min)
- Restate the problem — "So I need to find the shortest path in a weighted graph..."
- Ask clarifying: directed/undirected? positive/negative weights? cycle possible?
- Walk through 1-2 examples manually
- Identify the paradigm: DP? Greedy? Graph? Divide & Conquer?

### Step 2: Brute Force (3-5 min)
- State the naive approach clearly
- "The simplest way is to try all combinations, which is O(2^n)..."
- Analyze worst-case time and space
- **Identify the bottleneck**

### Step 3: Optimize (5-10 min)
- **DP**: Is there optimal substructure? Overlapping subproblems?
- **Greedy**: Does local choice lead to global optimum?
- **Divide & Conquer**: Can we split the problem in half?
- **Graph**: BFS for unweighted shortest, Dijkstra for weighted
- State the optimized complexity before coding
- **Get buy-in**: "Would you like me to proceed with this approach?"

### Step 4: Implement (15-20 min)
- Write clean, production-quality code
- Define helper functions for clarity
- Handle base cases and edge cases first
- Use meaningful variable names
- Talk through the code as you write

### Step 5: Test (5-10 min)
- Walk through your manual example step by step
- Test edge cases: empty, single, negatives, overflow, large values
- Re-analyze time and space complexity
- Discuss follow-ups: what if input is 1000x larger? What if it's a stream?

---

## Company-Specific Algorithm Tips

### Google — Algorithm Depth
- **Focus**: DP, graphs, binary search on answer, complex recursion
- **Pattern**: Starts easy, adds constraints until solution is optimal
- **Expect**: "Can you do better?" multiple times
- **Prepare**: Master DP (1D, 2D, interval, tree DP), graph algorithms, string algorithms
- **Tip**: Google loves binary search on answer — practice LC 4, 778, 875, 1011, 1283

### Meta — Speed and Breadth
- **Focus**: Arrays, strings, recursion, DP, backtracking
- **Pattern**: 2 problems in 45 minutes — time management is critical
- **Expect**: The first problem is easy (5-10 min), second is medium (15-20 min)
- **Prepare**: Be fast on arrays, strings, recursion. Practice 20-minute solves
- **Tip**: Meta reuses the same core problems — focus on high-frequency tagged problems

### Amazon — Scalability
- **Focus**: Greedy, DP, graph algorithms applied to real-world problems
- **Pattern**: Problems framed as business scenarios (delivery, inventory, recommendations)
- **Expect**: "What if this runs on 1000 servers?"
- **Prepare**: Greedy algorithms, Dijkstra, MST, task scheduling
- **Tip**: Always discuss scalability — mention distributed algorithms if appropriate

### Microsoft — Fundamentals
- **Focus**: Sorting, searching, recursion, complexity analysis
- **Pattern**: Clean implementation of well-known algorithms
- **Expect**: "Write merge sort from scratch" or "Implement binary search with duplicates"
- **Prepare**: Know sorting internals, recursion depth limits, iterative vs recursive
- **Tip**: Microsoft values correctness over cleverness — test your code thoroughly

### Apple — Memory-Constrained
- **Focus**: In-place algorithms, bit manipulation, recursion with stack limits
- **Pattern**: Problems that test understanding of memory hierarchy
- **Expect**: "Solve this in O(1) extra space" or "Use bit-level operations"
- **Prepare**: In-place array operations, bit manipulation tricks, iterative DFS/BFS
- **Tip**: Apple interviewers often run your code — make sure it compiles and passes

---

## Resume Preparation Tips

### Algorithm-Focused Resume
- **Highlight algorithmic work**: "Designed an O(n log n) scheduling algorithm that reduced latency by 60%"
- **Show complexity awareness**: "Optimized from O(n^2) to O(n) using dynamic programming"
- **Mention relevant coursework**: "Advanced Algorithms (A+), Computational Complexity Theory"
- **Include competitive programming**: "Ranked in top 5% of Codeforces, solved 500+ LeetCode problems"

### Quantify Everything
- "Reduced query time from 2s to 50ms by implementing a cache-efficient algorithm"
- "Designed a scheduling system handling 1M+ tasks/day with O(n log n) complexity"
- "Implemented concurrent merge sort achieving 4x speedup on 8-core system"

### Projects to Showcase
- Write a competitive programming blog or GitHub repo with solutions
- Implement a mini search engine (page rank, inverted index)
- Build an autocomplete system (trie, top-k heap)
- Implement a pathfinding visualizer (A*, Dijkstra, BFS comparison)

---

## Behavioral Questions: STAR Method

### Algorithm-Specific STAR Examples
- **Conflict**: "When my teammate proposed a greedy solution for a problem that needed DP, I..."
- **Trade-off**: "I had to choose between O(n) memory with HashMap and O(1) memory with sorting..."
- **Failure**: "My first solution was O(n^2) and failed on large input. I learned to analyze complexity upfront..."
- **Mentorship**: "I taught DP to my team using the climbing stairs analogy..."

### Amazon Leadership Principles for Algorithms
- **Customer Obsession**: "The O(n^2) algorithm was fine for 100 users but would fail at 1M — I redesigned it to O(n log n)"
- **Dive Deep**: "I analyzed the amortized cost of HashMap resizing and optimized initial capacity"
- **Deliver Results**: "By switching from recursion to iteration, I eliminated stack overflow in production"
- **Learn and Be Curious**: "I studied interval DP to solve the burst balloons problem and applied it to our scheduling system"

---

## Time Management During Interviews

### 45-Minute Algorithm Round
| Minute | Activity |
|--------|----------|
| 0-5 | Understand the problem, ask clarifying questions |
| 5-10 | Design approach: brute force → optimal, paradigm identification |
| 10-30 | Code the solution with clean implementation |
| 30-40 | Test with examples, handle edge cases |
| 40-45 | Complexity analysis, discuss follow-ups |

### Algorithm Recognition Time
| Paradigm | Recognition Time | Decision |
|----------|-----------------|----------|
| Sorting | < 30 sec | Sort then process |
| Binary Search | < 1 min | Is search space monotonic? |
| Greedy | < 2 min | Does local choice lead to global? |
| DP | < 3 min | Overlapping subproblems? |
| BFS/DFS | < 1 min | Graph or tree traversal? |
| Backtracking | < 2 min | Need to explore all configurations? |
| D&C | < 2 min | Can we split and combine? |

---

## How to Handle Rejection

### Algorithm Round Rejection Patterns
- **Couldn't identify the paradigm**: Practiced memorization, not pattern recognition
- **Couldn't optimize**: Only knew brute force, didn't study algorithmic improvements
- **Communication gap**: Solved silently, interviewer couldn't follow the thinking
- **Implementation bugs**: Off-by-one, wrong recursion base case, forgetting visited set

### Improvement Plan After Rejection
1. **Identify weak paradigms**: If you failed on DP, solve 30 DP problems
2. **Practice communication**: Record yourself solving problems aloud
3. **Do mock interviews**: Use Pramp, interviewing.io, or peer practice
4. **Study complexity analysis**: Be ready to analyze any solution instantly
5. **Retry in 6 months**: Most companies require a waiting period

### Mindset
- Rejection from Google/Meta/Amazon is normal — even ex-FAANG engineers face rejections
- The interview process has significant randomness — don't take it personally
- Each rejection identifies a specific skill to improve

---

## Negotiation Tips

### Know Algorithm Market Value
- Strong algorithm skills command premium compensation
- Top algorithm engineers (competitive programming background) are rare
- Companies pay more for algorithmic depth (Google, Palantir, Two Sigma, Jane Street)

### What to Negotiate
| Lever | Strategy |
|-------|----------|
| Competitive programming background | Highlight contest rankings, problem count |
| Publications (algorithms research) | Mention conference papers, patents |
| Multiple offers | The strongest negotiating position — be transparent |
| Specialized domain | Graph algorithms, ML algorithms, cryptography |

### Never
- Bluff about algorithmic skills (you'll be tested in interview)
- Accept the first offer — ask for 24-48 hours to consider
- Compare offers disparagingly ("Google's offer is better than yours")

---

## Study Plans

### 30-Day Intensive Plan
| Week | Focus | Daily | Target Problems |
|------|-------|-------|----------------|
| 1 | Sorting + Searching + Recursion | 4 hrs, 5 problems | LC 704, 912, 33, 50, 78 |
| 2 | DP (1D + 2D) | 4 hrs, 5 problems | LC 70, 198, 322, 1143, 72 |
| 3 | Graphs + Greedy | 4 hrs, 5 problems | LC 200, 207, 743, 55, 621 |
| 4 | Backtracking + Company-specific + Mocks | 5 hrs, 4 problems | LC 46, 78, 39, target company |

### 60-Day Standard Plan
| Weeks | Focus | Problems | Milestone |
|-------|-------|----------|-----------|
| 1-2 | Sorting + Searching + Recursion | 25 | O(n log n) fluent |
| 3-4 | DP (1D, 2D, Knapsack) | 30 | Pattern recognition ready |
| 5-6 | Graphs (BFS, DFS, Dijkstra, Topo) | 25 | Graph fluency |
| 7-8 | Greedy + Backtracking + D&C + Mocks | 20 | Interview ready |

### 90-Day Comprehensive Plan
| Weeks | Focus | Problems | Detail |
|-------|-------|----------|--------|
| 1-2 | Sorting + Binary Search | 20 | LC 34, 33, 153, 912, 69, 278, 875, 1011 |
| 3-4 | Recursion + Divide & Conquer | 15 | LC 50, 78, 46, 39, 169, 53, 215 |
| 5-6 | DP — 1D | 20 | LC 70, 198, 213, 322, 300, 416, 494 |
| 7-8 | DP — 2D + Intervals | 15 | LC 1143, 72, 10, 312, 516, 647 |
| 9-10 | Graphs | 25 | LC 200, 133, 207, 210, 743, 785, 684 |
| 11 | Greedy + Backtracking | 15 | LC 55, 56, 621, 46, 51, 78, 212 |
| 12-13 | String Algorithms + Advanced | 15 | LC 5, 28, 214, 647, 1044, 336 |
| 14 | Mock interviews + Review | 10+ mocks | Full-length simulations |

---

## Resources Organized by Company

### Google
- **LeetCode**: Filter by Google tag (highest frequency: LC 4, 10, 42, 124, 146, 200, 269, 297, 329, 642)
- **YouTube**: Google Coding Interview (Clément Mihailescu), Life at Google
- **Books**: Cracking the Coding Interview, System Design Interview (Alex Xu)
- **Courses**: MIT 6.006 (OCW), Stanford CS161

### Meta
- **LeetCode**: Filter by Meta tag (highest frequency: LC 1, 3, 15, 20, 49, 56, 76, 102, 121, 124, 200, 238)
- **YouTube**: Meta Engineering interviews, Coding Interview Prep
- **Books**: Cracking the Coding Interview, Elements of Programming Interviews

### Amazon
- **LeetCode**: Filter by Amazon tag (highest frequency: LC 1, 2, 5, 12, 15, 20, 42, 53, 73, 121, 138, 139, 146, 199, 200, 207, 212, 239, 240, 273)
- **Amazon Leadership Principles**: Know all 16, have stories for each
- **Books**: Working Backwards (Bryar & Carr)

### General
- **NeetCode 150**: Complete pattern-based algorithm study
- **Blind 75**: The 75 most common algorithm problems
- **AlgoExpert**: Company-specific problem sets with video solutions
- **Pramp / interviewing.io**: Free mock algorithm interviews
- **USACO Guide**: Competitive programming training path
- **CP-Algorithms**: Algorithm reference with implementations
- **Big-O Cheatsheet**: Quick complexity reference

### Algorithm Visualizers
- **Visualgo.net**: Visualize sorting, graph algorithms, DP
- **Algorithm Visualizer**: Interactive algorithm demonstrations
- **GeeksforGeeks**: Algorithm implementations in multiple languages

---

## Final Checklist

- [ ] Can implement merge sort and quick sort from scratch
- [ ] Can solve any 1D DP problem in under 20 minutes
- [ ] Can implement BFS, DFS, Dijkstra, topological sort
- [ ] Can solve backtracking problems (subsets, permutations, combinations)
- [ ] Can analyze time and space complexity instantly
- [ ] Can identify which paradigm a problem uses within 2 minutes
- [ ] Have completed 5+ mock interviews
- [ ] Can handle LC Hard problems within 45 minutes
- [ ] Can communicate algorithm design process clearly
- [ ] Have solved 75+ problems from target company tag
