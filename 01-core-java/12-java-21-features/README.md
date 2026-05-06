# Module 12: Java 21 Features

<div align="center">

![Module](https://img.shields.io/badge/Module-12-blue?style=for-the-badge)
![Difficulty](https://img.shields.io/badge/Difficulty-Advanced-red?style=for-the-badge)
![Duration](https://img.shields.io/badge/Duration-40%20hours-orange?style=for-the-badge)

**Master Modern Java 21+ Features - Virtual Threads, Pattern Matching, Records, and More**

</div>

---

## 📚 Table of Contents

1. [Overview](#overview)
2. [Learning Objectives](#learning-objectives)
3. [Module Structure](#module-structure)
4. [Getting Started](#getting-started)
5. [Key Features](#key-features)
6. [Feature Categories](#feature-categories)
7. [Exercises & Projects](#exercises--projects)
8. [Assessment](#assessment)
9. [Resources](#resources)

---

## 🎯 Overview

This module covers the cutting-edge features introduced in Java 21 and beyond. Java 21 represents a major milestone with significant improvements in concurrency, pattern matching, and language expressiveness.

**Key Focus Areas:**
- ✅ Virtual Threads (Project Loom)
- ✅ Pattern Matching for switch
- ✅ Record Patterns
- ✅ Sequenced Collections
- ✅ String Templates (Preview)
- ✅ Structured Concurrency
- ✅ Foreign Function & Memory API
- ✅ Sealed Classes and Records

---

## 📖 Learning Objectives

By completing this module, you will:

- ✅ Understand virtual threads and their benefits
- ✅ Master pattern matching in switch statements
- ✅ Use records and sealed classes effectively
- ✅ Work with sequenced collections
- ✅ Implement structured concurrency
- ✅ Use string templates for cleaner code
- ✅ Leverage modern Java features
- ✅ Migrate existing code to Java 21

---

## 🏗️ Module Structure

### Documentation Files
- **README.md** - Overview and quick start (this file)
- **PEDAGOGIC_GUIDE.md** - Learning philosophy and concepts
- **QUICK_REFERENCE.md** - Quick lookup guide
- **DEEP_DIVE.md** - Advanced topics and theory
- **QUIZZES.md** - Self-assessment questions
- **EDGE_CASES.md** - Pitfalls and prevention
- **EXERCISES.md** - 25+ exercises by difficulty
- **PROJECTS.md** - 3-5 real-world projects

### Code Structure
```
src/main/java/com/learning/
├── virtualthreads/
│   ├── VirtualThreadBasics.java
│   ├── VirtualThreadPerformance.java
│   ├── VirtualThreadScheduling.java
│   ├── VirtualThreadMigration.java
│   └── StructuredConcurrency.java
├── patternmatching/
│   ├── PatternMatchingSwitch.java
│   ├── TypePatterns.java
│   ├── RecordPatterns.java
│   ├── ArrayPatterns.java
│   ├── GuardClauses.java
│   └── NestedPatterns.java
├── records/
│   ├── RecordBasics.java
│   ├── RecordPatterns.java
│   ├── SealedClasses.java
│   └── RecordInheritance.java
├── sequencedcollections/
│   ├── SequencedCollectionBasics.java
│   ├── SequencedMapBasics.java
│   └── SequencedSetBasics.java
├── stringtemplates/
│   ├── StringTemplateBasics.java
│   ├── StringTemplateExpressions.java
│   └── StringTemplateProcessors.java
├── foreignfunction/
│   ├── ForeignFunctionBasics.java
│   └── MemoryAPIBasics.java
└── Examples/
    └── [Feature examples and demonstrations]
```

---

## 🚀 Getting Started

### Prerequisites
- ✅ Java 21+ installed
- ✅ Complete all previous modules (01-11)
- ✅ Understand concurrency concepts
- ✅ Familiar with functional programming

### Quick Start
```bash
# Navigate to module
cd 12-java-21-features

# Compile code (with preview features)
mvn clean compile

# Run tests
mvn test

# Run specific example
mvn exec:java -Dexec.mainClass="com.learning.virtualthreads.VirtualThreadBasics"
```

---

## 🔑 Key Features

### 1. Virtual Threads (Project Loom)
**Purpose:** Lightweight threads for high-concurrency applications

- **Benefits:** Millions of threads, simpler code
- **Use Cases:** Web servers, I/O-bound applications
- **Performance:** 1000x more threads than platform threads
- **Compatibility:** Drop-in replacement for Thread

### 2. Pattern Matching for switch
**Purpose:** More expressive and safer switch statements

- **Type Patterns:** Match types directly
- **Record Patterns:** Destructure records
- **Array Patterns:** Match array elements
- **Guard Clauses:** Add conditions to patterns
- **Nested Patterns:** Combine patterns

### 3. Records
**Purpose:** Immutable data carriers with less boilerplate

- **Compact Constructors:** Validate data
- **Record Patterns:** Destructure records
- **Sealed Records:** Restrict subclasses
- **Benefits:** Less code, more safety

### 4. Sealed Classes
**Purpose:** Control class hierarchy

- **Restrict Subclasses:** Only permitted classes can extend
- **Pattern Matching:** Exhaustive pattern matching
- **Design:** Better encapsulation
- **Safety:** Compiler-checked inheritance

### 5. Sequenced Collections
**Purpose:** Collections with defined encounter order

- **SequencedCollection:** Ordered collections
- **SequencedSet:** Ordered sets
- **SequencedMap:** Ordered maps
- **Benefits:** Consistent API, reverse iteration

### 6. String Templates (Preview)
**Purpose:** Cleaner string interpolation

- **Template Expressions:** Embed expressions in strings
- **Template Processors:** Custom processing
- **Benefits:** Safer than concatenation
- **Use Cases:** SQL, JSON, HTML generation

### 7. Structured Concurrency
**Purpose:** Manage concurrent tasks as a unit

- **Task Scope:** Group related tasks
- **Cancellation:** Cancel all tasks together
- **Error Handling:** Aggregate exceptions
- **Benefits:** Simpler concurrent code

### 8. Foreign Function & Memory API
**Purpose:** Interop with native code safely

- **Foreign Function:** Call native functions
- **Memory API:** Manage off-heap memory
- **Benefits:** Type-safe, performant
- **Use Cases:** JNI replacement, performance-critical code

---

## 💪 Exercises & Projects

### Exercises
- **25+ Exercises** organized by difficulty
- Easy: Basic feature usage
- Medium: Feature combination
- Hard: Real-world scenarios
- Interview: FAANG-style questions

### Projects
- **Project 1:** Virtual Thread Web Server
- **Project 2:** Pattern Matching Application
- **Project 3:** Record-based Data Processing
- **Project 4:** Structured Concurrency Task Manager
- **Project 5:** Modern Java Application

---

## ✅ Assessment

### Quizzes
- **20+ Quiz Questions** covering all features
- Multiple difficulty levels
- Detailed explanations
- Answer summary tables

### Interview Questions
- **30+ Interview Questions** from top companies
- Feature usage scenarios
- Performance questions
- Migration questions

### Design Questions
- **10+ Design Questions** for system design
- Feature selection
- Performance optimization
- Scalability considerations

---

## 📚 Resources

### Official Documentation
- [Java 21 Release Notes](https://www.oracle.com/java/technologies/javase/21-relnotes.html)
- [Java Language Updates](https://docs.oracle.com/en/java/javase/21/language/)
- [Virtual Threads Guide](https://docs.oracle.com/en/java/javase/21/core/virtual-threads.html)

### Books
- "Modern Java in Action" by Raoul-Gabriel Urma
- "Effective Java" by Joshua Bloch
- "Java Concurrency in Practice" by Brian Goetz

### Online Resources
- [Baeldung Java 21](https://www.baeldung.com/java-21-new-features)
- [Java 21 Features](https://www.oracle.com/java/technologies/javase/21-relnotes.html)
- [Virtual Threads Tutorial](https://docs.oracle.com/en/java/javase/21/core/virtual-threads.html)

---

## 🎓 Learning Path

### Beginner (Week 1)
1. Understand Java 21 overview
2. Learn virtual threads basics
3. Learn pattern matching basics
4. Complete exercises 1-10

### Intermediate (Week 2)
1. Learn records and sealed classes
2. Learn sequenced collections
3. Complete exercises 11-20
4. Start projects 1-2

### Advanced (Week 3)
1. Learn string templates
2. Learn structured concurrency
3. Complete exercises 21-25
4. Complete projects 3-4

### Expert (Week 4)
1. Learn foreign function API
2. Migrate existing code
3. Optimize with new features
4. Interview preparation

---

## 🔄 Migration Guide

### From Java 8 to Java 21
- [ ] Update syntax to use records
- [ ] Replace threads with virtual threads
- [ ] Use pattern matching in switch
- [ ] Leverage sealed classes
- [ ] Use sequenced collections
- [ ] Adopt string templates

### Performance Improvements
- Virtual threads: 1000x more threads
- Pattern matching: Cleaner code
- Records: Less memory overhead
- Sealed classes: Better optimization

---

## ✨ Next Steps

### After Completing This Module
- [ ] Review all Java 21 features
- [ ] Complete all exercises
- [ ] Build all projects
- [ ] Take all quizzes
- [ ] Prepare for interviews

### Career Development
- [ ] Become Java 21 expert
- [ ] Lead modernization efforts
- [ ] Mentor junior developers
- [ ] Contribute to open source

---

<div align="center">

## 🎯 Module 12: Java 21 Features

**Duration:** 40 hours

**Difficulty:** Advanced

**Status:** Ready to Learn

---

**Ready to Master Modern Java?**

[Start with PEDAGOGIC_GUIDE →](./PEDAGOGIC_GUIDE.md)

[View Quick Reference →](./QUICK_REFERENCE.md)

[Take Quizzes →](./QUIZZES.md)

---

⭐ **Java 21 is the future of Java development!**

</div>