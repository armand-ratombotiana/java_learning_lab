# Partitioning: Interview Questions

## Q1: How would you shard a user database for a social media app?
**A**: Use user_id as shard key. Consistent hashing for balanced distribution. For joins, denormalize data to the user's shard. For global operations, use fan-out queries.

## Q2: How do you handle hot spots in a sharded system?
**A**: Use composite shard keys, hash the hot key, split hot shards, cache popular data, use read replicas for hot data.

## Q3: What happens when you add a new shard?
**A**: Data migration from existing shards to new shard. Consistent hashing: only neighboring shard's data moves. Range sharding: split a range, move half.

## Q4: How does DynamoDB handle partitioning?
**A**: Hash of partition key determines partition. Items sorted by sort key within partition. Automatic splitting when partition grows. Adaptive capacity for hot partitions.

## Q5: How do you migrate from single DB to sharded?
**A**: Phase 1: Add sharding proxy, route some reads. Phase 2: Dual-write to shards. Phase 3: Backfill shards with historical data. Phase 4: Switch to sharded reads. Phase 5: Remove old DB.
