# Distributed Consensus (BFT) — Interview Questions

## Beginner
1. What is a Byzantine fault?
2. How many nodes are needed to tolerate 1 byzantine fault? Why?
3. What is the Byzantine Generals Problem?

## Intermediate
4. Explain the three phases of PBFT (pre-prepare, prepare, commit).
5. What is the FLP impossibility result and why is it significant?
6. How does a quorum intersection property differ for crash vs byzantine faults?

## Advanced
7. Describe a view change in PBFT — when does it happen and what messages are exchanged?
8. How does HotStuff (used in DiemBFT/Sui) improve on PBFT's O(n²) communication?
9. Why does PBFT require 3f+1 nodes while Raft only needs 2f+1 for crash faults?

## System Design
10. Design a BFT consensus layer for a permissioned blockchain handling financial transactions.