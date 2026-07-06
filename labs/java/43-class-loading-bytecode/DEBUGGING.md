# Debugging Class Loading Issues

## ClassNotFoundException
This means the class is not visible to the requesting ClassLoader. Check:
- The full class name (including package)
- The ClassLoader's classpath
- Whether the class is in a JAR that's not on the classpath
- Module accessibility (module exports/opens)

Use `-XX:+TraceClassLoading` to see which classes are loaded and by which ClassLoader.

## NoClassDefFoundError
The class was found at compile time but not at runtime. Common causes:
- The class existed in one ClassLoader but not another
- A static initializer threw an exception (the class is unusable)
- The class file was deleted after compilation

Check `-XX:+TraceClassLoading` and `-XX:+TraceClassUnloading`.

## UnsupportedClassVersionError
The class was compiled with a newer JDK than the runtime. Check:
- `javap -v ClassName` (look for major_version)
- The JVM version (`java -version`)
- Maven's `<maven.compiler.target>` setting

## ClassCastException with the Same Class Name
The class is loaded by two different ClassLoaders. Check:
- Thread context ClassLoader
- Parent delegation chain
- OSGi/Tomcat/application server module boundaries

Use `object.getClass().getClassLoader()` and `object.getClass().getName()` in the debugger.

## VerifyError
The bytecode is malformed or violates type safety. Common causes:
- ASM transformation produced invalid bytecode
- Manual bytecode generation mistakes
- Corrupted class files

Use `javap -verbose` to inspect the bytecode. Look for type mismatches on the operand stack.

## ASM Debugging
- Use `ClassReader.accept()` with `ClassWriter.COMPUTE_MAXS` to auto-compute stack frames
- Use `java -Djdk.asm.useTooManyConstants=false` to catch constant pool overflow
- Check `ClassWriter.toByteArray()` output with `javap`
- Add logging to ClassVisitor callbacks
