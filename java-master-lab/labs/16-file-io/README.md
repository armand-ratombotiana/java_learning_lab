# Lab 16: File I/O

## 📋 Lab Header

| Aspect | Details |
|--------|---------|
| **Difficulty** | Intermediate |
| **Estimated Time** | 5 hours |
| **Real-World Context** | Building a file management system |
| **Prerequisites** | Lab 15: Lock Mechanisms |
| **Learning Type** | Hands-on, Project-based |

## 🎯 Learning Objectives

By completing this lab, you will be able to:

1. **Read and write files** efficiently
2. **Use streams and readers** effectively
3. **Implement buffering strategies** for performance
4. **Handle character encoding** properly
5. **Traverse directories** and manage files
6. **Build a file management system** with error handling

## 📚 Prerequisites

- Lab 15: Lock Mechanisms completed
- Understanding of exceptions
- Knowledge of data types
- Familiarity with file systems

## 🧠 Concept Theory

### 1. File Reading

Reading file contents:

```java
import java.io.*;
import java.nio.file.*;

// Reading entire file
String content = new String(Files.readAllBytes(Paths.get("file.txt")));

// Reading line by line
try (BufferedReader reader = new BufferedReader(new FileReader("file.txt"))) {
    String line;
    while ((line = reader.readLine()) != null) {
        System.out.println(line);
    }
}

// Reading with FileInputStream
try (FileInputStream fis = new FileInputStream("file.txt")) {
    byte[] data = new byte[1024];
    int bytesRead;
    while ((bytesRead = fis.read(data)) != -1) {
        System.out.write(data, 0, bytesRead);
    }
}

// Reading with Scanner
try (Scanner scanner = new Scanner(new File("file.txt"))) {
    while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        System.out.println(line);
    }
}

// Reading all lines
List<String> lines = Files.readAllLines(Paths.get("file.txt"));
lines.forEach(System.out::println);
```

### 2. File Writing

Writing data to files:

```java
// Writing entire content
Files.write(Paths.get("output.txt"), "Hello, World!".getBytes());

// Writing line by line
try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"))) {
    writer.write("Line 1");
    writer.newLine();
    writer.write("Line 2");
    writer.newLine();
}

// Writing with FileOutputStream
try (FileOutputStream fos = new FileOutputStream("output.txt")) {
    fos.write("Hello, World!".getBytes());
}

// Writing with PrintWriter
try (PrintWriter writer = new PrintWriter(new FileWriter("output.txt"))) {
    writer.println("Line 1");
    writer.println("Line 2");
}

// Appending to file
Files.write(Paths.get("output.txt"), "Appended text".getBytes(), 
    StandardOpenOption.APPEND);
```

### 3. Buffering Strategies

Optimizing I/O performance:

```java
// Unbuffered - slow
try (FileInputStream fis = new FileInputStream("file.txt")) {
    int data;
    while ((data = fis.read()) != -1) {
        System.out.write(data);
    }
}

// Buffered - faster
try (BufferedInputStream bis = new BufferedInputStream(
        new FileInputStream("file.txt"))) {
    int data;
    while ((data = bis.read()) != -1) {
        System.out.write(data);
    }
}

// Custom buffer size
try (BufferedInputStream bis = new BufferedInputStream(
        new FileInputStream("file.txt"), 8192)) {
    byte[] buffer = new byte[8192];
    int bytesRead;
    while ((bytesRead = bis.read(buffer)) != -1) {
        System.out.write(buffer, 0, bytesRead);
    }
}

// Performance comparison
long startTime = System.nanoTime();
// Unbuffered read
long unbufferedTime = System.nanoTime() - startTime;

startTime = System.nanoTime();
// Buffered read
long bufferedTime = System.nanoTime() - startTime;

System.out.println("Unbuffered: " + unbufferedTime);
System.out.println("Buffered: " + bufferedTime);
System.out.println("Speedup: " + (unbufferedTime / (double) bufferedTime) + "x");
```

### 4. Character Encoding

Handling different character encodings:

```java
import java.nio.charset.*;

// Default encoding
String content = new String(Files.readAllBytes(Paths.get("file.txt")));

// Specific encoding
String content = new String(Files.readAllBytes(Paths.get("file.txt")), 
    StandardCharsets.UTF_8);

// Writing with encoding
Files.write(Paths.get("output.txt"), "Hello, 世界".getBytes(StandardCharsets.UTF_8));

// Reading with encoding
try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(new FileInputStream("file.txt"), StandardCharsets.UTF_8))) {
    String line;
    while ((line = reader.readLine()) != null) {
        System.out.println(line);
    }
}

// Available charsets
Charset.availableCharsets().forEach((name, charset) -> {
    System.out.println(name + ": " + charset);
});

// Common charsets
StandardCharsets.UTF_8;
StandardCharsets.UTF_16;
StandardCharsets.ISO_8859_1;
StandardCharsets.US_ASCII;
```

