# Common Mistakes with Partitioning

## 1. Poor Shard Key Choice
Choosing a key with low cardinality causes hot spots (e.g., sharding by country).

## 2. Hot Spots
Uneven data distribution causes some shards to be overloaded. Solution: use composite keys or hashed keys.

## 3. Cross-Shard Queries
Joining or aggregating across shards is expensive. Design to minimize cross-shard operations.

## 4. Ignoring Rebalancing
Adding nodes requires data migration. Plan for it from the start.

## 5. Shard Key Immutability
Many systems don't allow changing shard key. Choose carefully.

## 6. Too Many Shards
Each shard adds overhead. Start with reasonable number, scale as needed.
