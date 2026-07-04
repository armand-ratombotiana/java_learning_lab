# Math Foundation for Kubernetes

## Raft Consensus (etcd)
- **Quorum**: Requires > N/2 nodes to agree for writes.
- **Term**: Monotonically increasing election term number.
- **Log replication**: Entries committed when majority acknowledge.

## Scheduling Algorithms
- **Predicates**: Boolean filters (e.g., PodFitsResources: sum(requested) < node.allocatable).
- **Priorities**: Weighted scoring (e.g., LeastRequestedPriority: (1 - sum(requested)/capacity) * 10).
- **Bin-packing**: Scores nodes where Pod leaves more free space for larger future pods.

## Horizontal Pod Autoscaler
```
desiredReplicas = currentReplicas × (currentMetricValue / desiredMetricValue)
```

## Resource Quotas
- Namespace-level hard limits on CPU, memory, storage, and object counts.
- Admission controller validates request against remaining quota.

## Network Bandwidth
- Token bucket rate limiting via traffic shaping in CNI plugins.
- CIDR calculations for Pod and Service CIDR ranges.
