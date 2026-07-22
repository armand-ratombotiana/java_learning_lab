# Interview Questions: HashMap Design & Build

## Company-Specific Focus

### Google
- Design a HashMap from scratch: array + linked list/tree bins
- Hash function design: uniform distribution, bit mixing
- Collision resolution: chaining vs open addressing

### Microsoft
- HashMap design vs C# Dictionary: collision handling differences
- Load factor: why 0.75 is the default — time-space tradeoff

### Amazon
- Scalable HashMap: how to handle concurrent reads and writes
- Capacity planning: avoid resizing in production with good estimates
- Distributed hash tables: consistent hashing for multi-node caches

### Meta
- Implement a HashMap with open addressing: linear probing, quadratic probing, double hashing
- Deletion: tombstone marking in open addressing
- Rehash: when and how to resize

### Apple
- Immutable HashMap: building with Java's Map.of()
- Compact HashMap: reducing memory footprint
- IdentityHashMap: reference equality for special use cases

### Oracle
- HashMap design: JCF specification guidelines
- Hash table time complexity: O(1) average, O(n) worst case
- Implementation trade-offs: tree vs list bins

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 706 Design HashMap | Easy | Google, Amazon, Apple | Implement put, get, remove |
| 705 Design HashSet | Easy | Amazon, Google, Apple | Implement add, remove, contains |
| 380 Insert Delete GetRandom O(1) | Medium | Google, Amazon, Apple | HashMap + ArrayList |
| 381 Insert Delete GetRandom O(1) with Duplicates | Hard | Amazon, Google | HashMap of sets + ArrayList |
| 895 Maximum Frequency Stack | Hard | Amazon, Google | HashMap + stack of stacks |

## Real Production Scenarios
- **Google**: Building a custom hash map for protocol buffers field lookup — optimized for small maps (linear probing)
- **LinkedIn**: Custom identity hash map for object interning to deduplicate data
- **Amazon**: Concurrent hash map design for DynamoDB Accelerator (DAX) cache

## Interview Patterns & Tips
- **Chaining**: each bucket has a linked list; simple but cache-inefficient
- **Open addressing**: linear probing, all elements in the array; better cache locality
- **Load factor**: increase for memory efficiency, decrease for speed
- **hashCode quality**: uniform distribution reduces collision probability

## Deep Dive Questions
- **hash function**: How do you design a good hash function for strings?
- **Open addressing**: How to handle deletion in open addressing?
- **Resize**: What is the cost of resizing? How do you minimize it?
- **Load factor**: What is the impact of load factor on performance?
- **Thread safety**: How would you add thread safety to HashMap?