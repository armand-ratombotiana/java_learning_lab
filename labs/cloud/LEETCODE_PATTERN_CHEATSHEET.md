# LeetCode Pattern Cheatsheet — Cloud Engineering

## Table of Contents

1. [Resource Scheduling & Allocation](#1-resource-scheduling--allocation)
2. [Load Balancing & Auto-Scaling](#2-load-balancing--auto-scaling)
3. [Distributed Storage Algorithms](#3-distributed-storage-algorithms)
4. [Networking & Routing](#4-networking--routing)
5. [Consistency & Replication](#5-consistency--replication)
6. [Rate Limiting & Throttling](#6-rate-limiting--throttling)
7. [Monitoring & Observability](#7-monitoring--observability)
8. [Security & Cryptography](#8-security--cryptography)
9. [Cost Optimization](#9-cost-optimization)
10. [Practice Problems by Cloud Topic](#10-practice-problems-by-cloud-topic)

---

## 1. Resource Scheduling & Allocation

### Key Patterns

| Pattern | Description | Cloud Use Case | LeetCode Problems |
|---------|-------------|----------------|-------------------|
| Interval Scheduling | Select non-overlapping intervals to maximize resource utilization | VM placement, container scheduling, batch job scheduling | Non-overlapping Intervals (435), Meeting Rooms II (253), Minimum Number of Arrows (452) |
| Task Scheduling with Cooldown | Schedule tasks with minimum gaps | CPU scheduling in VM, Lambda concurrency limits | Task Scheduler (621), Rearrange String (358), Reorganize String (767) |
| Greedy Resource Allocation | Allocate constrained resources to maximize utility | Spot instance bidding, reserved instance allocation | Maximum Units on a Truck (1710), Two City Scheduling (1029), Minimum Cost to Connect Sticks (1167) |
| Load-Aware Scheduling | Assign tasks to machines balancing load | Auto-scaling group distribution, shard rebalancing | Maximum Average Pass Ratio (1792), Minimum Time to Complete Trips (2187) |
| Priority Queue Scheduling | Process tasks by priority | SQS FIFO priority, ECS task placement | Minimum Cost to Connect Sticks (1167), Find K Pairs with Smallest Sums (373) |

### Cloud Resource Scheduling Algorithm

```java
// Container scheduling on VMs — Best Fit Decreasing
// Problem: Pack containers into minimum VMs, similar to bin packing
// LeetCode: Minimum Number of Refueling Stops (871), Car Fleet (853)

public int minVMs(int[] containerSizes, int vmCapacity) {
    Arrays.sort(containerSizes); // Sort descending
    TreeMap<Integer, Integer> remaining = new TreeMap<>();
    // remaining maps remaining capacity -> count of VMs
    
    for (int size : containerSizes) {
        // Find the VM with the smallest remaining capacity that can fit this container
        Integer key = remaining.ceilingKey(size);
        if (key != null) {
            int newCap = key - size;
            remaining.put(key, remaining.get(key) - 1);
            if (remaining.get(key) == 0) remaining.remove(key);
            remaining.merge(newCap, 1, Integer::sum);
        } else {
            remaining.merge(vmCapacity - size, 1, Integer::sum);
        }
    }
    return remaining.values().stream().mapToInt(Integer::intValue).sum();
}
```

### Auto-Scaling Logic

```java
// LeetCode style: Implement simple auto-scaling algorithm
// Scale up when CPU > 70%, scale down when CPU < 30%

public int autoScale(int[] cpuUtilization, int currentInstances, 
                     int minInstances, int maxInstances) {
    int instances = currentInstances;
    for (int cpu : cpuUtilization) {
        if (cpu > 70 && instances < maxInstances) {
            instances = Math.min(maxInstances, (int) Math.ceil(instances * 1.5));
        } else if (cpu < 30 && instances > minInstances) {
            instances = Math.max(minInstances, instances / 2);
        }
        // Cooldown period simulation
    }
    return instances;
}
```

---

## 2. Load Balancing & Auto-Scaling

### Key Patterns

| Pattern | Description | LeetCode Problems |
|---------|-------------|-------------------|
| Round Robin | Distribute requests sequentially | Queue-based problems, Design Circular Queue (622) |
| Weighted Round Robin | Distribute based on capacity weights | Hand of Straights (846), Task Scheduler (621) |
| Least Connections | Route to least loaded server | Find Server That Handled Most Requests (1606) |
| Consistent Hashing | Minimize rehashing on node changes | Design HashMap (706), Design HashSet (705) |
| Two Pointer Work Distribution | Split work between two workers | Capacity To Ship Packages Within D Days (1011), Split Array Largest Sum (410) |

### Consistent Hashing Implementation

```java
// Core pattern for distributed caching, data partitioning
// LeetCode: Design HashMap (706), Insert Delete GetRandom O(1) (380)

class ConsistentHash {
    private final int VIRTUAL_NODES = 150;
    private final TreeMap<Integer, String> ring = new TreeMap<>();
    private final Map<String, List<Integer>> nodePositions = new HashMap<>();
    
    public void addNode(String node) {
        List<Integer> positions = new ArrayList<>();
        for (int i = 0; i < VIRTUAL_NODES; i++) {
            int hash = hash(node + "#" + i);
            ring.put(hash, node);
            positions.add(hash);
        }
        nodePositions.put(node, positions);
    }
    
    public void removeNode(String node) {
        for (int pos : nodePositions.getOrDefault(node, List.of())) {
            ring.remove(pos);
        }
        nodePositions.remove(node);
    }
    
    public String getNode(String key) {
        if (ring.isEmpty()) return null;
        int hash = hash(key);
        Map.Entry<Integer, String> entry = ring.ceilingEntry(hash);
        if (entry == null) entry = ring.firstEntry(); // Wrap around
        return entry.getValue();
    }
    
    private int hash(String key) {
        return key.hashCode() & 0x7fffffff;
    }
}
```

### Load Balancer Patterns

```java
// Weighted Round Robin Load Balancer

class WeightedRoundRobin {
    private final List<String> servers;
    private final int[] weights;
    private int current = 0;
    private int currentWeight = 0;
    
    public String getNext() {
        while (true) {
            current = (current + 1) % servers.size();
            if (current == 0) {
                currentWeight = currentWeight - gcd(weights);
                if (currentWeight <= 0) {
                    currentWeight = max(weights);
                    if (currentWeight == 0) return null;
                }
            }
            if (weights[current] >= currentWeight) {
                return servers.get(current);
            }
        }
    }
    
    private int gcd(int[] arr) {
        int result = arr[0];
        for (int i = 1; i < arr.length; i++) {
            result = gcd(result, arr[i]);
        }
        return result;
    }
    
    private int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }
    
    private int max(int[] arr) {
        return Arrays.stream(arr).max().getAsInt();
    }
}
```

---

## 3. Distributed Storage Algorithms

### Key Patterns

| Pattern | Description | LeetCode Problems |
|---------|-------------|-------------------|
| Consistent Hashing | Distribute data across nodes with minimal reshuffling | Design HashMap (706), Design Twitter (355) |
| Merkle Trees | Verify data integrity across replicas | Unique Binary Search Trees II (95), Recover Binary Search Tree (99) |
| Bloom Filters | Probabilistic existence check (no false negatives) | Design Phone Directory (379), Num Matrix (304 can be adapted) |
| Quorum Reads/Writes | Manage read/write consistency in distributed systems | Not directly in LC, but concurrency patterns apply |
| CAP Theorem Evaluation | Trade-off analysis in system design | Design Tic-Tac-Toe (348 — similar consistency patterns) |

### Bloom Filter Implementation

```java
// Used in cloud systems for cache optimization, 
// distributed key existence checks, resource filtering

class BloomFilter {
    private BitSet bits;
    private int bitSize;
    private int hashCount;
    
    public BloomFilter(int expectedElements, double falsePositiveRate) {
        this.bitSize = (int) (-expectedElements * Math.log(falsePositiveRate) / (Math.log(2) * Math.log(2)));
        this.hashCount = (int) (bitSize / expectedElements * Math.log(2));
        this.bits = new BitSet(bitSize);
    }
    
    public void add(String key) {
        for (int i = 0; i < hashCount; i++) {
            int hash = hash(key, i) % bitSize;
            bits.set(hash);
        }
    }
    
    public boolean mightContain(String key) {
        for (int i = 0; i < hashCount; i++) {
            int hash = hash(key, i) % bitSize;
            if (!bits.get(hash)) return false;
        }
        return true; // False positive possible, no false negative
    }
    
    private int hash(String key, int seed) {
        return (key.hashCode() ^ (seed * 0x9e3779b9)) & 0x7fffffff;
    }
}
```

### S3-like Object Storage Index

```java
// Design a simple key-value storage system
// LeetCode: Design HashMap (706), Design In-Memory File System (588)

class ObjectStore {
    class Node {
        Map<String, Node> children = new HashMap<>();
        String content;
        boolean isFile;
    }
    private Node root;
    
    public void put(String path, String content) {
        Node curr = root;
        for (String part : path.split("/")) {
            if (part.isEmpty()) continue;
            curr.children.putIfAbsent(part, new Node());
            curr = curr.children.get(part);
        }
        curr.content = content;
        curr.isFile = true;
    }
    
    public String get(String path) {
        Node curr = root;
        for (String part : path.split("/")) {
            if (part.isEmpty()) continue;
            if (!curr.children.containsKey(part)) return null;
            curr = curr.children.get(part);
        }
        return curr.isFile ? curr.content : null;
    }
}
```

---

## 4. Networking & Routing

### Key Patterns

| Pattern | Description | LeetCode Problems |
|---------|-------------|-------------------|
| Shortest Path Routing | Find optimal network path | Network Delay Time (743), Cheapest Flights Within K Stops (787) |
| Subnet Calculation | CIDR notation, subnet mask computation | IP to CIDR (751), Restore IP Addresses (93) |
| BFS Network Flood | Discover all reachable nodes | Number of Connected Components (323), All Paths From Source to Target (797) |
| Firewall Rules | Match IP ranges, allow/deny rules | Design Log Storage System (635), Design Phone Directory (379) |
| Topological Sort | Dependency resolution, build order | Course Schedule (207), Course Schedule II (210) |

### VPC Subnet Calculator Pattern

```java
// LeetCode: IP to CIDR (751)
// Problem: Given IP and prefix length, determine subnet information

public class SubnetCalculator {
    public String[] getSubnetInfo(String ip, int prefixLength) {
        int ipInt = ipToInt(ip);
        int mask = prefixLength == 0 ? 0 : (-1 << (32 - prefixLength));
        int network = ipInt & mask;
        int broadcast = network | ~mask;
        int firstHost = network + 1;
        int lastHost = broadcast - 1;
        int totalHosts = (int) Math.pow(2, 32 - prefixLength) - 2;
        
        return new String[] {
            intToIp(network),    // Network address
            intToIp(broadcast),  // Broadcast address
            intToIp(firstHost),  // First usable host
            intToIp(lastHost),   // Last usable host
            String.valueOf(totalHosts) // Total usable hosts
        };
    }
    
    private int ipToInt(String ip) {
        String[] parts = ip.split("\\.");
        int result = 0;
        for (String part : parts) {
            result = (result << 8) | Integer.parseInt(part);
        }
        return result;
    }
    
    private String intToIp(int ip) {
        return String.format("%d.%d.%d.%d", 
            (ip >> 24) & 0xFF, (ip >> 16) & 0xFF, 
            (ip >> 8) & 0xFF, ip & 0xFF);
    }
}
```

### Routing Algorithm (OSPF-like)

```java
// LeetCode: Network Delay Time (743)
// Dijkstra for shortest path routing in cloud networks

public int networkDelayTime(int[][] times, int n, int k) {
    Map<Integer, List<int[]>> graph = new HashMap<>();
    for (int[] time : times) {
        graph.computeIfAbsent(time[0], x -> new ArrayList<>())
             .add(new int[]{time[1], time[2]});
    }
    
    int[] dist = new int[n + 1];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[k] = 0;
    
    PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[1] - b[1]);
    pq.offer(new int[]{k, 0});
    
    while (!pq.isEmpty()) {
        int[] curr = pq.poll();
        int node = curr[0], d = curr[1];
        if (d > dist[node]) continue;
        
        for (int[] edge : graph.getOrDefault(node, List.of())) {
            int next = edge[0], w = edge[1];
            if (dist[next] > d + w) {
                dist[next] = d + w;
                pq.offer(new int[]{next, dist[next]});
            }
        }
    }
    
    int max = 0;
    for (int i = 1; i <= n; i++) {
        if (dist[i] == Integer.MAX_VALUE) return -1;
        max = Math.max(max, dist[i]);
    }
    return max;
}
```

---

## 5. Consistency & Replication

### Key Patterns

| Pattern | Description | LeetCode Problems |
|---------|-------------|-------------------|
| Eventual Consistency | Data converges over time | Design Tic-Tac-Toe (348), Design Snake Game (353) |
| Strong Consistency | All reads see latest write | Array-based problems with atomic operations |
| CRDTs | Conflict-free replicated data types | Design Add and Search Words Data Structure (211) |
| Leader Election | Select one node as active coordinator | Best Time to Buy and Sell Stock (121 — leader election strategy) |
| Vector Clocks | Track causality across distributed nodes | Merge Intervals (56), Insert Interval (57) |

### Leader Election Pattern

```java
// Bully Algorithm for leader election in distributed systems

class LeaderElection {
    private final int nodeId;
    private int leaderId;
    private final int maxNodes;
    private boolean electionInProgress;
    
    public LeaderElection(int nodeId, int maxNodes) {
        this.nodeId = nodeId;
        this.maxNodes = maxNodes;
        this.leaderId = -1;
    }
    
    public synchronized void startElection() {
        electionInProgress = true;
        boolean gotResponse = false;
        
        // Send election messages to higher-numbered nodes
        for (int i = nodeId + 1; i <= maxNodes; i++) {
            if (sendElectionMessage(i)) {
                gotResponse = true;
            }
        }
        
        if (!gotResponse) {
            // No higher nodes responded, I'm the leader
            leaderId = nodeId;
            announceVictory(nodeId);
        }
        electionInProgress = false;
    }
    
    public synchronized void receiveElection(int fromNode) {
        if (fromNode < nodeId && !electionInProgress) {
            // Higher node responds, start own election
            startElection();
        }
    }
    
    private boolean sendElectionMessage(int targetNode) {
        // Simulate sending message — in real system, this would be network call
        return targetNode < maxNodes; // Simulated response
    }
    
    private void announceVictory(int winnerId) {
        // Broadcast to all lower nodes
        System.out.println("Node " + winnerId + " is the new leader");
    }
}
```

---

## 6. Rate Limiting & Throttling

### Key Patterns

| Pattern | Description | LeetCode Problems |
|---------|-------------|-------------------|
| Token Bucket | Fixed rate, burst allowance | Design Hit Counter (362) |
| Leaky Bucket | Smooth output rate | Moving Average from Data Stream (346) |
| Sliding Window Log | Time-based request counting | Logger Rate Limiter (359) |
| Sliding Window Counter | Fixed window with bucket count | Number of Recent Calls (933) |
| Distributed Rate Limiting | Rate limit across multiple nodes | Design Underground System (1396 — similar distributed state) |

### Sliding Window Rate Limiter

```java
// LeetCode: Logger Rate Limiter (359), Design Hit Counter (362)

class RateLimiter {
    private final int maxRequests;
    private final long windowSizeMillis;
    private final Queue<Long> requestTimestamps;
    
    public RateLimiter(int maxRequests, long windowSizeSeconds) {
        this.maxRequests = maxRequests;
        this.windowSizeMillis = windowSizeSeconds * 1000;
        this.requestTimestamps = new LinkedList<>();
    }
    
    public synchronized boolean allowRequest() {
        long now = System.currentTimeMillis();
        long windowStart = now - windowSizeMillis;
        
        // Remove timestamps outside current window
        while (!requestTimestamps.isEmpty() && 
               requestTimestamps.peek() < windowStart) {
            requestTimestamps.poll();
        }
        
        if (requestTimestamps.size() < maxRequests) {
            requestTimestamps.offer(now);
            return true;
        }
        return false;
    }
}

// Token Bucket Rate Limiter
class TokenBucket {
    private final long maxTokens;
    private final long refillRate;
    private long currentTokens;
    private long lastRefillTime;
    
    public TokenBucket(long maxTokens, long refillRate) {
        this.maxTokens = maxTokens;
        this.refillRate = refillRate;
        this.currentTokens = maxTokens;
        this.lastRefillTime = System.currentTimeMillis();
    }
    
    public synchronized boolean tryConsume(long tokens) {
        refill();
        if (currentTokens >= tokens) {
            currentTokens -= tokens;
            return true;
        }
        return false;
    }
    
    private void refill() {
        long now = System.currentTimeMillis();
        long elapsed = now - lastRefillTime;
        long tokensToAdd = (elapsed / 1000) * refillRate;
        currentTokens = Math.min(maxTokens, currentTokens + tokensToAdd);
        lastRefillTime = now;
    }
}
```

---

## 7. Monitoring & Observability

### Key Patterns

| Pattern | Description | LeetCode Problems |
|---------|-------------|-------------------|
| Sliding Window Metrics | Moving average, percentile over time | Moving Average from Data Stream (346), Find Median from Data Stream (295) |
| Top-K Monitoring | Most frequent, highest latency endpoints | Top K Frequent Elements (347), Top K Frequent Words (692) |
| Anomaly Detection | Identify outliers in metrics | Identify outlier using simple stats (no direct LC) |
| Distributed Tracing | Track request across services | Serialize and Deserialize N-ary Tree (428 — trace context passing) |
| Log Aggregation | Merge logs from multiple sources | Merge k Sorted Lists (23), Merge Sorted Array (88) |

### Top-N Monitoring

```java
// LeetCode: Top K Frequent Elements (347)
// Monitor top-N highest-traffic endpoints

public List<Integer> topKFrequent(int[] nums, int k) {
    Map<Integer, Integer> freq = new HashMap<>();
    for (int num : nums) freq.merge(num, 1, Integer::sum);
    
    PriorityQueue<Map.Entry<Integer, Integer>> minHeap =
        new PriorityQueue<>(Map.Entry.comparingByValue());
    
    for (Map.Entry<Integer, Integer> entry : freq.entrySet()) {
        minHeap.offer(entry);
        if (minHeap.size() > k) minHeap.poll();
    }
    
    return minHeap.stream()
                  .map(Map.Entry::getKey)
                  .collect(Collectors.toList());
}
```

### Percentile Calculation (P99 Latency)

```java
// Cloud latency SLO monitoring — track P50, P95, P99

class PercentileTracker {
    private final int[] buckets;
    private long total;
    private final int[] percentiles;
    
    public PercentileTracker(int maxLatencyMs, int... percentiles) {
        this.buckets = new int[maxLatencyMs + 1];
        this.percentiles = percentiles;
    }
    
    public synchronized void recordLatency(int latencyMs) {
        if (latencyMs < buckets.length) {
            buckets[latencyMs]++;
            total++;
        }
    }
    
    public Map<Integer, Integer> getPercentiles() {
        Map<Integer, Integer> result = new HashMap<>();
        for (int p : percentiles) {
            long target = (total * p + 99) / 100;
            long count = 0;
            for (int i = 0; i < buckets.length; i++) {
                count += buckets[i];
                if (count >= target) {
                    result.put(p, i);
                    break;
                }
            }
        }
        return result;
    }
}
```

---

## 8. Security & Cryptography

### Key Patterns

| Pattern | Description | LeetCode Problems |
|---------|-------------|-------------------|
| Encryption/Decryption | AES, RSA (conceptual) | Encode and Decode TinyURL (535) |
| Hashing for Integrity | SHA, MD5 for data verification | Decode String (394) |
| JWT Token Validation | Token structure, expiry, signature | Basic Calculator II (227 — parsing patterns) |
| Access Control Matrix | Permission checking for resources | Insert Delete GetRandom O(1) (380) |
| Secure Random Generation | Cryptographically secure random | Random Pick Index (398), Random Pick with Weight (528) |

### URL Shortener (similar to cloud resource naming)

```java
// LeetCode: Encode and Decode TinyURL (535)
// Similar to S3 key generation, resource naming patterns

public class URLShortener {
    private static final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int KEY_LENGTH = 7;
    private Map<String, String> shortToLong = new HashMap<>();
    private Map<String, String> longToShort = new HashMap<>();
    private Random random = new Random();
    
    public String encode(String longUrl) {
        if (longToShort.containsKey(longUrl)) {
            return longToShort.get(longUrl);
        }
        StringBuilder sb = new StringBuilder();
        do {
            sb.setLength(0);
            for (int i = 0; i < KEY_LENGTH; i++) {
                sb.append(BASE62.charAt(random.nextInt(62)));
            }
        } while (shortToLong.containsKey(sb.toString()));
        
        String shortUrl = sb.toString();
        shortToLong.put(shortUrl, longUrl);
        longToShort.put(longUrl, shortUrl);
        return shortUrl;
    }
    
    public String decode(String shortUrl) {
        return shortToLong.get(shortUrl);
    }
}
```

---

## 9. Cost Optimization

### Key Patterns

| Pattern | Description | LeetCode Problems |
|---------|-------------|-------------------|
| Knapsack Optimization | Choose resources to maximize value under budget | Maximum Units on a Truck (1710), DP with cost constraints |
| Scheduling for Cost | Run jobs when cheapest | Minimum Cost to Hire K Workers (857) |
| Resource Right-Sizing | Choose instance type that fits | DP for capacity planning |
| Spot Instance Bidding | Bid optimally for spot compute | DP for auction strategy |

### Reserved Instance Optimization

```java
// Problem: Given on-demand cost, RI cost, and usage pattern,
// determine optimal number of RIs to purchase

public int optimalReservedInstances(int[] hourlyUsage, int onDemandPrice, 
                                     int riPrice, int riTermHours) {
    // DP approach: minimize cost over the term
    int n = hourlyUsage.length;
    int[][] dp = new int[n + 1][n + 1]; // dp[hour][riCount]
    
    for (int h = 1; h <= n; h++) {
        for (int r = 0; r <= n; r++) {
            int usage = hourlyUsage[h - 1];
            int riCovered = Math.min(usage, r);
            int onDemand = usage - riCovered;
            dp[h][r] = dp[h-1][r] + riCovered * 0 + onDemand * onDemandPrice;
            // No hourly cost for RI (already paid upfront)
        }
    }
    
    // Add RI cost and find minimum
    int minCost = Integer.MAX_VALUE;
    for (int r = 0; r <= n; r++) {
        int totalCost = dp[n][r] + r * riPrice;
        minCost = Math.min(minCost, totalCost);
    }
    return minCost;
}
```

---

## 10. Practice Problems by Cloud Topic

| Cloud Topic | LeetCode Problems | Difficulty |
|-------------|------------------|------------|
| **Compute (EC2/VM)** | | |
| VM auto-scaling | 1011 Capacity To Ship Packages Within D Days | Medium |
| Container scheduling | 621 Task Scheduler | Medium |
| Batch job scheduling | 252 Meeting Rooms, 253 Meeting Rooms II | Easy/Medium |
| **Storage (S3/Blob)** | | |
| Object storage design | 588 Design In-Memory File System | Hard |
| Key-value store | 706 Design HashMap | Easy |
| Cache invalidation | 146 LRU Cache, 460 LFU Cache | Medium/Hard |
| **Networking** | | |
| Shortest path routing | 743 Network Delay Time | Medium |
| DNS resolution | 535 Encode and Decode TinyURL | Medium |
| Firewall rules | 751 IP to CIDR | Medium |
| **Databases** | | |
| Data sharding | 642 Design Search Autocomplete | Hard |
| Replication lag | 432 All O`one Data Structure | Hard |
| Index design | 208 Implement Trie | Medium |
| **Security** | | |
| IAM policy evaluation | 681 Next Closest Time (parsing) | Medium |
| Encryption key storage | 535 Encode Decode TinyURL | Medium |
| **Serverless** | | |
| Function scheduling | 767 Reorganize String | Medium |
| Cold start optimization | 739 Daily Temperatures (warm-up) | Medium |
| **Containers/K8s** | | |
| Pod scheduling | 1353 Maximum Number of Events | Medium |
| Resource limits | 871 Minimum Refueling Stops | Hard |
| **Monitoring** | | |
| Metrics aggregation | 295 Find Median from Data Stream | Hard |
| Anomaly detection | 347 Top K Frequent Elements | Medium |
| **Cost Optimization** | | |
| Rightsizing | 416 Partition Equal Subset Sum | Medium |
| Reserved Instances | DP on intervals | Hard |

### Study Plan (4 weeks)

| Week | Focus | Problems/Day |
|------|-------|--------------|
| 1 | Arrays, Strings, Hash Maps (core patterns) | 2 easy + 1 medium |
| 2 | Trees, Graphs, DP (routing, scheduling) | 2 medium |
| 3 | System Design relevant: LRU, consistent hashing, rate limiter | 1-2 medium/hard |
| 4 | Full mock system design interviews | 1 per day |

---

*Last updated: July 2026*
