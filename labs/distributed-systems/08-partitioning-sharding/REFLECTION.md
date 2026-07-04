# Partitioning: Reflection

## Key Insights
- Shard key selection is the most important design decision
- Avoid cross-shard queries whenever possible
- Consistent hashing is the most practical strategy for dynamic clusters
- Rebalancing is expensive; plan for it

## Questions
1. What is your current data growth rate? When will you need sharding?
2. Could a different shard key improve your query patterns?
3. Do you have hot spots that could be avoided?
4. How would you rebalance with zero downtime?

## Personal Notes
- Sharding should be a last resort after other optimizations
- Design for sharding from the start even if you don't need it yet
- The shard key you choose will haunt you forever
