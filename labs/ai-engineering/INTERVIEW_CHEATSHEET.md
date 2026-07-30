# Interview Cheatsheet — One-Page Quick Reference

## Coding Quick Reference

### Data Structures — Time Complexities
```
Array: Access O(1), Search O(n), Insert/Delete O(n)
String: Concatenation O(n), Substring O(n)
HashMap: Get/Put/Contains O(1), Iterate O(n)
TreeSet: Insert/Delete/Search O(log n)
PriorityQueue: Offer/Poll/Peek O(log n)
Stack/Queue: Push/Pop/Enqueue/Dequeue O(1)
```

### Common Sort Complexities
```
QuickSort: O(n log n) avg, O(n²) worst
MergeSort: O(n log n) always, O(n) space
HeapSort: O(n log n) always, O(1) space
Counting Sort: O(n + k) where k = range
```

### Binary Search Template
```java
int lo = 0, hi = n;
while (lo < hi) {            // or lo <= hi
    int mid = lo + (hi - lo) / 2;
    if (condition(mid)) hi = mid;  // or lo = mid + 1
    else lo = mid + 1;            // or hi = mid
}
```

### Graph Traversal
```java
// BFS
Queue<Node> q = new LinkedList<>();
Set<Node> visited = new HashSet<>();
q.add(start); visited.add(start);
while (!q.isEmpty()) {
    Node cur = q.poll();
    for (Node n : cur.neighbors)
        if (!visited.contains(n)) { q.add(n); visited.add(n); }
}
```

### 0/1 Knapsack DP
```java
int[] dp = new int[capacity + 1];
for (int i = 0; i < n; i++)
    for (int w = capacity; w >= weights[i]; w--)
        dp[w] = Math.max(dp[w], dp[w - weights[i]] + values[i]);
```

## System Design Quick Reference

### Scale Estimates
```
1M DAU × 10 req/user/day = 115 QPS avg (350 QPS peak)
1B requests/day = 11,500 QPS
1KB response × 115 QPS = 115 KB/s bandwidth
```

### AI Serving Math
```
Model size (GB) = params × bytes_per_param
Llama 7B FP16 = 7B × 2 = 14 GB
KV-cache per token = 2 × layers × hidden_dim × num_heads × bytes
Single A100 (80GB): max ~5 concurrent 7B models
```

### Must-Know Patterns
```
Caching: LRU, TTL, write-through, write-behind
Sharding: Consistent hashing, range-based, directory-based
Replication: Leader-follower, multi-leader, quorum
Consensus: Paxos, Raft, Zab
Rate Limiting: Token bucket, leaky bucket, sliding window
```

### AI Design Checklist
- [ ] Model loading strategy (eager vs. lazy)
- [ ] Batching (dynamic, continuous, inflight)
- [ ] Caching (response, prefix, KV-cache)
- [ ] Load balancing (round-robin, least connections, power of two)
- [ ] Observability (tokens, latency, cost, drift)
- [ ] Security (injection detection, access control, audit)
- [ ] Deployment (blue-green, canary, rollback)

## Behavioral Quick Reference

### STAR Formula
```
S: Context (1 sentence)
T: Your goal (1 sentence)
A: What YOU did (2-3 sentences)
R: Measurable impact (1-2 sentences)
```

### 10 Universal STAR Stories
1. Led a complex technical project
2. Debugged a production incident
3. Improved system performance
4. Mentored a colleague
5. Resolved a team conflict
6. Made a data-driven decision
7. Handled a difficult customer
8. Learned a new technology quickly
9. Caught and fixed a critical bug
10. Proposed and shipped an improvement

### Top AI-Specific Stories
- Optimized LLM inference latency
- Built a RAG system from scratch
- Detected and fixed data drift
- Implemented prompt injection defenses
- Designed a multi-agent orchestration system
- Reduced model deployment time with CI/CD

## Questions to Ask the Interviewer

- "What's the team's current biggest technical challenge?"
- "How do you evaluate model quality in production?"
- "What's the development and deployment cycle like?"
- "How does this team collaborate with ML researchers / product teams?"
- "What would success look like in the first 90 days?"