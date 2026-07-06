# Class Loading Internals

## Class Object Representation
Internally, each loaded class has a `Klass` structure in the JVM (C++). The Java `Class` object is a handle to this Klass. The Klass contains:
- Constant pool cache (resolved references)
- Method table (vtable + itable for dispatch)
- Field layout information (offsets from object base)
- ClassLoader reference (who loaded this)
- Protection domain (security policy)

## Constant Pool Resolution
Symbolic references in the constant pool (e.g., "java/lang/System/out") are resolved lazily. When an instruction first references a constant pool entry, the JVM:
1. Parses the symbolic reference
2. Loads the referenced class if not already loaded
3. Resolves the field/method reference to an offset or index
4. Replaces the constant pool entry with a direct reference

## Method Area and Metaspace
Class metadata (Klass, constant pool, method bytecode) was stored in the Permanent Generation (PermGen) until Java 7. From Java 8, it's stored in Metaspace, which uses native memory (not heap). Metaspace grows dynamically and is garbage-collected when classes are unloaded.

## Class Unloading
A ClassLoader is eligible for GC when:
- No live references to any classes loaded by it
- No live references to the ClassLoader itself
- No live instances of any classes loaded by it

When a ClassLoader is collected, all its classes, metadata, and static fields are reclaimed. This is how application servers hot-deploy web applications — they create a new ClassLoader and discard the old one.

## Constant Pool Entry Types
The constant pool supports 17 entry types (Java 21):
- CONSTANT_Utf8: UTF-8 encoded string
- CONSTANT_Integer, Float, Long, Double: numeric constants
- CONSTANT_Class, String: symbolic references
- CONSTANT_Fieldref, Methodref, InterfaceMethodref: field/method references
- CONSTANT_NameAndType: name and descriptor pair
- CONSTANT_MethodHandle, MethodType, InvokeDynamic, Dynamic, Module, Package

## Bootstrap ClassLoader Implementation
The Bootstrap ClassLoader is not a Java class — it's implemented natively in the JVM. It loads classes from:
- `jrt:/java.base` (JDK 9+ modules)
- `jre/lib/rt.jar` (pre-JDK 9)
- `-Xbootclasspath/a:` entries
It has no parent and returns null for `getParent()`.
