# Module 07: File I/O - Mini Project

**Project Name**: Log File Merger & Filter  
**Difficulty Level**: Intermediate  
**Estimated Time**: 2-3 hours

---

## 🎯 Objective
Use Java NIO.2 (`java.nio.file.*`) and Java I/O Streams to perform robust file reading, writing, filtering, and directory traversal.

## 📝 Requirements

### Core Features
1. **Directory Traversal**:
   - Given a directory path, recursively find all files ending with `.log`.
   - Ensure you handle potential `AccessDeniedException` gracefully during traversal.

2. **File Processing & Filtering**:
   - Open each log file found.
   - Read the contents line by line.
   - Extract only the lines that contain the word `"ERROR"` or `"FATAL"`.

3. **Writing to Output**:
   - Create a new file named `merged_errors.log` in the parent directory.
   - If the file exists, overwrite it.
   - Write all extracted error lines into this single file.
   - Prepend each line with the name of the file it originated from (e.g., `[server1.log] ERROR: Connection timeout`).

4. **Resource Management**:
   - Use `Files.lines()`, `BufferedReader`, or `BufferedWriter` inside `try-with-resources` blocks to guarantee no file locks are left open.

---

## 💡 Solution Blueprint

1. **Traversal**: Use `Files.walk(Paths.get(dirPath))` or `Files.find()` to stream all `.log` paths.
2. **Setup Output Writer**: Open a `BufferedWriter` pointing to `merged_errors.log`.
3. **Stream Processing**: 
   ```java
   Files.walk(Paths.get(dir))
        .filter(path -> path.toString().endsWith(".log"))
        .forEach(path -> processLogFile(path, writer));
   ```
4. **Inside `processLogFile`**:
   ```java
   try (Stream<String> lines = Files.lines(logPath)) {
       lines.filter(line -> line.contains("ERROR") || line.contains("FATAL"))
            .forEach(line -> writeLine(writer, "[" + logPath.getFileName() + "] " + line));
   }
   ```
5. **Write Execution**:
   ```java
   void writeLine(BufferedWriter writer, String line) {
       try {
           writer.write(line);
           writer.newLine();
       } catch (IOException e) { ... }
   }
   ```