# Debugging: Vector Database

## Common Issues

### Recall is much lower than expected
- Check efSearch and efConstruction parameters
- Verify distance metric matches training (cosine vs L2)
- Check if vectors are normalized consistently
- Graph may be fragmented (too few connections per node)

### Search returns wrong number of results
- Metadata filter may be too restrictive
- Collection may have fewer vectors than K
- Check bitset intersection logic

### Insert performance degrades over time
- Graph is getting too dense (M too high)
- Memory-mapped file is thrashing (increase heap)
- WAL growing unbounded (implement checkpointing)

### Concurrent update causes inconsistent state
- Check if ReadWriteLock is correctly acquired on write
- Verify atomicity of delete+update operations
