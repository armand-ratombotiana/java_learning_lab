# Refactoring: Search Engine

## Current Pain Points
- Single-threaded indexing (sequential document processing)
- No fuzzy/prefix/wildcard query support
- No phrase queries (position indexing but no phrase scorer)
- No Boolean query optimization (must, should, filter not separated)
- No caching layer (filter cache, field cache, query cache)

## Suggested Improvements
- Parallel indexing with document partitions
- Add Levenshtein automaton for fuzzy query support
- Implement phrase scorer using position data
- Optimize Boolean queries: filter bitset intersection, should union
- Add filter cache (bitsets for repeated filters like category)
- Add field cache (sorted doc values for sorting)
- Implement near-real-time search using NRT (reopen reader after flush)
