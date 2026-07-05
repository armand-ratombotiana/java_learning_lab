# Flashcards: Vector Database

Front: What is HNSW? | Back: Hierarchical Navigable Small World — a multi-layer graph for efficient ANN search. Top layers have long-range edges, bottom layers have dense local connections.

Front: What is IVF? | Back: Inverted File Index — partitions vectors into clusters via k-means; search only nearest clusters to the query.

Front: What is Product Quantization? | Back: Splits vectors into sub-vectors, quantizes each with a codebook, reducing memory by 10-100x with minimal recall loss.

Front: What is recall in ANN search? | Back: The fraction of true nearest neighbors returned by the approximate search vs brute-force kNN.

Front: What is the entry point in HNSW? | Back: The node at the top-most layer where search begins; it should be the nearest to the query among top-layer nodes.

Front: What is a WAL? | Back: Write-Ahead Log — append-only log of all mutations used for crash recovery and replication.
