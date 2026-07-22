# Interview Questions: Count-Min Sketch

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| (No standard LeetCode problems — system design focus) | — | Google, Meta, Amazon, Microsoft, Oracle | Frequency estimation / heavy hitters |

## NeetCode Reference
Not in NeetCode. Count-Min Sketch is a system design topic for streaming frequency estimation.

## Company-Specific Questions

### Google
- Explain how Count-Min Sketch estimates frequencies of elements in a stream with sub-linear memory
- How does the conservative update optimization improve Count-Min Sketch estimation?
- What is the "heavy hitters" problem and how does Count-Min Sketch help identify frequent elements?
- Compare Count-Min Sketch vs Hash Table for frequency counting — memory, accuracy, and time trade-offs

### Microsoft
- Implement a Count-Min Sketch with d hash functions and w columns — how do you choose d and w for 0.1% error?
- How would you use Count-Min Sketch to detect DDoS attacks (IP addresses with abnormally high request counts)?
- Design a system to track trending hashtags using Count-Min Sketch

### Meta
- How would you use Count-Min Sketch to count post views per user without storing per-view data?
- Design a system to detect sudden increases in error rates (heavy hitters with change detection)
- Compare Count-Min Sketch vs HyperLogLog for social graph analysis

### Amazon
- How would you use Count-Min Sketch to track product page views per session without logging every view?
- Design a system to identify hot keys in DynamoDB (keys accessed much more than average)
- How does a streaming frequency estimator help with load balancing decisions?

### Apple
- How would you track app crash counts per device model using Count-Min Sketch (limited memory)?
- Design a system for identifying popular search queries on the App Store
- How would you merge multiple Count-Min Sketches from different servers for global frequency estimation?

### Oracle
- How does Count-Min Sketch differ from a Bloom Filter? (Frequency vs membership)
- What are the error guarantees of Count-Min Sketch? (With prob 1-δ, error ≤ ε * N)
- How would you use Count-Min Sketch in Oracle Database for approximate query processing?
- Compare Count-Min Sketch vs Count-Mean-Min Sketch (error reduction with median of repeated experiments)

## Real Production Scenarios

- **Scenario 1: DDoS Detection** — A network security appliance tracks packets per source IP using a Count-Min Sketch. When an IP's estimated packet count exceeds a threshold, it's flagged for investigation. The sketch uses ~1MB of memory to track millions of IPs with high accuracy. False positives are resolved by checking the flagged IP against an exact counter.

- **Scenario 2: Hot Key Detection** — A distributed database (like DynamoDB) uses Count-Min Sketch to detect "hot" keys that receive disproportionate traffic. The sketch estimates per-key access frequency. Hot keys are re-partitioned or throttled to prevent uneven load distribution.

- **Scenario 3: Trending Topics** — A social media platform processes a stream of hashtag mentions. A Count-Min Sketch estimates the frequency of each hashtag in the current time window. Hashtags whose estimated frequency exceeds a threshold are added to the "trending" list. The sketch is reset every hour.

## Interview Tips

- Time: O(d) per update/query (d = number of hash functions, typically 3-10)
- Space: O(d * w) counters (w = width). For ε error, w = ceil(e/ε); for δ confidence, d = ceil(ln(1/δ))
- Error bounds: estimate ≥ true count; error ≤ ε * N with probability 1 - δ (where N = total stream size)
- Common edge cases: empty sketch (all zero), very small stream (ratio ε*N << 1), integer overflow of counters
- Count-Min Sketch always overestimates (or equals true count) — never underestimates
- Conservative update: only update a counter if the new value is greater than the current minimum across all rows
- For heavy hitters: query sketch for each element; if estimated freq > threshold * N, it's a heavy hitter
- Count-Mean-Min Sketch: subtract expected noise from each estimate for better accuracy

## Java-Specific Considerations

- No standard Count-Min Sketch in Java — implement from scratch
- Counter array: `int[][] counters = new int[d][w];` initialized to zeros
- Hash functions: use `Objects.hash(item)` then derive d hash functions via `h_i(x) = h(x) ^ (salt[i] * (h(x) >>> 32))`
- Use `Integer.MAX_VALUE / 2` or `long` for counters that may overflow
- `long[][]` if 64-bit counters needed (for very large streams)
- `void update(T item, int count) { for (int i = 0; i < d; i++) counters[i][hash(item, i) % w] += count; }`
- `int estimate(T item) { int min = Integer.MAX_VALUE; for (int i = 0; i < d; i++) min = Math.min(min, counters[i][hash(item, i) % w]); return min; }`
- Conservative update: `void updateConservative(T item, int count, int curEst) { ... }` — only increment counters that equal the current min
- Thread safety: `AtomicInteger[][]` for concurrent updates (higher contention); or per-thread sketches with merge
- Merge: `void merge(CMSketch other) { for (int i = 0; i < d; i++) for (int j = 0; j < w; j++) counters[i][j] += other.counters[i][j]; }`
- Guava does not include Count-Min Sketch; use `org.apache.datasketches.cpc` or `stream-lib` library
- Serialization: flat `byte[]` of header (d, w) + counter values for distributed sketches
