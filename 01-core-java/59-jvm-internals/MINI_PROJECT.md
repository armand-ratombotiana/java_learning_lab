# Mini Project: ClassLoader & JIT Profiler

## Objective
Build a program that explores the JVM's internals. You will write a custom ClassLoader to see how classes are loaded into memory, and you will write a micro-benchmark to visually demonstrate the JIT compiler's "warm-up" phase.

## Prerequisites
*   Java 17+

## Step 1: The JIT Warm-up Demonstrator
We will write a tight loop that does some math. We will measure how long it takes to execute in batches. You will see the execution time drop dramatically as the JIT kicks in.

```java
public class JitProfiler {

    // A method that does some pointless math to keep the CPU busy
    public static double computeMath(int iterations) {
        double result = 0;
        for (int i = 0; i < iterations; i++) {
            result += Math.sin(i) * Math.cos(i);
        }
        return result;
    }

    public static void runProfiler() {
        System.out.println("--- JIT Compiler Warm-up Demo ---");
        System.out.println("Notice how the time drops as the JIT compiles the method to native code.");
        
        int batchSize = 10_000;
        
        // Run 20 batches
        for (int batch = 1; batch <= 20; batch++) {
            long start = System.nanoTime();
            
            computeMath(batchSize);
            
            long end = System.nanoTime();
            long durationMs = (end - start) / 1_000_000;
            
            System.out.printf("Batch %2d: %4d ms%n", batch, durationMs);
        }
    }
}
```

## Step 2: The Custom ClassLoader
We will write a ClassLoader that loads a class from a byte array (simulating loading a class over a network or generating one at runtime).

```java
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CustomNetworkClassLoader extends ClassLoader {

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        // 1. Convert the class name to a file path
        String fileName = name.replace('.', '/') + ".class";
        
        // 2. Read the raw bytes from the file system (or network)
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (is == null) {
                throw new ClassNotFoundException(name);
            }
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int b;
            while ((b = is.read()) != -1) {
                baos.write(b);
            }
            byte[] classBytes = baos.toByteArray();
            
            // 3. Define the class in the JVM (Loads it into Metaspace!)
            System.out.println("[CustomClassLoader] Defining class: " + name);
            return defineClass(name, classBytes, 0, classBytes.length);
            
        } catch (IOException e) {
            throw new ClassNotFoundException(name, e);
        }
    }
}
```

## Step 3: A Dummy Class to Load
Create a simple class that we will load dynamically.

```java
public class SecretLogic {
    public void execute() {
        System.out.println("SecretLogic executed successfully!");
    }
}
```

## Step 4: Execute the Demos
Run the JIT profiler, and then use the custom ClassLoader to load and instantiate the dummy class using reflection.

```java
import java.lang.reflect.Method;

public class Main {
    public static void main(String[] args) {
        // 1. Run JIT Demo
        JitProfiler.runProfiler();

        System.out.println("\n--- Custom ClassLoader Demo ---");
        try {
            CustomNetworkClassLoader loader = new CustomNetworkClassLoader();
            
            // 2. Ask our custom loader to load the class
            // It will delegate to the parent first, but if we structured this to load
            // a class not on the classpath, it would use our findClass method.
            // For this demo, we force it to load a class we know exists.
            Class<?> clazz = loader.findClass("SecretLogic");
            
            System.out.println("Class loaded by: " + clazz.getClassLoader().getClass().getSimpleName());
            
            // 3. Instantiate and execute via reflection
            Object instance = clazz.getDeclaredConstructor().newInstance();
            Method executeMethod = clazz.getMethod("execute");
            executeMethod.invoke(instance);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

## Expected Output
Notice the dramatic drop in execution time around Batch 3 or 4. This is the exact moment the C1/C2 JIT compilers kick in.
```text
--- JIT Compiler Warm-up Demo ---
Notice how the time drops as the JIT compiles the method to native code.
Batch  1:   15 ms
Batch  2:   12 ms
Batch  3:    8 ms
Batch  4:    1 ms   <-- JIT Compilation occurred!
Batch  5:    0 ms
Batch  6:    0 ms
...

--- Custom ClassLoader Demo ---
[CustomClassLoader] Defining class: SecretLogic
Class loaded by: CustomNetworkClassLoader
SecretLogic executed successfully!
```