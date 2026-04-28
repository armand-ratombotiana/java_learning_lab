# 📝 File I/O - Quizzes

## Beginner Level (5 Questions)

### Q1: Stream Types
**Question**: What is the difference between InputStream and Reader?

**Options**:
A) InputStream reads bytes, Reader reads characters  
B) Reader is faster than InputStream  
C) InputStream is for files, Reader is for networks  
D) No difference, they're the same  

**Answer**: **A) InputStream reads bytes, Reader reads characters**

**Explanation**:
```java
// InputStream: Reads bytes (0-255)
InputStream in = new FileInputStream("file.bin");
int data = in.read();  // Returns byte (0-255)

// Reader: Reads characters (Unicode)
Reader reader = new FileReader("file.txt");
int data = reader.read();  // Returns character (0-65535)

// Byte stream for binary data
try (FileInputStream in = new FileInputStream("image.jpg")) {
    byte[] buffer = new byte[1024];
    int bytesRead = in.read(buffer);
}

// Character stream for text data
try (FileReader reader = new FileReader("text.txt")) {
    char[] buffer = new char[1024];
    int charsRead = reader.read(buffer);
}

// Stream hierarchy
InputStream (abstract)
├── FileInputStream
├── BufferedInputStream
├── DataInputStream
└── ...

Reader (abstract)
├── FileReader
├── BufferedReader
├── InputStreamReader
└── ...
```

---

### Q2: Try-With-Resources
**Question**: What does try-with-resources do?

**Options**:
A) Catches all exceptions  
B) Automatically closes resources  
C) Prevents exceptions  
D) Manages memory  

**Answer**: **B) Automatically closes resources**

**Explanation**:
```java
// ❌ WRONG: Resource leak
public void readFile(String filename) throws IOException {
    FileReader reader = new FileReader(filename);
    // If exception occurs, reader not closed
}

// ✅ CORRECT: Try-with-resources
public void readFile(String filename) throws IOException {
    try (FileReader reader = new FileReader(filename)) {
        // Read file
    }
    // close() automatically called

// ✅ CORRECT: Multiple resources
try (FileReader reader = new FileReader("file.txt");
     BufferedReader buffered = new BufferedReader(reader)) {
    String line;
    while ((line = buffered.readLine()) != null) {
        System.out.println(line);
    }
}
// Both resources automatically closed

// Requirements:
// - Resource must implement AutoCloseable
// - close() called automatically
// - Works with multiple resources
// - Resources closed in reverse order
```

---

### Q3: Buffering
**Question**: Why use BufferedInputStream?

**Options**:
A) Reads more data  
B) Improves performance  
C) Prevents exceptions  
D) Reduces file size  

**Answer**: **B) Improves performance**

**Explanation**:
```java
// ❌ WRONG: No buffering (slow)
public void copyFile(String source, String dest) throws IOException {
    try (FileInputStream in = new FileInputStream(source);
         FileOutputStream out = new FileOutputStream(dest)) {
        
        int data;
        while ((data = in.read()) != -1) {
            out.write(data);  // One byte at a time
        }
    }
}

// ✅ CORRECT: Use buffering
public void copyFile(String source, String dest) throws IOException {
    try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(source));
         BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dest))) {
        
        int data;
        while ((data = in.read()) != -1) {
            out.write(data);  // Buffered
        }
    }
}

// ✅ CORRECT: Use buffer array
public void copyFile(String source, String dest) throws IOException {
    try (FileInputStream in = new FileInputStream(source);
         FileOutputStream out = new FileOutputStream(dest)) {
        
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
    }
}

// Performance comparison:
// No buffering: 1 system call per byte (very slow)
// Buffering: 1 system call per buffer (fast)
// Buffer array: 1 system call per buffer (fast)
```

---

### Q4: NIO Files Class
**Question**: What is the simplest way to read a file?

**Options**:
A) FileInputStream + loop  
B) BufferedReader + loop  
C) Files.readString()  
D) FileReader + loop  

**Answer**: **C) Files.readString()**

