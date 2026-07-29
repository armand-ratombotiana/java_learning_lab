# Design a Custom ClassLoader

> **Category**: JVM Deep (Class Loading)

## Problem

Design a custom `ClassLoader` that loads Java classes from a custom source (e.g., encrypted bytecode, network, database). The solution must respect the Java class-loading delegation model.

## Solution

A custom `ClassLoader` that loads `.class` files from a specified directory, overriding `findClass()` to implement custom bytecode loading.

```java
import java.io.*;
import java.nio.file.*;

/**
 * Custom ClassLoader — loads classes from a specified directory.
 *
 * Demonstrates: findClass(), defineClass(), class-loading delegation.
 */
public class CustomClassLoader extends ClassLoader {

    private final Path classDir;

    public CustomClassLoader(Path classDir, ClassLoader parent) {
        super(parent);
        this.classDir = classDir;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String path = name.replace('.', File.separatorChar) + ".class";
        Path classFile = classDir.resolve(path);

        if (!Files.exists(classFile)) {
            throw new ClassNotFoundException(name);
        }

        try {
            byte[] bytecode = Files.readAllBytes(classFile);
            return defineClass(name, bytecode, 0, bytecode.length);
        } catch (IOException e) {
            throw new ClassNotFoundException(name, e);
        }
    }

    /**
     * Override loadClass to demonstrate breaking delegation (optional).
     * Normal behavior: delegate to parent first, then findClass.
     * Here we keep standard delegation (calling super.loadClass).
     */
    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // Standard delegation: parent first
        return super.loadClass(name, resolve);
    }

    // ─────────────────────
    // Verification
    // ─────────────────────
    public static void main(String[] args) throws Exception {
        // Create a temporary directory with a compiled class
        Path tmpDir = Files.createTempDirectory("classloader-test");
        tmpDir.toFile().deleteOnExit();

        // Write a simple "HelloWorld" class file manually (compile a tiny .java file)
        Path srcFile = tmpDir.resolve("Hello.java");
        Files.writeString(srcFile, """
            public class Hello {
                public static String greet() { return "Hello from custom ClassLoader!"; }
            }
            """);

        // Compile it using the system Java compiler
        Process compile = new ProcessBuilder("javac", srcFile.toString())
            .directory(tmpDir.toFile())
            .inheritIO()
            .start();
        int exitCode = compile.waitFor();
        if (exitCode != 0) {
            System.out.println("Compilation failed (expected if javac not on PATH) — skipping verification.");
            return;
        }

        // Load the compiled class using our custom ClassLoader
        CustomClassLoader loader = new CustomClassLoader(tmpDir, ClassLoader.getSystemClassLoader());
        Class<?> helloClass = loader.loadClass("Hello");
        String greeting = (String) helloClass.getMethod("greet").invoke(null);
        System.out.println(greeting);
        assert "Hello from custom ClassLoader!".equals(greeting) : "Unexpected greeting";

        // Verify it was loaded by our ClassLoader
        assert helloClass.getClassLoader() == loader : "Wrong ClassLoader";
        System.out.println("All tests passed.");
    }
}
```

## Complexity

| Operation        | Time      |
|------------------|-----------|
| findClass        | O(S) — reads S bytes from disk |
| defineClass      | O(S) — verifies + links bytecode |

## Key Insights

1. **Delegation model**: `loadClass()` delegates to parent first; `findClass()` is called only if parent fails. This prevents re-loading system classes.
2. **`defineClass()`**: Converts byte array into a `Class<?>` object. The JVM verifies bytecode format, performs linking, and initializes static fields.
3. **Custom source**: The loader reads from a directory, but the pattern extends to encrypted streams, network sockets, or database BLOBs.
4. **Class identity**: Two class loaders can define different versions of the same class name — they are distinct types at runtime.
