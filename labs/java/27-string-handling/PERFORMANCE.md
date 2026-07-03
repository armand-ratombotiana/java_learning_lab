# Performance: String Handling

## String Performance Guidelines

### Concatenation
Use StringBuilder for loops (pre-size if possible).
Use String.concat() for 2 strings (fastest for simple concat).
Use + for 2-3 literals (compiler optimizes to StringBuilder).
Use String.join() for collections with delimiter.

### Compact Strings (Java 9+)
- Strings with Latin-1 characters use half the memory
- All English text, ASCII identifiers, JSON keys benefit
- Metrics show average ~50% memory reduction for typical applications
- No code changes needed (automatic in JVM)

### String Deduplication (G1 GC)
- `-XX:+UseStringDeduplication`
- Deduplicates String backing char[]/byte[] arrays
- Reduces duplicate strings to share the same array
- Typical memory savings: 10-30% of string memory
- Introduces minor CPU overhead for scanning

### String Pool Tuning
```bash
-XX:StringTableSize=N  # Default 60013, use prime ~1.5x expected unique strings
```
Too small: hash collisions, slow intern()
Too large: wasted memory

### Avoid
- ```java
  s.intern()  // in loops — cause of many production OOMs
  ```
- ```java
  new String("literal")  — creates unnecessary duplicate
  ```
- ```java
  s.substring(0, n)  // in Java 6 memory leak
  ```

## Microbenchmark Results
```
Operation                     Time (ns/op)
----------------------------------------
+ concat (2 literals)          ~5 ns
StringBuilder.append()         ~10 ns
String.concat()                ~7 ns
String.format()                ~200 ns
String.join()                  ~15 ns
s.intern()                     ~30-100 ns
```
