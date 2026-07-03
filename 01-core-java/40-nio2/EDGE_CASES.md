# Edge Cases & Pitfalls: NIO.2

NIO.2 makes file handling much safer than `java.io.File`, but it introduces new edge cases regarding lazy streams, symbolic links, and OS-specific behaviors.

## 1. The `Files.lines()` Resource Leak
*   **The Scenario**: You want to find a specific word in a massive 5GB log file. You use the shiny new Stream API: 
    ```java
    Optional<String> line = Files.lines(path).filter(l -> l.contains("ERROR")).findFirst();
    ```
*   **The Pitfall**: `Files.lines()` opens a file handle to the underlying OS file. Because streams are lazy, `findFirst()` will process lines until it finds a match, and then the stream terminates. However, the stream *does not automatically close the file handle* unless it exhausts the entire file. If you find the error on line 10, the file handle remains open. Do this enough times, and your application will crash with a "Too many open files" OS error.
*   **Mitigation**: The `Stream` interface implements `AutoCloseable`. You MUST wrap `Files.lines()` (and `Files.list()`, `Files.walk()`) in a `try-with-resources` block.
    ```java
    try (Stream<String> stream = Files.lines(path)) {
        return stream.filter(l -> l.contains("ERROR")).findFirst();
    }
    ```

## 2. The `WatchService` `reset()` Trap
*   **The Scenario**: You write a loop to monitor a directory for new files using `WatchService`.
    ```java
    WatchKey key;
    while ((key = watchService.take()) != null) {
        for (WatchEvent<?> event : key.pollEvents()) { process(event); }
    }
    ```
*   **The Pitfall**: The loop runs perfectly... exactly once. When the first file is created, the event triggers, and the loop processes it. But when the second file is created, nothing happens. The `WatchKey` is "consumed" when it triggers. If you don't explicitly reset it, it will never trigger again.
*   **Mitigation**: You must call `key.reset()` at the end of the `while` loop. Furthermore, if `reset()` returns `false`, it means the directory is no longer accessible (e.g., it was deleted), and you must exit the loop.

## 3. Symbolic Link Infinite Loops
*   **The Scenario**: You use `Files.walkFileTree()` to recursively search a directory structure.
*   **The Pitfall**: If the directory contains a symbolic link that points to a parent directory, `walkFileTree` will follow the link, re-enter the parent directory, follow the link again, and so on, resulting in a `StackOverflowError` or an infinite loop.
*   **Mitigation**: By default, `Files.walkFileTree` does *not* follow symbolic links. If you explicitly configure it to follow links (using `FileVisitOption.FOLLOW_LINKS`), the API attempts to track visited directories to detect cycles. If a cycle is detected, it invokes the `visitFileFailed` method with a `FileSystemLoopException`. You must handle this exception gracefully.

## 4. `Path.resolve()` Absolute Path Override
*   **The Scenario**: You are building a file path by combining a base directory and a user-provided filename: `Path target = basePath.resolve(userInput);`.
*   **The Pitfall**: If `userInput` is a relative path (e.g., `file.txt`), it correctly appends to `basePath` (e.g., `/base/file.txt`). However, if a malicious user provides an *absolute* path (e.g., `/etc/passwd`), `Path.resolve()` will completely ignore the `basePath` and return the absolute path `/etc/passwd`.
*   **Mitigation**: Always validate user input. If you expect a relative filename, check `Paths.get(userInput).isAbsolute()` and reject it if true, or use `Path.normalize()` and verify it doesn't escape the intended base directory.

## 5. Non-Atomic File Operations
*   **The Scenario**: You check if a file exists, and if it doesn't, you create it: `if (!Files.exists(path)) Files.createFile(path);`.
*   **The Pitfall**: This is a classic "Time-Of-Check to Time-Of-Use" (TOCTOU) race condition. Between the `exists` check and the `createFile` call, another thread (or another process on the OS) might have created the file. Your `createFile` call will then throw a `FileAlreadyExistsException`.
*   **Mitigation**: Rely on the OS's atomic operations. Don't check first; just try to create the file and catch the specific exception, or use `StandardOpenOption.CREATE_NEW` with `Files.write()`, which guarantees atomic creation at the OS level.