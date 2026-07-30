# Interview Deep-Dive: Consistent Hashing

## Common Questions

### Q1: Why does consistent hashing minimize rehashing compared to modulo-based hashing?
**Answer**: Modulo (key % N) causes nearly all keys to remap when N changes. Consistent hashing only remaps keys in the ring segment between the new/removed node and its predecessor — approximately K/N keys where K is total keys and N is total nodes.

### Q2: How do virtual nodes improve load distribution?
**Answer**: With physical nodes only, distribution quality depends on hash uniformity and node count. Virtual nodes spread each physical node across multiple ring positions, giving statistically uniform distribution. The standard deviation of load drops as the square root of virtual node count.

### Q3: How does DynamoDB use consistent hashing?
**Answer**: DynamoDB partitions data across nodes using consistent hashing. Each partition is replicated across 3 AZs. The ring uses 256 virtual partitions per physical node. When throughput changes, the partition splitter divides hot partitions. Consistent hashing allows DynamoDB to add/remove capacity seamlessly.

## System Design Whiteboard

**Design a distributed key-value store using consistent hashing.**
- 10 physical nodes, 150 virtual nodes each
- Hash function: MurmurHash3 (128-bit, mod 2^64)
- Replication factor: 3 (key stored on primary + 2 successors)
- Consistency: QUORUM writes (W=2), QUORUM reads (R=2)
- Read repair: background process fixes stale replicas
- Merkle trees for anti-entropy / gossip-based sync
- Hinted handoff for writes during temporary node failure

## Key Trade-offs to Discuss
- Virtual node count vs memory (ring size)
- Replication factor vs write throughput
- Consistency level vs latency
