# Mental Models: Vector Database

- **Vector = Point in High-Dim Space**: Data items are points; similarity is distance in this space.
- **Index = Map for Fast Lookup**: Organizes vectors so we avoid O(n) full scan.
- **HNSW = Multi-Layer Highway**: Top layers are highways (skip between regions), bottom layers are local streets (find exact neighbors).
- **IVF = Library with Sections**: Cluster centroids are library sections; search only relevant sections.
- **Quantization = Lossy Compression**: Reduce precision to fit more vectors in RAM; similar to JPEG for image vectors.
- **Recall = % of True Nearest Neighbors Found**: Trade 5% recall for 10x speed.
