# Consistency Models - REFLECTION

## Key Takeaways

1. **No one-size-fits-all**: Different data requires different consistency models. Design per data type.

2. **Strong consistency is expensive**: It limits availability, throughput, and geographic distribution. Use it only when necessary.

3. **Eventual consistency requires application-level thinking**: Conflict resolution, stale data handling, and idempotency become application concerns.

4. **PACELC over CAP**: In practice, systems spend most of their time without partitions, so the E(L)C trade-off matters most.

## Self-Assessment Questions

- Can I explain the difference between linearizable, sequential, causal, and eventual consistency?
- Do I understand when to use quorum-based vs leader-based replication?
- Can I implement a vector clock?
- Do I know the trade-offs between 2PC, Raft, and CRDTs?

## Common Misconceptions

- "CAP theorem means pick two" — It means pick two during a partition. Most of the time, all three are available.
- "Eventual consistency is always eventually consistent" — Convergence requires application-level conflict resolution.
- "Raft guarantees linearizability" — Raft provides sequential consistency by default, not linearizability.

## Next Steps

- Read "Designing Data-Intensive Applications" by Martin Kleppmann
- Set up a Raft cluster with Atomix or etcd
- Experiment with CRDT libraries
- Use Jepsen to test consistency guarantees of a real database
