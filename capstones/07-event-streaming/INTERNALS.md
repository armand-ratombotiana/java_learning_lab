# Internals: Event Streaming

## Core Components
- **BrokerServer**: Netty-based TCP server for producer/consumer requests
- **LogManager**: Manages topic partitions, log segments, and compaction
- **PartitionReplica**: Handles replication: leader accepts writes, followers fetch
- **Controller**: Handles partition leadership election, cluster metadata, topic management
- **ConsumerCoordinator**: Manages consumer group membership and rebalancing
- **LogSegment**: Sequential file on disk (data + offset index + time index)

## Log Storage
- Each partition is a directory with segments
- Segment = data file (.log) + offset index (.index) + time index (.timeindex)
- Segments rolled when: size > segment.bytes (1GB) or age > segment.ms (7 days)
- Index: sparse mapping of offset -> physical position in log file
- Active segment: current write target; older segments are read-only

## Replication Protocol
- Leader handles all produce/fetch requests
- ISR (In-Sync Replicas): replicas fully caught up with leader
- Follower sends FetchRequest with offset; leader responds with records since that offset
- If follower falls behind > replica.lag.time.max.ms, removed from ISR
- Leader election: first replica in ISR becomes leader
