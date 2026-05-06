# Exercises: File I/O

<div align="center">

![Module](https://img.shields.io/badge/Module-07-blue?style=for-the-badge)
![Exercises](https://img.shields.io/badge/Exercises-25-green?style=for-the-badge)
![Difficulty](https://img.shields.io/badge/Difficulty-Easy%20to%20Hard-orange?style=for-the-badge)

**25 comprehensive exercises for File I/O module**

</div>

---

## 📚 Table of Contents

1. [Easy Exercises (1-8)](#easy-exercises-1-8)
2. [Medium Exercises (9-16)](#medium-exercises-9-16)
3. [Hard Exercises (17-21)](#hard-exercises-17-21)
4. [Interview Exercises (22-25)](#interview-exercises-22-25)

---

## 🟢 Easy Exercises (1-8)

### Exercise 1: Reading Text Files
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** FileReader, BufferedReader, reading files

**Pedagogic Objective:**
Understand basic file reading operations.

**Problem:**
Read a text file line by line and print contents.

**Complete Solution:**
```java
import java.io.*;

public class ReadingTextFiles {
    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(
                new FileReader("test.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
}
```

**Key Concepts:**
- FileReader for character input
- BufferedReader for efficient reading
- Try-with-resources for automatic closing
- IOException handling

---

### Exercise 2: Writing Text Files
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** FileWriter, BufferedWriter, writing files

**Pedagogic Objective:**
Understand basic file writing operations.

**Complete Solution:**
```java
import java.io.*;

public class WritingTextFiles {
    public static void main(String[] args) {
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter("output.txt"))) {
            writer.write("Hello, World!");
            writer.newLine();
            writer.write("This is a test file.");
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }
}
```

**Key Concepts:**
- FileWriter for character output
- BufferedWriter for efficient writing
- newLine() for line breaks
- Automatic resource closing

---

### Exercise 3: Reading Binary Files
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** FileInputStream, reading bytes

**Pedagogic Objective:**
Understand reading binary data from files.

**Complete Solution:**
```java
import java.io.*;

public class ReadingBinaryFiles {
    public static void main(String[] args) {
        try (FileInputStream fis = new FileInputStream("binary.dat")) {
            int byteData;
            while ((byteData = fis.read()) != -1) {
                System.out.print(byteData + " ");
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
}
```

**Key Concepts:**
- FileInputStream for byte input
- read() returns -1 at EOF
- Binary data handling
- Exception handling

---

### Exercise 4: Writing Binary Files
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** FileOutputStream, writing bytes

**Pedagogic Objective:**
Understand writing binary data to files.

**Complete Solution:**
```java
import java.io.*;

public class WritingBinaryFiles {
    public static void main(String[] args) {
        try (FileOutputStream fos = new FileOutputStream("binary.dat")) {
            byte[] data = {65, 66, 67, 68, 69}; // A, B, C, D, E
            fos.write(data);
            System.out.println("Binary data written successfully");
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }
}
```

**Key Concepts:**
- FileOutputStream for byte output
- write() for byte arrays
- Binary data writing
- Resource management

---

### Exercise 5: File Operations
**Difficulty:** Easy  
**Time:** 20 minutes  
**Topics:** File class, file operations, checking existence

**Pedagogic Objective:**
Understand File class for file operations.

**Complete Solution:**
```java
import java.io.*;

public class FileOperations {
    public static void main(String[] args) {
        File file = new File("test.txt");
        
        // Check if file exists
        if (file.exists()) {
            System.out.println("File exists");
            System.out.println("Is file: " + file.isFile());
            System.out.println("Is directory: " + file.isDirectory());
            System.out.println("File size: " + file.length() + " bytes");
            System.out.println("Can read: " + file.canRead());
            System.out.println("Can write: " + file.canWrite());
        } else {
            System.out.println("File does not exist");
        }
    }
}
```

**Key Concepts:**
- File class for file metadata
- exists(), isFile(), isDirectory()
- length() for file size
- canRead(), canWrite() for permissions

---

### Exercise 6: Directory Operations
**Difficulty:** Easy  
**Time:** 20 minutes  
**Topics:** Directory handling, listing files

**Pedagogic Objective:**
Understand directory operations.

**Complete Solution:**
```java
import java.io.*;

public class DirectoryOperations {
    public static void main(String[] args) {
        File dir = new File(".");
        
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        System.out.println("File: " + file.getName());
                    } else if (file.isDirectory()) {
                        System.out.println("Directory: " + file.getName());
                    }
                }
            }
        }
    }
}
```

**Key Concepts:**
- listFiles() for directory contents
- Filtering files and directories
- File name extraction
- Null checking

---

### Exercise 7: Copying Files
**Difficulty:** Easy  
**Time:** 20 minutes  
**Topics:** File copying, streams, byte arrays

**Pedagogic Objective:**
Understand file copying operations.

**Complete Solution:**
```java
import java.io.*;

public class CopyingFiles {
    public static void copyFile(String source, String destination) 
            throws IOException {
        try (FileInputStream fis = new FileInputStream(source);
             FileOutputStream fos = new FileOutputStream(destination)) {
            
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            System.out.println("File copied successfully");
        }
    }
    
    public static void main(String[] args) throws IOException {
        copyFile("source.txt", "destination.txt");
    }
}
```

**Key Concepts:**
- Buffer-based copying
- Efficient byte handling
- Exception propagation
- Resource management

---

### Exercise 8: Deleting Files
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** File deletion, file operations

**Pedagogic Objective:**
Understand file deletion operations.

**Complete Solution:**
```java
import java.io.*;

public class DeletingFiles {
    public static void main(String[] args) {
        File file = new File("temp.txt");
        
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("File deleted successfully");
            } else {
                System.out.println("Failed to delete file");
            }
        } else {
            System.out.println("File does not exist");
        }
    }
}
```

**Key Concepts:**
- delete() method
- Return value indicates success
- File existence checking
- Error handling

---

## 🟡 Medium Exercises (9-16)

### Exercise 9: Reading CSV Files
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** CSV parsing, string splitting, data processing

**Complete Solution:**
```java
import java.io.*;
import java.util.*;

public class ReadingCSVFiles {
    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(
                new FileReader("data.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                System.out.println("Fields: " + Arrays.toString(fields));
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
```

---

### Exercise 10: Writing CSV Files
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** CSV generation, data formatting

**Complete Solution:**
```java
import java.io.*;
import java.util.*;

public class WritingCSVFiles {
    public static void main(String[] args) {
        List<String[]> data = Arrays.asList(
            new String[]{"Name", "Age", "City"},
            new String[]{"Alice", "25", "New York"},
            new String[]{"Bob", "30", "Los Angeles"}
        );
        
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter("output.csv"))) {
            for (String[] row : data) {
                writer.write(String.join(",", row));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
```

---

### Exercise 11: Serialization
**Difficulty:** Medium  
**Time:** 30 minutes  
**Topics:** Serialization, ObjectOutputStream, object persistence

**Complete Solution:**
```java
import java.io.*;

public class Person implements Serializable {
    private String name;
    private int age;
    
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    @Override
    public String toString() {
        return "Person{" + "name='" + name + '\'' + ", age=" + age + '}';
    }
}

public class SerializationExample {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        // Serialize
        Person person = new Person("Alice", 25);
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream("person.dat"))) {
            oos.writeObject(person);
        }
        
        // Deserialize
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream("person.dat"))) {
            Person loaded = (Person) ois.readObject();
            System.out.println("Loaded: " + loaded);
        }
    }
}
```

---

### Exercise 12: Deserialization
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Deserialization, ObjectInputStream, object restoration

**Complete Solution:**
```java
import java.io.*;
import java.util.*;

public class DeserializationExample {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        // Create and serialize list
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream("names.dat"))) {
            oos.writeObject(names);
        }
        
        // Deserialize
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream("names.dat"))) {
            @SuppressWarnings("unchecked")
            List<String> loaded = (List<String>) ois.readObject();
            System.out.println("Loaded: " + loaded);
        }
    }
}
```

---

### Exercise 13: NIO - Channels and Buffers
**Difficulty:** Medium  
**Time:** 30 minutes  
**Topics:** NIO, FileChannel, ByteBuffer

**Complete Solution:**
```java
import java.io.*;
import java.nio.*;
import java.nio.channels.*;

public class NIOChannelsAndBuffers {
    public static void main(String[] args) throws IOException {
        // Write using NIO
        try (RandomAccessFile file = new RandomAccessFile("nio.dat", "rw");
             FileChannel channel = file.getChannel()) {
            
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            buffer.put("Hello, NIO!".getBytes());
            buffer.flip();
            channel.write(buffer);
        }
        
        // Read using NIO
        try (RandomAccessFile file = new RandomAccessFile("nio.dat", "r");
             FileChannel channel = file.getChannel()) {
            
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            channel.read(buffer);
            buffer.flip();
            System.out.println(new String(buffer.array()).trim());
        }
    }
}
```

---

### Exercise 14: File Watching
**Difficulty:** Medium  
**Time:** 30 minutes  
**Topics:** WatchService, file system events

**Complete Solution:**
```java
import java.nio.file.*;

public class FileWatching {
    public static void main(String[] args) throws Exception {
        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path path = Paths.get(".");
        path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);
        
        System.out.println("Watching directory: " + path.toAbsolutePath());
        
        WatchKey key;
        while ((key = watchService.take()) != null) {
            for (WatchEvent<?> event : key.pollEvents()) {
                System.out.println("Event: " + event.kind() + " - " + event.context());
            }
            key.reset();
        }
    }
}
```

---

### Exercise 15: Reading Large Files
**Difficulty:** Medium  
**Time:** 30 minutes  
**Topics:** Efficient reading, memory management, streaming

**Complete Solution:**
```java
import java.io.*;
import java.nio.file.*;

public class ReadingLargeFiles {
    public static void main(String[] args) throws IOException {
        // Using Files.lines() for streaming
        Files.lines(Paths.get("large.txt"))
            .filter(line -> !line.isEmpty())
            .limit(10)
            .forEach(System.out::println);
        
        // Using BufferedReader with buffer
        try (BufferedReader reader = new BufferedReader(
                new FileReader("large.txt"), 8192)) {
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null && count < 10) {
                System.out.println(line);
                count++;
            }
        }
    }
}
```

---

### Exercise 16: File Compression
**Difficulty:** Medium  
**Time:** 30 minutes  
**Topics:** ZIP files, compression, archiving

**Complete Solution:**
```java
import java.io.*;
import java.util.zip.*;

public class FileCompression {
    public static void compressFile(String source, String destination) 
            throws IOException {
        try (FileInputStream fis = new FileInputStream(source);
             FileOutputStream fos = new FileOutputStream(destination);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            
            ZipEntry entry = new ZipEntry(new File(source).getName());
            zos.putNextEntry(entry);
            
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                zos.write(buffer, 0, bytesRead);
            }
            zos.closeEntry();
        }
    }
    
    public static void main(String[] args) throws IOException {
        compressFile("test.txt", "test.zip");
        System.out.println("File compressed successfully");
    }
}
```

---

## 🔴 Hard Exercises (17-21)

### Exercise 17: Custom File Format
**Difficulty:** Hard  
**Time:** 40 minutes  
**Topics:** Custom serialization, binary format, data encoding

**Complete Solution:**
```java
import java.io.*;

public class CustomFileFormat {
    static class Record {
        String name;
        int age;
        double salary;
        
        Record(String name, int age, double salary) {
            this.name = name;
            this.age = age;
            this.salary = salary;
        }
    }
    
    public static void writeRecord(Record record, DataOutputStream dos) 
            throws IOException {
        dos.writeUTF(record.name);
        dos.writeInt(record.age);
        dos.writeDouble(record.salary);
    }
    
    public static Record readRecord(DataInputStream dis) 
            throws IOException {
        String name = dis.readUTF();
        int age = dis.readInt();
        double salary = dis.readDouble();
        return new Record(name, age, salary);
    }
    
    public static void main(String[] args) throws IOException {
        // Write
        try (DataOutputStream dos = new DataOutputStream(
                new FileOutputStream("records.dat"))) {
            writeRecord(new Record("Alice", 25, 50000), dos);
            writeRecord(new Record("Bob", 30, 60000), dos);
        }
        
        // Read
        try (DataInputStream dis = new DataInputStream(
                new FileInputStream("records.dat"))) {
            Record r1 = readRecord(dis);
            Record r2 = readRecord(dis);
            System.out.println(r1.name + " - " + r1.age + " - " + r1.salary);
            System.out.println(r2.name + " - " + r2.age + " - " + r2.salary);
        }
    }
}
```

---

### Exercise 18: Buffered I/O Performance
**Difficulty:** Hard  
**Time:** 40 minutes  
**Topics:** Performance optimization, buffering, benchmarking

**Complete Solution:**
```java
import java.io.*;

public class BufferedIOPerformance {
    public static void writeWithoutBuffer(String filename, int lines) 
            throws IOException {
        long start = System.currentTimeMillis();
        try (FileWriter fw = new FileWriter(filename)) {
            for (int i = 0; i < lines; i++) {
                fw.write("Line " + i + "\n");
            }
        }
        long time = System.currentTimeMillis() - start;
        System.out.println("Without buffer: " + time + "ms");
    }
    
    public static void writeWithBuffer(String filename, int lines) 
            throws IOException {
        long start = System.currentTimeMillis();
        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(filename))) {
            for (int i = 0; i < lines; i++) {
                bw.write("Line " + i + "\n");
            }
        }
        long time = System.currentTimeMillis() - start;
        System.out.println("With buffer: " + time + "ms");
    }
    
    public static void main(String[] args) throws IOException {
        writeWithoutBuffer("test1.txt", 10000);
        writeWithBuffer("test2.txt", 10000);
    }
}
```

---

### Exercise 19: Random Access Files
**Difficulty:** Hard  
**Time:** 40 minutes  
**Topics:** RandomAccessFile, seeking, random access

**Complete Solution:**
```java
import java.io.*;

public class RandomAccessFilesExample {
    public static void main(String[] args) throws IOException {
        // Write fixed-size records
        try (RandomAccessFile raf = new RandomAccessFile("random.dat", "rw")) {
            raf.writeInt(100);
            raf.writeInt(200);
            raf.writeInt(300);
        }
        
        // Read in random order
        try (RandomAccessFile raf = new RandomAccessFile("random.dat", "r")) {
            // Read third record
            raf.seek(8); // 2 * 4 bytes
            System.out.println("Third: " + raf.readInt());
            
            // Read first record
            raf.seek(0);
            System.out.println("First: " + raf.readInt());
            
            // Read second record
            raf.seek(4);
            System.out.println("Second: " + raf.readInt());
        }
    }
}
```

---

### Exercise 20: File Locking
**Difficulty:** Hard  
**Time:** 40 minutes  
**Topics:** File locking, concurrent access, synchronization

**Complete Solution:**
```java
import java.io.*;
import java.nio.channels.*;

public class FileLocking {
    public static void main(String[] args) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile("locked.txt", "rw");
             FileChannel channel = raf.getChannel()) {
            
            // Acquire exclusive lock
            FileLock lock = channel.lock();
            try {
                System.out.println("Lock acquired");
                raf.writeBytes("Protected data");
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.release();
                System.out.println("Lock released");
            }
        }
    }
}
```

---

### Exercise 21: Memory-Mapped Files
**Difficulty:** Hard  
**Time:** 40 minutes  
**Topics:** MappedByteBuffer, memory mapping, performance

**Complete Solution:**
```java
import java.io.*;
import java.nio.*;
import java.nio.channels.*;

public class MemoryMappedFiles {
    public static void main(String[] args) throws IOException {
        // Create file
        try (RandomAccessFile raf = new RandomAccessFile("mmap.dat", "rw");
             FileChannel channel = raf.getChannel()) {
            
            // Map file to memory
            MappedByteBuffer buffer = channel.map(
                FileChannel.MapMode.READ_WRITE, 0, 1024);
            
            // Write data
            buffer.put("Hello, Memory-Mapped Files!".getBytes());
            
            // Read data
            buffer.flip();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);
            System.out.println(new String(data));
        }
    }
}
```

---

## 🎯 Interview Exercises (22-25)

### Exercise 22: Log File Parser
**Difficulty:** Interview  
**Time:** 35 minutes

**Complete Solution:**
```java
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class LogFileParser {
    static class LogEntry {
        String timestamp;
        String level;
        String message;
        
        LogEntry(String timestamp, String level, String message) {
            this.timestamp = timestamp;
            this.level = level;
            this.message = message;
        }
    }
    
    public static List<LogEntry> parseLogFile(String filename) 
            throws IOException {
        List<LogEntry> entries = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\[(.*?)\\] (\\w+): (.*)");
        
        try (BufferedReader reader = new BufferedReader(
                new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    entries.add(new LogEntry(
                        matcher.group(1),
                        matcher.group(2),
                        matcher.group(3)
                    ));
                }
            }
        }
        return entries;
    }
    
    public static void main(String[] args) throws IOException {
        List<LogEntry> entries = parseLogFile("app.log");
        entries.forEach(e -> System.out.println(
            e.timestamp + " [" + e.level + "] " + e.message));
    }
}
```

---

### Exercise 23: File Backup System
**Difficulty:** Interview  
**Time:** 40 minutes

**Complete Solution:**
```java
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileBackupSystem {
    public static void backupFile(String source, String backupDir) 
            throws IOException {
        File sourceFile = new File(source);
        if (!sourceFile.exists()) {
            throw new FileNotFoundException("Source file not found");
        }
        
        String timestamp = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String backupName = sourceFile.getName() + "." + timestamp + ".bak";
        String backupPath = backupDir + File.separator + backupName;
        
        Files.copy(sourceFile.toPath(), Paths.get(backupPath),
            StandardCopyOption.REPLACE_EXISTING);
        
        System.out.println("Backup created: " + backupPath);
    }
    
    public static void main(String[] args) throws IOException {
        backupFile("important.txt", "backups");
    }
}
```

---

### Exercise 24: Configuration File Reader
**Difficulty:** Interview  
**Time:** 35 minutes

**Complete Solution:**
```java
import java.io.*;
import java.util.*;

public class ConfigurationFileReader {
    private Properties properties;
    
    public ConfigurationFileReader(String filename) throws IOException {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream(filename)) {
            properties.load(fis);
        }
    }
    
    public String getString(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    public int getInt(String key, int defaultValue) {
        String value = properties.getProperty(key);
        return value != null ? Integer.parseInt(value) : defaultValue;
    }
    
    public boolean getBoolean(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }
    
    public static void main(String[] args) throws IOException {
        ConfigurationFileReader config = new ConfigurationFileReader("app.properties");
        System.out.println("App name: " + config.getString("app.name", "Unknown"));
        System.out.println("Port: " + config.getInt("server.port", 8080));
        System.out.println("Debug: " + config.getBoolean("debug", false));
    }
}
```

---

### Exercise 25: Streaming Data Processor
**Difficulty:** Interview  
**Time:** 40 minutes

**Complete Solution:**
```java
import java.io.*;
import java.util.*;
import java.util.stream.*;

public class StreamingDataProcessor {
    public static Map<String, Long> processLargeFile(String filename) 
            throws IOException {
        return Files.lines(Paths.get(filename))
            .filter(line -> !line.trim().isEmpty())
            .flatMap(line -> Arrays.stream(line.split("\\s+")))
            .collect(Collectors.groupingBy(
                String::toLowerCase,
                Collectors.counting()
            ));
    }
    
    public static void main(String[] args) throws IOException {
        Map<String, Long> wordFrequency = processLargeFile("data.txt");
        wordFrequency.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .limit(10)
            .forEach(e -> System.out.println(e.getKey() + ": " + e.getValue()));
    }
}
```

---

## 📊 Solutions Summary

| Exercise | Title | Difficulty | Time | Topics |
|----------|-------|-----------|------|--------|
| 1 | Reading Text | Easy | 15 min | Reading |
| 2 | Writing Text | Easy | 15 min | Writing |
| 3 | Reading Binary | Easy | 15 min | Binary |
| 4 | Writing Binary | Easy | 15 min | Binary |
| 5 | File Operations | Easy | 20 min | Operations |
| 6 | Directory Operations | Easy | 20 min | Directories |
| 7 | Copying Files | Easy | 20 min | Copying |
| 8 | Deleting Files | Easy | 15 min | Deletion |
| 9 | Reading CSV | Medium | 25 min | CSV |
| 10 | Writing CSV | Medium | 25 min | CSV |
| 11 | Serialization | Medium | 30 min | Serialization |
| 12 | Deserialization | Medium | 25 min | Deserialization |
| 13 | NIO Channels | Medium | 30 min | NIO |
| 14 | File Watching | Medium | 30 min | Watching |
| 15 | Large Files | Medium | 30 min | Streaming |
| 16 | Compression | Medium | 30 min | ZIP |
| 17 | Custom Format | Hard | 40 min | Custom |
| 18 | Performance | Hard | 40 min | Optimization |
| 19 | Random Access | Hard | 40 min | Random |
| 20 | File Locking | Hard | 40 min | Locking |
| 21 | Memory Mapped | Hard | 40 min | Memory |
| 22 | Log Parser | Interview | 35 min | Parsing |
| 23 | Backup System | Interview | 40 min | Backup |
| 24 | Config Reader | Interview | 35 min | Config |
| 25 | Data Processor | Interview | 40 min | Streaming |

---

<div align="center">

## Exercises: File I/O

**25 Comprehensive Exercises**

**Easy (8) | Medium (8) | Hard (5) | Interview (4)**

**Total Time: 8-10 hours**

---

[Back to Module →](./README.md)

[View Pedagogic Guide →](./PEDAGOGIC_GUIDE.md)

[Take Quizzes →](./QUIZZES.md)

</div>

(ending readme)