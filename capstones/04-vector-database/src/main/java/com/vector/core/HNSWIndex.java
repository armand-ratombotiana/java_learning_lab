package com.vector.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class HNSWIndex {

    private final int m;
    private final int maxLayers;
    private final double efConstruction;
    private final Random random;

    private final Map<Integer, List<HNSWNode>> graph;
    private int currentMaxLayer;

    public HNSWIndex() {
        this.m = 16;
        this.maxLayers = 16;
        this.efConstruction = 200;
        this.random = new Random(42);
        this.graph = new ConcurrentHashMap<>();
        this.currentMaxLayer = 0;
    }

    public void insert(int id, double[] vector) {
        HNSWNode newNode = new HNSWNode(id, vector, selectLevel());

        if (graph.isEmpty()) {
            for (int l = 0; l <= newNode.level; l++) {
                graph.put(l, new ArrayList<>());
            }
            graph.get(newNode.level).add(newNode);
            currentMaxLayer = newNode.level;
            return;
        }

        int epId = graph.get(0).get(0).id;
        double[] epVector = graph.get(0).get(0).vector;

        for (int level = newNode.level; level >= 0; level--) {
            List<HNSWNode> layer = graph.computeIfAbsent(level, k -> new ArrayList<>());

            List<HNSWNode> candidates = searchLayer(epVector, epId, 1, level);
            List<HNSWNode> neighbors = selectNeighbors(candidates, m, level);

            for (HNSWNode neighbor : neighbors) {
                neighbor.connections.computeIfAbsent(level, k -> new ArrayList<>()).add(newNode);
                if (neighbor.connections.get(level).size() > m * 2) {
                    neighbor.connections.get(level).remove(random.nextInt(neighbor.connections.get(level).size()));
                }
            }

            neighbors.add(newNode);
            layer.add(newNode);
            newNode.connections.put(level, new ArrayList<>(neighbors.subList(0, Math.min(neighbors.size(), m))));

            if (!candidates.isEmpty()) {
                epId = candidates.get(0).id;
                epVector = candidates.get(0).vector;
            }
        }

        log.debug("Inserted vector id: {} level: {}", id, newNode.level);
    }

    public List<SearchResult> search(double[] queryVector, int k, int ef) {
        if (graph.isEmpty()) {
            return List.of();
        }

        int epId = graph.get(0).get(0).id;
        double[] epVector = graph.get(0).get(0).vector;

        for (int level = currentMaxLayer; level > 0; level--) {
            List<HNSWNode> layer = graph.get(level);
            if (layer == null || layer.isEmpty()) continue;

            List<HNSWNode> results = searchLayer(epVector, epId, 1, level);
            if (!results.isEmpty()) {
                epId = results.get(0).id;
                epVector = results.get(0).vector;
            }
        }

        PriorityQueue<HNSWNode> currentCandidates = new PriorityQueue<>(
            Comparator.comparingDouble(n -> -euclideanDistance(queryVector, n.vector))
        );
        currentCandidates.addAll(searchLayer(epVector, epId, ef, 0));

        PriorityQueue<HNSWNode> topResults = new PriorityQueue<>(
            Comparator.comparingDouble(n -> euclideanDistance(queryVector, n.vector))
        );

        while (!currentCandidates.isEmpty()) {
            HNSWNode candidate = currentCandidates.poll();
            if (topResults.size() >= k && 
                euclideanDistance(queryVector, candidate.vector) >= 
                euclideanDistance(queryVector, topResults.peek().vector)) {
                continue;
            }

            topResults.offer(candidate);
            if (topResults.size() > k) {
                topResults.poll();
            }
        }

        List<SearchResult> results = new ArrayList<>();
        while (!topResults.isEmpty()) {
            HNSWNode node = topResults.poll();
            double distance = euclideanDistance(queryVector, node.vector);
            results.add(new SearchResult(node.id, distance));
        }

        Collections.reverse(results);
        return results;
    }

    private List<HNSWNode> searchLayer(double[] queryVector, int entryPointId, int ef, int level) {
        Map<Integer, HNSWNode> nodes = new HashMap<>();
        for (List<HNSWNode> layer : graph.values()) {
            for (HNSWNode node : layer) {
                nodes.put(node.id, node);
            }
        }

        HNSWNode ep = nodes.get(entryPointId);
        if (ep == null) return List.of();

        Set<Integer> visited = new HashSet<>();
        PriorityQueue<HNSWNode> topCandidates = new PriorityQueue<>(
            Comparator.comparingDouble(n -> -euclideanDistance(queryVector, n.vector))
        );
        topCandidates.offer(ep);
        visited.add(entryPointId);

        PriorityQueue<HNSWNode> dynamicCandidates = new PriorityQueue<>(
            Comparator.comparingDouble(n -> euclideanDistance(queryVector, n.vector))
        );

        while (!topCandidates.isEmpty()) {
            HNSWNode candidate = topCandidates.poll();

            HNSWNode topOfQueue = topResults(topCandidates);
            double topDist = topOfQueue != null ? euclideanDistance(queryVector, topOfQueue.vector) : Double.MAX_VALUE;
            double candidateDist = euclideanDistance(queryVector, candidate.vector);

            if (candidateDist > topDist && topCandidates.size() >= ef) {
                break;
            }

            List<Integer> connections = candidate.connections.getOrDefault(level, List.of());
            for (Integer neighborId : connections) {
                if (visited.contains(neighborId)) continue;
                visited.add(neighborId);

                HNSWNode neighbor = nodes.get(neighborId);
                if (neighbor == null) continue;

                double neighborDist = euclideanDistance(queryVector, neighbor.vector);

                if (topCandidates.size() < ef || neighborDist < topDist) {
                    dynamicCandidates.offer(neighbor);
                    topCandidates.offer(neighbor);
                    if (topCandidates.size() > ef) {
                        topCandidates.poll();
                    }
                }
            }
        }

        return new ArrayList<>(topCandidates);
    }

    private HNSWNode topResults(PriorityQueue<HNSWNode> queue) {
        return queue.peek();
    }

    private List<HNSWNode> selectNeighbors(List<HNSWNode> candidates, int m, int level) {
        if (candidates.size() <= m) {
            return candidates;
        }

        candidates.sort(Comparator.comparingDouble(n -> n.id));
        return candidates.subList(0, m);
    }

    private int selectLevel() {
        int level = 0;
        while (random.nextDouble() < 0.5 && level < maxLayers) {
            level++;
        }
        return level;
    }

    private double euclideanDistance(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += (a[i] - b[i]) * (a[i] - b[i]);
        }
        return Math.sqrt(sum);
    }

    public int size() {
        return graph.getOrDefault(0, List.of()).size();
    }

    public static class HNSWNode {
        int id;
        double[] vector;
        int level;
        Map<Integer, List<Integer>> connections;

        HNSWNode(int id, double[] vector, int level) {
            this.id = id;
            this.vector = vector;
            this.level = level;
            this.connections = new HashMap<>();
        }
    }

    public record SearchResult(int id, double distance) {}
}