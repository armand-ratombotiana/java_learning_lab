# Lab 07 — Load Balancing

## Overview
Deep dive into load balancing: Layer 4 vs Layer 7, algorithms (round-robin, least connections, consistent hashing), health checks, session persistence, and modern LB architectures.

## Prerequisites
- Java 21+ development environment
- Basic networking concepts
- Understanding of HTTP/TCP

## What You Will Learn
- Implement Layer 4 (TCP/UDP) and Layer 7 (HTTP) load balancers
- Build multiple load balancing algorithms
- Implement health checks with passive and active probing
- Design session persistence strategies (sticky sessions)
- Model consistent hashing for cache affinity

## Topics Covered
| Topic | Description |
|-------|-------------|
| Layer 4 vs Layer 7 | TCP load balancing vs HTTP reverse proxy |
| Round-Robin | Sequential distribution, weighted variants |
| Least Connections | Dynamic distribution based on active connections |
| Consistent Hashing | Ring-based distribution, minimal disruption on changes |
| Health Checks | Active probes, passive failure detection, circuit breakers |
| Session Persistence | Sticky sessions, cookie insertion, IP hash |

## Java Package
`com.networking.deep.lab07`
