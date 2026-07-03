# Module 07: File I/O - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What is the difference between `InputStream`/`OutputStream` and `Reader`/`Writer`?
**Answer**:
- `InputStream` and `OutputStream` are used for **byte-based** I/O (reading/writing binary data like images, audio, or raw network packets) processing 8-bit bytes.
- `Reader` and `Writer` are used for **character-based** I/O (reading/writing text) processing 16-bit Unicode characters. They automatically handle character encoding/decoding.

### Q2: Why wrap streams in `Buffered` counterparts? (e.g., `BufferedReader`)
**Answer**:
Unbuffered streams read/write one byte or character at a time directly to the OS/Hardware, which involves an expensive native system call for every single byte. 
Buffered streams wrap the underlying stream and store chunks of data in memory (a buffer array). System calls are only made when the buffer is empty (reading) or full (writing), drastically improving performance.

### Q3: What is Java NIO and how does it differ from standard I/O?
**Answer**:
- **Standard I/O (java.io)**: Is block-oriented and synchronous (blocking). A thread reading from a stream will block until data is available.
- **Java NIO (java.nio)**: Introduced in Java 1.4, it is buffer-oriented and supports asynchronous (non-blocking) I/O. It introduces `Channels`, `Buffers`, and `Selectors`, allowing a single thread to manage multiple concurrent I/O connections (ideal for high-throughput web servers like Netty/Tomcat). NIO.2 (Java 7+) introduced the `java.nio.file.Files` and `Paths` APIs, simplifying standard file operations.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Copy a File Efficiently
**Problem**: Write a method to copy a large file (1GB+) from one location to another as efficiently as possible in Java.

**Solution**:
Instead of reading byte-by-byte or using large memory buffers manually, use NIO `FileChannel.transferTo()` or the modern `Files.copy()`, which utilizes OS-level zero-copy optimizations.

```java
public void copyFile(Path source, Path target) throws IOException {
    // Option 1: Java 7+ Files utility
    Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
    
    // Option 2: NIO Channels (under the hood of Option 1)
    /*
    try (FileChannel srcChannel = FileChannel.open(source, StandardOpenOption.READ);
         FileChannel destChannel = FileChannel.open(target, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
        srcChannel.transferTo(0, srcChannel.size(), destChannel);
    }
    */
}
```

### Scenario 2: Find the Largest File in a Directory Tree
**Problem**: Traverse a directory structure and find the file with the largest size.

**Solution**:
```java
public Optional<Path> findLargestFile(Path startDir) throws IOException {
    return Files.walk(startDir)
                .filter(Files::isRegularFile)
                .max(Comparator.comparingLong(p -> {
                    try {
                        return Files.size(p);
                    } catch (IOException e) {
                        return 0L;
                    }
                }));
}
```