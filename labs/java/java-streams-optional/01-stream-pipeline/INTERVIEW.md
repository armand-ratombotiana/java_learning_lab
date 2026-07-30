# INTERVIEW — Stream Pipeline

## Company-Specific Focus

### Google
- Lazy evaluation — when does the stream actually execute?
- `findFirst()` vs `findAny()` — ordering implications

### Amazon
- Stream pipeline memory pressure with large datasets
- `Files.lines()` — try‑with‑resources pattern

### Oracle
- Stream characterstics: `ORDERED`, `SIZED`, `DISTINCT`, `SORTED`
- `spliterator()` and `trySplit()`

## Common Questions
1. Explain the order of execution in `collection.stream().filter().map().collect()`.
2. Can a stream be reused?
3. What happens if you chain `sorted()` after `limit()`?
