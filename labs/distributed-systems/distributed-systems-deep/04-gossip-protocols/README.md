# 04 - Gossip Protocols

## Topics Covered
- Gossip dissemination (push/push-pull/pull gossip)
- Failure detection (SWIM protocol)
- Epidemic protocols
- Anti-entropy (full sync, Merkle tree sync)
- Suspicion mechanism, phi-accrual detection
- Membership protocols

## Goal
Understand how gossip enables decentralized information dissemination and failure detection.

## Exercises

1. Implement push-gossip and pull-gossip for message propagation.
2. Implement a SWIM-style failure detector with suspicion.
3. Simulate gossip convergence across a 50-node cluster.
4. Implement Merkle tree-based anti-entropy for data sync.