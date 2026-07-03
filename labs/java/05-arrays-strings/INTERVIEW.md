# Arrays & Strings — Interview Questions

1. **Q: How are arrays stored in memory?** A: Contiguous block of heap memory. Object header + length field + elements. Access is O(1) with bounds checking.

2. **Q: Why is String immutable?** A: (1) Thread-safe, (2) String pool interning, (3) Hash code caching, (4) Security (no modification of sensitive strings).

3. **Q: What is the String pool?** A: A hashmap of String references in heap. String literals are automatically interned. `new String("hello")` creates a separate object.

4. **Q: When should you use StringBuilder vs StringBuffer?** A: StringBuilder for single-threaded (faster). StringBuffer for multi-threaded (synchronized). Both are mutable.

5. **Q: How does String concatenation work internally?** A: Java 9+: uses `invokedynamic` with `StringConcatFactory`. Java 8: uses `StringBuilder.append()`. In loops: O(n²) without explicit StringBuilder.

6. **Q: What is the difference between array and ArrayList?** A: Array: fixed size, primitives allowed, `length` field. ArrayList: resizable, only objects, part of Collections Framework.

7. **Q: What is a jagged array?** A: An array of arrays where inner arrays have different lengths: `int[][] jagged = new int[3][]; jagged[0] = new int[2]; jagged[1] = new int[5];`.

8. **Q: How does `substring()` work?** A: Java 7+: creates new char array copy. Java 6: shared original char array (O(1) but could cause memory leaks).

9. **Q: What are compact Strings (Java 9+)?** A: String uses `byte[]` instead of `char[]`. Latin-1 = 1 byte/char. UTF-16 = 2 bytes/char. Cuts memory usage ~50% for Latin-1 text.

10. **Q: What is String interning?** A: `s.intern()` returns canonical String from pool. If not in pool, adds it. Reduces memory for duplicate strings.

11. **Q: How do you reverse a String?** A: `new StringBuilder(s).reverse().toString()`. Or recursive/loop approaches.

12. **Q: What is Arrays.deepToString()?** A: Returns string representation of multi-dimensional arrays. Unlike toString(), it handles nested arrays.
