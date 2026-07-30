# Interview Questions — Network Observability

## Beginner

Q: What are VPC Flow Logs and what information do they capture?
A: Network traffic metadata: source/dest IP, ports, protocol, packets, bytes, action (accept/reject).

Q: What is eBPF and how is it used for networking?
A: eBPF runs sandboxed programs in kernel for packet filtering, tracing, observability without kernel module.

## Intermediate

Q: How does packet capture work and what key challenges exist?
A: Libpcap captures raw packets from network interface. Challenges: high packet rate, storage, privacy, TLS-encrypted payloads.

Q: What metrics would you collect to monitor network health?
A: Throughput (bps), packet loss (%), latency (RTT), jitter (ms), retransmission rate, connection establishment rate.

## Advanced

Q: Design a network observability platform for a large-scale microservice deployment.
A: eBPF-based per-pod metrics, flow logs with service identity, distributed tracing with network spans, latency heatmaps, anomaly detection.

Q: How would you implement hop-by-hop network tracing across cloud and on-prem?
A: VPC Reachability Analyzer, traceroute with ICMP probes, MTR for continuous monitoring, service mesh telemetry.
