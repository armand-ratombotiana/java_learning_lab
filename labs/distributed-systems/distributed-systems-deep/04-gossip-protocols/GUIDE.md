# Gossip Protocols — Deep Dive Guide

## Gossip Dissemination

**Push**: when a node learns new info, it randomly selects k peers and pushes it.
**Pull**: nodes periodically pull updates from random peers.
**Push-Pull**: both directions.

Convergence: O(log N) rounds with fanout k.

## SWIM Protocol

**Components**:
1. **Dissemination**: piggyback membership changes on failure detection messages
2. **Failure Detection**: each node pings a random target; if no response, indirect ping via another node; if both fail, mark as suspected

**Suspicion mechanism**: target marked as "suspected" before "failed" to handle false positives.

## Epidemic Protocols

- **SI model**: susceptible → infected (once learned, never forgets)
- **SIR model**: susceptible → infected → recovered (removed from circulation)

## Anti-Entropy

- **Full sync**: exchange entire state (expensive)
- **Merkle tree sync**: compare hash trees; only exchange differing subtrees

## Phi-Accrual Failure Detection (Cassandra)

- Track arrival times of heartbeats
- Compute "phi" = -log10(P(later than expected))
- If phi > threshold (e.g., 8), mark as down
- Adaptive — accounts for network variability