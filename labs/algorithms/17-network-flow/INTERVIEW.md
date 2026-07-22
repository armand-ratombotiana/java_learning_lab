# Interview Questions: Network Flow

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| LC 3239 Minimum Number of Flips | Hard | Google | Max flow / min cut |
| No direct LC problems | - | - | System design focus |

Note: Network flow is primarily a **system design / operations research** topic. LeetCode has few direct problems; interviews focus on modeling real-world problems as flow networks.

## NeetCode Reference
Not directly covered in NeetCode 150. Relevant to advanced algorithm discussions.

## Company-Specific Questions
### Google
- Model a bipartite matching problem as a max flow problem
- How would you find edge-disjoint paths in a network?
- Design a flow network for YouTube CDN capacity planning
- Explain the max-flow min-cut theorem and its practical applications

### Microsoft
- How does Azure's network traffic engineering use flow algorithms?
- Design a flow network for Office 365 service migration
- Model job assignment as a flow problem
- How would you compute the minimum cut in a cloud network?

### Meta
- Model social network friend recommendation as a flow problem
- How would you find the maximum bipartite matching for ad placement?
- Design a flow network for content distribution
- Edge-disjoint paths for data center network redundancy

### Amazon
- Model warehouse robot task assignment as min-cost flow
- How does AWS Direct Connect use flow concepts?
- Design a supply chain flow network for inventory management
- Minimum cut for identifying network vulnerabilities

### Apple
- Modeling device-to-device data transfer as network flow
- Max flow for bandwidth allocation across apps
- How would you route traffic efficiently in a mesh network?

### Oracle
- How does Oracle's network layer handle connection pooling?
- Design a database replication flow network
- Model data migration as a flow optimization problem
- Explain Oracle's connection manager and traffic routing

## Real Production Scenarios
- Scenario 1: Video streaming CDN - using max flow to determine optimal bitrate encoding ladders and edge server assignment minimizing rebuffering across global viewer distribution
- Scenario 2: Supply chain optimization - modeling factory-to-warehouse-to-retail distribution as a min-cost flow to minimize total transportation costs under capacity constraints
- Scenario 3: Network capacity planning - debugging a network bottleneck by computing min-cut to identify which link upgrade would most improve overall throughput

## Interview Tips
- Max flow = min cut (duality); useful for both capacity planning and bottleneck identification
- Ford-Fulkerson (O(E * maxflow)), Edmonds-Karp (O(VE^2)), Dinic (O(V^2E) or O(E*sqrt(V)) for bipartite)
- Reduction is key: bipartite matching, edge-disjoint paths, baseball elimination, project selection
- Common edge cases: zero capacity edges, multiple sources/sinks, undirected edges

## Java-Specific Considerations
- Dinic's algorithm: `int[] level` for BFS layering, `int[] ptr` for DFS blocking flow
- Edge representation: `class Edge { int to, rev; long cap; }` with adjacency list
- Min-cost max flow: add `cost` field to Edge and use Bellman-Ford / SPFA for shortest augmenting path
- Pitfall: integer overflow in capacity sums (use `long` for large capacities)
- Pitfall: forgetting to add reverse edges with zero initial capacity
- `java.util.Queue` for BFS layering in Dinic; `int[]` arrays for performance
