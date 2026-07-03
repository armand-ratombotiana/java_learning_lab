# Module 15: JVM Internals - Mini Project

**Project Name**: JVM Memory Analyzer & Leaker  
**Difficulty Level**: Advanced  
**Estimated Time**: 2 hours

---

## 🎯 Objective
Understand how the JVM manages memory by intentionally triggering specific OutOfMemory errors and analyzing the behavior using JVM flags.

## 📝 Requirements

### Core Features
1. **Heap Space Exhaustion**:
   - Write a method `triggerHeapOOM()` that creates a `List<byte[]>` and continuously adds large byte arrays (e.g., 10MB each) in an infinite loop.
   - Run the application with JVM flag `-Xmx50m` (limiting heap to 50MB) to ensure it crashes quickly with `java.lang.OutOfMemoryError: Java heap space`.

2. **Stack Overflow Generation**:
   - Write a method `triggerStackOverflow()` that calls itself recursively without a base case.
   - Observe the `java.lang.StackOverflowError`. Run it with `-Xss256k` to see it crash faster.

3. **Heap Dump Analysis (Bonus)**:
   - Run the program with `-XX:+HeapDumpOnOutOfMemoryError`.
   - After the Heap OOM occurs, locate the generated `.hprof` file.
   - Open the file in a tool like VisualVM or Eclipse MAT to visually inspect the memory leak.

---

## 💡 Solution Blueprint

```java
public class MemoryLeaker {

    public static void main(String[] args) {
        // Uncomment one to test:
        // triggerHeapOOM();
        // triggerStackOverflow();
    }

    public static void triggerHeapOOM() {
        List<byte[]> memoryLeak = new ArrayList<>();
        while (true) {
            // Allocate 10MB blocks
            memoryLeak.add(new byte[10 * 1024 * 1024]); 
        }
    }

    public static void triggerStackOverflow() {
        triggerStackOverflow();
    }
}
```