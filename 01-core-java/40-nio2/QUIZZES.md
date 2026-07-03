# Quizzes: NIO.2 (New I/O 2)

Test your knowledge of the Path API, Files utility, and WatchService.

## Quiz 1: Path and Files API

**Q1: What is the primary difference between `java.io.File` and `java.nio.file.Path`?**
- A) `Path` can only be used for network sockets.
- B) `Path` is an interface that represents a location in a file system, offering robust, platform-independent path manipulation without necessarily touching the physical disk, whereas `File` is a concrete class with limited metadata support and poor error reporting.
- C) `File` is faster than `Path`.
- D) `Path` automatically encrypts data.
*Answer: B*

**Q2: What happens if you call `path.resolve("/absolute/path")` where `path` is `/var/log`?**
- A) It returns `/var/log/absolute/path`.
- B) It throws an `IllegalArgumentException`.
- C) It completely ignores the base path and returns `/absolute/path`.
- D) It returns `null`.
*Answer: C (If the argument to `resolve` is an absolute path, it overrides the base path entirely).*

## Quiz 2: Resource Management

**Q1: Why is it dangerous to use `Files.lines(path).findFirst()` without a `try-with-resources` block?**
- A) It will read the entire file into memory, causing an `OutOfMemoryError`.
- B) `Files.lines()` opens an underlying OS file handle. Because the stream is lazy, `findFirst()` terminates the stream early. If not wrapped in a `try-with-resources` block, the file handle is left open, eventually causing a "Too many open files" OS error.
- C) It will delete the file after reading.
- D) It will block the main thread forever.
*Answer: B*

**Q2: When using the `WatchService` to monitor a directory, what must you do at the end of your event processing loop to ensure you receive future events?**
- A) Re-register the path with the `WatchService`.
- B) Call `watchService.take()` again.
- C) Call `key.reset()`. If you don't reset the `WatchKey`, it remains in a "signaled" state and will not queue new events.
- D) Close and reopen the `WatchService`.
*Answer: C*

## Quiz 3: File Attributes

**Q1: Which attribute view should you use to retrieve the file owner and group permissions (e.g., `rwxr-xr-x`) on a Linux system?**
- A) `BasicFileAttributes`
- B) `DosFileAttributes`
- C) `PosixFileAttributes`
- D) `AclFileAttributes`
*Answer: C*