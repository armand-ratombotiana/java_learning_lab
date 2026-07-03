# Pedagogic Guide: NIO.2 (New I/O 2)

## 1. Module Overview
This module transitions learners from the clunky, error-prone file handling of early Java to the modern, robust, and highly scalable `java.nio.file` package. It emphasizes the importance of separating the concept of a file "path" from the physical file on disk, and introduces OS-level concepts like native event notification and atomic operations.

## 2. Learning Paths

### Path A: The Application Developer (Focus: Path & Files API)
**Target Audience**: Developers writing standard business applications that need to read configs, parse CSVs, or move files around.
*   **Focus**: `DEEP_DIVE.md` (Path and Files) and `EDGE_CASES.md` (The `Files.lines` leak).
*   **Key Takeaway**: Understanding that `java.io.File` is obsolete, and memorizing the `try-with-resources` requirement when using `Files.lines()` to prevent production server crashes.

### Path B: The Systems Engineer (Focus: WatchService & OS Integration)
**Target Audience**: Senior developers building build tools (like Maven/Gradle), hot-reloading servers, or heavy file-processing pipelines.
*   **Focus**: `MINI_PROJECT.md` (WatchService) and `INTERVIEW_PREP.md` (TOCTOU, AIO).
*   **Key Takeaway**: Mastering the `WatchService` event loop, understanding the necessity of `key.reset()`, and recognizing TOCTOU race conditions in file system checks.

## 3. Teaching Strategies

### The "Map vs. Territory" Metaphor (Path vs. File)
To explain the difference between `Path` and the physical file:
A `Path` is just an address on a map. "123 Main Street". You can manipulate the address (change the street name, figure out the zip code) without ever actually visiting the location. You can have an address for a house that hasn't been built yet.
The `Files` utility class is the car you drive to actually interact with the physical location. You give it the `Path` (the address), and it goes there to read the data, build the house, or tear it down.

### The "Security Camera" Metaphor (WatchService)
To explain `WatchService` vs. polling:
*   **Polling**: A security guard walks around the entire building every 5 minutes checking every single door to see if it's open. It's exhausting and misses things that happen between patrols.
*   **WatchService**: The security guard sits in a chair and goes to sleep. Every door has an alarm wired directly to the guard's chair. If a door opens, the alarm wakes the guard up instantly. This is zero effort until an event actually happens.

## 4. Common Mental Blocks & Clarifications

### Block 1: "Why did my application crash with 'Too many open files'?"
*   **Clarification**: This is the most common and dangerous mistake with NIO.2. Developers are used to the Streams API (`java.util.stream`) working entirely in memory. They assume `Files.lines()` just reads strings. You must emphasize that a Stream is an abstraction. Underneath `Files.lines()` is a hard OS file handle. Because Streams are lazy, if you don't exhaust the stream, the file handle stays open forever unless explicitly closed via `try-with-resources`.

### Block 2: "My WatchService only worked once!"
*   **Clarification**: This happens when learners forget `key.reset()`. Explain that the `WatchKey` is like a mousetrap. Once it snaps (an event occurs), it cannot catch another mouse until you explicitly pull the spring back and reset it.

### Block 3: "Why does `resolve` act weird with absolute paths?"
*   **Clarification**: Show the code `Paths.get("/app/data").resolve("/etc/passwd")`. Ask what it should output. Beginners assume it concatenates: `/app/data/etc/passwd`. Explain that `resolve` means "go to this location." If you are at `/app/data` and someone says "go to `file.txt`", you stay in the folder. If someone says "go to the absolute root `/etc/passwd`", you leave your current folder entirely. This is a crucial security concept (Directory Traversal attacks).

## 5. Assessment Strategy
*   **Formative**: Provide a code snippet that uses `if (!Files.exists(path)) Files.createFile(path);`. Ask the learner to identify the security/stability flaw (TOCTOU) and provide the atomic alternative.
*   **Summative**: The `MINI_PROJECT.md` requires the learner to build a Real-Time Directory Sync tool. By forcing them to correctly implement the `WatchService` loop, handle the `reset()`, and safely read file attributes, they prove they can integrate Java with native OS file events.