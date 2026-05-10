# Java Files and I/O - Implementation Guide

## Module Overview

This module covers Java file I/O operations including reading/writing files, streams, buffers, serialization, and NIO.2 features.

---

## Part 1: From-Scratch Java Implementation

### 1.1 Custom File Operations

```java
package com.learning.files.implementation;

import java.io.*;
import java.nio.*;
import java.nio.file.*;

public class FileOperations {
    
    // Read file with FileInputStream
    public String readFileOldWay(String path) throws IOException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
            StringBuilder content = new StringBuilder();
            int ch;
            while ((ch = fis.read()) != -1) {
                content.append((char) ch);
            }
            return content.toString();
        } finally {
            if (fis != null) fis.close();
        }
    }
    
    // Read file with BufferedReader
    public String readFileBuffered(String path) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new FileReader(path))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();
        }
    }
    
    // Read file using NIO Files (Java 7+)
    public String readFileNIO(String path) throws IOException {
        return Files.readString(Path.of(path));
    }
    
    // Read file as lines
    public List<String> readLines(String path) throws IOException {
        return Files.readAllLines(Path.of(path));
    }
    
    // Read bytes from file
    public byte[] readBytes(String path) throws IOException {
        return Files.readAllBytes(Path.of(path));
    }
    
    // Write file with FileOutputStream
    public void writeFileOldWay(String path, String content) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(content.getBytes());
        }
    }
    
    // Write file with BufferedWriter
    public void writeFileBuffered(String path, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(path))) {
            writer.write(content);
        }
    }
    
    // Write file using NIO Files
    public void writeFileNIO(String path, String content) throws IOException {
        Files.writeString(Path.of(path), content);
    }
    
    // Write lines
    public void writeLines(String path, List<String> lines) throws IOException {
        Files.write(Path.of(path), lines);
    }
    
    // Append to file
    public void appendToFile(String path, String content) throws IOException {
        Files.writeString(Path.of(path), content + System.lineSeparator(),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
}
```

### 1.2 Stream-Based Operations

```java
package com.learning.files.implementation;

import java.io.*;
import java.util.zip.*;

public class StreamOperations {
    
    // Buffered stream copy
    public void copyWithBuffer(InputStream in, OutputStream out) 
            throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(in);
             BufferedOutputStream bos = new BufferedOutputStream(out)) {
            
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
        }
    }
    
    // Data input/output streams
    public void writeDataTypes(String path) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(
                new FileOutputStream(path))) {
            dos.writeInt(42);
            dos.writeDouble(3.14);
            dos.writeBoolean(true);
            dos.writeUTF("Hello");
        }
    }
    
    public void readDataTypes(String path) throws IOException {
        try (DataInputStream dis = new DataInputStream(
                new FileInputStream(path))) {
            int i = dis.readInt();
            double d = dis.readDouble();
            boolean b = dis.readBoolean();
            String s = dis.readUTF();
            System.out.println(i + " " + d + " " + b + " " + s);
        }
    }
    
    // Object serialization
    public void serializeObject(String path, Object obj) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(path))) {
            oos.writeObject(obj);
        }
    }
    
    public Object deserializeObject(String path) 
            throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(path))) {
            return ois.readObject();
        }
    }
    
    // GZIP compression
    public void compressFile(String source, String target) throws IOException {
        try (FileInputStream fis = new FileInputStream(source);
             FileOutputStream fos = new FileOutputStream(target);
             GZIPOutputStream gzos = new GZIPOutputStream(fos)) {
            
            byte[] buffer = new byte[4096];
            int len;
            while ((len = fis.read(buffer)) > 0) {
                gzos.write(buffer, 0, len);
            }
        }
    }
    
    public void decompressFile(String source, String target) throws IOException {
        try (FileInputStream fis = new FileInputStream(source);
             GZIPInputStream gzis = new GZIPInputStream(fis);
             FileOutputStream fos = new FileOutputStream(target)) {
            
            byte[] buffer = new byte[4096];
            int len;
            while ((len = gzis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
        }
    }
    
    // ZIP operations
    public void createZip(String sourceDir, String zipFile) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(
                new FileOutputStream(zipFile))) {
            
            Files.walk(Path.of(sourceDir))
                .filter(Files::isRegularFile)
                .forEach(file -> {
                    try {
                        ZipEntry entry = new ZipEntry(
                                sourceDir.relativize(file).toString());
                        zos.putNextEntry(entry);
                        Files.copy(file, zos);
                        zos.closeEntry();
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
        }
    }
    
    public void extractZip(String zipFile, String targetDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(
                new FileInputStream(zipFile))) {
            
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path targetPath = Path.of(targetDir, entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(targetPath);
                } else {
                    Files.createDirectories(targetPath.getParent());
                    Files.copy(zis, targetPath);
                }
                zis.closeEntry();
            }
        }
    }
}
```

