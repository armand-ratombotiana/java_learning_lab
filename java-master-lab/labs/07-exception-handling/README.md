# Lab 07: Exception Handling

## 📋 Lab Header

| Aspect | Details |
|--------|---------|
| **Difficulty** | Beginner-Intermediate |
| **Estimated Time** | 4 hours |
| **Real-World Context** | Building a file error handler system with robust error management |
| **Prerequisites** | Lab 06: Interfaces |
| **Learning Type** | Hands-on, Project-based |

## 🎯 Learning Objectives

By completing this lab, you will be able to:

1. **Understand exception handling** and error management
2. **Use try-catch-finally blocks** effectively
3. **Create custom exceptions** for specific scenarios
4. **Handle multiple exceptions** appropriately
5. **Implement error logging** and recovery
6. **Build a file error handler system** with robust error management

## 📚 Prerequisites

- Lab 06: Interfaces completed
- Understanding of interfaces
- Knowledge of polymorphism
- Familiarity with Java basics

## 🧠 Concept Theory

### 1. Exception Basics

Exceptions are objects representing errors:

```java
// Try-catch block
try {
    int result = 10 / 0;  // ArithmeticException
} catch (ArithmeticException e) {
    System.out.println("Cannot divide by zero: " + e.getMessage());
}

// Multiple catch blocks
try {
    String str = null;
    str.length();  // NullPointerException
} catch (NullPointerException e) {
    System.out.println("Null pointer error");
} catch (Exception e) {
    System.out.println("General error: " + e.getMessage());
}
```

**Exception Hierarchy**:
- `Throwable` (root)
  - `Error` (system errors)
  - `Exception` (recoverable)
    - `Checked` (must handle)
    - `Unchecked` (optional)

### 2. Try-Catch-Finally

Complete exception handling:

```java
try {
    // Code that might throw exception
    int[] arr = {1, 2, 3};
    System.out.println(arr[5]);  // ArrayIndexOutOfBoundsException
} catch (ArrayIndexOutOfBoundsException e) {
    System.out.println("Array index out of bounds");
} catch (Exception e) {
    System.out.println("General exception: " + e.getMessage());
} finally {
    // Always executes
    System.out.println("Cleanup code");
}
```

**Key Points**:
- `try`: Code that might throw exception
- `catch`: Handle specific exceptions
- `finally`: Always executes (cleanup)

### 3. Checked vs Unchecked Exceptions

Different exception types:

```java
// Checked Exception (must handle)
public void readFile(String filename) throws IOException {
    FileReader reader = new FileReader(filename);
    // Must handle or declare throws
}

// Unchecked Exception (optional)
public void divide(int a, int b) {
    int result = a / b;  // ArithmeticException
    // No need to handle
}

// Handling checked exception
try {
    readFile("file.txt");
} catch (IOException e) {
    System.out.println("File not found: " + e.getMessage());
}
```

**Checked Exceptions**:
- Must be caught or declared
- Compile-time checking
- Examples: IOException, SQLException

**Unchecked Exceptions**:
- Optional to catch
- Runtime errors
- Examples: NullPointerException, ArithmeticException

### 4. Custom Exceptions

Create domain-specific exceptions:

```java
// Custom exception
public class InsufficientFundsException extends Exception {
    private double amount;
    
    public InsufficientFundsException(String message, double amount) {
        super(message);
        this.amount = amount;
    }
    
    public double getAmount() {
        return amount;
    }
}

// Using custom exception
public class BankAccount {
    private double balance;
    
    public void withdraw(double amount) throws InsufficientFundsException {
        if (amount > balance) {
            throw new InsufficientFundsException(
                "Insufficient funds for withdrawal", 
                amount - balance
            );
        }
        balance -= amount;
    }
}

// Handling custom exception
try {
    account.withdraw(1000);
} catch (InsufficientFundsException e) {
    System.out.println(e.getMessage());
    System.out.println("Short by: $" + e.getAmount());
}
```

### 5. Exception Propagation

Throwing exceptions up the call stack:

```java
public void methodA() throws IOException {
    methodB();
}

public void methodB() throws IOException {
    methodC();
}

public void methodC() throws IOException {
    throw new IOException("File not found");
}

// Handling at top level
try {
    methodA();
} catch (IOException e) {
    System.out.println("Error: " + e.getMessage());
}
```

