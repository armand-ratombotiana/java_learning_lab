# Performance: Search Engine

## Throughput Targets
- Indexing throughput: 10k docs/s (1KB each)
- Query latency: < 10ms P99 for term query on 10M docs
- Merge speed: 50 MB/s
- P99 recall: > 95% for top-10 results

## Bottlenecks
- **Tokenization**: Java String operations are expensive. Use Character.isLetter/isDigit with pre-allocated buffers.
- **Postings list encoding**: Naive int arrays waste memory. Use delta encoding + VInt for doc IDs.
- **Terms dictionary lookup**: HashMap is memory-heavy. Use FST (Finite State Transducer) for prefix compression.
- **Score computation**: BM25 involves log + division per match. Cache IDF values per segment.
- **Disk seeks**: Random read on many segments. Merge smaller segments.

## Optimization Strategies
- Use skip lists in postings for faster AND intersection
- Memory-map index files for faster reads
- Reuse Analyzer instances across documents
- Pre-compute TF-IDF/BM25 component values
- Use block-based KD-tree for numeric field range queries
- Document compression: LZ4 for stored fields
