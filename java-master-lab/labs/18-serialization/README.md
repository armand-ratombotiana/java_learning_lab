# Lab 18: Serialization

## 📋 Lab Header

| Aspect | Details |
|--------|---------|
| **Difficulty** | Intermediate |
| **Estimated Time** | 4 hours |
| **Real-World Context** | Building an object persistence system |
| **Prerequisites** | Lab 17: NIO |
| **Learning Type** | Hands-on, Project-based |

## 🎯 Learning Objectives

By completing this lab, you will be able to:

1. **Understand object serialization** and its uses
2. **Implement Serializable interface** properly
3. **Use custom serialization** for control
4. **Handle deserialization** safely
5. **Manage version control** in serialized objects
6. **Build an object persistence system** with serialization

## 📚 Prerequisites

- Lab 17: NIO completed
- Understanding of I/O operations
- Knowledge of object-oriented programming
- Familiarity with file operations

## 🧠 Concept Theory

### 1. Object Serialization

Converting objects to bytes:

```java
import java.io.*;

// Implement Serializable
class Person implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private int age;
    
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
}

// Serialize object
Person person = new Person("John", 30);
try (ObjectOutputStream oos = new ObjectOutputStream(
        new FileOutputStream("person.ser"))) {
    oos.writeObject(person);
}

// Deserialize object
Person deserializedPerson;
try (ObjectInputStream ois = new ObjectInputStream(
        new FileInputStream("person.ser"))) {
    deserializedPerson = (Person) ois.readObject();
}

// Serialize multiple objects
try (ObjectOutputStream oos = new ObjectOutputStream(
        new FileOutputStream("objects.ser"))) {
    oos.writeObject(person1);
    oos.writeObject(person2);
    oos.writeObject(person3);
}

// Deserialize multiple objects
try (ObjectInputStream ois = new ObjectInputStream(
        new FileInputStream("objects.ser"))) {
    Person p1 = (Person) ois.readObject();
    Person p2 = (Person) ois.readObject();
    Person p3 = (Person) ois.readObject();
}
```

### 2. Serializable Interface

Marking classes for serialization:

```java
// ✅ Good: Implement Serializable
class Employee implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private double salary;
    
    public Employee(String name, double salary) {
        this.name = name;
        this.salary = salary;
    }
}

// ❌ Bad: Not implementing Serializable
class NonSerializable {
    private String data;
}

// ✅ Good: Serializable with version control
class Product implements Serializable {
    private static final long serialVersionUID = 2L;  // Version 2
    private String name;
    private double price;
    private String description;  // Added in version 2
}

// ✅ Good: Serializable with transient fields
class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;
    private transient String password;  // Not serialized
    private String email;
}
```

### 3. Custom Serialization

Controlling serialization process:

```java
class CustomSerializable implements Serializable {
    private static final long serialVersionUID = 1L;
    private String data;
    private transient int tempValue;
    
    // Custom write
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();  // Write default fields
        oos.writeInt(tempValue);   // Write transient field
    }
    
    // Custom read
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();   // Read default fields
        tempValue = ois.readInt(); // Read transient field
    }
    
    // Replace object during serialization
    private Object writeReplace() throws ObjectStreamException {
        return new SerializationProxy(this);
    }
    
    // Replace object during deserialization
    private Object readResolve() throws ObjectStreamException {
        return new CustomSerializable();
    }
}

// Serialization proxy
class SerializationProxy implements Serializable {
    private static final long serialVersionUID = 1L;
    private String data;
    
    SerializationProxy(CustomSerializable original) {
        this.data = original.data;
    }
    
    private Object readResolve() throws ObjectStreamException {
        return new CustomSerializable();
    }
}
```

### 4. Deserialization

Reading serialized objects:

```java
// Basic deserialization
try (ObjectInputStream ois = new ObjectInputStream(
        new FileInputStream("object.ser"))) {
    Object obj = ois.readObject();
    if (obj instanceof Person) {
        Person person = (Person) obj;
        System.out.println(person.getName());
    }
}

// Deserialization with error handling
try (ObjectInputStream ois = new ObjectInputStream(
        new FileInputStream("object.ser"))) {
    try {
        Person person = (Person) ois.readObject();
    } catch (ClassNotFoundException e) {
        System.err.println("Class not found: " + e.getMessage());
    } catch (InvalidClassException e) {
        System.err.println("Invalid class: " + e.getMessage());
    }
}

// Deserialization with version checking
try (ObjectInputStream ois = new ObjectInputStream(
        new FileInputStream("object.ser"))) {
    Person person = (Person) ois.readObject();
    if (person.getSerialVersionUID() != expectedVersion) {
        System.out.println("Version mismatch");
    }
}
```

### 5. Version Control