### 6. Try-With-Resources

Automatic resource management:

```java
// Traditional approach
FileReader reader = null;
try {
    reader = new FileReader("file.txt");
    // Use reader
} catch (IOException e) {
    System.out.println("Error: " + e.getMessage());
} finally {
    if (reader != null) {
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// Try-with-resources (Java 7+)
try (FileReader reader = new FileReader("file.txt")) {
    // Use reader
    // Automatically closed
} catch (IOException e) {
    System.out.println("Error: " + e.getMessage());
}
```

### 7. Exception Chaining

Preserving original exception:

```java
try {
    // Some operation
    int result = Integer.parseInt("abc");
} catch (NumberFormatException e) {
    // Chain exception
    throw new IllegalArgumentException("Invalid number format", e);
}

// Handling chained exception
try {
    // Code
} catch (IllegalArgumentException e) {
    System.out.println("Error: " + e.getMessage());
    System.out.println("Caused by: " + e.getCause());
}
```

### 8. Stack Trace

Understanding error information:

```java
try {
    methodA();
} catch (Exception e) {
    // Print stack trace
    e.printStackTrace();
    
    // Get stack trace elements
    StackTraceElement[] elements = e.getStackTrace();
    for (StackTraceElement element : elements) {
        System.out.println(element.getClassName() + "." + 
                         element.getMethodName() + ":" + 
                         element.getLineNumber());
    }
}
```

### 9. Finally Block Behavior

Understanding finally execution:

```java
public int testFinally() {
    try {
        return 1;
    } catch (Exception e) {
        return 2;
    } finally {
        System.out.println("Finally block");  // Always executes
    }
}

// Output: Finally block, returns 1

// Finally with exception
public void testFinallyException() {
    try {
        throw new Exception("Error");
    } finally {
        System.out.println("Finally");  // Executes before exception propagates
    }
}
```

### 10. Best Practices

Exception handling guidelines:

```java
// ❌ Bad: Catching too broad
try {
    // Code
} catch (Exception e) {
    // Catches everything
}

// ✅ Good: Specific exceptions
try {
    // Code
} catch (IOException e) {
    // Handle IO errors
} catch (NumberFormatException e) {
    // Handle format errors
}

// ❌ Bad: Ignoring exceptions
try {
    // Code
} catch (Exception e) {
    // Silent failure
}

// ✅ Good: Logging exceptions
try {
    // Code
} catch (Exception e) {
    logger.error("Operation failed", e);
    throw new RuntimeException("Operation failed", e);
}

// ❌ Bad: Using exceptions for control flow
try {
    while (true) {
        // Code
    }
} catch (Exception e) {
    // Exit loop
}

// ✅ Good: Using proper control flow
while (hasMore()) {
    // Code
}
```

---

## 💻 Step-by-Step Coding Tasks

### Task 1: Basic Exception Handling

**Objective**: Implement try-catch-finally blocks

**Acceptance Criteria**:
- [ ] Try block with risky code
- [ ] Catch block handling exception
- [ ] Finally block for cleanup
- [ ] Proper error messages
- [ ] Code compiles without errors

**Instructions**:
1. Create method that reads file
2. Implement try-catch-finally
3. Handle IOException
4. Add cleanup code
5. Test with valid and invalid files

### Task 2: Custom Exceptions

**Objective**: Create and use custom exceptions

**Acceptance Criteria**:
- [ ] Custom exception class created
- [ ] Extends Exception properly
- [ ] Constructor with message
- [ ] Thrown in appropriate places
- [ ] Caught and handled correctly

**Instructions**:
1. Create custom exception
2. Throw in validation method
3. Catch and handle
4. Display custom error message
5. Test with invalid data

### Task 3: Multiple Exception Handling

**Objective**: Handle different exception types

**Acceptance Criteria**:
- [ ] Multiple catch blocks
- [ ] Specific exception handling
- [ ] Proper exception ordering
- [ ] Different handling for each type
- [ ] No exception escapes

