# Failure Detection: Internals

## SWIM Protocol

### Components
- **Ping**: Direct heartbeat to random member
- **Ping-Request**: Ask other members to ping a suspected node
- **Indirect probing**: If direct ping fails, use K other members

### Membership Updates
- Piggyback on ping/pong messages
- Propagate join/leave/failure events
- Infection-style: eventually consistent

### Suspicion Mechanism
- Before declaring failure, spread suspicion
- Allows slow nodes to recover
- Reduces false positives

## Gossip Protocol (Cassandra)

### Round-Based
Each second, every node:
1. Selects 1-3 random nodes
2. Exchanges membership state
3. Applies new information

### State Exchange
- Each node tracks state per member: ALIVE, SUSPECT, DEAD
- Gossip includes heartbeat counter
- Higher heartbeat = more recent state

### Convergence
O(log N) rounds for new information to reach all N nodes.
In a 100-node cluster: ~7 rounds (log₂ 100) to converge.

## Failure Detection in Raft
- Leader sends AppendEntries as heartbeat
- Followers expect heartbeat within election timeout
- If no heartbeat → start election
- Election timeout randomized (150-300ms typical)
