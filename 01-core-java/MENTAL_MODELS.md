# Mental Models for Core Java

## Object-Oriented Thinking

Core Java is fundamentally object-oriented. Think of objects as:
- **Entities** with identity, state, and behavior
- **Blueprints** (classes) that define what objects can do
- **Contracts** (interfaces) that define what objects must implement

## Memory Model

Java separates memory into:
- **Stack**: Method calls, local variables
- **Heap**: Objects, arrays, class data
- Understanding this separation is crucial for performance

## Type System

Java uses:
- **Primitive types**: int, double, boolean, etc.
- **Reference types**: Objects, arrays, interfaces
- **Type erasure**: Generics exist at compile-time only

## Execution Flow

1. Source code (.java)
2. Compile to bytecode (.class)
3. JVM loads and interprets bytecode
4. Just-in-time compilation to native code
5. Execution on the underlying platform