**Instructions**:
1. Create method with multiple risks
2. Add multiple catch blocks
3. Handle each exception type
4. Log different messages
5. Test all scenarios

---

## 🎨 Mini-Project: File Error Handler System

### Project Overview

**Description**: Create a comprehensive file handling system with robust error management and logging.

**Real-World Application**: File processing systems, data import/export, backup systems.

**Learning Value**: Master exception handling, logging, and error recovery.

### Project Requirements

#### Functional Requirements
- [ ] Read files with error handling
- [ ] Write files with validation
- [ ] Copy files with error recovery
- [ ] Delete files safely
- [ ] Log all operations
- [ ] Generate error reports

#### Non-Functional Requirements
- [ ] Clean code structure
- [ ] Comprehensive error handling
- [ ] Detailed logging
- [ ] Unit tests
- [ ] Documentation

### Project Structure

```
file-error-handler-system/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/learning/
│   │           ├── FileException.java
│   │           ├── FileHandler.java
│   │           ├── Logger.java
│   │           ├── FileValidator.java
│   │           ├── FileProcessor.java
│   │           └── Main.java
│   └── test/
│       └── java/
│           └── com/learning/
│               ├── FileHandlerTest.java
│               └── FileValidatorTest.java
├── pom.xml
└── README.md
```

### Implementation Guide

#### Step 1: Create Custom Exceptions

```java
package com.learning;

/**
 * Base exception for file operations.
 */
public class FileException extends Exception {
    private String filename;
    
    public FileException(String message, String filename) {
        super(message);
        this.filename = filename;
    }
    
    public FileException(String message, String filename, Throwable cause) {
        super(message, cause);
        this.filename = filename;
    }
    
    public String getFilename() {
        return filename;
    }
}

/**
 * Exception for file not found.
 */
public class FileNotFoundException extends FileException {
    public FileNotFoundException(String filename) {
        super("File not found: " + filename, filename);
    }
}

/**
 * Exception for invalid file.
 */
public class InvalidFileException extends FileException {
    private String reason;
    
    public InvalidFileException(String filename, String reason) {
        super("Invalid file: " + reason, filename);
        this.reason = reason;
    }
    
    public String getReason() {
        return reason;
    }
}

/**
 * Exception for file operation failure.
 */
public class FileOperationException extends FileException {
    public FileOperationException(String message, String filename, Throwable cause) {
        super(message, filename, cause);
    }
}
```

#### Step 2: Create Logger Class

```java
package com.learning;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Simple logger for file operations.
 */
public class Logger {
    private String logFile;
    private DateTimeFormatter formatter;
    
    public Logger(String logFile) {
        this.logFile = logFile;
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }
    
    /**
     * Log info message.
     */
    public void info(String message) {
        log("INFO", message);
    }
    
    /**
     * Log warning message.
     */
    public void warn(String message) {
        log("WARN", message);
    }
    
    /**
     * Log error message.
     */
    public void error(String message, Exception e) {
        log("ERROR", message + " - " + e.getMessage());
    }
    
    /**
     * Log message to file and console.
     */
    private void log(String level, String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        String logMessage = "[" + timestamp + "] " + level + ": " + message;
        
        // Print to console
        System.out.println(logMessage);
        
        // Write to file
        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.write(logMessage + "\n");
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }
}
```

#### Step 3: Create FileValidator Class

```java
package com.learning;

import java.io.File;

/**
 * Validates files before operations.
 */
public class FileValidator {
    
    /**
     * Validate file exists.
     */
    public static void validateFileExists(String filename) throws FileNotFoundException {
        File file = new File(filename);
        if (!file.exists()) {
            throw new FileNotFoundException(filename);
        }
    }
    
    /**
     * Validate file is readable.
     */
    public static void validateFileReadable(String filename) throws InvalidFileException {
        File file = new File(filename);
        if (!file.canRead()) {
            throw new InvalidFileException(filename, "File is not readable");
        }
    }
    
    /**
     * Validate file is writable.
     */
    public static void validateFileWritable(String filename) throws InvalidFileException {
        File file = new File(filename);
        if (file.exists() && !file.canWrite()) {
            throw new InvalidFileException(filename, "File is not writable");
        }
    }
    
    /**
     * Validate file size.
     */
    public static void validateFileSize(String filename, long maxSize) 
            throws InvalidFileException {
        File file = new File(filename);
        if (file.length() > maxSize) {
            throw new InvalidFileException(filename, 
                "File size exceeds maximum: " + maxSize + " bytes");
        }
    }
    
    /**
     * Validate file extension.
     */
    public static void validateFileExtension(String filename, String... extensions) 
            throws InvalidFileException {
        for (String ext : extensions) {
            if (filename.endsWith(ext)) {
                return;
            }
        }
        throw new InvalidFileException(filename, 
            "Invalid file extension. Expected: " + String.join(", ", extensions));
    }
}
```