Managing serialized object versions:

```java
// Version 1
class Product implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private double price;
}

// Version 2 - Added field
class Product implements Serializable {
    private static final long serialVersionUID = 2L;
    private String name;
    private double price;
    private String description;  // New field
    
    // Handle deserialization from version 1
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        if (description == null) {
            description = "No description";
        }
    }
}

// Version 3 - Removed field
class Product implements Serializable {
    private static final long serialVersionUID = 3L;
    private String name;
    private double price;
    private String description;
    // Removed: private String category;
    
    // Handle deserialization from version 2
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        // category is ignored
    }
}
```

### 6. Security Considerations

Safe serialization practices:

```java
// ❌ Bad: Serializing sensitive data
class Account implements Serializable {
    private String accountNumber;
    private String password;  // Sensitive!
    private double balance;
}

// ✅ Good: Transient sensitive fields
class Account implements Serializable {
    private static final long serialVersionUID = 1L;
    private String accountNumber;
    private transient String password;  // Not serialized
    private double balance;
}

// ✅ Good: Custom serialization for security
class SecureAccount implements Serializable {
    private static final long serialVersionUID = 1L;
    private String accountNumber;
    private transient String password;
    private double balance;
    
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        // Don't write password
    }
    
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        // Password must be set separately
    }
}

// ✅ Good: Validate on deserialization
class ValidatedObject implements Serializable {
    private static final long serialVersionUID = 1L;
    private String data;
    
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        if (data == null || data.isEmpty()) {
            throw new InvalidObjectException("Data cannot be empty");
        }
    }
}
```

### 7. Performance Optimization

Efficient serialization:

```java
// ❌ Bad: Serializing large objects
class LargeObject implements Serializable {
    private byte[] largeArray = new byte[1000000];
    private List<String> largeList = new ArrayList<>();
}

// ✅ Good: Selective serialization
class OptimizedObject implements Serializable {
    private static final long serialVersionUID = 1L;
    private transient byte[] largeArray;
    private List<String> importantData;
    
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        // Only write important data
    }
}

// ✅ Good: Compression
try (ObjectOutputStream oos = new ObjectOutputStream(
        new GZIPOutputStream(new FileOutputStream("object.ser.gz")))) {
    oos.writeObject(largeObject);
}

try (ObjectInputStream ois = new ObjectInputStream(
        new GZIPInputStream(new FileInputStream("object.ser.gz")))) {
    Object obj = ois.readObject();
}
```

### 8. Externalizable Interface

Complete control over serialization:

```java
class ExternalizableClass implements Externalizable {
    private String name;
    private int age;
    
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(name);
        out.writeInt(age);
    }
    
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        name = in.readUTF();
        age = in.readInt();
    }
}

// Serialize
try (ObjectOutputStream oos = new ObjectOutputStream(
        new FileOutputStream("external.ser"))) {
    oos.writeObject(new ExternalizableClass("John", 30));
}

// Deserialize
try (ObjectInputStream ois = new ObjectInputStream(
        new FileInputStream("external.ser"))) {
    ExternalizableClass obj = (ExternalizableClass) ois.readObject();
}
```

### 9. Common Pitfalls

Avoiding serialization problems:

```java
// ❌ Bad: Circular references
class Node implements Serializable {
    private String data;
    private Node next;
    private Node previous;  // Circular reference
}

// ✅ Good: Handle circular references
class SafeNode implements Serializable {
    private static final long serialVersionUID = 1L;
    private String data;
    private transient SafeNode next;
    private transient SafeNode previous;
}

// ❌ Bad: Non-serializable fields
class Container implements Serializable {
    private Thread thread;  // Not serializable
    private Socket socket;  // Not serializable
}

// ✅ Good: Transient non-serializable fields
class SafeContainer implements Serializable {
    private static final long serialVersionUID = 1L;
    private transient Thread thread;
    private transient Socket socket;
}

// ❌ Bad: Changing serialVersionUID
class Evolving implements Serializable {
    private static final long serialVersionUID = 1L;  // Changed!
    private String data;
}

// ✅ Good: Keep serialVersionUID stable
class StableEvolving implements Serializable {
    private static final long serialVersionUID = 1L;  // Never change
    private String data;
    private String newField;  // Add new fields
}
```

### 10. Best Practices

Serialization guidelines:

```java
// ✅ Always implement Serializable
class GoodClass implements Serializable {
    private static final long serialVersionUID = 1L;
}

// ✅ Always define serialVersionUID
class WithVersion implements Serializable {
    private static final long serialVersionUID = 1L;
}

// ✅ Mark sensitive fields as transient
class WithTransient implements Serializable {
    private static final long serialVersionUID = 1L;
    private transient String password;
}

// ✅ Implement custom serialization if needed
class CustomSerialization implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
    }
    
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
    }
}

// ✅ Validate on deserialization
class Validated implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        // Validate state
    }
}
```

