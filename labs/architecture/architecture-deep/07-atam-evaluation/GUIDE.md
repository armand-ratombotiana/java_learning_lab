# ATAM — Step-by-Step Guide

## 1. Present the Architecture
- Describe the system (e-commerce platform) and its architectural drivers.

## 2. Business Drivers
- Scale to 10M users, 99.99% uptime, sub-200ms latency.

## 3. Utility Tree
- Root: Utility -> Performance, Availability, Security, Modifiability.
- Each node has quality attribute scenarios with stimulus/response.

## 4. Architectural Approaches
- Microservices, event-driven, CQRS, sharded database.

## 5. Tradeoff Analysis
- Event-driven improves scalability but adds complexity.
- CQRS improves read performance but introduces eventual consistency.

## 6. Risk Themes
- Identified: eventual consistency risk, operational complexity.

## Build & Run
```bash
javac --enable-preview -source 21 -d out src/com/architecture/deep/lab07/*.java
java --enable-preview -cp out com.architecture.deep.lab07.AtamLab
```
