# ⚠️ File I/O - Edge Cases & Pitfalls

## 1. Resource Leaks

### Pitfall: Not Closing Resources
```java
// ❌ WRONG: Resource leak
public void readFile(String filename) throws IOException {
    FileReader reader = new FileReader(filename);
    // If exception occurs, reader is never closed
    // File handle remains open
    // Eventually: "Too many open files" error
}

// ✅ CORRECT: Try-with-resources
public void readFile(String filename) throws IOException {
    try (FileReader reader = new FileReader(filename)) {
        // Automatically closed
    }
}

// ✅ CORRECT: Try-finally
public void readFile(String filename) throws IOException {
    FileReader reader = new FileReader(filename);
    try {
        // Read file
    } finally {
        reader.close();
    }
}

// Impact:
// - File handles exhausted
// - Cannot open new files
// - Application crashes
// - Hard to debug
```

---

### Pitfall: Closing in Wrong Order
```java
// ❌ WRONG: Closing in wrong order
public void copyFile(String source, String dest) throws IOException {
    FileInputStream in = new FileInputStream(source);
    FileOutputStream out = new FileOutputStream(dest);
    
    try {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
    } finally {
        in.close();
        out.close();  // If in.close() throws, out not closed
    }
}

// ✅ CORRECT: Try-with-resources handles order
public void copyFile(String source, String dest) throws IOException {
    try (FileInputStream in = new FileInputStream(source);
         FileOutputStream out = new FileOutputStream(dest)) {
        
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
    }
    // Closed in reverse order: out, then in
}

// Impact:
// - Resource leak if first close() throws
// - Difficult to manage multiple resources
```

---

## 2. Encoding Issues

### Pitfall: Wrong Encoding
```java
// ❌ WRONG: Platform default encoding
public void readFile(String filename) throws IOException {
    try (FileReader reader = new FileReader(filename)) {
        // Uses system default (might be wrong)
        // On Windows: CP1252
        // On Linux: UTF-8
        // File might be unreadable
    }
}

// ✅ CORRECT: Specify encoding
public void readFile(String filename) throws IOException {
    try (InputStreamReader reader = new InputStreamReader(
            new FileInputStream(filename), StandardCharsets.UTF_8)) {
        // Explicitly UTF-8
    }
}

// ✅ CORRECT: NIO with encoding
public String readFile(String filename) throws IOException {
    return Files.readString(Paths.get(filename), StandardCharsets.UTF_8);
}

// Example:
// File contains: "Café" (UTF-8: C3 A9)
// Read with ISO-8859-1: "CafÃ©" (wrong!)
// Read with UTF-8: "Café" (correct!)
```

---

### Pitfall: Encoding Mismatch
```java
// ❌ WRONG: Write UTF-8, read ISO-8859-1
public void writeFile(String filename) throws IOException {
    try (OutputStreamWriter writer = new OutputStreamWriter(
            new FileOutputStream(filename), StandardCharsets.UTF_8)) {
        writer.write("Café");
    }
}

public void readFile(String filename) throws IOException {
    try (InputStreamReader reader = new InputStreamReader(
            new FileInputStream(filename), StandardCharsets.ISO_8859_1)) {
        // Reads as "CafÃ©" (wrong!)
    }
}

// ✅ CORRECT: Use same encoding
public void writeFile(String filename) throws IOException {
    try (OutputStreamWriter writer = new OutputStreamWriter(
            new FileOutputStream(filename), StandardCharsets.UTF_8)) {
        writer.write("Café");
    }
}

public void readFile(String filename) throws IOException {
    try (InputStreamReader reader = new InputStreamReader(
            new FileInputStream(filename), StandardCharsets.UTF_8)) {
        // Reads as "Café" (correct!)
    }
}

// Impact:
// - Corrupted characters
// - Unreadable data
// - Hard to debug
```

---

## 3. Buffer Management

### Pitfall: Not Flushing
```java
// ❌ WRONG: Data not written
public void writeFile(String filename) throws IOException {
    try (FileOutputStream out = new FileOutputStream(filename)) {
        out.write("Hello".getBytes());
        // Data might still be in buffer
        // If program crashes, data lost
    }
}

// ✅ CORRECT: Flush explicitly
public void writeFile(String filename) throws IOException {
    try (FileOutputStream out = new FileOutputStream(filename)) {
        out.write("Hello".getBytes());
        out.flush();  // Ensure data written
    }
}

// ✅ CORRECT: Use PrintWriter with auto-flush
public void writeFile(String filename) throws IOException {
    try (PrintWriter writer = new PrintWriter(
            new FileWriter(filename), true)) {  // true = auto-flush
        writer.println("Hello");
    }
}

// Impact:
// - Data loss on crash
// - Incomplete files
// - Hard to debug
```

