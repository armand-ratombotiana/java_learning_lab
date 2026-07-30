# Lab 08 — Network Observability

## Overview
Deep dive into network observability: flow logs, packet capture, eBPF-based monitoring, network metrics collection, and distributed tracing at the network level.

## Prerequisites
- Java 21+ development environment
- Basic networking concepts
- Understanding of observability principles

## What You Will Learn
- Implement network flow log collection and analysis
- Build packet capture and protocol parsing
- Model eBPF-based network monitoring
- Collect and aggregate network metrics (throughput, latency, packet loss)
- Implement network-level distributed tracing

## Topics Covered
| Topic | Description |
|-------|-------------|
| Flow Logs | VPC Flow Logs, NetFlow, IPFIX, aggregatio |
| Packet Capture | pcap format, protocol parsing, TCP reassembly |
| eBPF | XDP, TC hooks, kernel-level observability |
| Network Metrics | Throughput, latency, jitter, packet loss, retransmission |
| Network Tracing | Hop-by-hop tracing, MTR, Traceroute |

## Java Package
`com.networking.deep.lab08`
