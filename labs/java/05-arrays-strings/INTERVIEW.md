# Interview Questions: Arrays & Strings

## Company-Specific Focus

### Google
- Array covariance vs generic invariance: why arrays are covariant and why that leads to ArrayStoreException
- String immutability: how it enables string pooling, caching, and security
- String concatenation: the bytecode difference between `+` in a loop vs `StringBuilder.append()`

### Microsoft
- Java vs C# char handling: Unicode support and surrogate pairs differences
- Java `String.split()` vs `Regex.Split()` in C#
- `StringBuilder` and `StringBuffer` thread-safety comparison

### Amazon
- Memory representation of arrays on heap: object header, length field, data
- Array performance: why get/put is O(1) and what influences it
- Java string deduplication in G1GC: how duplicate strings are detected and merged

### Meta
- String equality using `.equals()` vs `==` for interned strings
- Efficient string building in high-throughput applications
- String buffer and builder capacity management

### Apple
- String immutability: thread safety without synchronization
- Use of `char[]` for sensitive data to allow explicit clearing
- String interning: the string pool and Java's internalization of String literals

### Oracle
- The `String` class: why it is marked final and its fields are final (char[] value, int hash)
- JVM string pool: its location (permgen vs heap) across Java versions
- JLS 10: Arrays, array members, length field, and array cloning
- ArrayStoreException: why it is a runtime exception and how it protects against heap pollution

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 1 Two Sum | Easy | Google, Apple, Amazon, Meta | HashMap on array elements |
| 217 Contains Duplicate | Easy | Amazon, Microsoft, Meta | HashSet for O(n) detection |
| 238 Product of Array Except Self | Medium | Facebook, Apple, Google | Two-pass array with prefix/suffix products |
| 53 Maximum Subarray | Easy | Amazon, Apple, Microsoft | Kadane's algorithm |
| 344 Reverse String | Easy | Google, Microsoft, Amazon | Two-pointer array swap |
| 125 Valid Palindrome | Easy | Facebook, Apple, Google | Two-pointer on characters |
| 242 Valid Anagram | Easy | Amazon, Microsoft, Google | Int array for character counts |
| 3 Longest Substring Without Repeating Characters | Medium | Google, Amazon, Facebook | Sliding window with char index tracking |
| 49 Group Anagrams | Medium | Amazon, Apple, Expedia | Sorting string vs counting sort |
| 76 Minimum Window Substring | Hard | Facebook, Google, Amazon | Two pointer with frequency hash map |

## Real Production Scenarios
- **Cloudflare**: String concatenation in a hot loop caused a latency regression — code was using `+` inside a loop of 50K iterations, causing `O(n^2)` string copy
- **LinkedIn**: ArrayIndexOutOfBounds while processing CSV file with quoted fields — not properly handling escaped quotes
- **Netflix**: Large array of 24-bit integer values stored in `int[]` wasting 25% of memory — specialized representation considered but not implemented due to complexity

## Interview Patterns & Tips
- **Two-pointer**: The most common pattern for array and string linear traversal. It avoids extra memory. Key insight: in a sorted array, two pointers reduce O(n^2) to O(n).
- **Sliding window**: For substring or subarray problems, maintain a window and expand/shrink it accordingly. O(n) time and O(1) or O(k) space.
- **Prefix sum**: For subarray sum, range sum queries, or product of array except self, compute cumulative sums first.
- **HashMap for arrays**: For any problem requiring O(1) lookup while traversing, HashMap is your best friend.
- **String concatenation gotcha**: `s += "a"` in a loop is O(n^2) because strings are immutable. Use `StringBuilder` for O(n).
- **Array vs ArrayList**: Array has no type erasure and is covariant; `ArrayList<T>` is invariant and the actual runtime array of `Object[]` is hidden.
- **Split regex**: `String.split()` takes a regex; `.` in regex matches any character, so ".".split(".") gives an empty array.

## Deep Dive Questions
- **JVM**: How is the array length stored in memory? What is the memory offset of array data when accounting for JVM object header and length?
- **String compression**: What is the JVM flag for string compression? How are Latin-1 vs UTF-16 strings stored internally (since Java 9 `COMPACT_STRINGS`)?
- **Concurrency**: If an array is `final` but its elements are modified by multiple threads, what thread-safety guarantees exist?
- **Performance**: How does the JIT optimize a for-loop with a constant bound over an array? Loop unrolling, SIMD vectors, bound-check elimination.
- **GC**: What type of objects are arrays in the JVM? How does the mark-compact algorithm handle large arrays?