### 1.3 NIO.2 File Operations

```java
package com.learning.files.implementation;

import java.nio.file.*;
import java.nio.file.attribute.*;

public class NIO2Operations {
    
    // File metadata
    public void getFileAttributes(String path) throws IOException {
        Path file = Paths.get(path);
        
        System.out.println("Exists: " + Files.exists(file));
        System.out.println("Is Directory: " + Files.isDirectory(file));
        System.out.println("Is Regular File: " + Files.isRegularFile(file));
        System.out.println("Size: " + Files.size(file));
        System.out.println("Readable: " + Files.isReadable(file));
        System.out.println("Writable: " + Files.isWritable(file));
    }
    
    // Get detailed attributes
    public void getDetailedAttributes(String path) throws IOException {
        BasicFileAttributes attrs = Files.readAttributes(
                Paths.get(path), BasicFileAttributes.class);
        
        System.out.println("Creation: " + attrs.creationTime());
        System.out.println("Last Modified: " + attrs.lastModifiedTime());
        System.out.println("Size: " + attrs.size());
        System.out.println("Is Directory: " + attrs.isDirectory());
        System.out.println("Is Regular: " + attrs.isRegularFile());
    }
    
    // Walk file tree
    public void walkDirectory(String startPath) throws IOException {
        Files.walk(Paths.get(startPath))
            .forEach(path -> {
                System.out.println(path);
            });
    }
    
    // Find files
    public void findFiles(String directory, String pattern) throws IOException {
        Files.find(Paths.get(directory), Integer.MAX_VALUE, 
                (path, attrs) -> path.toString().contains(pattern))
            .forEach(System.out::println);
    }
    
    // Directory watching
    public void watchDirectory(String path) throws IOException {
        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path dir = Paths.get(path);
        
        dir.register(watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE);
        
        while (true) {
            WatchKey key = watchService.take();
            for (WatchEvent<?> event : key.pollEvents()) {
                System.out.println(event.kind() + ": " + event.context());
            }
            key.reset();
        }
    }
    
    // Copy file
    public void copyFile(String source, String target) throws IOException {
        Files.copy(Paths.get(source), Paths.get(target),
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.COPY_ATTRIBUTES);
    }
    
    // Move file
    public void moveFile(String source, String target) throws IOException {
        Files.move(Paths.get(source), Paths.get(target),
                StandardCopyOption.REPLACE_EXISTING);
    }
    
    // Create temp files
    public void createTempFiles() throws IOException {
        Path tempFile = Files.createTempFile("prefix", ".suffix");
        Path tempDir = Files.createTempDirectory("tempDir");
        
        System.out.println("Temp file: " + tempFile);
        System.out.println("Temp dir: " + tempDir);
        
        Files.deleteIfExists(tempFile);
        Files.deleteIfExists(tempDir);
    }
    
    // File permissions (Unix-like systems)
    public void setPermissions(String path) throws IOException {
        Path file = Paths.get(path);
        Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxr-x---");
        Files.setPosixFilePermissions(file, perms);
    }
}
```

