# Why Partitioning Matters

## Business Impact
- **Scale**: Handle billions of records across hundreds of nodes
- **Cost**: Use commodity hardware instead of expensive single machines
- **Performance**: Each node handles less data = faster queries
- **Availability**: Smaller failure domain (one shard fails, others work)

## Technical Impact
- **Shard key selection** is critical for performance
- **Hot spots** can negate benefits of sharding
- **Cross-shard queries** are expensive
- **Rebalancing** is complex and risky

## Key Insight
A good sharding strategy makes distributed data look like a single database. A poor one creates operational nightmares.
