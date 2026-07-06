# Code Deep Dive: Class Loading & Bytecode

## ClassLoaderHierarchy.java Analysis
This simple program traverses the ClassLoader chain by repeatedly calling `getParent()`. The Application ClassLoader is returned by `ClassLoaderHierarchy.class.getClassLoader()`. `getParent()` returns the Platform ClassLoader. The Platform ClassLoader's parent returns null (Bootstrap). The Platform ClassLoader's `definedPackages()` shows platform module packages — each package belongs to a specific module (java.base, java.sql, etc.).

## BytecodeAnalyzer.java Analysis
The `DataInputStream` reads the .class file byte by byte. The magic number (0xCAFEBABE) is validated. Version 65.0 = Java 21. The constant pool is scanned by reading each entry's tag byte and skipping the appropriate payload. This demonstrates that the constant pool is a variable-length structure — each entry type has a different size.

The constant pool parsing is simplified — in production, you'd need to read each entry fully and build a symbol table. But the structure is clear: after the constant pool come access flags, this/super class indices, interfaces, fields, and methods.

## AsmTransformer.java Analysis
The `addAnnotation()` method creates a ClassReader from the original bytes, a ClassWriter, and a ClassVisitor that overrides `visitEnd()`. At `visitEnd()`, the visitor calls `cv.visitAnnotation()` to add a new `@GeneratedAnnotation` to the class. The `visit("value", "transformed by ASM")` call sets the annotation's value element.

The transformed bytecode is the original plus the new annotation attribute. The annotation appears in the class file's RuntimeVisibleAnnotations attribute, which the JVM reads to provide `getAnnotation(GeneratedAnnotation.class)` at runtime.

## InvokeDynamicExample.java Analysis
The `LambdaMetafactory.metafactory()` call manually performs what the compiler does for lambda expressions. The arguments:
- `lookup`: MethodHandles.Lookup for the caller context
- `"apply"`: the functional interface method name
- `MethodType.methodType(Function.class)`: the expected factory type
- `MethodType.methodType(Object.class, Object.class)`: erased implementation type
- `toUpper`: the target MethodHandle (String::toUpperCase)
- `MethodType.methodType(String.class, String.class)`: specialized type

The returned CallSite's target is a MethodHandle that, when invoked, produces a Function instance that calls `toUpperCase`.
