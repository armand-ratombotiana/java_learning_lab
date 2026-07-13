# Classloading Code Deep Dive

This lab provides a pure Java implementation of a Custom Classloader. It demonstrates how to bypass the standard classpath and load a class directly from an arbitrary byte array (e.g., loaded over a network or from an encrypted file).

## 💻 Pure Java Implementation

```java file="labs/java/jvm-internals/classloading/SOLUTION/CustomClassLoaderDemo.java"
package java.jvminternals.classloading;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * A demonstration of writing a Custom ClassLoader in Java.
 */
public class CustomClassLoaderDemo extends ClassLoader {

    private final String classPathDir;

    /**
     * @param classPathDir The custom directory to search for .class files, 
     *                     outside the normal JVM classpath.
     */
    public CustomClassLoaderDemo(String classPathDir) {
        // Pass the System ClassLoader as the parent to maintain the delegation model
        super(CustomClassLoaderDemo.class.getClassLoader());
        this.classPathDir = classPathDir;
    }

    /**
     * This method is called by the JVM when the parent classloaders fail to find the class.
     * We must override it to provide our custom loading logic.
     */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        System.out.println("[CustomClassLoader] Attempting to load class: " + name);
        
        byte[] classBytes = loadClassBytesFromFile(name);
        if (classBytes == null) {
            throw new ClassNotFoundException(name);
        }

        // The magic method: Converts an array of bytes into an actual java.lang.Class object in the Metaspace
        System.out.println("[CustomClassLoader] Defining class in JVM memory...");
        return defineClass(name, classBytes, 0, classBytes.length);
    }

    /**
     * Reads the raw binary data of the .class file from the custom directory.
     */
    private byte[] loadClassBytesFromFile(String className) {
        String fileName = classPathDir + File.separator + className.replace('.', File.separatorChar) + ".class";
        
        try (FileInputStream fis = new FileInputStream(fileName);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            
            int b;
            while ((b = fis.read()) != -1) {
                baos.write(b);
            }
            return baos.toByteArray();
            
        } catch (IOException e) {
            System.err.println("Could not read file: " + fileName);
            return null;
        }
    }

    public static void main(String[] args) {
        try {
            // Assume we have a compiled SecretAlgorithm.class sitting in the /tmp/plugins directory
            String customDirectory = "/tmp/plugins";
            String targetClassName = "com.example.plugins.SecretAlgorithm";

            System.out.println("Creating Custom ClassLoader...");
            CustomClassLoaderDemo loader = new CustomClassLoaderDemo(customDirectory);

            // 1. Load the class dynamically at runtime
            Class<?> loadedClass = loader.loadClass(targetClassName);
            System.out.println("Class loaded successfully: " + loadedClass.getName());
            
            // Prove that it was loaded by OUR classloader, not the Application classloader
            System.out.println("Loaded by: " + loadedClass.getClassLoader().getClass().getName());

            // 2. Instantiate and execute the code via Reflection
            // We cannot cast it to 'SecretAlgorithm' directly because that class isn't on our compile-time classpath!
            Object instance = loadedClass.getDeclaredConstructor().newInstance();
            Method executeMethod = loadedClass.getMethod("execute");
            
            System.out.println("\nExecuting dynamically loaded method:");
            executeMethod.invoke(instance);

        } catch (Exception e) {
            System.err.println("Demonstration failed (expected if /tmp/plugins/SecretAlgorithm.class doesn't exist).");
            e.printStackTrace();
        }
    }
}
```

## 🔍 Key Takeaways
1. **The Delegation Model Preserved**: Notice the constructor calls `super(...)`. When we call `loader.loadClass("java.lang.String")`, our custom classloader will *not* intercept it. The `loadClass` method (implemented in the base `ClassLoader` class) will delegate to the parent first. The parent will find `String` and return it. Our custom `findClass` method is only triggered as a fallback if the parent fails.
2. **`defineClass`**: This is the most powerful method in the classloader API. It takes a raw array of bytes and asks the JVM to parse it, verify the bytecode, and inject it into the Metaspace as a living `Class` object. 
3. **Plugin Architectures**: This is exactly how tools like Jenkins, Eclipse, and Minecraft servers load plugins at runtime. They download a JAR file, extract the bytes, and use a custom classloader to inject the plugin into the running application without needing to restart the JVM.