### 5. Directory Traversal

Working with directories:

```java
import java.nio.file.*;
import java.nio.file.attribute.*;

// List files in directory
try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get("."))) {
    for (Path path : stream) {
        System.out.println(path.getFileName());
    }
}

// List files with filter
try (DirectoryStream<Path> stream = Files.newDirectoryStream(
        Paths.get("."), "*.java")) {
    for (Path path : stream) {
        System.out.println(path.getFileName());
    }
}

// Walk directory tree
Files.walk(Paths.get("."))
    .filter(Files::isRegularFile)
    .forEach(System.out::println);

// Walk with depth limit
Files.walk(Paths.get("."), 2)
    .filter(Files::isRegularFile)
    .forEach(System.out::println);

// Get file attributes
Path path = Paths.get("file.txt");
BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
System.out.println("Size: " + attrs.size());
System.out.println("Created: " + attrs.creationTime());
System.out.println("Modified: " + attrs.lastModifiedTime());
System.out.println("Is directory: " + attrs.isDirectory());
System.out.println("Is regular file: " + attrs.isRegularFile());
```

### 6. File Operations

Common file operations:

```java
// Create file
Files.createFile(Paths.get("newfile.txt"));

// Create directory
Files.createDirectory(Paths.get("newdir"));

// Create directories (including parents)
Files.createDirectories(Paths.get("a/b/c"));

// Copy file
Files.copy(Paths.get("source.txt"), Paths.get("dest.txt"));

// Copy with options
Files.copy(Paths.get("source.txt"), Paths.get("dest.txt"),
    StandardCopyOption.REPLACE_EXISTING);

// Move file
Files.move(Paths.get("source.txt"), Paths.get("dest.txt"));

// Delete file
Files.delete(Paths.get("file.txt"));

// Delete if exists
Files.deleteIfExists(Paths.get("file.txt"));

// Check existence
if (Files.exists(Paths.get("file.txt"))) {
    System.out.println("File exists");
}

// Get file size
long size = Files.size(Paths.get("file.txt"));
System.out.println("Size: " + size + " bytes");
```

### 7. Error Handling

Proper exception handling:

```java
// ❌ Bad: Not handling exceptions
BufferedReader reader = new BufferedReader(new FileReader("file.txt"));
String line = reader.readLine();
reader.close();

// ✅ Good: Try-with-resources
try (BufferedReader reader = new BufferedReader(new FileReader("file.txt"))) {
    String line = reader.readLine();
}

// ✅ Good: Explicit exception handling
try (BufferedReader reader = new BufferedReader(new FileReader("file.txt"))) {
    String line = reader.readLine();
} catch (FileNotFoundException e) {
    System.err.println("File not found: " + e.getMessage());
} catch (IOException e) {
    System.err.println("I/O error: " + e.getMessage());
}

// ✅ Good: Handling specific exceptions
try {
    Files.readAllBytes(Paths.get("file.txt"));
} catch (NoSuchFileException e) {
    System.err.println("File not found");
} catch (AccessDeniedException e) {
    System.err.println("Access denied");
} catch (IOException e) {
    System.err.println("I/O error");
}
```

### 8. File Copying

Efficient file copying:

```java
// Copy small file
Files.copy(Paths.get("source.txt"), Paths.get("dest.txt"));

// Copy large file with buffer
try (InputStream in = Files.newInputStream(Paths.get("source.bin"));
     OutputStream out = Files.newOutputStream(Paths.get("dest.bin"))) {
    byte[] buffer = new byte[8192];
    int bytesRead;
    while ((bytesRead = in.read(buffer)) != -1) {
        out.write(buffer, 0, bytesRead);
    }
}

// Copy with progress tracking
long totalSize = Files.size(Paths.get("source.bin"));
long copiedSize = 0;

try (InputStream in = Files.newInputStream(Paths.get("source.bin"));
     OutputStream out = Files.newOutputStream(Paths.get("dest.bin"))) {
    byte[] buffer = new byte[8192];
    int bytesRead;
    while ((bytesRead = in.read(buffer)) != -1) {
        out.write(buffer, 0, bytesRead);
        copiedSize += bytesRead;
        double progress = (copiedSize * 100.0) / totalSize;
        System.out.printf("Progress: %.2f%%\n", progress);
    }
}
```

