# Interview Questions: Data Stream Algorithms

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| LC 295 Find Median from Data Stream | Hard | Google, Meta, Amazon, Microsoft | Two heaps |
| LC 346 Moving Average from Data Stream | Easy | Google, Amazon, Microsoft | Queue / sliding window |
| LC 703 Kth Largest Element in a Stream | Easy | Google, Amazon, Meta | Min-heap |
| LC 480 Sliding Window Median | Hard | Google | Two heaps / BST |
| LC 1825 Finding MK Average | Hard | Google | Three heaps / multiset |
| LC 1854 Maximum Population Year | Easy | Google, Amazon | Difference array / sweep |

## NeetCode Reference
- LC 295 Find Median from Data Stream (NeetCode 150)
- LC 703 Kth Largest Element in a Stream (NeetCode 150)

## Company-Specific Questions
### Google
- Design a system to count unique visitors in real-time (HyperLogLog)
- How would you find top-k frequent items in a billion-element stream?
- Implement Count-Min Sketch for frequency estimation
- Design a distributed streaming algorithm for Google Search query logs
- How does Google's Sawzall/Lumberjack handle stream analysis?

### Microsoft
- How does Azure Stream Analytics process streaming data?
- Design a sliding window aggregation system for IoT data
- Implement a real-time anomaly detection system
- How would you compute running statistics on a stream?

### Meta
- Design a system for real-time engagement metrics (likes, shares per second)
- How would you detect trending topics from a stream of posts?
- Implement a frequency estimation algorithm for content moderation
- Real-time A/B test result monitoring with sequential analysis

### Amazon
- Design a real-time inventory tracking system
- How does Kinesis process streaming sales data?
- Implement a top-K selling products dashboard
- Sliding window for detecting DDOS attacks on AWS

### Apple
- Stream processing for health data (Apple Watch heart rate)
- How would you detect anomalies in sensor data streams?
- Memory-efficient streaming on device with limited resources
- Real-time audio stream analysis for Siri keyword detection

### Oracle
- How does Oracle Streams / GoldenGate process data streams?
- Design a streaming database ingestion pipeline
- Explain Oracle's continuous query language (CQL) for streams
- How would you implement materialized view maintenance on a stream?

## Real Production Scenarios
- Scenario 1: Real-time analytics dashboard - using t-digest / HDR histogram to compute approximate percentiles for API latency monitoring across 100K requests/sec with sub-second freshness
- Scenario 2: Fraud detection - implementing a streaming algorithm (Count-Min Sketch + Bloom filter) to detect credit card transaction fraud patterns in real-time from a stream of 50K TPS
- Scenario 3: Trending topics - debugging a HyperLogLog implementation that reports 0 unique items due to incorrect hash function output distribution causing register collision

## Interview Tips
- Streaming constraints: O(1) or O(log n) per element, O(n) or less memory, single-pass
- Sketch algorithms trade accuracy for memory: Bloom filter (membership), Count-Min (frequency), HyperLogLog (cardinality)
- Two heaps for median: max-heap for lower half, min-heap for upper half, maintain size balance
- Common edge cases: stream smaller than window size, all equal values, high-cardinality streams

## Java-Specific Considerations
- Two heaps median: `PriorityQueue<Integer>` for both min and max heaps; `Collections.reverseOrder()` for max
- Moving average: `ArrayDeque<Integer>` with running sum for O(1) per element
- Sliding window median: two `TreeSet`s (simulated heap) for O(log k) removal
- Pitfall: `PriorityQueue.remove()` is O(n); use `TreeMap` with frequency counters for lazy removal
- Pitfall: integer overflow in stream sum for moving average (use `long` for sum)
- For sketches: implement `BitSet` for Bloom filter, `long[][]` for Count-Min Sketch with pairwise-independent hash functions
