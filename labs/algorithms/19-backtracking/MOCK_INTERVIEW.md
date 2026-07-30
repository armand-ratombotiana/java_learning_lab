# Mock Interview: Backtracking (Combination Sum)

## Meta Information

| Aspect | Detail |
|--------|--------|
| Company | Microsoft |
| Level | L63 / Senior SWE |
| Problem | Combination Sum (LeetCode 39) |
| Duration | 45 minutes |
| Paradigm | Backtracking |

---

## Transcript

### Phase 1: Problem Understanding (0:00–5:00)

**Interviewer:** Given an array of distinct integers and a target, find all unique combinations where the numbers sum to the target. You can reuse numbers an unlimited number of times.

**Candidate:** So for `candidates = [2,3,6,7]`, `target = 7`, the answer should be `[[2,2,3],[7]]`. Are the candidates always positive?

**Interviewer:** Yes, positive integers. The array is distinct but not necessarily sorted.

**Candidate:** Can a combination be a single number if it equals the target?

**Interviewer:** Yes — `[7]` is valid if `7` is in the candidates.

**Candidate:** And the order within a combination doesn't matter, so `[2,2,3]` and `[3,2,2]` are the same?

**Interviewer:** Correct. Only include one version, typically in non-decreasing order.

### Phase 2: Approach Design (5:00–15:00)

**Candidate:** This is a classic backtracking problem. Let me think through the design.

**Brute force:** Generate all subsets of candidates (with repetition) and filter those summing to target. The number of possibilities is enormous — essentially exponential.

**Backtracking approach:**
1. Sort the candidates (for pruning and to ensure non-decreasing output).
2. Define a recursive function that explores adding candidates to the current combination.
3. At each step, we can add any candidate from the current index onward (to avoid permutations).
4. Prune: if the candidate exceeds the remaining target, stop exploring (this requires sorting).
5. Base case: if remaining == 0, record the current path.

**Interviewer:** Why do we pass the current index, not 0, to the recursive call?

**Candidate:** To avoid duplicate combinations. If we allow using any candidate at each step, we'd get both `[2,2,3]` and `[3,2,2]` and `[2,3,2]` — permutations of the same combination. By restricting to candidates at index `i` or higher, we ensure combinations are generated in non-decreasing order, producing each unique combination exactly once.

Note that we pass `i` (not `i+1`) because we can reuse the same element. If the problem restricted to using each element at most once (LeetCode 40), we'd pass `i+1`.

**Interviewer:** What's the pruning strategy?

**Candidate:** Sorting is essential for pruning. Once `candidates[i] > remaining`, all subsequent candidates (which are ≥ candidates[i]) will also exceed remaining, so we can break out of the loop entirely. Without sorting, we'd need to check each candidate individually.

### Phase 3: Coding (15:00–35:00)

**Candidate:** I'll implement the backtracking solution.

```java
class Solution {
    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(candidates);
        backtrack(candidates, target, 0, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(int[] candidates, int remaining, int start,
                           List<Integer> path, List<List<Integer>> result) {
        if (remaining == 0) {
            result.add(new ArrayList<>(path));
            return;
        }

        for (int i = start; i < candidates.length; i++) {
            if (candidates[i] > remaining) break;

            path.add(candidates[i]);
            backtrack(candidates, remaining - candidates[i], i, path, result);
            path.remove(path.size() - 1);
        }
    }
}
```

**Interviewer:** Walk through `candidates = [2,3,6,7], target = 7`.

**Candidate:** Already sorted. Let's trace the backtrack tree:

```
backtrack(7, 0)  // remaining=7, start=0
├── pick 2 → backtrack(5, 0)
│   ├── pick 2 → backtrack(3, 0)
│   │   ├── pick 2 → backtrack(1, 0) → 2 > 1, break
│   │   ├── pick 3 → backtrack(0, 1) → remaining=0 → add [2,2,3]
│   │   └── pick 6 → 6 > 3, break
│   ├── pick 3 → backtrack(2, 1)
│   │   ├── pick 3 → 3 > 2, break
│   │   └── pick 6 → 6 > 2, break
│   ├── pick 6 → 6 > 5, break
│   └── pick 7 → 7 > 5, break
├── pick 3 → backtrack(4, 1)
│   ├── pick 3 → backtrack(1, 1) → 3 > 1, break
│   └── pick 6 → 6 > 4, break
├── pick 6 → backtrack(1, 2) → 6 > 1, break
└── pick 7 → backtrack(0, 3) → remaining=0 → add [7]
```

Result: `[[2,2,3],[7]]`. Correct.

### Phase 4: Complexity & Follow-ups (35:00–45:00)

**Interviewer:** What's the time complexity?

**Candidate:** In the worst case, the number of combinations is exponential. We can bound it as O(N^(T/M + 1)) where N = candidates length, T = target, M = minimum candidate value. The depth of recursion is at most T/M (using the smallest candidate). At each level, we branch up to N times. Space complexity is O(T/M) for the recursion stack and path list.

**Interviewer:** How is this different from subset generation?

**Candidate:** In subset generation, each element is either included or excluded (binary choice), giving 2^N subsets. Here, elements can be included multiple times, so the branching factor is larger. However, the pruning with sorted candidates and the `remaining` check significantly reduces the search space in practice.

**Interviewer:** What if we had negative numbers allowed?

**Candidate:** That changes the problem significantly:
1. Sorting no longer helps for pruning — a negative number could take us further from the target, but another negative could bring us back.
2. The search space becomes infinite if there's a negative cycle that can sum to zero.
3. We'd need a depth limit or a different algorithmic approach.

Most LeetCode problems restrict to positive candidates for this reason.

**Interviewer:** What about the variant where each candidate can be used at most once?

**Candidate:** That's LeetCode 40 (Combination Sum II). The changes are:
1. After picking `candidates[i]`, recurse with `i+1` instead of `i`.
2. Skip duplicates at the same recursion level: if `candidates[i] == candidates[i-1]`, skip (since they'd produce the same combination).

```java
for (int i = start; i < candidates.length; i++) {
    if (i > start && candidates[i] == candidates[i - 1]) continue;
    if (candidates[i] > remaining) break;
    // ... rest same
}
```

**Interviewer:** Can you solve this iteratively instead of recursively?

**Candidate:** Yes — we can use an explicit stack. Each stack frame stores `(remaining, start, path)`. The recursive approach is cleaner for most backtracking problems, but an iterative approach avoids recursion stack limits (though those are rarely hit with the constraints here).

**Interviewer:** What about memoization? Can we optimize this with DP?

**Candidate:** If we only needed the *number* of combinations, we'd use DP (coin change 2 — LeetCode 518). But since we need the actual combinations (not just the count), we have to enumerate them, which is inherently exponential. Memoization could help in some cases by caching results for `(remaining, start)` if many paths lead to the same subproblem, but the overhead often outweighs the benefit.

**Interviewer:** Good analysis. I'm satisfied.

---

## Key Takeaways

| Topic | Insight |
|-------|---------|
| Backtracking Pattern | Explore → check → recurse → backtrack |
| Pruning | Sort + break when candidate > remaining |
| Unbounded vs Bounded | Pass `i` (unbounded) vs `i+1` (bounded) for reuse |
| Duplicate Safety | Pass `start` to enforce non-decreasing order in combinations |
| Complexity | Exponential with pruning — acceptable for small integer targets |
