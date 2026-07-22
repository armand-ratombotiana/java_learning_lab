# Interview Questions: Online Algorithms

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| LC 346 Moving Average from Data Stream | Easy | Google, Amazon, Microsoft | Sliding window / queue |
| LC 703 Kth Largest Element in a Stream | Easy | Google, Amazon, Meta | Min-heap |
| LC 170 Two Sum III - Data Structure | Easy | Meta, Amazon | HashMap |
| LC 362 Design Hit Counter | Medium | Google, Amazon, Microsoft | Queue / circular buffer |

Note: Online algorithms is primarily a **theory** topic. Most LeetCode problems about streaming data relate to online algorithm concepts.

## NeetCode Reference
- LC 703 Kth Largest Element in a Stream (NeetCode 150)
- LC 346 Moving Average (NeetCode All)

## Company-Specific Questions
### Google
- Design a caching strategy with competitive analysis (LRU vs FIFO vs LFU)
- What is the optimal strategy for the ski rental problem?
- Explain the secretary problem and its 1/e optimal stopping rule
- How does Google's load balancer handle online request distribution?
- Design an online algorithm for ad slot allocation

### Microsoft
- How does the Windows page replacement algorithm work?
- Design an online algorithm for Azure autoscaling
- Explain the Multiplicative Weights Update algorithm
- How would you implement online anomaly detection?

### Meta
- Design an online recommendation system with exploration-exploitation
- How would you A/B test with multi-armed bandits?
- Online clustering for social graph streams
- Budget allocation for ads with unknown user response rates

### Amazon
- How does DynamoDB handle adaptive capacity allocation?
- Design an online algorithm for inventory placement
- Online bin packing for warehouse robot storage
- Explain AWS auto-scaling as an online algorithm

### Apple
- Design an online battery management algorithm
- How would you predict user activity patterns online?
- Online compression for memory-constrained device storage
- Adaptive streaming bitrate selection over variable network conditions

### Oracle
- How does Oracle's adaptive query optimization work?
- Design an online statistics gathering algorithm
- Explain Oracle's dynamic sampling in query optimization
- Online algorithms for database index self-tuning

## Real Production Scenarios
- Scenario 1: CDN cache admission - using the adaptive replacement cache (ARC) algorithm to decide which content to cache and evict based on real-time access patterns
- Scenario 2: Adaptive bitrate streaming - implementing an online algorithm that selects video quality based on observed network throughput to minimize rebuffering while maximizing quality
- Scenario 3: Anomaly detection - debugging an online anomaly detection algorithm that produces too many false positives due to concept drift in the underlying data distribution

## Interview Tips
- Competitive ratio: worst-case performance ratio compared to optimal offline algorithm
- Key online problems: caching (LRU is k-competitive), ski rental (2-competitive), secretary (1/e-optimal)
- Multi-armed bandits balance exploration vs exploitation (UCB, Thompson Sampling, epsilon-greedy)
- Common edge cases: adversarial inputs, changing distributions (non-stationary), delayed feedback

## Java-Specific Considerations
- Moving average: `Queue<Integer>` with `int sum` for O(1) average computation
- Kth largest stream: `PriorityQueue<Integer>` (min-heap) of size k
- Hit counter: `Queue<Integer>` or `int[]` circular buffer with timestamps
- Pitfall: not handling overflow in sum/moving-average calculation for high-volume streams
- Pitfall: `PriorityQueue` ordering for kth largest requires `(a, b) -> a - b` for min-heap
- For competitive analysis: no Java-specific library; implement algorithms directly with standard collections
