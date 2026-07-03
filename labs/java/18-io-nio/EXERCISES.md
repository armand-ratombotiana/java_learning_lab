# Exercises — I/O & NIO

## Beginner
1. Read a text file line by line using `BufferedReader` and print each line.
2. Write a byte array to a binary file.
3. Copy a file using `InputStream` and `OutputStream`.
4. List all `.txt` files in a directory.

## Intermediate
5. Implement a recursive directory search for files matching a glob pattern using `Files.walk()`.
6. Read a large CSV file with `Files.lines()` and filter rows with a stream.
7. Use `FileChannel.transferTo()` to copy a large file efficiently.
8. Use `WatchService` to monitor a directory for new files.

## Advanced
9. Implement a simple HTTP file server using `ServerSocketChannel` and `Selector`.
10. Memory-map a 1 GB file and read specific offsets.
11. Compare throughput: `FileInputStream` vs `BufferedInputStream` vs `FileChannel` vs memory-mapped.

## Debugging
12. Write a program that intentionally leaks file handles and observe `ulimit` / `lsof`.

## Reflection
13. Refactor a legacy I/O codebase (File + streams) to NIO.2 (Path + Files).
