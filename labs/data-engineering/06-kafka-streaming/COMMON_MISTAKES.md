# Common Mistakes

1. **Wrong Serde**: Missing serialization config causes errors
2. **State Store Name Conflicts**: Duplicate names across topologies
3. **Not Handling Tombstones**: Null values in KTable cause NPE
4. **Ignoring Rebalance**: No state listener for graceful shutdown
