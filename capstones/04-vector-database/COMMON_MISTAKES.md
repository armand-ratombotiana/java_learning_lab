# Common Mistakes: Vector Database

- **Not normalizing vectors**: Cosine similarity on non-normalized vectors gives incorrect rankings. Always L2-normalize before insert and query.
- **Too few connections (M)**: Low M creates fragmented graph; search gets stuck in local minima. M >= 16 recommended.
- **efConstruction too low**: Poor graph connectivity during construction leads to low recall at search time. Use efConstruction = 2 * efSearch.
- **No concurrent read safety**: Readers traversing graph while writer modifies nodes will see inconsistent state. Use read-write locks.
- **Memory-mapped file not synced**: Data loss on crash if MappedByteBuffer isn't periodically forced to disk.
- **Metadata filtering after top-K**: Filtering after finding nearest neighbors may return fewer than K results. Filter before or use larger efSearch.