---

## 💻 Step-by-Step Coding Tasks

### Task 1: Implement Serializable Classes

**Objective**: Create serializable classes with proper implementation

**Acceptance Criteria**:
- [ ] Serializable implemented
- [ ] serialVersionUID defined
- [ ] Transient fields marked
- [ ] Proper constructors
- [ ] Code compiles without errors

**Instructions**:
1. Create serializable class
2. Define serialVersionUID
3. Mark transient fields
4. Implement constructors
5. Test serialization

### Task 2: Custom Serialization

**Objective**: Implement custom serialization logic

**Acceptance Criteria**:
- [ ] writeObject implemented
- [ ] readObject implemented
- [ ] Custom logic works
- [ ] Data preserved
- [ ] Tests pass

**Instructions**:
1. Implement writeObject
2. Implement readObject
3. Add custom logic
4. Test serialization
5. Verify data integrity

### Task 3: Version Management

**Objective**: Handle serialization version changes

**Acceptance Criteria**:
- [ ] Version tracking
- [ ] Backward compatibility
- [ ] Forward compatibility
- [ ] Migration logic
- [ ] Tests pass

**Instructions**:
1. Create versioned class
2. Add version handling
3. Implement migration
4. Test compatibility
5. Verify correctness

---

## 🎨 Mini-Project: Object Persistence System

### Project Overview

**Description**: Create a comprehensive object persistence system using serialization.

**Real-World Application**: Data storage, caching, object databases.

**Learning Value**: Master serialization and object persistence.

### Project Structure

```
object-persistence-system/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/learning/
│   │           ├── Persistable.java
│   │           ├── ObjectStore.java
│   │           ├── PersistenceStats.java
│   │           └── Main.java
│   └── test/
│       └── java/
│           └── com/learning/
│               └── ObjectStoreTest.java
├── pom.xml
└── README.md
```

### Implementation Guide

#### Step 1: Create Persistable Interface

```java
package com.learning;

import java.io.Serializable;

/**
 * Interface for persistable objects.
 */
public interface Persistable extends Serializable {
    String getId();
    void setId(String id);
}
```

#### Step 2: Create ObjectStore Class

```java
package com.learning;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Stores and retrieves objects.
 */
public class ObjectStore {
    private String storePath;
    private PersistenceStats stats;
    
    /**
     * Constructor for ObjectStore.
     */
    public ObjectStore(String storePath) throws IOException {
        this.storePath = storePath;
        this.stats = new PersistenceStats();
        Files.createDirectories(Paths.get(storePath));
    }
    
    /**
     * Save object.
     */
    public void save(Persistable obj) throws IOException {
        String filename = storePath + "/" + obj.getId() + ".ser";
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filename))) {
            oos.writeObject(obj);
            stats.recordSave();
        }
    }
    
    /**
     * Load object.
     */
    public Persistable load(String id) throws IOException, ClassNotFoundException {
        String filename = storePath + "/" + id + ".ser";
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filename))) {
            Persistable obj = (Persistable) ois.readObject();
            stats.recordLoad();
            return obj;
        }
    }
    
    /**
     * Delete object.
     */
    public void delete(String id) throws IOException {
        String filename = storePath + "/" + id + ".ser";
        Files.deleteIfExists(Paths.get(filename));
        stats.recordDelete();
    }
    
    /**
     * Check if object exists.
     */
    public boolean exists(String id) {
        String filename = storePath + "/" + id + ".ser";
        return Files.exists(Paths.get(filename));
    }
    
    /**
     * Get statistics.
     */
    public PersistenceStats getStats() {
        return stats;
    }
}
```

#### Step 3: Create PersistenceStats Class

```java
package com.learning;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Tracks persistence statistics.
 */
public class PersistenceStats {
    private AtomicLong saves = new AtomicLong(0);
    private AtomicLong loads = new AtomicLong(0);
    private AtomicLong deletes = new AtomicLong(0);
    
    /**
     * Record save.
     */
    public void recordSave() {
        saves.incrementAndGet();
    }
    
    /**
     * Record load.
     */
    public void recordLoad() {
        loads.incrementAndGet();
    }
    
    /**
     * Record delete.
     */
    public void recordDelete() {
        deletes.incrementAndGet();
    }
    
    /**
     * Display statistics.
     */
    public void displayStats() {
        System.out.println("\n========== PERSISTENCE STATS ==========");
        System.out.println("Saves: " + saves.get());
        System.out.println("Loads: " + loads.get());
        System.out.println("Deletes: " + deletes.get());
        System.out.println("========================================\n");
    }
}
```

