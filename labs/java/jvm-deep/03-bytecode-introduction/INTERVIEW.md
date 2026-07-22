# Interview Questions: Bytecode Introduction

## Company-Specific Focus

### Google
- JVM bytecode instruction set: 200+ instructions, stack-based architecture
- Instruction categories: load/store, arithmetic, object/array, control transfer, invocation
- Type-specific instructions: aload, iload, fload, dload for different types

### Microsoft
- JVM bytecode vs CIL (.NET): both stack-based, different instruction sets
- Verification: ensuring type safety through bytecode verification

### Amazon
- Bytecode optimization: understanding how the JIT optimizes common patterns
- Bytecode patterns: how for-each loops, switch statements, try-catch compile

### Meta
- Method invocation: invokevirtual, invokespecial, invokestatic, invokeinterface, invokedynamic
- Stack frames: local variable array, operand stack, frame data

### Apple
- Bytecode decompilation: javap -c to see JVM instructions
- Constant pool references: how instructions reference constants

### Oracle
- JVM Specification Chapter 6: instruction set
- Stack-based execution: operands from operand stack
- Frame management: creating and destroying frames

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| (No direct LC problems — bytecode is JVM implementation) |

## Real Production Scenarios
- **Netflix**: Bytecode instrumentation for monitoring — Java agent added bytecode to track method timing
- **LinkedIn**: Bytecode analysis revealed inefficient synchronized block placement in hot path

## Interview Patterns & Tips
- **Stack-based**: operations pop operands from stack, push results
- **Local variable table**: indexed storage for method parameters and local variables
- **Invocation**: invokevirtual (instance method), invokestatic (static method)
- **Return**: return, ireturn, lreturn, freturn, dreturn, areturn

## Deep Dive Questions
- **Iconst instructions**: How does the JVM push constants?
- **Method invocation**: What is the difference between invokevirtual and invokeinterface?
- **Frame creation**: When are frames created and destroyed?
- **Operand stack**: How deep can the operand stack be?
- **Verification**: How does the bytecode verifier check stack consistency?