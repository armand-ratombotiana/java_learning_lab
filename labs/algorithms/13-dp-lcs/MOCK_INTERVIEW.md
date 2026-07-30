# Mock Interview: LCS / Edit Distance

## Meta Information

| Aspect | Detail |
|--------|--------|
| Company | Google |
| Level | L5 / Senior SWE |
| Problem | Edit Distance (LeetCode 72) |
| Duration | 45 minutes |
| Paradigm | DP — Longest Common Subsequence variant |

---

## Transcript

### Phase 1: Problem Understanding (0:00–5:00)

**Interviewer:** Given two strings, we want to find the minimum number of operations needed to convert one into the other. The allowed operations are inserting a character, deleting a character, or replacing a character. Can you solve this?

**Candidate:** Let me make sure I understand. For `word1 = "horse"` and `word2 = "ros"`, what would you expect?

**Interviewer:** Let's say the answer should be 3.

**Candidate:** Let me verify manually: `horse` → replace `h` with `r` → `rorse` → delete `r` (second) → `rose` → delete `e` → `ros`. That's 3 operations. Alternatively, `horse` → delete `h` → `orse` → delete `o` → `rse` → replace `r` with `r` (no-op) → `rse` → replace `s` with `s` (no-op) → ... hmm, that doesn't work.

Actually, the optimal is: `horse` → delete `h` → `orse`, delete `o` → `rse`, replace `r`→`r` (no op), replace `s`→`o` → `roe`, hmm. Let me think more carefully.

The standard answer: replace `h→r` (1), delete `o` (2), delete `e` (3) after `hors` → `ros`. Or: delete `h`, delete `o`, replace `e → s`. Let me just trust the problem statement and work on the algorithm.

**Interviewer:** That's fine. The exact sequence isn't critical — the algorithm will find it.

### Phase 2: Approach Design (5:00–15:00)

**Candidate:** This is a classic DP problem, very similar to Longest Common Subsequence but with a cost model. Let me derive the recurrence.

Define `dp[i][j]` = minimum edits to convert `word1[0..i-1]` to `word2[0..j-1]`.

**Base cases:**
- `dp[0][j] = j` — need to insert `j` characters.
- `dp[i][0] = i` — need to delete `i` characters.

**Recurrence:**
If `word1[i-1] == word2[j-1]`:
  `dp[i][j] = dp[i-1][j-1]` — characters match, no operation needed.
Else:
  `dp[i][j] = 1 + min(
      dp[i-1][j],      // delete word1[i-1]
      dp[i][j-1],      // insert word2[j-1]
      dp[i-1][j-1]     // replace word1[i-1] with word2[j-1]
  )`

**Interviewer:** Why does delete correspond to `dp[i-1][j]`?

**Candidate:** When we delete `word1[i-1]`, the problem reduces to converting `word1[0..i-2]` to `word2[0..j-1]` — we've removed one character from `word1`. The cost is 1 (the delete) plus whatever it takes to convert the shorter word1 to word2.

Similarly, inserting `word2[j-1]` means we match that character, and the problem reduces to converting `word1[0..i-1]` to `word2[0..j-2]`. Replacing means we transform `word1[i-1]` to match `word2[j-1]`, then solve for the remaining prefixes.

**Interviewer:** What's the time and space?

**Candidate:** Time is O(m * n), space is O(m * n) for the full table. We can optimize space to O(min(m, n)) if we only need the distance, not the path, by keeping just the previous row.

**Interviewer:** Let's also discuss path reconstruction.

**Candidate:** To reconstruct the edit operations, we can walk backwards through the DP table from `dp[m][n]` to `dp[0][0]`:
- If chars match: move diagonally (no operation).
- If value came from top (`dp[i-1][j]`): it was a delete.
- If value came from left (`dp[i][j-1]`): it was an insert.
- If value came from diagonal (`dp[i-1][j-1]`): it was a replace.

Each step records the operation, and we reverse the list at the end.

### Phase 3: Coding (15:00–35:00)

**Candidate:** I'll implement both the distance computation and path reconstruction.

```java
class Solution {
    public int minDistance(String word1, String word2) {
        int m = word1.length(), n = word2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(
                        dp[i - 1][j],
                        Math.min(dp[i][j - 1], dp[i - 1][j - 1])
                    );
                }
            }
        }
        return dp[m][n];
    }
}
```

**Interviewer:** Can you walk through `dp` for `word1 = "horse", word2 = "ros"`?

**Candidate:** 

```
    ""  r   o   s
""  0   1   2   3
h   1   1   2   3
o   2   2   1   2
r   3   2   2   2
s   4   3   3   2
e   5   4   4   3
```

Let me trace a few cells:
- `dp[1][1]`: 'h' vs 'r' — no match, so `1 + min(dp[0][1]=1, dp[1][0]=1, dp[0][0]=0)` = `1 + 0 = 1`. So replacing 'h'→'r' costs 1.
- `dp[2][2]`: 'o' vs 'o' — match, so `dp[1][1] = 1`. `"ho" → "ro"` costs 1 (replace h→r).
- `dp[3][3]`: 'r' vs 's' — no match, so `1 + min(dp[2][3]=3, dp[3][2]=2, dp[2][2]=1)` = `1 + 1 = 2`.

Final answer `dp[5][3] = 3`.

### Phase 4: Extensions (35:00–45:00)

**Interviewer:** How would you handle different costs for each operation? Say delete costs 2, insert costs 1, replace costs 3?

**Candidate:** Simple — replace the `1 + min(...)` with a weighted version:

```java
dp[i][j] = Math.min(
    dp[i - 1][j] + deleteCost,
    Math.min(
        dp[i][j - 1] + insertCost,
        dp[i - 1][j - 1] + replaceCost
    )
);
```

This creates a weighted edit distance, also known as the Needleman-Wunsch algorithm in bioinformatics.

**Interviewer:** What if the strings are very long — say 10,000 characters each?

**Candidate:** Then O(m*n) memory becomes problematic — 10k × 10k = 100 million entries ≈ 400 MB for ints. Two optimizations:

1. **Space optimization**: Only keep two rows (previous and current). This reduces space to O(min(m, n)). But we lose the ability to reconstruct the path.

2. **Hirschberg's algorithm**: A divide-and-conquer approach that computes the edit distance and reconstructs the path in O(m*n) time and O(min(m, n)) space. It works by finding the "midpoint" of the optimal path using a forward and reverse DP pass, then recursively solving the two halves.

**Interviewer:** That's advanced. When would you actually need path reconstruction?

**Candidate:** In version control systems (diff), bioinformatics (DNA sequence alignment), plagiarism detection, and spell checkers — knowing *what* changed is as important as knowing how many changes.

**Interviewer:** Great answer. Let's wrap up.

---

## Key Takeaways

| Topic | Insight |
|-------|---------|
| DP Table Semantics | dp[i][j] = min edits to convert prefix i of word1 to prefix j of word2 |
| Operation Mapping | Delete = remove from word1, Insert = add to word2, Replace = transform one char to another |
| Path Reconstruction | Walk backwards through table following the arrows; reverse at end |
| Weighted Variant | Bioinformatic algorithms like Needleman-Wunsch generalize costs |
| Large Inputs | Hirschberg's algorithm gives O(min(m,n)) space with full reconstruction |
