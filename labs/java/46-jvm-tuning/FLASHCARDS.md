# Flashcards: JVM Tuning

**Q: What is the difference between -Xms and -Xmx?**
A: -Xms = initial heap size (committed at startup). -Xmx = maximum heap size (heap can grow to this).

**Q: What is -Xmn?**
A: Young generation size (Eden + two Survivor spaces).

**Q: What happens when code cache fills?**
A: JIT stops compiling (first C2, then C1). Methods run in interpreter. Performance degrades.

**Q: What is the purpose of -XX:MaxMetaspaceSize?**
A: Limits the native memory used for class metadata, preventing Metaspace from growing unbounded.

**Q: What is the benefit of -XX:+AlwaysPreTouch?**
A: Pre-commits all heap pages at startup, eliminating runtime page faults (but increases startup time).

**Q: What does -XX:+UseStringDeduplication do?**
A: G1 merges identical String char[] arrays during young GC, reducing memory usage.

**Q: How do large pages improve performance?**
A: 2 MB pages reduce TLB misses (one entry covers 512× more memory than 4 KB pages).

**Q: What does -XX:+PrintFlagsFinal do?**
A: Prints all JVM flags with their current values and origins (default, command-line, etc.).
