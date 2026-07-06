# Exercises: Class Loading & Bytecode

## Exercise 1: ClassLoader View
Write a program that prints all classes in the current namespace with their ClassLoader. Use `Class.forName()` with various names and print the ClassLoader for each.

## Exercise 2: Custom ClassLoader for Versioned Classes
Implement a ClassLoader that loads classes from versioned directories. Given "com.example.Foo", it looks for "classes/v1/com/example/Foo.class" and "classes/v2/com/example/Foo.class". Use the latest version.

## Exercise 3: Constant Pool Analyzer
Extend BytecodeAnalyzer to fully parse the constant pool. For each entry type, print the resolved value. Compare with `javap -v`.

## Exercise 4: ASM Method Timer
Write an ASM transformer that adds timing code to every method. On method entry, record `System.nanoTime()`. On exit, print the elapsed time.

## Exercise 5: Lambda Desugaring
Write a program that loads a class file containing lambdas and uses ASM to find all invokedynamic instructions. Print the bootstrap method and constant pool arguments for each.

## Exercise 6: Class Dependency Graph
Write a tool that reads a class file and prints all classes it references (in the constant pool). Build a dependency graph and identify circular dependencies.

## Exercise 7: Bytecode Obfuscator
Write an ASM-based bytecode obfuscator that renames local variables, adds dummy code, and reorders instructions where semantically safe.
