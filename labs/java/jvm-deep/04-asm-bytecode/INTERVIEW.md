# Interview Questions: ASM Bytecode Framework

## Company-Specific Focus

### Google
- ASM: lightweight bytecode manipulation framework
- ClassReader: reading class files
- ClassWriter: writing (generating) class files
- ClassVisitor: visitor pattern for traversing class file structures

### Microsoft
- ASM vs Mono.Cecil (.NET): bytecode manipulation libraries
- Tree API vs Visitor API: when to use each

### Amazon
- ASM for performance monitoring: adding bytecode instrumentation
- ByteBuddy vs ASM: higher-level API on top of ASM
- CGLIB (deprecated) to ASM: migration path

### Meta
- ASM for dynamic proxy generation
- MethodVisitor: adding/modifying method bytecode
- Opcodes: instruction constants (ALOAD, GETFIELD, PUTFIELD, etc.)

### Apple
- ASM for static analysis tools
- Type resolution: Type.getType() for internal names

### Oracle
- ASM is maintained by the OW2 Consortium
- Java agent + ASM: runtime bytecode transformation
- ClassFileTransformer: premain/agentmain for bytecode transformation

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| (No direct LC problems — ASM is a bytecode manipulation tool) |

## Real Production Scenarios
- **New Relic**: Uses ASM for bytecode instrumentation of Java applications
- **IntelliJ**: Uses ASM for code analysis and refactoring
- **Spring Framework**: Uses ASM for class metadata reading and proxy generation

## Interview Patterns & Tips
- **Visitor pattern**: ClassReader -> ClassVisitor -> ClassWriter pipeline
- **AdviceAdapter**: Add before/after method calls
- **Tree API**: modify class structure as a tree

## Deep Dive Questions
- **ClassReader**: How does ClassReader parse class files?
- **MethodVisitor**: How do you modify method bytecode?
- **AdviceAdapter**: How is entry/exit advice added to methods?
- **Opcodes**: What are common opcodes used in ASM?
- **Performance**: What is the overhead of ASM transformation?