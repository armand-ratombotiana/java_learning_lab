# Lab 17: NIO (New I/O)

## 📋 Lab Header

| Aspect | Details |
|--------|---------|
| **Difficulty** | Intermediate-Advanced |
| **Estimated Time** | 5 hours |
| **Real-World Context** | Building a high-performance file server |
| **Prerequisites** | Lab 16: File I/O |
| **Learning Type** | Hands-on, Project-based |

## 🎯 Learning Objectives

By completing this lab, you will be able to:

1. **Understand channels and buffers** in NIO
2. **Use selectors** for non-blocking I/O
3. **Implement non-blocking operations** efficiently
4. **Work with memory-mapped files** for performance
5. **Handle file locking** properly
6. **Build a high-performance file server** with NIO

## 📚 Prerequisites

- Lab 16: File I/O completed
- Understanding of file operations
- Knowledge of networking basics
- Familiarity with threading

## 🧠 Concept Theory

### 1. Channels and Buffers

Core NIO concepts:

```java
import java.nio.*;
import java.nio.channels.*;
import java.nio.file.*;

// FileChannel - read/write files
try (RandomAccessFile file = new RandomAccessFile("file.txt", "rw");
     FileChannel channel = file.getChannel()) {
    
    // Create buffer
    ByteBuffer buffer = ByteBuffer.allocate(1024);
    
    // Read from channel
    int bytesRead = channel.read(buffer);
    
    // Flip buffer for reading
    buffer.flip();
    
    // Read data
    while (buffer.hasRemaining()) {
        System.out.print((char) buffer.get());
    }
    
    // Clear buffer for writing
    buffer.clear();
    buffer.put("Hello, NIO!".getBytes());
    
    // Flip for writing
    buffer.flip();
    
    // Write to channel
    channel.write(buffer);
}

// Buffer operations
ByteBuffer buffer = ByteBuffer.allocate(1024);
buffer.put("Hello".getBytes());
System.out.println("Position: " + buffer.position());  // 5
System.out.println("Limit: " + buffer.limit());        // 1024
System.out.println("Capacity: " + buffer.capacity());  // 1024

buffer.flip();
System.out.println("Position: " + buffer.position());  // 0
System.out.println("Limit: " + buffer.limit());        // 5

buffer.rewind();  // Reset position to 0
buffer.clear();   // Reset position and limit
```

### 2. Selectors

Non-blocking I/O with selectors:

```java
import java.nio.channels.*;
import java.util.*;

// Create selector
Selector selector = Selector.open();

// Create server socket channel
ServerSocketChannel serverChannel = ServerSocketChannel.open();
serverChannel.bind(new InetSocketAddress(8080));
serverChannel.configureBlocking(false);

// Register with selector
SelectionKey key = serverChannel.register(selector, SelectionKey.OP_ACCEPT);

// Select loop
while (true) {
    // Wait for events
    int readyChannels = selector.select();
    
    if (readyChannels == 0) continue;
    
    // Get ready keys
    Set<SelectionKey> selectedKeys = selector.selectedKeys();
    Iterator<SelectionKey> iterator = selectedKeys.iterator();
    
    while (iterator.hasNext()) {
        SelectionKey selectedKey = iterator.next();
        
        if (selectedKey.isAcceptable()) {
            // Accept new connection
            ServerSocketChannel server = (ServerSocketChannel) selectedKey.channel();
            SocketChannel client = server.accept();
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
        } else if (selectedKey.isReadable()) {
            // Read from client
            SocketChannel client = (SocketChannel) selectedKey.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int bytesRead = client.read(buffer);
            if (bytesRead > 0) {
                buffer.flip();
                // Process data
            }
        } else if (selectedKey.isWritable()) {
            // Write to client
            SocketChannel client = (SocketChannel) selectedKey.channel();
            ByteBuffer buffer = ByteBuffer.wrap("Response".getBytes());
            client.write(buffer);
        }
        
        iterator.remove();
    }
}
```

### 3. Non-Blocking I/O

Non-blocking file operations:

```java
// Non-blocking read
FileChannel channel = new RandomAccessFile("file.txt", "r").getChannel();
ByteBuffer buffer = ByteBuffer.allocate(1024);

// Read doesn't block
int bytesRead = channel.read(buffer);
if (bytesRead > 0) {
    buffer.flip();
    // Process data
}

// Non-blocking write
FileChannel writeChannel = new RandomAccessFile("output.txt", "rw").getChannel();
ByteBuffer writeBuffer = ByteBuffer.wrap("Data".getBytes());

int bytesWritten = writeChannel.write(writeBuffer);
if (bytesWritten > 0) {
    System.out.println("Wrote " + bytesWritten + " bytes");
}

// Scatter/Gather
ByteBuffer[] buffers = new ByteBuffer[2];
buffers[0] = ByteBuffer.allocate(1024);
buffers[1] = ByteBuffer.allocate(1024);

// Scatter read
long bytesRead = channel.read(buffers);

// Gather write
long bytesWritten = channel.write(buffers);
```

### 4. Memory-Mapped Files

High-performance file access:

```java
import java.nio.MappedByteBuffer;

// Map file to memory
RandomAccessFile file = new RandomAccessFile("largefile.bin", "rw");
FileChannel channel = file.getChannel();

// Map entire file
MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, channel.size());

// Access like array
buffer.put(0, (byte) 'A');
byte value = buffer.get(0);

// Iterate through buffer
while (buffer.hasRemaining()) {
    byte b = buffer.get();
    System.out.print((char) b);
}

// Force changes to disk
buffer.force();

// Performance comparison
long startTime = System.nanoTime();
// Memory-mapped read
long mmapTime = System.nanoTime() - startTime;

startTime = System.nanoTime();
// Traditional read
long traditionalTime = System.nanoTime() - startTime;

System.out.println("Memory-mapped: " + mmapTime);
System.out.println("Traditional: " + traditionalTime);
System.out.println("Speedup: " + (traditionalTime / (double) mmapTime) + "x");
```

### 5. File Locking

Coordinating file access:

```java
// Exclusive lock
FileChannel channel = new RandomAccessFile("file.txt", "rw").getChannel();
FileLock lock = channel.lock();  // Blocks until lock acquired

try {
    // Exclusive access
    channel.write(ByteBuffer.wrap("Data".getBytes()));
} finally {
    lock.release();
}

// Non-blocking lock
FileLock lock = channel.tryLock();
if (lock != null) {
    try {
        // Exclusive access
    } finally {
        lock.release();
    }
} else {
    System.out.println("Could not acquire lock");
}

// Shared lock
FileLock lock = channel.lock(0, channel.size(), true);  // true = shared

try {
    // Shared access
    ByteBuffer buffer = ByteBuffer.allocate(1024);
    channel.read(buffer);
} finally {
    lock.release();
}

// Lock with range
FileLock lock = channel.lock(0, 1024, false);  // Lock first 1024 bytes
```

### 6. Performance Comparison

Comparing I/O approaches:

```java
// Traditional I/O
long startTime = System.nanoTime();
try (BufferedInputStream bis = new BufferedInputStream(
        new FileInputStream("largefile.bin"))) {
    byte[] buffer = new byte[8192];
    int bytesRead;
    while ((bytesRead = bis.read(buffer)) != -1) {
        // Process data
    }
}
long traditionalTime = System.nanoTime() - startTime;

// NIO
startTime = System.nanoTime();
try (RandomAccessFile file = new RandomAccessFile("largefile.bin", "r");
     FileChannel channel = file.getChannel()) {
    ByteBuffer buffer = ByteBuffer.allocate(8192);
    while (channel.read(buffer) > 0) {
        buffer.flip();
        // Process data
        buffer.clear();
    }
}
long nioTime = System.nanoTime() - startTime;

// Memory-mapped
startTime = System.nanoTime();
try (RandomAccessFile file = new RandomAccessFile("largefile.bin", "r");
     FileChannel channel = file.getChannel()) {
    MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
    while (buffer.hasRemaining()) {
        byte b = buffer.get();
        // Process data
    }
}
long mmapTime = System.nanoTime() - startTime;

System.out.println("Traditional: " + traditionalTime);
System.out.println("NIO: " + nioTime);
System.out.println("Memory-mapped: " + mmapTime);
```

### 7. Asynchronous I/O

Async file operations:

```java
import java.nio.channels.*;
import java.util.concurrent.*;

// Async file channel
AsynchronousFileChannel channel = AsynchronousFileChannel.open(
    Paths.get("file.txt"), StandardOpenOption.READ);

// Read with callback
ByteBuffer buffer = ByteBuffer.allocate(1024);
channel.read(buffer, 0, null, new CompletionHandler<Integer, Void>() {
    @Override
    public void completed(Integer bytesRead, Void attachment) {
        System.out.println("Read " + bytesRead + " bytes");
        buffer.flip();
        // Process data
    }
    
    @Override
    public void failed(Throwable exc, Void attachment) {
        System.err.println("Read failed: " + exc.getMessage());
    }
});

// Read with Future
Future<Integer> future = channel.read(buffer, 0);
try {
    Integer bytesRead = future.get();
    System.out.println("Read " + bytesRead + " bytes");
} catch (ExecutionException e) {
    System.err.println("Read failed: " + e.getMessage());
}
```