#### Step 4: Create Main Class

```java
package com.learning;

import java.io.IOException;

/**
 * Main entry point for Object Persistence System.
 */
public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ObjectStore store = new ObjectStore("data");
        
        // Create and save objects
        Person person = new Person("John", 30);
        person.setId("person1");
        store.save(person);
        
        // Load object
        Person loaded = (Person) store.load("person1");
        System.out.println("Loaded: " + loaded.getName());
        
        // Display statistics
        store.getStats().displayStats();
    }
}

/**
 * Sample persistable class.
 */
class Person implements Persistable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private int age;
    
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
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
 * Unit tests for ObjectStore.
 */
public class ObjectStoreTest {
    
    private ObjectStore store;
    
    @BeforeEach
    void setUp(@TempDir Path tempDir) throws IOException {
        store = new ObjectStore(tempDir.toString());
    }
    
    @Test
    void testSaveAndLoad() throws IOException, ClassNotFoundException {
        Person person = new Person("John", 30);
        person.setId("person1");
        
        store.save(person);
        Person loaded = (Person) store.load("person1");
        
        assertEquals("John", loaded.getName());
    }
    
    @Test
    void testExists() throws IOException {
        Person person = new Person("Jane", 25);
        person.setId("person2");
        
        store.save(person);
        assertTrue(store.exists("person2"));
    }
    
    @Test
    void testDelete() throws IOException {
        Person person = new Person("Bob", 35);
        person.setId("person3");
        
        store.save(person);
        store.delete("person3");
        
        assertFalse(store.exists("person3"));
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

### Exercise 1: Custom Serialization

**Objective**: Implement custom serialization logic

**Task Description**:
Create class with custom serialization for special handling

**Acceptance Criteria**:
- [ ] Custom writeObject
- [ ] Custom readObject
- [ ] Special logic works
- [ ] Data preserved
- [ ] Tests pass

### Exercise 2: Version Migration

**Objective**: Handle version changes in serialized objects

**Task Description**:
Create system to migrate between serialization versions

**Acceptance Criteria**:
- [ ] Version tracking
- [ ] Migration logic
- [ ] Backward compatibility
- [ ] Data preservation
- [ ] Tests pass

### Exercise 3: Secure Serialization

**Objective**: Implement secure serialization

**Task Description**:
Create system with encryption and validation

**Acceptance Criteria**:
- [ ] Encryption
- [ ] Validation
- [ ] Security checks
- [ ] Error handling
- [ ] Tests pass

---

## 🧪 Quiz

### Question 1: What is serialization?

A) Compressing objects  
B) Converting objects to bytes  
C) Encrypting objects  
D) Deleting objects  

**Answer**: B) Converting objects to bytes

### Question 2: What is serialVersionUID?

A) A version number  
B) A unique identifier for serialization  
C) A security key  
D) A compression ratio  

**Answer**: B) A unique identifier for serialization

### Question 3: What does transient do?

A) Makes field temporary  
B) Prevents field from being serialized  
C) Encrypts field  
D) Compresses field  

**Answer**: B) Prevents field from being serialized

### Question 4: What is custom serialization?

A) Encrypting objects  
B) Implementing writeObject/readObject  
C) Compressing objects  
D) Deleting objects  

**Answer**: B) Implementing writeObject/readObject

### Question 5: When should you use Externalizable?

A) Always  
B) For complete control over serialization  
C) For simple objects  
D) Never  

**Answer**: B) For complete control over serialization

---

## 🚀 Advanced Challenge

### Challenge: Complete Serialization Framework

**Difficulty**: Advanced

**Objective**: Build comprehensive serialization framework

**Requirements**:
- [ ] Multiple serialization formats
- [ ] Version management
- [ ] Encryption support
- [ ] Compression
- [ ] Validation
- [ ] Performance optimization

---

## 🏆 Best Practices

### Serialization

1. **Always Define serialVersionUID**
   - Ensures compatibility
   - Prevents version conflicts
   - Required for production

2. **Mark Sensitive Fields Transient**
   - Protects passwords
   - Prevents data leaks
   - Improves security

3. **Implement Custom Serialization**
   - When needed for control
   - For performance optimization
   - For special handling

---

## 🔗 Next Steps

**Next Lab**: [Lab 19: Reflection](../19-reflection/README.md)

---

## ✅ Completion Checklist

- [ ] Completed all coding tasks
- [ ] Built persistence system
- [ ] Solved all exercises
- [ ] Passed quiz (80%+)
- [ ] Attempted advanced challenge
- [ ] Reviewed best practices

---

**Congratulations on completing Lab 18! 🎉**

You've mastered object serialization. Ready for reflection? Move on to [Lab 19: Reflection](../19-reflection/README.md).