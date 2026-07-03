# Interview Preparation: NIO.2 (New I/O 2)

This document covers advanced questions related to the `java.nio.file` package, resource leaks, and asynchronous I/O.

## Q1: What are the main advantages of using `java.nio.file.Files` over the legacy `java.io.File` class?
**Answer:**
1.  **Exception Handling**: `java.io.File` methods often return `false` on failure, leaving the developer guessing why it failed. `Files` methods throw specific, descriptive `IOException`s (e.g., `NoSuchFileException`, `AccessDeniedException`).
2.  **Metadata**: `Files` provides robust support for reading and writing file attributes (permissions, owners, creation times) across different operating systems via `BasicFileAttributes` and `PosixFileAttributes`.
3.  **Symbolic Links**: `Files` correctly handles symbolic links, allowing you to detect them and choose whether to follow them.
4.  **Utility Methods**: It provides powerful one-liners for common tasks (e.g., `Files.readAllLines()`, `Files.copy()`, `Files.walkFileTree()`).

## Q2: How does `Files.lines(Path)` work under the hood, and what is the critical danger of using it?
**Answer:**
`Files.lines()` returns a lazy `Stream<String>` containing the lines of the file. It does not load the entire file into memory, making it highly efficient for processing massive files.
**The Danger**: Because it is lazy, it holds an open OS file handle while the stream is active. If the stream is terminated early (e.g., by calling `.findFirst()` or encountering an exception), the file handle is left open. This causes a resource leak that will eventually crash the application with a "Too many open files" error.
**Solution**: You must always wrap `Files.lines()` in a `try-with-resources` block, because the `Stream` interface implements `AutoCloseable`.

## Q3: What is the `WatchService`, and how does it improve performance over traditional directory polling?
**Answer:**
Before NIO.2, to detect a new file in a directory, a thread had to constantly list the directory contents in a `while(true)` loop, sleep, and compare the list to the previous list. This "polling" wastes CPU cycles and disk I/O.
The `WatchService` taps directly into the operating system's native file event notification API (e.g., `inotify` on Linux). The Java thread calls `watchService.take()` and goes to sleep (consuming zero CPU). The OS wakes the thread up *only* when a registered event (e.g., `ENTRY_CREATE`) actually occurs.

## Q4: Explain the "Time-Of-Check to Time-Of-Use" (TOCTOU) race condition in file operations.
**Answer:**
A TOCTOU race condition occurs when a program checks the state of a file (e.g., `Files.exists()`) and then performs an action based on that state (e.g., `Files.createFile()`).
Because the OS is a multi-processing environment, between the time your Java thread checks if the file exists and the time it attempts to create it, another process (or thread) might have created the file. Your `createFile()` call will then fail unexpectedly.
**Prevention**: Never check first. Rely on the atomic operations provided by the OS. Just attempt to create the file and catch the `FileAlreadyExistsException`, or use atomic open options like `StandardOpenOption.CREATE_NEW`.

## Q5: What is Asynchronous I/O (AIO) in Java, and how does it differ from Non-Blocking I/O (NIO)?
**Answer:**
*   **Non-Blocking I/O (NIO)**: You ask the OS to read data. If no data is available, it returns immediately with 0 bytes. You must use a `Selector` to actively monitor the channel and try again when data is ready.
*   **Asynchronous I/O (AIO)**: You ask the OS to read data and you provide a `CompletionHandler` (a callback). The thread returns immediately. The OS performs the read entirely in the background. When the read is 100% complete, the OS invokes your callback on a background thread pool. You don't have to poll or select; you are simply notified when the job is done.