#### Step 4: Create FileHandler Class

```java
package com.learning;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Handles file operations with error handling.
 */
public class FileHandler {
    private Logger logger;
    
    public FileHandler(Logger logger) {
        this.logger = logger;
    }
    
    /**
     * Read file content.
     */
    public String readFile(String filename) throws FileException {
        try {
            FileValidator.validateFileExists(filename);
            FileValidator.validateFileReadable(filename);
            
            String content = new String(Files.readAllBytes(Paths.get(filename)));
            logger.info("File read successfully: " + filename);
            return content;
        } catch (FileNotFoundException | InvalidFileException e) {
            logger.error("Validation failed", e);
            throw e;
        } catch (IOException e) {
            logger.error("Failed to read file", e);
            throw new FileOperationException("Failed to read file", filename, e);
        }
    }
    
    /**
     * Write content to file.
     */
    public void writeFile(String filename, String content) throws FileException {
        try {
            FileValidator.validateFileWritable(filename);
            
            try (FileWriter writer = new FileWriter(filename)) {
                writer.write(content);
            }
            logger.info("File written successfully: " + filename);
        } catch (InvalidFileException e) {
            logger.error("Validation failed", e);
            throw e;
        } catch (IOException e) {
            logger.error("Failed to write file", e);
            throw new FileOperationException("Failed to write file", filename, e);
        }
    }
    
    /**
     * Copy file.
     */
    public void copyFile(String source, String destination) throws FileException {
        try {
            FileValidator.validateFileExists(source);
            FileValidator.validateFileReadable(source);
            FileValidator.validateFileWritable(destination);
            
            String content = readFile(source);
            writeFile(destination, content);
            logger.info("File copied: " + source + " -> " + destination);
        } catch (FileException e) {
            logger.error("Copy operation failed", e);
            throw e;
        }
    }
    
    /**
     * Delete file.
     */
    public void deleteFile(String filename) throws FileException {
        try {
            FileValidator.validateFileExists(filename);
            
            File file = new File(filename);
            if (!file.delete()) {
                throw new IOException("Failed to delete file");
            }
            logger.info("File deleted: " + filename);
        } catch (FileNotFoundException e) {
            logger.error("File not found", e);
            throw e;
        } catch (IOException e) {
            logger.error("Failed to delete file", e);
            throw new FileOperationException("Failed to delete file", filename, e);
        }
    }
    
    /**
     * Get file size.
     */
    public long getFileSize(String filename) throws FileException {
        try {
            FileValidator.validateFileExists(filename);
            File file = new File(filename);
            return file.length();
        } catch (FileNotFoundException e) {
            logger.error("File not found", e);
            throw e;
        }
    }
}
```

#### Step 5: Create FileProcessor Class

