# Interview Questions: ArrayList Deep Dive

## Company-Specific Focus

### Google
- ArrayList: resizable array implementation of List interface
- Internal array: Object[] elementData; grow strategy: 1.5x (oldCapacity + (oldCapacity >> 1))
- Default capacity: 10, lazy initialization in Java 8+

### Microsoft
- ArrayList vs C# List<T>: similar implementation
- Capacity management: ensureCapacity(int) for pre-allocation

### Amazon
- ArrayList with initial capacity for known data sizes
- TrimToSize: reducing capacity to free memory
- SubList: view of a range, structural modifications invalidate it

### Meta
- Random access O(1): direct array index, unlike LinkedList
- Add at index: O(n) due to element shifting
- Remove from front: avoid with ArrayList, use LinkedList or ArrayDeque

### Apple
- ArrayList memory efficiency: only stores array reference + length
- Fail-fast iterator: modCount check on every iteration
- toArray() vs toArray(T[] a): type-safe array creation

### Oracle
- ArrayList JCF specification: resizable array implementation
- grow() method: 1.5x growth factor with potential overflow protection
- serialization: writeObject/readObject for efficient serialization

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 1 Two Sum | Easy | Google, Amazon, Meta | HashMap index to value mapping |
| 15 3Sum | Medium | Amazon, Google, Apple | Sorting + two-pointer |
| 217 Contains Duplicate | Easy | Amazon, Apple, Google | HashSet or sort |
| 238 Product of Array Except Self | Medium | Amazon, Google, Meta | Prefix/suffix product arrays |
| 53 Maximum Subarray | Easy | Amazon, Apple, Google | Kadane's algorithm |
| 118 Pascal's Triangle | Easy | Amazon, Google | List<List<Integer>> construction |
| 350 Intersection of Two Arrays II | Easy | Amazon, Google, Apple | ArrayList result from HashMap counts |

## Real Production Scenarios
- **Uber**: ArrayList with default capacity added 100K elements causing 14 resizes — pre-sized to avoid resizing
- **Netflix**: Removing from front of ArrayList in a loop caused O(n^2) behavior — replaced with ArrayDeque

## Interview Patterns & Tips
- **Pre-allocate capacity**: If the size is known, use new ArrayList<>(size) to avoid resizes
- **Avoid remove(0)**: Removing from the front is O(n) due to shift
- **batch operations**: addAll pre-allocates, may be more efficient than repeated add
- **read-only**: List.of() for immutable lists

## Deep Dive Questions
- **Growth factor**: Why 1.5x and not 2x?
- **Overflow check**: How does ArrayList handle overflow of capacity?
- **subList**: Why does modifying the original list invalidate subList?
- **Fast-fail**: How does the iterator detect concurrent modifications?
- **Compact strings**: Does ArrayList use compact string representation?