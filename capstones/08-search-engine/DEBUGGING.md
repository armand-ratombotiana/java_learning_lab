# Debugging: Search Engine

## Common Issues

### Query returns no results
- Check if index is empty (count documents)
- Check tokenization: does "Java" become "java" or "jav" (stemming)?
- Verify query parser: is "java spring" parsed as phrase or boolean?
- Check field name in query matches index field name

### Low relevance (wrong results ranked first)
- BM25 parameters may need tuning for your corpus
- Check if stop words are indexed (they shouldn't be)
- Field length normalization may be too high/low (adjust b)
- Consider adding field boosts (title field > body field)

### Slow query execution
- Too many segments (merge needed)
- Query is expensive (e.g., fuzzy query, wildcard prefix)
- Index too large without caching (add filter cache)
- No segment-level doc ID ordering (postings list not optimized)

### Index corruption
- Check segments_N file integrity
- Verify CRC checksums on segment files
- Last commit may be incomplete (recovery from WAL)
