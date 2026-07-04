# Why Graph Theory Matters

Graph theory models every connected system in the modern world.

## Applications

| Domain | Use |
|--------|-----|
| GPS Navigation | Shortest paths (Dijkstra, A*) |
| Social Networks | Friend recommendations, influence spread |
| Web Search | PageRank: web pages as nodes, links as edges |
| Biology | Protein interaction networks, neural connections |
| Operations | Supply chain optimization, routing |
| CS Theory | Dependency graphs, call graphs, data flow |

## In Java

```java
// JGraphT library provides graph algorithms
Graph<Integer, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
Graphs.addEdgeWithVertices(g, 1, 2);
DijkstraShortestPath.findPathBetween(g, 1, 3);
```
