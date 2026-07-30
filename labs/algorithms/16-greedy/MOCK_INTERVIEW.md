# Mock Interview: Greedy (Task Scheduler)

## Meta Information

| Aspect | Detail |
|--------|--------|
| Company | Meta |
| Level | E5 / Senior SWE |
| Problem | Task Scheduler (LeetCode 621) |
| Duration | 45 minutes |
| Paradigm | Greedy + Math |

---

## Transcript

### Phase 1: Problem Understanding (0:00–5:00)

**Interviewer:** We have a list of tasks the CPU needs to execute. Each task takes 1 unit of time. There's a cooldown period `n` — between two identical tasks, there must be at least `n` other tasks. Find the minimum total time needed.

**Candidate:** Let me clarify. Tasks are labeled with letters, and the cooldown only applies to identical tasks. Different tasks have no constraints between them?

**Interviewer:** Correct.

**Candidate:** And we can reorder tasks arbitrarily to minimize idle time?

**Interviewer:** Yes, in any order you choose.

**Candidate:** So for `tasks = [A, A, A, B, B, B]` with `n = 2`:
Sequence: `A → B → idle → A → B → idle → A → B` = 8 units.

Or better: `A → B → C → A → B → C → A → B` — wait, we don't have C. Let's see. `A → B → idle → A → B → idle → A → B` is 8. Could we do `A → B → A → B → A → B`? No — between the two As, only one B separates them, and n=2 requires at least 2 units. So 8 seems right.

**Interviewer:** That matches our expected answer.

### Phase 2: Approach Design (5:00–15:00)

**Candidate:** The greedy intuition is to always schedule the most frequent task first because it creates the tightest constraints. Let me reason through this.

**Brute force:** Try all permutations — O(n!) — infeasible.

**Greedy insight:** The most frequent task determines the minimum schedule length. If `maxFreq` is the frequency of the most common task and `maxCount` is how many tasks share that max frequency, then:

- We need at least `(maxFreq - 1) * (n + 1) + maxCount` time units.
- The `(maxFreq - 1)` represents the gaps between executions of the most frequent task.
- Each gap must be at least `n` units long, but within each gap we can fill with `n + 1` slots (one for the task itself, `n` for cooldown).
- The last occurrence doesn't need a gap after it.
- If there are `maxCount` tasks tied for most frequent, they share the last row.

**Interviewer:** But what if there are plenty of other tasks to fill the gaps?

**Candidate:** Exactly — the actual answer is `max(tasks.length, formula)`. If there are enough filler tasks, no idle time is needed, and the answer is simply `tasks.length`.

The formula accounts for the **worst-case** idle slots. Let me refine:

```
partCount = maxFreq - 1          // number of gaps between groups
partLength = n + 1 - maxCount    // available slots per gap that aren't the max-freq tasks
emptySlots = partCount * partLength   // slots that need to be filled
availableTasks = tasks.length - maxFreq * maxCount   // non-max-freq tasks
idles = max(0, emptySlots - availableTasks)
result = tasks.length + idles
```

**Interviewer:** Walk through the example `tasks = [A,A,A,B,B,B], n = 2`.

**Candidate:**
- Frequencies: A=3, B=3
- `maxFreq = 3`, `maxCount = 2` (both A and B appear 3 times)
- `partCount = 2` (gaps between the 3 groups)
- `partLength = n + 1 - maxCount = 2 + 1 - 2 = 1`
- `emptySlots = 2 * 1 = 2`
- `availableTasks = 6 - 3*2 = 0`
- `idles = max(0, 2 - 0) = 2`
- `result = 6 + 2 = 8`

Correct!

Let me trace another: `tasks = [A,A,A,B,C], n = 3`:
- Frequencies: A=3, B=1, C=1
- `maxFreq = 3`, `maxCount = 1`
- `partCount = 2`, `partLength = 3 + 1 - 1 = 3`
- `emptySlots = 2 * 3 = 6`
- `availableTasks = 5 - 3*1 = 2`
- `idles = 6 - 2 = 4`
- `result = 5 + 4 = 9`

