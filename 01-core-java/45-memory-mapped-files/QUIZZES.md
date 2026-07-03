# Quizzes: Memory-Mapped Files

Test your knowledge of MappedByteBuffer, Zero-Copy, and File Locking.

## Quiz 1: Core Mechanics

**Q1: How does a Memory-Mapped File achieve "Zero-Copy" performance?**
- A) It compresses the data so it copies faster.
- B) It maps the file directly into the application's virtual memory space. When the app reads the memory, it reads directly from the OS page cache, eliminating the need to copy data from Kernel Space to User Space (the JVM heap).
- C) It uses a dedicated hardware chip on the motherboard.
- D) It copies the data asynchronously in a background thread.
*Answer: B*

**Q2: What is the maximum size of a file chunk you can map into a single `MappedByteBuffer`?**
- A) 1 GB
- B) Exactly 2 GB (because the buffer uses a 32-bit signed `int` for its capacity and index).
- C) 4 GB
- D) Unlimited (limited only by physical RAM).
*Answer: B*

## Quiz 2: Edge Cases and Dangers

**Q1: You map a file, read it, and close the `FileChannel`. On Windows, you immediately try to delete the file using `Files.delete()`, but it throws an `AccessDeniedException`. Why?**
- A) Windows does not allow deleting files.
- B) The file is read-only.
- C) The `MappedByteBuffer` object still exists on the JVM heap. Until the Garbage Collector reclaims it, the OS retains a lock on the physical file, preventing deletion.
- D) You must call `channel.force()` before deleting.
*Answer: C*

**Q2: What happens if a Java application is reading from a `MappedByteBuffer`, and another process on the OS deletes the physical file from the hard drive?**
- A) The JVM throws an `IOException`.
- B) The JVM throws a `FileNotFoundException`.
- C) The JVM returns `-1` or `null`.
- D) The OS sends a `SIGBUS` signal, causing the entire Java process to crash instantly without a stack trace.
*Answer: D*

## Quiz 3: File Locking

**Q1: What does it mean that Java File Locks are generally "advisory" on Linux/Unix systems?**
- A) They provide advice on performance.
- B) They only work if every single program accessing the file agrees to check and respect the lock API. If a rogue C++ program ignores the lock API, it can still overwrite the file.
- C) They automatically unlock after 5 minutes.
- D) They only lock the file for reading, not writing.
*Answer: B*