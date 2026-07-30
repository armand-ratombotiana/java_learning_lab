package com.cloud.deep.lab04;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class KubernetesDeepDive {

    public enum PodPhase { PENDING, RUNNING, SUCCEEDED, FAILED, CRASH_LOOP_BACK_OFF, UNKNOWN }

    public record Pod(String name, String namespace, PodPhase phase, String node, Map<String,String> labels, Map<String,String> annotations, int restartCount) {}
    public record Node(String name, double cpuCapacity, double memoryGb, double cpuRequested, double memoryRequested, Map<String,String> labels) {}
    public record CustomResource(String apiVersion, String kind, String name, String namespace, Map<String,Object> spec) {}

    public record NetworkPolicyRule(String direction, String podSelector, String namespaceSelector, List<String> ports, List<String> protocols, String action) {}

    public static class CustomScheduler {
        private final List<Node> nodes;

        public CustomScheduler(List<Node> nodes) { this.nodes = nodes; }

        public record ScoreResult(Node node, double score) {}

        public Optional<ScoreResult> schedule(Pod pod) {
            var feasible = nodes.stream()
                .filter(n -> n.cpuCapacity() >= n.cpuRequested() + 0.5)
                .filter(n -> n.memoryGb() >= n.memoryRequested() + 1.0)
                .toList();
            return feasible.stream()
                .map(n -> new ScoreResult(n, score(n)))
                .max(Comparator.comparingDouble(ScoreResult::score));
        }

        private double score(Node node) {
            double cpuRemaining = node.cpuCapacity() - node.cpuRequested();
            double memRemaining = node.memoryGb() - node.memoryRequested();
            double cpuScore = cpuRemaining / node.cpuCapacity() * 50;
            double memScore = memRemaining / node.memoryGb() * 50;
            return cpuScore + memScore;
        }
    }

    public static class ReplicaSetController {
        private final Map<String, List<Pod>> replicaSets = new ConcurrentHashMap<>();
        private final Random rand = new Random();

        public void reconcile(String rsName, int desired) {
            var current = replicaSets.getOrDefault(rsName, new CopyOnWriteArrayList<>());
            var running = current.stream().filter(p -> p.phase() == PodPhase.RUNNING).count();
            long diff = desired - running;
            if (diff > 0) {
                for (int i = 0; i < diff; i++) {
                    var pod = new Pod(rsName + "-" + UUID.randomUUID().toString().substring(0, 4),
                        "default", PodPhase.RUNNING, "node-" + rand.nextInt(3), Map.of("app", rsName), Map.of(), 0);
                    current.add(pod);
                }
            } else if (diff < 0) {
                var toRemove = current.stream()
                    .filter(p -> p.phase() == PodPhase.RUNNING)
                    .limit(-diff)
                    .toList();
                current.removeAll(toRemove);
            }
            replicaSets.put(rsName, current);
        }

        public List<Pod> getPods(String rsName) { return List.copyOf(replicaSets.getOrDefault(rsName, List.of())); }
    }

    public static class NetworkPolicyEngine {
        private final List<NetworkPolicyRule> rules = new CopyOnWriteArrayList<>();

        public void addRule(NetworkPolicyRule rule) { rules.add(rule); }

        public boolean evaluateIngress(Pod source, Pod target, String port, String protocol) {
            return rules.stream()
                .filter(r -> r.direction().equals("ingress"))
                .filter(r -> r.ports().contains(port) || r.ports().contains("*"))
                .filter(r -> r.protocols().contains(protocol) || r.protocols().contains("*"))
                .anyMatch(r -> {
                    if (r.podSelector().equals("*")) return true;
                    return target.labels().entrySet().stream()
                        .anyMatch(e -> r.podSelector().contains(e.getKey() + "=" + e.getValue()));
                });
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Custom Scheduler ===");
        var nodes = List.of(
            new Node("node-1", 4, 16, 1.5, 3.0, Map.of("zone", "us-east-1a")),
            new Node("node-2", 8, 32, 4.0, 12.0, Map.of("zone", "us-east-1b")),
            new Node("node-3", 4, 16, 0.5, 2.0, Map.of("zone", "us-east-1c"))
        );
        var scheduler = new CustomScheduler(nodes);
        var pod = new Pod("web-1", "default", PodPhase.PENDING, "", Map.of("app", "web"), Map.of(), 0);
        scheduler.schedule(pod).ifPresent(result ->
            System.out.printf("Pod %s scheduled to %s (score: %.2f)%n", pod.name(), result.node().name(), result.score()));

        System.out.println("\n=== ReplicaSet Controller ===");
        var rs = new ReplicaSetController();
        rs.reconcile("web-frontend", 3);
        System.out.println("Pods after scale to 3: " + rs.getPods("web-frontend").size());
        rs.reconcile("web-frontend", 5);
        System.out.println("Pods after scale to 5: " + rs.getPods("web-frontend").size());

        System.out.println("\n=== Network Policy ===");
        var netPol = new NetworkPolicyEngine();
        netPol.addRule(new NetworkPolicyRule("ingress", "app=web", "*", List.of("80", "443"), List.of("TCP"), "allow"));
        var source = new Pod("monitor", "default", PodPhase.RUNNING, "node-1", Map.of("app", "monitor"), Map.of(), 0);
        var target = new Pod("web-1", "default", PodPhase.RUNNING, "node-2", Map.of("app", "web"), Map.of(), 0);
        System.out.println("Ingress to web: " + netPol.evaluateIngress(source, target, "80", "TCP"));
        System.out.println("Ingress to db: " + netPol.evaluateIngress(source, target, "5432", "TCP"));
    }
}
