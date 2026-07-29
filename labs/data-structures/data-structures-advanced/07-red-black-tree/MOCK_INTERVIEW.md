# Mock Interview: Red-Black Tree

## Setting

- **Round**: Onsite data structures deep dive
- **Duration**: 60 minutes
- **Focus**: RB tree mechanics, Java TreeMap, system design

---

## Transcript

### Part 1: Warm-up (10 min)

**Interviewer:** Explain the five properties of a Red-Black tree and why they guarantee balance.

**Candidate:**
1. Red or black colour per node
2. Root is black
3. NIL leaves are black
4. Red nodes can't have red children (no red-red)
5. All root-to-NIL paths have same black count

These ensure the longest path (alternating red-black) is at most 2× the shortest path (all black), giving height ≤ 2·log₂(n+1).

**Interviewer:** How many rotations can happen during one insert?

**Candidate:** At most 2. Case 2 (inner child) requires 1 rotation to make it outer child. Case 3 (outer child) requires 1 rotation of grandparent. So maximum 2 rotations, and they're both O(1) pointer changes.

**Interviewer:** Implement insert with fixup.

**Candidate:** [Implements standard BST insert + fixup with 3 uncle-colour cases — see GUIDE.md]

---

### Part 2: Core Problem — Range Module (30 min)

**Interviewer:** Implement a RangeModule that tracks intervals [left, right). Support addRange, removeRange, and queryRange. Up to 10⁴ operations, values up to 10⁹.

**Candidate:** I'll use a `TreeMap<Integer, Integer>` where key = left endpoint, value = right endpoint. The RB tree keeps intervals sorted by left.

```java
class RangeModule {
    TreeMap<Integer, Integer> map = new TreeMap<>();

    public void addRange(int left, int right) {
        // Find floor entry
        Map.Entry<Integer, Integer> floor = map.floorEntry(left);

        // Merge with left neighbour if overlapping
        if (floor != null && floor.getValue() >= left) {
            left = Math.min(left, floor.getKey());
            right = Math.max(right, floor.getValue());
            map.remove(floor.getKey());
        }

        // Merge with right neighbours
        Map.Entry<Integer, Integer> next = map.ceilingEntry(left);
        while (next != null && next.getKey() <= right) {
            right = Math.max(right, next.getValue());
            map.remove(next.getKey());
            next = map.ceilingEntry(left);
        }

        map.put(left, right);
    }

    public boolean queryRange(int left, int right) {
        Map.Entry<Integer, Integer> floor = map.floorEntry(left);
        return floor != null && floor.getValue() >= right;
    }

    public void removeRange(int left, int right) {
        Map.Entry<Integer, Integer> floor = map.floorEntry(left);

        if (floor != null && floor.getValue() > left) {
            int leftPartEnd = left;
            int rightPartStart = right;
            int rightPartEnd = floor.getValue();

            map.remove(floor.getKey());

            if (floor.getKey() < leftPartEnd)
                map.put(floor.getKey(), leftPartEnd);
            if (rightPartEnd > rightPartStart)
                map.put(rightPartStart, rightPartEnd);
        }

        Map.Entry<Integer, Integer> next = map.ceilingEntry(left);
        while (next != null && next.getKey() < right) {
            int currRight = next.getValue();
            map.remove(next.getKey());
            if (currRight > right) map.put(right, currRight);
            next = map.ceilingEntry(left);
        }
    }
}
```

**Interviewer:** Walk me through addRange([15, 25]) when we have [10, 20].

**Candidate:**
- floorEntry(15) = [10, 20]
- floor.getValue() = 20 ≥ 15 → overlaps
- left = min(10, 15) = 10; right = max(20, 25) = 25
- Remove [10, 20]
- ceilingEntry(10) = none (map is empty after remove)
- Put [10, 25]

Result: [10, 25]. Merged.

**Interviewer:** Now removeRange([14, 16]) from [10, 25].

**Candidate:**
- floorEntry(14) = [10, 25]
- floor.getValue() = 25 > 14
- Remove [10, 25]
- floor.getKey()=10 < 14 → put [10, 14]
- floor.getValue()=25 > 16 → put [16, 25]
- ceilingEntry(14) = none (nothing between 14 and 16)

Result: [10, 14], [16, 25]

**Interviewer:** What's the time complexity?

**Candidate:** Each operation is O(k + log n) where k = number of intervals overlapped. log n for the TreeMap floor/ceiling lookup, and k for the iteration over overlapping intervals. In the worst case, a single operation can touch all O(n) intervals, but that's rare for non-overlapping intervals.

---

### Part 3: Follow-up (10 min)

**Interviewer:** How would you support querying all intervals that cover a given point?

**Candidate:** Walk the TreeMap. Find floor entry with key ≤ point. If its value > point, it covers. Then check ceiling entries whose key < point — none (since intervals are non-overlapping). So it's just the floor entry: O(log n).

For "all intervals covering point" in a system that allows overlapping intervals, I'd use an interval tree (augmented BST storing max right endpoint in each subtree).

**Interviewer:** How would you support exactly tracking which interval each point is in (like memory allocation)?

**Candidate:** Use the same TreeMap approach but with a two-way mapping: `TreeMap<Integer, Interval>` by start. For malloc/free semantics, also maintain a free list. Or use a balanced BST directly (Red-Black) with interval augmentation.

---

### Part 4: System Design (5 min)

**Interviewer:** Design a distributed interval management system for a calendar application.

**Candidate:**
1. **Sharding**: Partition by user ID (hash). Each shard manages intervals for its users.
2. **Storage**: Each shard has a TreeMap (or ConcurrentSkipListMap for concurrency) per user.
3. **Persistence**: Write-ahead log for durability. Periodic snapshot to database.
4. **Caching**: Hot users' intervals cached in memory.
5. **Conflict resolution**: Timestamp-based ordering. Latest write wins.
6. **Scale**: For 10M users, each with 1000 intervals, and 100 shards, each shard has 100K users × 1000 intervals = 100M intervals — too many. Instead, store intervals in database and cache only active users (~1% = 1M users × 100 intervals = 100M intervals — still high). Use Redis with TreeMap-like sorted sets for active users.

---

## Debrief

### What Went Well
- Clear explanation of RB properties
- Correct TreeMap-based RangeModule implementation
- Accurate trace of add/remove operations

### Areas for Growth
- Could mention AVL tree comparison for search-heavy workloads
- Delete fixup was not discussed (too complex for time)

### Score
| Category | Score (1-5) |
|----------|-------------|
| RB Tree Knowledge | 5 |
| Problem Decomposition | 5 |
| Code Quality | 5 |
| Complexity Analysis | 4 |
| Follow-up Handling | 4 |
| System Design | 4 |
| **Overall** | **4.5 / 5** |