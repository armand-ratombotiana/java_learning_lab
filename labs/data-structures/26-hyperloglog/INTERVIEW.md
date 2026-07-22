# Interview Questions: HyperLogLog

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| (No standard LeetCode problems — system design focus) | — | Google, Meta, Amazon, Microsoft, Oracle | Cardinality estimation |

## NeetCode Reference
Not in NeetCode. HyperLogLog is a system design topic for large-scale counting.

## Company-Specific Questions

### Google
- Explain how HyperLogLog estimates distinct count (cardinality) — how does it use the maximum leading zeros as a proxy for uniqueness?
- What is the harmonic mean correction in HyperLogLog and why is it needed?
- How would you merge two HyperLogLog sketches from different shards to get the total distinct count?
- Design a system to count unique visitors to a website across 1000 servers with 1KB memory budget per server

### Microsoft
- Compare HyperLogLog vs exact counting using a HashSet — memory for 1B distinct elements
- How does HyperLogLog handle small cardinalities (e.g., < 5 distinct values)?
- Implement a simple HyperLogLog with 2^b registers and hash function

### Meta
- Design a system to count daily active users (DAU) across billions of accounts using HyperLogLog
- How would you detect trending topics using cardinality estimation (burst of distinct mentions)?
- Compare HyperLogLog vs Bloom filter cardinality estimation

### Amazon
- How would you use HyperLogLog in Redshift for approximate count-distinct queries?
- Design a system to count distinct product searches across all users in the last hour
- What register size (b) would you choose for estimating cardinalities up to 10^9 with 2% error?

### Apple
- How would you count unique device installs of an iOS app update using HyperLogLog?
- Design a system to estimate the number of unique viewers of a live streaming event
- How does HyperLogLog handle sparse vs dense data (small vs large cardinalities)?

### Oracle
- How does HyperLogLog work in Oracle Database's approximate count distinct feature?
- What is the standard error formula for HyperLogLog: 1.04 / sqrt(m)?
- Compare HyperLogLog vs Count-Min Sketch vs Bloom Filter for distinct counting

## Real Production Scenarios

- **Scenario 1: Unique Visitor Count** — An analytics platform tracks unique visitors per page per day across 100 web servers. Each server maintains a HyperLogLog sketch (1KB) for its traffic. At the end of the day, all 100 sketches are merged (bitwise OR of register arrays) to produce the total unique visitor count with ~2% error.

- **Scenario 2: Trend Detection** — A social media platform detects trending hashtags by maintaining a HyperLogLog per hashtag per minute. A hashtag "trends" when its distinct poster count exceeds a threshold. The HyperLogLog enables real-time counting of millions of hashtags with bounded memory.

- **Scenario 3: Database Query Optimization** — A query optimizer uses HyperLogLog on table columns to estimate the cardinality of join columns. An estimate of 10K distinct values in `users.id` vs 100B distinct values in `log.user_id` informs the optimizer to use a hash join vs sort-merge join.

## Interview Tips

- Time: O(1) per element add (hash + find leading zeros), O(m) for merge (m = 2^b registers)
- Space: m registers, typically 6 bits each → 6 * 2^b bits. With b=14: 2^14 * 6 bits ≈ 12KB for cardinalities up to ~10^9
- Error: standard error ≈ 1.04 / sqrt(m). For m=2^14: error ≈ 0.8%
- Common edge cases: empty set (cardinality = 0), very small cardinalities (use LinearCounting for bias correction), register overflow (unlikely with 6-bit counters)
- Merge: element-wise max of register arrays (only works if same b parameter)
- Hash function must produce uniform distribution — use MurmurHash3 or xxHash
- Hash prefix: first b bits determine register index; remaining bits find leading zeros (ρ)
- Sparse representation: for small cardinalities, store raw hashes instead of full register array

## Java-Specific Considerations

- No standard HyperLogLog in Java standard library — use Apache DataSketches or Guava's approximation
- `com.clearspring.analytics.stream.cardinality.HyperLogPlusPlus` (stream-lib library)
- `org.apache.datasketches.hll.HllSketch` (Apache DataSketches) — production-grade implementation
- Custom implementation:
  ```java
  class HyperLogLog {
      byte[] registers;  // 6-bit packed registers
      int b;             // precision
      int m;             // 1 << b
      HyperLogLog(int b) { this.b = b; this.m = 1 << b; registers = new byte[m]; }
  }
  ```
- For 6-bit registers: pack into bytes with bit shifting or use `short[]` for simplicity (more memory)
- Register update: `int idx = hash >>> (32 - b); int w = hash << b; int zeros = Integer.numberOfLeadingZeros(w) + 1; registers[idx] = (byte)Math.max(registers[idx], zeros);`
- `Integer.numberOfLeadingZeros()` is intrinsic (fast) in modern JVMs
- MurmurHash3 (Guava: `Hashing.murmur3_128()`) or `Hashing.sipHash24()` for hashing
- Sparse mode: use `HashMap<Integer, Integer>` for small cardinalities, switch to dense array at ~5 * 2^b elements
- Merge: `void merge(HyperLogLog other) { for (int i = 0; i < m; i++) registers[i] = (byte)Math.max(registers[i], other.registers[i]); }`
- Harmonic mean correction: `double sum = 0; for (byte r : registers) sum += 1.0 / (1 << r); double E = alpha * m * m / sum;`
- Bias correction: use lookup table for small cardinality corrections (standard algorithm)
- Thread safety: not thread-safe; use per-thread sketches + merge at query time
