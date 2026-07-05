# Code Deep Dive: Vector Database

## HNSW Index

`HNSWIndex` manages a list of layers, each containing a ConcurrentHashMap of node IDs to neighbor lists. Search uses a priority queue (min-heap by distance). The `searchLayer` method visits neighbors, computes distance, and adds closer nodes. Insert navigates the graph similarly, then performs bidirectional connections.

## Distance Calculation

`DistanceCalculator` uses an enum-based strategy pattern. Cosine distance precomputes normalized vectors. L2 uses the `java.lang.Math.sqrt` carefully (hot path avoids sqrt by comparing squared distances and only taking sqrt at the end).

## Concurrency

`ReadWriteLock` on each layer allows multiple concurrent searches with exclusive writes. Nodes are immutable after creation; updates are delete+insert. The WAL uses `FileChannel` with `force(true)` for durability.

## Metadata Filtering

`MetadataStore` maintains inverted indexes: HashMap<String, HashMap<Object, Set<Long>>> for equality filters, and TreeMap for range queries. Filter results are intersected with ANN results using a bitset.
