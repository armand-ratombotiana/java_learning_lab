# Code Deep Dive: Event Streaming

## Log Storage

`LogManager` manages a `ConcurrentHashMap<String, TopicPartition>` where each `TopicPartition` owns a `PartitionLog`. The `PartitionLog` maintains a list of `LogSegment` objects. When writing a record batch, the active segment's `FileChannel` is appended. When the active segment exceeds `segment.bytes`, it's rolled: current active is marked read-only, a new segment is created.

## Replication

`PartitionReplica` implements the leader/follower logic. The leader maintains a `Set<ReplicaId>` for the ISR. Followers run a `FetcherThread` that polls the leader with `FetchRequest(partition, offset, maxBytes)`. The leader reads from its log starting at the requested offset and returns records. If a follower's fetch offset is behind by > `replica.lag.time.max.ms`, it's removed from ISR.

## Consumer Groups

`ConsumerCoordinator` uses Kafka's group protocol. Consumers in the same group send `JoinGroupRequest`. The elected group leader receives member metadata (subscriptions), computes partition assignment (range or round-robin), and sends `SyncGroupRequest` with assignments. Consumers poll for records at their assigned partitions and commit offsets to `__consumer_offsets` topic.

## Controller

The `Controller` is the first broker that starts in the cluster (or elected via ZooKeeper/KRaft). It handles topic creation/deletion, partition reassignment, leader election. On broker failure, the controller detects via session timeout and triggers leader election for affected partitions.
