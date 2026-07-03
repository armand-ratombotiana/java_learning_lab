# How Core Java Works

## Compilation Process

The Java compiler (javac) transforms source code into bytecode - a platform-independent intermediate representation. This bytecode is stored in .class files and can be executed on any JVM.

## JVM Architecture

The JVM consists of:
- **Class Loader**: Loads classes from various sources
- **Bytecode Verifier**: Ensures code safety
- **Interpreter**: Executes bytecode line by line
- **JIT Compiler**: Optimizes hot paths to native code
- **Garbage Collector**: Manages heap memory

## Method Execution

When a method is called:
1. A new stack frame is pushed onto the stack
2. Parameters and local variables are allocated
3. Method body executes
4. Return value is placed on the caller's stack
5. Stack frame is popped

## Object Lifecycle

1. Object created via `new` keyword
2. Constructor initializes the object
3. Object is used via references
4. When no references remain, GC reclaims memory