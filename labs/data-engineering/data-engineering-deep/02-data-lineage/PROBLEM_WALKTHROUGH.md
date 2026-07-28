# Lab 02: Problem Walkthrough — Impact Analysis Engine

## Problem

Build a `LineageImpactAnalyzer` that, given a node ID, returns:
1. All downstream dependencies (direct and transitive)
2. The critical path (longest path to a leaf node)
3. A risk score based on number of consumers

## Walkthrough

### Step 1: Node and Graph

```java
public record LineageNode(String id, int complexity) {}

public class LineageGraph {
    Map<String, LineageNode> nodes = new HashMap<>();
    Map<String, List<String>> adjacency = new HashMap<>();
    public void addEdge(String from, String to) {
        adjacency.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
        adjacency.putIfAbsent(to, new ArrayList<>());
    }
}
```

### Step 2: All Downstream (BFS)

```java
public Set<String> getAllDownstream(String start) {
    Set<String> visited = new HashSet<>();
    Queue<String> queue = new LinkedList<>();
    queue.add(start);
    while (!queue.isEmpty()) {
        String cur = queue.poll();
        for (String next : adjacency.getOrDefault(cur, List.of())) {
            if (visited.add(next)) queue.add(next);
        }
    }
    return visited;
}
```

### Step 3: Critical Path (DFS with memoization)

```java
public int criticalPathLength(String node, Map<String, Integer> memo) {
    if (memo.containsKey(node)) return memo.get(node);
    int maxLen = 0;
    for (String next : adjacency.getOrDefault(node, List.of())) {
        maxLen = Math.max(maxLen, 1 + criticalPathLength(next, memo));
    }
    memo.put(node, maxLen);
    return maxLen;
}
```

### Step 4: Risk Score

```java
public double riskScore(String node) {
    var downstream = getAllDownstream(node);
    if (downstream.isEmpty()) return 0;
    double totalComplexity = downstream.stream()
        .mapToDouble(n -> nodes.getOrDefault(n, new LineageNode(n, 1)).complexity())
        .sum();
    return Math.min(100, totalComplexity / downstream.size() * 10);
}
```

### Complexity

- **Time**: O(V + E) for BFS/DFS traversal
- **Space**: O(V) for visited set + queue/stack