```java
package com.learning;

import java.util.ArrayList;
import java.util.List;

/**
 * Processes multiple files with error handling.
 */
public class FileProcessor {
    private FileHandler fileHandler;
    private Logger logger;
    private List<String> processedFiles;
    private List<String> failedFiles;
    
    public FileProcessor(FileHandler fileHandler, Logger logger) {
        this.fileHandler = fileHandler;
        this.logger = logger;
        this.processedFiles = new ArrayList<>();
        this.failedFiles = new ArrayList<>();
    }
    
    /**
     * Process multiple files.
     */
    public void processFiles(String[] filenames) {
        logger.info("Starting batch file processing: " + filenames.length + " files");
        
        for (String filename : filenames) {
            try {
                String content = fileHandler.readFile(filename);
                processedFiles.add(filename);
                logger.info("Processed: " + filename);
            } catch (FileException e) {
                failedFiles.add(filename);
                logger.error("Failed to process file", e);
            }
        }
        
        displaySummary();
    }
    
    /**
     * Display processing summary.
     */
    private void displaySummary() {
        System.out.println("\n=== Processing Summary ===");
        System.out.println("Successful: " + processedFiles.size());
        System.out.println("Failed: " + failedFiles.size());
        
        if (!failedFiles.isEmpty()) {
            System.out.println("\nFailed files:");
            for (String file : failedFiles) {
                System.out.println("  - " + file);
            }
        }
    }
    
    /**
     * Get processed files.
     */
    public List<String> getProcessedFiles() {
        return new ArrayList<>(processedFiles);
    }
    
    /**
     * Get failed files.
     */
    public List<String> getFailedFiles() {
        return new ArrayList<>(failedFiles);
    }
}
```

#### Step 6: Create Main Class

```java
package com.learning;

/**
 * Main entry point for File Error Handler System.
 */
public class Main {
    public static void main(String[] args) {
        // Create logger
        Logger logger = new Logger("file_operations.log");
        
        // Create file handler
        FileHandler fileHandler = new FileHandler(logger);
        
        logger.info("=== File Error Handler System Started ===");
        
        // Test 1: Read existing file
        System.out.println("\n=== Test 1: Read File ===");
        try {
            String content = fileHandler.readFile("test.txt");
            System.out.println("Content: " + content.substring(0, Math.min(50, content.length())));
        } catch (FileException e) {
            System.out.println("Error: " + e.getMessage());
        }
        
        // Test 2: Write file
        System.out.println("\n=== Test 2: Write File ===");
        try {
            fileHandler.writeFile("output.txt", "Hello, World!");
            System.out.println("File written successfully");
        } catch (FileException e) {
            System.out.println("Error: " + e.getMessage());
        }
        
        // Test 3: Copy file
        System.out.println("\n=== Test 3: Copy File ===");
        try {
            fileHandler.copyFile("output.txt", "backup.txt");
            System.out.println("File copied successfully");
        } catch (FileException e) {
            System.out.println("Error: " + e.getMessage());
        }
        
        // Test 4: Get file size
        System.out.println("\n=== Test 4: Get File Size ===");
        try {
            long size = fileHandler.getFileSize("output.txt");
            System.out.println("File size: " + size + " bytes");
        } catch (FileException e) {
            System.out.println("Error: " + e.getMessage());
        }
        
        // Test 5: Process multiple files
        System.out.println("\n=== Test 5: Batch Processing ===");
        FileProcessor processor = new FileProcessor(fileHandler, logger);
        String[] files = {"output.txt", "backup.txt", "nonexistent.txt"};
        processor.processFiles(files);
        
        logger.info("=== File Error Handler System Completed ===");
    }
}
```

#### Step 7: Create Unit Tests

