# Solutions: Class Loading & Bytecode

## ClassLoaderHierarchy.java
Prints the class loader chain: Application ClassLoader (loads from classpath) → Platform ClassLoader (JDK modules) → Bootstrap ClassLoader (null, C++ implementation). The Platform ClassLoader's `definedPackages()` shows which modules it owns.

## CustomClassLoader.java
Extends `ClassLoader` and overrides `findClass()`. The parent is set to Platform ClassLoader (not the Application ClassLoader) to demonstrate delegation. When `loadClass()` is called, the parent is checked first. Only if the parent cannot find the class does `findClass()` attempt to load from the custom directory.

## BytecodeAnalyzer.java
Parses the binary `.class` file format using `DataInputStream`. The magic number `0xCAFEBABE` appears at offset 0. Version is read as minor and major (e.g., 65.0 = Java 21). The constant pool is scanned by reading each entry's tag byte and skipping the appropriate number of bytes based on the tag type.

## AsmTransformer.java
Uses ASM's `ClassReader` to read the original bytecode, a `ClassVisitor` to transform it, and `ClassWriter` to produce the new bytecode. The `visitEnd()` callback injects a class-level annotation. The transformed bytes contain an additional `RuntimeVisibleAnnotation` attribute in the class file.

## InvokeDynamicExample.java
Lambda expressions compile to `invokedynamic` with a bootstrap method `LambdaMetafactory.metafactory()`. The demo manually calls `LambdaMetafactory.metafactory()` to create a `CallSite` at runtime. The `CallSite.getTarget()` returns a `MethodHandle` pointing to the generated function implementation.
