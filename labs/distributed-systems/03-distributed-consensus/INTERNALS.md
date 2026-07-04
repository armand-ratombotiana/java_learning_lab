# Distributed Consensus: Internals

## Paxos Deep Dive

### Roles
- **Proposer**: Proposes values
- **Acceptor**: Accepts or rejects proposals
- **Learner**: Learns decided values

### Phase 1: Prepare
1. Proposer sends Prepare(n) to acceptors
2. Each acceptor responds with promise(n) if n > highest seen
3. Acceptor includes any accepted value it has

### Phase 2: Accept
1. Proposer sends Accept(n, v) where v is from highest accepted or its own
2. Acceptors accept if no higher prepare seen
3. Value decided when majority accepts

## Multi-Paxos
- Optimize by electing a distinguished proposer (leader)
- Leader bypasses Phase 1 for subsequent proposals
- Each proposal becomes a log entry

## Zab (ZooKeeper)
- Primary-backup protocol
- Leader proposes, followers acknowledge
- Commit message from leader finalizes
- Supports crash recovery with epoch numbering
