# Why Distributed Consensus Exists

## Need for Agreement
In distributed systems, nodes must agree on:
- Which node is the leader
- Whether a transaction committed
- Configuration changes
- State machine replication order

## Without Consensus
- Split-brain scenarios (two leaders)
- Inconsistent state across replicas
- Undefined behavior during failures

## Real-World Use
- **Chubby/ZooKeeper**: Coordination services
- **etcd**: Kubernetes configuration storage
- **Apache Kafka**: Controller election
- **Google Spanner**: Transaction commit
