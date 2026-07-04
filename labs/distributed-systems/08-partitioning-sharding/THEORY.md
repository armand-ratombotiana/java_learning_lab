# Partitioning: Theory

## Partitioning Strategies

### Range Sharding
- Data divided by key ranges (e.g., A-M, N-Z)
- Efficient range queries
- Prone to hot spots and uneven distribution

### Hash Sharding
- Hash function maps keys to partitions
- Even distribution (with good hash)
- Loses range query ability

### Consistent Hashing
- Hash ring with virtual nodes
- Minimal data movement on node change
- Used by Cassandra, DynamoDB

### Directory-Based
- Lookup table maps keys to partitions
- Flexible but requires managing directory

## Rebalancing
- Moving data when nodes added/removed
- Must minimize data movement and downtime

## Cross-Partition Queries
- Scatter/gather across all partitions
- Coordination overhead
- Avoid if possible
