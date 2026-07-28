# Lab 02: Data Lineage — Implementation Guide

## Step 1: Lineage Graph Model

```java
public class LineageGraph {
    private final Map<String, DataNode> nodes = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> upstream = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> downstream = new ConcurrentHashMap<>();
}
```

## Step 2: DataNode

```java
public record DataNode(String id, NodeType type, String namespace, Dataset dataset) {
    public enum NodeType { DATASET, TRANSFORM, JOB }
}
```

## Step 3: Add Edge

```java
public void addEdge(String from, String to) {
    upstream.computeIfAbsent(to, k -> ConcurrentHashMap.newKeySet()).add(from);
    downstream.computeIfAbsent(from, k -> ConcurrentHashMap.newKeySet()).add(to);
}
```

## Step 4: Impact Analysis (Downstream)

```java
public Set<String> getDownstream(String nodeId) {
    Set<String> result = new HashSet<>();
    Queue<String> queue = new LinkedList<>();
    queue.add(nodeId);
    while (!queue.isEmpty()) {
        String current = queue.poll();
        for (String neighbor : downstream.getOrDefault(current, Set.of())) {
            if (result.add(neighbor)) queue.add(neighbor);
        }
    }
    return result;
}
```

## Step 5: Impact Analysis (Upstream / Root Cause)

```java
public Set<String> getUpstream(String nodeId) {
    Set<String> result = new HashSet<>();
    Queue<String> queue = new LinkedList<>();
    queue.add(nodeId);
    while (!queue.isEmpty()) {
        for (String neighbor : upstream.getOrDefault(queue.poll(), Set.of())) {
            if (result.add(neighbor)) queue.add(neighbor);
        }
    }
    return result;
}
```

## Step 6: Column-Level Lineage

```java
public record ColumnLineage(String column, String sourceDataset, String sourceColumn, String transform) {}
```

Extend `LineageGraph` with a `Map<String, List<ColumnLineage>>` keyed by `dataset.column`.