### 9. File Deletion

Safe file deletion:

```java
// Delete single file
Files.delete(Paths.get("file.txt"));

// Delete if exists
Files.deleteIfExists(Paths.get("file.txt"));

// Delete directory (must be empty)
Files.delete(Paths.get("emptydir"));

// Delete directory recursively
Files.walk(Paths.get("dir"))
    .sorted(Comparator.reverseOrder())
    .forEach(path -> {
        try {
            Files.delete(path);
        } catch (IOException e) {
            System.err.println("Failed to delete: " + path);
        }
    });

// Safe deletion with error handling
try {
    Files.delete(Paths.get("file.txt"));
} catch (NoSuchFileException e) {
    System.err.println("File not found");
} catch (DirectoryNotEmptyException e) {
    System.err.println("Directory not empty");
} catch (IOException e) {
    System.err.println("I/O error");
}
```

### 10. Best Practices

File I/O guidelines:

```java
// ✅ Always use try-with-resources
try (BufferedReader reader = new BufferedReader(new FileReader("file.txt"))) {
    // Read file
}

// ✅ Use buffering for performance
try (BufferedInputStream bis = new BufferedInputStream(
        new FileInputStream("file.bin"))) {
    // Read file
}

// ✅ Specify encoding explicitly
Files.readAllLines(Paths.get("file.txt"), StandardCharsets.UTF_8);

// ✅ Use NIO for large files
Files.walk(Paths.get("."))
    .filter(Files::isRegularFile)
    .forEach(System.out::println);

// ✅ Handle exceptions properly
try {
    Files.readAllBytes(Paths.get("file.txt"));
} catch (IOException e) {
    System.err.println("Error reading file: " + e.getMessage());
}
```

---

## 💻 Step-by-Step Coding Tasks

### Task 1: Read and Write Files

**Objective**: Implement file reading and writing operations

**Acceptance Criteria**:
- [ ] File reading implemented
- [ ] File writing implemented
- [ ] Buffering used
- [ ] Error handling proper
- [ ] Code compiles without errors

**Instructions**:
1. Create file reading method
2. Create file writing method
3. Implement buffering
4. Handle exceptions
5. Test with sample files

### Task 2: Directory Operations

**Objective**: Implement directory traversal and operations

**Acceptance Criteria**:
- [ ] Directory listing
- [ ] File filtering
- [ ] Recursive traversal
- [ ] File attributes
- [ ] Error handling

**Instructions**:
1. List directory contents
2. Filter files by extension
3. Traverse recursively
4. Get file attributes
5. Test with sample directories

### Task 3: File Management

**Objective**: Implement file management operations

**Acceptance Criteria**:
- [ ] File copying
- [ ] File moving
- [ ] File deletion
- [ ] Safe operations
- [ ] Error handling

**Instructions**:
1. Implement file copy
2. Implement file move
3. Implement file delete
4. Add error handling
5. Test operations

---

## 🎨 Mini-Project: File Management System

### Project Overview

**Description**: Create a comprehensive file management system with operations and monitoring.

**Real-World Application**: File managers, backup systems, data processing tools.

**Learning Value**: Master file I/O and directory operations.

### Project Structure

```
file-management-system/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/learning/
│   │           ├── FileManager.java
│   │           ├── DirectoryWalker.java
│   │           ├── FileStats.java
│   │           └── Main.java
│   └── test/
│       └── java/
│           └── com/learning/
│               └── FileManagerTest.java
├── pom.xml
└── README.md
```

### Implementation Guide

#### Step 1: Create FileManager Class

```java
package com.learning;

import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;

/**
 * Manages file operations.
 */
public class FileManager {
    
    /**
     * Read file content.
     */
    public String readFile(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)), 
            StandardCharsets.UTF_8);
    }
    
    /**
     * Write file content.
     */
    public void writeFile(String filePath, String content) throws IOException {
        Files.write(Paths.get(filePath), content.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * Append to file.
     */
    public void appendFile(String filePath, String content) throws IOException {
        Files.write(Paths.get(filePath), content.getBytes(StandardCharsets.UTF_8),
            StandardOpenOption.APPEND);
    }
    
    /**
     * Copy file.
     */
    public void copyFile(String source, String destination) throws IOException {
        Files.copy(Paths.get(source), Paths.get(destination),
            StandardCopyOption.REPLACE_EXISTING);
    }
    
    /**
     * Move file.
     */
    public void moveFile(String source, String destination) throws IOException {
        Files.move(Paths.get(source), Paths.get(destination),
            StandardCopyOption.REPLACE_EXISTING);
    }
    
    /**
     * Delete file.
     */
    public void deleteFile(String filePath) throws IOException {
        Files.deleteIfExists(Paths.get(filePath));
    }
    
    /**
     * Check if file exists.
     */
    public boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }
    
    /**
     * Get file size.
     */
    public long getFileSize(String filePath) throws IOException {
        return Files.size(Paths.get(filePath));
    }
}
```

