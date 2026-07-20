package com.capstone.vectordb;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class HNSWGraph {
    private final int M;
    private final int Mmax;
    private final int efConstruction;
    private final int mL;
    private final Map<String, Node> nodes = new ConcurrentHashMap<>();
    private final VectorIndex baseIndex;
    private int maxLayer = 0;

    public record Node(String id, float[] vector, int level, List<Set<String>> neighbors) {
        public Node {
            List<Set<String>> layers = new ArrayList<>();
            for (int i = 0; i <= level; i++) layers.add(new HashSet<>());
            neighbors = layers.stream().map(Collections::unmodifiableSet).collect(Collectors.toList());
        }
    }

    public HNSWGraph(VectorIndex baseIndex) {
        this(baseIndex, 16, 200);
    }

    public HNSWGraph(VectorIndex baseIndex, int M, int efConstruction) {
        this.baseIndex = baseIndex;
        this.M = M;
        this.Mmax = M;
        this.efConstruction = efConstruction;
        this.mL = (int) (1.0 / Math.log(M));
    }

    public void insert(String id, float[] vector) {
        int level = randomLevel();
        Node newNode = new Node(id, vector, level, null);
        if (nodes.isEmpty()) {
            maxLayer = level;
            nodes.put(id, newNode);
            return;
        }
        Node currEntry = nodes.values().iterator().next();
        for (int l = maxLayer; l > level; l--) {
            currEntry = searchLayer(vector, currEntry, 1, l);
        }
        for (int l = Math.min(level, maxLayer); l >= 0; l--) {
            List<Node> candidates = searchLayerCandidates(vector, currEntry, efConstruction, l);
            Set<String> neighbors = selectNeighbors(vector, candidates, M);
            newNode.neighbors().get(l).addAll(neighbors);
            for (String neighborId : neighbors) {
                Node neighbor = nodes.get(neighborId);
                if (neighbor != null) {
                    neighbor.neighbors().get(l).add(id);
                    if (neighbor.neighbors().get(l).size() > Mmax) {
                        shrinkNeighbors(neighbor, l);
                    }
                }
            }
        }
        nodes.put(id, newNode);
        if (level > maxLayer) maxLayer = level;
    }

    public List<VectorIndex.SearchResult> search(float[] query, int k, int ef) {
        if (nodes.isEmpty()) return List.of();
        Node entry = nodes.values().iterator().next();
        for (int l = maxLayer; l > 0; l--) {
            entry = searchLayer(query, entry, 1, l);
        }
        List<Node> candidates = searchLayerCandidates(query, entry, Math.max(ef, k), 0);
        candidates.sort(Comparator.comparingDouble(n ->
            -VectorIndex.cosineSimilarity(query, n.vector())));
        return candidates.stream()
            .limit(k)
            .map(n -> new VectorIndex.SearchResult(n.id(), VectorIndex.cosineSimilarity(query, n.vector())))
            .collect(Collectors.toList());
    }

    private Node searchLayer(float[] query, Node entry, int ef, int layer) {
        PriorityQueue<NodeDistance> candidates = new PriorityQueue<>(
            Comparator.comparingDouble(NodeDistance::distance));
        Set<String> visited = new HashSet<>();
        candidates.offer(new NodeDistance(entry, distance(query, entry.vector())));
        visited.add(entry.id());
        while (!candidates.isEmpty()) {
            NodeDistance current = candidates.poll();
            for (String neighborId : current.node().neighbors().get(layer)) {
                if (visited.add(neighborId)) {
                    Node neighbor = nodes.get(neighborId);
                    if (neighbor != null) {
                        candidates.offer(new NodeDistance(neighbor, distance(query, neighbor.vector())));
                    }
                }
            }
        }
        return candidates.isEmpty() ? entry : candidates.peek().node();
    }

    private List<Node> searchLayerCandidates(float[] query, Node entry, int ef, int layer) {
        PriorityQueue<NodeDistance> candidates = new PriorityQueue<>(
            Comparator.comparingDouble(NodeDistance::distance));
        TreeSet<NodeDistance> resultSet = new TreeSet<>(
            Comparator.comparingDouble(NodeDistance::distance));
        Set<String> visited = new HashSet<>();
        float entryDist = distance(query, entry.vector());
        candidates.offer(new NodeDistance(entry, entryDist));
        resultSet.add(new NodeDistance(entry, entryDist));
        visited.add(entry.id());
        while (!candidates.isEmpty()) {
            NodeDistance current = candidates.poll();
            NodeDistance furthest = resultSet.last();
            if (current.distance() > furthest.distance()) break;
            for (String neighborId : current.node().neighbors().get(layer)) {
                if (visited.add(neighborId)) {
                    Node neighbor = nodes.get(neighborId);
                    if (neighbor != null) {
                        float dist = distance(query, neighbor.vector());
                        if (dist < furthest.distance() || resultSet.size() < ef) {
                            candidates.offer(new NodeDistance(neighbor, dist));
                            resultSet.add(new NodeDistance(neighbor, dist));
                            if (resultSet.size() > ef) resultSet.pollLast();
                            furthest = resultSet.last();
                        }
                    }
                }
            }
        }
        return resultSet.stream().map(NodeDistance::node).collect(Collectors.toList());
    }

    private Set<String> selectNeighbors(float[] query, List<Node> candidates, int m) {
        return candidates.stream()
            .sorted(Comparator.comparingDouble(n -> distance(query, n.vector())))
            .limit(m)
            .map(Node::id)
            .collect(Collectors.toSet());
    }

    private void shrinkNeighbors(Node node, int layer) {
        List<String> neighbors = new ArrayList<>(node.neighbors().get(layer));
        neighbors.sort(Comparator.comparingDouble(n ->
            -distance(node.vector(), nodes.get(n).vector())));
        Set<String> reduced = new HashSet<>();
        for (int i = 0; i < M && i < neighbors.size(); i++) reduced.add(neighbors.get(i));
        node.neighbors().get(layer).retainAll(reduced);
    }

    private int randomLevel() {
        return (int) (-Math.log(ThreadLocalRandom.current().nextDouble()) * mL);
    }

    private float distance(float[] a, float[] b) {
        return -VectorIndex.cosineSimilarity(a, b);
    }

    private record NodeDistance(Node node, float distance) {}
}
