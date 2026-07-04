# References: Hash Tables

## Books

- **Cormen et al., Introduction to Algorithms (4th ed.)** — Chapter 11: Hash Tables
- **Robert Sedgewick, Algorithms (4th ed.)** — Section 3.4: Hash Tables
- **Brian Goetz, Java Concurrency in Practice** — Chapter 5: Building Blocks (ConcurrentHashMap)
- **Joshua Bloch, Effective Java (3rd ed.)** — Item 11: Always override hashCode when you override equals

## Java Documentation

- [java.util.HashMap](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/HashMap.html)
- [java.util.Hashtable](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Hashtable.html) (legacy)
- [java.util.concurrent.ConcurrentHashMap](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ConcurrentHashMap.html)
- [Object.hashCode()](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Object.html#hashCode())

## Academic Papers

- **Morris** (1962) — "Scatter Storage Techniques" — first major hash table survey
- **Celis** (1986) — "Robin Hood Hashing" — variance-reducing collision resolution
- **Pagh & Rodler** (2001) — "Cuckoo Hashing" — O(1) worst-case lookup with multiple hash functions

## Online Resources

- [Visualgo.net — Hash Table visualization](https://visualgo.net/en/hashtable)
- [OpenJDK HashMap source](https://github.com/openjdk/jdk/blob/master/src/java.base/share/classes/java/util/HashMap.java)
- [OpenJDK ConcurrentHashMap source](https://github.com/openjdk/jdk/blob/master/src/java.base/share/classes/java/util/concurrent/ConcurrentHashMap.java)
- [Baeldung — Guide to hashCode() in Java](https://www.baeldung.com/java-hashcode)