#### Step 2: Create DirectoryWalker Class

```java
package com.learning;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * Walks directory tree.
 */
public class DirectoryWalker {
    
    /**
     * List files in directory.
     */
    public List<String> listFiles(String dirPath) throws IOException {
        List<String> files = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(dirPath))) {
            for (Path path : stream) {
                files.add(path.getFileName().toString());
            }
        }
        return files;
    }
    
    /**
     * List files with extension filter.
     */
    public List<String> listFilesByExtension(String dirPath, String extension) throws IOException {
        List<String> files = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(
                Paths.get(dirPath), "*." + extension)) {
            for (Path path : stream) {
                files.add(path.getFileName().toString());
            }
        }
        return files;
    }
    
    /**
     * Walk directory tree.
     */
    public List<String> walkDirectory(String dirPath) throws IOException {
        List<String> files = new ArrayList<>();
        Files.walk(Paths.get(dirPath))
            .filter(Files::isRegularFile)
            .forEach(path -> files.add(path.toString()));
        return files;
    }
    
    /**
     * Count files in directory.
     */
    public long countFiles(String dirPath) throws IOException {
        return Files.walk(Paths.get(dirPath))
            .filter(Files::isRegularFile)
            .count();
    }
    
    /**
     * Get total size of directory.
     */
    public long getDirectorySize(String dirPath) throws IOException {
        return Files.walk(Paths.get(dirPath))
            .filter(Files::isRegularFile)
            .mapToLong(path -> {
                try {
                    return Files.size(path);
                } catch (IOException e) {
                    return 0;
                }
            })
            .sum();
    }
}
```

#### Step 3: Create FileStats Class

```java
package com.learning;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Tracks file statistics.
 */
public class FileStats {
    private AtomicLong filesRead = new AtomicLong(0);
    private AtomicLong filesWritten = new AtomicLong(0);
    private AtomicLong filesCopied = new AtomicLong(0);
    private AtomicLong filesDeleted = new AtomicLong(0);
    
    /**
     * Record file read.
     */
    public void recordRead() {
        filesRead.incrementAndGet();
    }
    
    /**
     * Record file write.
     */
    public void recordWrite() {
        filesWritten.incrementAndGet();
    }
    
    /**
     * Record file copy.
     */
    public void recordCopy() {
        filesCopied.incrementAndGet();
    }
    
    /**
     * Record file delete.
     */
    public void recordDelete() {
        filesDeleted.incrementAndGet();
    }
    
    /**
     * Display statistics.
     */
    public void displayStats() {
        System.out.println("\n========== FILE STATS ==========");
        System.out.println("Files Read: " + filesRead.get());
        System.out.println("Files Written: " + filesWritten.get());
        System.out.println("Files Copied: " + filesCopied.get());
        System.out.println("Files Deleted: " + filesDeleted.get());
        System.out.println("================================\n");
    }
}
```

#### Step 4: Create Main Class

```java
package com.learning;

import java.io.IOException;

/**
 * Main entry point for File Management System.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        FileManager fm = new FileManager();
        DirectoryWalker dw = new DirectoryWalker();
        FileStats stats = new FileStats();
        
        // Create test file
        fm.writeFile("test.txt", "Hello, World!");
        stats.recordWrite();
        
        // Read file
        String content = fm.readFile("test.txt");
        System.out.println("Content: " + content);
        stats.recordRead();
        
        // Copy file
        fm.copyFile("test.txt", "test_copy.txt");
        stats.recordCopy();
        
        // List files
        System.out.println("\nFiles in current directory:");
        dw.listFiles(".").forEach(System.out::println);
        
        // Count files
        long fileCount = dw.countFiles(".");
        System.out.println("\nTotal files: " + fileCount);
        
        // Display statistics
        stats.displayStats();
        
        // Cleanup
        fm.deleteFile("test.txt");
        fm.deleteFile("test_copy.txt");
        stats.recordDelete();
        stats.recordDelete();
    }
}
```

#### Step 5: Create Unit Tests

