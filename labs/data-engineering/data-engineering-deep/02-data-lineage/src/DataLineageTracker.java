package com.dataengineering.deep.lab02;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DataLineageTracker {

    public record DataNode(String id, NodeType type, String namespace) {
        public enum NodeType { DATASET, TRANSFORM, JOB }
    }

    public record ColumnLineage(String column, String sourceDataset, String sourceColumn, String transform) {}

    public static class LineageGraph {
        private final Map<String, DataNode> nodes = new ConcurrentHashMap<>();
        private final Map<String, Set<String>> upstream = new ConcurrentHashMap<>();
        private final Map<String, Set<String>> downstream = new ConcurrentHashMap<>();
        private final Map<String, List<ColumnLineage>> columnLineage = new ConcurrentHashMap<>();

        public void addNode(DataNode node) { nodes.put(node.id(), node); }

        public void addEdge(String from, String to) {
            upstream.computeIfAbsent(to, k -> ConcurrentHashMap.newKeySet()).add(from);
            downstream.computeIfAbsent(from, k -> ConcurrentHashMap.newKeySet()).add(to);
        }

        public void addColumnLineage(String datasetColumn, ColumnLineage cl) {
            columnLineage.computeIfAbsent(datasetColumn, k -> new ArrayList<>()).add(cl);
        }

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

        public List<ColumnLineage> getColumnLineage(String datasetColumn) {
            return columnLineage.getOrDefault(datasetColumn, List.of());
        }

        public List<String> findImpactedDatasets(String datasetId) {
            return getDownstream(datasetId).stream().filter(id -> {
                DataNode n = nodes.get(id);
                return n != null && n.type() == DataNode.NodeType.DATASET;
            }).toList();
        }

        public List<String> findRootDatasets(String datasetId) {
            return getUpstream(datasetId).stream().filter(id -> {
                DataNode n = nodes.get(id);
                return n != null && n.type() == DataNode.NodeType.DATASET;
            }).toList();
        }

        public Map<String, Long> consumerCountByDataset() {
            return downstream.entrySet().stream()
                .filter(e -> {
                    DataNode n = nodes.get(e.getKey());
                    return n != null && n.type() == DataNode.NodeType.DATASET;
                })
                .collect(Collectors.toMap(Map.Entry::getKey, e -> (long) e.getValue().size()));
        }
    }

    public static void main(String[] args) {
        var graph = new LineageGraph();
        graph.addNode(new DataNode("raw_events", DataNode.NodeType.DATASET, "s3://data/raw"));
        graph.addNode(new DataNode("clean_events", DataNode.NodeType.DATASET, "s3://data/clean"));
        graph.addNode(new DataNode("agg_hourly", DataNode.NodeType.DATASET, "s3://data/agg"));
        graph.addNode(new DataNode("etl_job_1", DataNode.NodeType.JOB, "airflow"));

        graph.addEdge("raw_events", "etl_job_1");
        graph.addEdge("etl_job_1", "clean_events");
        graph.addEdge("clean_events", "agg_hourly");

        graph.addColumnLineage("clean_events.user_id",
            new ColumnLineage("user_id", "raw_events", "user_id", "identity"));
        graph.addColumnLineage("agg_hourly.event_count",
            new ColumnLineage("event_count", "clean_events", "event_id", "COUNT"));

        System.out.println("Downstream of raw_events: " + graph.findImpactedDatasets("raw_events"));
        System.out.println("Upstream of agg_hourly: " + graph.findRootDatasets("agg_hourly"));
        System.out.println("Column lineage for clean_events.user_id: " + graph.getColumnLineage("clean_events.user_id"));
        System.out.println("Consumer counts: " + graph.consumerCountByDataset());
    }
}
