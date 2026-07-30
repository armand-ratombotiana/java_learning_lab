package com.networking.deep.lab08;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class NetworkObservabilityDeep {

    public record FlowLogEntry(String srcIp, String dstIp, int srcPort, int dstPort, String protocol, long packets, long bytes, Instant timestamp, String action) {}
    public record PacketData(int length, long timestamp, String srcMac, String dstMac, String protocol, String payloadPreview) {}
    public record NetworkMetric(String name, double value, Instant timestamp, Map<String,String> labels) {}
    public record TraceHop(int hopNumber, String ip, double rttMs, String asn) {}

    public static class FlowLogCollector {
        private final List<FlowLogEntry> entries = new CopyOnWriteArrayList<>();
        private final Random rand = new Random();

        public void generateSampleData(int count) {
            var srcs = List.of("10.0.0.1", "10.0.0.2", "10.0.1.1", "192.168.1.1");
            var dsts = List.of("93.184.216.34", "142.250.80.14", "8.8.8.8", "203.0.113.1");
            for (int i = 0; i < count; i++) {
                entries.add(new FlowLogEntry(
                    srcs.get(rand.nextInt(srcs.size())),
                    dsts.get(rand.nextInt(dsts.size())),
                    rand.nextInt(65535), 80 + rand.nextInt(400),
                    rand.nextBoolean() ? "TCP" : "UDP",
                    rand.nextInt(1000), rand.nextInt(1000000),
                    Instant.now().minusSeconds(rand.nextInt(3600)),
                    rand.nextDouble() > 0.1 ? "ACCEPT" : "REJECT"
                ));
            }
        }

        public Map<String, Long> bytesByDestination() {
            return entries.stream()
                .filter(e -> e.action().equals("ACCEPT"))
                .collect(Collectors.groupingBy(FlowLogEntry::dstIp, Collectors.summingLong(FlowLogEntry::bytes)));
        }

        public void analyze() {
            System.out.println("Total flow log entries: " + entries.size());
            System.out.println("Unique source IPs: " + entries.stream().map(FlowLogEntry::srcIp).distinct().count());
            long rejected = entries.stream().filter(e -> e.action().equals("REJECT")).count();
            System.out.printf("Rejected traffic: %.1f%%%n", (double) rejected / entries.size() * 100);
        }
    }

    public static class PacketCaptureSimulator {
        private final Random rand = new Random();

        public PacketData capturePacket() {
            var protocols = List.of("TCP", "UDP", "ICMP", "ARP");
            var prot = protocols.get(rand.nextInt(protocols.size()));
            return new PacketData(rand.nextInt(1500), System.currentTimeMillis(),
                "00:1a:2b:3c:4d:5e", "00:6f:7g:8h:9i:0j",
                prot, prot.equals("TCP") ? "HTTP GET /..." : prot);
        }

        public List<PacketData> captureBurst(int count) {
            var result = new ArrayList<PacketData>(count);
            for (int i = 0; i < count; i++) result.add(capturePacket());
            return result;
        }
    }

    public static class EbpfNetworkMonitor {
        private final Map<String, Long> packetCounts = new ConcurrentHashMap<>();
        private final Map<String, Long> byteCounts = new ConcurrentHashMap<>();

        public void attachIngress(String interfaceName) {
            System.out.println("eBPF XDP program attached to " + interfaceName);
        }

        public void recordPacket(String direction, int length) {
            packetCounts.merge(direction, 1L, Long::sum);
            byteCounts.merge(direction, (long) length, Long::sum);
        }

        public void printStats() {
            System.out.println("eBPF stats:");
            for (var dir : List.of("ingress", "egress")) {
                System.out.printf("  %s: %d packets, %d bytes%n",
                    dir, packetCounts.getOrDefault(dir, 0L), byteCounts.getOrDefault(dir, 0L));
            }
        }
    }

    public static class NetworkTracer {
        public List<TraceHop> traceroute(String destination) {
            var hops = new ArrayList<TraceHop>();
            var rand = new Random();
            for (int i = 1; i <= 8; i++) {
                double rtt = 0.5 + rand.nextDouble() * 50;
                hops.add(new TraceHop(i, "10.0." + i + ".1", rtt, "AS" + (1000 + rand.nextInt(5000))));
            }
            return hops;
        }

        public double measureLatency(String destination) {
            return 10 + new Random().nextDouble() * 30;
        }

        public double measureJitter(String destination, int samples) {
            double total = 0;
            var rand = new Random();
            for (int i = 0; i < samples; i++) {
                total += Math.abs(rand.nextDouble() * 5);
            }
            return total / samples;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Flow Logs ===");
        var collector = new FlowLogCollector();
        collector.generateSampleData(20);
        collector.analyze();
        System.out.println("Top destinations:");
        collector.bytesByDestination().forEach((dst, bytes) ->
            System.out.printf("  %s: %d bytes%n", dst, bytes));

        System.out.println("\n=== Packet Capture ===");
        var pcap = new PacketCaptureSimulator();
        var packets = pcap.captureBurst(5);
        packets.forEach(p -> System.out.printf("  %s pkt len=%d%n", p.protocol(), p.length()));

        System.out.println("\n=== eBPF Monitoring ===");
        var ebpf = new EbpfNetworkMonitor();
        ebpf.attachIngress("eth0");
        for (int i = 0; i < 100; i++) {
            ebpf.recordPacket(i % 2 == 0 ? "ingress" : "egress", 64 + (i * 10));
        }
        ebpf.printStats();

        System.out.println("\n=== Network Tracing ===");
        var tracer = new NetworkTracer();
        var hops = tracer.traceroute("93.184.216.34");
        System.out.println("Traceroute to example.com:");
        hops.forEach(h -> System.out.printf("  hop %d: %s (%.2fms) [%s]%n", h.hopNumber(), h.ip(), h.rttMs(), h.asn()));

        System.out.printf("Latency: %.2fms%n", tracer.measureLatency("8.8.8.8"));
        System.out.printf("Jitter: %.2fms%n", tracer.measureJitter("8.8.8.8", 10));
    }
}
