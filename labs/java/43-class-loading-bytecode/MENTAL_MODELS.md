# Mental Models for Class Loading

## ClassLoader as a Library Catalog
Think of a ClassLoader as a library catalog. When you need a book (class), you ask the nearest catalog (Application ClassLoader). If it doesn't have it, your request goes up the chain to the regional catalog (Platform), then the national catalog (Bootstrap). Each catalog checks its collection before forwarding. If no catalog has the book, a ClassNotFoundException is thrown.

## Delegation as a Veto Chain
The delegation model is like a chain of approval. When a request comes in, it starts at the top (Bootstrap). Each level can either approve (load the class) or pass to the next. The bottom level is the last resort. This ensures standard classes are always loaded by the highest authority.

## Class File Format as a Shipping Container
The .class file is like a standardized shipping container. The magic number (0xCAFEBABE) is the container ID. The version is the shipping manifest. The constant pool is the inventory list. Methods and fields are the cargo. Attributes are handling instructions. Every container follows the same format, making them interchangeable across platforms.

## Bytecode as Assembly for the JVM
Bytecode is to the JVM what assembly language is to a CPU. Each instruction (opcode + operands) performs a primitive operation: load a value, add two numbers, jump to a label, invoke a method. The operand stack is like CPU registers. The local variable array is like stack memory. The bytecode verifier is like an assembler that checks instruction validity.

## ASM Visitor as a Factory Inspector
The ASM visitor pattern is like a factory inspector walking through an assembly line. `ClassReader` is the conveyor belt feeding the class file. `ClassVisitor` is the inspector who looks at each component (fields, methods, attributes). `ClassWriter` is the assembly robot that builds the new class file. The inspector can add, remove, or modify components as they pass by.

## invokedynamic as a Mail Forwarding Service
invokedynamic is like a mail forwarding service. When you send a letter (call a method), the address is checked by a directory service (bootstrap method). The directory returns the current address (MethodHandle target). The letter is then delivered efficiently (optimizable by JIT). If the recipient moves (call site retargeting), only the directory changes — the letter-sending code stays the same.
