# 07 - Java Files & I/O

File operations and input/output in Java. Covers file creation/reading/writing, I/O streams (InputStream, OutputStream), buffered I/O (BufferedReader, BufferedWriter), object serialization (ObjectInputStream, ObjectOutputStream), and NIO (Files, Path, DirectoryStream, walk).

## Prerequisites

- Java 17+
- Maven 3.x

## Key Concepts

- File operations: `Files.createTempFile`, `Files.writeString`, `Files.readString`, `Files.deleteIfExists`
- I/O streams: `FileInputStream`, `FileOutputStream`, `DataInputStream`, `DataOutputStream`
- Buffered I/O: `BufferedReader`, `BufferedWriter`, `FileReader`, `FileWriter`
- Object serialization: `ObjectOutputStream`, `ObjectInputStream`, `Serializable` interface
- NIO API: `Path`, `Files`, `DirectoryStream`, `Files.walk`, `Files.createTempDirectory`

## Module Structure

- `src/main/java/com/learning/lab/module07/Lab.java` - Main lab source

## Learning Objectives

- Perform file read/write operations using Java I/O
- Serialize and deserialize Java objects
- Use NIO for efficient file and directory operations

## Estimated Time

- 1-2 hours

## How to Run

```bash
cd 07-files-io
mvn compile exec:java -Dexec.mainClass="com.learning.lab.module07.Lab"
```
