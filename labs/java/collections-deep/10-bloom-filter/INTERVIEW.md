# Interview Questions: Bloom Filter

## Company-Specific Focus

### Google
- Bloom filter: probabilistic data structure for set membership
- False positive rate: tunable based on size and hash count
- No false negatives: if element is in set, filter always says yes
- Space-efficient: uses bit array instead of storing the elements

### Microsoft
- Bloom filter vs .NET HashSet: memory comparison for large datasets
- Applications: spell checkers, network routers, content filtering

### Amazon
- Distributed systems: Bloom filters for cache efficiency (avoid cache misses)
- DynamoDB: using bloom filters to reduce disk lookups
- Apache Cassandra: Bloom filter usage for SSTable queries
- Redis: Bloom filter module for large-scale deduplication

### Meta
- Social graph: Bloom filter for friend recommendations (avoid processing seen users)
- URL deduplication: web crawler uses bloom filter for visited URLs
- Ad targeting: Bloom filter for fast user segment membership

### Apple
- Memory-constrained environments: Bloom filter for efficient approximate storage
- Bookkeeping: track processed items in batch operations
- Cache: Bloom-guided cache to reduce expensive lookups

### Oracle
- Not part of standard JDK: needs Guava or custom implementation
- Common in distributed databases and caching layers
- Theoretical foundation: probability of false positives given parameters

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| (No direct LeetCode problems — but bloom filter is frequently asked in system design) |
| 211 Design Add and Search Words Data Structure | Medium | Google, Amazon, Apple | Similar prefix matching concept |

## Real Production Scenarios
- **LinkedIn**: Feed deduplication using Bloom filters — prevented showing same content twice
- **Netflix**: Using Bloom filter to check if a movie recommendation was already shown
- **Uber**: Geo-hashing with Bloom filter for ride matching optimization

## Interview Patterns & Tips
- **K hash functions**: use multiple hash functions for lower false positive rate
- **Optimal k**: ln(2) * (m/n) where m is bits per element
- **Size estimation**: For 1% false positive rate, ~10 bits per element
- **Cannot delete**: standard Bloom filter doesn't support removal (use Counting Bloom Filter)

## Deep Dive Questions
- **False positive math**: How is the false positive probability computed?
- **Optimal hash count**: What is the optimal number of hash functions?
- **Counting bloom filter**: How does counting bloom filter support deletion?
- **Scalable bloom filter**: How to handle growing data sets?
- **Cassandra**: How does Cassandra use Bloom filters for query efficiency?