# Academy Interview Guide: Advanced Data Structures

## Purpose

This guide provides a structured learning and interview preparation framework for mastering 10 advanced data structures commonly asked in FAANG, unicorn startups, and top-tier software engineering interviews. It is designed as an "academy-style" curriculum — self-paced, milestone-based, with regular assessments.

---

## Curriculum Overview

### Duration: 8 Weeks

| Week | Focus Area | Structures | Goal |
|------|-----------|------------|------|
| 1 | String Structures | Trie, Suffix Array | Handle prefix and substring problems |
| 2 | Probabilistic Structures | Bloom Filter | Memory-efficient membership testing |
| 3 | Range Query Structures | Fenwick Tree, Segment Tree | Efficient range operations |
| 4 | Balanced Trees | Red-Black Tree, Treap | Self-balancing BST mastery |
| 5 | Probabilistic Balancing | Skip List | Concurrent near-O(log n) structures |
| 6 | Graph Connectivity | Union-Find | Disjoint set operations |
| 7 | Cryptographic Structures | Merkle Tree | Integrity verification systems |
| 8 | Review + System Design | All | Cross-cutting concepts |

---

## Weekly Structure

Each week follows the same format:

### Day 1: Read GUIDE.md
- Understand the data structure's purpose and mechanics
- Trace the ASCII diagrams
- Read the Java source code alongside the walkthrough

### Day 2: Code It
- Close the solution file
- Implement the data structure from scratch (or memory)
- Write test cases using the Java main() examples from GUIDE.md

### Day 3: Solve Problems
- Complete 3-5 LeetCode problems listed in INTERVIEW.md
- Focus on mediums first, then hards
- Time yourself: 30 min per medium, 45 min per hard

### Day 4: System Design Tie-in
- Read the SYSTEM_DESIGN_CHEATSHEET.md section
- Design one system that uses this structure
- Practice explaining the architecture out loud

### Day 5: Mock Interview
- Partner up or use the MOCK_INTERVIEW.md for self-practice
- Simulate 45-minute interview with whiteboard/coding
- Focus on explaining trade-offs clearly

### Day 6: Review + INTERVIEW.md Questions
- Read through all 15-20 interview questions
- Answer each without looking
- Review PROBLEM_WALKTHROUGH.md

### Day 7: Deep Work / Catch Up
- Review misunderstood concepts
- Implement one advanced variant (e.g., persistent trie, counting Bloom filter, lazy segment tree)
- Write down key insights in personal notes

---

## Study Resources

### Primary (in this repo)
- GUIDE.md per lab — comprehensive tutorial
- Java source code — compilable implementations
- INTERVIEW.md — LeetCode-linked questions
- PROBLEM_WALKTHROUGH.md — step-by-step solutions
- MOCK_INTERVIEW.md — simulated interviews

### Secondary (external)
- **Introduction to Algorithms (CLRS)**: Chapters on each structure
- **LeetCode**: Problem lists per tag
- **NeetCode.io**: Video walkthroughs
- **AlgoExpert**: Advanced DS coverage
- **System Design Interview (Alex Xu)**: System context
- **Designing Data-Intensive Applications (Kleppmann)**: Merkle trees in distributed systems

### Practice Platforms
- LeetCode (primary)
- HackerRank (DSU/segment tree problems)
- Codeforces (advanced competitive problems)
- AtCoder (treap/implicit treap problems)

---

## Assessment Milestones

### Week 2 Checkpoint
- [ ] Implement Trie insert/search/startsWith from memory (5 min)
- [ ] Implement Bloom filter with 3 hash functions (10 min)
- [ ] Solve "Implement Trie" (LC 208) — medium — <20 min
- [ ] Solve "Word Search II" (LC 212) — hard — <40 min
- [ ] Design a URL dedup system

### Week 4 Checkpoint
- [ ] Implement Fenwick Tree point update + prefix sum (5 min)
- [ ] Implement Segment Tree with lazy propagation (15 min)
- [ ] Solve "Range Sum Query - Mutable" (LC 307) — medium — <20 min
- [ ] Solve "Falling Squares" (LC 699) — hard — <45 min
- [ ] Explain when to use BIT vs Segment Tree

### Week 6 Checkpoint
- [ ] Implement Skip List insert/search/delete (15 min)
- [ ] Implement RB Tree insert with fixup (20 min) — focus on logic
- [ ] Implement Treap split/merge (10 min)
- [ ] Solve "Design Skiplist" (LC 1206) — hard — <40 min
- [ ] Explain why Red-Black Tree over AVL (more insert-friendly)

