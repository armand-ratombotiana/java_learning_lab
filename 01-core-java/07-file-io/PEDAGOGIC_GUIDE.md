# 🎓 Pedagogic Guide: File I/O & NIO

<div align="center">

![Module](https://img.shields.io/badge/Module-07-blue?style=for-the-badge)
![Difficulty](https://img.shields.io/badge/Difficulty-Medium-yellow?style=for-the-badge)
![Importance](https://img.shields.io/badge/Importance-High-orange?style=for-the-badge)

**Master file I/O and NIO with deep conceptual understanding**

</div>

---

## 📚 Table of Contents

1. [Learning Philosophy](#learning-philosophy)
2. [Conceptual Foundation](#conceptual-foundation)
3. [Progressive Learning Path](#progressive-learning-path)
4. [Deep Dive Concepts](#deep-dive-concepts)
5. [Common Misconceptions](#common-misconceptions)
6. [Real-World Applications](#real-world-applications)
7. [Interview Preparation](#interview-preparation)

---

## 🎯 Learning Philosophy

### Why File I/O Matters

File I/O is fundamental to real-world applications:

1. **Data Persistence**: Save data to disk
2. **Configuration**: Read application settings
3. **Logging**: Write application logs
4. **Performance**: Efficient I/O is critical
5. **Scalability**: NIO enables high-performance servers

### Our Pedagogic Approach

We teach File I/O through **three generations**:

```
Generation 1: Traditional I/O (InputStream/OutputStream)
    ↓ (understand limitations)
Generation 2: NIO (Channels, Buffers, Selectors)
    ↓ (understand modern approach)
Generation 3: NIO.2 (Path, Files, FileSystem)
    ↓ (understand modern Java)
```

---

## 🧠 Conceptual Foundation

### Core Concept 1: I/O Streams

#### What is a Stream?

A stream is a **sequence of bytes** flowing from source to destination:

```
Source → Stream → Destination

File → FileInputStream → Program
Program → FileOutputStream → File
```

#### Two Types of Streams

**Input Streams** (reading data):
```
File → InputStream → Program
```

**Output Streams** (writing data):
```
Program → OutputStream → File
```

#### Stream Hierarchy
```
InputStream (abstract)
├── FileInputStream (read from file)
├── ByteArrayInputStream (read from byte array)
├── PipedInputStream (read from pipe)
└── FilterInputStream (wrapper)
    ├── BufferedInputStream (buffering)
    └── DataInputStream (read primitives)

OutputStream (abstract)
├── FileOutputStream (write to file)
├── ByteArrayOutputStream (write to byte array)
├── PipedOutputStream (write to pipe)
└── FilterOutputStream (wrapper)
    ├── BufferedOutputStream (buffering)
    └── DataOutputStream (write primitives)
```

#### Code Example
```java
// Reading from file
try (FileInputStream fis = new FileInputStream("data.txt")) {
    int byte1 = fis.read();  // Read one byte
    byte[] buffer = new byte[1024];
    int bytesRead = fis.read(buffer);  // Read multiple bytes
} catch (IOException e) {
    // Handle error
}

// Writing to file
try (FileOutputStream fos = new FileOutputStream("output.txt")) {
    fos.write(65);  // Write one byte (ASCII 'A')
    fos.write("Hello".getBytes());  // Write multiple bytes
} catch (IOException e) {
    // Handle error
}
```

---

### Core Concept 2: Buffering

#### The Problem Without Buffering
```
Without Buffering:
Program → [1 byte] → Disk (slow!)
Program → [1 byte] → Disk (slow!)
Program → [1 byte] → Disk (slow!)
...
Total: 1000 disk operations for 1000 bytes
```

#### The Solution With Buffering
```
With Buffering:
Program → [Buffer: 1024 bytes] → Disk (fast!)
Program → [Buffer: 1024 bytes] → Disk (fast!)
...
Total: 1 disk operation for 1024 bytes
```

#### Code Example
```java
// Without buffering (slow)
try (FileInputStream fis = new FileInputStream("large.txt")) {
    int byte1;
    while ((byte1 = fis.read()) != -1) {
        // Process one byte at a time
        // Slow! Many disk operations
    }
}

// With buffering (fast)
try (BufferedInputStream bis = new BufferedInputStream(
        new FileInputStream("large.txt"))) {
    int byte1;
    while ((byte1 = bis.read()) != -1) {
        // Process one byte at a time
        // Fast! Buffered internally
    }
}

// Even better: Read into buffer
try (FileInputStream fis = new FileInputStream("large.txt")) {
    byte[] buffer = new byte[8192];
    int bytesRead;
    while ((bytesRead = fis.read(buffer)) != -1) {
        // Process buffer
        // Very fast! Explicit buffering
    }
}
```

---

### Core Concept 3: Character Encoding

#### The Problem
```
File contains bytes: 48 65 6C 6C 6F
How to interpret?
- As ASCII: "Hello"
- As UTF-8: "Hello"
- As UTF-16: "䠀攀氀氀漀" (wrong!)
```

#### The Solution: Specify Encoding
```java
// Specify encoding explicitly
try (FileInputStream fis = new FileInputStream("file.txt");
     InputStreamReader reader = new InputStreamReader(fis, "UTF-8")) {
    int char1 = reader.read();  // Reads character, not byte
}

// Or use modern approach
try (BufferedReader reader = Files.newBufferedReader(
        Paths.get("file.txt"), StandardCharsets.UTF_8)) {
    String line;
    while ((line = reader.readLine()) != null) {
        // Process line
    }
}
```

#### Common Encodings
```
ASCII: 7-bit, English only
UTF-8: Variable-length, universal (recommended)
UTF-16: Fixed 2-byte, less common
ISO-8859-1: 8-bit, Western European
```

---

### Core Concept 4: NIO Fundamentals

#### The Problem with Traditional I/O
```
Traditional I/O (Blocking):
Thread 1: Read from file (blocks until data available)
Thread 2: Read from network (blocks until data available)
Thread 3: Read from database (blocks until data available)
...
Result: Need many threads, high memory usage
```

#### The Solution: NIO (Non-Blocking)
```
NIO (Non-Blocking):
Selector: Check which channels have data
Channel 1: Has data? Read it
Channel 2: No data? Skip it
Channel 3: Has data? Read it
...
Result: One thread handles many channels
```

#### Key Components

**Channels**: Bidirectional connections
```
Traditional I/O: One-way (InputStream or OutputStream)
NIO: Two-way (Channel can read and write)
```

**Buffers**: Containers for data
```
Traditional I/O: Read/write one byte at a time
NIO: Read/write many bytes at once
```

**Selectors**: Monitor multiple channels
```
Traditional I/O: One thread per connection
NIO: One thread monitors many connections
```

#### Visual Comparison
```
Traditional I/O (Blocking):
┌─────────────┐
│ Thread 1    │ ← Blocked reading from File
├─────────────┤
│ Thread 2    │ ← Blocked reading from Network
├─────────────┤
│ Thread 3    │ ← Blocked reading from Database
├─────────────┤
│ Thread 4    │ ← Blocked reading from Socket
└─────────────┘
High memory usage!

NIO (Non-Blocking):
┌─────────────────────────────────────┐
│ Thread 1 (Selector)                 │
│ ├─ File: Has data? Read it          │
│ ├─ Network: No data? Skip it        │
│ ├─ Database: Has data? Read it      │
│ └─ Socket: Has data? Read it        │
└─────────────────────────────────────┘
Low memory usage!
```

---

### Core Concept 5: NIO.2 (Modern Approach)

#### The Problem with NIO
```
NIO is powerful but complex:
- Channels, Buffers, Selectors
- Manual buffer management
- Difficult to learn
```

#### The Solution: NIO.2
```
NIO.2 simplifies common operations:
- Path (replaces File)
- Files (utility methods)
- FileSystem (abstraction)
```

#### Code Comparison

**Traditional I/O:**
```java
File file = new File("data.txt");
if (file.exists()) {
    long size = file.length();
    boolean readable = file.canRead();
}
```

**NIO.2:**
```java
Path path = Paths.get("data.txt");
if (Files.exists(path)) {
    long size = Files.size(path);
    boolean readable = Files.isReadable(path);
}
```

#### Advantages of NIO.2
```
1. More intuitive API
2. Better error handling
3. Atomic operations
4. File attributes
5. Directory traversal
```

---

## 📈 Progressive Learning Path

### Phase 1: Traditional I/O (Days 1-2)

#### Day 1: Streams and Readers
**Concepts:**
- InputStream/OutputStream
- Reader/Writer
- Buffering
- Character encoding

**Exercises:**
```java
// Exercise 1: Read file byte by byte
try (FileInputStream fis = new FileInputStream("file.txt")) {
    int byte1;
    while ((byte1 = fis.read()) != -1) {
        System.out.print((char) byte1);
    }
}

// Exercise 2: Read file with buffering
try (BufferedInputStream bis = new BufferedInputStream(
        new FileInputStream("file.txt"))) {
    byte[] buffer = new byte[1024];
    int bytesRead;
    while ((bytesRead = bis.read(buffer)) != -1) {
        System.out.write(buffer, 0, bytesRead);
    }
}

// Exercise 3: Read text file with encoding
try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(
            new FileInputStream("file.txt"), 
            StandardCharsets.UTF_8))) {
    String line;
    while ((line = reader.readLine()) != null) {
        System.out.println(line);
    }
}

// Exercise 4: Write to file
try (FileOutputStream fos = new FileOutputStream("output.txt");
     PrintWriter writer = new PrintWriter(fos)) {
    writer.println("Hello, World!");
    writer.println("Line 2");
}
```

#### Day 2: File Operations
**Concepts:**
- File class
- File operations
- Directory traversal
- File filters

**Exercises:**
```java
// Exercise 1: File operations
File file = new File("data.txt");
System.out.println("Exists: " + file.exists());
System.out.println("Size: " + file.length());
System.out.println("Readable: " + file.canRead());
System.out.println("Writable: " + file.canWrite());

// Exercise 2: Directory listing
File dir = new File(".");
File[] files = dir.listFiles();
for (File f : files) {
    System.out.println(f.getName());
}

// Exercise 3: Recursive directory traversal
void listFiles(File dir) {
    File[] files = dir.listFiles();
    for (File f : files) {
        if (f.isDirectory()) {
            listFiles(f);  // Recursive
        } else {
            System.out.println(f.getAbsolutePath());
        }
    }
}

// Exercise 4: File filter
File[] txtFiles = dir.listFiles(f -> f.getName().endsWith(".txt"));
```

---

### Phase 2: NIO (Days 3-4)

#### Day 3: Channels and Buffers
**Concepts:**
- FileChannel
- ByteBuffer
- Buffer operations
- Channel operations

**Exercises:**
```java
// Exercise 1: Read with FileChannel
try (RandomAccessFile file = new RandomAccessFile("data.txt", "r");
     FileChannel channel = file.getChannel()) {
    ByteBuffer buffer = ByteBuffer.allocate(1024);
    int bytesRead = channel.read(buffer);
    buffer.flip();  // Switch to read mode
    while (buffer.hasRemaining()) {
        System.out.print((char) buffer.get());
    }
}

// Exercise 2: Write with FileChannel
try (RandomAccessFile file = new RandomAccessFile("output.txt", "rw");
     FileChannel channel = file.getChannel()) {
    ByteBuffer buffer = ByteBuffer.allocate(1024);
    buffer.put("Hello, NIO!".getBytes());
    buffer.flip();  // Switch to write mode
    channel.write(buffer);
}

// Exercise 3: Copy file with channels
try (FileInputStream fis = new FileInputStream("input.txt");
     FileOutputStream fos = new FileOutputStream("output.txt");
     FileChannel inChannel = fis.getChannel();
     FileChannel outChannel = fos.getChannel()) {
    ByteBuffer buffer = ByteBuffer.allocate(8192);
    while (inChannel.read(buffer) > 0) {
        buffer.flip();
        outChannel.write(buffer);
        buffer.clear();
    }
}

// Exercise 4: Buffer operations
ByteBuffer buffer = ByteBuffer.allocate(256);
buffer.put("Hello".getBytes());
System.out.println("Position: " + buffer.position());  // 5
System.out.println("Limit: " + buffer.limit());  // 256
buffer.flip();  // position = 0, limit = 5
System.out.println("Remaining: " + buffer.remaining());  // 5
```

#### Day 4: Selectors and Non-Blocking I/O
**Concepts:**
- Selector
- SelectionKey
- Non-blocking channels
- Event-driven I/O

**Exercises:**
```java
// Exercise 1: Non-blocking server
ServerSocketChannel serverChannel = ServerSocketChannel.open();
serverChannel.configureBlocking(false);
serverChannel.bind(new InetSocketAddress(8080));

Selector selector = Selector.open();
serverChannel.register(selector, SelectionKey.OP_ACCEPT);

while (true) {
    selector.select();  // Wait for events
    Set<SelectionKey> keys = selector.selectedKeys();
    for (SelectionKey key : keys) {
        if (key.isAcceptable()) {
            // Accept new connection
            SocketChannel client = serverChannel.accept();
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
        } else if (key.isReadable()) {
            // Read from client
            SocketChannel client = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            client.read(buffer);
        }
    }
    keys.clear();
}

// Exercise 2: Buffer states
ByteBuffer buffer = ByteBuffer.allocate(10);
// Initial: position=0, limit=10, capacity=10

buffer.put("Hello".getBytes());
// After put: position=5, limit=10, capacity=10

buffer.flip();
// After flip: position=0, limit=5, capacity=10

buffer.get();
// After get: position=1, limit=5, capacity=10

buffer.rewind();
// After rewind: position=0, limit=5, capacity=10
```

---

### Phase 3: NIO.2 (Days 5-6)

#### Day 5: Path and Files API
**Concepts:**
- Path interface
- Files utility class
- File attributes
- Path operations

**Exercises:**
```java
// Exercise 1: Path operations
Path path = Paths.get("data.txt");
System.out.println("Filename: " + path.getFileName());
System.out.println("Parent: " + path.getParent());
System.out.println("Root: " + path.getRoot());
System.out.println("Absolute: " + path.toAbsolutePath());

// Exercise 2: File operations
Path path = Paths.get("data.txt");
System.out.println("Exists: " + Files.exists(path));
System.out.println("Size: " + Files.size(path));
System.out.println("Readable: " + Files.isReadable(path));
System.out.println("Directory: " + Files.isDirectory(path));

// Exercise 3: Read file
List<String> lines = Files.readAllLines(
    Paths.get("data.txt"), 
    StandardCharsets.UTF_8);
for (String line : lines) {
    System.out.println(line);
}

// Exercise 4: Write file
Files.write(
    Paths.get("output.txt"),
    "Hello, NIO.2!".getBytes(),
    StandardOpenOption.CREATE,
    StandardOpenOption.WRITE);

// Exercise 5: Copy file
Files.copy(
    Paths.get("input.txt"),
    Paths.get("output.txt"),
    StandardCopyOption.REPLACE_EXISTING);
```

#### Day 6: Directory Operations and File Attributes
**Concepts:**
- Directory traversal
- File attributes
- File watching
- Glob patterns

**Exercises:**
```java
// Exercise 1: List directory
try (DirectoryStream<Path> stream = Files.newDirectoryStream(
        Paths.get("."))) {
    for (Path path : stream) {
        System.out.println(path.getFileName());
    }
}

// Exercise 2: Recursive directory walk
Files.walk(Paths.get("."))
    .filter(Files::isRegularFile)
    .forEach(System.out::println);

// Exercise 3: File attributes
Path path = Paths.get("data.txt");
BasicFileAttributes attrs = Files.readAttributes(
    path, BasicFileAttributes.class);
System.out.println("Size: " + attrs.size());
System.out.println("Created: " + attrs.creationTime());
System.out.println("Modified: " + attrs.lastModifiedTime());

// Exercise 4: Glob pattern
try (DirectoryStream<Path> stream = Files.newDirectoryStream(
        Paths.get("."), "*.txt")) {
    for (Path path : stream) {
        System.out.println(path.getFileName());
    }
}

// Exercise 5: File watching
WatchService watcher = FileSystems.getDefault().newWatchService();
Path dir = Paths.get(".");
dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);

WatchKey key = watcher.take();
for (WatchEvent<?> event : key.pollEvents()) {
    System.out.println("Event: " + event.kind());
}
```

---

## 🔍 Deep Dive Concepts

### Concept 1: Buffer States and Operations

#### Understanding Buffer States
```
ByteBuffer buffer = ByteBuffer.allocate(10);

Initial State:
┌─────────────────────────────┐
│ 0 1 2 3 4 5 6 7 8 9         │
│ ^                       ^   │
│ position=0          limit=10│
│                    capacity=10
└─────────────────────────────┘

After put("Hello"):
┌─────────────────────────────┐
│ H e l l o 0 0 0 0 0         │
│         ^               ^   │
│     position=5      limit=10│
└─────────────────────────────┘

After flip():
┌─────────────────────────────┐
│ H e l l o 0 0 0 0 0         │
│ ^           ^               │
│ position=0  limit=5         │
└─────────────────────────────┘

After get():
┌─────────────────────────────┐
│ H e l l o 0 0 0 0 0         │
│   ^         ^               │
│ position=1  limit=5         │
└─────────────────────────────┘
```

#### Key Methods
```java
buffer.put(byte)      // Write byte, position++
buffer.get()          // Read byte, position++
buffer.flip()         // position=0, limit=position
buffer.rewind()       // position=0
buffer.clear()        // position=0, limit=capacity
buffer.remaining()    // limit - position
buffer.hasRemaining() // remaining > 0
```

---

### Concept 2: Blocking vs Non-Blocking I/O

#### Blocking I/O
```
Thread: Read from file
    ↓ (blocks until data available)
File: Waiting for disk...
    ↓ (data arrives)
Thread: Continues with data
```

#### Non-Blocking I/O
```
Thread: Check if data available
    ↓ (returns immediately)
File: No data yet
    ↓ (thread continues)
Thread: Do other work
    ↓ (later)
Thread: Check again
    ↓ (data available now)
Thread: Read data
```

#### Performance Implications
```
Blocking I/O:
- Simple to understand
- One thread per connection
- High memory usage
- Good for few connections

Non-Blocking I/O:
- Complex to understand
- One thread for many connections
- Low memory usage
- Good for many connections
```

---

### Concept 3: Character Encoding

#### How Encoding Works
```
Text: "Hello"
    ↓ (encode with UTF-8)
Bytes: 48 65 6C 6C 6F
    ↓ (write to file)
File: [48 65 6C 6C 6F]
    ↓ (read from file)
Bytes: 48 65 6C 6C 6F
    ↓ (decode with UTF-8)
Text: "Hello"
```

#### Common Issues
```
Wrong Encoding:
File contains: 48 65 6C 6C 6F (UTF-8 for "Hello")
Decode as ASCII: "Hello" (correct by chance)
Decode as UTF-16: "䠀攀氀氀漀" (wrong!)

Solution: Always specify encoding explicitly
```

---

## ⚠️ Common Misconceptions

### Misconception 1: "Streams are Fast"
**Wrong!**
```java
// Slow: One byte at a time
try (FileInputStream fis = new FileInputStream("large.txt")) {
    int byte1;
    while ((byte1 = fis.read()) != -1) {
        // Process byte
    }
}
```

**Correct:**
```java
// Fast: Buffered
try (BufferedInputStream bis = new BufferedInputStream(
        new FileInputStream("large.txt"))) {
    byte[] buffer = new byte[8192];
    int bytesRead;
    while ((bytesRead = bis.read(buffer)) != -1) {
        // Process buffer
    }
}
```

### Misconception 2: "NIO is Always Better"
**Wrong!**
```java
// NIO is complex and not always faster
// For simple file operations, traditional I/O is fine
```

**Correct:**
```java
// Use NIO for:
// - High-performance servers
// - Many concurrent connections
// - Non-blocking operations

// Use traditional I/O for:
// - Simple file operations
// - Reading configuration files
// - Writing logs
```

### Misconception 3: "Encoding Doesn't Matter"
**Wrong!**
```java
// Without specifying encoding
try (FileInputStream fis = new FileInputStream("file.txt")) {
    // Uses platform default encoding
    // Might be wrong on different systems!
}
```

**Correct:**
```java
// Always specify encoding
try (FileInputStream fis = new FileInputStream("file.txt");
     InputStreamReader reader = new InputStreamReader(
         fis, StandardCharsets.UTF_8)) {
    // Explicit encoding
}
```

---

## 🌍 Real-World Applications

### Application 1: Log File Processing
```java
public class LogProcessor {
    public void processLogs(Path logFile) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(
                logFile, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                processLogLine(line);
            }
        }
    }
}
```

### Application 2: Configuration File Reader
```java
public class ConfigReader {
    public Properties loadConfig(Path configFile) throws IOException {
        Properties props = new Properties();
        try (InputStream is = Files.newInputStream(configFile)) {
            props.load(is);
        }
        return props;
    }
}
```

### Application 3: High-Performance Server
```java
public class NIOServer {
    public void start(int port) throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.bind(new InetSocketAddress(port));
        
        Selector selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        
        while (true) {
            selector.select();
            // Handle multiple connections with one thread
        }
    }
}
```

---

## 🎓 Interview Preparation

### Question 1: Traditional I/O vs NIO
**Answer:**

| Aspect | Traditional I/O | NIO |
|--------|-----------------|-----|
| **Blocking** | Yes | No |
| **Threads** | One per connection | One for many |
| **Complexity** | Simple | Complex |
| **Performance** | Good for few | Good for many |
| **Use Case** | Simple operations | High-performance servers |

### Question 2: How to Read Large File Efficiently?
**Answer:**
```java
// Use buffering
try (BufferedInputStream bis = new BufferedInputStream(
        new FileInputStream("large.txt"))) {
    byte[] buffer = new byte[8192];
    int bytesRead;
    while ((bytesRead = bis.read(buffer)) != -1) {
        // Process buffer
    }
}
```

### Question 3: What is Buffer Flip?
**Answer:**
```java
// flip() switches buffer from write mode to read mode
buffer.put("Hello".getBytes());  // Write mode
buffer.flip();  // Switch to read mode
// Now position=0, limit=5
while (buffer.hasRemaining()) {
    System.out.print((char) buffer.get());
}
```

---

## 📝 Summary

### Key Takeaways
1. **Streams** are sequences of bytes
2. **Buffering** improves performance
3. **Encoding** must be specified explicitly
4. **NIO** enables non-blocking I/O
5. **Channels** are bidirectional
6. **Buffers** manage data efficiently
7. **Selectors** monitor multiple channels
8. **NIO.2** simplifies common operations

### Learning Progression
```
Day 1-2: Traditional I/O
Day 3-4: NIO
Day 5-6: NIO.2
```

### Practice Strategy
1. **Understand streams** (read this guide)
2. **Write simple readers/writers** (traditional I/O)
3. **Learn buffering** (performance)
4. **Explore NIO** (advanced)
5. **Use NIO.2** (modern approach)

---

<div align="center">

**Ready to master File I/O?**

[Start with Traditional I/O →](#phase-1-traditional-io-days-1-2)

[Review Deep Dive Concepts →](#-deep-dive-concepts)

[Prepare for Interviews →](#-interview-preparation)

⭐ **File I/O is fundamental to real-world applications!**

</div>