# Module 47: Memory Profiling & Analysis - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What is a Memory Leak in Java, and how does it happen despite having a Garbage Collector?
**Answer**:
A memory leak in Java occurs when an application creates objects, uses them, but maintains **unintentional strong references** to them after they are no longer needed. 
Because the Garbage Collector only cleans up objects that are unreachable (have no references pointing to them from GC Roots), these unintentionally referenced objects remain in the Heap indefinitely. Over time, as more of these objects accumulate, the Heap fills up, leading to an `OutOfMemoryError`. Common causes include un-cleared static HashMaps, unclosed resources, and ThreadLocal variables.

### Q2: How do you read a Dominator Tree in a heap dump?
**Answer**:
A Dominator Tree is a view provided by tools like Eclipse MAT. It helps identify the "biggest" objects in the heap.
If Object A "dominates" Object B, it means that every path from the GC Roots to Object B must pass through Object A. Therefore, if Object A is garbage collected, Object B will also be garbage collected. The Dominator Tree sorts objects by their **Retained Size** (the total amount of memory that would be freed if the object was deleted), making it the fastest way to find the root cause of a memory leak.

### Q3: What is the difference between `jmap` and `jstack`?
**Answer**:
- **jmap (Memory Map)**: Used to inspect the JVM memory. It prints shared object memory maps or heap memory details of a given process. It is primarily used to generate a heap dump (`.hprof` file) to analyze memory leaks.
- **jstack (Stack Trace)**: Used to inspect JVM threads. It prints the Java stack traces of all threads currently running in the JVM. It is primarily used to troubleshoot unresponsive applications, deadlocks, or infinite loops by showing exactly what line of code each thread is blocked on.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: The Infinite String Leak
**Problem**: An interviewer shows you this code snippet running in a backend service. They tell you it crashes with an OOM after a few days in production. Identify the leak and fix it.

```java
public class TextProcessor {
    private List<String> history = new ArrayList<>();
    
    public void process(String massiveDocument) {
        String summary = massiveDocument.substring(0, 100);
        history.add(summary);
        System.out.println("Processed summary: " + summary);
    }
}
```

**Solution**:
Before Java 7 Update 6, `String.substring()` did not create a new character array; it simply created a new String object that shared the *original* massive character array of the parent string, adjusting the offset and length. Because `history` keeps a strong reference to the `summary` String, the entire `massiveDocument` character array (which could be megabytes) cannot be garbage collected, causing a massive memory leak.
*Fix*: Force the creation of a new, distinct character array using the `new String()` constructor.

```java
public void process(String massiveDocument) {
    // Forces a true copy of the characters, allowing the massive array to be GC'd
    String summary = new String(massiveDocument.substring(0, 100));
    history.add(summary);
}
```
*(Note: In modern Java, substring() creates a new array by default, but this remains a classic interview question to test deep memory understanding).*