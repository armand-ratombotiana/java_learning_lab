# Deep Dive: NIO.2 (New I/O 2)

## 1. The Evolution of File I/O
The original `java.io.File` class (from Java 1.0) had significant flaws. It lacked support for symbolic links, its methods returned `boolean` instead of throwing descriptive exceptions (e.g., `delete()` just returned `false` if it failed, leaving you guessing why), and it couldn't access file metadata easily.

Java 7 introduced NIO.2 (JSR 203), which completely overhauled file system interactions with the `java.nio.file` package.

## 2. The `Path` and `Paths` API
The `Path` interface replaces `java.io.File` as the primary way to represent a file or directory location.

*   **Creation**: `Path p = Paths.get("folder", "subfolder", "file.txt");` (or in Java 11+, `Path.of(...)`).
*   **Manipulation**: `Path` provides robust methods for path manipulation without touching the physical disk:
    *   `p.normalize()`: Removes redundancies like `.` and `..` (e.g., `/a/b/../c` becomes `/a/c`).
    *   `p.resolve(other)`: Appends a path.
    *   `p.relativize(other)`: Calculates the relative path from one path to another.

## 3. The `Files` Utility Class
The `Files` class is a massive utility class containing static methods for almost all file operations. Unlike the old `File` class, these methods throw specific `IOException`s (like `NoSuchFileException` or `AccessDeniedException`).

*   **Basic Ops**: `Files.createFile()`, `Files.createDirectories()`, `Files.copy()`, `Files.move()`, `Files.delete()`.
*   **Reading/Writing**: 
    *   `Files.readString(path)` (Java 11+)
    *   `Files.readAllLines(path)`
    *   `Files.write(path, bytes)`
*   **Stream Integration**: `Files.lines(path)` returns a `Stream<String>`, allowing you to process massive files lazily without loading the whole file into memory.

## 4. File Attributes and Metadata
NIO.2 provides a unified way to read file metadata (size, creation time, permissions) across different operating systems using "Attribute Views".

*   **`BasicFileAttributes`**: Common attributes supported by all OSs (size, creation time, last modified, isDirectory).
*   **`PosixFileAttributes`**: Unix/Linux specific attributes (owner, group, permissions like `rwxr-xr-x`).
*   **`DosFileAttributes`**: Windows specific attributes (hidden, readonly, system).

```java
// Read all basic attributes in a single bulk OS call (highly efficient)
BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
System.out.println("Creation Time: " + attr.creationTime());
```

## 5. The `WatchService`
Before NIO.2, if you wanted to know if a file was added to a directory, you had to write a loop that constantly polled the directory (`while(true) { checkFiles(); Thread.sleep(1000); }`). This burns CPU.

The `WatchService` taps directly into the Operating System's native file event notification system (e.g., `inotify` on Linux, `FSEvents` on macOS). It allows your Java thread to sleep (block) until the OS wakes it up to report a file change.

```java
WatchService watchService = FileSystems.getDefault().newWatchService();
path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);

WatchKey key;
while ((key = watchService.take()) != null) { // Blocks until an event occurs
    for (WatchEvent<?> event : key.pollEvents()) {
        System.out.println("Event: " + event.kind() + " on file: " + event.context());
    }
    key.reset(); // Crucial: put the key back in the ready state
}
```

## 6. Asynchronous I/O (AIO)
NIO.2 introduced Asynchronous Channels (`AsynchronousFileChannel`, `AsynchronousSocketChannel`). 
Unlike the `Selector` approach in NIO (where you poll to see if a channel is ready), AIO uses a pure callback (or `Future`) model. You tell the OS: "Read 10KB from this file, and when you are completely finished, execute this `CompletionHandler`." The thread returns immediately and does other work while the OS reads the disk in the background.