### 8. Best Practices

NIO programming guidelines:

```java
// ✅ Always flip buffer after writing
ByteBuffer buffer = ByteBuffer.allocate(1024);
buffer.put("Data".getBytes());
buffer.flip();  // Important!

// ✅ Clear buffer before reading
buffer.clear();
channel.read(buffer);
buffer.flip();

// ✅ Use try-with-resources
try (FileChannel channel = new RandomAccessFile("file.txt", "r").getChannel()) {
    // Use channel
}

// ✅ Handle partial reads/writes
ByteBuffer buffer = ByteBuffer.allocate(1024);
int bytesRead = channel.read(buffer);
while (bytesRead > 0) {
    buffer.flip();
    // Process data
    buffer.clear();
    bytesRead = channel.read(buffer);
}

// ✅ Use appropriate buffer size
ByteBuffer buffer = ByteBuffer.allocate(8192);  // 8KB is typical

// ✅ Release locks properly
FileLock lock = channel.lock();
try {
    // Use lock
} finally {
    lock.release();
}
```

### 9. Selector Patterns

Common selector patterns:

```java
// Pattern 1: Accept and read
if (key.isAcceptable()) {
    ServerSocketChannel server = (ServerSocketChannel) key.channel();
    SocketChannel client = server.accept();
    client.configureBlocking(false);
    client.register(selector, SelectionKey.OP_READ);
}

// Pattern 2: Read and respond
if (key.isReadable()) {
    SocketChannel client = (SocketChannel) key.channel();
    ByteBuffer buffer = ByteBuffer.allocate(1024);
    int bytesRead = client.read(buffer);
    if (bytesRead > 0) {
        buffer.flip();
        // Process request
        client.register(selector, SelectionKey.OP_WRITE);
    }
}

// Pattern 3: Write response
if (key.isWritable()) {
    SocketChannel client = (SocketChannel) key.channel();
    ByteBuffer response = ByteBuffer.wrap("Response".getBytes());
    client.write(response);
    client.register(selector, SelectionKey.OP_READ);
}
```

### 10. Use Cases

When to use NIO:

```java
// ✅ Use NIO for:
// - High-throughput servers
// - Many concurrent connections
// - Non-blocking operations
// - Memory-mapped files
// - Large file processing

// ❌ Use traditional I/O for:
// - Simple file operations
// - Small files
// - Blocking operations
// - Simple applications

// Example: High-performance server
Selector selector = Selector.open();
ServerSocketChannel serverChannel = ServerSocketChannel.open();
serverChannel.bind(new InetSocketAddress(8080));
serverChannel.configureBlocking(false);
serverChannel.register(selector, SelectionKey.OP_ACCEPT);

while (true) {
    selector.select();
    // Handle events
}
```

---

## 💻 Step-by-Step Coding Tasks

### Task 1: Use Channels and Buffers

**Objective**: Implement NIO file operations

**Acceptance Criteria**:
- [ ] FileChannel usage
- [ ] ByteBuffer operations
- [ ] Read/write implemented
- [ ] Buffer flipping correct
- [ ] Code compiles without errors

**Instructions**:
1. Create FileChannel
2. Create ByteBuffer
3. Implement read operation
4. Implement write operation
5. Test with sample files

### Task 2: Implement Selector

**Objective**: Use selector for non-blocking I/O

**Acceptance Criteria**:
- [ ] Selector created
- [ ] Channels registered
- [ ] Events handled
- [ ] Non-blocking works
- [ ] Code compiles without errors

**Instructions**:
1. Create selector
2. Register channels
3. Implement event loop
4. Handle different events
5. Test with multiple clients

### Task 3: Use Memory-Mapped Files

**Objective**: Implement high-performance file access

**Acceptance Criteria**:
- [ ] Memory mapping
- [ ] File access
- [ ] Performance tested
- [ ] Locks used
- [ ] Error handling

**Instructions**:
1. Map file to memory
2. Access like array
3. Measure performance
4. Compare with traditional I/O
5. Test with large files

---

