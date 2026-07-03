# Deep Dive: Memory-Mapped Files

## 1. The Bottleneck of Standard I/O
When you read a file using standard Java I/O (`FileInputStream` or even `FileChannel.read()`), a complex sequence of events occurs:
1.  Your Java application requests data.
2.  The OS reads the data from the hard drive into an internal OS buffer (Kernel Space).
3.  The OS copies the data from the Kernel Space buffer into your Java `byte[]` array (User Space).
This **double-copy** process consumes CPU cycles and memory bandwidth. For massive files (e.g., a 10GB database file), this overhead becomes a severe bottleneck.

## 2. The Solution: Memory-Mapped Files
A Memory-Mapped File allows you to map a segment of a file directly into the virtual memory address space of your application.

*   **How it works**: When you map a file, the OS does *not* load the entire file into RAM. Instead, it creates a mapping between the file on disk and your application's virtual memory. When you read from the mapped memory, the OS automatically pages the required data from the disk into RAM (using the OS's highly optimized page cache).
*   **Zero-Copy**: Because the memory is mapped directly, there is no copying between Kernel Space and User Space. You are reading/writing directly to the OS page cache.
*   **Performance**: It is exceptionally fast, especially for random access on large files.

## 3. `MappedByteBuffer`
In Java, memory-mapped files are represented by the `MappedByteBuffer` class (a subclass of `ByteBuffer`).

```java
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

try (RandomAccessFile file = new RandomAccessFile("large_data.bin", "rw");
     FileChannel channel = file.getChannel()) {

    // Map 1GB of the file into memory, starting at byte 0, in Read-Write mode
    long size = 1024 * 1024 * 1024; // 1 GB
    MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, size);

    // Write directly to the file via memory!
    buffer.put(0, (byte) 65); // Writes 'A' to the first byte of the file
    
    // Read directly from the file
    byte b = buffer.get(0); 
}
```

### Map Modes
*   `READ_ONLY`: Any attempt to modify the buffer throws a `ReadOnlyBufferException`.
*   `READ_WRITE`: Changes made to the buffer are eventually propagated to the file on disk.
*   `PRIVATE`: Changes made to the buffer are *not* propagated to the file, and are not visible to other programs mapping the same file (Copy-on-Write).

## 4. Forcing Writes to Disk
When you write to a `READ_WRITE` `MappedByteBuffer`, the changes are written to the OS page cache immediately, but the OS decides when to actually flush those changes to the physical hard drive.
If the power fails before the OS flushes the cache, data is lost.
To guarantee that changes are written to the physical disk, you must call `buffer.force()`. (Note: This is an expensive operation).

## 5. File Locking
When multiple processes (or threads) are accessing the same file, you need to prevent data corruption. Java NIO provides `FileLock`.

*   **Exclusive Lock**: Prevents any other program from reading or writing to the locked region.
*   **Shared Lock**: Prevents other programs from writing, but allows them to read.

```java
try (FileChannel channel = FileChannel.open(Paths.get("data.txt"), StandardOpenOption.WRITE)) {
    // Try to acquire an exclusive lock on the entire file.
    // tryLock() returns null immediately if the lock is held by another program.
    // lock() blocks until the lock is acquired.
    FileLock lock = channel.tryLock();
    
    if (lock != null) {
        try {
            // ... write to file ...
        } finally {
            lock.release(); // CRITICAL: Always release the lock!
        }
    }
}
```
*Important Note*: Java File Locks are generally **advisory**, not mandatory, on Unix/Linux systems. This means they only work if all programs accessing the file agree to use the lock API. If another program ignores the lock API, it can still write to the file. On Windows, file locks are usually mandatory.