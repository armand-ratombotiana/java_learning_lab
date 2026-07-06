# Real-World Project: Production JVM Tuning Guide

## Overview
Create a systematic JVM tuning guide for a production microservice. The service processes financial transactions with strict latency SLAs (P99 < 10ms).

## Service Profile
- **Traffic**: 10,000 requests/second peak
- **Request size**: 2-50 KB (JSON payloads)
- **Allocation rate**: ~1 GB/s during peak
- **Heap**: 8 GB container limit
- **CPUs**: 4 cores available
- **Latency SLA**: P99 < 10ms, no pause > 50ms

## Tuning Dimensions

### 1. Heap Sizing
- Young generation: test `-Xmn` from 1 GB to 4 GB
- Survivor ratio: `-XX:SurvivorRatio=6,8,12`
- Tenuring threshold: `-XX:MaxTenuringThreshold=6,15`

### 2. GC Selection
- G1: `-XX:MaxGCPauseMillis=5,10,25,50` — measure actual vs requested
- ZGC: `-XX:ZCollectionInterval` and `-XX:ZAllocationSpikeTolerance`
- Compare pause time distribution (not just average)

### 3. Compiler Tuning
- `-XX:ReservedCodeCacheSize=128m,256m`
- `-XX:InlineSize=35,100,200` — test impact on hot methods
- `-XX:+UseCounterDecay` on/off

### 4. Memory Management
- `-XX:StringDeduplicationAgeThreshold=3`
- `-XX:+AlwaysPreTouch` — measure startup vs throughput tradeoff
- `-XX:+UseLargePages` — if available on OS

## Deliverables
- `TransactionProcessor.java` — simulated service
- `LoadTestClient.java` — drives traffic at target rates
- Tuning report: recommendation for each dimension with evidence
- GC log analysis for the recommended configuration
