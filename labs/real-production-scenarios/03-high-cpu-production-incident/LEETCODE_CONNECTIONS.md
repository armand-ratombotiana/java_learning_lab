# Lab 03 — High CPU / ReDoS: LeetCode Connections

## Algorithmic Concepts in ReDoS Debugging

| Concept | ReDoS Application |
|---------|-------------------|
| NFA vs DFA | Understanding regex engine types and their time complexity |
| Exponential complexity | O(2^n) patterns from nested quantifiers |
| Backtracking | DFS with pruning — backtracking is DFS with failed-path rollback |
| Memoization | Preventing re-computation — atomic groups act like memoization |
| Greedy vs. Dynamic Programming | Greedy algorithms match eagerly; DP considers all options |
| Automata theory | NFA/DFA equivalence, epsilon transitions in regex |

## LeetCode Problems Related to ReDoS

### Easy

**Q1: Valid Palindrome (LeetCode 125)**

| Aspect | Connection |
|--------|-----------|
| **Problem** | Check if string is palindrome |
| **ReDoS Connection** | Simple regex like `^[a-zA-Z0-9]+$` can be vulnerable. The alternation + quantifier pattern is common. Understanding how different regex patterns evaluate against different strings helps identify which ones might be ReDoS-susceptible. |
| **Algorithmic Lesson** | Simple problems solved with regex might be better solved with direct string manipulation. If the regex is more complex than the problem, consider non-regex solutions. |

**Q2: Implement strStr() (LeetCode 28)**

| Aspect | Connection |
|--------|-----------|
| **Problem** | Find first occurrence of pattern in string |
| **ReDoS Connection** | This is fundamentally what a regex engine does — find a pattern in text. The naive O(n*m) approach is like backtracking: try each position, if mismatch, backtrack. KMP is O(n+m) — like a DFA approach. |
| **Algorithmic Lesson** | There are always faster alternatives to backtracking. KMP, Boyer-Moore, Rabin-Karp are to string matching what RE2 is to regex — O(n) instead of O(2^n). |

### Medium

**Q3: Generate Parentheses (LeetCode 22)**

| Aspect | Connection |
|--------|-----------|
| **Problem** | Generate all valid combinations of n pairs of parentheses |
| **ReDoS Connection** | This generates 2^n combinations — the exact growth rate that makes ReDoS dangerous. A regex with nested quantifiers explores a similar exponential space of alternatives. |
| **Algorithmic Lesson** | Exponential state explosion is the root cause of ReDoS. Recognizing when a problem has 2^n complexity is critical for designing defenses. |

**Q4: Word Break II (LeetCode 140)**

| Aspect | Connection |
|--------|-----------|
| **Problem** | Break string into dictionary words, return all combinations |
| **ReDoS Connection** | This is backtracking with memoization. Without memoization, it's 2^n. With memoization, it's O(n*m). The atomic group `(?>...)` acts like memoization — eliminating redundant exploration of backtracking paths. |
| **Algorithmic Lesson** | Memoization converts exponential to polynomial. Atomic groups do the same for regex. Understanding the difference between DFS with and without memoization is the key to understanding catastrophic backtracking. |

**Q5: Word Search (LeetCode 79)**

| Aspect | Connection |
|--------|-----------|
| **Problem** | Find word in 2D grid (DFS with backtracking) |
| **ReDoS Connection** | This is backtracking in a grid. Each wrong path requires unhooking and trying the next. This is exactly how regex backtracking works — the engine tries a path, fails, backtracks, tries another. The more paths = the more backtracking. |
| **Algorithmic Lesson** | Backtracking on a graph (grid) is the same algorithm as backtracking in a regex. Both explore paths and backtrack on failure. Pruning (atomic groups) reduces search space. |

**Q6: Letter Combinations of a Phone Number (LeetCode 17)**

| Aspect | Connection |
|--------|-----------|
| **Problem** | Generate all combinations from phone digits |
| **ReDoS Connection** | This explicitly generates the exponential space that ReDoS exploits. For digits = 4, there are 3^4 = 81 combinations. For digits = 10, 3^10 = 59,049. The growth is exponential. |
| **Algorithmic Lesson** | Always bound the input size when dealing with exponential-time algorithms. Input length validation is the most effective ReDoS defense. |

### Hard

**Q7: Regular Expression Matching (LeetCode 10)**

| Aspect | Connection |
|--------|-----------|
| **Problem** | Implement regex matching with '.' and '*' |
| **ReDoS Connection** | This is the EXACT problem. LeetCode 10 requires handling backtracking with `.` and `*`. The naive recursive solution has exponential worst-case time. The DP solution is O(n*m). This is the textbook example of how to optimize backtracking. |
| **Algorithmic Lesson** | DP vs. backtracking = RE2 vs. java.util.regex. The LeetCode 10 DP solution is conceptually what a DFA engine does — process each character once. |

**Q8: Wildcard Matching (LeetCode 44)**

| Aspect | Connection |
|--------|-----------|
| **Problem** | Wildcard matching with '*' and '?' |
| **ReDoS Connection** | Same as LeetCode 10 — exponential backtracking vs. polynomial DP. The pattern `*` matching is analogous to `.*` in regex and can cause catastrophic backtracking when combined with other patterns. |
| **Algorithmic Lesson** | Patterns with wildcards are inherently prone to backtracking issues. Always use DP or automate to avoid exponential time. |

## How Algorithmic Thinking Helps

### Exponential Complexity Recognition

The most important algorithmic skill for preventing ReDoS is recognizing exponential time complexity. If a pattern has nested quantifiers (e.g., `(a+)+b`), it's O(2^n). Any pattern matching engineer should be able to identify this.

### DFS with Pruning

Regex backtracking is DFS on a tree of possible matches. Without pruning (atomic groups), all branches are explored. Atomic groups prune the search space. This is analogous to branch-and-bound algorithms in optimization.

### Memoization = Atomic Groups

Just as memoization in DP eliminates repeated subproblem computation, atomic groups eliminate repeated backtracking attempts. The key insight: once you've committed to a match within an atomic group, don't try alternatives.

## Practice Problems

1. **Implement a simple regex engine** (medium): Support `.`, `*`, and `+`. Show the exponential behavior on nested quantifiers.

2. **ReDoS vulnerability scanner** (medium): Write a program that takes a regex pattern and tests it against known malicious inputs, reporting patterns that exceed a time threshold.

3. **Atomic group optimizer** (hard): Given a regex pattern without atomic groups, automatically insert atomic groups where they would prevent backtracking without changing the pattern's behavior.

4. **NFA simulation** (medium): Implement an NFA regex engine that processes each character exactly once (no backtracking), similar to RE2.
