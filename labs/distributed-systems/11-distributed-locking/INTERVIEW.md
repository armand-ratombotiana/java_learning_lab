# Distributed Locking: Interview Questions

## Q1: How would you implement a distributed lock?
**A**: Use ZooKeeper sequential ephemeral nodes for strong guarantees. Create /lock/node-0001, check if you're lowest. Watch predecessor. On release or session expiry, next in line gets lock.

## Q2: What are the problems with Redlock?
**A**: Clock drift can cause safety violations. Without fencing tokens, delayed lock holders can corrupt data. Martin Kleppmann argues it's unsafe without fencing.

## Q3: How do you handle a lock holder that pauses (GC)?
**A**: Use fencing tokens - the resource validates each operation has an increasing token. Also set appropriate lease duration (accounting for max expected pause).

## Q4: What's the difference between ZooKeeper and etcd for locking?
**A**: ZooKeeper: sequential nodes, watches. etcd: TTL leases, revision-based. Both provide linearizable operations. etcd is generally faster and simpler.

## Q5: When should you NOT use a distributed lock?
**A**: When you can use optimistic concurrency control (CAS), when CRDTs can resolve conflicts, or when the operation can be made idempotent.
