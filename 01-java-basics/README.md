# Java Basics Module

## Overview

This module covers fundamental Java programming concepts essential for any Java developer. Students will learn variables, data types, control flow, methods, arrays, and basic object-oriented programming.

## Learning Objectives

- Understand Java syntax and basic constructs
- Master variables, data types, and operators
- Implement control flow with loops and conditionals
- Create and use methods effectively
- Work with arrays and collections basics
- Apply basic OOP principles

## Module Structure

| Document | Purpose |
|----------|---------|
| README.md | Module overview and navigation |
| DEEP_DIVE.md | Comprehensive concept explanations |
| EXERCISES.md | Practice problems with solutions |
| QUIZZES.md | Knowledge assessment tests |
| EDGE_CASES.md | Tricky scenarios and corner cases |
| PEDAGOGIC_GUIDE.md | Teaching methodology |
| PROJECTS.md | Hands-on project implementations |

## Topics Covered

1. **Variables and Data Types**
   - Primitive types (int, double, boolean, etc.)
   - Reference types (String, Objects)
   - Type conversion and casting

2. **Operators**
   - Arithmetic operators
   - Comparison and logical operators
   - Bitwise operators

3. **Control Flow**
   - If-else statements
   - Switch statements
   - Loops (for, while, do-while)
   - Break and continue

4. **Methods**
   - Method declaration and invocation
   - Parameters and return values
   - Method overloading
   - Recursion basics

5. **Arrays**
   - Single-dimensional arrays
   - Multi-dimensional arrays
   - Array manipulation
   - Common array algorithms

6. **Basic OOP**
   - Classes and objects
   - Constructors
   - Encapsulation with getters/setters
   - Static members

## Prerequisites

- Basic computer literacy
- Understanding of programming concepts (optional)
- Java Development Kit (JDK 17+)

## Estimated Duration

- **Total**: 20-25 hours
- **Lessons**: 10-12 hours
- **Exercises**: 5-8 hours
- **Projects**: 5-8 hours

## Getting Started

```bash
# Verify Java installation
java -version

# Compile and run a basic program
javac HelloWorld.java
java HelloWorld
```

## Quick Examples

### Hello World
```java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, Java!");
    }
}
```

### Variables and Types
```java
int age = 25;
double salary = 75000.50;
boolean isActive = true;
String name = "John";
```

### Control Flow
```java
if (age >= 18) {
    System.out.println("Adult");
} else {
    System.out.println("Minor");
}

for (int i = 0; i < 5; i++) {
    System.out.println("Count: " + i);
}
```

## Next Steps

After completing this module, proceed to:
- **Module 2**: Object-Oriented Programming
- **Module 3**: Java Collections Framework
- **Module 4**: Stream API

## Additional Resources

- [Oracle Java Tutorials](https://docs.oracle.com/javase/tutorial/)
- [Java Language Specification](https://docs.oracle.com/javase/specs/jls/se17/html/)
- [Effective Java (Book)](https://www.oreilly.com/library/view/effective-java/9780134686097/)
