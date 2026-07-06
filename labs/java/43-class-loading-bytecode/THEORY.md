# Class Loading Theory

## The Class Loading Process
Class loading happens lazily — classes are loaded on first use (new, getstatic, invokestatic, etc.). The process has three phases:
1. **Loading**: Find the binary representation (.class bytes) and create a Class object
2. **Linking**: Verify bytecode, prepare static fields, resolve symbolic references
3. **Initialization**: Execute static initializers (static blocks, static field assignments)

## Delegation Model
The ClassLoader hierarchy follows a delegation model:
- When asked to load a class, a ClassLoader first delegates to its parent
- If the parent can't find the class, the child attempts to load it
- This ensures core Java classes (java.lang.String) are always loaded by the Bootstrap ClassLoader, preventing malicious replacements

## Class File Format
The .class file follows a strict binary format:
```
ClassFile {
    u4             magic;
    u2             minor_version;
    u2             major_version;
    u2             constant_pool_count;
    cp_info        constant_pool[constant_pool_count-1];
    u2             access_flags;
    u2             this_class;
    u2             super_class;
    u2             interfaces_count;
    u2             interfaces[interfaces_count];
    u2             fields_count;
    field_info     fields[fields_count];
    u2             methods_count;
    method_info    methods[methods_count];
    u2             attributes_count;
    attribute_info attributes[attributes_count];
}
```

## Bytecode Instruction Set
Java bytecode has ~256 instructions, grouped into categories:
- **Load/store**: aload, iload, astore, istore (move values between local vars and stack)
- **Arithmetic**: iadd, isub, imul, idiv (operate on stack values)
- **Object**: new, getfield, putfield, instanceof, checkcast
- **Control flow**: ifeq, goto, tableswitch, lookupswitch
- **Method invocation**: invokevirtual, invokespecial, invokestatic, invokeinterface, invokedynamic

## ASM Library
ASM is a bytecode manipulation framework that reads, writes, and transforms Java bytecode. Key classes:
- ClassReader: reads existing bytecode
- ClassWriter: generates new bytecode
- ClassVisitor: transformation visitor pattern
- MethodVisitor: method-level bytecode manipulation
- AdviceAdapter: convenient hooks for method entry/exit

## invokedynamic
invokedynamic (Java 7) defers method dispatch to a bootstrap method at runtime. The bootstrap method returns a CallSite with a MethodHandle target. Lambdas compile to invokedynamic where the bootstrap method is `LambdaMetafactory.metafactory()`. This enables flexible dispatch for dynamic languages and lambda expressions.
