# 03 - Distributed Consensus (BFT)

## Topics Covered
- Byzantine Fault Tolerance (BFT)
- PBFT (Practical Byzantine Fault Tolerance)
- Quorum systems (byzantine quorums, N=3f+1)
- FLP impossibility (consensus in async system)
- Byzantine Generals Problem
- BFT vs CFT (Crash Fault Tolerance)

## Goal
Understand the guarantees and costs of tolerating arbitrary (byzantine) failures.

## Exercises

1. Simulate the Byzantine Generals Problem with a traitor general.
2. Implement PBFT pre-prepare/prepare/commit phases for a 4-node cluster.
3. Demonstrate FLP impossibility with a simulated asynchronous run that cannot terminate.
4. Compare quorum sizes for crash vs byzantine fault tolerance.