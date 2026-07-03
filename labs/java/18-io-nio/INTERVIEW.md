# Interview Questions — I/O & NIO

## Beginner
1. What is the difference between byte streams and character streams?
2. How does `BufferedInputStream` improve performance?
3. What is try-with-resources and why is it important for I/O?

## Intermediate
4. Explain the differences between `File`, `Path`, and `Files`.
5. How does NIO's `FileChannel` differ from `FileInputStream`/`FileOutputStream`?
6. What is a `ByteBuffer`? What is the purpose of `flip()` and `compact()`?
7. How would you traverse a directory tree and process all `.java` files?

## Advanced
8. How does zero-copy I/O work in Java? What methods enable it?
9. Explain the Selector (Reactor) pattern for non-blocking I/O.
10. What is memory-mapped I/O? When should you use it?
11. How does NIO.2 integrate with the Stream API?

## Problem-Solving
12. "Design an efficient file watcher that monitors 10,000 directories."
13. "Implement a fast binary log parser that can handle multi-GB files."
14. "Our application leaks file descriptors. How would you diagnose and fix it?"
