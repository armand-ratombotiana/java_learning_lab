package com.cloud.networking;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class LoadBalancer {

    public static class TargetGroup {
        private final String name;
        private final List<Target> targets = new CopyOnWriteArrayList<>();
        private final AtomicInteger counter = new AtomicInteger(0);

        public static class Target {
            public final String id;
            public final String ip;
            public final int port;
            public volatile boolean healthy;

            public Target(String id, String ip, int port) {
                this.id = id;
                this.ip = ip;
                this.port = port;
                this.healthy = true;
            }

            public String getEndpoint() { return ip + ":" + port; }
        }

        public TargetGroup(String name) { this.name = name; }

        public void registerTarget(Target target) {
            targets.add(target);
            System.out.println("Registered: " + target.getEndpoint() + " to " + name);
        }

        public void deregisterTarget(Target target) {
            targets.remove(target);
        }

        public Target selectTarget() {
            List<Target> healthy = targets.stream().filter(t -> t.healthy).toList();
            if (healthy.isEmpty()) return null;
            return healthy.get(counter.getAndIncrement() % healthy.size());
        }

        public void healthCheck() {
            for (Target t : targets) {
                t.healthy = Math.random() > 0.1;
            }
        }
    }

    public static class ApplicationLoadBalancer {
        private final String name;
        private final String dnsName;
        private final Map<String, TargetGroup> listenerRules = new ConcurrentHashMap<>();

        public ApplicationLoadBalancer(String name) {
            this.name = name;
            this.dnsName = name + "-" + System.currentTimeMillis() + ".elb.amazonaws.com";
            System.out.println("Created ALB: " + dnsName);
        }

        public void addListenerRule(String pathPattern, TargetGroup group) {
            listenerRules.put(pathPattern, group);
        }

        public String routeRequest(String path) {
            for (Map.Entry<String, TargetGroup> entry : listenerRules.entrySet()) {
                if (path.startsWith(entry.getKey())) {
                    TargetGroup.Target target = entry.getValue().selectTarget();
                    if (target != null) {
                        return target.getEndpoint();
                    }
                }
            }
            return null;
        }
    }

    public static void main(String[] args) {
        TargetGroup webTargets = new TargetGroup("web-targets");
        webTargets.registerTarget(new TargetGroup.Target("i-001", "10.0.1.10", 80));
        webTargets.registerTarget(new TargetGroup.Target("i-002", "10.0.1.11", 80));

        TargetGroup apiTargets = new TargetGroup("api-targets");
        apiTargets.registerTarget(new TargetGroup.Target("i-003", "10.0.2.10", 8080));

        ApplicationLoadBalancer alb = new ApplicationLoadBalancer("my-alb");
        alb.addListenerRule("/api", apiTargets);
        alb.addListenerRule("/", webTargets);

        System.out.println("\n=== Load Balancer ===");
        System.out.println("/api/users -> " + alb.routeRequest("/api/users"));
        System.out.println("/home -> " + alb.routeRequest("/home"));
        System.out.println("/api/orders -> " + alb.routeRequest("/api/orders"));
    }
}