**Explanation**:
```java
import java.nio.file.*;

// ❌ WRONG: Verbose
public String readFile(String filename) throws IOException {
    StringBuilder sb = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
    }
    return sb.toString();
}

// ✅ CORRECT: Simple and clean
public String readFile(String filename) throws IOException {
    return Files.readString(Paths.get(filename));
}

// ✅ CORRECT: Read all lines
public List<String> readLines(String filename) throws IOException {
    return Files.readAllLines(Paths.get(filename));
}

// ✅ CORRECT: Write file
public void writeFile(String filename, String content) throws IOException {
    Files.writeString(Paths.get(filename), content);
}

// ✅ CORRECT: Copy file
public void copyFile(String source, String dest) throws IOException {
    Files.copy(Paths.get(source), Paths.get(dest));
}

// NIO advantages:
// - Simpler API
// - Better performance
// - Modern approach
// - Recommended for new code
```

---

### Q5: File Class
**Question**: What does File.exists() do?

**Options**:
A) Creates the file  
B) Checks if file exists  
C) Deletes the file  
D) Reads the file  

**Answer**: **B) Checks if file exists**

**Explanation**:
```java
import java.io.File;

// Create File object
File file = new File("data.txt");

// Check if file exists
if (file.exists()) {
    System.out.println("File exists");
}

// Check if directory
if (file.isDirectory()) {
    System.out.println("Is directory");
}

// Check if file
if (file.isFile()) {
    System.out.println("Is file");
}

// Get file name
String name = file.getName();

// Get absolute path
String path = file.getAbsolutePath();

// Get parent directory
File parent = file.getParentFile();

// List files in directory
File[] files = file.listFiles();

// Create file
file.createNewFile();

// Delete file
file.delete();

// Get file size
long size = file.length();

// Get last modified time
long modified = file.lastModified();
```

---

## Intermediate Level (5 Questions)

### Q6: DataInputStream and DataOutputStream
**Question**: What are DataInputStream and DataOutputStream used for?

**Answer**:
```java
import java.io.*;

// Write primitive types
public void writeData(String filename) throws IOException {
    try (DataOutputStream out = new DataOutputStream(new FileOutputStream(filename))) {
        out.writeInt(42);
        out.writeDouble(3.14);
        out.writeBoolean(true);
        out.writeUTF("Hello");
    }
}

// Read primitive types
public void readData(String filename) throws IOException {
    try (DataInputStream in = new DataInputStream(new FileInputStream(filename))) {
        int intValue = in.readInt();
        double doubleValue = in.readDouble();
        boolean boolValue = in.readBoolean();
        String str = in.readUTF();
        
        System.out.println("Int: " + intValue);
        System.out.println("Double: " + doubleValue);
        System.out.println("Boolean: " + boolValue);
        System.out.println("String: " + str);
    }
}

// Benefits:
// - Write/read primitive types directly
// - No conversion needed
// - Efficient binary format
// - Type-safe

// Methods:
// writeInt(), readInt()
// writeDouble(), readDouble()
// writeBoolean(), readBoolean()
// writeUTF(), readUTF()
// writeLong(), readLong()
// writeFloat(), readFloat()
```

---

### Q7: PrintWriter
**Question**: What is PrintWriter used for?

**Answer**:
```java
import java.io.*;

// PrintWriter: Convenient for writing text
public void writeFile(String filename) throws IOException {
    try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
        writer.println("Hello");
        writer.println("World");
        writer.printf("Number: %d\n", 42);
    }
}

// PrintWriter with auto-flush
try (PrintWriter writer = new PrintWriter(new FileWriter(filename), true)) {
    writer.println("Hello");  // Automatically flushed
}

// PrintWriter with System.out
PrintWriter out = new PrintWriter(System.out, true);
out.println("Hello");  // Prints to console

// Benefits:
// - println() method (convenient)
// - printf() method (formatted output)
// - Auto-flush option
// - No need to catch IOException for print operations

// Comparison:
// FileWriter: Basic character writing
// BufferedWriter: Buffered character writing
// PrintWriter: Convenient text writing with println/printf
```

