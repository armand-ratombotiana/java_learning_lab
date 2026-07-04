# Security in Graph Theory

## Graph-Based Attacks

### Social Engineering

Attackers exploit social network graphs: target central nodes (influencers) or use shortest path to find attack vectors.

### Network Topology Analysis

Attackers map network graphs to find critical nodes for DDoS or infiltration.

### PageRank Manipulation

Link farms and spamdexing exploit the PageRank graph algorithm.

## Graph Algorithms in Security

### Intrusion Detection

Anomaly detection in network traffic graphs: sudden changes in edge weights, new connections, unusual paths.

### Dependency Graph Security

```java
// Circular dependency detection in package managers
// (topological sort fails → cycle → vulnerability)
```

### Firewall Rules

Graph-based analysis of network reachability: which nodes can communicate through which paths.

## Safe Graph Implementation

```java
// Defensive copy to avoid mutation
public List<Integer> neighbors(int v) {
    return Collections.unmodifiableList(adj.getOrDefault(v, List.of()));
}
```

## Cryptography

- **Elliptic curve cryptography**: points on a curve form a group under addition
- **Zero-knowledge proofs**: graph isomorphism is NP-intermediate
- **Blockchain**: Merkle trees, transaction graph analysis
