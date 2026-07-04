# CAP Theorem: Quiz

## Questions

1. What does CAP stand for?
2. Who proposed the CAP theorem?
3. Can a distributed system provide all three CAP guarantees?
4. What happens during a network partition?
5. Which two options are available during a partition?
6. Is MongoDB CP or AP?
7. Is Cassandra CP or AP?
8. What does PACELC add to CAP?
9. What is a quorum?
10. Why are CA systems impossible in distributed environments?

## Answers
1. Consistency, Availability, Partition Tolerance
2. Eric Brewer
3. No
4. System must choose between consistency and availability
5. CP or AP
6. CP
7. AP
8. Adds latency vs consistency tradeoff when no partition
9. Minimum number of nodes that must agree
10. Partitions are unavoidable in distributed systems