## 🎨 Mini-Project: High-Performance File Server

### Project Overview

**Description**: Create a high-performance file server using NIO.

**Real-World Application**: Web servers, file servers, streaming services.

**Learning Value**: Master NIO and non-blocking I/O patterns.

### Project Structure

```
high-performance-file-server/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/learning/
│   │           ├── FileServer.java
│   │           ├── ClientHandler.java
│   │           ├── ServerStats.java
│   │           └── Main.java
│   └── test/
│       └── java/
│           └── com/learning/
│               └── FileServerTest.java
├── pom.xml
└── README.md
```

### Implementation Guide

#### Step 1: Create FileServer Class

```java
package com.learning;

import java.io.IOException;
import java.nio.channels.*;
import java.nio.file.Paths;
import java.net.InetSocketAddress;
import java.util.*;

/**
 * High-performance file server using NIO.
 */
public class FileServer {
    private ServerSocketChannel serverChannel;
    private Selector selector;
    private ServerStats stats;
    
    /**
     * Constructor for FileServer.
     */
    public FileServer(int port) throws IOException {
        this.stats = new ServerStats();
        this.serverChannel = ServerSocketChannel.open();
        this.serverChannel.bind(new InetSocketAddress(port));
        this.serverChannel.configureBlocking(false);
        this.selector = Selector.open();
        this.serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    }
    
    /**
     * Start server.
     */
    public void start() throws IOException {
        System.out.println("Server started on port 8080");
        
        while (true) {
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();
            
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                
                if (key.isAcceptable()) {
                    handleAccept(key);
                } else if (key.isReadable()) {
                    handleRead(key);
                }
                
                iterator.remove();
            }
        }
    }
    
    /**
     * Handle accept.
     */
    private void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        SocketChannel client = server.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
        stats.recordConnection();
    }
    
    /**
     * Handle read.
     */
    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ClientHandler handler = new ClientHandler(client, stats);
        handler.handle();
    }
    
    /**
     * Get statistics.
     */
    public ServerStats getStats() {
        return stats;
    }
    
    /**
     * Close server.
     */
    public void close() throws IOException {
        serverChannel.close();
        selector.close();
    }
}
```

#### Step 2: Create ClientHandler Class

```java
package com.learning;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Handles client requests.
 */
public class ClientHandler {
    private SocketChannel client;
    private ServerStats stats;
    
    /**
     * Constructor for ClientHandler.
     */
    public ClientHandler(SocketChannel client, ServerStats stats) {
        this.client = client;
        this.stats = stats;
    }
    
    /**
     * Handle client request.
     */
    public void handle() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int bytesRead = client.read(buffer);
        
        if (bytesRead > 0) {
            buffer.flip();
            String request = new String(buffer.array(), 0, bytesRead);
            String response = processRequest(request);
            
            ByteBuffer responseBuffer = ByteBuffer.wrap(response.getBytes());
            client.write(responseBuffer);
            stats.recordRequest();
        } else if (bytesRead < 0) {
            client.close();
        }
    }
    
    /**
     * Process request.
     */
    private String processRequest(String request) {
        try {
            String filename = request.trim();
            byte[] content = Files.readAllBytes(Paths.get(filename));
            stats.recordFileRead();
            return new String(content);
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }
}
```

#### Step 3: Create ServerStats Class

```java
package com.learning;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Tracks server statistics.
 */
public class ServerStats {
    private AtomicLong connections = new AtomicLong(0);
    private AtomicLong requests = new AtomicLong(0);
    private AtomicLong filesRead = new AtomicLong(0);
    
    /**
     * Record connection.
     */
    public void recordConnection() {
        connections.incrementAndGet();
    }
    
    /**
     * Record request.
     */
    public void recordRequest() {
        requests.incrementAndGet();
    }
    
    /**
     * Record file read.
     */
    public void recordFileRead() {
        filesRead.incrementAndGet();
    }
    
    /**
     * Display statistics.
     */
    public void displayStats() {
        System.out.println("\n========== SERVER STATS ==========");
        System.out.println("Connections: " + connections.get());
        System.out.println("Requests: " + requests.get());
        System.out.println("Files Read: " + filesRead.get());
        System.out.println("==================================\n");
    }
}
```

#### Step 4: Create Main Class

