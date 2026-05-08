package com.learning.edgecomputing;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

public class Lab {

    static class EdgeNode {
        private final String id;
        private final String location;
        private final double cpuCapacity;
        private final double memoryCapacity;
        private double cpuUsage;
        private double memoryUsage;
        private final AtomicInteger requestCount = new AtomicInteger(0);

        EdgeNode(String id, String location, double cpu, double mem) {
            this.id = id; this.location = location;
            this.cpuCapacity = cpu; this.memoryCapacity = mem;
        }

        boolean canHandle(double cpuReq, double memReq) {
            return (cpuUsage + cpuReq) <= cpuCapacity && (memoryUsage + memReq) <= memoryCapacity;
        }

        String handleRequest(String task, double cpuReq, double memReq) {
            if (!canHandle(cpuReq, memReq)) return "REJECTED";
            cpuUsage += cpuReq;
            memoryUsage += memReq;
            requestCount.incrementAndGet();
            return "Processed " + task + " @" + id;
        }

        void release(double cpu, double mem) {
            cpuUsage = Math.max(0, cpuUsage - cpu);
            memoryUsage = Math.max(0, memoryUsage - mem);
        }

        double getUtilization() { return (cpuUsage / cpuCapacity) * 100; }
        int getRequestCount() { return requestCount.get(); }
        public String toString() { return id + " [" + location + "] CPU:" + (int)(getUtilization()) + "%"; }
    }

    static class EdgeOrchestrator {
        private final List<EdgeNode> nodes = new CopyOnWriteArrayList<>();

        void addNode(EdgeNode node) { nodes.add(node); }

        EdgeNode findBestNode(double cpuReq, double memReq) {
            return nodes.stream()
                .filter(n -> n.canHandle(cpuReq, memReq))
                .min(Comparator.comparingDouble(EdgeNode::getUtilization))
                .orElse(null);
        }

        void report() {
            System.out.println("  Edge nodes:");
            nodes.forEach(n -> System.out.printf("    %s %s, utilization: %.1f%%, requests: %d%n",
                n.id, n.location, n.getUtilization(), n.getRequestCount()));
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Edge Computing Lab ===\n");

        edgeArchitecture();
        workloadOffloading();
        federatedLearning();
        vsCloudComputing();
        useCases();
    }

    static void edgeArchitecture() {
        System.out.println("--- Edge Architecture ---");
        var orchestrator = new EdgeOrchestrator();
        orchestrator.addNode(new EdgeNode("edge-1", "NYC-Office", 4.0, 8192));
        orchestrator.addNode(new EdgeNode("edge-2", "SF-Datacenter", 8.0, 16384));
        orchestrator.addNode(new EdgeNode("edge-3", "LHR-Branch", 2.0, 4096));
        orchestrator.addNode(new EdgeNode("edge-4", "TKY-Pop", 4.0, 8192));

        orchestrator.report();

        System.out.println("""
  Edge tiers:
  Device edge: IoT devices, phones, sensors
  Local edge: gateways, base stations, routers
  Regional edge: micro datacenters, CDN nodes
  Cloud: centralized datacenters

  Latency hierarchy:
  Device: <1ms   | Local: 1-5ms
  Regional: 5-20ms | Cloud: 20-100ms
    """);
    }

    static void workloadOffloading() {
        System.out.println("\n--- Workload Offloading ---");
        var orchestrator = new EdgeOrchestrator();
        orchestrator.addNode(new EdgeNode("edge-a", "NYC", 4.0, 8192));
        orchestrator.addNode(new EdgeNode("edge-b", "SF", 4.0, 8192));

        var tasks = List.of(
            Map.of("name", "face-detection", "cpu", 1.0, "mem", 1024.0),
            Map.of("name", "nlp-inference", "cpu", 2.0, "mem", 2048.0),
            Map.of("name", "video-transcode", "cpu", 3.0, "mem", 4096.0)
        );

        System.out.println("  Task scheduling:");
        for (var task : tasks) {
            var node = orchestrator.findBestNode((double) task.get("cpu"), (double) task.get("mem"));
            if (node != null) {
                var result = node.handleRequest((String) task.get("name"),
                    (double) task.get("cpu"), (double) task.get("mem"));
                System.out.println("    " + result);
            } else {
                System.out.println("    " + task.get("name") + " -> OFFLOADED TO CLOUD");
            }
        }

        orchestrator.report();
        System.out.println("  Offloading strategies: latency-min, energy-min, cost-min");
    }

    static void federatedLearning() {
        System.out.println("\n--- Federated Learning ---");
        System.out.println("""
  Instead of sending data to cloud:
  1. Cloud sends model to edge devices
  2. Edge trains locally (on-device data)
  3. Edge sends only model updates (gradients)
  4. Cloud aggregates updates (Federated Averaging)
  5. Repeat

  Benefits:
  - Data privacy (data never leaves device)
  - Reduced bandwidth (gradients vs raw data)
  - Personalization (local model adapts to user)

  Challenges:
  - Non-IID data distribution across devices
  - Communication efficiency
  - Heterogeneous devices (different compute/capacity)
  - Secure aggregation (encrypted gradient averaging)

  Frameworks: TensorFlow Federated, PySyft, Flower
    """);
    }

    static void vsCloudComputing() {
        System.out.println("\n--- Edge vs Cloud Computing ---");
        System.out.println("""
  | Aspect          | Edge Computing              | Cloud Computing            |
  |-----------------|----------------------------|----------------------------|
  | Latency         | <5ms (real-time)           | 20-100ms                   |
  | Bandwidth       | Filtered/aggregated data   | Raw data to datacenter     |
  | Compute         | Limited (ARM, GPU light)   | Virtually unlimited        |
  | Storage         | GBs (local SSD)            | PBs (distributed)          |
  | Connectivity    | Intermittent (WiFi, 5G)    | Always-on (backbone)       |
  | Management      | Distributed, complex       | Centralized, simpler       |
  | Cost per unit   | Lower (no egress)          | Pay-as-you-go              |
  | Security        | Physical access risk       | Virtualization isolation   |

  Hybrid: edge for preprocessing, cloud for heavy analysis
    """);
    }

    static void useCases() {
        System.out.println("\n--- Edge Computing Use Cases ---");
        System.out.println("""
  IoT / Industrial:
  - Predictive maintenance (vibration analysis on sensor gateways)
  - Real-time quality control (camera + ML at factory line)
  - SCADA monitoring with local failover

  Autonomous Vehicles:
  - LIDAR/Camera processing onboard (no cloud dependency)
  - V2X communication between vehicles and RSUs

  CDN / Media:
  - Video transcoding at edge PoPs
  - Live streaming optimization (WebRTC on edge)

  Retail:
  - Smart shelves (weight sensors + edge AI)
  - Checkout-free stores (computer vision on edge)

  5G / Telecom:
  - UPF (User Plane Function) at edge
  - Network slicing for ultra-low latency
    """);
    }
}