---

## Part 2: Production Variant with Spring Boot

### 2.1 File Service

```java
package com.learning.files.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.file.*;

@Service
public class FileStorageService {
    
    private final Path storageLocation;
    
    public FileStorageService() throws IOException {
        this.storageLocation = Paths.get("uploads");
        Files.createDirectories(storageLocation);
    }
    
    public String storeFile(MultipartFile file) throws IOException {
        String filename = System.currentTimeMillis() + "_" + 
                file.getOriginalFilename();
        Path targetPath = storageLocation.resolve(filename);
        
        Files.copy(file.getInputStream(), targetPath,
                StandardCopyOption.REPLACE_EXISTING);
        
        return filename;
    }
    
    public byte[] loadFileAsBytes(String filename) throws IOException {
        Path filePath = storageLocation.resolve(filename);
        return Files.readAllBytes(filePath);
    }
    
    public InputStream loadFileAsStream(String filename) throws IOException {
        Path filePath = storageLocation.resolve(filename);
        return new FileInputStream(filePath.toFile());
    }
    
    public boolean deleteFile(String filename) throws IOException {
        Path filePath = storageLocation.resolve(filename);
        return Files.deleteIfExists(filePath);
    }
    
    public List<String> listFiles() throws IOException {
        return Files.list(storageLocation)
                .map(Path::getFileName)
                .map(Path::toString)
                .toList();
    }
}
```

### 2.2 File Controller

```java
package com.learning.files.controller;

import com.learning.files.service.FileStorageService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api/files")
public class FileController {
    
    private final FileStorageService storageService;
    
    public FileController(FileStorageService storageService) {
        this.storageService = storageService;
    }
    
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") 
            MultipartFile file) throws IOException {
        
        String filename = storageService.storeFile(file);
        return ResponseEntity.ok(filename);
    }
    
    @GetMapping("/{filename}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String filename) 
            throws IOException {
        
        byte[] data = storageService.loadFileAsBytes(filename);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(data);
    }
    
    @DeleteMapping("/{filename}")
    public ResponseEntity<Void> deleteFile(@PathVariable String filename) 
            throws IOException {
        
        storageService.deleteFile(filename);
        return ResponseEntity.noContent().build();
    }
}
```

---

## Part 3: Step-by-Step Code Explanation

### 3.1 File Reading

**Step 1: FileInputStream**
- Low-level byte reading
- Need to handle conversion manually

**Step 2: BufferedReader**
- Buffered character reading
- ReadLine() for lines

**Step 3: NIO Files**
- Modern API, simpler usage
- readString(), readAllLines()

### 3.2 File Writing

**Step 1: FileOutputStream**
- Raw bytes, manual encoding

**Step 2: BufferedWriter**
- Buffered for efficiency

**Step 3: NIO Files**
- writeString() for modern usage

### 3.3 Streams

**Step 1: Buffered Streams**
- Wrap unbuffered for performance

**Step 2: Data Streams**
- Binary format for primitives

**Step 3: Object Streams**
- Serialization for objects

---

## Part 4: Key Concepts Demonstrated

| Concept | Implementation | Usage |
|---------|---------------|-------|
| **File I/O** | FileReader/Writer | Simple text files |
| **Buffered I/O** | Buffered streams | Performance |
| **NIO.2** | Files utility | Modern file ops |
| **Serialization** | Object streams | Object persistence |
| **Compression** | GZIP/ZIP | Data compression |
| **File Watching** | WatchService | Directory monitoring |
| **Path** | Path API | File path operations |

---

## Key Takeaways

1. Always use try-with-resources for file operations
2. Prefer NIO.2 for modern file operations
3. Use buffered streams for better performance
4. Handle encoding explicitly when needed
5. Be careful with path handling across platforms