---

### Q8: BufferedReader
**Question**: How do you read lines from a file?

**Answer**:
```java
import java.io.*;

// ❌ WRONG: Reading character by character
public void readFile(String filename) throws IOException {
    try (FileReader reader = new FileReader(filename)) {
        int data;
        while ((data = reader.read()) != -1) {
            System.out.print((char) data);
        }
    }
}

// ✅ CORRECT: Use BufferedReader
public void readFile(String filename) throws IOException {
    try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }
}

// ✅ CORRECT: Use NIO
public void readFile(String filename) throws IOException {
    Files.lines(Paths.get(filename))
        .forEach(System.out::println);
}

// ✅ CORRECT: Read all lines
public List<String> readLines(String filename) throws IOException {
    return Files.readAllLines(Paths.get(filename));
}

// Methods:
// readLine(): Read one line
// read(): Read one character
// read(char[]): Read into array
```

---

### Q9: File Copying
**Question**: What's the best way to copy a file?

**Answer**:
```java
import java.io.*;
import java.nio.file.*;

// ❌ WRONG: Byte by byte (slow)
public void copyFile(String source, String dest) throws IOException {
    try (FileInputStream in = new FileInputStream(source);
         FileOutputStream out = new FileOutputStream(dest)) {
        
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
        
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
    }
}

// ✅ CORRECT: Use NIO (simplest)
public void copyFile(String source, String dest) throws IOException {
    Files.copy(Paths.get(source), Paths.get(dest));
}

// ✅ CORRECT: Use channels (efficient)
public void copyFile(String source, String dest) throws IOException {
    try (FileInputStream in = new FileInputStream(source);
         FileOutputStream out = new FileOutputStream(dest);
         FileChannel inChannel = in.getChannel();
         FileChannel outChannel = out.getChannel()) {
        
        inChannel.transferTo(0, inChannel.size(), outChannel);
    }
}

// Performance:
// Byte by byte: Very slow
// Buffer array: Fast
// NIO Files.copy(): Fast and simple
// Channels: Very fast
```

---

### Q10: Serialization
**Question**: What is serialization used for?

**Answer**:
```java
import java.io.*;

// Make class serializable
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private int age;
    private transient String password;  // Not serialized
    
    public User(String name, int age, String password) {
        this.name = name;
        this.age = age;
        this.password = password;
    }
}

// Serialize object
public void serializeUser(User user, String filename) throws IOException {
    try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
        out.writeObject(user);
    }
}

// Deserialize object
public User deserializeUser(String filename) throws IOException, ClassNotFoundException {
    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
        return (User) in.readObject();
    }
}

// Usage
User user = new User("John", 30, "secret");
serializeUser(user, "user.dat");

User loaded = deserializeUser("user.dat");
System.out.println("Name: " + loaded.getName());
System.out.println("Password: " + loaded.getPassword());  // null (not serialized)

// Benefits:
// - Save objects to file
// - Send objects over network
// - Preserve object state

// Important:
// - Implement Serializable
// - Define serialVersionUID
// - Use transient for sensitive data
// - Handle ClassNotFoundException
```

---

## Advanced Level (5 Questions)

### Q11: Directory Operations
**Question**: How do you list files in a directory?

**Answer**:
```java
import java.nio.file.*;
import java.io.File;

// Old way: File class
public void listFiles(String dirname) {
    File dir = new File(dirname);
    File[] files = dir.listFiles();
    if (files != null) {
        for (File file : files) {
            System.out.println(file.getName());
        }
    }
}

// ✅ CORRECT: NIO DirectoryStream
public void listFiles(String dirname) throws IOException {
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(dirname))) {
        for (Path path : stream) {
            System.out.println(path.getFileName());
        }
    }
}

// ✅ CORRECT: NIO walk
public void walkDirectory(String dirname) throws IOException {
    Files.walk(Paths.get(dirname))
        .forEach(System.out::println);
}

// ✅ CORRECT: Find files matching pattern
public void findFiles(String dirname, String pattern) throws IOException {
    Files.walk(Paths.get(dirname))
        .filter(path -> path.getFileName().toString().matches(pattern))
        .forEach(System.out::println);
}

// ✅ CORRECT: Create directory
public void createDirectory(String dirname) throws IOException {
    Files.createDirectory(Paths.get(dirname));
}

// ✅ CORRECT: Create directories recursively
public void createDirectories(String dirname) throws IOException {
    Files.createDirectories(Paths.get(dirname));
}
```

