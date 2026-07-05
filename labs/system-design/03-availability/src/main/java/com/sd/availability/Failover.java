package com.sd.availability;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class Failover {

    public static class FailoverNode {
        private final String id;
        private volatile boolean primary;
        private volatile boolean healthy;
        private final AtomicLong requestCount = new AtomicLong(0);

        public FailoverNode(String id, boolean primary) {
            this.id = id;
            this.primary = primary;
            this.healthy = true;
        }

        public String handleRequest(String request) {
            if (!healthy) throw new RuntimeException(id + " is DOWN");
            requestCount.incrementAndGet();
            return id + " processed: " + request;
        }

        public void setHealthy(boolean h) { this.healthy = h; }
        public boolean isHealthy() { return healthy; }
        public boolean isPrimary() { return primary; }
        public String getId() { return id; }
        public long getRequestCount() { return requestCount.get(); }
    }

    public static class FailoverManager {
        private final List<FailoverNode> nodes;
        private FailoverNode active;

        public FailoverManager(List<FailoverNode> nodes) {
            this.nodes = nodes;
            this.active = nodes.get(0);
        }

        public String handleRequest(String request) {
            if (active.isHealthy()) {
                return active.handleRequest(request);
            }

            System.out.println("Failover from " + active.getId());
            for (FailoverNode node : nodes) {
                if (node.isHealthy() && !node.equals(active)) {
                    active = node;
                    System.out.println("  Failed over to " + active.getId());
                    return active.handleRequest(request);
                }
            }
            return "ERROR: All nodes unavailable";
        }

        public FailoverNode getActive() { return active; }
    }

    public static void main(String[] args) {
        List<FailoverNode> nodes = Arrays.asList(
            new FailoverNode("primary", true),
            new FailoverNode("standby-1", false),
            new FailoverNode("standby-2", false)
        );

        FailoverManager manager = new FailoverManager(nodes);

        System.out.println("=== Failover Pattern ===");
        System.out.println(manager.handleRequest("req-1"));

        nodes.get(0).setHealthy(false);
        System.out.println("\nPrimary failed!");

        System.out.println(manager.handleRequest("req-2"));
        System.out.println(manager.handleRequest("req-3"));

        System.out.println("\nActive node: " + manager.getActive().getId());
        System.out.println("Request counts:");
        nodes.forEach(n -> System.out.println("  " + n.getId() + ": " + n.getRequestCount()));
    }
}
