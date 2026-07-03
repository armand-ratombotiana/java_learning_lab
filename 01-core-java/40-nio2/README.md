# Module 40: NIO.2 (New I/O 2)

## Overview
This module covers the modern file system API introduced in Java 7 (`java.nio.file`). You will learn how to replace the legacy `java.io.File` class with the robust `Path` and `Files` APIs to perform safe, platform-independent file manipulations, handle metadata, and monitor directories in real-time without burning CPU cycles.

## Learning Objectives
By the end of this module, you will be able to:
1. Explain the architectural advantages of NIO.2 over the legacy `java.io.File` class.
2. Utilize the `Path` interface to resolve, relativize, and normalize file system locations.
3. Use the `Files` utility class to read, write, copy, and delete files safely.
4. Process massive files efficiently and lazily using `Files.lines()`.
5. Read OS-specific file metadata using `BasicFileAttributes` and `PosixFileAttributes`.
6. Implement a CPU-efficient directory monitor using the `WatchService`.
7. Prevent critical resource leaks when using Streams with file I/O.

## Module Contents

### 1. Core Learning
*   [**Deep Dive**](./DEEP_DIVE.md): Comprehensive theory on the Path API, Files utility, Attribute Views, WatchService, and Asynchronous I/O.

### 2. Practical Application
*   [**Mini Project**](./MINI_PROJECT.md): Build a Real-Time Directory Sync & Audit service that monitors a folder and asynchronously logs file metadata.
*   [**Edge Cases & Pitfalls**](./EDGE_CASES.md): Learn about the `Files.lines()` resource leak, `WatchKey` reset traps, symbolic link infinite loops, and TOCTOU race conditions.

### 3. Assessment & Interview Prep
*   [**Quizzes**](./QUIZZES.md): Test your understanding of Path resolution, Stream resource management, and WatchService mechanics.
*   [**Interview Preparation**](./INTERVIEW_PREP.md): Master common interview questions regarding legacy File vs NIO.2, TOCTOU vulnerabilities, and AIO vs NIO.

### 4. Teaching & Learning Support
*   [**Pedagogic Guide**](./PEDAGOGIC_GUIDE.md): Recommended learning paths, teaching strategies, and mental models.

## Prerequisites
*   Solid understanding of basic File I/O (Module 07).
*   Solid understanding of the Streams API (Module 04).
*   Understanding of the `try-with-resources` statement (Module 06).