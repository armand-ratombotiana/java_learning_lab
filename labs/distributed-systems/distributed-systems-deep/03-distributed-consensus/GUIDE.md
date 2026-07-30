# Distributed Consensus (BFT) — Deep Dive Guide

## Byzantine Faults

A byzantine node can arbitrarily deviate from the protocol — send conflicting messages, lie, collude. Requires **N = 3f + 1** nodes to tolerate f byzantine faults.

## PBFT (Practical BFT)

Three phases for a view change + three phases for normal case:

**Normal case (3 phases)**:
1. **Pre-prepare**: primary proposes sequence number + request
2. **Prepare**: replicas broadcast prepare messages (2f + 1 needed)
3. **Commit**: replicas broadcast commit messages (2f + 1 needed)

**View change**: triggered when timer expires waiting for pre-prepare

**Optimistic**: 3 phases + 2f+1 quorums → O(n²) messages

## Quorum Systems

| Type | Size | Tolerates |
|------|------|-----------|
| Crash (majority) | f + 1 | f crashes |
| Byzantine | 2f + 1 | f byzantine |

Proof: any two quorums intersect in at least one correct node.

## FLP Impossibility

In an asynchronous system where processes can crash, **no deterministic consensus algorithm can guarantee termination** (even with one crash).

Systems circumvent FLP with:
- Failure detectors (timeouts → partial synchrony)
- Randomized algorithms
- Leader election