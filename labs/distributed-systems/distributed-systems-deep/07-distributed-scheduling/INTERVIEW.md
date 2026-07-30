# Distributed Scheduling — Interview Questions

## Beginner
1. What is consistent hashing and why is it used?
2. What is the problem with simple mod-N hashing?
3. What is rendezvous hashing (HRW)?

## Intermediate
4. How do virtual nodes improve consistent hashing distribution?
5. Compare consistent hashing to rendezvous hashing in terms of complexity and rebalancing.
6. What is the power-of-two-choices load balancing technique?

## Advanced
7. How would you handle weighted nodes in consistent hashing?
8. Describe the rebalancing cost when using consistent hashing vs deterministic hash rings.
9. How does Cassandra's vnode-based partitioning compare to deterministic hash slot assignment (Redis Cluster)?

## System Design
10. Design a distributed task scheduler for 10K worker nodes that balances CPU-intensive and I/O-intensive tasks.