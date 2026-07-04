# Partitioning: Quiz

## Questions
1. What is the difference between hash and range sharding?
2. What is consistent hashing?
3. How does consistent hashing minimize data movement?
4. What is a shard key?
5. What causes hot spots in sharded systems?
6. What is rebalancing?
7. How do virtual nodes improve consistent hashing?
8. What is scatter/gather?
9. When should you use range vs hash sharding?
10. What is the tradeoff of directory-based partitioning?

## Answers
1. Hash: even distribution, no range queries. Range: good range queries, uneven distribution
2. Hash ring where nodes/keys placed, key assigned to nearest node
3. Only keys that hash between the new/removed node and its neighbor move
4. The key used to determine which shard stores a record
5. Poor shard key choice, skewed data distribution
6. Moving data when nodes join/leave to maintain balance
7. Each real node maps to multiple tokens for better distribution
8. Send query to all shards, collect and merge results
9. Range for range queries, hash for uniform distribution
10. Flexibility vs. scalability bottleneck of directory
