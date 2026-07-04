# Mental Models for Graph Theory

## The Map

Nodes are cities, edges are roads. Shortest path = fastest route, minimum spanning tree = cheapest road network connecting all cities.

## The Social Network

Nodes are people, edges are friendships. Clusters = communities, shortest path = degrees of separation, central nodes = influencers.

## The Tree Hierarchy

A tree is a connected graph with no cycles. Models: file systems, organizational charts, HTML DOM, tournament brackets, decision trees.

## The Flow Network

Nodes are junctions, edges are pipes with capacity. Max flow = maximum throughput from source to sink.

## The Dependency Graph

Edge $A \to B$ means "A depends on B". Topological order = valid build sequence. Cycles = circular dependency error.

## In Java

```java
// The JVM call stack is a tree (each thread)
// Class inheritance is a DAG
// The object graph (reachability) determines GC roots
```