---

### Pitfall: Buffer Position Issues
```java
// ❌ WRONG: Forgetting to flip buffer
public void copyFile(String source, String dest) throws IOException {
    try (FileInputStream in = new FileInputStream(source);
         FileOutputStream out = new FileOutputStream(dest);
         FileChannel inChannel = in.getChannel();
         FileChannel outChannel = out.getChannel()) {
        
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        while (inChannel.read(buffer) > 0) {
            // buffer.flip();  // FORGOT THIS!
            outChannel.write(buffer);  // Writes nothing
            buffer.clear();
        }
    }
}

// ✅ CORRECT: Flip before writing
public void copyFile(String source, String dest) throws IOException {
    try (FileInputStream in = new FileInputStream(source);
         FileOutputStream out = new FileOutputStream(dest);
         FileChannel inChannel = in.getChannel();
         FileChannel outChannel = out.getChannel()) {
        
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        while (inChannel.read(buffer) > 0) {
            buffer.flip();  // Switch from write to read mode
            outChannel.write(buffer);
            buffer.clear();  // Clear for next read
        }
    }
}

// Buffer states:
// - After allocate: position=0, limit=capacity
// - After read: position=bytesRead, limit=capacity
// - After flip: position=0, limit=bytesRead
// - After write: position=bytesWritten
// - After clear: position=0, limit=capacity
```

---

## 4. File Operations

### Pitfall: File Already Exists
```java
// ❌ WRONG: Overwrites existing file
public void createFile(String filename) throws IOException {
    try (FileOutputStream out = new FileOutputStream(filename)) {
        out.write("Hello".getBytes());
    }
    // If file exists, it's overwritten!
}

// ✅ CORRECT: Check before creating
public void createFile(String filename) throws IOException {
    if (Files.exists(Paths.get(filename))) {
        throw new IOException("File already exists");
    }
    try (FileOutputStream out = new FileOutputStream(filename)) {
        out.write("Hello".getBytes());
    }
}

// ✅ CORRECT: Use NIO with options
public void createFile(String filename) throws IOException {
    Files.write(Paths.get(filename), "Hello".getBytes(),
        StandardOpenOption.CREATE_NEW);  // Fails if exists
}

// Impact:
// - Data loss
// - Unexpected behavior
// - Hard to debug
```

---

### Pitfall: File Not Found
```java
// ❌ WRONG: No error handling
public void readFile(String filename) throws IOException {
    try (FileReader reader = new FileReader(filename)) {
        // Throws FileNotFoundException if not found
    }
}

// ✅ CORRECT: Handle exception
public void readFile(String filename) throws IOException {
    try (FileReader reader = new FileReader(filename)) {
        // Read file
    } catch (FileNotFoundException e) {
        System.err.println("File not found: " + filename);
    }
}

// ✅ CORRECT: Check before reading
public void readFile(String filename) throws IOException {
    if (!Files.exists(Paths.get(filename))) {
        throw new FileNotFoundException("File not found: " + filename);
    }
    try (FileReader reader = new FileReader(filename)) {
        // Read file
    }
}

// Impact:
// - Unhandled exception
// - Application crash
// - Poor user experience
```

---

### Pitfall: Directory vs File
```java
// ❌ WRONG: Treating directory as file
public void readFile(String path) throws IOException {
    try (FileReader reader = new FileReader(path)) {
        // If path is directory, throws IOException
    }
}

// ✅ CORRECT: Check type
public void readFile(String path) throws IOException {
    Path p = Paths.get(path);
    if (!Files.isRegularFile(p)) {
        throw new IOException("Not a regular file: " + path);
    }
    try (FileReader reader = new FileReader(path)) {
        // Read file
    }
}

// ✅ CORRECT: Use NIO
public void readFile(String path) throws IOException {
    Path p = Paths.get(path);
    if (Files.isDirectory(p)) {
        throw new IOException("Is a directory: " + path);
    }
    String content = Files.readString(p);
}

// Impact:
// - Unexpected exceptions
// - Wrong behavior
// - Hard to debug
```

---

## 5. Large File Processing