```java
package com.learning;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for file handler.
 */
public class FileHandlerTest {
    
    private FileHandler fileHandler;
    private Logger logger;
    private String testFile = "test_file.txt";
    
    @BeforeEach
    void setUp() {
        logger = new Logger("test.log");
        fileHandler = new FileHandler(logger);
    }
    
    @Test
    void testWriteFile() throws FileException {
        fileHandler.writeFile(testFile, "Test content");
        assertTrue(new File(testFile).exists());
    }
    
    @Test
    void testReadFile() throws FileException {
        fileHandler.writeFile(testFile, "Test content");
        String content = fileHandler.readFile(testFile);
        assertEquals("Test content", content);
    }
    
    @Test
    void testReadNonexistentFile() {
        assertThrows(FileNotFoundException.class, () -> {
            fileHandler.readFile("nonexistent.txt");
        });
    }
    
    @Test
    void testCopyFile() throws FileException {
        fileHandler.writeFile(testFile, "Test content");
        fileHandler.copyFile(testFile, "copy.txt");
        assertTrue(new File("copy.txt").exists());
    }
    
    @Test
    void testGetFileSize() throws FileException {
        fileHandler.writeFile(testFile, "Test");
        long size = fileHandler.getFileSize(testFile);
        assertEquals(4, size);
    }
}

/**
 * Unit tests for file validator.
 */
public class FileValidatorTest {
    
    private String testFile = "validator_test.txt";
    
    @BeforeEach
    void setUp() throws Exception {
        new File(testFile).createNewFile();
    }
    
    @Test
    void testValidateFileExists() {
        assertDoesNotThrow(() -> {
            FileValidator.validateFileExists(testFile);
        });
    }
    
    @Test
    void testValidateFileNotExists() {
        assertThrows(FileNotFoundException.class, () -> {
            FileValidator.validateFileExists("nonexistent.txt");
        });
    }
    
    @Test
    void testValidateFileExtension() {
        assertDoesNotThrow(() -> {
            FileValidator.validateFileExtension(testFile, ".txt", ".doc");
        });
    }
    
    @Test
    void testValidateInvalidExtension() {
        assertThrows(InvalidFileException.class, () -> {
            FileValidator.validateFileExtension(testFile, ".pdf", ".doc");
        });
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

### Exercise 1: Database Connection Handler

**Objective**: Practice exception handling with database operations

**Task Description**:
Create database connection handler with retry logic and error recovery

**Acceptance Criteria**:
- [ ] Connection management
- [ ] Retry mechanism
- [ ] Error logging
- [ ] Connection pooling
- [ ] Graceful shutdown

### Exercise 2: Network Request Handler

**Objective**: Practice exception handling with network operations

**Task Description**:
Create HTTP request handler with timeout and retry logic

**Acceptance Criteria**:
- [ ] Request execution
- [ ] Timeout handling
- [ ] Retry logic
- [ ] Error logging
- [ ] Response validation

### Exercise 3: Data Validation Handler

**Objective**: Practice custom exceptions with validation

**Task Description**:
Create data validator with custom exceptions for different validation failures

**Acceptance Criteria**:
- [ ] Multiple validation rules
- [ ] Custom exceptions
- [ ] Error messages
- [ ] Validation chaining
- [ ] Error reporting

---

## 🧪 Quiz

### Question 1: What is the purpose of try-catch?

A) Define variables  
B) Handle exceptions  
C) Create loops  
D) Define methods  

**Answer**: B) Handle exceptions

### Question 2: What is finally block used for?

A) Catch exceptions  
B) Throw exceptions  
C) Cleanup code  
D) Define variables  

**Answer**: C) Cleanup code

### Question 3: What is a checked exception?

A) Exception that must be caught  
B) Exception that is optional  
C) Exception in finally  
D) Exception in catch  

**Answer**: A) Exception that must be caught

### Question 4: Can you create custom exceptions?

A) No  
B) Yes, extend Exception  
C) Yes, extend Error  
D) Only built-in  

**Answer**: B) Yes, extend Exception

### Question 5: What does throws keyword do?

A) Catches exception  
B) Declares exception  
C) Creates exception  
D) Handles exception  

**Answer**: B) Declares exception

---

## 🚀 Advanced Challenge

### Challenge: Complete Error Management System

**Difficulty**: Intermediate

**Objective**: Build comprehensive error management with monitoring

**Requirements**:
- [ ] Multiple exception types
- [ ] Error logging
- [ ] Error recovery
- [ ] Error monitoring
- [ ] Error reporting
- [ ] Error analytics

---

## 🏆 Best Practices

### Exception Handling

1. **Specific Exceptions**
   - Catch specific exceptions
   - Avoid catching Exception

2. **Meaningful Messages**
   - Provide context
   - Include relevant data

3. **Proper Cleanup**
   - Use finally or try-with-resources
   - Release resources

---

## 🔗 Next Steps

**Next Lab**: [Lab 08: Collections Framework](../08-collections-framework/README.md)

---

## ✅ Completion Checklist

- [ ] Completed all coding tasks
- [ ] Built file error handler system
- [ ] Solved all exercises
- [ ] Passed quiz (80%+)
- [ ] Attempted advanced challenge
- [ ] Reviewed best practices

---

**Congratulations on completing Lab 07! 🎉**

You've mastered exception handling and error management. Ready for collections? Move on to [Lab 08: Collections Framework](../08-collections-framework/README.md).