### Week 8 Checkpoint (Final)
- [ ] Implement Union-Find with path compression + union by rank (5 min)
- [ ] Implement Merkle Tree build + verification (10 min)
- [ ] Solve "Number of Islands" (LC 200) — medium — <15 min
- [ ] Solve "Accounts Merge" (LC 721) — medium — <25 min
- [ ] Solve "Redundant Connection" (LC 684) — medium — <20 min
- [ ] Design a file sync system using Merkle trees (20 min explanation)

---

## Scoring Rubric

Each interview problem you solve should be evaluated on:

| Criteria | Weight | 1 (Needs Work) | 3 (Good) | 5 (Excellent) |
|----------|--------|----------------|----------|----------------|
| Understanding constraints | 15% | Asked none | Asked 1-2 | Asked all relevant |
| Approach explanation | 20% | Jumped to code | Explained approach briefly | Compared 2-3 approaches |
| DS choice justification | 15% | No reasoning | Basic trade-off | Deep complexity analysis |
| Code correctness | 25% | Major bugs | Minor bugs | Compiles and passes tests |
| Complexity analysis | 10% | Wrong analysis | Correct but hesitant | Instant, correct, amortised |
| Follow-up handling | 15% | Stuck | Answered with hints | Proactive, explored variants |

### Scoring Guide
- **85%+**: Ready for FAANG
- **70-84%**: Needs 2-3 more weeks of practice
- **50-69%**: Review fundamentals, retry after 1 week
- **<50%**: Spend 2 weeks on basics before advanced DS

---

## Daily Practice Tracker

| Date | Structure | Problem Solved | Time Taken | Score (1-5) | Notes |
|------|-----------|---------------|------------|-------------|-------|
| | | | | | |
| | | | | | |
| | | | | | |

---

## Interview Format Template

### First Round (Phone Screen — 45 min)
- 5 min: Introduction + clarifying questions
- 5 min: Warm-up data structure implementation
- 25 min: Main problem (medium-level)
- 5 min: Complexity analysis
- 5 min: Follow-up question

### Second Round (Onsite — 60 min)
- 5 min: Introduction
- 10 min: System design warm-up
- 35 min: Main problem (hard-level)
- 5 min: Complexity + edge cases
- 5 min: Follow-up system design integration

### Final Round (Virtual Onsite — 60 min)
- 5 min: Introduction
- 10 min: Low-level design / internals discussion
- 30 min: Problem with 2-structure combination
- 10 min: Trade-off analysis + alternatives
- 5 min: Summary + questions for interviewer

---

## Problem Solution Template

Use this template for solving problems during practice:

```
## Problem Statement
[One sentence summary]

## Constraints
- Input size: [range]
- Time limit: [implicit/explicit]
- Memory limit: [implicit/explicit]
- Edge cases: [empty, single, duplicates, etc.]

## Approach 1: Brute Force
- Idea: [description]
- Time: O([complexity])
- Space: O([complexity])
- Problem: [why this doesn't scale]

## Approach 2: Optimised (DS-based)
- Idea: [description using DS]
- DS Choice: [structure] because [reason]
- Algorithm:
  1. [Step 1]
  2. [Step 2]
  3. [Step 3]
- Time: O([complexity]) — [justification]
- Space: O([complexity]) — [justification]

## Approach 3: Further Optimisation (if applicable)
- Idea: [description]
- Trade-off: [what we gain vs what we lose]

## Edge Case Handling
- Empty input: [handling]
- Single element: [handling]
- Duplicates: [handling]
- Large input: [handling]

## Code
[Implementation]

## Test Cases
1. [Simple case]: Input → Expected → Actual
2. [Edge case]: Input → Expected → Actual
3. [Large case]: Input → Expected → Actual

## Follow-up Questions
1. [What if input is a stream?]
2. [What if we need persistence?]
3. [What if we have multiple readers/writers?]
```

---

## Debugging Common Mistakes

### Trie
```
Mistake: Not marking end-of-word properly
Diagnosis: search returns true for prefixes
Fix: Use separate isEndOfWord boolean, check before returning

Mistake: Deleting child before checking other words share it
Diagnosis: Deletes words that share a prefix
Fix: Only delete node when no other word uses it (prefixCount == 0)
```

### Bloom Filter
```
Mistake: Hash function returns same value for different inputs
Diagnosis: High false positive rate
Fix: Use well-distributed hash (MurmurHash3) with different seeds

Mistake: Bit array too small
Diagnosis: All bits become 1, every check returns "maybe"
Fix: Calculate m = -n*ln(P) / ln(2)^2 before implementation
```