### Pitfall: Loading Entire File into Memory
```java
// ❌ WRONG: 1GB file = 1GB memory
public void processFile(String filename) throws IOException {
    String content = Files.readString(Paths.get(filename));
    // If file is 1GB, uses 1GB of memory
    // OutOfMemoryError possible
}

// ✅ CORRECT: Process line by line
public void processFile(String filename) throws IOException {
    try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
        String line;
        while ((line = reader.readLine()) != null) {
            processLine(line);
        }
    }
}

// ✅ CORRECT: Use NIO streams
public void processFile(String filename) throws IOException {
    Files.lines(Paths.get(filename))
        .forEach(this::processLine);
}

// ✅ CORRECT: Use channels
public void processFile(String filename) throws IOException {
    try (FileInputStream in = new FileInputStream(filename);
         FileChannel channel = in.getChannel()) {
        
        ByteBuffer buffer = ByteBuffer.allocate(8192);
        while (channel.read(buffer) > 0) {
            buffer.flip();
            processBuffer(buffer);
            buffer.clear();
        }
    }
}

// Impact:
// - OutOfMemoryError
// - Slow performance
// - Application crash
```

---

### Pitfall: Not Handling Partial Reads
```java
// ❌ WRONG: Assumes read() returns all data
public void readFile(String filename) throws IOException {
    try (FileInputStream in = new FileInputStream(filename)) {
        byte[] buffer = new byte[1024];
        in.read(buffer);  // Might read less than 1024 bytes
        // Processes only partial data
    }
}

// ✅ CORRECT: Handle partial reads
public void readFile(String filename) throws IOException {
    try (FileInputStream in = new FileInputStream(filename)) {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            processData(buffer, 0, bytesRead);
        }
    }
}

// ✅ CORRECT: Use readFully
public void readFile(String filename) throws IOException {
    try (DataInputStream in = new DataInputStream(new FileInputStream(filename))) {
        byte[] buffer = new byte[1024];
        in.readFully(buffer);  // Reads exactly 1024 bytes or throws
    }
}

// Impact:
// - Data loss
// - Incomplete processing
// - Corrupted results
```

---

## 6. Serialization Issues

### Pitfall: serialVersionUID Mismatch
```java
// Version 1
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
}

// Serialize user
User user = new User("John");
// Save to file

// Version 2 (modified)
public class User implements Serializable {
    private static final long serialVersionUID = 1L;  // Same ID!
    private String name;
    private int age;  // New field
}

// Try to deserialize
// InvalidClassException: serialVersionUID mismatch!

// ✅ CORRECT: Update serialVersionUID
public class User implements Serializable {
    private static final long serialVersionUID = 2L;  // Changed!
    private String name;
    private int age;
}

// Impact:
// - Cannot deserialize old objects
// - Data loss
// - Version incompatibility
```

---

### Pitfall: Sensitive Data in Serialization
```java
// ❌ WRONG: Password serialized
public class User implements Serializable {
    private String name;
    private String password;  // Serialized!
}

// Serialize user
User user = new User("John", "secret123");
// Save to file (password visible in file!)

// ✅ CORRECT: Use transient
public class User implements Serializable {
    private String name;
    private transient String password;  // Not serialized
}

// Serialize user
User user = new User("John", "secret123");
// Save to file (password not in file)

// Deserialize
User loaded = deserialize();
System.out.println(loaded.getPassword());  // null

// Impact:
// - Security breach
// - Sensitive data exposed
// - Hard to fix
```

---

## 7. Path Issues

### Pitfall: Hardcoded Paths
```java
// ❌ WRONG: Hardcoded path
public void readFile() throws IOException {
    String content = Files.readString(Paths.get("C:\\Users\\John\\file.txt"));
    // Only works on Windows
    // Only works if user is John
    // Fails on other systems
}

// ✅ CORRECT: Use relative paths
public void readFile() throws IOException {
    String content = Files.readString(Paths.get("data/file.txt"));
    // Works on any system
}

// ✅ CORRECT: Use system properties
public void readFile() throws IOException {
    String home = System.getProperty("user.home");
    String content = Files.readString(Paths.get(home, "file.txt"));
}

// ✅ CORRECT: Use Path methods
public void readFile() throws IOException {
    Path path = Paths.get("data").resolve("file.txt");
    String content = Files.readString(path);
}

// Impact:
// - Not portable
// - Fails on different systems
// - Hard to deploy
```

---

### Pitfall: Path Separator Issues
```java
// ❌ WRONG: Hardcoded separator
public void readFile() throws IOException {
    String path = "data/file.txt";  // Works on Unix
    // On Windows: "data\file.txt"
    // Mixing separators causes issues
}

// ✅ CORRECT: Use Path API
public void readFile() throws IOException {
    Path path = Paths.get("data", "file.txt");
    // Automatically uses correct separator
}

// ✅ CORRECT: Use File.separator
public void readFile() throws IOException {
    String path = "data" + File.separator + "file.txt";
}

// Impact:
// - Not portable
// - FileNotFoundException on wrong OS
// - Hard to debug
```

