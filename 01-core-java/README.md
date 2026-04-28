# 📚 Core Java 21+ - Complete Learning Path

<div align="center">

![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Status](https://img.shields.io/badge/Status-Active-success?style=for-the-badge)
![Modules](https://img.shields.io/badge/Modules-10-blue?style=for-the-badge)

**Master Core Java from fundamentals to advanced Java 21+ features**

</div>

---

## 🎯 Overview

This section covers everything you need to master Core Java, from basic syntax to advanced Java 21+ features including Virtual Threads, Pattern Matching, Records, and more.

---

## 📋 Module List

### 1️⃣ [Java Basics](./01-java-basics)
**Duration:** 4-6 hours | **Difficulty:** 🟢 Beginner

Learn Java fundamentals including:
- Variables, Data Types, and Operators
- Control Flow (if-else, switch, loops)
- Methods and Parameters
- Arrays and Strings
- Input/Output basics

**Key Topics:**
- ✅ Java syntax and structure
- ✅ Primitive and reference types
- ✅ String manipulation
- ✅ Basic I/O operations

---

### 2️⃣ [Object-Oriented Programming](./02-oop-concepts)
**Duration:** 6-8 hours | **Difficulty:** 🟡 Intermediate

Master OOP principles:
- Classes and Objects
- Encapsulation, Inheritance, Polymorphism
- Abstraction and Interfaces
- Access Modifiers
- Static and Final keywords

**Key Topics:**
- ✅ Class design principles
- ✅ Inheritance hierarchies
- ✅ Interface implementation
- ✅ Abstract classes vs Interfaces

---

### 3️⃣ [Collections Framework](./03-collections-framework)
**Duration:** 8-10 hours | **Difficulty:** 🟡 Intermediate

Deep dive into Java Collections:
- List, Set, Map interfaces
- ArrayList, LinkedList, HashSet, TreeSet
- HashMap, TreeMap, LinkedHashMap
- Queue and Deque
- Collections utility class

**Key Topics:**
- ✅ Collection interfaces and implementations
- ✅ Performance characteristics
- ✅ Choosing the right collection
- ✅ Custom comparators

---

### 4️⃣ [Streams API](./04-streams-api)
**Duration:** 6-8 hours | **Difficulty:** 🟡 Intermediate

Master functional programming with Streams:
- Stream creation and operations
- Intermediate operations (map, filter, flatMap)
- Terminal operations (collect, reduce, forEach)
- Parallel streams
- Stream performance

**Key Topics:**
- ✅ Functional programming concepts
- ✅ Stream pipelines
- ✅ Collectors and reduction
- ✅ Performance optimization

---

### 5️⃣ [Lambda Expressions](./05-lambda-expressions)
**Duration:** 4-6 hours | **Difficulty:** 🟡 Intermediate

Learn functional interfaces and lambdas:
- Lambda syntax
- Functional interfaces
- Method references
- Built-in functional interfaces
- Closures and scope

**Key Topics:**
- ✅ Lambda expressions syntax
- ✅ Predicate, Function, Consumer, Supplier
- ✅ Method and constructor references
- ✅ Effectively final variables

---

### 6️⃣ [Concurrency & Multithreading](./06-concurrency)
**Duration:** 10-12 hours | **Difficulty:** 🔴 Advanced

Master concurrent programming:
- Thread creation and lifecycle
- Synchronization and locks
- Executor framework
- Concurrent collections
- CompletableFuture
- Virtual Threads (Java 21)

**Key Topics:**
- ✅ Thread safety
- ✅ Synchronization mechanisms
- ✅ Thread pools
- ✅ Async programming
- ✅ Virtual threads

---

### 7️⃣ [Java I/O & NIO](./07-java-io-nio)
**Duration:** 6-8 hours | **Difficulty:** 🟡 Intermediate

Master file and network I/O:
- File I/O operations
- Streams (byte and character)
- Buffered I/O
- NIO.2 (Path, Files)
- Serialization

**Key Topics:**
- ✅ File operations
- ✅ Stream types
- ✅ NIO.2 API
- ✅ Object serialization

---

### 8️⃣ [Generics](./08-generics)
**Duration:** 6-8 hours | **Difficulty:** 🟡 Intermediate

Deep dive into Java Generics:
- Generic classes and methods
- Bounded type parameters
- Wildcards (?, extends, super)
- Type erasure
- Generic restrictions

**Key Topics:**
- ✅ Type safety
- ✅ Generic collections
- ✅ Bounded wildcards
- ✅ PECS principle

---

### 9️⃣ [Reflection & Annotations](./09-reflection-annotations)
**Duration:** 6-8 hours | **Difficulty:** 🔴 Advanced

Learn metaprogramming in Java:
- Reflection API
- Class, Method, Field inspection
- Custom annotations
- Annotation processing
- Dynamic proxies

**Key Topics:**
- ✅ Runtime type inspection
- ✅ Dynamic invocation
- ✅ Custom annotations
- ✅ Annotation processors

---

### 🔟 [Java 21 Features](./10-java-21-features)
**Duration:** 8-10 hours | **Difficulty:** 🔴 Advanced

Explore cutting-edge Java 21+ features:
- Virtual Threads (Project Loom)
- Pattern Matching for switch
- Record Patterns
- Sequenced Collections
- String Templates (Preview)
- Structured Concurrency

**Key Topics:**
- ✅ Virtual threads
- ✅ Pattern matching
- ✅ Records and sealed classes
- ✅ Modern Java syntax

---

## 🚀 Getting Started

### Prerequisites
```bash
☕ Java 21+ installed
📦 Maven 3.8+ or Gradle 7+
🔧 IDE (IntelliJ IDEA recommended)
```

### Quick Start
```bash
# Navigate to any module
cd 01-java-basics

# Compile and run
javac src/main/java/com/learning/*.java
java -cp src/main/java com.learning.Main

# Or use Maven
mvn clean compile
mvn exec:java
```

---

## 📊 Learning Path

```mermaid
graph LR
    A[Java Basics] --> B[OOP Concepts]
    B --> C[Collections]
    C --> D[Streams API]
    D --> E[Lambda Expressions]
    E --> F[Concurrency]
    F --> G[I/O & NIO]
    G --> H[Generics]
    H --> I[Reflection]
    I --> J[Java 21 Features]
    
    style A fill:#4CAF50
    style J fill:#FF9800
```

---

## 🎯 Learning Objectives

By completing this section, you will:

✅ Write clean, efficient Java code  
✅ Understand OOP principles deeply  
✅ Master Java Collections Framework  
✅ Use functional programming effectively  
✅ Write concurrent and parallel code  
✅ Handle I/O operations efficiently  
✅ Use generics for type safety  
✅ Apply reflection and annotations  
✅ Leverage modern Java 21+ features  

---

## 📚 Recommended Learning Order

### For Beginners
1. Java Basics
2. OOP Concepts
3. Collections Framework
4. Lambda Expressions
5. Streams API

### For Intermediate Developers
1. Concurrency & Multithreading
2. I/O & NIO
3. Generics
4. Java 21 Features

### For Advanced Developers
1. Reflection & Annotations
2. Java 21 Features
3. Performance Optimization
4. Design Patterns

---

## 🏆 Projects

Each module includes hands-on projects:

1. **Calculator Application** (Basics)
2. **Library Management System** (OOP)
3. **Student Grade Analyzer** (Collections)
4. **Data Processing Pipeline** (Streams)
5. **Task Scheduler** (Concurrency)
6. **File Manager** (I/O)
7. **Generic Data Structures** (Generics)
8. **Plugin System** (Reflection)
9. **Modern Java Application** (Java 21)

---

## 📖 Additional Resources

### Books
- "Effective Java" by Joshua Bloch
- "Java Concurrency in Practice" by Brian Goetz
- "Core Java Volume I & II" by Cay S. Horstmann

### Online Resources
- [Oracle Java Tutorials](https://docs.oracle.com/javase/tutorial/)
- [Java Documentation](https://docs.oracle.com/en/java/)
- [Baeldung Java Tutorials](https://www.baeldung.com/)

### Practice Platforms
- [LeetCode](https://leetcode.com/)
- [HackerRank](https://www.hackerrank.com/domains/java)
- [Exercism](https://exercism.org/tracks/java)

---

## ✅ Progress Tracker

- [ ] Module 01: Java Basics
- [ ] Module 02: OOP Concepts
- [ ] Module 03: Collections Framework
- [ ] Module 04: Streams API
- [ ] Module 05: Lambda Expressions
- [ ] Module 06: Concurrency
- [ ] Module 07: I/O & NIO
- [ ] Module 08: Generics
- [ ] Module 09: Reflection & Annotations
- [ ] Module 10: Java 21 Features

---

<div align="center">

**Ready to master Core Java?**

[Start with Module 01 →](./01-java-basics)

</div>