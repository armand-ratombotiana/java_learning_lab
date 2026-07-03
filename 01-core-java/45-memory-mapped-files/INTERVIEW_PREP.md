# Interview Preparation: Memory-Mapped Files

This document covers advanced questions related to Zero-Copy, Page Faults, and the dangers of off-heap memory management.

## Q1: What does "Zero-Copy" mean in the context of Memory-Mapped Files?
**Answer:**
In standard Java I/O, reading a file requires the OS to read data from the hard drive into Kernel Space memory, and then the JVM must copy that data from Kernel Space into User Space (the JVM heap byte array). This "double-copy" consumes CPU and RAM.
A Memory-Mapped File maps the file directly into the application's virtual memory space. When the Java application reads from the `MappedByteBuffer`, it reads *directly* from the OS's page cache. There is zero copying of data between Kernel Space and User Space, resulting in massive performance gains for large files.

## Q2: What is a Page Fault, and how does it affect the performance of `MappedByteBuffer`?
**Answer:**
When you map a 10GB file, the OS does not load 10GB of data into RAM. It just creates a virtual memory map.
When your Java code tries to read a byte from the buffer, the CPU checks if that specific "page" of memory is currently physically present in RAM. If it isn't, the CPU triggers a "Page Fault". The OS suspends your Java thread, reads that specific chunk of the file from the hard drive into RAM, and then resumes your thread.
If you do random access across a massive file on a slow spinning hard drive, your application will suffer from thousands of page faults (micro-pauses), severely degrading performance.

## Q3: What happens if you try to map a 5GB file into a single `MappedByteBuffer`?
**Answer:**
It will fail. The `FileChannel.map()` method takes a `size` argument of type `long`, but the resulting `MappedByteBuffer` relies on an `int` for indexing (capacity, limit, position). The maximum value of a signed 32-bit `int` is 2,147,483,647 (roughly 2GB).
To process a 5GB file using memory mapping, you must map it in chunks (e.g., mapping 1GB at a time in a loop, processing it, and then mapping the next 1GB).

## Q4: Why is it difficult to delete a file on Windows after you have finished using its `MappedByteBuffer`?
**Answer:**
Windows places a strict lock on memory-mapped files. In Java, there is no official `buffer.close()` or `buffer.unmap()` method. The unmapping only occurs when the Garbage Collector decides to reclaim the `MappedByteBuffer` object.
Even if you close the `FileChannel`, the buffer object remains on the heap. If you try to call `Files.delete()` before the GC runs, Windows will throw an `AccessDeniedException`.
The only immediate workaround is to use an unsafe reflection hack to invoke the internal `sun.misc.Cleaner` on the buffer, though this is heavily restricted in modern Java modules.

## Q5: What is a `SIGBUS` error, and how is it triggered by memory-mapped files?
**Answer:**
A `SIGBUS` (Bus Error) is a severe OS-level signal indicating that a process attempted to access memory that the hardware cannot physically address.
In the context of mapped files: If your Java application maps a file, and then *another* process on the OS deletes or truncates that physical file, your Java virtual memory map becomes invalid. When your Java thread attempts to read from the `MappedByteBuffer`, the OS cannot fetch the data from disk. The OS sends a `SIGBUS` signal to the JVM, which instantly crashes the entire Java process without throwing a catchable Java `Exception` or producing a standard stack trace.