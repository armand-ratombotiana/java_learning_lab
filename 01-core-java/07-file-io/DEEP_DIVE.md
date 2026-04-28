# 🔍 File I/O - Deep Dive

## Table of Contents
1. [File I/O Fundamentals](#file-io-fundamentals)
2. [Streams](#streams)
3. [Readers and Writers](#readers-and-writers)
4. [NIO (New I/O)](#nio-new-io)
5. [File Operations](#file-operations)
6. [Serialization](#serialization)
7. [Best Practices](#best-practices)

---

## File I/O Fundamentals

### I/O Streams

```
Stream: Sequence of bytes flowing from source to destination

Input Stream:
Source → InputStream → Program
(File, Network, etc.)

Output Stream:
Program → OutputStream → Destination
(File, Network, etc.)

Byte Stream:
- Reads/writes bytes (0-255)
- Used for binary data
- InputStream, OutputStream

Character Stream:
- Reads/writes characters (Unicode)
- Used for text data
- Reader, Writer
```

### Stream Hierarchy

```
InputStream (abstract)
├── FileInputStream: Read bytes from file
├── ByteArrayInputStream: Read bytes from array
├── BufferedInputStream: Buffered reading
├── DataInputStream: Read primitive types
└── ...

OutputStream (abstract)
├── FileOutputStream: Write bytes to file
├── ByteArrayOutputStream: Write bytes to array
├── BufferedOutputStream: Buffered writing
├── DataOutputStream: Write primitive types
└── ...

Reader (abstract)
├── FileReader: Read characters from file
├── BufferedReader: Buffered reading
├── InputStreamReader: Convert stream to reader
└── ...

Writer (abstract)
├── FileWriter: Write characters to file
├── BufferedWriter: Buffered writing
├── OutputStreamWriter: Convert stream to writer
└── ...
```

---

## Streams

### FileInputStream and FileOutputStream

```java
// ❌ WRONG: Resource leak
public void copyFile(String source, String dest) throws IOException {
    FileInputStream in = new FileInputStream(source);
    FileOutputStream out = new FileOutputStream(dest);
    
    int data;
    while ((data = in.read()) != -1) {
        out.write(data);
    }
    // Streams not closed!
}

// ✅ CORRECT: Use try-with-resources
public void copyFile(String source, String dest) throws IOException {
    try (FileInputStream in = new FileInputStream(source);
         FileOutputStream out = new FileOutputStream(dest)) {
        
        int data;
        while ((data = in.read()) != -1) {
            out.write(data);
        }
    }
    // Streams automatically closed
}

// ✅ CORRECT: Use buffering for performance
public void copyFile(String source, String dest) throws IOException {
    try (FileInputStream in = new FileInputStream(source);
         FileOutputStream out = new FileOutputStream(dest)) {
        
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
    }
    // Much faster than reading byte by byte
}

// ✅ CORRECT: Use BufferedInputStream/BufferedOutputStream
public void copyFile(String source, String dest) throws IOException {
    try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(source));
         BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dest))) {
        
        int data;
        while ((data = in.read()) != -1) {
            out.write(data);
        }
    }
    // Buffering handled automatically
}
```

### DataInputStream and DataOutputStream

```java
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
```

---

## Readers and Writers

### FileReader and FileWriter

```java
// ❌ WRONG: Resource leak
public void readFile(String filename) throws IOException {
    FileReader reader = new FileReader(filename);
    int data;
    while ((data = reader.read()) != -1) {
        System.out.print((char) data);
    }
    // Reader not closed!
}

// ✅ CORRECT: Use try-with-resources
public void readFile(String filename) throws IOException {
    try (FileReader reader = new FileReader(filename)) {
        int data;
        while ((data = reader.read()) != -1) {
            System.out.print((char) data);
        }
    }
    // Reader automatically closed
}

// ✅ CORRECT: Use BufferedReader for line reading
public void readFile(String filename) throws IOException {
    try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }
    // Much more convenient for text files
}

// ✅ CORRECT: Write text
public void writeFile(String filename, String content) throws IOException {
    try (FileWriter writer = new FileWriter(filename)) {
        writer.write(content);
    }
    // Writer automatically closed
}

// ✅ CORRECT: Write with buffering
public void writeFile(String filename, List<String> lines) throws IOException {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
        for (String line : lines) {
            writer.write(line);
            writer.newLine();
        }
    }
    // Buffering improves performance
}
```

### PrintWriter

```java
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
```

---

## NIO (New I/O)

### Files and Paths

```java
import java.nio.file.*;

// Read entire file
public String readFile(String filename) throws IOException {
    return Files.readString(Paths.get(filename));
}

// Write entire file
public void writeFile(String filename, String content) throws IOException {
    Files.writeString(Paths.get(filename), content);
}

// Read all lines
public List<String> readLines(String filename) throws IOException {
    return Files.readAllLines(Paths.get(filename));
}

// Write lines
public void writeLines(String filename, List<String> lines) throws IOException {
    Files.write(Paths.get(filename), lines);
}

// Copy file
public void copyFile(String source, String dest) throws IOException {
    Files.copy(Paths.get(source), Paths.get(dest));
}

// Delete file
public void deleteFile(String filename) throws IOException {
    Files.delete(Paths.get(filename));
}

// Check if file exists
public boolean fileExists(String filename) {
    return Files.exists(Paths.get(filename));
}

// Get file size
public long getFileSize(String filename) throws IOException {
    return Files.size(Paths.get(filename));
}
```

### Channels and Buffers

```java
import java.nio.channels.*;
import java.nio.ByteBuffer;

// Copy file using channels
public void copyFile(String source, String dest) throws IOException {
    try (FileInputStream in = new FileInputStream(source);
         FileOutputStream out = new FileOutputStream(dest);
         FileChannel inChannel = in.getChannel();
         FileChannel outChannel = out.getChannel()) {
        
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        while (inChannel.read(buffer) > 0) {
            buffer.flip();
            outChannel.write(buffer);
            buffer.clear();
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
```

### Directory Operations

```java
import java.nio.file.*;

// List files in directory
public void listFiles(String dirname) throws IOException {
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(dirname))) {
        for (Path path : stream) {
            System.out.println(path.getFileName());
        }
    }
}

// Walk directory tree
public void walkDirectory(String dirname) throws IOException {
    Files.walk(Paths.get(dirname))
        .forEach(System.out::println);
}

// Find files matching pattern
public void findFiles(String dirname, String pattern) throws IOException {
    Files.walk(Paths.get(dirname))
        .filter(path -> path.getFileName().toString().matches(pattern))
        .forEach(System.out::println);
}

// Create directory
public void createDirectory(String dirname) throws IOException {
    Files.createDirectory(Paths.get(dirname));
}

// Create directories recursively
public void createDirectories(String dirname) throws IOException {
    Files.createDirectories(Paths.get(dirname));
}
```

---

## File Operations

### File Class

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

### Path Class (NIO)

```java
import java.nio.file.*;

// Create Path
Path path = Paths.get("data.txt");

// Get file name
Path fileName = path.getFileName();

// Get parent directory
Path parent = path.getParent();

// Get root
Path root = path.getRoot();

// Resolve relative path
Path resolved = path.resolve("subdir/file.txt");

// Relativize path
Path relative = path.relativize(Paths.get("other.txt"));

// Convert to absolute
Path absolute = path.toAbsolutePath();

// Convert to File
File file = path.toFile();

// Get file attributes
BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
System.out.println("Size: " + attrs.size());
System.out.println("Created: " + attrs.creationTime());
System.out.println("Modified: " + attrs.lastModifiedTime());
```

---

## Serialization

### Serializable Interface

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
    
    // Getters...
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
System.out.println("Age: " + loaded.getAge());
System.out.println("Password: " + loaded.getPassword());  // null (not serialized)
```

### Custom Serialization

```java
public class CustomUser implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private int age;
    
    // Custom serialization
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        // Custom logic
        out.writeUTF("Custom data");
    }
    
    // Custom deserialization
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        // Custom logic
        String customData = in.readUTF();
    }
}
```

---

## Best Practices

### 1. Always Close Resources

```java
// ❌ WRONG: Resource leak
public void readFile(String filename) throws IOException {
    FileReader reader = new FileReader(filename);
    // If exception occurs, reader not closed
}

// ✅ CORRECT: Use try-with-resources
public void readFile(String filename) throws IOException {
    try (FileReader reader = new FileReader(filename)) {
        // Reader automatically closed
    }
}

// ✅ CORRECT: Use finally
public void readFile(String filename) throws IOException {
    FileReader reader = null;
    try {
        reader = new FileReader(filename);
        // Read file
    } finally {
        if (reader != null) {
            reader.close();
        }
    }
}
```

### 2. Use Buffering

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
```

### 3. Use NIO for Modern Code

```java
// ❌ WRONG: Old I/O
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

// ✅ CORRECT: NIO (simpler)
public String readFile(String filename) throws IOException {
    return Files.readString(Paths.get(filename));
}

// ✅ CORRECT: NIO for large files
public void processLargeFile(String filename) throws IOException {
    Files.lines(Paths.get(filename))
        .forEach(System.out::println);
}
```

### 4. Handle Exceptions Properly

```java
// ❌ WRONG: Ignoring exception
try {
    // File operations
} catch (IOException e) {
    // Ignoring!
}

// ✅ CORRECT: Log and handle
try {
    // File operations
} catch (IOException e) {
    logger.error("Failed to read file", e);
    throw new RuntimeException("Failed to read file", e);
}

// ✅ CORRECT: Provide meaningful message
try {
    // File operations
} catch (IOException e) {
    throw new IOException("Failed to read file: " + filename, e);
}
```

---

## Key Takeaways

### Stream Types
1. **Byte Streams**: InputStream, OutputStream (binary data)
2. **Character Streams**: Reader, Writer (text data)
3. **Buffered Streams**: BufferedInputStream, BufferedReader (performance)
4. **Data Streams**: DataInputStream, DataOutputStream (primitive types)

### NIO Advantages
1. **Simpler API**: Files.readString(), Files.writeString()
2. **Better Performance**: Channels, buffers
3. **Directory Operations**: Files.walk(), Files.list()
4. **File Attributes**: BasicFileAttributes

### Best Practices
1. ✅ Always close resources (try-with-resources)
2. ✅ Use buffering for performance
3. ✅ Use NIO for modern code
4. ✅ Handle exceptions properly
5. ✅ Use appropriate stream types
6. ✅ Validate file operations
7. ✅ Use serialVersionUID for serialization

---

**Next**: Study QUIZZES.md to test your understanding!