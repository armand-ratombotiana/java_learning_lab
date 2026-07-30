# INTERVIEW — Stream Collectors

## Company-Specific Focus

### Google
- Custom `Collector` — `supplier`, `accumulator`, `combiner`, `finisher`, `characteristics`
- `Collector.Characteristics.CONCURRENT` — when can you use it

### Amazon
- `toMap` with merge function for duplicate keys
- Stream API memory efficiency with large datasets

### Oracle
- `groupingByConcurrent` vs `groupingBy` — thread safety
- `Collectors.collectingAndThen()` — post‑processing

## Common Questions
1. What is the difference between `groupingBy` and `partitioningBy`?
2. How do you handle duplicate keys in `toMap`?
3. What does `Characteristics.IDENTITY_FINISH` mean?
