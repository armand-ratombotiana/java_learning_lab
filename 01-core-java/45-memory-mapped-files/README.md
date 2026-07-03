# Module 45: Memory-Mapped Files

## Overview
This module explores the extreme limits of Java I/O performance. You will learn how to bypass the JVM heap entirely and map files directly into the operating system's virtual memory space, enabling zero-copy reads and writes, and facilitating microsecond-level Inter-Process Communication (IPC).

## Learning Objectives
By the end of this module, you will be able to:
1. Understand the "double-copy" bottleneck of standard I/O and how Memory-Mapped Files achieve "Zero-Copy" performance.
2. Utilize `FileChannel` to create a `MappedByteBuffer` for direct memory access.
3. Understand the mechanics of OS Page Faults and how they impact random access performance on large files.
4. Implement Inter-Process Communication (IPC) by having multiple Java processes map the same physical file.
5. Utilize `FileLock` to synchronize access to shared files across different JVMs.
6. Identify and mitigate severe system-level bugs, including the Windows un-mapping bug, the 2GB mapping limit, and OS-level `SIGBUS` crashes.

## Module Contents

### 1. Core Learning
*   [**Deep Dive**](./DEEP_DIVE.md): Comprehensive theory on Zero-Copy, `MappedByteBuffer`, Map Modes, and File Locking.

### 2. Practical Application
*   [**Mini Project**](./MINI_PROJECT.md): Build a High-Speed IPC system where two separate Java processes communicate by reading and writing to the exact same physical memory addresses via a shared mapped file.
*   [**Edge Cases & Pitfalls**](./EDGE_CASES.md): Learn about the un-unmappable buffer bug, page fault micro-pauses, the 2GB `int` limit, and `SIGBUS` process crashes.

### 3. Assessment & Interview Prep
*   [**Quizzes**](./QUIZZES.md): Test your understanding of Zero-Copy mechanics, the 2GB limit, and File Locking guarantees.
*   [**Interview Preparation**](./INTERVIEW_PREP.md): Master common interview questions regarding Page Faults, Windows deletion exceptions, and the dangers of off-heap memory.

### 4. Teaching & Learning Support
*   [**Pedagogic Guide**](./PEDAGOGIC_GUIDE.md): Recommended learning paths, teaching strategies, and mental models.

## Prerequisites
*   Solid understanding of basic File I/O (Module 07).
*   Understanding of NIO Channels and Buffers (Module 39).
*   Basic understanding of Operating System memory concepts (Virtual Memory, RAM, Disk).