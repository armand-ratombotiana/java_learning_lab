# Interview Questions: Push-Relabel Flow

## LeetCode Problem Map
No direct LeetCode problems. Advanced flow algorithms: push-relabel, min-cost max flow.

## NeetCode Reference
Not covered in NeetCode 150. This is advanced algorithm content for specialized roles.

## Company-Specific Questions
### Google
- Implement max flow using the push-relabel algorithm
- Explain the difference between Dinic and push-relabel (Goldberg-Tarjan)
- What is the gap heuristic and how does it improve push-relabel performance?
- Design a flow network for data center traffic engineering

### Microsoft
- How would you implement min-cost max flow for job scheduling?
- Compare augmenting path algorithms vs push-relabel for dense graphs
- Design a flow network for Azure network capacity planning
- How does push-relabel perform on GPU architectures?

### Meta
- Push-relabel for social graph analysis at scale
- How would you model content distribution as a max flow problem?
- Design a flow network for video transcoding job allocation

### Amazon
- Min-cost flow for supply chain optimization
- How would you use max flow for warehouse order picking routes?
- Design a flow network for last-mile delivery routing

### Apple
- Not typically asked; may appear in research roles
- Understanding of advanced flow theory for routing protocols
- How would you implement flow algorithms in resource-constrained systems?

### Oracle
- Network flow for database replication topology optimization
- How would you model data migration as a flow problem?
- Design a flow-based query routing system for RAC

## Real Production Scenarios
- Scenario 1: Traffic engineering - using min-cost max flow to route traffic across a software-defined WAN backbone minimizing latency while respecting link capacity constraints
- Scenario 2: Job scheduling - modeling compute job allocation across GPU clusters as a min-cost flow where each job requires specific resources and has priority weights
- Scenario 3: Network provisioning - debugging a network flow model that fails to converge due to capacity oversubscription and applying min-cut to identify bottleneck links

## Interview Tips
- Push-relabel maintains preflow (incoming >= outgoing) and heights for each node
- Key operations: push (send flow along edge with surplus) and relabel (increase height when stuck)
- Gap heuristic: if a height level becomes empty, all nodes above can be marked inactive
- Common edge cases: very wide graphs, unit capacity networks, multiple sources/sinks

## Java-Specific Considerations
- Edge representation: `class Edge { int to, rev; int cap; }` with adjacency list of `ArrayList<ArrayList<Edge>>`
- Node height: `int[] height = new int[n]`; sink height is 0, source height is n
- Excess flow: `int[] excess = new int[n]`; only source has initial excess
- Pitfall: forgetting to add reverse edges with cap=0 for residual graph
- Pitfall: infinite loops in push-relabel without proper gap heuristic
- Performance: use `int[]` arrays instead of ArrayList for hot paths in tight loops
- For min-cost max flow: add `cost` field and use potentials (Johnson's) for reduced costs