### Suffix Array
```
Mistake: Off-by-one in LCP array comparison
Diagnosis: Wrong longest repeated substring
Fix: Remember LCP[i] = LCP between suffix at SA[i] and SA[i-1]

Mistake: O(n² log n) build
Diagnosis: Naive suffix comparison in sort
Fix: Use prefix doubling or SA-IS algorithm
```

### Fenwick Tree
```
Mistake: 0-index vs 1-index confusion
Diagnosis: Infinite loop (i += i & -i with i = 0)
Fix: Always use 1-indexed internally, translate on API boundary

Mistake: Off-by-one in range sum
Diagnosis: sum(r) - sum(l) instead of sum(r) - sum(l-1)
Fix: rangeSum(l, r) = prefixSum(r) - prefixSum(l-1)
```

### Segment Tree
```
Mistake: Tree array size = 2n (not 4n)
Diagnosis: ArrayIndexOutOfBounds during build
Fix: Allocate size = 4 * n for safety

Mistake: Not propagating lazy flags
Diagnosis: Stale values after partial overlap queries
Fix: push() before recursing into children
```

### Skip List
```
Mistake: Not updating previous pointers
Diagnosis: List splits during insertion
Fix: Track update array during search, update all levels

Mistake: Random level too high (maxLevel too small)
Diagnosis: O(n) worst-case search
Fix: maxLevel = log₂(n) or ceil(log₂(expected_n))
```

### Red-Black Tree
```
Mistake: Wrong uncle colour check
Diagnosis: Tree violates red-black properties
Fix: Uncle is sibling of parent, not sibling of node

Mistake: Not handling root colour
Diagnosis: Root is red after fixup
Fix: Always set root.color = BLACK at end of insert/delete
```

### Treap
```
Mistake: Priority collisions
Diagnosis: Tree not heap-ordered
Fix: Add tie-breaker (second random value or insertion order)

Mistake: Wrong split key
Diagnosis: Wrong subtree sizes in implicit treap
Fix: split(root, k) where k is size of left tree, not key value
```

### Union-Find
```
Mistake: No path compression
Diagnosis: O(n) find on deep trees
Fix: parent[x] = find(parent[x]) in find()

Mistake: Union without rank/size
Diagnosis: Skewed tree height
Fix: Track size/rank array, attach smaller to larger
```

### Merkle Tree
```
Mistake: Odd number of leaves not handled
Diagnosis: Last leaf has no sibling
Fix: Duplicate last leaf (or hash with itself) if odd count

Mistake: Using non-collision-resistant hash
Diagnosis: Potential hash collision
Fix: Use SHA-256 minimum for any security context
```

---

## Interview Day Checklist

### Before Interview
- [ ] Review INTERVIEW_CHEATSHEET.md — 10 min
- [ ] Review one GUIDE.md per structure you expect — 5 min each
- [ ] Code one warm-up (Trie insert + Union-Find + BIT) — 10 min
- [ ] Sleep 8 hours
- [ ] Prepare water, quiet room, stable internet

### During Interview
- [ ] Clarify constraints before proposing DS
- [ ] State the DS you'll use and why
- [ ] Write the standard code pattern (not optimised magic)
- [ ] Walk through example test case
- [ ] State complexity again after coding
- [ ] Handle follow-ups proactively

### After Interview
- [ ] Write down the questions asked
- [ ] Note what you did well / could improve
- [ ] Review the relevant lab documentation
- [ ] Practice the weak areas before next interview

---

## Final Advice

> "You don't really understand a data structure until you can explain it to someone who doesn't know it, implement it from scratch under time pressure, and know exactly when NOT to use it."

### The 10-Minute Rule
If you haven't started coding after 10 minutes of thinking, you don't understand the problem well enough. Go back to constraints. Ask clarifying questions. Don't jump into code.

### The Trade-off Rule
Every data structure decision is a trade-off. In interviews, you MUST state both sides:
- "Trie gives O(L) prefix search but costs O(A·L) space per word"
- "Bloom filter saves memory but has false positives"
- "Segment tree handles arbitrary range queries but uses 4x memory of BIT"

### The "I Don't Know" Rule
It's OK to say "I don't know" and then show how you'd figure it out:
- "I'm not sure about the optimal hash count for our Bloom filter. Let me derive it. The formula is k = (m/n) * ln(2). If we have n elements and m bits..."
- This shows analytical thinking > memorisation.

### Final Milestone
When you can:
1. Implement all 10 structures from memory
2. Explain trade-offs between each pair
3. Design a system using 3+ of them together
4. Answer all 20 INTERVIEW.md questions per lab fluently
5. Complete a 60-min mock interview without needing hints

...you are ready for any advanced data structure interview in the industry.