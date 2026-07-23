# LEETCODE_SOLUTIONS — Database Sharding

## Horizontal Scaling Solutions

| LeetCode Problem | Sharding Strategy | Partition Key |
|-----------------|------------------|---------------|
| 175 Combine Tables | User ID hash % N | `user_id % shard_count` |
| 185 Top 3 Salaries | Department ID hash | `dept_id % shard_count` |
| 380 Randomized Set | Key hash | Random data distribution |
| 706 HashMap | Key hash | Consistent hashing |
| 362 Hit Counter | Time-based | `timestamp / window` |

### Sharding Algorithm for LeetCode Problems
```java
public class ShardManager {
    private static final int SHARD_COUNT = 8;

    public int getShardForKey(int key) {
        return Math.abs(key) % SHARD_COUNT;
    }

    // For cross-shard queries (e.g., 185 Top 3 Salaries)
    public List<Integer> getAllShards() {
        return IntStream.range(0, SHARD_COUNT).boxed().collect(Collectors.toList());
    }
}
```

### Cross-Shard Challenges in LeetCode Problems
- **185 Top 3:** Requires querying all shards, then merging results
- **181 Emp > Manager:** If employee and manager on different shards
- **184 Dept Highest:** Department data must be co-located or merged
