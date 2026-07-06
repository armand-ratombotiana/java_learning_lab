# Mathematical Foundation of Class Loading

## Class File Size
The minimum class file size is approximately 80 bytes (empty class). Each field adds ~8 bytes (header + descriptor). Each method adds ~15 bytes (header + bytecode). A class with 10 fields and 10 methods is roughly 1-2 KB. The constant pool grows with the number of unique strings, types, and references.

## Class Loading Time
Loading a class involves:
- Disk I/O: ~10-100 μs (SSD) or 1-10 ms (HDD)
- Bytecode verification: ~1-100 μs per class
- Linking (resolution): ~10 ns per resolved reference
- Class initialization: depends on static initializer complexity

For a typical Spring Boot application with 50,000 classes, startup time can be 5-30 seconds depending on class loading overhead.

## Memory Overhead per Class
Each loaded class consumes:
- Klass metadata: ~500-1000 bytes
- Constant pool cache: ~4 bytes per entry (on average)
- Method tables: ~8 bytes per method (vtables)
- JIT compiled code: varies (10-100 KB per hot method)

With 50,000 classes, class metadata alone can be 50-100 MB.

## Bytecode Instruction Size
Bytecode instructions are variable-length:
- 1 byte for opcode (most common)
- 1-4 bytes for operands (indices, offsets, constants)
- Average instruction size: ~2.5 bytes
- A method with 100 instructions averages ~250 bytes of bytecode

## Constant Pool Entropy
The constant pool size correlates with code complexity. For each unique string, type reference, field reference, or method reference, an entry is added. Method calls to different classes add entries for each class, method name, and descriptor. The constant pool entropy determines the class file's compressibility.

## Class Unloading Overhead
When a ClassLoader dies, the JVM must scan all thread stacks and heap for references to its classes. This is similar to GC root scanning but applies only to class metadata. The cost is proportional to the number of classes loaded by the dying ClassLoader.
