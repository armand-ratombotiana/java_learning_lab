# Edge Cases & Pitfalls: Memory-Mapped Files

Memory-mapped files blur the line between Java memory and OS memory. Because they bypass standard JVM management, they introduce unique and severe failure modes.

## 1. The Un-unmappable Buffer (File Locking Bug on Windows)
*   **The Scenario**: You map a file, read some data, and then close the `FileChannel`. You then try to delete the file using `Files.delete(path)`.
*   **The Pitfall**: On Windows, mapped files are implicitly locked by the OS. Even if you close the `FileChannel`, the `MappedByteBuffer` object still exists on the JVM heap until the Garbage Collector decides to clean it up. Because the buffer object still exists, the OS lock remains, and `Files.delete()` will throw an `AccessDeniedException`.
*   **Mitigation**: There is no official `buffer.unmap()` or `buffer.close()` method in Java. The official recommendation is to rely on the GC. If you absolutely must delete the file immediately, you have to use a notorious, unsafe reflection hack to invoke the internal `Cleaner` on the buffer.
    ```java
    // EXTREMELY UNSAFE HACK (Often blocked in Java 9+ without --add-opens)
    sun.misc.Cleaner cleaner = ((sun.nio.ch.DirectBuffer) buffer).cleaner();
    cleaner.clean();
    ```

## 2. Page Fault Pauses
*   **The Scenario**: You map a 10GB file and iterate over it sequentially. You expect it to be lightning fast.
*   **The Pitfall**: When you map a file, it is not loaded into RAM. When you access a byte that is not currently in RAM, the CPU triggers a "Page Fault". The OS must pause your thread, spin up the hard drive, read the block of data into RAM, and then resume your thread. If you are doing random access on a massive file on a spinning HDD, your application will suffer from thousands of micro-pauses, resulting in terrible performance.
*   **Mitigation**: Use `buffer.load()`. This is a hint to the OS to pre-load the mapped region into physical memory, reducing page faults during actual processing. (Note: It is only a hint; the OS may ignore it).

## 3. The 2GB Limit
*   **The Scenario**: You have a 5GB database file. You try to map the entire file at once: `channel.map(MapMode.READ_WRITE, 0, 5_000_000_000L)`.
*   **The Pitfall**: The `map` method takes a `size` argument of type `long`, but the resulting `MappedByteBuffer` uses an `int` for its index (capacity, position, limit). The maximum value of an `int` is 2,147,483,647 (roughly 2GB). If you try to map more than 2GB in a single call, it will fail or truncate.
*   **Mitigation**: To process files larger than 2GB, you must map the file in chunks (e.g., mapping 1GB at a time in a loop).

## 4. Advisory vs Mandatory File Locks
*   **The Scenario**: You use `channel.lock()` to prevent other programs from modifying a config file while you read it.
*   **The Pitfall**: As mentioned in the Deep Dive, Java file locks are **advisory** on most POSIX systems (Linux/macOS). If a malicious or poorly written C++ program opens the same file and ignores the locking API, it can overwrite your data while you hold the Java lock.
*   **Mitigation**: Do not rely on file locks for absolute security against untrusted applications on Unix systems. They are for coordinating cooperative processes.

## 5. `SIGBUS` Crashes
*   **The Scenario**: You map a file into memory. While your Java application is reading from the buffer, another process on the OS truncates (shrinks) or deletes the underlying physical file.
*   **The Pitfall**: Your `MappedByteBuffer` still thinks the memory exists. When it tries to read a memory address that the OS has invalidated, the OS sends a `SIGBUS` (Bus Error) signal to the JVM. The JVM cannot catch or handle this signal. The entire Java process will instantly crash without a stack trace or an `OutOfMemoryError`.
*   **Mitigation**: Ensure strict access control over mapped files. If a file is mapped by a Java process, no other process should be allowed to truncate or delete it.