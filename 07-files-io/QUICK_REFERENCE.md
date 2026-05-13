# Quick Reference: Files & I/O

<div align="center">

![Module](https://img.shields.io/badge/Module-07-blue?style=for-the-badge)
![Topic](https://img.shields.io/badge/Topic-Files%20and%20IO-green?style=for-the-badge)

**Quick lookup guide for Java file operations and NIO.2**

</div>

---

## 📋 File I/O Overview

### Legacy I/O (java.io)
| Class | Purpose |
|-------|---------|
| FileInputStream/OutputStream | Byte streams |
| FileReader/Writer | Character streams |
| BufferedReader/Writer | Buffered streams |
| ObjectInputStream/OutputStream | Serialization |
| DataInputStream/OutputStream | Primitive data |

### NIO.2 (java.nio.file)
| Class | Purpose |
|-------|---------|
| Path | Abstract file path |
| Paths | Path factory methods |
| Files | File utility methods |
| FileSystem | File system abstraction |
| DirectoryStream | Directory iteration |

---

## 🔑 Key Concepts

### Reading Files
```java
// Old way - FileInputStream
FileInputStream fis = new FileInputStream("file.txt");
int ch;
while ((ch = fis.read()) != -1) {
    // Process byte
}
fis.close();

// Modern way - BufferedReader
try (BufferedReader reader = new BufferedReader(
        new FileReader("file.txt"))) {
    String line;
    while ((line = reader.readLine()) != null) {
        // Process line
    }
}

// NIO.2 - Files.readAllLines
List<String> lines = Files.readAllLines(Paths.get("file.txt"));
```

### Writing Files
```java
// Old way
FileWriter writer = new FileWriter("output.txt");
writer.write("Hello");
writer.close();

// Modern way - BufferedWriter
try (BufferedWriter writer = new BufferedWriter(
        new FileWriter("output.txt"))) {
    writer.write("Hello");
}

// NIO.2
Files.write(Paths.get("output.txt"), "Hello".getBytes());
Files.writeString(Paths.get("output.txt"), "Hello");
```

### NIO.2 Path Operations
```java
Path path = Paths.get("/home/user/file.txt");

// Path operations
path.getFileName();       // file.txt
path.getParent();        // /home/user
path.getRoot();          // /
path.isAbsolute();       // true
path.toAbsolutePath();   // /home/user/file.txt
path.resolve("subdir");  // /home/user/subdir
path.normalize();       // Normalize path
path.toRealPath();       // Resolve symlinks
```

### NIO.2 Files Utility
```java
// File existence and attributes
Files.exists(path);
Files.isRegularFile(path);
Files.isDirectory(path);
Files.isReadable(path);
Files.isWritable(path);
Files.size(path);
Files.getLastModifiedTime(path);

// File operations
Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
Files.delete(path);
Files.createDirectory(path);
Files.createDirectories(path);

// Reading/Writing
Files.readAllBytes(path);
Files.readAllLines(path);
Files.write(path, bytes);
Files.writeString(path, content);
```

### Directory Operations
```java
// List directory contents
Files.list(dir).forEach(System.out::println);

// Walk directory tree
Files.walk(dir)
    .filter(Files::isRegularFile)
    .forEach(System.out::println);

// Find files
PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*.java");
Files.find(dir, Integer.MAX_VALUE, 
    (path, attrs) -> matcher.matches(path.getFileName()));
```

### File Attributes
```java
// Basic attributes
BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
attrs.creationTime();
attrs.lastModifiedTime();
attrs.size();
attrs.isDirectory();

// POSIX attributes (Unix)
PosixFileAttributes posix = Files.readAttributes(path, PosixFileAttributes.class);
posix.permissions();

// DOS attributes (Windows)
DosFileAttributes dos = Files.readAttributes(path, DosFileAttributes.class);
dos.hidden();
```

### Watch Service (File Monitoring)
```java
WatchService watchService = FileSystems.getDefault().newWatchService();
Path dir = Paths.get("/watch/folder");
dir.register(watchService,
    StandardWatchEventKinds.ENTRY_CREATE,
    StandardWatchEventKinds.ENTRY_MODIFY,
    StandardWatchEventKinds.ENTRY_DELETE);

while (true) {
    WatchKey key = watchService.take();
    for (WatchEvent<?> event : key.pollEvents()) {
        Path changed = (Path) event.context();
        System.out.println(event.kind() + ": " + changed);
    }
    key.reset();
}
```

---

## 💻 Code Snippets

### File Copy
```java
// Simple copy
Files.copy(source, target);

// With options
Files.copy(source, target, 
    StandardCopyOption.REPLACE_EXISTING,
    StandardCopyOption.COPY_ATTRIBUTES);
```

### Reading Large Files
```java
// Line by line with try-with-resources
try (Stream<String> lines = Files.lines(path)) {
    lines.filter(line -> line.contains("keyword"))
         .forEach(System.out::println);
}

// Buffered reading with buffer size
try (BufferedReader reader = Files.newBufferedReader(path)) {
    // Process
}
```

### Writing with Options
```java
// Append to file
Files.write(path, content.getBytes(), 
    StandardOpenOption.CREATE,
    StandardOpenOption.APPEND);

// New file only
Files.write(path, content.getBytes(), 
    StandardOpenOption.CREATE_NEW);
```

### temp File Operations
```java
Path tempFile = Files.createTempFile("prefix", ".suffix");
Path tempDir = Files.createTempDirectory("prefix");
```

---

## 📊 Best Practices

### ✅ DO
- Use try-with-resources for all I/O
- Use Buffered streams for better performance
- Use NIO.2 for modern file operations
- Close streams in finally or try-with-resources
- Use Path instead of File

### ❌ DON'T
- Use File.exists() before operations (TOCTOU)
- Forget to close streams
- Use absolute paths - use relative
- Hardcode file separators

---

## 🎯 Common Patterns

### Pattern 1: Safe File Read
```java
public String readFile(String path) throws IOException {
    return Files.readString(Paths.get(path));
}
```

### Pattern 2: File Processing Pipeline
```java
Files.walk(inputDir)
    .filter(Files::isRegularFile)
    .filter(p -> p.toString().endsWith(".txt"))
    .map(this::processFile)
    .forEach(output -> writeOutput(output));
```

### Pattern 3: Watch Directory
```java
public void watchDirectory(Path dir) throws IOException {
    WatchService ws = FileSystems.getDefault().newWatchService();
    dir.register(ws, ENTRY_CREATE, ENTRY_MODIFY);
    // Process events in separate thread
}
```

---

## 🔍 Checklist

### File Operations
- [ ] Use try-with-resources
- [ ] Handle IOException
- [ ] Choose correct stream type
- [ ] Consider buffering
- [ ] Close resources properly

### NIO.2 Operations
- [ ] Use Path instead of File
- [ ] Use Files utility class
- [ ] Handle symbolic links
- [ ] Consider file attributes

---

<div align="center">

[Back to Module →](./IMPLEMENTATION.md)

[Take Quizzes →](./PROJECTS.md)

</div>