```java
package com.learning;

import java.io.IOException;

/**
 * Main entry point for High-Performance File Server.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        FileServer server = new FileServer(8080);
        
        // Start server in separate thread
        Thread serverThread = new Thread(() -> {
            try {
                server.start();
            } catch (IOException e) {
                System.err.println("Server error: " + e.getMessage());
            }
        });
        
        serverThread.setDaemon(true);
        serverThread.start();
        
        // Keep main thread alive
        try {
            Thread.sleep(60000);  // Run for 60 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        server.close();
        server.getStats().displayStats();
    }
}
```

#### Step 5: Create Unit Tests

```java
package com.learning;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FileServer.
 */
public class FileServerTest {
    
    @Test
    void testServerCreation() throws Exception {
        FileServer server = new FileServer(8081);
        assertNotNull(server);
        server.close();
    }
    
    @Test
    void testStatsTracking() throws Exception {
        FileServer server = new FileServer(8082);
        ServerStats stats = server.getStats();
        
        stats.recordConnection();
        stats.recordRequest();
        stats.recordFileRead();
        
        assertNotNull(stats);
        server.close();
    }
}
```

### Running the Project

```bash
# Compile
mvn clean compile

# Run tests
mvn test

# Run the application
mvn exec:java -Dexec.mainClass="com.learning.Main"
```

---

## 📝 Exercises

### Exercise 1: Memory-Mapped File Processor

**Objective**: Process large files with memory mapping

**Task Description**:
Create system to process large files efficiently

**Acceptance Criteria**:
- [ ] Memory mapping
- [ ] Large file handling
- [ ] Performance optimization
- [ ] Error handling
- [ ] Tests pass

### Exercise 2: Async File Operations

**Objective**: Implement asynchronous file operations

**Task Description**:
Create async file reader/writer system

**Acceptance Criteria**:
- [ ] Async operations
- [ ] Callbacks
- [ ] Futures
- [ ] Error handling
- [ ] Tests pass

### Exercise 3: Multi-Client Server

**Objective**: Handle multiple clients efficiently

**Task Description**:
Create server handling many concurrent clients

**Acceptance Criteria**:
- [ ] Multiple clients
- [ ] Non-blocking I/O
- [ ] Efficient handling
- [ ] Monitoring
- [ ] Tests pass

---

## 🧪 Quiz

### Question 1: What is a FileChannel?

A) A way to read files  
B) A way to write files  
C) A bidirectional channel for file I/O  
D) A type of buffer  

**Answer**: C) A bidirectional channel for file I/O

### Question 2: What does buffer.flip() do?

A) Clears the buffer  
B) Prepares buffer for reading  
C) Prepares buffer for writing  
D) Closes the buffer  

**Answer**: B) Prepares buffer for reading

### Question 3: What is a Selector?

A) A way to select files  
B) A way to select channels for non-blocking I/O  
C) A type of buffer  
D) A type of lock  

**Answer**: B) A way to select channels for non-blocking I/O

### Question 4: What are memory-mapped files?

A) Files stored in memory  
B) Files mapped to memory for fast access  
C) Files that are compressed  
D) Files that are encrypted  

**Answer**: B) Files mapped to memory for fast access

### Question 5: When should you use NIO?

A) Always  
B) For high-throughput servers  
C) For simple file operations  
D) Never  

**Answer**: B) For high-throughput servers

---

## 🚀 Advanced Challenge

### Challenge: Complete NIO Framework

**Difficulty**: Advanced

**Objective**: Build comprehensive NIO framework

**Requirements**:
- [ ] Multiple channel types
- [ ] Selector management
- [ ] Async operations
- [ ] Memory mapping
- [ ] Performance optimization
- [ ] Monitoring

---

## 🏆 Best Practices

### NIO Programming

1. **Always Flip Buffers**
   - After writing, flip for reading
   - After reading, clear for writing
   - Prevents data corruption

2. **Use Appropriate Buffer Sizes**
   - 8KB typical for I/O
   - Larger for bulk operations
   - Smaller for interactive

3. **Handle Partial Operations**
   - Reads may return less than requested
   - Writes may write less than provided
   - Loop until complete

---

## 🔗 Next Steps

**Next Lab**: [Lab 18: Serialization](../18-serialization/README.md)

---

## ✅ Completion Checklist

- [ ] Completed all coding tasks
- [ ] Built file server
- [ ] Solved all exercises
- [ ] Passed quiz (80%+)
- [ ] Attempted advanced challenge
- [ ] Reviewed best practices

---

**Congratulations on completing Lab 17! 🎉**

You've mastered NIO and non-blocking I/O. Ready for serialization? Move on to [Lab 18: Serialization](../18-serialization/README.md).