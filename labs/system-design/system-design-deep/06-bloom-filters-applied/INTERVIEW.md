# Interview Deep-Dive: Bloom Filters

## Common Questions

### Q1: Why does Cassandra use Bloom filters?
**Answer**: Cassandra (and DynamoDB) store data in SSTables on disk. Each SSTable has a Bloom filter in memory. Before querying an SSTable, Cassandra checks the Bloom filter. If it reports the key is not present, Cassandra skips that SSTable entirely — avoiding an expensive disk I/O. This reduces read amplification from O(N) SSTables to O(1) in practice.

### Q2: How do you calculate the optimal number of hash functions?
**Answer**: Optimal k = (m / n) * ln(2), where m is bits and n is expected elements. At this k, the false positive rate is minimized: (0.6185)^(m/n). For a 1% false positive rate, you need ~9.6 bits per element.

### Q3: Can a Bloom filter return false negatives?
**Answer**: No. Bloom filters never return false negatives. If an element was added, all k bits are set, so the filter will always report "might contain" correctly. False positives are possible (all k bits happened to be set by other elements), but false negatives are impossible.

## System Design Whiteboard

**Design a URL deduplication system for a web crawler.**
- Target: 1 billion unique URLs
- False positive rate: 1%
- Bloom filter size: 1.2 GB (10 bits/url)
- 7 hash functions (optimal for 1% FPP)
- Sharded across 10 nodes (120 MB each)
- Counting Bloom for recently crawled URLs (enables recrawl)
- Scalable Bloom for the full history
- Disk-backed Bloom filter for crash recovery

## Key Trade-offs to Discuss
- Space vs false positive rate (logarithmic trade-off)
- Deletion support (counting Bloom vs Cuckoo)
- Memory vs CPU (hash function computation cost)
