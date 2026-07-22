# Interview Questions: JVM Class Loading & Bytecode

## Company-Specific Focus

### Google
- Class loading: loading, linking (verification, preparation, resolution), initialization
- Class loader hierarchy: Bootstrap, Platform, Application
- Custom class loaders: URLClassLoader, defining custom loading strategies

### Microsoft
- Java class loading vs .NET assembly loading
- Bytecode format vs MSIL (CIL)
- Verification in JVM vs PEVerify in .NET

### Amazon
- Class loading in containerized environments
- Bytecode manipulation: ASM, ByteBuddy, cglib for proxying
- HotSwap: replacing code at runtime via DCEVM

### Meta
- Bytecode analysis: reading class files, constant pool, instruction sets
- Proxy classes and dynamic class generation
- Instrumentation: java.lang.instrument for agents

### Apple
- Bytecode verification: ensuring type safety at the JVM level
- Class file format: magic number, version, constant pool
- Security: class loading and code source

### Oracle
- JVM Specification: The class file format (Chapter 4)
- Bytecode instruction set: 256+ instructions, stack based
- Class loading delegation model
- DefineClass vs LoadClass/FindClass

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| (No direct LC problems — bytecode is a JVM implementation concept) |
| 208 Implement Trie | Medium | Amazon, Google | Class loading analogous to trie with delegation |

## Real Production Scenarios
- **Uber**: Custom class loader for hot reloading plugin modules in microservice architecture
- **Netflix**: ByteBuddy generated proxy classes for Hystrix command wrapping
- **LinkedIn**: ClassLoader leak from redeployment in Tomcat — classes in permgen not GC'd

## Interview Patterns & Tips
- **Delegation model**: parent-first loading, child-first for application servers
- **ClassLoader leak**: a common issue in app servers — the loaded class references a class from a stale classloader
- **Instrumentation**: java agents for profiling, monitoring, AOP

## Deep Dive Questions
- **Class file**: Describe the major sections of the class file format
- **Bytecode**: What is the stack-based execution model?
- **Verification**: What does the bytecode verifier check?
- **Constant pool**: What is stored in the constant pool?
- **ASM**: How do libraries like ASM generate bytecode? (ClassWriter, MethodVisitor)