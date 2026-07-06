# Class Loading & Bytecode Engineering — Overview

This lab explores JVM class loading architecture, the class file format, bytecode instruction set, ASM bytecode engineering, and invokedynamic.

## Learning Objectives
- Understand the Bootstrap, Platform, and Application ClassLoader hierarchy
- Build a custom ClassLoader for loading classes from non-standard locations
- Parse and analyze .class file structure (magic, constant pool, methods)
- Use ASM to transform bytecode at class load time
- Explain how invokedynamic enables lambdas and dynamic languages

## Prerequisites
- Java 21+
- Basic understanding of the JVM architecture
- Familiarity with Java class files and command-line tools

## Files in This Lab
- Java sources: `src/main/java/com/javaacademy/lab43/classloading/`
- Tests: `src/test/java/com/javaacademy/lab43/classloading/`
- 24 documentation .md files covering class loading and bytecode in depth
