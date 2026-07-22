# Interview Questions: Class File Format

## Company-Specific Focus

### Google
- Class file structure: magic number (0xCAFEBABE), version, constant pool, access flags, this/super class, interfaces, fields, methods, attributes
- Major/minor version: mapping to JDK releases
- Constant pool: types of constants (Utf8, Integer, Float, Long, Double, String, Class, MethodRef, etc.)

### Microsoft
- Class file format vs .NET PE/COFF and metadata
- Bytecode verification: type safety at class loading time

### Amazon
- Class file structure understanding for bytecode manipulation
- Constant pool analysis for class size optimization

### Meta
- Access flags: public, private, static, final, abstract, etc.
- Method descriptor: (Lpackage/Class;)V format for type encoding

### Apple
- Class file decompilation: understanding JVM bytecode
- Attributes collection: Code, LineNumberTable, LocalVariableTable, Exceptions

### Oracle
- JVM Specification Chapter 4: class file format
- Class file parser in OpenJDK
- Evolution of class file format across Java versions

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| (No direct LC problems — class file format is JVM internals) |

## Real Production Scenarios
- **LinkedIn**: Invalid class file format caused JVM crash after JDK upgrade — had to recompile all jars
- **Netflix**: Class file version mismatch in application server — mixing JDK 11 and JDK 17 libraries

## Interview Patterns & Tips
- **Magic number**: 0xCAFEBABE identifies a class file
- **Constant pool**: all class references, method references, and string literals
- **Major version**: 61 = JDK 17, 65 = JDK 21
- **Internal form**: fully qualified names use / not . (e.g., java/lang/Object)

## Deep Dive Questions
- **Class file structure**: List major sections of the class file in order
- **Constant pool**: What types of constants exist in the constant pool?
- **Access flags**: What are the access flags for a class, method, field?
- **Descriptor**: How are method descriptors encoded?
- **Attributes**: What is the Code attribute and what does it contain?