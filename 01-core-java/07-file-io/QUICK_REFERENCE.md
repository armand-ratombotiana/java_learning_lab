# 📌 File I/O & NIO - Quick Reference Sheet

Use this sheet for quick lookups while coding or studying.

---

## 📁 File Operations (java.io)

### Reading Files
```java
// Character streams
FileReader reader = new FileReader("file.txt");
BufferedReader br = new BufferedReader(reader);

// With try-with-resources
try (BufferedReader br = new BufferedReader(new FileReader("file.txt"))) {
    String line;
    while ((line = br.readLine()) != null) { process(line); }
}

// Byte streams
FileInputStream fis = new FileInputStream("file.bin");
BufferedInputStream bis = new BufferedInputStream(fis);
```

### Writing Files
```java
try (PrintWriter pw = new PrintWriter(new FileWriter("output.txt"))) {
    pw.println("Hello");
    pw.write("World");
}

try (BufferedWriter bw = new BufferedWriter(new FileWriter("file.txt"))) {
    bw.write("Content");
    bw.newLine();
}
```

---

## 📂 NIO Basics (java.nio)

### Path & Files
```java
Path path = Paths.get("folder", "file.txt");
Path absolute = path.toAbsolutePath();
String filename = path.getFileName();
Path parent = path.getParent();

// File utility methods
boolean exists = Files.exists(path);
boolean readable = Files.isReadable(path);
boolean directory = Files.isDirectory(path);
long size = Files.size(path);
```

### Reading Files (NIO)
```java
// Single read
String content = Files.readString(path);

// Lines
List<String> lines = Files.readAllLines(path);

// Stream
Files.lines(path).forEach(System.out::println);

// Bytes
byte[] data = Files.readAllBytes(path);
```

### Writing Files (NIO)
```java
Files.writeString(path, "content");
Files.write(path, "content".getBytes());
Files.writeLines(path, listOfStrings);
```

---

## 🔄 Buffered Streams

| Stream | Purpose |
|--------|---------|
| `BufferedReader` | Read text line-by-line efficiently |
| `BufferedWriter` | Write text efficiently |
| `BufferedInputStream` | Read bytes efficiently |
| `BufferedOutputStream` | Write bytes efficiently |

```java
// Buffer sizes: default 8192 chars/bytes
BufferedReader br = new BufferedReader(new FileReader(f), 16384);
```

---

## 🗂️ Directory Operations

```java
// List files
String[] files = new File("folder").list();
Path dir = Paths.get("folder");
DirectoryStream<Path> stream = Files.newDirectoryStream(dir);

// Create
Files.createDirectory(path);
Files.createDirectories(path);  // Creates parent dirs

// Delete
Files.delete(path);           // Throws if not exists
Files.deleteIfExists(path);   // Safe delete

// Walk tree
Files.walk(path).forEach(System.out::println);
Files.walk(path, maxDepth).filter(...).collect(...);
```

---

## 📦 Working with Paths

```java
Path p = Paths.get("/home/user/file.txt");

// Modify
Path resolved = p.resolve("subdir");      // /home/user/file.txt/subdir
Path normalized = p.normalize();           // Resolve . and ..
Path relativized = p1.relativize(p2);      // Relative path

// Inspect
p.getFileName();
p.getParent();
p.getRoot();
p.isAbsolute();
```

---

## 🔐 File Attributes

```java
// Basic attributes
BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
attrs.creationTime();
attrs.lastModifiedTime();
attrs.size();
attrs.isDirectory();

// Permissions (PosixFileAttributes on Unix)
PosixFileAttributes perms = Files.readAttributes(path, PosixFileAttributes.class);
Set<PosixFilePermission> perms = perms.permissions();

// View specific attributes
DosFileAttributes dos = Files.readAttributes(path, DosFileAttributes.class);
dos.isHidden();
dos.isReadOnly();
```

---

## 🔄 Byte vs Character Encoding

```java
// Specify encoding
Files.readString(path, StandardCharsets.UTF_8);
new FileReader(f, StandardCharsets.UTF_8);

// Common encodings
StandardCharsets.UTF_8
StandardCharsets.US_ASCII
StandardCharsets.ISO_8859_1
StandardCharsets.UTF_16
```

---

## ⚠️ Common Pitfalls

1. **Forgetting to close streams** - Use try-with-resources
2. **Wrong encoding** - Specify charset explicitly
3. **Path separators** - Use `System.getProperty("file.separator")` or Paths
4. **Large files** - Don't readAllBytes, use streaming
5. **Not handling exceptions** - FileNotFoundException, IOException

---

## 📈 Performance Tips

| Practice | Benefit |
|----------|---------|
| Use Buffered streams | Reduces system calls |
| Use NIO for large files | Channels + buffers |
| Use memory-mapped files | OS-level caching |
| Batch I/O operations | Reduce overhead |
| Close resources promptly | Release handles |

---

## 🔧 Serialization

```java
// Serialize
ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("obj.ser"));
out.writeObject(myObject);

// Deserialize
ObjectInputStream in = new ObjectInputStream(new FileInputStream("obj.ser"));
MyObject obj = (MyObject) in.readObject();

// Mark class as serializable
class MyObject implements Serializable {
    private static final long serialVersionUID = 1L;
}
```

---

**Remember**: "Close resources, specify encoding, handle exceptions"