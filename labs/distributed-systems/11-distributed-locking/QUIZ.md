# Distributed Locking: Quiz

## Questions
1. What is the purpose of a fencing token?
2. How does ZooKeeper implement locks?
3. What is the majority requirement in Redlock?
4. What happens if a lock lease expires while the holder is still working?
5. What is the difference between an ephemeral and persistent ZooKeeper node?
6. Why is clock drift important for Redlock?
7. What is a lease in distributed locking?
8. How do you prevent releasing someone else's lock in Redis?
9. What is the difference between etcd and ZooKeeper for locking?
10. How do you handle GC pauses in lock holders?

## Answers
1. Monotonically increasing number to detect stale lock holders
2. Sequential ephemeral nodes; lowest sequence number holds lock
3. Acquire lock on majority (N/2+1) of Redis nodes
4. Lock becomes available, causing potential concurrent access
5. Ephemeral: deleted when session ends. Persistent: remains.
6. Redlock assumes synchronized clocks; drift can violate safety
7. Time-bounded lock that auto-releases after expiry
8. Use Lua script: check key value matches owner before deleting
9. etcd uses gRPC/RAFT; ZooKeeper uses custom protocol/Zab
10. Increase lease duration, implement fencing tokens