Verify: `A → B → C → idle → A → idle → idle → idle → A` = 9. Actually we could arrange better: `A → B → C → idle → A → idle → idle → idle → A` — hmm, after the first A we have B, C, idle (3 units), then A. Between the last two As there are B, C, idle (but B and C already used). Let's see: `A, B, C, idle, A, idle, idle, idle, A` = 9. The formula gives 9. Correct.

### Phase 3: Coding (15:00–33:00)

**Candidate:** I'll implement the formula-based solution.

```java
class Solution {
    public int leastInterval(char[] tasks, int n) {
        int[] freq = new int[26];
        for (char c : tasks) {
            freq[c - 'A']++;
        }

        int maxFreq = 0, maxCount = 0;
        for (int f : freq) {
            if (f > maxFreq) {
                maxFreq = f;
                maxCount = 1;
            } else if (f == maxFreq) {
                maxCount++;
            }
        }

        int partCount = maxFreq - 1;
        int partLength = n - (maxCount - 1);
        int emptySlots = partCount * partLength;
        int availableTasks = tasks.length - maxFreq * maxCount;
        int idles = Math.max(0, emptySlots - availableTasks);

        return tasks.length + idles;
    }
}
```

**Interviewer:** What if `n` is 0?

**Candidate:** Then `partLength = 0 - (maxCount - 1)`. If `maxCount = 1`, then `partLength = -1`, and `emptySlots = (maxFreq - 1) * (-1) = negative`. Then `idles = max(0, negative - availableTasks) = 0`. Result = `tasks.length`. Which makes sense — with no cooldown, there's no idle time.

If `maxCount > 1`, `partLength` could be even more negative, but the `Math.max(0, ...)` handles it. The result is always at least `tasks.length`.

### Phase 4: Complexity & Follow-ups (33:00–45:00)

**Interviewer:** What if we could also have idle tasks that we can insert? Does the formula change?

**Candidate:** The formula already accounts for idle slots. An "idle" is just a time unit where the CPU does nothing. The formula calculates exactly how many such idles are needed.

**Interviewer:** What if tasks had different execution times?

**Candidate:** Then it becomes a more general scheduling problem — similar to multiprocessor scheduling with precedence constraints. The greedy approach becomes: always run the task with the most remaining execution time that's not on cooldown. This can be implemented with a max-heap for ready tasks and a queue for cooling tasks.

**Interviewer:** Could you sketch that approach?

**Candidate:** 

```java
public int leastInterval(char[] tasks, int n) {
    Map<Character, Integer> counts = new HashMap<>();
    for (char c : tasks) counts.merge(c, 1, Integer::sum);
    
    PriorityQueue<Integer> heap = new PriorityQueue<>(Collections.reverseOrder());
    heap.addAll(counts.values());
    
    Queue<int[]> cooldown = new LinkedList<>();
    int time = 0;
    
    while (!heap.isEmpty() || !cooldown.isEmpty()) {
        if (!cooldown.isEmpty() && cooldown.peek()[1] <= time) {
            heap.offer(cooldown.poll()[0]);
        }
        if (!heap.isEmpty()) {
            int remaining = heap.poll() - 1;
            if (remaining > 0) {
                cooldown.offer(new int[]{remaining, time + n + 1});
            }
        }
        time++;
    }
    return time;
}
```

This simulates the process second-by-second. The heap always has ready tasks, and the cooldown queue tracks when a task becomes available again. This works for any task counts and any cooldown, but runs in O(time) which could be large if n is big.

The formula approach is O(n) and constant space, making it the preferred solution for this problem's constraints.

**Interviewer:** Good. I think that's all we need.

---

## Key Takeaways

| Topic | Insight |
|-------|---------|
| Greedy Principle | Always schedule the most frequent task first |
| Mathematical Formula | Idle calculation using maxFreq, maxCount, and n |
| Simulation Alternative | Max-heap + cooldown queue for general case |
| Edge Cases | n=0 → all tasks run consecutively; n large → many idle slots |
| Complexity | O(n) time, O(1) space using frequency array |