---

### Q12: File Attributes
**Question**: How do you get file attributes?

**Answer**:
```java
import java.nio.file.*;
import java.nio.file.attribute.*;

// Get file attributes
public void getAttributes(String filename) throws IOException {
    Path path = Paths.get(filename);
    
    // Basic attributes
    BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
    System.out.println("Size: " + attrs.size());
    System.out.println("Created: " + attrs.creationTime());
    System.out.println("Modified: " + attrs.lastModifiedTime());
    System.out.println("Accessed: " + attrs.lastAccessTime());
    System.out.println("Is directory: " + attrs.isDirectory());
    System.out.println("Is regular file: " + attrs.isRegularFile());
    System.out.println("Is symbolic link: " + attrs.isSymbolicLink());
    
    // POSIX attributes (Unix/Linux)
    PosixFileAttributes posixAttrs = Files.readAttributes(path, PosixFileAttributes.class);
    System.out.println("Permissions: " + posixAttrs.permissions());
    System.out.println("Owner: " + posixAttrs.owner());
    System.out.println("Group: " + posixAttrs.group());
}

// Check if readable/writable/executable
public void checkPermissions(String filename) {
    Path path = Paths.get(filename);
    System.out.println("Readable: " + Files.isReadable(path));
    System.out.println("Writable: " + Files.isWritable(path));
    System.out.println("Executable: " + Files.isExecutable(path));
}

// Get file size
public long getFileSize(String filename) throws IOException {
    return Files.size(Paths.get(filename));
}

// Check if exists
public boolean fileExists(String filename) {
    return Files.exists(Paths.get(filename));
}
```

---

### Q13: Channels and Buffers
**Question**: What are channels and buffers?

**Answer**:
```java
import java.nio.channels.*;
import java.nio.ByteBuffer;
import java.io.*;

// Channels: Connections to I/O resources
// Buffers: Containers for data

// Copy file using channels
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

// Read file using channels
public String readFile(String filename) throws IOException {
    try (FileInputStream in = new FileInputStream(filename);
         FileChannel channel = in.getChannel()) {
        
        ByteBuffer buffer = ByteBuffer.allocate((int) channel.size());
        channel.read(buffer);
        buffer.flip();
        return new String(buffer.array());
    }
}

// Direct transfer (most efficient)
public void copyFile(String source, String dest) throws IOException {
    try (FileInputStream in = new FileInputStream(source);
         FileOutputStream out = new FileOutputStream(dest);
         FileChannel inChannel = in.getChannel();
         FileChannel outChannel = out.getChannel()) {
        
        inChannel.transferTo(0, inChannel.size(), outChannel);
    }
}

// Benefits:
// - Better performance
// - Non-blocking I/O possible
// - Memory-mapped files
// - Direct buffer access
```

---

### Q14: InputStreamReader and OutputStreamWriter
**Question**: What are InputStreamReader and OutputStreamWriter?

**Answer**:
```java
import java.io.*;
import java.nio.charset.StandardCharsets;

// InputStreamReader: Convert InputStream to Reader
public void readFile(String filename) throws IOException {
    try (InputStreamReader reader = new InputStreamReader(
            new FileInputStream(filename), StandardCharsets.UTF_8)) {
        
        int data;
        while ((data = reader.read()) != -1) {
            System.out.print((char) data);
        }
    }
}

// OutputStreamWriter: Convert OutputStream to Writer
public void writeFile(String filename, String content) throws IOException {
    try (OutputStreamWriter writer = new OutputStreamWriter(
            new FileOutputStream(filename), StandardCharsets.UTF_8)) {
        
        writer.write(content);
    }
}

// With BufferedReader
public void readFile(String filename) throws IOException {
    try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8))) {
        
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }
}

// Benefits:
// - Specify character encoding
// - Convert between byte and character streams
// - Support different charsets (UTF-8, ISO-8859-1, etc.)
```