---

## 8. Exception Handling

### Pitfall: Swallowing Exceptions
```java
// ❌ WRONG: Exception ignored
public void readFile(String filename) {
    try {
        String content = Files.readString(Paths.get(filename));
    } catch (IOException e) {
        // Silently ignored!
    }
}

// ✅ CORRECT: Log exception
public void readFile(String filename) {
    try {
        String content = Files.readString(Paths.get(filename));
    } catch (IOException e) {
        System.err.println("Error reading file: " + e.getMessage());
        e.printStackTrace();
    }
}

// ✅ CORRECT: Re-throw exception
public void readFile(String filename) throws IOException {
    String content = Files.readString(Paths.get(filename));
}

// Impact:
// - Silent failures
// - Hard to debug
// - Data loss
```

---

### Pitfall: Catching Too Broad Exception
```java
// ❌ WRONG: Catches all exceptions
public void readFile(String filename) {
    try {
        String content = Files.readString(Paths.get(filename));
        int value = Integer.parseInt(content);
    } catch (Exception e) {
        // Catches both IOException and NumberFormatException
        // Can't distinguish between them
    }
}

// ✅ CORRECT: Catch specific exceptions
public void readFile(String filename) throws IOException {
    try {
        String content = Files.readString(Paths.get(filename));
        int value = Integer.parseInt(content);
    } catch (IOException e) {
        System.err.println("File error: " + e.getMessage());
    } catch (NumberFormatException e) {
        System.err.println("Invalid number: " + e.getMessage());
    }
}

// Impact:
// - Can't handle errors properly
// - Masks bugs
// - Hard to debug
```

---

## 9. Concurrency Issues

### Pitfall: Concurrent File Access
```java
// ❌ WRONG: Multiple threads writing
public class FileWriter {
    private PrintWriter writer;
    
    public FileWriter(String filename) throws IOException {
        writer = new PrintWriter(new FileWriter(filename));
    }
    
    public void write(String data) {
        writer.println(data);  // Not thread-safe!
    }
}

// Thread 1: writer.println("A");
// Thread 2: writer.println("B");
// Result: "AB" or "BA" or corrupted?

// ✅ CORRECT: Synchronize access
public class FileWriter {
    private PrintWriter writer;
    
    public FileWriter(String filename) throws IOException {
        writer = new PrintWriter(new FileWriter(filename));
    }
    
    public synchronized void write(String data) {
        writer.println(data);
    }
}

// ✅ CORRECT: Use concurrent utilities
public class FileWriter {
    private PrintWriter writer;
    private final Object lock = new Object();
    
    public void write(String data) {
        synchronized (lock) {
            writer.println(data);
        }
    }
}

// Impact:
// - Data corruption
// - Lost writes
// - Hard to debug
```

---

## 10. Performance Issues

### Pitfall: No Buffering
```java
// ❌ WRONG: Byte by byte (very slow)
public void copyFile(String source, String dest) throws IOException {
    try (FileInputStream in = new FileInputStream(source);
         FileOutputStream out = new FileOutputStream(dest)) {
        
        int data;
        while ((data = in.read()) != -1) {
            out.write(data);  // One system call per byte!
        }
    }
}

// ✅ CORRECT: Use buffering
public void copyFile(String source, String dest) throws IOException {
    try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(source));
         BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dest))) {
        
        int data;
        while ((data = in.read()) != -1) {
            out.write(data);
        }
    }
}

// ✅ CORRECT: Use buffer array
public void copyFile(String source, String dest) throws IOException {
    try (FileInputStream in = new FileInputStream(source);
         FileOutputStream out = new FileOutputStream(dest)) {
        
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
    }
}

// Performance:
// No buffering: 1000ms
// Buffering: 10ms
// 100x faster!
```

---

## Summary of Common Pitfalls

| Pitfall | Impact | Solution |
|---------|--------|----------|
| Not closing resources | Resource leak | Use try-with-resources |
| Wrong encoding | Corrupted data | Specify encoding explicitly |
| Not flushing | Data loss | Call flush() or use auto-flush |
| File already exists | Data loss | Check before creating |
| Loading large files | OutOfMemoryError | Process line by line |
| Hardcoded paths | Not portable | Use Path API |
| Swallowing exceptions | Silent failures | Log or re-throw |
| No buffering | Slow performance | Use buffering |
| Concurrent access | Data corruption | Synchronize access |
| serialVersionUID mismatch | Cannot deserialize | Update version ID |

---

**Next**: Review PEDAGOGIC_GUIDE.md for learning strategies!