```java
package com.learning;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FileManager.
 */
public class FileManagerTest {
    
    private FileManager fileManager;
    
    @BeforeEach
    void setUp() {
        fileManager = new FileManager();
    }
    
    @Test
    void testWriteAndRead(@TempDir Path tempDir) throws IOException {
        String filePath = tempDir.resolve("test.txt").toString();
        String content = "Test content";
        
        fileManager.writeFile(filePath, content);
        String readContent = fileManager.readFile(filePath);
        
        assertEquals(content, readContent);
    }
    
    @Test
    void testFileExists(@TempDir Path tempDir) throws IOException {
        String filePath = tempDir.resolve("test.txt").toString();
        fileManager.writeFile(filePath, "content");
        
        assertTrue(fileManager.fileExists(filePath));
    }
    
    @Test
    void testCopyFile(@TempDir Path tempDir) throws IOException {
        String source = tempDir.resolve("source.txt").toString();
        String dest = tempDir.resolve("dest.txt").toString();
        
        fileManager.writeFile(source, "content");
        fileManager.copyFile(source, dest);
        
        assertTrue(fileManager.fileExists(dest));
    }
    
    @Test
    void testDeleteFile(@TempDir Path tempDir) throws IOException {
        String filePath = tempDir.resolve("test.txt").toString();
        fileManager.writeFile(filePath, "content");
        fileManager.deleteFile(filePath);
        
        assertFalse(fileManager.fileExists(filePath));
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

### Exercise 1: Batch File Processing

**Objective**: Process multiple files in batch

**Task Description**:
Create system to process multiple files with transformations

**Acceptance Criteria**:
- [ ] Batch processing
- [ ] File transformations
- [ ] Error handling
- [ ] Progress tracking
- [ ] Tests pass

### Exercise 2: File Backup System

**Objective**: Create backup system for files

**Task Description**:
Implement incremental backup with versioning

**Acceptance Criteria**:
- [ ] Backup creation
- [ ] Versioning
- [ ] Restoration
- [ ] Compression
- [ ] Tests pass

### Exercise 3: Log File Analyzer

**Objective**: Analyze log files

**Task Description**:
Create system to parse and analyze log files

**Acceptance Criteria**:
- [ ] Log parsing
- [ ] Pattern matching
- [ ] Statistics
- [ ] Reporting
- [ ] Tests pass

---

## 🧪 Quiz

### Question 1: What is buffering?

A) Reading one byte at a time  
B) Reading data in chunks  
C) Writing data immediately  
D) Deleting files  

**Answer**: B) Reading data in chunks

### Question 2: What is try-with-resources?

A) A way to handle exceptions  
B) A way to automatically close resources  
C) A way to read files  
D) A way to write files  

**Answer**: B) A way to automatically close resources

### Question 3: What is character encoding?

A) Encrypting text  
B) Mapping characters to bytes  
C) Compressing files  
D) Deleting files  

**Answer**: B) Mapping characters to bytes

### Question 4: How do you copy a file?

A) Files.copy()  
B) Files.move()  
C) Files.delete()  
D) Files.read()  

**Answer**: A) Files.copy()

### Question 5: What is directory traversal?

A) Moving files  
B) Deleting files  
C) Walking through directory tree  
D) Reading files  

**Answer**: C) Walking through directory tree

---

## 🚀 Advanced Challenge

### Challenge: Complete File Processing Framework

**Difficulty**: Advanced

**Objective**: Build comprehensive file processing framework

**Requirements**:
- [ ] Multiple file formats
- [ ] Batch processing
- [ ] Error recovery
- [ ] Progress tracking
- [ ] Performance optimization
- [ ] Monitoring

---

## 🏆 Best Practices

### File I/O

1. **Always Use Try-With-Resources**
   - Automatic resource closing
   - Exception safety
   - Clean code

2. **Use Buffering**
   - Improves performance
   - Reduces system calls
   - Better throughput

3. **Handle Encoding**
   - Specify explicitly
   - Use UTF-8 by default
   - Support multiple encodings

---

## 🔗 Next Steps

**Next Lab**: [Lab 17: NIO](../17-nio/README.md)

---

## ✅ Completion Checklist

- [ ] Completed all coding tasks
- [ ] Built file management system
- [ ] Solved all exercises
- [ ] Passed quiz (80%+)
- [ ] Attempted advanced challenge
- [ ] Reviewed best practices

---

**Congratulations on completing Lab 16! 🎉**

You've mastered file I/O operations. Ready for NIO? Move on to [Lab 17: NIO](../17-nio/README.md).