---

### Q15: File Deletion and Renaming
**Question**: How do you delete and rename files?

**Answer**:
```java
import java.nio.file.*;
import java.io.File;

// Old way: File class
public void deleteFile(String filename) {
    File file = new File(filename);
    if (file.delete()) {
        System.out.println("File deleted");
    } else {
        System.out.println("Failed to delete");
    }
}

public void renameFile(String oldName, String newName) {
    File oldFile = new File(oldName);
    File newFile = new File(newName);
    if (oldFile.renameTo(newFile)) {
        System.out.println("File renamed");
    } else {
        System.out.println("Failed to rename");
    }
}

// ✅ CORRECT: NIO way
public void deleteFile(String filename) throws IOException {
    Files.delete(Paths.get(filename));
}

public void deleteFileIfExists(String filename) throws IOException {
    Files.deleteIfExists(Paths.get(filename));
}

public void renameFile(String oldName, String newName) throws IOException {
    Files.move(Paths.get(oldName), Paths.get(newName));
}

// Move with options
public void moveFile(String source, String dest) throws IOException {
    Files.move(Paths.get(source), Paths.get(dest),
        StandardCopyOption.REPLACE_EXISTING,
        StandardCopyOption.ATOMIC_MOVE);
}

// Benefits of NIO:
// - Throws exception on failure (not silent)
// - More options (REPLACE_EXISTING, ATOMIC_MOVE)
// - Better error handling
```

---

## Interview Tricky Questions (3 Questions)

### Q16: Encoding Issues
**Question**: What happens if you don't specify encoding?

**Answer**:
```java
import java.io.*;
import java.nio.charset.StandardCharsets;

// ❌ WRONG: Uses platform default encoding
public void readFile(String filename) throws IOException {
    try (FileReader reader = new FileReader(filename)) {
        // Uses system default encoding (might be wrong)
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

// Common encodings:
// - UTF-8: Universal, recommended
// - ISO-8859-1: Western European
// - UTF-16: Unicode
// - ASCII: English only

// Problems without encoding:
// - File might be unreadable
// - Characters might be corrupted
// - Platform-dependent behavior
```

---

### Q17: Large File Processing
**Question**: How do you process a large file efficiently?

**Answer**:
```java
import java.nio.file.*;
import java.io.*;

// ❌ WRONG: Load entire file into memory
public void processFile(String filename) throws IOException {
    String content = Files.readString(Paths.get(filename));
    // If file is 1GB, this uses 1GB of memory!
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

// ✅ CORRECT: Use channels with buffers
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

// Benefits:
// - Constant memory usage
// - Can process files larger than RAM
// - Better performance
```

---

## Summary

### Key Concepts to Master
1. **Stream Types**: InputStream/OutputStream, Reader/Writer
2. **Buffering**: BufferedInputStream, BufferedReader
3. **Try-With-Resources**: Automatic resource management
4. **NIO**: Modern file I/O with Files class
5. **Serialization**: Object persistence
6. **File Operations**: Copy, delete, rename, attributes
7. **Encoding**: Character set handling

### Common Mistakes
- ❌ Not closing resources
- ❌ Not using buffering
- ❌ Ignoring encoding
- ❌ Loading large files into memory
- ❌ Not handling exceptions
- ❌ Using old File class instead of NIO

### Best Practices
- ✅ Always use try-with-resources
- ✅ Use buffering for performance
- ✅ Specify encoding explicitly
- ✅ Use NIO for modern code
- ✅ Process large files line by line
- ✅ Handle exceptions properly
- ✅ Close resources in finally

---

**Next**: Study EDGE_CASES.md to learn about common pitfalls!