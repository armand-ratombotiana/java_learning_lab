# Mental Models for Consensus

## The Voting Model
- **Paxos**: Parliamentary voting with multiple rounds
- **Raft**: Strong leader with simpler voting
- Consensus = majority agreement

## The Log Replication Model
Think of a replicated log as a shared append-only file:
- Leader appends entries
- Followers replicate
- Once majority acknowledges, entry is committed

## The Leader Election Model
- Nodes start as followers
- Timeout → candidate → request votes
- Majority → leader
- Leader handles all client requests

## The FLP Barrier
Fischer, Lynch, Paterson proved consensus impossible in purely async systems. Practical algorithms use:
- Timeouts (partial synchrony)
- Randomization
- Unreliable failure detectors
