# Why Bloom Filters Matter

Bloom filters matter because they **save enormous amounts of memory** in systems handling billions of elements.

## Practical Impact

- **Databases**: **Apache Cassandra** and **PostgreSQL** use Bloom filters to avoid expensive disk lookups for non-existent keys
- **Caching**: **Google Bigtable** and **HBase** use Bloom filters to reduce cache misses
- **Web browsers**: **Chrome** uses a Bloom filter to check URLs against a database of malicious sites (downloaded once, checked locally)
- **Blockchain**: **Bitcoin** uses Bloom filters for Simplified Payment Verification (SPV) — lightweight wallet queries
- **Content filtering**: **Medium** and other platforms use Bloom filters for deduplication and recommendation filtering
- **Networking**: **Routers** use Bloom filters for packet classification and flow tracking
- **Search engines**: Crawlers use Bloom filters to avoid revisiting URLs

## Why Learn Bloom Filters

1. **System design essential**: Bloom filters appear in many high-scale system designs
2. **Probabilistic thinking**: the first data structure that trades accuracy for space
3. **Interview relevant**: system design questions (cache, dedup, spell check)
4. **Mathematical insight**: optimal k calculation demonstrates applied probability
5. **Java ecosystem**: Guava provides `BloomFilter`; understanding its internals is valuable

Bloom filters are the **most famous probabilistic data structure** — they demonstrate that sometimes "good enough" is better than "exact."
