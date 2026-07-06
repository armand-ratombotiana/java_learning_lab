# How Class Loading Works

## The `loadClass` Method
When `ClassLoader.loadClass(String name)` is called:
1. Check if the class is already loaded (ClassLoader cache)
2. If not loaded, delegate to parent ClassLoader
3. If parent returns null (or throws ClassNotFoundException), call `findClass()`
4. `findClass()` locates the .class bytes and calls `defineClass()`
5. `defineClass()` converts bytes to a Class object (linking may happen later)

## `defineClass` Internals
`defineClass(String name, byte[] b, int off, int len)` is a native method that:
1. Creates a `Class` object in the JVM's internal class metadata
2. Links the class (verify, prepare, resolve) or defers linking
3. Adds the class to the ClassLoader's namespace (dictionary)
4. Returns the Class reference

## Bytecode Verification
The verifier (part of the linking phase) checks:
- Final classes not subclassed, final methods not overridden
- Every instruction has valid operands (types match)
- The operand stack doesn't overflow/underflow
- Local variables are initialized before use
- Access control rules are followed (private methods only called from same class)

## ASM Transformation Flow
1. `ClassReader` reads the original bytecode and fires events: visitClass, visitField, visitMethod, visitAnnotation
2. `ClassVisitor` (which may wrap another ClassVisitor) receives events and can modify/add/remove elements
3. `ClassWriter` (the last visitor in the chain) receives events and builds new bytecode
4. The new byte array replaces the original in the ClassLoader or file

## invokedynamic Execution
1. The first time an invokedynamic instruction executes, the JVM calls the bootstrap method
2. The bootstrap method receives: MethodHandles.Lookup, method name, MethodType, and any static arguments
3. The bootstrap method returns a `CallSite` object containing a `MethodHandle` target
4. The JVM links the call site — subsequent executions bypass the bootstrap and call the target directly
5. If the CallSite is mutable (MutableCallSite or VolatileCallSite), the target can be changed at runtime
