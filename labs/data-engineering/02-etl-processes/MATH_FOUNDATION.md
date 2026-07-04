# Math Foundation

## Data Volume
Raw = Sum(source sizes), Intermediate = Raw + enrichment, Final = After aggregation
Compression ratio: 3-10x with Parquet

## Complexity
O(n): row-wise ops, O(n log n): sorts, O(n*m): joins

## Parallelism
Optimal = min(SourcePartitions, TargetPartitions, Cores*2)
