# Implementation Guide: Consistent Hashing

## 1. Basic Hash Ring

### Algorithm
1. Hash each node to a point on a circular ring (0 to 2^32-1)
2. Hash each key to a point on the same ring
3. Assign key to the first node encountered moving clockwise

### Implementation
```java
public class HashRing {
    private final TreeMap<Integer, Node> ring = new TreeMap<>();
    private final HashFunction hashFn;

    public void addNode(Node node) {
        int hash = hashFn.hash(node.id);
        ring.put(hash, node);
    }

    public Node getNode(String key) {
        int hash = hashFn.hash(key);
        Map.Entry<Integer, Node> entry = ring.ceilingEntry(hash);
        if (entry == null) entry = ring.firstEntry();
        return entry.getValue();
    }
}
```

### Problem
Without virtual nodes, distribution can be skewed if nodes hash to close positions. Adding/removing a node causes only N/m keys to remap (where N = total keys, m = number of nodes), which is optimal.

## 2. Virtual Nodes

### Concept
Each physical node is represented by multiple virtual nodes at different positions on the ring.

### Benefits
- Better key distribution (law of large numbers)
- Gradual weight changes (more vnodes = more capacity)
- Easier to handle heterogeneous node capacities

### Implementation
```java
for (int i = 0; i < virtualNodeCount; i++) {
    int hash = hashFn.hash(node.id + ":" + i);
    ring.put(hash, node);
}
```

## 3. Replication

### Concept
Keys are replicated to the next N nodes on the ring (successors).

### Write Path
1. Hash key to find primary node
2. Write to primary and next N-1 successors
3. Wait for W (write consistency level) acknowledgments

### Read Path
1. Hash key to find primary node
2. Read from primary and next N-1 successors
3. Wait for R (read consistency level) responses
4. Return most recent version (vector clock or timestamp)

## 4. Node Addition/Removal

### Adding a Node
- Only keys in the ring segment between the new node and its predecessor are remapped
- Approximately K/m keys move (optimal)

### Removing a Node
- Keys previously mapped to the removed node are reassigned to successors
- With replication: read repair brings replicas up to date

## 5. Sharding Strategies

| Strategy | Ring Assignment | Rebalance Cost |
|----------|----------------|---------------|
| Fixed partition | Predefined ranges | High (full rebuild) |
| Consistent hashing | Dynamic ring | Low (incremental) |
| Directory-based | Centralized mapping | Low (lookup overhead) |
